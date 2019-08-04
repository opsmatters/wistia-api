/*
 * Copyright 2019 Gerald Curley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opsmatters.wistia;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Generic response from Wistia.
 *
 * @author Gerald Curley (opsmatters)
 */
public class WistiaResponse
{
    private JSONObject json;
    private JSONArray jsonArray;
    private JSONObject headers;
    private int statusCode;

    public WistiaResponse(JSONObject json, JSONArray jsonArray, JSONObject headers, int statusCode)
    {
        this.json = json;
        this.jsonArray = jsonArray;
        this.headers = headers;
        this.statusCode = statusCode;
    }

    public JSONObject getJson()
    {
        return json;
    }

    public JSONArray getJsonArray()
    {
        return jsonArray;
    }

    public JSONObject getHeaders()
    {
        return headers;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public String toString()
    {
        return new StringBuilder("Status Code: \n").append(getStatusCode())
            .append("\nJSON: \n").append(getJson().toString(2))
            .append("\nHeaders: \n").append(getHeaders().toString(2))
            .toString();
    }
}
