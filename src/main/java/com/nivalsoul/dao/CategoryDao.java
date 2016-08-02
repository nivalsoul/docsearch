package com.nivalsoul.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.nivalsoul.model.Category;
import com.nivalsoul.utils.ESUtil;

@Component
public class CategoryDao {
	
	public int add(String sql) {
		return ESUtil.exec(sql);
	}

	public int add(Category category) {
		String sql = "insert into category (_id,parent_id,name,dept_id,user_id,type)" + " values('"
				+ category.get_id() + "', '" + category.getParent_id() + "', '" 
				+ category.getName() + "', '" + category.getDept_id() + "', '" 
				+ category.getUser_id() + "', '" + category.getType() + "')";
		return ESUtil.exec(sql);
	}

	public int update(Category category) {
		// use insert sql to update es
		return add(category);
	}

	public int delete(String userId, String categoryId) {
		String sql = "delete from category where user_id='" + userId 
				+ "' and _id = '" + categoryId + "'";
		return ESUtil.exec(sql);
	}


	public Category getCategoryById(String id) {
		Connection con = Dao.getConnection();
		Category category = null;
		try {
			String sql = "select * from category where _id='" + id + "'";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs != null && rs.next()) {
				category = new Category();
				category.set_id(id);
				category.setName(rs.getString("name"));
				category.setParent_id(rs.getString("parent_id"));
				category.setDept_id(rs.getString("dept_id"));
				category.setType(rs.getString("type"));
				category.setUser_id(rs.getString("user_id"));
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
		return category;
	}

	public Object getCategories(String userId) {
		Connection con = Dao.getConnection();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Category> list = new ArrayList<Category>();
		try {
			String sql = "select * from category "
				+ "where parent_id='0' and user_id='" + userId + "'";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs != null) {
				while(rs.next()){
					Category category = new Category();
					category.set_id(rs.getString("_id"));
					category.setName(rs.getString("name"));
					category.setParent_id("0");
					category.setDept_id(rs.getString("dept_id"));
					category.setType(rs.getString("type"));
					category.setUser_id(rs.getString("user_id"));
					list.add(category);
				}
			}
			result.put("code", 200);
			result.put("folders", list);
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

	public Category getCategoryByName(String userId, String name) {
		Connection con = Dao.getConnection();
		Category category = null;
		try {
			String sql = "select * from category where user_id='" + userId + "' and name='"+name+"'";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs != null && rs.next()) {
				category = new Category();
				category.set_id(rs.getString("_id"));
				category.setName(rs.getString("name"));
				category.setParent_id(rs.getString("parent_id"));
				category.setDept_id(rs.getString("dept_id"));
				category.setType(rs.getString("type"));
				category.setUser_id(rs.getString("user_id"));
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
		return category;
	}

}
