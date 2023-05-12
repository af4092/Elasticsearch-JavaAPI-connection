
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

public class search_dmap0012_ST extends search
{
    String rtnVal = "";

    public String execute()
    {
        return rtnVal;
    }

    public void search(RestHighLevelClient client , SearchSourceBuilder ssb,
                        String keyword,String operator, Integer aggsSize)
    {
        try
        {
            //전처리

            SearchTemplateRequest request = new SearchTemplateRequest();
            request.setRequest(new SearchRequest("dmap_map_user_do_log"));//index

            request.setScriptType(ScriptType.STORED);
            request.setScript("dmap_0012");//script title

            Map<String, Object> params = new HashMap<>();
            params.put("page", "");
            params.put("size", "");
            String[] excludeFields = {};
            params.put("excludeFields", excludeFields);

            params.put("keyword", keyword);
            params.put("operator", operator);
            params.put("aggs_size", aggsSize);

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

            SearchHits hits = searchResponse.getHits();

            //집계
            JSONArray resultAgg1 = new JSONArray();
            Terms buck1 = searchResponse.getAggregations().get("userlog");
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
            result.put("resultCnt", hits.getTotalHits().value);  
            result.put("resultAggCnt", resultAgg1.size());  
            result.put("resultAggsMsg", resultAgg1);

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