package com.nivalsoul.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.nivalsoul.service.DocumentService;
import com.nivalsoul.service.FileService;

@Controller  
@RequestMapping("/v1/docdive") 
public class DocumentController {
	
	@Autowired
	private DocumentService documentService;
	
	@Autowired
	private FileService fileService;

	/**
	 * 上传文件，已实现多个文件上传功能
	 * @param request
	 * @param files
	 * @return
	 */
	@RequestMapping(value ="/documents/file", method = RequestMethod.POST)
	@ResponseBody
	public Object uploadFile(HttpServletRequest request,
			@RequestParam(value ="file") MultipartFile[] files){
		String userId = (String) request.getAttribute("userId");
		return documentService.uploadFile(userId, files);
	}
	
	@RequestMapping(value ="/documents/file/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object deleteFile(HttpServletRequest request,
			@PathVariable("id") String documentId){
		String userId = (String) request.getAttribute("userId");
		return documentService.deleteFile(userId, documentId);
	}
	

	/**
	 * 将文档转为pdf并解析
	 * @param documentId
	 * @return
	 */
	@RequestMapping(value ="/documents/file/format/{id}", method = RequestMethod.POST)
	@ResponseBody
	public Object convertFile(@PathVariable("id") String documentId){
		 return documentService.convertFile(documentId);
	}

	/**
	 * 保存文档信息
	 * @param request
	 * @param files 多个文档信息
	 * @return
	 */
	@RequestMapping(value ="/documents", method = RequestMethod.POST)
    @ResponseBody
    public Object addDocuments(HttpServletRequest request,
    		@RequestBody List<Map<String, String>> files){
		String userId = (String) request.getAttribute("userId");
		String userName = (String) request.getAttribute("userName");
        return documentService.addDocuments(userId, userName, files);
    }
	
	/**
	 * 查看文档详情
	 * @param request
	 * @param documentId
	 * @return
	 */
	@RequestMapping(value ="/documents/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Object getDocument(HttpServletRequest request,
    		@PathVariable("id") String documentId){
		String userId = (String) request.getAttribute("userId");
        return documentService.getDocument(userId, documentId);
    }
	
	
	/**
	 * 修改文档信息
	 * @param request
	 * @param documentId
	 * @param fileInfo
	 * @return
	 */
	@RequestMapping(value ="/documents/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateDocument(HttpServletRequest request,
    		@PathVariable("id") String documentId,
    		@RequestBody Map<String, String> fileInfo){
		String userId = (String) request.getAttribute("userId");
        return documentService.updateDocument(userId, documentId, fileInfo);
    }
	
	/**
	 * 删除文档
	 * @param request
	 * @param documentId
	 * @return
	 */
	@RequestMapping(value ="/documents/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteDocument(HttpServletRequest request,
    		@PathVariable("id") String documentId){
		String userId = (String) request.getAttribute("userId");
        return documentService.deleteDocument(userId, documentId);
    }
	
	/**
	 * 文档数按部门(租户)分组统计
	 * @param request
	 * @return
	 */
	@RequestMapping(value ="/documents/stats", method = RequestMethod.GET)
    @ResponseBody
    public Object stats(HttpServletRequest request){
		String userId = (String) request.getAttribute("userId");
        return documentService.stats(userId);
    }
	
	/**
	 * 文档搜索
	 * @param request
	 * @param query
	 * @param page_num
	 * @param page_size
	 * @param order
	 * @return
	 */
	@RequestMapping(value ="/documents/search", method = RequestMethod.GET)
    @ResponseBody
    public Object search(HttpServletRequest request,
    		@RequestParam(value ="q") String query, 
    		@RequestParam(value ="page_num", defaultValue="1") int page_num, 
    		@RequestParam(value ="page_size", defaultValue="10") int page_size, 
    		@RequestParam(value ="order", defaultValue="title") String order){
		
		String userId = (String) request.getAttribute("userId");
        return documentService.search(userId, query,page_num,page_size,order);
    }
	
	/**
	 * 获取文档的原文件或者pdf以及相关的图片、文本
	 * @param request
	 * @param documentId
	 * @param filename
	 * @return
	 */
	@RequestMapping(value ="/documents/{id}/{filename:.+}", method = RequestMethod.GET)
    public void getDocumentFile(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable("id") String documentId, @PathVariable("filename") String filename,
    		@RequestParam(value="isdownload", defaultValue="no") String isdownload){
		String userId = (String) request.getAttribute("userId");
        try {  
        	response.setDateHeader("Expires", System.currentTimeMillis()+3600000);
        	response.addHeader("Cache-Control", "max-age=3600");
        	if(isdownload.equals("yes")){
        		//设置文件ContentType类型，这样设置，会自动判断下载文件类型  
        		response.setContentType("multipart/form-data");  
        		//设置文件头会让浏览器下载文件
        		response.setHeader("Content-Disposition", "attachment;fileName="
        				+java.net.URLEncoder.encode(filename, "UTF-8"));  
        	}else{
        		if(filename.toLowerCase().endsWith(".png")){
            		response.setContentType("image/png");
            		filename = "images/"+filename;
            	}else if (filename.toLowerCase().endsWith(".txt")) {
            		response.setContentType("text/plain");
            		filename = "text/"+filename;
            	}else if (filename.toLowerCase().endsWith(".pdf")) {
            		response.setContentType("application/pdf");
    			}else{
            		response.setContentType("multipart/form-data");  
            		response.setHeader("Content-Disposition", "attachment;fileName="
            				+java.net.URLEncoder.encode(filename, "UTF-8"));  
            	}
        	}
        	
        	File file = documentService.getDocumentFile(userId, documentId, filename);
        	if(file == null){
        		String fileType = FilenameUtils.getExtension(filename).toLowerCase();
        		file = new File(this.getClass().getResource("/404."+fileType).getPath());
        	}
        	
            FileInputStream fis = new FileInputStream(file);  
            ServletOutputStream out = response.getOutputStream();  
            //必须一次性write,如果在while中多次调用out.write则会报getOutputStream() has already been called异常
            byte[] b = new byte[fis.available()];
	        fis.read(b);
	        out.write(b);
	        out.flush();
	        fis.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        
    }
	
}
