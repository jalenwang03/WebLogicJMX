package com.eproe.shepherd.wls_health_check.tp_life.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.eproe.shepherd.wls_health_check.tplife.utils.ShepherdProperties;

import net.sf.json.JSONObject;

public class ShepherdConnector {
	public boolean  NEEDAUTH=true;
	public Map<String,String> cookieMap = new HashMap<String, String>(64);
	public String c;
	public RequestConfig requestConfig;
	public RequestConfig defaultRequestConfig;
	public CloseableHttpClient httpclient;
	public HttpPost p ;
	public boolean isConnected;
	HttpParams httpparameters;
    public boolean inited=false;
    public static ShepherdConnector spdConnection;
//    public static void main(String args[]){
//    	ShepherdConnector.connection("115.159.193.51", "8000", "/auth/signin","/api/v1/perfdata","admin","admin");
////    	ShepherdConnector.connection("115.159.193.51", "8000", "/api/v1/perfdata");
//    }
    public static ShepherdConnector getShepherdConnection(){
    	return spdConnection;
    }
    public static int connection(){
    	ShepherdConnector.spdConnection=new ShepherdConnector();
    	if(ShepherdProperties.AuthUrl!=null)
    		ShepherdConnector.spdConnection.NEEDAUTH=true;
    	else
    		ShepherdConnector.spdConnection.NEEDAUTH=false;
    	return ShepherdConnector.spdConnection.initConnection();
    }

    public void connect(String sendurl){
    	ShepherdConnector.spdConnection.requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
		 ShepherdConnector.spdConnection.httpclient = HttpClients.custom()
				    .setDefaultRequestConfig(defaultRequestConfig)
				    .build();
		 ShepherdConnector.spdConnection.p = new HttpPost(sendurl);
		 ShepherdConnector.spdConnection.isConnected=true;
	}
    
    public int initConnection() {
    	if(!ShepherdConnector.spdConnection.NEEDAUTH){
    		return 1;
    	}
//        String url="http://"+ShepherdProperties.IpAddress
//        		+":"+ShepherdProperties.Port+
//        		ShepherdProperties.AuthUrl;
//        HttpPost httpPost = new HttpPost(url);
        JSONObject jsonParam = new JSONObject(); 
        jsonParam.put("username", ShepherdProperties.Username);
        jsonParam.put("password",  ShepherdProperties.Password);
        ShepherdConnector.spdConnection.connect("http://"+ShepherdProperties.IpAddress+
        		":"+ShepherdProperties.Port+ShepherdProperties.AuthUrl);
        StringEntity entity = new StringEntity(jsonParam.toString(), Consts.UTF_8);
        entity.setContentType("application/json");
		p.setEntity(entity);
		try {
			HttpResponse r =httpclient.execute(p);

			if(r.getStatusLine().getStatusCode()==200){
//           	 //得到post请求返回的cookie信息
               this.c=this.setCookie(r);
               this.inited=true;
               return 1;
           }else{
           	System.out.println(r.getStatusLine());
           	return 0;
           }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

    public static String setCookie(HttpResponse httpResponse)
    {
        Header headers[] = httpResponse.getHeaders("Set-Cookie");
        if (headers == null || headers.length==0)
        {
            return null;
        }
        String cookie = "";
        for (int i = 0; i < headers.length; i++) {
            cookie += headers[i].getValue();
            if(i != headers.length-1)
            {
                cookie += ";";
            }
        }

        return cookie.split(";")[0];
    }
    
}
