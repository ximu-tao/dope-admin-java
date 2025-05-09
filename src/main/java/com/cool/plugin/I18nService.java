//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.cool.plugin;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

public class I18nService {
    


    public Object invokePlugin(String... params) {
        System.out.println("请调用插件具体的方法");
        return null;
    }

    public JSONObject invokeTranslate(Map<String, String> map, String language) {
        if (map.isEmpty()) {
            return null;
        } else if (ObjUtil.equal(language, "zh-cn")) {
            return JSONUtil.parseObj(map);
        } else {
            try {
                Map<String, Object> data = new HashMap();
                data.put("label", "i18n-node");
                data.put("params", Map.of("text", JSONUtil.toJsonStr(map), "language", language));
                data.put("stream", false);
                String res = HttpUtil.post("https://service.cool-js.com/api/open/flow/run/invoke", JSONUtil.toJsonStr(data));
                JSONObject jsonObject = JSONUtil.parseObj(res);
                return jsonObject.getJSONObject("data").getJSONObject("result").getJSONObject("data");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
