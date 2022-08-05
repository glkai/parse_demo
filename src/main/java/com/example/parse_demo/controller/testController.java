package com.example.parse_demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.parse_demo.utils.PostOtherController;
import com.example.parse_demo.utils.myFileUtils;
import org.apache.xmlbeans.impl.piccolo.io.FileFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.ls.LSInput;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author GaoLiuKai
 * @date 2022/8/3 14:17
 */
@RestController
public class testController {

    @Value("${elasticsearch.indexName}")
    private String indexName;

    @Value("${controller.url}")
    private String controllerUrl;

    @Value("${directory.path}")
    private  String filePath ;

    @RequestMapping("/read/test")
    public String testReadController() throws FileFormatException {
        List<File> docFiles = new ArrayList<>();
        List<File> zipFiles = new ArrayList<>();
        List<JSONObject> objs = new ArrayList<>();
//        myFileUtils.getAllZipFile(new File(filePath),zipFiles);
//        myFileUtils.UnZipFiles(zipFiles);
        myFileUtils.getAllDocxFile(new File(filePath),docFiles);
        List<String> docxContentList = myFileUtils.readAllDocxFiles(docFiles);
        for(String docText : docxContentList.subList(1,5)){
            JSONObject text = PostOtherController.post(controllerUrl, new HashMap<>() {
                {
                    put("text", docText);
                }
            });
            objs.add(text);
        }
        System.out.println(objs.get(5000));
        return "1";
    }
}
