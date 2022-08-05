package com.example.parse_demo.utils; /**
 * @author GaoLiuKai
 * @date 2022/8/5 11:15
 */


import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class elasticsearchUtil {
    public static void main(String[] args) throws Exception {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("192.168.108.128", 9200, "http")));
        getSearch(client);
        client.close();
    }

    /**
     * 添加索引
     */
    public static void addIndex(RestHighLevelClient client ) throws Exception {
        IndicesClient indices = client.indices();
        //设置索引名称
        CreateIndexRequest createIndexRequest=new CreateIndexRequest("ws");
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse.isAcknowledged());
    }

    /**
     * 添加索引，并添加映射
     */
    public static void addIndexAndMapping(RestHighLevelClient client) throws Exception {
        IndicesClient indices = client.indices();
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("ws");
        //设置mappings
        String mapping = "    {\n" +
                "          \"properties\": {\n" +
                "            \"user\": {\n" +
                "              \"type\": \"text\"\n" +
                "            },\n" +
                "            \"title\": {\n" +
                "              \"type\": \"text\"\n" +
                "            },\n" +
                "            \"desc\": {\n" +
                "              \"type\": \"text\"\n" +
                "            }\n" +
                "          }\n" +
                "    }";

        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse.isAcknowledged());
    }

    /**
     * 查询索引
     */
    public static  void queryIndex(RestHighLevelClient client) throws Exception {
        IndicesClient indices = client.indices();
        GetIndexRequest getRequest=new GetIndexRequest("ws");
        GetIndexResponse response = indices.get(getRequest, RequestOptions.DEFAULT);
        Map<String,MappingMetadata> mappings = response.getMappings();
        for (Object key : mappings.keySet()) {
            System.out.println(key+"==="+mappings.get(key).getSourceAsMap());
        }
    }

    /**
     * 删除索引
     */
    public static void deleteIndex(RestHighLevelClient client) throws IOException {
        IndicesClient indices = client.indices();
        DeleteIndexRequest deleteRequest=new DeleteIndexRequest("ws");
        AcknowledgedResponse delete = indices.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    /**
     * 索引是否存在
     */
    public static void existIndex(RestHighLevelClient client) throws IOException {
        IndicesClient indices = client.indices();
        GetIndexRequest getIndexRequest=new GetIndexRequest("ws");
        boolean exists = indices.exists(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * 添加文档
     */
    public static void addDoc(RestHighLevelClient client) throws Exception {
        Map<String, Object> map=new HashMap<>();
        map.put("user","ws2");
        map.put("title","2");
        map.put("desc","2");
        IndexRequest request=new IndexRequest("ws").id("2").source(map);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.getId());
    }

    /**
     * 根据id查询文档
     */
    public static void getDoc(RestHighLevelClient client) throws IOException {
        GetRequest indexRequest=new GetRequest("ws","1");
        GetResponse response = client.get(indexRequest, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
    }

    /**
     * 高级查询
     */
    public static void getSearch(RestHighLevelClient client) throws IOException {
        /**
         * 模糊查询?匹配单个字符，*匹配多个字符
         *  QueryBuilders.wildcardQuery("ws","?ws*");
         *  匹配单个字段
         *   matchQuery("filedname","value")
         *   多个字段匹配某一个值
         *   QueryBuilders.multiMatchQuery("music","name", "interest");搜索name中或interest中包含有music的文档
         termQuery("key", obj) 完全匹配
         termsQuery("key", obj1, obj2..)   一次匹配多个值
         //范围查询
         QueryBuilders.rangeQuery("id").from(5).to(7) // 包含上届
         聚合查询
         AvgAggregationBuilder field = AggregationBuilders.avg("avg_num").field("like");
         minQuery("")
         maxQuery( )
         valueCountQuery()统计个数
         */
        //条件布尔构造（组合匹配条件，都满足为true）
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("user","ws333"));
        //should会带一个以上的条件，至少满足一个条件，类似in
        boolQueryBuilder.should(QueryBuilders.matchQuery("user","ws1"));

        //创建查询
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.from(0);
        sourceBuilder.size(1);
        //sourceBuilder.sort("")
//        sourceBuilder.timeout()
        //创建查询索引对象
        SearchRequest searchRequest = new SearchRequest("ws");
        searchRequest.source(sourceBuilder);
        SearchResponse response=client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = response.getHits().getHits();
        for(SearchHit hit : hits){
            System.out.println(hit);
        }

    }

    /**
     * 根据id删除文档
     */
    public static void delDoc(RestHighLevelClient client) throws IOException {
        DeleteRequest deleteRequest=new DeleteRequest("ws","1");
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(response.getId());
    }

}


