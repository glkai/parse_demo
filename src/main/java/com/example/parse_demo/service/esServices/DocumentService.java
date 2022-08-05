package com.example.parse_demo.service.esServices;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.bulk.BulkResponse;

import java.io.IOException;
import java.util.List;

public interface DocumentService {

    BulkResponse bulkAddDocument(List<JSONObject> documents, String index) throws IOException;

}
