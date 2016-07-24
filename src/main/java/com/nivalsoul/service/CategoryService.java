package com.nivalsoul.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.nivalsoul.dao.CategoryDao;
import com.nivalsoul.dao.DocumentDao;
import com.nivalsoul.domain.Category;
import com.nivalsoul.domain.Document;
import com.nivalsoul.domain.ResultInfo;

@Service
public class CategoryService {

	@Autowired
	private CategoryDao categoryDao;
	
	@Autowired
	private DocumentDao documentDao;

	public Object getCategories(String userId, String categoryId) {
		// categoryId=0 获取所有文档夹，否则显示文档夹下的文档
		if ("0".equals(categoryId)) {
			return categoryDao.getCategories(userId);
		} else {
			return documentDao.getDocumentsByCategoryId(userId, categoryId);
		}
	}

	public Object addCategory(String userId, String name) {
		ResultInfo info = new ResultInfo();
		if (Strings.isNullOrEmpty(name)) {
			info.setCode(415); // 请求格式不匹配
			info.setMessage("cannot set the empty value of [name]");
			return info;
		}
		if(categoryDao.getCategoryByName(userId,name) != null){
			info.setCode(415);
			info.setMessage("the folder name["+name+"] already exists!");
			return info;
		}
		String id = UUID.randomUUID().toString();
		Category category = new Category();
		category.set_id(id);
		category.setName(name);
		category.setParent_id("0");
		category.setTenant_id("");
		category.setType("用户");
		category.setUser_id(userId);
		int count = categoryDao.add(category);
		if (count > 0) {
			info.setCode(200);
			info.setMessage("ok");
		}else {
			info.setCode(500);
			info.setMessage("add folder failed");
		}
		
		return info;
	}

	public Object updateCategory(String userId, String categoryId, String name) {
		ResultInfo info = new ResultInfo();
		if (Strings.isNullOrEmpty(name)) {
			info.setCode(415); // 请求格式不匹配
			info.setMessage("cannot set the empty value of [name]");
			return info;
		}
		Category category = categoryDao.getCategoryByName(userId,name);
		if(category != null && !category.get_id().equals(categoryId)){
			info.setCode(415);
			info.setMessage("the folder name["+name+"] already exists!");
			return info;
		}
		category = categoryDao.getCategoryById(categoryId);
		if (category == null) {
			info.setCode(404); // 请求资源不存在
			info.setMessage("cannot find the request resource");
			return info;
		}
		if (!category.getUser_id().equals(userId)) {
			info.setCode(403); // 没有权限
			info.setMessage("cannot modify category [" + name + "]: Permission denied");
			return info;
		}
		// use insert sql to update es
		category.setName(name);
		int count = categoryDao.update(category);
		if (count > 0) {
			info.setCode(200);
			info.setMessage("ok");
		}else{
			info.setCode(500);
			info.setMessage("update catefory failed, please try again");
		}
		
		return info;
	}

	public Object deleteCategory(String userId, String categoryId) {
		ResultInfo info = new ResultInfo();
		int count = categoryDao.delete(userId, categoryId);
		if (count == -1) {
			info.setCode(500);
			info.setMessage("delete catefory failed, please try again");
		}else if (count == 0) {
			info.setCode(400);
			info.setMessage("permisson denied or resource is not exist");
		}else {
			//删除文件夹，同时修改(删除？)文件夹下的文件（异步处理）
			new Thread(new Runnable() {
				@Override
				public void run() {
					Map<String, Object> res = (Map<String, Object>) documentDao.getDocumentsByCategoryId(userId, categoryId);
					List<Document> list = (List<Document>) res.get("documents");
					for (Document document : list) {
						document.setStatus("deleted");
						documentDao.update(document);
						//documentDao.delete(userId, document.get_id());
					}
				}
			}).start();
			info.setCode(200);
			info.setMessage("ok");
		}
		
		return info;
	}

}
