package com.opsmatters.wistia;

import java.io.File;
import org.json.JSONObject;
import org.json.JSONArray;

public class WistiaTest
{
    public static void main(String[] args) throws Exception
    {
        Wistia wistia = new Wistia("<API-token>"); 

        // List videos
        {
            WistiaResponse result = wistia.get("/medias.json");
            JSONArray items = result.getJsonArray();
            if(items != null && items.length() > 0)
            {
                for(int i = 0; i < items.length(); i++)
                {
                    JSONObject item = items.getJSONObject(i);
                    System.out.println(item.optString("name"));
                }
            }
        }
    
        // Get video details
        String hashedId = "x123456y";
        WistiaResponse result = wistia.get(String.format("/medias/%s.json", hashedId));
        JSONObject item = result.getJson();
        System.out.println(item.optString("name"));
    }
}