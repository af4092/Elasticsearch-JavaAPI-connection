package ElasticAPI.ElasticAPI;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class HighLevelClientLocalServer {

	    private static final boolean SearchHit = false;

		public static void main(String[] args) throws IOException {

	/******************************LOCAL HOST ELASTIC & KIBANA (WORKING ****** locally + docker)***********************************************/

	    	RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
	    	
	        SearchRequest searchRequest = new SearchRequest();
	        searchRequest.indices("countries");
	        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
	         
	        String nestedPath="country.city";
	        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
	        
	        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("country.city.type", "Uzbekistan");
	        NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(nestedPath, boolQueryBuilder.must(matchQuery), ScoreMode.None);
	        searchSourceBuilder.query(nestedQuery);
	        searchRequest.source(searchSourceBuilder);
	        java.util.Map<String, Object> map=null;
	          
	        try { 
	        	SearchResponse searchResponse = null;
	            searchResponse =client.search(searchRequest, RequestOptions.DEFAULT);
	            if (searchResponse.getHits().getTotalHits().value > 0) {
	                SearchHit[] searchHit = searchResponse.getHits().getHits();
	                for (SearchHit hit : searchHit) {
	                	String id = hit.getId();
	                    map = hit.getSourceAsMap();
	                      System.out.println("output: ID = " + id + ", " + Arrays.toString(map.entrySet().toArray()));
	                }
	            }
	           
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	         
	    }
	}

