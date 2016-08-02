package com.nivalsoul.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nivalsoul.service.CategoryService;

@Controller
@RequestMapping("/v1/docdive")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@RequestMapping(value = "/categories/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Object getCategories(HttpServletRequest request, @PathVariable("id") String categoryId) {
		String userId = (String) request.getAttribute("userId");
		return categoryService.getCategories(userId, categoryId);
	}

	@RequestMapping(value = "/categories", method = RequestMethod.POST)
	@ResponseBody
	public Object addCategory(HttpServletRequest request, @RequestBody Map<String, Object> data) {
		String userId = (String) request.getAttribute("userId");
		String name = (String) data.get("name");
		return categoryService.addCategory(userId, name);
	}

	@RequestMapping(value = "/categories/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public Object updateCategory(HttpServletRequest request, @PathVariable("id") String categoryId,
			@RequestBody Map<String, Object> data) {
		String userId = (String) request.getAttribute("userId");
		String name = (String) data.get("name");
		return categoryService.updateCategory(userId, categoryId, name);
	}

	@RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object deleteCategory(HttpServletRequest request, @PathVariable("id") String categoryId) {
		String userId = (String) request.getAttribute("userId");
		return categoryService.deleteCategory(userId, categoryId);
	}

}
