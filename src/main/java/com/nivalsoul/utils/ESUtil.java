package com.nivalsoul.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.alibaba.fastjson.JSON;
import com.nivalsoul.dao.Dao;

public class ESUtil {
	
	private String clasterName = "elasticsearch";
	private String ip = "192.168.1.104";
	private int port = 9400;
	
	public ESUtil(String clasterName, String ip, int port) {
		if(clasterName!=null && !clasterName.equals(""))
		    this.clasterName = clasterName;
		this.ip = ip;
		this.port = port;
	}
	
	
	private Client getClient() throws UnknownHostException {
		Settings settings = Settings.settingsBuilder()
				.put("cluster.name", clasterName).build();
		Client client = TransportClient.builder().settings(settings).build()
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), port));
		return client;
	}
	
	/**
	 * 单次提交
	 * @param database 索引库名称
	 * @param table    表名
	 * @param data     一行数据，map类型，指定主键的话key为"_id"
	 * @return 
	 */
	public int singleRequest(String database, String table, Map<String, Object> data) {
		int count = -1;
		try {
			Client client = getClient();
			IndexResponse response;
			String id = null;
			if(data.containsKey("_id")){
				id = String.valueOf(data.get("_id"));
				data.remove("_id");
			}
			IndexRequestBuilder req = client.prepareIndex(database, table, id);
			long v = -1;
			if(data.containsKey("_version")){
				v = Long.valueOf(data.get("_version").toString());
				data.remove("_version");
				req = req.setVersion(v);
			}
			//必须先setVersion再setSource，为何？
			req = req.setSource(JSON.toJSONString(data));
			response = req.setRefresh(true).get();
			count = response.isCreated() ? 1 : 0;
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * bulk批量提交
	 * @param database 索引库名称
	 * @param table    表名
	 * @param rows     多行数据，每行都是一个map类型，指定主键的话key为"_id"
	 * @return         成功返回OK，否则返回错误信息
	 */
	public int bulkRequest(String database, String table, List<Map<String, Object>> rows) {
		int count = -1;
		try {
			Client client = getClient();
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			for (Map<String, Object> data : rows) {
				String id = null;
				if(data.containsKey("_id")){
					id = String.valueOf(data.get("_id"));
					data.remove("_id");
				}
				IndexRequestBuilder req = client.prepareIndex(database, table, id);
				long v = -1;
				if(data.containsKey("_version")){
					v = Long.valueOf(data.get("_version").toString());
					data.remove("_version");
					req = req.setVersion(v);
				}
				//必须先setVersion再setSource，为何？
				req = req.setSource(JSON.toJSONString(data));
				bulkRequest.add(req);
			}
			BulkResponse bulkResponse = bulkRequest.setRefresh(true).get();
			if (!bulkResponse.hasFailures()) {
				count = 1;
			}
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public Map<String, Object> getOneByID(String database, String table, String id) {
		Map<String, Object> data = null;
		try {
			Client client = getClient();
			GetResponse response = client.prepareGet(database, table, id).get();
			data = response.getSource();
			data.put("_version", response.getVersion());
			data.put("_id", id);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return data;
	}
	
	/**
	 * 通过sql4es执行sql语句
	 * @param sql
	 * @return
	 */
	public static int exec(String sql) {
		int count = -1;
		try {
			Connection con = Dao.getConnection();
			/*Class.forName("nl.anchormen.sql4es.jdbc.ESDriver");
			Properties info = new Properties();
			info.setProperty("cluster.name", "elasticsearch");
			info.setProperty("scroll.timeout.sec", "100");
			//info.setProperty("result.nested.lateral", "false");
			String url = "jdbc:sql4es://172.16.50.81:19300/docdive";
			Connection con = DriverManager.getConnection(url,info);*/
			Statement st = con.createStatement();
			count = st.executeUpdate(sql);
			con.close();
			//由于es默认有1s的延迟，故等待1s后返回
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return count;
	}
	
	/**
	 * 通过sql4es执行查询语句
	 * @param sql
	 * @return 
	 */
	public static ResultSet query(String sql) {
		ResultSet rs = null;
		try {
			Connection con = Dao.getConnection();
			/*Class.forName("nl.anchormen.sql4es.jdbc.ESDriver");
			Properties info = new Properties();
			info.setProperty("cluster.name", "elasticsearch");
			info.setProperty("scroll.timeout.sec", "100");
			info.setProperty("result.nested.lateral", "false"); //内嵌对象聚合成一条数据
			String url = "jdbc:sql4es://172.16.50.81:19300/docdive";
			Connection con = DriverManager.getConnection(url,info);*/
			Statement st = con.createStatement();
			rs = st.executeQuery(sql);
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
}
