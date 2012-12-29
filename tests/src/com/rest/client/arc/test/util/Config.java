package com.rest.client.arc.test.util;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;
 
public class Config {
	private Properties configFile;
   
	public Config(String filename) {
		configFile = new Properties();
		
		try {
			if (configFile != null)
			configFile.load(new FileReader(new File(filename)));			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	public String getProperty(String key) {
		String value = this.configFile.getProperty(key);		
		return value;
	}
}