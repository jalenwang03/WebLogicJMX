package com.eproe.shepherd.wls.connector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import com.eproe.shepherd.wls_health_check.tp_life.collector.Collector;



/**
 * 实例化Mbean连接
 * @author jalenwang
 *
 */
public class WeblogicConnector {
	private  JMXConnector connector;
	private  MBeanServerConnection connection;
	private  String returnInfo;
	public String connectionInfo;
	 /*
	   * Initialize connection to the Domain Runtime MBean Server.
	   */
	   public boolean initConnection(String hostname, String portString,
	      String username, String password) {
		   this.connectionInfo=hostname+":"+portString;
	       String protocol = "t3";
	      Integer portInteger = Integer.valueOf(portString);
	      int port = portInteger.intValue();
	      String jndiroot = "/jndi/";
	      String mserver = "weblogic.management.mbeanservers.domainruntime";
	       JMXServiceURL serviceURL = null;
		try {
			serviceURL = new JMXServiceURL(protocol, hostname, port,
			  jndiroot + mserver);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       Hashtable<String, Comparable> h = new Hashtable<String, Comparable>();
	      h.put(Context.SECURITY_PRINCIPAL, username);
	      h.put(Context.SECURITY_CREDENTIALS, password);
	      h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES,
	         "weblogic.management.remote");
	      h.put("jmx.remote.x.request.waiting.timeout", new Long(50000));
	      try {
			connector = JMXConnectorFactory.connect(serviceURL, h);
			connection = connector.getMBeanServerConnection();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Admin URL连接失败:"+hostname+":"+port+"-->"+e.getMessage());
			Collector.Message.add("Admin URL连接失败:"+hostname+":"+port);
			return false;
//			e.printStackTrace();
		}catch (java.lang.SecurityException e){
			System.err.println("Admin URL连接失败/密码验证错误:"+hostname+":"+port);
			Collector.Message.add("Admin URL连接失败/密码验证错误:"+hostname+":"+port+"-->"+e.getMessage());
			return false;
		}
	      
	   }
	public String getReturnInfo(){
		return this.returnInfo;
	}
	/*
	 * 返回连接
	 */
	public MBeanServerConnection getConnection(){
		return connection;
	}
	public void closeConn(){
		try {
			connector.close();
			this.connection=null;
			this.connector=null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
     * Get the main Values for the connected domain
     * @return Hashtable<String,String>
     * @throws Exception
     */
    public  Hashtable<String,String> getMainServerDomainValues() throws Exception
    {
    	ObjectName service = new ObjectName("com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean");
        try {
            Hashtable<String,String> result = new Hashtable<String,String>();
            ObjectName domainMBean =(ObjectName) this.getConnection().getAttribute(service,"DomainConfiguration");

            String serverName = (String) this.getConnection().getAttribute(service,"ServerName");

            String adminServerName = (String) this.getConnection().getAttribute(domainMBean,"AdminServerName");
            String domainName = domainMBean.getKeyProperty("Name");
            String domainRoot = (String) this.getConnection().getAttribute(domainMBean,"RootDirectory");

            result.put("serverName",serverName);
            result.put("adminServerName",adminServerName);
            result.put("domainName",domainName);
            result.put("domainRoot",domainRoot);
            result.put("domainBase",domainRoot.substring(0,domainRoot.length()-(domainName.length()+1)));
         return result;
        }
        catch (Exception ex)
        {
            //LogUtils.getLogger(LogUtils.JMX_LAYER).error("PROBLEM with JMXWrapperLocal:getMainServerDomainValues: " + ex.getMessage(), ex);
            throw new Exception("PROBLEM with JMXWrapperLocal:getMainServerDomainValues: " + ex.getMessage());
        }
    }
    
    /**
    *
    * @param name ObjectName
    * @param operationName String
    * @param params Object[]
    * @param signature String[]
    * @return Object
    * @throws Exception
    */
   public Object invoke(ObjectName name, String operationName, Object[] params, String[] signature)  throws Exception
   {
       try {
           // todo: logging  - JMXWrapperLocal:invoke called for "+name+" : "+ operationName +" !");

           if (params==null)
               params = new Object[0];
           if (signature == null)
               signature = new String[0];

           // do INVOKE
           return this.getConnection().invoke(name, operationName, params, signature);

       }
       catch (Exception ex) {
           ex.printStackTrace();
           throw new Exception("PROBLEM with JMXWrapperLocal:invoke: " + ex.getMessage());
      }
   }

}
