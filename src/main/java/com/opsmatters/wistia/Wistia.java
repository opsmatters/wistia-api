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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Client to execute operations on Wistia.
 *
 * @author Gerald Curley (opsmatters)
 */

public class Wistia
{
    private static final String WISTIA_SERVER = "https://api.wistia.com";
	private static final String API_VERSION = "/v1";
    private String token;
    private URL proxy;

    /**
     * Constructor that takes an API token.
     */
    public Wistia(String token)
    {
        this.token = token;
    }

    public WistiaResponse get(String endpoint) throws IOException
    {
        return apiRequest(endpoint, HttpGet.METHOD_NAME, null, null);
    }

    public WistiaResponse get(String endpoint, Map<String, String> params) throws IOException
    {
        return apiRequest(endpoint, HttpGet.METHOD_NAME, params, null);
    }

    public WistiaResponse put(String endpoint) throws IOException
    {
        return apiRequest(endpoint, HttpPut.METHOD_NAME, null, null);
    }

    public WistiaResponse put(String endpoint, Map<String, String> params) throws IOException
    {
        return apiRequest(endpoint, HttpPut.METHOD_NAME, params, null);
    }

    public WistiaResponse delete(String endpoint) throws IOException
    {
        return apiRequest(endpoint, HttpDelete.METHOD_NAME, null, null);
    }

    public WistiaResponse delete(String endpoint, Map<String, String> params) throws IOException
    {
        return apiRequest(endpoint, HttpDelete.METHOD_NAME, params, null);
    }

    public WistiaResponse patch(String endpoint) throws IOException
    {
        return apiRequest(endpoint, HttpPatch.METHOD_NAME, null, null);
    }

    public WistiaResponse patch(String endpoint, Map<String, String> params) throws IOException
    {
        return apiRequest(endpoint, HttpPatch.METHOD_NAME, params, null);
    }

    public URL getProxy()
    {
        return proxy;
    }

    public void setProxy(URL proxy)
    {
        this.proxy = proxy;
    }

    /**
     * Execute the API request.
     */
    protected WistiaResponse apiRequest(String endpoint, String methodName, Map<String, String> params, InputStream inputStream)
        throws IOException
    {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpRequestBase request = null;
        String url = null;
        if(endpoint.startsWith("http"))
        {
            url = endpoint;
        }
        else
        {
            StringBuilder str = new StringBuilder(WISTIA_SERVER).append(API_VERSION).append(endpoint);
            if(str.indexOf("?") == -1)
                str.append("?");
            str.append("api_password=").append(token);
            url = str.toString();
        }

        if(methodName.equals(HttpGet.METHOD_NAME))
        {
            request = new HttpGet(url);
        }
        else if(methodName.equals(HttpPost.METHOD_NAME))
        {
            request = new HttpPost(url);
        }
        else if(methodName.equals(HttpPut.METHOD_NAME))
        {
            request = new HttpPut(url);
        }
        else if(methodName.equals(HttpDelete.METHOD_NAME))
        {
            request = new HttpDelete(url);
        }
        else if (methodName.equals(HttpPatch.METHOD_NAME))
        {
            request = new HttpPatch(url);
        }

        request.addHeader("Accept", "application/json");

        HttpEntity entity = null;
        if(params != null)
        {
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            for(String key : params.keySet())
                postParameters.add(new BasicNameValuePair(key, params.get(key)));
            entity = new UrlEncodedFormEntity(postParameters);
        }
        else if(inputStream != null)
        {
            entity = new InputStreamEntity(inputStream, ContentType.MULTIPART_FORM_DATA);
        }

        if(entity != null)
        {
            if(request instanceof HttpPost)
                ((HttpPost)request).setEntity(entity);
            else if(request instanceof HttpPatch)
                ((HttpPatch)request).setEntity(entity);
            else if(request instanceof HttpPut)
                ((HttpPut)request).setEntity(entity);
        }
        
        if(proxy != null)
        {
            HttpHost httpProxy = new HttpHost(proxy.getHost(), proxy.getPort(), proxy.getProtocol());
            RequestConfig config = RequestConfig.custom()
                .setProxy(httpProxy)
                .build();
            request.setConfig(config);
        }
        
        CloseableHttpResponse response = client.execute(request);
        String responseAsString = null;
        int statusCode = response.getStatusLine().getStatusCode();
        if(methodName.equals(HttpPut.METHOD_NAME) || methodName.equals(HttpDelete.METHOD_NAME))
        {
            JSONObject out = new JSONObject();
            for(Header header : response.getAllHeaders())
                out.put(header.getName(), header.getValue());
            responseAsString = out.toString();
        }
        else if (statusCode != 204)
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            responseAsString = out.toString("UTF-8");
            out.close();
        }

        JSONObject json = null;
        JSONArray jsonArray = null;
        JSONObject headers = null;
        try
        {
            if(responseAsString.startsWith("["))
                jsonArray = new JSONArray(responseAsString);
            else
                json = new JSONObject(responseAsString);

            headers = new JSONObject();
            for(Header header : response.getAllHeaders())
                headers.put(header.getName(), header.getValue());
        }
        catch(Exception e)
        {
            json = new JSONObject();
            headers = new JSONObject();
        }

        WistiaResponse jsonResponse = new WistiaResponse(json, jsonArray, headers, statusCode);
        response.close();
        client.close();

        return jsonResponse;
    }
}
