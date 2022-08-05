package com.example.parse_demo.utils;

/**
 * @author GaoLiuKai
 * @date 2022/8/3 14:26
 */

import org.apache.xmlbeans.impl.piccolo.io.FileFormatException;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class test {

//    @Value("${directory.path}")
//    private static String filePath;

    public static void main(String[] args)  {


        File dir = new File("C:\\Users\\17566\\Desktop\\徐州市裁判文书爬取");

        List<File> allFileList = new ArrayList<>();


        // 判断文件夹是否存在
        if (!dir.exists()) {
            System.out.println("目录不存在");
            return;
        }

        getAllFile(dir, allFileList);

        for(File file : allFileList){
            System.out.println(file.getPath());
        }

        System.out.println("该文件夹下共有" + allFileList.size() + "个文件");
    }

    public static void getAllFile(File fileInput, List<File> allFileList) {
        // 获取文件列表
        File[] fileList = fileInput.listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                // 递归处理文件夹
                // 如果不想统计子文件夹则可以将下一行注释掉
                getAllFile(file, allFileList);
            } else {
                // 如果是文件则将其加入到文件数组中
                allFileList.add(file);
            }
        }
    }
}

