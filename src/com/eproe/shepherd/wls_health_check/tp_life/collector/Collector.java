package com.eproe.shepherd.wls_health_check.tp_life.collector;

import com.eproe.shepherd.wls.instance.wlsInstance;
import com.eproe.shepherd.wls.mbean.application.applicationConfigurationMbeans;
import com.eproe.shepherd.wls.mbean.application.applicationRuntimeMbeans;
import com.eproe.shepherd.wls.mbean.jdbc.jdbcConfigurationMbeans;
import com.eproe.shepherd.wls.mbean.jdbc.jdbcRuntimeMbeans;
import com.eproe.shepherd.wls.mbeans.domain.DomainConfigurationMbean;
import com.eproe.shepherd.wls.mbeans.domain.DomainRuntimeMbean;
import com.eproe.shepherd.wls.mbeans.server.ServerMbean;
import com.eproe.shepherd.wls_health_check.tp_life.Wls_health_check;
import com.eproe.shepherd.wls_health_check.tp_life.CI.Domain;
import com.eproe.shepherd.wls_health_check.tp_life.communication.Sender;

import net.sf.json.JSONObject;

public class Collector implements Runnable{
	Domain dom;
	String process;
	public Collector(Domain dom,String process){
		this.dom=dom;
		this.process=process;
	}
	public void doCollect(){
		wlsInstance wls=new wlsInstance(dom.host,dom.port,dom.username,dom.password);
//		DomainRuntimeMbean domainrt=wls.getDomainRuntime();
//		DomainConfigurationMbean domaincft=wls.getDomainConfiguration();
		if(!wls.connected){
			Wls_health_check.finish_CNT++;
			return;
		}

		System.out.println(this.process+dom.host+":"+dom.port+"开始收集");
    	for(int i=0;i<dom.servers.size();i++){
    		System.out.println(process+"\t\t"+dom.host+":"+dom.port+":"+dom.servers.get(i).getServerName()+"开始收集");
			JSONObject data=new JSONObject();
			ServerMbean sm=new ServerMbean(wls.getConnector(),wls.getDomainName(),dom.servers.get(i).getServerName());
			
//			JSONObject serverConfigurationJson=sm.getServerConfigurationData();
//			JSONObject serverRuntimeJson=sm.getServerRuntimeData();
			
	
		
			data.put("ServerConfiguration", sm.getServerConfigurationData());
			data.put("ServerRuntime", sm.getServerRuntimeData());
			
			applicationRuntimeMbeans arm=new applicationRuntimeMbeans(wls.getConnector());
			data.put("ApplicationRuntime", arm.getAllApplicationRuntimes(dom.servers.get(i).getServerName()));
			applicationConfigurationMbeans acm=new applicationConfigurationMbeans(wls.getConnector());
			data.put("ApplicationConfiguration", acm.getallApplicationsConfiguration(dom.servers.get(i).getServerName()));
			jdbcConfigurationMbeans jdbccfm=new jdbcConfigurationMbeans(wls.getConnector());
			jdbcRuntimeMbeans jdbcrtm=new jdbcRuntimeMbeans(wls.getConnector());
			data.put("DatasourcesRuntime", jdbcrtm.getAllJdbcRuntime(dom.servers.get(i).getServerName()));
			data.put("DatasourcesConfiguration", jdbccfm.getAllJdbcConfiguration(dom.servers.get(i).getServerName()));
//			System.out.println(data.toString());
			Sender.sendData(this.ToStandJson(data,dom.servers.get(i).getServerName()));
			System.out.println(process+"\t\t"+dom.host+":"+dom.port+":"+dom.servers.get(i).getServerName()+"收集完成");
    	}
    	Wls_health_check.finish_CNT++;
    	System.out.println(this.process+dom.host+":"+dom.port+"收集完成");
    	wls.close();
	}
	public JSONObject ToStandJson(JSONObject beforejson,String serverName){
		JSONObject afterjson = new JSONObject();
		afterjson.put("data", beforejson);
		afterjson.put("subtype", "wls.server");
		afterjson.put("type", "as");
		afterjson.put("name", serverName);
		return afterjson;
	}
	@Override
	public void run() {
		this.doCollect();
		// TODO Auto-generated method stub
	}
}
