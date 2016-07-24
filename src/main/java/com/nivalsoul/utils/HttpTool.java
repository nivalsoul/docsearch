package com.nivalsoul.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpTool {
	
	public static String get(String url){
		String result = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(url);
		    CloseableHttpResponse response = httpclient.execute(httpGet);
		    try{
				HttpEntity entity = response.getEntity();
				if (entity != null){
					result = EntityUtils.toString(entity);
				}
			}finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	    return result;
	}
   
	/**
	 * Post请求
	 * @param url 请求地址
	 * @param params 参数列表
	 * @return 请求结果正文
	 */
	public static String post(String url, Map<String, String>params) {
		String result = null;
		try{
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
	        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	        Iterator<Entry<String, String>> iter = params.entrySet().iterator();
	        while (iter.hasNext()) {
	        	Entry<String, String> entry = iter.next();
	        	String key = entry.getKey();
	        	String val = entry.getValue();
	        	nvps.add(new BasicNameValuePair(key, val));
	        }
	        httpPost.setEntity(new UrlEncodedFormEntity(nvps,"UTF-8"));
	        CloseableHttpResponse response = httpclient.execute(httpPost);
			try{
				HttpEntity entity = response.getEntity();
				if (entity != null){
					result = EntityUtils.toString(entity);
				}
			}finally {
				response.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}

}
