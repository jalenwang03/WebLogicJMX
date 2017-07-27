package com.eproe.shepherd.wls.mbeans.domain;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.eproe.shepherd.wls.connector.WeblogicConnector;
import com.eproe.shepherd.wls.utils.MBeanProperties;
import com.eproe.shepherd.wls_health_check.tp_life.collector.Collector;

import net.sf.json.JSONObject;

public class DomainConfigurationMbean {
	public  ObjectName service;
	WeblogicConnector connector;
	public DomainConfigurationMbean(WeblogicConnector connector){
		this.connector=connector;
		try {
			service=(ObjectName) connector.getConnection().getAttribute(new DomainRuntimeMbean(connector).service,"DomainConfiguration");
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e) {
			// TODO Auto-generated catch block
			System.out.println(connector.connectionInfo+e.getMessage());
			Collector.Message.add(e.getMessage());
//			e.printStackTrace();
		}catch (IllegalStateException e){
			Collector.Message.add(e.getMessage());
			System.err.println(connector.connectionInfo+e.getMessage());
		}
	}

	   
	   public JSONObject getDomainConfig(){
		   JSONObject resultjson = new JSONObject();
		   if(MBeanProperties.cfgDomainList==null){
			   return null;
		   }
		   List domainConfigcfgList = MBeanProperties.cfgDomainList;
		   for(int i=0;i<domainConfigcfgList.size();i++){
//			   System.out.println(domainConfigcfgList.get(i));
			   try {
				resultjson.put(domainConfigcfgList.get(i), connector.getConnection().getAttribute(service, (String) domainConfigcfgList.get(i)));
			} catch (AttributeNotFoundException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			} catch (InstanceNotFoundException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			} catch (MBeanException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			} catch (ReflectionException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Collector.Message.add(e.getMessage());
				e.printStackTrace();
			}
		   }
		   ObjectName servers[];
		   try {
			servers=(ObjectName[]) connector.getConnection().getAttribute(service, "Servers");
			for(int i=0;i<servers.length;i++){
//				System.out.println(servers[i]);
			}
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e) {
			// TODO Auto-generated catch block
			Collector.Message.add(e.getMessage());
			e.printStackTrace();
		}
		   return resultjson;
	   }
	   public  String getDomainName(){
		   try {
			return (String) connector.getConnection().getAttribute(this.service, "Name");
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e) {
			// TODO Auto-generated catch block
			Collector.Message.add(e.getMessage());
			e.printStackTrace();
		}
		return null;
	   }
	   public ObjectName[] getServersConf(){
		   try {
			return (ObjectName[])connector.getConnection().getAttribute(this.service, "Servers");
		} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
				| IOException e) {
			// TODO Auto-generated catch block
			Collector.Message.add(e.getMessage());
			e.printStackTrace();
		}
		return null;
				   
	   }
	   public ArrayList<String> getServersList() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException{
		   ArrayList<String> l=new ArrayList<String>();
		   ObjectName[] servers=this.getServersConf();
		   for(int i=0;i<servers.length;i++){
			   l.add((String) connector.getConnection().getAttribute(servers[i], "Name"));
		   }
		return l;
	   }
	 
}
