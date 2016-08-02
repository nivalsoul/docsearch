package docsearch;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.nutz.json.Json;

import com.alibaba.fastjson.JSON;
import com.nivalsoul.model.Document;
import com.nivalsoul.model.Page;

public class ESAPI {

	public static void main(String[] args) {
		try {
			Settings settings = Settings.settingsBuilder()
					.put("cluster.name", "elasticsearch").build();
			Client client = TransportClient.builder().settings(settings).build()
					.addTransportAddress(new InetSocketTransportAddress(
							InetAddress.getByName("192.168.2.108"), 9300));
			
			query(client);
			
			//index(client);
			
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private static void query(Client client) {
		SearchRequestBuilder searchReq = client.prepareSearch("test");
		SearchRequestBuilder req = searchReq.setTypes("document");
		QueryBuilder qb = QueryBuilders.hasChildQuery(
			    "pages",                     
			    QueryBuilders.termQuery("text","revolution") 
		);
		req.setQuery(qb);
		//req.setFrom(6).setSize(3);
		//req.addSort(SortBuilders.fieldSort("title").order(SortOrder.DESC));
		SearchResponse res = req.execute().actionGet();
		int k=0;
		for (SearchHit hit :res.getHits().getHits()) {
			System.out.println("========"+(k++));
			System.out.println(hit.getId());
		}
	}

	private static void index(Client client) {
		IndexResponse response = null;
		Document doc = getDoc();
		String jsonStr = Json.toJson(doc);
		Map data = Json.fromJsonAsMap(Object.class, jsonStr);
		System.out.println(data);
		String id = String.valueOf(data.get("_id"));
		data.remove("_id");
		response = client.prepareIndex("docdive", "document", id)
				.setSource(JSON.toJSONString(data)).setRefresh(true).get();
		boolean status = response.isCreated();
		System.out.println(status);
	}

	private static Document getDoc() {
		Document doc = new Document();
		doc.set_id("020b25fb-1a42-4c2b-83b7-26c34aa63b5c");
		doc.setCategory_id("fbc12eb5-11da-43e1-bd6d-395f7a5b56c4");
		doc.setDept_id("4906d847-83b6-468f-b8bd-f91969985542");
		doc.setUser_id("4191994b-66b4-4c28-8b0a-39f215a735b8");
		doc.setUser_name("xuwenlong@chinacloud.com.cn");
		doc.setTitle("简桢散文集");
		doc.setDescription("原创散文");
		doc.setStatus("uploaded");
		doc.setFormat("");
		doc.setCreated_at("2016-05-18 09:31:20");
		doc.setUpdated_at("2016-05-18 09:31:20");
		doc.setPage_count(0);
		List<Page> list = new ArrayList<Page>();
		Page page = new Page();
		page.setPage(1);
		page.setText("是不是柳烟太浓密，你寻不着春日的门扉？");
		list.add(page);
		page = new Page();
		page.setPage(2);
		page.setText("是不是湖中无堤无桥，你泅不到芳香的草岸？");
		list.add(page);
		doc.setPages(list);
		doc.setFile_hash("");
		return doc;
	}

}
