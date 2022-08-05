package com.example.parse_demo.service.implement;

import com.example.parse_demo.service.esServices.IndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 索引操作
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class IndexServiceImpl implements IndexService {

    private final RestHighLevelClient client;

    /**
     * 验证索引是否存在
     */
    @Override
    public boolean existsIndex(String indexName) {
        boolean exists;
        try {
            // 获取索引请求
            GetIndexRequest request = new GetIndexRequest();

            // 设置要查询的索引名称
            request.indices(indexName);
            request.indicesOptions(null);
            exists = client.indices().exists(request, RequestOptions.DEFAULT);
            return exists;
        } catch (IOException e) {
            log.error("", e);
        }
        return false;
    }

    /**
     * 创建索引
     */
    @Override
    public CreateIndexResponse createIndex(String indexName, String source) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexName);

        request.setTimeout(TimeValue.timeValueMinutes(2));
        request.setMasterTimeout(TimeValue.timeValueMinutes(1));
        return client.indices().create(request, RequestOptions.DEFAULT);

    }

    /**
     * 创建索引
     */
    @Override
    public void createIndex(String indexName, Map<String, Object> properties) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 1)

        );

        Map<String, Object> indexSettings = new HashMap<>();
        indexSettings.put("index.analysis",new HashMap<>(){
            {
                put("filter",new HashMap<>(){
                    {
                        put("my_stopwords",new HashMap<>(){
                            {
                                put("type","stop");
                            }
                        });
                    }
                });
                put("index.analyzer",new HashMap<>(){
                    {
                        put("max_analyzer",new HashMap<>(){
                            {
                                put("type","custom");
                                put("char_filter",new ArrayList<String>(){
                                    {
                                        add("html_strip");
                                    }
                                });
                                put("tokenizer","ik_max_word");
                            }
                        });
                        put("smart_analyzer",new HashMap<>(){
                            {
                                put("type","custom");
                                put("char_filter",new ArrayList<String>(){
                                    {
                                        add("html_strip");
                                    }
                                });
                                put("tokenizer","ik_max_word");
                            }
                        });
                    }
                });
            }
        });
        request.mapping(properties);
        request.settings(indexSettings);

        request.setTimeout(TimeValue.timeValueMinutes(2));
        request.setMasterTimeout(TimeValue.timeValueMinutes(1));
        try {
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            if (createIndexResponse.isAcknowledged()) {
                log.info(new HashMap<String, String>() {
                    {
                        put("acknowledged", "true");
                    }
                }.toString());
            }
        } catch (ElasticsearchStatusException e) {
            log.error(e.getMessage());
        }
    }

    /***
     * 修改索引mapping
     */
    @Override
    public void putMapping(String indexName, Map<String, Object> properties) throws IOException {
        PutMappingRequest request = new PutMappingRequest(indexName);
        request.source(properties);
        try {
            AcknowledgedResponse putMappingResponse = client.indices().putMapping(request, RequestOptions.DEFAULT);
            if (putMappingResponse.isAcknowledged()) {
                log.info(new HashMap<String, String>() {
                    {
                        put("acknowledged", "true");
                    }
                }.toString());
            }
        } catch (ElasticsearchStatusException e) {
            log.error(e.getMessage());
        }
    }

    /***
     * 更新索引
     */
    @Override
    public void createIndexOrPutMappings(String indexName, Map<String, Object> properties) throws IOException {
        if (existsIndex(indexName)) {
            putMapping(indexName, properties);
        } else {
            createIndex(indexName, properties);
        }
    }

    public void initIndex(String indexName) throws IOException {
        log.info("开始初始化案例数据索引...");
        Map<String, Object> properties = new HashMap<String, Object>() {
            {
                put("properties", new HashMap<String, Object>() {
                    {
                        put("caseNumber", new HashMap<String, String>() {
                            {
                                put("type", "keyword");
                            }
                        });
                        put("caseNumberType", new HashMap<String, String>() {
                            {
                                put("type", "keyword");
                            }
                        });
                        put("caseNumberYear", new HashMap<String, String>() {
                            {
                                put("type", "integer");
                            }
                        });
                        put("caseType", new HashMap<String, String>() {
                            {
                                put("type", "keyword");
                            }
                        });
                        put("causes", new HashMap<String, String>() {
                            {
                                put("type", "keyword");
                            }
                        });
                        put("city", new HashMap<String, String>() {
                            {
                                put("type", "keyword");
                            }
                        });
                        put("claims", new HashMap<String, String>() {
                            {
                                put("type", "text");
                            }
                        });
                        put("content",new HashMap<String, Object>(){
                            {
                                put("type","text");
                                put("fields", new HashMap<String, Object>(){
                                    {
                                        put("max",new HashMap<String, String>(){
                                            {
                                                put("type","text");
                                                put("analyzer","max_analyzer");
                                            }
                                        });
                                        put("smart",new HashMap<String, Object>(){
                                            {
                                                put("type","text");
                                                put("analyzer","max_analyzer");
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        put("court", new HashMap<String, String>() {
                            {
                                put("type", "keyword");
                            }
                        });
                        put("courtConsider", new HashMap<String, String>() {
                            {
                                put("type", "keyword");
                            }
                        });
                        put("courtId", new HashMap<String, String>() {
                            {
                                put("type", "keyword");
                            }
                        });
                        put("courtType", new HashMap<String, String>() {
                            {
                                put("type", "keyword");
                            }
                        });
                        put("createTime", new HashMap<String, String>() {
                            {
                                put("type", "date");
                            }
                        });
                        put("decideTime", new HashMap<String, String>() {
                            {
                                put("type", "date");
                            }
                        });
                        put("decideTimeYear", new HashMap<String, String>() {
                            {
                                put("type", "integer");
                            }
                        });
                        put("deleted", new HashMap<String, String>() {
                            {
                                put("type", "boolean");
                            }
                        });
                        put("docId", new HashMap<String, String>() {
                            {
                                put("type", "keyword");
                            }
                        });
                        put("docType", new HashMap<String, String>() {
                            {
                                put("type", "keyword");
                            }
                        });
                        put("errors", new HashMap<String, String>() {
                            {
                                put("type", "keyword");
                            }
                        });
                        put("evidences", new HashMap<String, Object>() {
                            {
                                put("type", "text");
                                put("fields", new HashMap<String, Object>() {
                                    {
                                        put("keyword", new HashMap<String, Object>() {
                                            {
                                                put("type", "keyword");
                                                put("ignore_above",256);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        put("fields", new HashMap<String, Object>() {
                            {
                                put("type", "nested");
                                put("properties", new HashMap<String, Object>() {
                                    {
                                        put("fieldId", new HashMap<String, Object>() {
                                            {
                                                put("type", "keyword");
                                            }
                                        });
                                        put("fieldName", new HashMap<String, Object>() {
                                            {
                                                put("type", "keyword");
                                            }
                                        });
                                        put("name", new HashMap<String, Object>() {
                                            {
                                                put("type", "text");
                                                put("keyword", new HashMap<String, Object>() {
                                                    {
                                                        put("type", "keyword");
                                                        put("ignore_above",256);
                                                    }
                                                });
                                            }
                                        });
                                        put("rootId",new HashMap<String, String>(){
                                            {
                                                put("type","keyword");
                                            }
                                        });
                                        put("value", new HashMap<String, Object>() {
                                            {
                                                put("type", "text");
                                                put("keyword", new HashMap<String, Object>() {
                                                    {
                                                        put("type", "keyword");
                                                        put("ignore_above",256);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        put("focusOfControversy",new HashMap<String, Object>(){
                            {
                                put("type","text");
                            }
                        });
                        put("forSearch", new HashMap<String, Object>() {
                            {
                                put("properties", new HashMap<String, Object>() {
                                    {
                                        put("cause", new HashMap<String, Object>() {
                                            {
                                                put("type", "text");
                                                put("fields",new HashMap<String, Object>(){
                                                    {
                                                        put("max",new HashMap<String, String>(){
                                                            {
                                                                put("type","text");
                                                                put("analyzer","max_analyzer");
                                                            }
                                                        });
                                                        put("smart",new HashMap<String, String>(){
                                                            {
                                                                put("type","text");
                                                                put("analyzer","smart_analyzer");
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                        put("court", new HashMap<String, Object>() {
                                            {
                                                put("type", "text");
                                                put("fields",new HashMap<String, Object>(){
                                                    {
                                                        put("max",new HashMap<String, String>(){
                                                            {
                                                                put("type","text");
                                                                put("analyzer","max_analyzer");
                                                            }
                                                        });
                                                        put("smart",new HashMap<String, String>(){
                                                            {
                                                                put("type","text");
                                                                put("analyzer","smart_analyzer");
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                        put("historyCaseNumber", new HashMap<String, String>() {
                                            {
                                                put("type", "keyword");
                                            }
                                        });
                                        put("historyId", new HashMap<String, String>() {
                                            {
                                                put("type", "keyword");
                                            }
                                        });
                                        put("id", new HashMap<String, String>() {
                                            {
                                                put("type", "keyword");
                                            }
                                        });
                                        put("insertTime",new HashMap<String,Object>(){
                                            {
                                                put("type","date");
                                                put("format","yyyy-MM-dd HH:mm:ss");
                                            }
                                        });
                                        put("judge",new HashMap<String, Object>(){
                                            {
                                                put("type","nested");
                                                put("properties",new HashMap<String, Object>(){
                                                    {
                                                        put("name", new HashMap<String, String>() {
                                                            {
                                                                put("type", "keyword");
                                                            }
                                                        });
                                                        put("type", new HashMap<String, String>() {
                                                            {
                                                                put("type", "keyword");
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                        put("judgeGist", new HashMap<String, String>() {
                                            {
                                                put("type", "keyword");
                                            }
                                        });
                                        put("judgementCategory", new HashMap<String, String>() {
                                            {
                                                put("type", "keyword");
                                            }
                                        });
                                        put("judgementResult",new HashMap<String, Object>(){
                                            {
                                                put("properties",new HashMap<String, Object>(){
                                                    {
                                                        put("desc",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("detail",new HashMap<String, Object>(){
                                                            {
                                                                put("properties",new HashMap<String, String>(){
                                                                    {
                                                                        put("剥夺政治权利终身","keyword");
                                                                        put("当事人","keyword");
                                                                        put("徒刑","keyword");
                                                                        put("拘役","keyword");
                                                                        put("死刑","keyword");
                                                                        put("没收财产","keyword");
                                                                        put("管制","keyword");
                                                                        put("缓刑","keyword");
                                                                        put("罚金","keyword");
                                                                        put("罪名","keyword");
                                                                        put("罪名是否成立","keyword");
                                                                    }
                                                                });
                                                            }
                                                        });
                                                        put("全部支持","keyword");
                                                        put("全部改判","keyword");
                                                        put("全部驳回","keyword");
                                                        put("发回重审","keyword");
                                                        put("改判","keyword");
                                                        put("维持原判","keyword");
                                                        put("部分支持","keyword");
                                                        put("部分改判","keyword");
                                                    }
                                                });
                                            }
                                        });
                                        put("judgementTime",new HashMap<String, Object>(){
                                            {
                                                put("type","date");
                                                put("format","yyyy-MM-dd");
                                            }
                                        });
                                        put("lastUpdateTime",new HashMap<String, Object>(){
                                            {
                                                put("type","date");
                                            }
                                        });
                                        put("lawInfo",new HashMap<String, Object>(){
                                            {
                                                put("type","nested");
                                                put("properties",new HashMap<String, Object>(){
                                                    {
                                                        put("clauses",new HashMap<String, String>(){
                                                            {
                                                                put("type","integer");
                                                            }
                                                        });
                                                        put("detail",new HashMap<String, String>(){
                                                            {
                                                                put("type","text");
                                                            }
                                                        });
                                                        put("lawName",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("law_category",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                        put("laws",new HashMap<String, String>(){
                                            {
                                                put("type","keyword");
                                            }
                                        });
                                        put("lawyerInfo",new HashMap<String, Object>(){
                                            {
                                                put("type","nested");
                                                put("properties",new HashMap<String, Object>(){
                                                    {
                                                        put("lawFirmName",new HashMap<String, Object>(){
                                                            {
                                                                put("type","text");
                                                                put("fields",new HashMap<String, Object>(){
                                                                    {
                                                                        put("keyword",new HashMap<String, Object>(){
                                                                            {
                                                                                put("type","keyword");
                                                                                put("ignore_above",50);
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                        put("lawyerName",new HashMap<String, Object>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("lawyerRepresent",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                        put("litigationParticipant",new HashMap<String, Object>(){
                                            {
                                                put("properties",new HashMap<String, Object>(){
                                                    {
                                                        put("address",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("anonymousName",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("birth",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("census_register",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("city",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("district",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("education",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("isLawyer",new HashMap<String, String>(){
                                                            {
                                                                put("type","boolean");
                                                            }
                                                        });
                                                        put("lawAgentType",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("lawFirmName",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("lawyerRepresent",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("litigationParticipantType",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("name",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("nation",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("province",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("sex",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("street",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("type",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                        put("litigationText",new HashMap<String, String>(){
                                            {
                                                put("type","keyword");
                                            }
                                        });
                                        put("paraTags",new HashMap<String, String>(){
                                            {
                                                put("type","keyword");
                                            }
                                        });
                                        put("paras",new HashMap<String, Object>(){
                                            {
                                               put("type","nested");
                                               put("properties",new HashMap<String, Object>(){
                                                   {
                                                       put("content",new HashMap<String, Object>(){
                                                           {
                                                               put("type","text");
                                                               put("fields",new HashMap<String, Object>(){
                                                                   {
                                                                       put("max",new HashMap<String, Object>(){
                                                                           {
                                                                               put("type","text");
                                                                               put("analyzer","max_analyzer");
                                                                           }
                                                                       });
                                                                       put("smart",new HashMap<String, Object>(){
                                                                           {
                                                                               put("type","text");
                                                                               put("analyzer","smart_analyzer");
                                                                           }
                                                                       });
                                                                   }
                                                               });
                                                           }
                                                       });
                                                       put("tag",new HashMap<String, Object>(){
                                                           {
                                                               put("type","keyword");
                                                           }
                                                       });
                                                   }
                                               });
                                            }
                                        });
                                        put("party",new HashMap<String, Object>(){
                                            {
                                                put("type","nested");
                                                put("properties",new HashMap<String, Object>(){
                                                    {
                                                        put("address",new HashMap<String, Object>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("age",new HashMap<String, Object>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("anonymousName",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("birth",new HashMap<String, String>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("career",new HashMap<String, Object>(){
                                                            {
                                                                put("type","text");
                                                                put("fields",new HashMap<String, Object>(){
                                                                    {
                                                                        put("keyword",new HashMap<String, Object>(){
                                                                            {
                                                                                put("type","keyword");
                                                                                put("ignore_above",256);
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                        put("census_register",new HashMap<String, Object>(){
                                                            {
                                                                put("type","text");
                                                                put("fields",new HashMap<String, Object>(){
                                                                    {
                                                                        put("keyword",new HashMap<String, Object>(){
                                                                            {
                                                                                put("type","keyword");
                                                                                put("ignore_above",256);
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                        put("city",new HashMap<String, Object>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("country",new HashMap<String, Object>(){
                                                            {
                                                                put("type","text");
                                                                put("fields",new HashMap<String, Object>(){
                                                                    {
                                                                        put("keyword",new HashMap<String, Object>(){
                                                                            {
                                                                                put("type","keyword");
                                                                                put("ignore_above",256);
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                        put("defender",new HashMap<String, Object>(){
                                                            {
                                                                put("type","nested");
                                                                put("properties",new HashMap<String, Object>(){
                                                                    {
                                                                        put("lawFirmName",new HashMap<String, Object>(){
                                                                            {
                                                                                put("type","keyword");
                                                                            }
                                                                        });
                                                                        put("name",new HashMap<String, Object>(){
                                                                            {
                                                                                put("type","keyword");
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                        put("district",new HashMap<String, Object>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("education",new HashMap<String, Object>(){
                                                            {
                                                                put("type","text");
                                                                put("fields",new HashMap<String, Object>(){
                                                                    {
                                                                        put("keyword",new HashMap<String, Object>(){
                                                                            {
                                                                                put("type","keyword");
                                                                                put("ignore_above",256);
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                        put("guardian",new HashMap<String, Object>(){
                                                            {
                                                                put("type","text");
                                                                put("fields",new HashMap<String, Object>(){
                                                                    {
                                                                        put("keyword",new HashMap<String, Object>(){
                                                                            {
                                                                                put("type","keyword");
                                                                                put("ignore_above",256);
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                        put("industry",new HashMap<String, Object>(){
                                                            {
                                                                put("type","text");
                                                                put("fields",new HashMap<String, Object>(){
                                                                    {
                                                                        put("keyword",new HashMap<String, Object>(){
                                                                            {
                                                                                put("type","keyword");
                                                                                put("ignore_above",256);
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                        put("lawAgentType",new HashMap<String, Object>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("litigationParticipantType",new HashMap<String, Object>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("name",new HashMap<String, Object>(){
                                                            {
                                                                put("type","text");
                                                            }
                                                        });
                                                        put("nation",new HashMap<String, Object>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("province",new HashMap<String, Object>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("sex",new HashMap<String, Object>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("street",new HashMap<String, Object>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                        put("type",new HashMap<String, Object>(){
                                                            {
                                                                put("type","keyword");
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                        put("previousTrialCaseNumber",new HashMap<String, Object>(){
                                            {
                                                put("type","keyword");
                                            }
                                        });
                                        put("province",new HashMap<String, Object>(){
                                            {
                                                put("type","keyword");
                                            }
                                        });
                                        put("rejectReason",new HashMap<String, Object>(){
                                            {
                                                put("type","text");
                                            }
                                        });
                                        put("repeated",new HashMap<String, Object>(){
                                            {
                                                put("type","keyword");
                                            }
                                        });
                                        put("sensitiveInfo",new HashMap<String, Object>(){
                                            {
                                                put("type","text");
                                            }
                                        });
                                        put("subTrialRound",new HashMap<String, Object>(){
                                            {
                                                put("type","keyword");
                                            }
                                        });
                                        put("title",new HashMap<String, Object>(){
                                            {
                                                put("type","text");
                                                put("fields",new HashMap<String, Object>(){
                                                    {
                                                        put("max",new HashMap<String, String>(){
                                                            {
                                                                put("type","text");
                                                                put("analyzer","max_analyzer");
                                                            }
                                                        });
                                                        put("smart",new HashMap<String, String>(){
                                                            {
                                                                put("type","text");
                                                                put("analyzer","smart_analyzer");
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                        put("topCause",new HashMap<String, Object>(){
                                            {
                                                put("type","keyword");
                                            }
                                        });
                                        put("trailForm",new HashMap<String, Object>(){
                                            {
                                                put("type","keyword");
                                            }
                                        });
                                        put("trialProcedure",new HashMap<String, Object>(){
                                            {
                                                put("type","keyword");
                                            }
                                        });
                                        put("trialRound",new HashMap<String, Object>(){
                                            {
                                                put("type","keyword");
                                            }
                                        });
                                        put("uniqueId",new HashMap<String, Object>(){
                                            {
                                                put("type","keyword");
                                            }
                                        });
                                        put("unique_id",new HashMap<String, Object>(){
                                            {
                                                put("type","keyword");
                                            }
                                        });
                                        put("updateTime",new HashMap<String, Object>(){
                                            {
                                                put("type","date");
                                                put("format","yyyy-MM-dd HH:mm:ss");
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        };
        createIndexOrPutMappings(indexName, properties);
        log.info("完成埋点数据索引初始化.");
    }
}
