package com.nivalsoul.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="sql4es")  
public class ESConfig {
	private String driver;
	private String url;
	private int maxCon;
	private int minCon;
	private String clusterName;
	private String esHosts;
	private int fetchSize;
	
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getMaxCon() {
		return maxCon;
	}
	public void setMaxCon(int maxCon) {
		this.maxCon = maxCon;
	}
	public int getMinCon() {
		return minCon;
	}
	public void setMinCon(int minCon) {
		this.minCon = minCon;
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	public int getFetchSize() {
		return fetchSize;
	}
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}
	public String getEsHosts() {
		return esHosts;
	}
	public void setEsHosts(String esHosts) {
		this.esHosts = esHosts;
	}
	
}
