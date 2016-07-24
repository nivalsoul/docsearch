package com.nivalsoul.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix="file")  
@Component("fileSaveConfig")
public class FileSaveConfig {
	private String saveTo;
	private Map<String, String> configInfo;
	
	public String getSaveTo() {
		return saveTo;
	}
	public void setSaveTo(String saveTo) {
		this.saveTo = saveTo;
	}
	public Map<String, String> getConfigInfo() {
		return configInfo;
	}
	public void setConfigInfo(Map<String, String> configInfo) {
		this.configInfo = configInfo;
	}
	
}
