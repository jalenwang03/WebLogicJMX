package com.eproe.shepherd.wls.mbean.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.eproe.shepherd.wls.connector.WeblogicConnector;
import com.eproe.shepherd.wls.mbeans.domain.DomainConfigurationMbean;
import com.eproe.shepherd.wls.utils.MBeanProperties;
import com.eproe.shepherd.wls_health_check.tp_life.collector.Collector;

import net.sf.json.JSONObject;

public class applicationConfigurationMbeans {
	ObjectName[] applications = null;
	HashMap<ObjectName,List> appMap=new HashMap<ObjectName,List>();
	WeblogicConnector connector;
	public applicationConfigurationMbeans(WeblogicConnector connector){
		this.connector=connector;
		try {
			applications = (ObjectName[]) connector.getConnection().getAttribute(
					new DomainConfigurationMbean(connector).service, "AppDeployments");
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e1) {
			// TODO Auto-generated catch block
			Collector.Message.add(e1.getMessage());
			e1.printStackTrace();
		}
	}
	public JSONObject getApplicationConfiguration(ObjectName appObj){
		JSONObject json=new JSONObject();
		if(MBeanProperties.cfgApplicationList==null)
			return null;
		for(int i=0;i<MBeanProperties.cfgApplicationList.size();i++){
			try {
				json.put(MBeanProperties.cfgApplicationList.get(i),
						connector.getConnection().getAttribute(appObj, (String) MBeanProperties.cfgApplicationList.get(i)));
			} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
					| IOException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			}
		}
		return json;
	}
	public JSONObject getallApplicationsConfiguration(){
		JSONObject json=new JSONObject();
		
		for(int i=0;i<applications.length;i++){
			try {
				json.put(
						((String)connector.getConnection().getAttribute(applications[i], "Name")).replace(".", "_"), 
						getApplicationConfiguration(applications[i])
						);
			} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
					| IOException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			}
		}
		return json;
	}
	public JSONObject getallApplicationsConfiguration(String serverName){
		this.parseAppTargets();
		List appList=new ArrayList();
		JSONObject json=new JSONObject();
		for(ObjectName app : appMap.keySet()){
			if(appMap.get(app).contains(serverName))
				appList.add(app);
		}
		for(int i=0;i<appList.size();i++){
//			System.out.println(this.connector.connectionInfo+"-----"+serverName+"----"+appList.get(i));
			try {
				json.put(((String)connector.getConnection().getAttribute((ObjectName) appList.get(i), "Name")).replace(".", "_"), this.getApplicationConfiguration((ObjectName)appList.get(i)));
			} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
					| IOException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			}
		}
		return json;
		
	}
	public void parseAppTargets(){
		if(!appMap.isEmpty())
			return;
		for(int i=0;i<applications.length;i++){
			try {
				List Targets = new ArrayList();
				ObjectName[] targetobjs=(ObjectName[]) connector.getConnection().getAttribute(applications[i], "Targets");
				for(int j=0;j<targetobjs.length;j++){
					Targets.add(connector.getConnection().getAttribute(targetobjs[j], "Name"));
				}
				appMap.put(applications[i], Targets);
			} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
					| IOException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	public List getappList(String serverName){
		List appList=new ArrayList();
		JSONObject json=new JSONObject();
		for(ObjectName app : appMap.keySet()){
			if(appMap.get(app).contains(serverName))
				appList.add(app);
		}
		return appList;
	}
	public JSONObject getallApplicationsConfiguration(ObjectName serverConfObj){
		return null;
		
	}
}
