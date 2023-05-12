
package Nia;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class search_portal0008_ST extends search
{
    String rtnVal = "";

    public String execute()
    {
        return rtnVal;
    }

    public void search(RestHighLevelClient client , SearchSourceBuilder ssb,String keyword, int size ,int aggs_size)
    {
        try
        {
            //전처리

            SearchTemplateRequest request = new SearchTemplateRequest();
            request.setRequest(new SearchRequest("portal_public_searchlog"));//index

            request.setScriptType(ScriptType.STORED);
            request.setScript("portal_0008");//script title

            Map<String, Object> params = new HashMap<>();
            if (size!=0) params.put("size", size);
            if (aggs_size!=0) params.put("aggs_size", size);
            params.put("keyword", keyword);
            request.setScriptParams(params);

            /*쿼리 보기*/ 
            // XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();
            // System.out.println("requestBody: " + Strings.toString(request.toXContent(xContentBuilder, ToXContent.EMPTY_PARAMS)));

            SearchTemplateResponse response = client.searchTemplate(request, RequestOptions.DEFAULT);
            SearchResponse searchResponse = response.getResponse();

            /* 랜더링 된 쿼리 보기 */
            // request.setSimulate(true); 
            // BytesReference source = response.getSource(); 
            // System.out.println("requestBody: " + source.utf8ToString());
            
            //hits 검색
            SearchHits hits = searchResponse.getHits();

            // JSONArray req_array = new JSONArray();
            // for(SearchHit hit : hits)
            // {
            //     HashMap<String, Object> elements = new HashMap<String, Object>();
            //     elements.put("_score", hit.getScore());
            //     elements.putAll(hit.getSourceAsMap());
            //     JSONObject data = new JSONObject(elements);
            //     req_array.add(data);
            // }

            //집계
            JSONArray resultAgg1 = new JSONArray();
            Terms buck1 = searchResponse.getAggregations().get("co");
            for (Terms.Bucket entry : buck1.getBuckets()) 
            {
                HashMap<String, Object> elements = new HashMap<String, Object>();
                elements.put("key", entry.getKey());
                elements.put("doc_count", entry.getDocCount());
                JSONObject data = new JSONObject(elements);
                resultAgg1.add(data);
            }
            

            //result
            HashMap<String, Object> result = new HashMap<String, Object>();
            result.put("resultCode", "OK");
            result.put("resultCnt", hits.getTotalHits().value );   
            //result.put("resultMsg", req_array);
            result.put("resultMsg", resultAgg1);

            JSONObject jsonObject = new JSONObject(result);
            rtnVal = jsonObject.toString();
            
        }

        catch (Exception e) {

            HashMap<String, Object> err = new HashMap<String, Object>();
            err.put("resultCode", "ERROR");
            err.put("resultCnt", 0);
            err.put("resultMsg", e.getMessage());
            JSONObject errObj = new JSONObject(err);
            rtnVal = errObj.toString();

        } 
        
    }


}