package com.nivalsoul.dao;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.nivalsoul.config.ESConfig;
import com.nivalsoul.model.Document;
import com.nivalsoul.model.Page;
import com.nivalsoul.model.ResultInfo;
import com.nivalsoul.utils.ESUtil;

@Component
public class DocumentDao {
	
	@Autowired
	private ESConfig esConfig;

	public int add(Document doc) {
		String sql = "insert into document (_id,tenant_id,category_id,user_id,user_name"
			+ ",updated_at,created_at,title,description,format,page_count,file_hash,file_name,status)" 
			+ " values('" + doc.get_id() + "','" + doc.getTenant_id() + "','" 
			+ doc.getCategory_id() + "','" + doc.getUser_id() + "','" + doc.getUser_name() + "','" 
			+ doc.getUpdated_at() + "','" + doc.getCreated_at() + "','" + doc.getTitle() + "','" 
			+ doc.getDescription() + "','" + doc.getFormat() + "'," + doc.getPage_count() + ",'" 
			+ doc.getFile_hash()+ "','" + doc.getFile_name()+ "','" + doc.getStatus()+ "')";
		int count = ESUtil.exec(sql);
		if(count == 1){
			return addPages(doc.get_id(), doc.getPages());
		}
		
		/*//由于sql4es目前无法实现内前对象的添加，故采用es的api来实现
		String jsonStr = Json.toJson(doc);//由于fastjson转成字符串时_id没有了下划线，故使用nutz的库来转换
		Map data = Json.fromJsonAsMap(Object.class, jsonStr);
		data.remove("tenant_name");
		String[] ip_port = esConfig.getUrl().split("//")[1].split("/")[0].split(":");
		int count = new ESUtil(esConfig.getClusterName(), ip_port[0], Integer.parseInt(ip_port[1]))
		    .singleRequest("docdive", "document", data);*/
		return count;
	}
	
	public int addPages(String documentId, List<Page> pages) {
		if(pages == null || pages.size()==0)
			return 1;
		for (Page page : pages) {
			if(page.get_id() == null){
				page.set_id(UUID.randomUUID().toString());
			}
		}
		
		//由于sql4es目前无法实现内前对象的添加，故采用es的api来实现
		String jsonStr = Json.toJson(pages);//由于fastjson转成字符串时_id没有了下划线，故使用nutz的库来转换
		//Map data = Json.fromJsonAsMap(Object.class, jsonStr);
		List<Map<String, Object>> rows = new ArrayList<>();
		for (Map<String, Object> map : JSON.parseArray(jsonStr, Map.class)) {
			rows.add(map);
		}
		String[] ip_port = esConfig.getUrl().split("//")[1].split("/")[0].split(":");
		int count = new ESUtil(esConfig.getClusterName(), ip_port[0], Integer.parseInt(ip_port[1]))
		    .bulkRequest("docdive", "pages", rows);
		
		return count;
	}

	public int update(Document document) {
		// use insert sql to update es
		return add(document);
	}

	public int delete(String userId, String documentId) {
		String sql = "delete from document where user_id='" + userId 
				+ "' and _id = '" + documentId + "'";
		int count = ESUtil.exec(sql);
		if(count==1){
			ESUtil.exec("delete from pages where document_id = '" + documentId + "'");
		}
		return count;
	}

	/**
	 * 我的文档里面的文档概要信息，不需要全部内容
	 * @param userId
	 * @param categoryId
	 * @return
	 */
	public Object getDocumentsByCategoryId(String userId, String categoryId) {
		Connection con = Dao.getConnection();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Document> list = new ArrayList<Document>();
		try {
			String sql = "select * from document "
				+ "where category_id='"+categoryId+"' and user_id='" + userId + "'";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			Map<String, String> tenats = new HashMap<String, String>();
			if (rs != null) {
				while(rs.next()){
					//填充数据
					Document document = fillData(rs);
					//页面内容转为List对象
					List<Page> pageList = getPages(document.get_id(), null);
					document.setPages(pageList);
					if(!tenats.containsKey(document.getTenant_id())){
						String tenant_name = "";
						document.setTenant_name(tenant_name);
						tenats.put(document.getTenant_id(), tenant_name);
					}else{
						document.setTenant_name(tenats.get(document.getTenant_id()));
					}
					list.add(document);
				}
			}
			result.put("code", 200);
			result.put("documents", list);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("code", 500);
			result.put("message", "server error");
		}finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * 文档详情
	 * @param documentId
	 * @return
	 */
	public Document getDocumentById(String documentId) {
		Connection con = Dao.getConnection();
		//Connection con = Dao.getConNotPool();
		Document document = null;
		try {
			String sql = "select * from document "+ "where _id='"+documentId+"'";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs != null && rs.next()) {
				//填充数据
				document = fillData(rs);
				//页面内容转为List对象
				List<Page> pageList = getPages(document.get_id(), null);
				document.setPages(pageList);
				String tenant_name = "";
				document.setTenant_name(tenant_name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return document;
	}

	private Document fillData(ResultSet rs) throws SQLException {
		Document document = new Document();
		document.set_id(rs.getString("_id"));
		String tenant_id = rs.getString("tenant_id");
		document.setTenant_id(tenant_id);
		document.setCategory_id(rs.getString("category_id"));
		document.setUser_id(rs.getString("user_id"));
		document.setUser_name(rs.getString("user_name"));
		document.setCreated_at(rs.getString("created_at"));
		document.setUpdated_at(rs.getString("updated_at"));
		document.setFile_name(rs.getString("file_name"));
		document.setTitle(rs.getString("title"));
		document.setDescription(rs.getString("description"));
		document.setStatus(rs.getString("status"));
		document.setFormat(rs.getString("format"));
		document.setFile_hash(rs.getString("file_hash"));
		document.setPage_count(rs.getInt("page_count"));
		
		return document;
	}
	
	/**
	 * 查询文档的页面信息
	 * @param documentId
	 * @param cnd 查询条件
	 * @return
	 */
	public List<Page> getPages(String documentId, String cnd){
		List<Page> pageList = new ArrayList<Page>();
		Connection con = Dao.getConnection();
		try {
			String sql = "select  _score,highlight(text) as hltext,text,page,_id"
					+ " from pages where document_id='"+documentId+"'";
			if(cnd!=null && !cnd.equals(""))
				sql += " and _search='" + cnd + "'";
			sql += " order by page limit 1000";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs != null) {
				while(rs.next()){
					Page page = new Page();
					page.set_id(rs.getString("_id"));
					page.setDocument_id(documentId);
					page.setPage(rs.getInt("page"));
					Array arr = rs.getArray("hltext");
					if(arr != null){
						String hltext = arr.toString().substring(1);
						hltext = hltext.substring(0, hltext.length()-1);
						hltext = HtmlUtils.htmlEscape(hltext);//先转义html
						//还原高亮部分
						hltext = hltext.replaceAll("&lt;em&gt;", "<em>").replaceAll("&lt;/em&gt;", "</em>");
						page.setText("..."+hltext+"...");
					}else{
						//page.setText(rs.getString("text"));//暂不返回
					}
					pageList.add(page);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return pageList;
	}

	/**
	 * 文档数按部门（租户）统计
	 * @param condition
	 * @param tenantNames
	 * @return
	 */
	public Object stats(String condition, Map<String, String> tenantNames) {
		Connection con = Dao.getConnection();
		Map<String, Object> result = Maps.newHashMap();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try {
			String sql = "select tenant_id,count(*) as document_count from document "
				+ "where status='success' and tenant_id in "+ condition +" group by tenant_id";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs != null) {
				while(rs.next()){
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("tenant_id", rs.getString("tenant_id"));
					map.put("document_count", (int)rs.getDouble("document_count"));
					map.put("tenant_name", tenantNames.get(rs.getString("tenant_id")));
					list.add(map);
				}
			}
			
			result.put("code", 200);
			result.put("data", list);
		} catch (Exception e) {
			if (!"No result found for this query".equals(e.getMessage())){
				e.printStackTrace();
				result.put("code", 500);
				result.put("message", "server error");
			}else{
				//没有文档时，返回0
				Iterator<Entry<String, String>> it = tenantNames.entrySet().iterator();
				while(it.hasNext()){
					Entry<String, String> entry = it.next();
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("tenant_id", entry.getKey());
					map.put("document_count", 0);
					map.put("tenant_name", entry.getValue());
					list.add(map);
				}
				result.put("code", 200);
				result.put("data", list);
			}
		}finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	/**
	 * 文档搜索<br>
	 * 由于文档和页面分开存放到两个表中，对全文检索的处理再次查询了页面表和文档表<br>
	 * 由于sql4es不能实现分页，暂时采取了多查询数据然后丢弃的方式来实现分页
	 * @param userCnd
	 * @param cnd
	 * @param pageCnd
	 * @param page_num
	 * @param page_size
	 * @param order
	 * @return
	 */
	public Object search(String userCnd, String cnd, String pageCnd, 
			int page_num, int page_size, String order) {
		Connection con = Dao.getConnection();
		List<Document> list = new ArrayList<Document>();
		try {
			int limitNum = page_num*page_size;
			if(pageCnd != null){ //如果全文检索关键词条件非空，则查询9999条后过滤
				limitNum = 9999;
			}
			String sql = "select *,title.raw from document where status='success'" + userCnd + " and _search='" + cnd
					+ "' order by " + order.replaceAll("title", "title.raw") + " limit " + limitNum;
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			Map<String, String> tenats = new HashMap<String, String>();
			//分页处理的临时方案
			int n = (page_num - 1)*page_size;
			int i = 1;
			if (rs != null) {
				while(rs.next()){
					if(pageCnd == null && i <= n){ //跳过前(page_num-1)*page_size条记录
						i++;
						continue;
					}
					//填充数据
					Document document = fillData(rs);
					//页面内容转为List对象
					List<Page> pageList = getPages(document.get_id(), pageCnd);
					//查看document的title,description和user_name是否包含查询关键词
					Document doc = queryForFilter(document.get_id(), pageCnd);
					if(pageList.isEmpty() && doc == null){
						continue;
					}
					document.setPages(pageList);
					if(doc != null){
						document.setTitle(doc.getTitle());
						document.setDescription(doc.getDescription());
						document.setUser_name(doc.getUser_name());
					}
					if(!tenats.containsKey(document.getTenant_id())){
						String tenant_name = "";
						document.setTenant_name(tenant_name);
						tenats.put(document.getTenant_id(), tenant_name);
					}else{
						document.setTenant_name(tenats.get(document.getTenant_id()));
					}
					if(pageCnd != null && i <= n){//对于满足全文检索条件的记录，跳过前面的(page_num-1)*page_size条记录
						i++;
						continue;
					}
					list.add(document);
					if(list.size() == page_size){ //够page_size条记录了则返回
						break;
					}
				}
			}
			
			Map<String, Object> result = Maps.newHashMap();
			result.put("code", 200);
			result.put("documents", list);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			ResultInfo info = new ResultInfo();
			info.setCode(500);
			info.setMessage("server error");
			return info;
		}finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Document queryForFilter(String documentId, String pageCnd) {
		if(pageCnd == null)
			return null;
		Connection con = Dao.getConnection();
		Document document = null;
		try {
			String sql = "select _score,highlight(title) as hltitle, highlight(description) as hldescription,"
					+ " highlight(user_name) as hluser_name,title,description,user_name"
					+ " from document where _id='" + documentId + "'" 
					+ " and _search='"+pageCnd.replaceAll("text", "title").replaceAll("\"", "")
					+ " OR "+pageCnd.replaceAll("text", "description").replaceAll("\"", "")
					+ " OR "+pageCnd.replaceAll("text", "user_name").replaceAll("\"", "")+"'";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs != null && rs.next()) {
				//填充数据
				document = new Document();
				document.setTitle(rs.getString("title"));
				document.setDescription(rs.getString("description"));
				document.setUser_name(rs.getString("user_name"));
				Array arr = rs.getArray("hltitle");
				if(arr != null){
					String str = arr.toString().substring(1);
					str = str.substring(0, str.length()-1);
					document.setTitle(str);
				}
				arr = rs.getArray("hldescription");
				if(arr != null){
					String str = arr.toString().substring(1);
					str = str.substring(0, str.length()-1);
					document.setDescription(str);
				}
				arr = rs.getArray("hluser_name");
				if(arr != null){
					String str = arr.toString().substring(1);
					str = str.substring(0, str.length()-1);
					document.setUser_name(str);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return document;
	}

	

}
