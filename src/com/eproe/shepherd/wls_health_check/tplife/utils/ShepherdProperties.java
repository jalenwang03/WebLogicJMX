package com.eproe.shepherd.wls_health_check.tplife.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class ShepherdProperties {
	public static String IpAddress;
	public static String Port;
	public static String AuthUrl;
	public static String SendUrl;
	public static String CfgUrl;
	public static String PerfDataUrl;
	public static String Username;
	public static String Password;
	public static String DomainType;
	public static String ServerType;
	public static int MaxActive;
	static{
		try {
			YamlReader reader = new YamlReader(new FileReader("cfg/Shepherd.properties"));
			Object object = reader.read();
			ShepherdProperties.IpAddress=(String) ((Map)((Map)object).get("Shepherd")).get("IpAddress");
			ShepherdProperties.Port=(String) ((Map)((Map)object).get("Shepherd")).get("Port");
			ShepherdProperties.AuthUrl=(String) ((Map)((Map)object).get("Shepherd")).get("AuthUrl");
			ShepherdProperties.SendUrl=(String) ((Map)((Map)object).get("Shepherd")).get("SendUrl");
			ShepherdProperties.CfgUrl=(String) ((Map)((Map)object).get("Shepherd")).get("CfgUrl");
			ShepherdProperties.PerfDataUrl=(String) ((Map)((Map)object).get("Shepherd")).get("PerfDataUrl");
			ShepherdProperties.Username=(String) ((Map)((Map)object).get("Shepherd")).get("Username");
			ShepherdProperties.Password=(String) ((Map)((Map)object).get("Shepherd")).get("Password");
			ShepherdProperties.DomainType=(String) ((Map)((Map)object).get("Shepherd")).get("DomainType");
			ShepherdProperties.ServerType=(String) ((Map)((Map)object).get("Shepherd")).get("ServerType");
			ShepherdProperties.MaxActive=Integer.parseInt((String) ((Map)((Map)object).get("Shepherd")).get("MaxActive"));
//			System.out.println(ShepherdProperties.IpAddress);
//			System.out.println(ShepherdProperties.Port);
//			System.out.println(ShepherdProperties.AuthUrl);
//			System.out.println(ShepherdProperties.SendUrl);
//			System.out.println(ShepherdProperties.CfgUrl);
//			System.out.println(ShepherdProperties.PerfDataUrl);
//			System.out.println(ShepherdProperties.Username);
//			System.out.println(ShepherdProperties.Password);
//			System.out.println(ShepherdProperties.DomainType);
//			System.out.println(ShepherdProperties.ServerType);
		} catch (FileNotFoundException | YamlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
		System.out.println(ShepherdProperties.IpAddress);
		System.out.println(ShepherdProperties.Port);
		System.out.println(ShepherdProperties.AuthUrl);
		System.out.println(ShepherdProperties.SendUrl);
		System.out.println(ShepherdProperties.CfgUrl);
		System.out.println(ShepherdProperties.PerfDataUrl);
		System.out.println(ShepherdProperties.Username);
		System.out.println(ShepherdProperties.Password);
	}
}
