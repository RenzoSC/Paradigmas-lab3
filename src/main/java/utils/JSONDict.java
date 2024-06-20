package utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONDict implements Serializable {
    private String DictPath = "";
    private List<Map<String, Object>> dictList = new ArrayList<>();

    public JSONDict(String filePath) {
        DictPath = filePath;
        try {
            JSONArray JDict = new JSONArray(new String(Files.readAllBytes(Paths.get(filePath))));
            for (int i = 0; i < JDict.length(); i++) {
                JSONObject jsonObj = JDict.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                map.put("Topics", jsonArrayToList(jsonObj.getJSONArray("Topics")));
                map.put("keywords", jsonArrayToList(jsonObj.getJSONArray("keywords")));
                map.put("Category", jsonObj.getString("Category"));
                map.put("label", jsonObj.getString("label"));
                dictList.add(map);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public String getPath() {
        return DictPath;
    }

    public Integer getLength() {
        return dictList.size();
    }

    @SuppressWarnings("unchecked")
    public List<String> getTopic(Integer index) {
        return (List<String>) dictList.get(index).get("Topics");
    }

    @SuppressWarnings("unchecked")
    public List<String> getKeywords(Integer index) {
        return (List<String>) dictList.get(index).get("keywords");
    }

    public String getCategory(Integer index) {
        return (String) dictList.get(index).get("Category");
    }

    public String getLabel(Integer index) {
        return (String) dictList.get(index).get("label");
    }

    private List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }
}