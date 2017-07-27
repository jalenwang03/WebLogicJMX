package com.eproe.shepherd.wls.mbeans.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.management.ObjectName;

import com.eproe.shepherd.wls.connector.WeblogicConnector;
import com.eproe.shepherd.wls.mbeans.server.ServerMbean;

import net.sf.json.JSONObject;

public class jdbcMbean {
	public ObjectName serverRuntimeObj;
	public ObjectName serverConfigurationObj;
	public ArrayList jdbcRTList=new ArrayList();
	public ArrayList jdbcCFGList=new ArrayList();
	public jdbcMbean(ServerMbean server){
		this.serverConfigurationObj=server.runtimeservice;
		this.serverRuntimeObj=server.configrationservice;
//		this.jdbcCFGList=WeblogicConnector.getConnection().getAttribute(this.serverConfigurationObj, "JDBC");
//		this.jdbcRTList=WeblogicConnector.getConnection().getAttribute(this.serverRuntimeObj, "JDBC");
	}
	public JSONObject jdbcRuntime(){
		JSONObject json=new JSONObject();
		
		return json;
	}
	public void Fetch(){
		
	}
}
