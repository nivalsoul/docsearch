package com.nivalsoul.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.nivalsoul.config.ESConfig;

public class Dao {
	
	private static ESConfig esConfig = null;

	private static DruidDataSource ds = null;
	
	public static void init(ESConfig config) {
		System.out.println("初始化连接池...");
		esConfig = config;
		ds = new DruidDataSource();
		ds.setDriverClassName(config.getDriver());
		ds.setUrl(config.getUrl());
		ds.setMaxActive(config.getMaxCon());
		ds.setMinIdle(config.getMinCon());
		Properties info = new Properties();
		info.setProperty("cluster.name", config.getClusterName());
		info.setProperty("fetch.size", String.valueOf(config.getFetchSize()));
		info.setProperty("result.nested.lateral", "false"); //内嵌对象聚合成一条数据
		//info.setProperty("es.hosts", config.getEsHosts());
		ds.setConnectProperties(info);
	}


	public static Connection getConnection(){
		Connection con = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public static Connection getConNotPool() {
		Connection con = null;
		try{
			Class.forName(esConfig.getDriver());
			Properties info = new Properties();
			info.setProperty("cluster.name", esConfig.getClusterName());
			info.setProperty("fetch.size", String.valueOf(esConfig.getFetchSize()));
			info.setProperty("result.nested.lateral", "false");
			con = DriverManager.getConnection(esConfig.getUrl(),info);
		}catch(Exception e){
			;
		}
		return con;
	}
	
	public static void closeAllConnetion() {
		ds.close();
	}
}
