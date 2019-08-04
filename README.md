### Java Wistia API v1.
To use this api you’ll first need to obtain an API key from Wistia:

https://wistia.com/support/developers/data-api

The generated token is all you need to use the Java Wistia API.

```java

package com.opsmatters.wistia;

import java.io.File;

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
        System.out.println(result.optString("name"));
  }
}


```

The class WistiaResponse provides a response code and JSON response, see the Wistia API documentation in case of errors.

### Use with Maven

```xml

<dependency>
  <groupId>com.opsmatters</groupId>
  <artifactId>wistia-api</artifactId>
  <version>1.0.0</version>
</dependency>

```