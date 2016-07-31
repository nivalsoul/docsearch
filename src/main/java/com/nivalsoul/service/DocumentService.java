package com.nivalsoul.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.nivalsoul.dao.DocumentDao;
import com.nivalsoul.model.Document;
import com.nivalsoul.model.Page;
import com.nivalsoul.model.ResultInfo;

@Service
public class DocumentService {
	
	@Autowired
	private DocumentDao documentDao;
	
	@Autowired
	private FileService fileService;
	
	public Object getDocument(String userId, String documentId) {
		Document document = documentDao.getDocumentById(documentId);
		ResultInfo info = new ResultInfo();
		if(document == null){
			info.setCode(404);
			info.setMessage("document is not exist");
			return info;
		}
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("code", 200);
		res.put("data", document);
		
		return res;
	}

	public Object addDocuments(String userId, String userName, List<Map<String, String>> files) {
		List<Map<String, Object>> resultInfos = new ArrayList<Map<String, Object>>();
		boolean hasErrors = false;
		String dt = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
		for (Map<String, String> fileInfo : files) {
			Document doc = new Document();
			doc.set_id(fileInfo.get("document_id"));
			doc.setCategory_id(fileInfo.get("category_id"));
			doc.setTenant_id(fileInfo.get("tenant_id"));
			doc.setUser_id(userId);
			doc.setUser_name(userName);
			doc.setFile_name(fileInfo.get("title"));
			doc.setTitle(fileInfo.get("title"));
			doc.setDescription(fileInfo.get("description"));
			doc.setStatus("uploaded");
			doc.setFormat("");
			doc.setCreated_at(dt);
			doc.setUpdated_at(dt);
			doc.setPage_count(0);
			doc.setPages(new ArrayList<Page>());
			doc.setFile_hash("");
			int count = documentDao.add(doc);
			Map<String, Object> info = new HashMap<String, Object>();
			if (count == -1) {
				info.put("code", 500);
				info.put("document_id", doc.get_id());
				info.put("message", "save document["+doc.getTitle()+"] failed, please try again");
				hasErrors = true;
			}else{
				info.put("code", 200);
				info.put("message", "save document["+doc.getTitle()+"] successful");
				//保存成功后给消息系统发送消息，进行文档转解析操作
				fileService.sendMessage(doc.get_id());
			}
			resultInfos.add(info);
		}
		
		//return resultInfos;
		ResultInfo info = new ResultInfo();
		if(hasErrors){
			info.setCode(500);
			info.setMessage("server error");
		}else{
			info.setCode(200);
			info.setMessage("save documents successful");
		}
		return info;
	}

	public Object updateDocument(String userId, String documentId, Map<String, String> fileInfo) {
		ResultInfo info = new ResultInfo();
		if (Strings.isNullOrEmpty(fileInfo.get("title")) 
				|| Strings.isNullOrEmpty(fileInfo.get("tenant_id"))
				|| Strings.isNullOrEmpty(fileInfo.get("category_id"))) {
			info.setCode(415); // 请求格式不匹配
			info.setMessage("cannot set the empty value of [title/tenant_id/category_id]");
			return info;
		}
		Document document = documentDao.getDocumentById(documentId);
		if (document == null) {
			info.setCode(404); // 请求资源不存在
			info.setMessage("cannot find the request resource");
			return info;
		}
		if (!document.getUser_id().equals(userId)) {
			info.setCode(403); // 没有权限
			info.setMessage("cannot modify document [" + fileInfo.get("title") + "]: Permission denied");
			return info;
		}
		// use insert sql to update es
		document.setTitle(fileInfo.get("title"));
		document.setCategory_id(fileInfo.get("category_id"));
		document.setTenant_id(fileInfo.get("tenant_id"));
		document.setDescription(fileInfo.get("description"));
		String dt = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
		document.setUpdated_at(dt);
		int count = documentDao.update(document);
		if (count > -1) {
			info.setCode(200);
			info.setMessage("ok");
		}else{
			info.setCode(500);
			info.setMessage("update document failed, please try again");
		}
		
		return info;
	}

	public Object deleteDocument(String userId, String documentId) {
		ResultInfo info = new ResultInfo();
		int count = documentDao.delete(userId, documentId);
		if (count == -1) {
			info.setCode(500);
			info.setMessage("delete document failed, please try again");
		}else if (count == 0) {
			info.setCode(400);
			info.setMessage("permisson denied or document is not exist");
		}else {
			info.setCode(200);
			info.setMessage("ok");
			//同时删除文件？
			fileService.deleteFile(documentId);
		}
		
		return info;
	}
	
	public Object deleteFile(String userId, String documentId) {
		boolean result = fileService.deleteFile(documentId);
		//不管是否成功删除文件都返回ok！
		ResultInfo info = new ResultInfo();
		info.setCode(200);
		info.setMessage("ok");
		return info;
	}


	public Object uploadFile(String userId, MultipartFile[] files) {
		List<Map<String, Object>> resultInfos = new ArrayList<Map<String, Object>>();
        try {  
            for (MultipartFile mf : files) {  
                if(!mf.isEmpty()){  
                	//保存上传的文件，可根据需求在该方法中保存到不同地方
                	Map<String, Object> info = fileService.saveFile(mf);
                	resultInfos.add(info);
                }else {
                	Map<String, Object> info = new HashMap<String, Object>();
                	info.put("code", 415);
    				info.put("message", "file ["+mf.getOriginalFilename()+"] is empty");
            		resultInfos.add(info);
				}
                //暂时上传一个文件
                break;
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  

        //上传一个文件
		return resultInfos.get(0);
		//批量上传
		//return resultInfos;
	}

	/**
	 * 统计当前用户所属租户的文档总数，如果是管理员则可以查看当前子系统所有租户的文档总数
	 * @param userId
	 * @return
	 */
	public Object stats(String userId) {
		Map<String, String> tenantNames = new HashMap<String, String>();
		String condition = getTenantInfoByUserId(userId, tenantNames);
		if(condition == null){
			ResultInfo info = new ResultInfo();
			info.setCode(403);
			info.setMessage("stats error: you do not belong to any tenant");
			return info;
		}
		
		return documentDao.stats(condition, tenantNames);
	}

	private String getTenantInfoByUserId(String userId, Map<String, String> tenantNames) {
		List<String> tenants = Lists.newArrayList();
		String condition = "('";
		for (String tenant : tenants) {
			condition += tenant + "','";
		}
		condition = condition.substring(0, condition.length() - 2);
		condition += ")";
		return condition;
	}
	
	public Object search(String userId, String query, int page_num, int page_size, String order) {
		//权限判断
		Map<String, String> tenantNames = new HashMap<String, String>();
		String tenantCnd = getTenantInfoByUserId(userId, tenantNames);
		if(tenantCnd == null){
			ResultInfo info = new ResultInfo();
			info.setCode(403);
			info.setMessage("search error: you do not belong to any tenant");
			return info;
		}
		//参数处理
		if(page_num < 1) page_num = 1;
		if(page_size < 0) page_size = 10;
		String cnd = "";
		String pageCnd = "text:(";
		String[] terms = query.split("@");
		for (String item : terms) {
			String[] arr = item.split(":");
			if(arr.length > 1){
				cnd += arr[0] + ":(\"" + arr[1] + "\") AND ";
			}else{
				//all += "" + arr[0] + " AND "; //不加引号则是分词后匹配，加了则是整词匹配
				pageCnd += "\"" + arr[0] + "\" AND ";
			}
		}
		if(!"".equals(cnd)){
			cnd = cnd.substring(0, cnd.length()-5);
		}
		if(pageCnd.length() > 6){
			pageCnd = pageCnd.substring(0, pageCnd.length()-5) + ")";
			//cnd += " OR " + pageCnd.replaceAll("text", "_all").replaceAll("\"", "");
		}else{
			pageCnd = null;
		}
		String userCnd="";
		/*if(!permissionService.isDataAdmin(userId)){
			userCnd += " and user_id='"+userId+"'";
		}*/
		System.out.println("cnd=="+cnd);
		System.out.println("pageCnd=="+pageCnd);
		return documentDao.search(userCnd, cnd, pageCnd, page_num, page_size, order);
	}

	public File getDocumentFile(String userId, String documentId, String filename) {
		return fileService.getFile(documentId, filename);
	}

	/**
	 * 重新转换格式
	 * @param documentId
	 * @return
	 */
	public Object convertFile(String documentId) {
		Document document = documentDao.getDocumentById(documentId);
		document.setStatus("uploaded");
		documentDao.update(document);
		//send message to covert file to pdf
		fileService.sendMessage(documentId);
		
		ResultInfo info = new ResultInfo();
		info.setCode(200);
		info.setMessage("convert job started...");
		return info;
	}
	
}
