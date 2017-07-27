package com.eproe.shepherd.wls_health_check.tp_life.communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.eproe.shepherd.wls_health_check.tplife.utils.ShepherdProperties;

import net.sf.json.JSONObject;

public class Sender {
	static ShepherdConnector connector;
	public static int sendData(JSONObject jsonParam){      
	connector=ShepherdConnector.spdConnection;
	if(jsonParam==null){
		System.err.println("发送数据为空");
		return 0;
	}
	if(!connector.isConnected)
		connector.initConnection();
	
	connector.connect("http://"+ShepherdProperties.IpAddress+":"+ShepherdProperties.Port+ShepherdProperties.PerfDataUrl);
    try {
    	ArrayList<HashMap> l=new ArrayList();
	    if(connector.NEEDAUTH){
			StringEntity entity1 = new StringEntity(jsonParam.toString(),"utf-8");
			entity1.setContentType("application/json");
			connector.p.setEntity(entity1);
			connector.p.setHeader("Cookie",connector.c);
	    }
	    CloseableHttpResponse r = connector.httpclient.execute(connector.p);
	    String content = EntityUtils.toString(r.getEntity());
	    if(r.getStatusLine().getStatusCode()==200){
	    	r.close();
	        return 1;
	    }else if(r.getStatusLine().getStatusCode()==400){
	    	System.out.println("认证失败");
	    	connector.initConnection();
	    	Sender.sendData(jsonParam);
	    }else{
	        System.err.println("数据发送失败，失败信息为："+r.getStatusLine()+"  "+content+"submitted data:"+jsonParam);
	        r.close();
	        return 0;
	    }
    } catch (IOException e) {
    	System.err.println("数据发送异常："+e.getMessage());
	} 
	return 0;
	}
}
