package com.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author yuanmengfan
 * @date 2021/11/18 4:38 下午
 * @description
 */
@Controller
public class PoiController {
    @RequestMapping("/test")
    @ResponseBody
    public Object test(){
        JSONObject bbb = new JSONObject();
        JSONArray  result = new JSONArray();
        ExcelReader reader = ExcelUtil.getReader("/Users/yuanmengfan/Documents/GitHub/InstanceLibrary/src/main/resources/副本行政办公用房全校台账汇总表20210429（最新版）规范名称20211108.xlsx",0);
        List<Map<String,Object>> readAll = reader.readAll();


        //上一个房间编号
        String fjbh = "";
        int lastMap = 0;
        for (int i = 0; i < readAll.size(); i++) {
            Map<String,Object> stringObjectMap = readAll.get(i);

            JSONObject jsonObject = new JSONObject();
            JSONArray ryxxs = new JSONArray();
            JSONObject ryxx = null;
            String xm = stringObjectMap.get("xm").toString();
            if(!"".equals(xm)){
                ryxx = new JSONObject();
                ryxx.put("xm", xm);
                ryxx.put("xz", stringObjectMap.get("xz"));
                ryxx.put("jb", stringObjectMap.get("jb"));
            }
            String zFXXFJBH = stringObjectMap.get("zFXXFJBH").toString();
            if(fjbh.equals(zFXXFJBH)){
                //拿到上一次下标的map并在他的ryxx下面加上ryxx
                result.getJSONObject(lastMap-1).getJSONArray("ryxxs").add(ryxx);
            }else{
                lastMap++;
                if(ryxx!=null){
                    ryxxs.add(ryxx);
                }
                jsonObject.put("zFXXLC", stringObjectMap.get("zFXXLC"));
                jsonObject.put("zFXXFJBH", zFXXFJBH);
                jsonObject.put("zFXXBZ", stringObjectMap.get("zFXXBZ"));
                jsonObject.put("zFXXFJSYMJ", stringObjectMap.get("zFXXFJSYMJ"));
                jsonObject.put("yFGPMC", stringObjectMap.get("yFGPMC"));
                jsonObject.put("zFXXYFLX", stringObjectMap.get("zFXXYFLX"));
                jsonObject.put("ryxxs",ryxxs);
                result.add(jsonObject);
            }
            fjbh = zFXXFJBH;
        }
        bbb.put("result", result);
        return readAll;
    }
}
