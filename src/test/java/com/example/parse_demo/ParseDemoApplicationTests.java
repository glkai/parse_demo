package com.example.parse_demo;


import com.example.parse_demo.service.esServices.DocumentService;
import com.example.parse_demo.service.esServices.IndexService;
import com.example.parse_demo.utils.myFileUtils;
import com.example.parse_demo.utils.PostOtherController;

import com.alibaba.fastjson.JSONObject;
import org.apache.xmlbeans.impl.piccolo.io.FileFormatException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.ls.LSInput;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class ParseDemoApplicationTests {

    @Autowired
    private IndexService indexService;
    @Autowired
    private DocumentService service;
    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() throws IOException {
        String json = myFileUtils.readWord("E:\\docx\\徐州市裁判文书爬取\\data\\租赁合同纠纷\\租赁合同纠纷500份\\1-4931睢宁金港家居广场有限公司与王少舫房屋租赁合同纠纷一审民事案84429994.docx");

        String url = "https://shilv-apigateway.aegis-info.com/api/parsers/DocumentParser";
        HashMap<String, Object> map = new HashMap<>();
        map.put("text",json);

        JSONObject post = PostOtherController.post(url, map);

        BulkRequest bulkRequest = new BulkRequest();
        IndexRequest request = new IndexRequest("case_parse_temp","_doc");
        request.source(post);
        bulkRequest.add(request);
        client.bulk(bulkRequest, RequestOptions.DEFAULT);


        System.out.println(post);
    }

    @Test
    void test() throws IOException {
        List<File> docFiles = new ArrayList<>();
        List<File> zipFiles = new ArrayList<>();
        myFileUtils.getAllDocxFile(new File("C:\\Users\\17566\\Desktop\\徐州市裁判文书爬取"),docFiles);

        myFileUtils.getAllZipFile(new File("C:\\Users\\17566\\Desktop\\徐州市裁判文书爬取"),zipFiles);

        List<String> list = myFileUtils.readAllDocxFiles(docFiles);

        List<JSONObject> objs = new ArrayList<>();
        for(String doc : list.subList(1,1)){
            JSONObject text = PostOtherController.post("https://shilv-apigateway.aegis-info.com/api/parsers/DocumentParser", new HashMap<>() {
                {
                    put("text", doc);
                }
            });
            objs.add(text);
        }
        for(JSONObject json : objs){
            System.out.println(json);
        }

    }

    @Test
    public void testES() throws IOException {
        indexService.initIndex("data");
        System.out.println(indexService.existsIndex("case_parse_temp"));
    }
}
