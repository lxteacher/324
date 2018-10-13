package cn.itcast.solrj;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

/**
 * SolrJ的管理
 * 
 * @author lx
 *
 */
public class SolrJDemo {

	
	//添加  修改
	@Test
	public void testAdd() throws Exception {
		//1:连接上SOlr服务器
		String baseURL = "http://localhost:8080/solr/";
		SolrServer solrServer = new HttpSolrServer(baseURL);
		//2:添加
		SolrInputDocument doc = new SolrInputDocument();
		doc.setField("id", 3);
		doc.setField("name", "赵云");//Solr 某个域 1:先定义域  2:再使用域   未定义域不能使用  Apache默认存在 定义好了  修改域
		solrServer.add(doc, 1000);
	}
	//删除
	@Test
	public void testDelete() throws Exception {
		//1:连接上SOlr服务器
		String baseURL = "http://localhost:8080/solr/";
		SolrServer solrServer = new HttpSolrServer(baseURL);
		solrServer.deleteById("1");
		solrServer.commit();
	}
	//查询
	@Test
	public void testQuery() throws Exception {
		//1:连接上SOlr服务器
		String baseURL = "http://localhost:8080/solr/";
		SolrServer solrServer = new HttpSolrServer(baseURL);
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.set("q", "*:*");
		QueryResponse response = solrServer.query(solrQuery);
		
		//结果集Docs
		SolrDocumentList docs = response.getResults();
		System.out.println("总条数:" + docs.getNumFound());
		
		for (SolrDocument doc : docs) {
			System.out.println("id:" + doc.get("id"));
			System.out.println("title:" + doc.get("title"));
			System.out.println("name:" + doc.get("name"));
		}
	}
	//SOlrJ完成复杂查询
	@Test
	public void testQuery1() throws Exception {
		//1:连接上SOlr服务器
		String baseURL = "http://localhost:8080/solr/";
		SolrServer solrServer = new HttpSolrServer(baseURL);
		
		SolrQuery solrQuery = new SolrQuery();
		//关键词
		solrQuery.set("q", "钻石");
		//默认域
		solrQuery.set("df", "product_keywords");
		//过滤条件
		solrQuery.set("fq", "product_price:{33 TO 80}");
		//排序
		solrQuery.addSort("product_price", ORDER.desc);
		//查询指定的域
		solrQuery.set("fl", "id,product_name,product_price");
		//分页
		solrQuery.setStart(0);
		solrQuery.setRows(2);
		//高亮
		//打开高亮的开关
		solrQuery.setHighlight(true);
		//高亮的域
		solrQuery.addHighlightField("product_name");
		//<font color='red'>
		solrQuery.setHighlightSimplePre("<font color='red'>");
		solrQuery.setHighlightSimplePost("</font>");
		
		//执行查询
		QueryResponse response = solrServer.query(solrQuery);
		//结果集
		SolrDocumentList docs = response.getResults();
		long numFound = docs.getNumFound();
		//高亮结果集
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		
		System.out.println("总条数:" + numFound);
		//请求文档的ID 名称  价格 等 打印出来
		
		for (SolrDocument doc : docs) {
			System.out.println("id:" + doc.get("id"));
			System.out.println("普通product_name:" + doc.get("product_name"));
			
			Map<String, List<String>> map = highlighting.get(doc.get("id"));
			if(null != map && map.size() > 0){
				List<String> list = map.get("product_name");
				System.out.println("高亮的product_name:" + list.get(0));
			}
			System.out.println("product_price:" + doc.get("product_price"));
		}
	}
}
