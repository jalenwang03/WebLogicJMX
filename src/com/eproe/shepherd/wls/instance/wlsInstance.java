package com.eproe.shepherd.wls.instance;

import java.io.IOException;
import java.util.List;

import com.eproe.shepherd.wls.connector.WeblogicConnector;
import com.eproe.shepherd.wls.mbean.application.applicationConfigurationMbeans;
import com.eproe.shepherd.wls.mbean.application.applicationRuntimeMbeans;
import com.eproe.shepherd.wls.mbean.jdbc.jdbcConfigurationMbeans;
import com.eproe.shepherd.wls.mbean.jdbc.jdbcRuntimeMbeans;
import com.eproe.shepherd.wls.mbeans.domain.DomainConfigurationMbean;
import com.eproe.shepherd.wls.mbeans.domain.DomainRuntimeMbean;
import com.eproe.shepherd.wls.mbeans.server.ServerMbean;
import com.eproe.shepherd.wls.utils.MBeanProperties;
import com.eproe.shepherd.wls.utils.myLogger;

public class wlsInstance {
	public String serverName=null;
	public boolean connected=false;
	public String domainName;
	public List serverList;
	public WeblogicConnector connector;
	public wlsInstance(String hostname,String port,String user,String pass){
		connector=new WeblogicConnector();
		this.connected=connector.initConnection(hostname, port, user, pass);
		if(!this.connected){
			return;
		}
		//鍒濆鍖栭厤缃枃浠�
		MBeanProperties.init();
		this.domainName=new DomainConfigurationMbean(connector).getDomainName();
	}
	public DomainRuntimeMbean getDomainRuntime(){
		return new DomainRuntimeMbean(connector);
	}
	public DomainConfigurationMbean getDomainConfiguration(){
		return new DomainConfigurationMbean(connector);
	}
	public String getDomainName(){
		return this.domainName;
	}
	public wlsInstance(String hostname,int port,String user,String pass){
		connector=new WeblogicConnector();
		this.connected=connector.initConnection(hostname, String.valueOf(port), user, pass);
		if(!this.connected==true){
//			System.err.println("AdminServer");
			return;
		}
		//鍒濆鍖栭厤缃枃浠�
//		MBeanProperties.init();
		this.domainName=new DomainConfigurationMbean(connector).getDomainName();
	}
	
	public wlsInstance(String server,String user,String pass,String hostname,String port){
		connector.initConnection(hostname, port, user, pass);
		this.connected=true;
//		MBeanProperties.init();
		this.serverName=server;
	}
	public WeblogicConnector getConnector(){
		return this.connector;
	}
	public String getAllData(){
		if (!this.connected){
			return null;
		}
		return null;
	}
	public String getJDBCData(){
		if (!this.connected){
			return null;
		}
		return null;
	}
	public String getServerData(){
		if (!this.connected){
			return null;
		}
		
		return null;
	}
	public String getApplicationData(){
		if (!this.connected){
			return null;
		}
		
		return null;
	}
	public String getWTCData(){
		if (!this.connected){
			return null;
		}
		
		return null;
	}
	public void close(){
		this.connector.closeConn();
		this.connector=null;
		this.connected=false;
	}
	public static void main(String args[]){
		new wlsInstance("115.159.193.51","7001","weblogic","123weblogic");
//		wtcMbean wtc=new wtcMbean();
//		System.out.println(wtc.getWTCRuntime("AdminServer"));
//		System.out.println(wtc.getwtcConfiguration());
//		System.out.println(arm.ge("AdminServer"));
//		sm.getWTCRuntime();
//		System.out.println(sm.getServerRuntimeData());
//		System.out.println(sm.getServerConfigurationData());
	}

}
