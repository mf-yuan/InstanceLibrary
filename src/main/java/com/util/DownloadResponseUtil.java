package com.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author yuanmengfan
 * @date 2023/12/27 23:50
 * @description
 */
public class DownloadResponseUtil {

    public static void download(HttpServletRequest request, HttpServletResponse response,String saveFile,byte[] bytes){
        OutputStream fOut = null;
        try{
            String userAgent = request.getHeader("User-Agent");
            if(userAgent != null){
                if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                    // IE
                    saveFile = URLEncoder.encode(saveFile, "UTF-8").replace("+", "%20");;
                } else if (userAgent.contains("Edge")) {
                    // Edge浏览器
                    saveFile = URLEncoder.encode(saveFile, "UTF-8");
                } else {
                    // 火狐，Chrome,Safari
                    saveFile = new String(saveFile.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
                }
            }
            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;charset=UTF-8;filename=" + saveFile);
            fOut = response.getOutputStream();
            fOut.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
