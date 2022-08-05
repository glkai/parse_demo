package com.example.parse_demo.utils;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.impl.piccolo.io.FileFormatException;
import org.w3c.dom.ls.LSInput;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author GaoLiuKai
 * @date 2022/8/3 17:29
 */
public class myFileUtils {

    // 批量解压缩
    public static void UnZipFiles(List<File> ZipFiles) throws FileFormatException {
        for(File file : ZipFiles){
            if(file.getPath().endsWith(".zip")){
                myFileUtils.unzip(file.getPath());
            }else {
                throw new FileFormatException("待解压缩文件类型错误");
            }
        }
    }

    // 读取 docx/doc 文件的内容，集合中的每一项都是一个文件内容
    public static List<String> readAllDocxFiles(List<File> DocxFiles) throws FileFormatException {
        List<String> res = new ArrayList<>();
        for(File file : DocxFiles){
            if(file.getPath().endsWith(".docx") || file.getPath().endsWith(".doc")){
                res.add(myFileUtils.readWord(file.getPath()));
            }else {
                throw new FileFormatException("待读取文件类型错误");
            }
        }
        return res;
    }

    /**
     *
     * @param fileInput    文件目录的路径
     * @param allZipFileList  所有Zip文件的集合
     */
    // 获取文件目录下的所有压缩文件路径
    public static void getAllZipFile(File fileInput, List<File> allZipFileList) {
        // 获取文件列表
        File[] fileList = fileInput.listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                // 递归处理文件夹
                // 如果不想统计子文件夹则可以将下一行注释掉
                getAllZipFile(file, allZipFileList);
            } else {
                // 如果是文件则将其加入到文件数组中
                if(file.getPath().endsWith(".zip")){
                    allZipFileList.add(file);
                }
            }
        }
    }

    /**
     *
     * @param fileInput    文件目录的路径
     * @param allDocxFileList  所有 docx 文件的集合
     */
    // 获取文件目录下的所有压缩文件路径
    public static void getAllDocxFile(File fileInput, List<File> allDocxFileList) {
        // 获取文件列表
        File[] fileList = fileInput.listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                // 递归处理文件夹
                // 如果不想统计子文件夹则可以将下一行注释掉
                getAllDocxFile(file, allDocxFileList);
            } else {
                // 如果是文件则将其加入到文件数组中
                if(file.getPath().endsWith(".docx")){
                    allDocxFileList.add(file);
                }
            }
        }
    }

    // 读取docx文件
    public static String readWord(String filePath)  {

        String buffer;
        try {
            if (filePath.endsWith(".doc")) {
                InputStream is = new FileInputStream(filePath);
                WordExtractor ex = new WordExtractor(is);
                buffer = ex.getText();
                ex.close();

            } else if (filePath.endsWith(".docx")) {
                OPCPackage opcPackage = POIXMLDocument.openPackage(filePath);
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                buffer = extractor.getText();
                extractor.close();
            } else {
                return null;
            }

            return buffer;
        } catch (Exception e) {
            System.out.print("error---->" + filePath);
            e.printStackTrace();
            return null;
        }
    }

    // 对单个.zip文件进行解压
    private static void unzip(String zipPath) {

        File zipFile = new File(zipPath);
        // 1.创建解压缩目录
        // 获取zip文件的名称
        String zipFileName = zipFile.getName();

        // 根据zip文件名称，提取压缩文件目录
        String targetDirName = zipFileName.substring(0, zipFileName.indexOf("."));

        // 创建解压缩目录
        File targetDir = new File(zipFile.getParent() + "\\" + targetDirName);

        if (!targetDir.exists()) {
            targetDir.mkdir(); // 创建目录
        }

        // 2.解析读取zip文件
        try (ZipInputStream in = new ZipInputStream(new FileInputStream(zipFile), Charset.forName("gbk"))) {
            // 遍历zip文件中的每个子文件
            ZipEntry zipEntry = null;
            while ((zipEntry = in.getNextEntry()) != null) {
                // 获取zip压缩包中的子文件名称
                String zipEntryFileName = zipEntry.getName();

                // 创建该文件的输出流
                String zipFilePath = targetDir.getPath() + "\\" + zipEntryFileName;

                // 输出流定义在try()块，结束自动清空缓冲区并关闭
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zipFilePath))) {

                    // 读取该子文件的字节内容
                    byte[] buff = new byte[1024];
                    int len = -1;
                    while ((len = in.read(buff)) != -1) {
                        bos.write(buff, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
