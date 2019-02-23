package com.kostas4949.mundis.nmapserver;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
/**
 * Reading the property file
 * 
 *
 */
public class AMPropertyReader {
	private String PropertyFileName,BASE_URI,sqlurl,pass,usrname,Inputjobs,database_mode;
	private int sleeptime;
	/**
	 * PropertyReader takes as an argument a string pointing to a property file.
	 * @param PropertyFileName Property file to be read.
	 */
	public AMPropertyReader(String PropertyFileName){
		this.PropertyFileName=PropertyFileName;
	}
	/**
	 * Reads the property file getting credentials and url for sql server,getting http server url,getting if database or cache will be used for showing results in gui and if txt file will be input for jobs.
	 */
	public void readpropertyfile(){
		try{
			FileInputStream propfileinput=new FileInputStream(PropertyFileName);
			Properties prop=new Properties();
			prop.load(propfileinput);                         
			propfileinput.close();
			sqlurl=prop.getProperty("sqlurl");
			System.out.println("sqlurl is "+sqlurl);
			pass=prop.getProperty("pass");
			System.out.println("pass is "+pass);
			usrname=prop.getProperty("usrname");
			System.out.println("usrname is "+usrname);
			BASE_URI=prop.getProperty("BASE_URI");
			System.out.println("BASE_URI for server is "+BASE_URI);
			Inputjobs=prop.getProperty("Inputjobs");
			System.out.println("Inputjobs is "+Inputjobs);
			sleeptime=Integer.parseInt(prop.getProperty("sleeptime"));
			System.out.println("sleep time is "+sleeptime);
			database_mode=prop.getProperty("database_mode");
			System.out.println("database_mode is "+database_mode);
		} catch (FileNotFoundException e){
			System.out.println("Property file not found: "+e);
			System.exit(2);
		} catch (IOException e){
			System.out.println("Error: "+e);
			System.exit(1);
		}
	}
	/**
	 * 
	 * @return Returns the amount of time anything will sleep on server side.
	 */
	public int get_sleeptime(){
		return sleeptime;
	}
	/**
	 * 
	 * @return Returns the url the grizzly server is listening on.
	 */
	public String get_BASE_URI(){
		return BASE_URI;
	}
	public String get_sqlurl(){
		return sqlurl;
	}
	/**
	 * 
	 * @return The password to connect to the sql database.
	 */
	public String get_pass(){
		return pass;
	}
	/**
	 * 
	 * @return The username to connect to the sql database.
	 */
	public String get_usrname(){
		return usrname;
	}
	/**
	 * 
	 * @return Inputjobs location if inputjobs.txt will be used, else null.
	 */
	public String get_Inputjobs(){
		return Inputjobs;
	}
	/**
	 * 
	 * @return True if database will be used to show GUI results or false if cache will be used to show GUI results.
	 */
	public String get_databasemode(){
		return database_mode;
	}
}
