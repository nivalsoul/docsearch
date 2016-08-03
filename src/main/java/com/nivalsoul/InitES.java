package com.nivalsoul;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nivalsoul.utils.HttpTool;

public class InitES {

	public static void main(String[] args) throws IOException {
		String data = FileUtils.readFileToString(new File("src/main/resources/ESMapping.txt"));
		JSONObject obj = JSON.parseObject(data);
		String url = obj.getString("url");
		//创建索引库
		HttpTool.post(url, "{}");
		JSONArray types = obj.getJSONArray("types");
		//分别创建表
		for (Object t : types) {
			JSONObject jo = (JSONObject)t;
			Set<String> keys = jo.keySet();
			String type = keys.iterator().next();
			HttpTool.post(url+"/"+type+"/_mapping", jo.toJSONString());
		}
	}

}
