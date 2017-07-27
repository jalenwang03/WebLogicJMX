package com.eproe.shepherd.wls_health_check.tp_life.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.eproe.shepherd.wls_health_check.tp_life.CI.CI;
import com.eproe.shepherd.wls_health_check.tplife.utils.ShepherdProperties;

public class Getter {
	public static ShepherdConnector connector;
	public static ArrayList<CI> ciList=null;
	
	public static String getData(String subtype) {
		connector=ShepherdConnector.spdConnection;
		String str = null;
		BufferedReader in = null;  
        HttpClient client = new DefaultHttpClient(connector.httpparameters);  
        HttpGet request = new HttpGet();  
        request.setConfig(connector.defaultRequestConfig);
        try {
			request.setURI(new URI("http://"+ShepherdProperties.IpAddress
					+":"+ShepherdProperties.Port+
					ShepherdProperties.CfgUrl+"?subtype="+subtype));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.err.println("服务端"+ShepherdProperties.IpAddress
					+":"+ShepherdProperties.Port+
					ShepherdProperties.CfgUrl+"连接失败"+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("服务端"+ShepherdProperties.IpAddress
					+":"+ShepherdProperties.Port
					+ShepherdProperties.CfgUrl+"连接失败"+e.getMessage());
		}  

        try {
			in = new BufferedReader(new InputStreamReader(response.getEntity()  
			        .getContent()));
		} catch (UnsupportedOperationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
        StringBuffer sb = new StringBuffer("");  
        try {
			while ((str = in.readLine()) != null) {  
			    sb.append(str);  
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
       
        try {
			in.close();
		} catch (IOException e) {
			System.err.println("断开连接失败"+e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        str = sb.toString(); 
		return str;
	}
}
