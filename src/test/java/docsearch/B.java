package docsearch;


import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.nivalsoul.utils.ESUtil;

public class B {

	public static void main(String[] args) throws SQLException {
		/*String sql = "select count(*) from category " + "where user_id='aaa-001-02'";
		show(sql);
		System.out.println(System.currentTimeMillis());
		ESUtil.exec("insert into docdive.category (parent_id,name,tenant_id,user_id,type)"
				+ " values(3, '分类1', '', 'aaa-001-02', '用户2')");
		System.out.println(System.currentTimeMillis());
		show(sql);*/

	}
	 private static boolean deleteDir(File dir) {
	        if (dir.isDirectory()) {
	            String[] children = dir.list();
	            for (int i=0; i<children.length; i++) {
	                boolean success = deleteDir(new File(dir, children[i]));
	                if (!success) {
	                    return false;
	                }
	            }
	        }
	        // 目录此时为空，可以删除
	        return dir.delete();
	    }
	
	private static void show(String sql) throws SQLException {
		ResultSet rs = ESUtil.query(sql);
		ResultSetMetaData rsmd = rs.getMetaData();
		int nrCols = rsmd.getColumnCount();
		System.out.println("=============");
		while (rs.next()) {
			for (int i = 1; i <= nrCols; i++) {
				System.out.print(rs.getObject(i) + "\t");
			}
			System.out.println();
		}

		System.out.println("=============");
		rs.close();
	}

}
