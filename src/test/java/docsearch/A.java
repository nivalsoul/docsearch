package docsearch;


import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.nutz.json.Json;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.facebook.presto.sql.tree.Select;
import com.nivalsoul.model.Document;
import com.nivalsoul.model.Page;
import com.nivalsoul.utils.ESUtil;

import nl.anchormen.sql4es.ESResultSet;

public class A{

	public static void main(String[] args) throws Exception {
		Class.forName("nl.anchormen.sql4es.jdbc.ESDriver");
		Properties info = new Properties();
		info.setProperty("cluster.name", "elasticsearch");
		//info.setProperty("fetch.size", "10");
		info.setProperty("scroll.timeout.sec", "100");
		info.setProperty("result.nested.lateral", "false");
		String url = "jdbc:sql4es://192.168.2.108:9300/test";
		//url = "jdbc:sql4es://10.111.131.22:9300/docdive";
		Connection con = DriverManager.getConnection(url,info);
		Statement st = con.createStatement();
		
		
		//int k = st.executeUpdate("insert into docdive.document2 select * from document");
		
		//update(st);
		
		/*String highlightSql = "select _score, highlight(title),highlight(file_name),title,file_name from document"
				+ " where title='曹操' and file_name='诸葛亮'" ;*/
		
		
		/*String sql = "select _score,highlight(text) as hltext,text,page from pages "
				+ "where _search='text:(\"数组\" AND \"变量\")' "
				+ "and document_id='305b7b30-44a0-4db5-bf46-0fad026312b3' order by page  limit 3";*/
		/*String sql = "select * from document where status='success' "
				+ "and _search='tenant_id:(\"7c9e5674-4376-4dd9-b125-d271d40ca2d8\") "
				+ "AND _all:(前端界面)' order by title asc limit 10";*/
		
	    /*String sql = "select _score,highlight(title) as hltitle, "
	    		+ "highlight(description) as hldescription, "
	    		+ "highlight(user_name) as hluser_name,"
	    		+ "title,description,user_name from document "
	    		+ "where _search='title:(知识) or description:(知识)'";*/
		
		//String sql="select title,created_at,page_count from document where status='success' order by title desc limit 10";
		//show(st, sql );
		
	  /*  int k = st.executeUpdate("INSERT INTO t2 (title2,text2) "
	    		+ "VALUES ('abc2015年年终总结以及下年度工作计划', '这是一篇全文检索测试'),('ddjava葵花宝典', '好好学习'),"
	    		+ "('cab中国English学习比赛example or demo', '上海东方明珠广播电视塔')");
		System.out.println("num=="+k);*/
		
		/*int r = st.executeUpdate("update pages set page=(_source.page+5) where _id='8'");
		System.out.println("===="+r);*/

		/*int m = st.executeUpdate("delete from ordersDate");
		System.out.println("===="+m);*/
		
		
		String sql = "select * from t2";
		show(st, sql);
		con.close();
	}
	
	public static void show(Statement st, String sql) throws SQLException, FileNotFoundException {
		ResultSet rs = st.executeQuery(sql);
		ResultSetMetaData rsmd = rs.getMetaData();
		int nrCols = rsmd.getColumnCount();
		while (rs.next()) {
			System.out.println("=============");
			for (int i = 1; i <= nrCols; i++) {
				System.out.print(rs.getObject(i) + "\t");
				//ps.println(rs.getString(2)+"\t\t"+rs.getObject(3));
			}
			System.out.println();
			
		}
		
		rs.close();
	}

	
	private static void update(Statement st) {
		String sql = "select * from document " ;
		try{
		ResultSet rs = st.executeQuery(sql);
		ResultSetMetaData rsmd = rs.getMetaData();
		int nrCols = rsmd.getColumnCount();
		System.out.println("=============");
		while (rs.next()) {
			Document doc = fillData(rs);
			String jsonStr = Json.toJson(doc);
			Map data = Json.fromJsonAsMap(Object.class, jsonStr);
			data.remove("tenant_name");
			int count = new ESUtil("elasticsearch", "172.16.50.81", 19300)
			    .singleRequest("docdive", "document", data);
		}
		
		System.out.println("=============");
		rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static Document fillData(ResultSet rs) throws SQLException {
		Document document = new Document();
		document.set_id(rs.getString("_id"));
		String tenant_id = rs.getString("tenant_id");
		document.setTenant_id(tenant_id);
		document.setTenant_name(rs.getString("tenant_name"));
		document.setCategory_id(rs.getString("category_id"));
		document.setUser_id(rs.getString("user_id"));
		document.setUser_name(rs.getString("user_name"));
		document.setCreated_at(rs.getString("created_at"));
		document.setUpdated_at(rs.getString("update_at"));
		document.setFile_name(rs.getString("file_name"));
		document.setTitle(rs.getString("title"));
		document.setDescription(rs.getString("description"));
		document.setStatus(rs.getString("status"));
		document.setFormat(rs.getString("format"));
		document.setFile_hash(rs.getString("file_hash"));
		document.setPage_count(rs.getInt("page_count"));
		//页面内容转为List对象
		//ESResultSet pagesrs.getObject("pages", ESResultSet.class)；//不使用druid连接池时，可以使用该方法：
		List<Page> pageList = new ArrayList<Page>();
		ESResultSet pages = (ESResultSet)rs.getObject("pages");
		if(pages != null){
			while(pages.next()){
				Page page = new Page();
				page.setPage(pages.getInt("page"));
				page.setText(pages.getString("text"));
				pageList.add(page);
			}
		}
		document.setPages(pageList);
		return document;
	}

	

	protected static Set<String> query(Statement st, String sql) throws SQLException {
		ResultSet rs = st.executeQuery(sql);
		Set<String> result = new HashSet<String>();
		while (rs.next()) {
			result.add(rs.getString("CT_JDCHPHM"));
		}
		rs.close();
		return result;
	}
	
	
	private static void queryAllGroupAndLike(Statement st) throws Exception {
		long  start = System.currentTimeMillis();
		
		String sql = "select _score,CT_JDCHPHM,count(*) from jjkkxx where (SBBH='5%' and GCSJ<='2015-12-01T11:58:45.000Z'  and''"
				+ " GCSJ >='2015-12-05T11:58:45.000Z' and search='_all:('东坡立交'))  and  (SBBH='5%' and GCSJ<='2015-12-01T11:58:45.000Z'  and''"
				+ " GCSJ >='2015-12-08T11:58:45.000Z' and search='_all:('人民南路'))  and   (SBBH='5%' and GCSJ<='2015-12-03T11:58:45.000Z'  and''"
				+ " GCSJ >='2015-12-07T11:58:45.000Z' and search='_all:('人民南路'))"
				+ "  group by CT_JDCHPHM limit 10";
	
		 show(st, sql );
		long end = System.currentTimeMillis();
		System.out.println(" use: "+(end-start)/1000+" s");
		start = end;		
	}
	
	

	protected static void insert(Statement st) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/wuji", "wuji", "abc123456");
		Statement smt = con.createStatement();
		ResultSet rs = smt.executeQuery("select * from article limit 5");
		ResultSetMetaData rsmd = rs.getMetaData();
		int nrCols = rsmd.getColumnCount();
		String insertSql = "insert into article (id,article_title,article_content,"
				+ "author,article_type,pub_time) values(";
		while (rs.next()) {
			String sql = insertSql;
			sql += rs.getLong("id") + ",";
			sql += "'" + rs.getString("article_title").replaceAll("'", "\"") + "',";
			sql += "'" + rs.getString("article_content").replaceAll("'", "\"") + "',";
			sql += "'" + rs.getString("author").replaceAll("'", "\"") + "',";
			sql += "'" + rs.getString("article_type") + "',";
			sql += "'" + rs.getTimestamp("pub_time") + "')";
			try {
				//st.executeUpdate(sql);
				System.out.println(sql);
			} catch (Exception e) {
				System.out.println("id:"+rs.getLong("id")+" error..");
			}
			
		}

		rs.close();
		// 
	}

}
