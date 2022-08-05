package com.example.parse_demo.utils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.*;
import java.util.List;

/**
 * @author GaoLiuKai
 * @date 2022/8/3 14:16
 */
public class myDocxReader {

    public static void main(String[] args) {
        System.out.println(myDocxReader.readDocxFile("E:\\docx\\徐州市裁判文书爬取\\data\\变更抚养关系纠纷\\变更抚养关系纠纷142份\\20-常某、贾某甲与贾某乙变更抚养关系纠纷一审民事案20839135.docx"));
    }

    public static String readDocxFile(String filePathName) {
        StringBuilder result = new StringBuilder();
        try {
            File file = new File(filePathName);
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());

            XWPFDocument document = new XWPFDocument(fis);

            List<XWPFParagraph> paragraphs = document.getParagraphs();


            for (XWPFParagraph para : paragraphs) {
                result.append(para.getText());
                result.append("\n");
            }
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
