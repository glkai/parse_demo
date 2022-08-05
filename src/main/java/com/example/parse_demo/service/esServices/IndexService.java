package com.example.parse_demo.service.esServices;

import org.elasticsearch.client.indices.CreateIndexResponse;

import java.io.IOException;
import java.util.Map;

public interface IndexService {
    boolean existsIndex(String indexName);

    CreateIndexResponse createIndex(String indexName, String source) throws IOException;

    void createIndex(String indexName, Map<String, Object> properties) throws IOException;

    void putMapping(String indexName, Map<String, Object> properties) throws IOException;

    void createIndexOrPutMappings(String indexName, Map<String, Object> properties) throws IOException;

    void initIndex(String indexName) throws IOException;
}
