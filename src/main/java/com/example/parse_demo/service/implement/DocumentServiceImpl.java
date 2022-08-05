package com.example.parse_demo.service.implement;

import com.alibaba.fastjson.JSONObject;
import com.example.parse_demo.service.esServices.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentServiceImpl implements DocumentService {

    private final RestHighLevelClient restHighLevelClient;

    /**
     * 批量创建文档
     */
    @Override
    public BulkResponse bulkAddDocument(List<JSONObject> documents, String index) throws IOException {
        BulkRequest request = new BulkRequest();
        for (JSONObject document : documents) {
            request.add(new IndexRequest(index, "_doc").source(document));
        }
        return restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
    }


}