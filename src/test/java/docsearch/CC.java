package docsearch;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.nivalsoul.utils.ESUtil;

public class CC {

	public static void main(String[] args) throws Exception {
		ESUtil esUtil = new ESUtil("elasticsearch", "192.168.2.108", 9300);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "解密「云计算的太祖长拳」系列之一“胆”：基础网络改造与新架构");
		map.put("commentCount", 2);
		List<Map<String, Object>> comments = new ArrayList<Map<String, Object>>();
		Map<String, Object> c = new HashMap<String, Object>();
		c.put("commentor", "aa");
		c.put("riqi", "2016-07-08 12:33:05");
		comments.add(c);
		c = new HashMap<String, Object>();
		c.put("commentor", "bb");
		c.put("riqi", "2016-07-08 11:23:08");
		comments.add(c);
		map.put("comments", comments);
		System.out.println(JSON.toJSON(map));
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add(map);
		int result = new ESUtil("elasticsearch", "192.168.2.108", 9300)
				.bulkRequest("jingzong", "article", list);
		System.out.println(result);
		
		/*Map<String, Object> data = esUtil.getOneByID("jingzong", "article", "16668");
		data.put("author", "abc");
		//data.put("_version",6);
		int r = esUtil.singleRequest("jingzong", "article", data);
		System.out.println(r);*/

		/*String url="jdbc:sql4es://172.16.50.81:19300/jingzong";
		String[] ip_port = url.split("//")[1].split("/")[0].split(":");
		System.err.println(ip_port[0]);*/
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
