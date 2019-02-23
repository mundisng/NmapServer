package com.kostas4949.mundis.nmapserver;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 * Handles all database connections and almost all queries.
 * 
 *
 */
public class mysqlconnector {
	/**
	 * Creates connection to the database
	 * @return The connection if successfull, else null.
	 */
	public static Connection connect(){
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn=DriverManager.getConnection(Main.propreader.get_sqlurl(),Main.propreader.get_usrname(), Main.propreader.get_pass());
			return conn;
		} catch (Exception e) {
			System.out.println("connect exception : "+e);
			JOptionPane.showMessageDialog(null,e);
			e.printStackTrace();
			System.exit(10);
			return null;
		}
	}
	/**
	 * Insert jobs we already sent to SA's to the database with AutoIncrement.
	 * @param mys  Job string that was sent to SA.
	 * @param my_hash Hash of SA that got the job.
	 * @return the AutoIncrement ID
	 */
	public static int insertautonmapjob(String mys,int my_hash){
		PreparedStatement myPreState=null;
		int job_id=0;
		Connection myconn = connect();
		try {
			String myQuery="insert into nmapjobs (job,SAgents_hash) values (?,?)";
			myPreState=myconn.prepareStatement(myQuery,Statement.RETURN_GENERATED_KEYS);  //Protection from sql injection
			myPreState.setString(1,mys);
			myPreState.setInt(2,my_hash);
			
			myPreState.execute();
			ResultSet generatedKeys = myPreState.getGeneratedKeys(); //Get AutoIncrement ID from database
			if (generatedKeys.next()) {
				job_id=generatedKeys.getInt(1); 
			}
			myPreState.close();
			myconn.close();
		} catch (SQLException ee) {
			System.out.println("Connection Failed! Check output console");
			ee.printStackTrace();
		}
		return job_id; //Return AutoIncrement ID
	}
	/**
	 * Insert jobs we already sent to SA's to the database.
	 * @param id Id of job.
	 * @param mys  Job string that was sent to SA.
	 * @param my_hash Hash of SA that got the job.
	 */
	public static void insertnmapjob(int id,String mys,int my_hash){
		PreparedStatement myPreState=null;
		Connection myconn = connect();
		try {
			String myQuery="insert into nmapjobs (id,job,SAgents_hash) values (?,?,?)";
			myPreState=myconn.prepareStatement(myQuery);  //Protection from sql injection
			myPreState.setInt(1,id);
			myPreState.setString(2,mys);
			myPreState.setInt(3,my_hash);
			myPreState.execute();
			myPreState.close();
			myconn.close();
		} catch (SQLException ee) {
			System.out.println("Connection Failed! Check output console");
			ee.printStackTrace();
		}
	}
	/**
	 * Insert result returned from SA into database.
	 * @param id Id of job that gave the result.
	 * @param hostname Hostname from result.
	 * @param time Time the result was accepted by the server.
	 * @param tasks Tasks from the result.
	 * @param resulttt Ports from the result.
	 * @param periodic 1 for periodic thread that generated this result, else 0.
	 * @param hash Hash of SA that sent this result.
	 */
	public static void insertsqlresults(Integer id,String hostname,String time,String tasks,String resulttt,Integer periodic,int hash){
		String result=resulttt;
		PreparedStatement myPreState=null;
		Connection myconn = connect();
		try {
			String myQuery="insert into nmapresults (nmapjobs_id,hostname,time,results,periodic,nmapjobs_SAgents_hash,tasks) values (?,?,?,?,?,?,?)";
			myPreState=myconn.prepareStatement(myQuery);
			myPreState.setInt(1,id);   //Protection against sql injection
			myPreState.setString(2,hostname);
			myPreState.setString(3,time);
			myPreState.setString(4,result);
			myPreState.setInt(5,periodic);
			myPreState.setInt(6,hash);
			myPreState.setString(7,tasks);
	        myPreState.execute();
	        myPreState.close();
	        myconn.close();
		} catch (SQLException ee) {
			System.out.println("Connection Failed! Check output console");
			ee.printStackTrace();
		}finally{
		}
	}
	/**
	 * Change active state of SAs in database.
	 * @param hash Hash of SA.
	 * @param active State to change the SA to. 1 for active, 0 for not active.
	 */
	public static void changesqlactivesagents(int hash,int active){
		Connection myconn = connect();
		try {
			String myQuery="UPDATE SAgents SET active=? WHERE hash=?";
			PreparedStatement myPreState=myconn.prepareStatement(myQuery);
			myPreState.setInt(1,active);
			myPreState.setInt(2,hash);
	        myPreState.execute();
	        myPreState.close();
	        myconn.close();
		} catch (SQLException ee) {
			System.out.println("Connection Failed! Check output console");
			ee.printStackTrace();
		}
		
	}
	/**
	 * Insert SAs into database.
	 * @param hash Hash of SA.
	 * @param device Device name of SA.
	 * @param IP IP of SA.
	 * @param MAC Mac Address of SA.
	 * @param OS Operating System version of SA.
	 * @param nmapver Nmap version of SA.
	 */
	public static void insertsqlsagents(int hash,String device,String IP,String MAC,String OS,String nmapver){
		Connection myconn = connect();
		try {
			String myQuery="insert into SAgents (hash,device_name,interface_ip,interface_macaddr,os_version,nmap_version,active) values (?,?,?,?,?,?,?)";
			PreparedStatement myPreState=myconn.prepareStatement(myQuery);
			myPreState.setInt(1,hash);
			myPreState.setString(2,device);
			myPreState.setString(3,IP);
			myPreState.setString(4,MAC);
			myPreState.setString(5,OS);
			myPreState.setString(6,nmapver);
			myPreState.setInt(7,1);
	        myPreState.execute();
	        myPreState.close();
	        myconn.close();
		} catch (SQLException ee) {
			System.out.println("Connection Failed! Check output console");
			ee.printStackTrace();
		}
		finally{
			
		}
	}
	/**
	 * Insert credentials for android device connecting.
	 * @param username Username given by android device.
	 * @param password Password given by android device.
	 */
	public static void insertandroidcredentials(String username,String password){
		Connection myconn = connect();
		try{
			String myQuery="insert into androiduser (username,password) values (?,?)"; //SQL Injection protection
			PreparedStatement myPreState=myconn.prepareStatement(myQuery);
			myPreState.setString(1,username);
			myPreState.setString(2,password);
			 myPreState.execute();
		        myPreState.close();
		        myconn.close();
		}catch (SQLException ee){
			ee.printStackTrace();
		}
	}
	/**
	 * Easy way to drop the whole database, was used for internal testing purposes.
	 */
	public static void dropdatabase(){
		Connection myconn = connect();
		Statement stmt = null;
		try {
		stmt=myconn.createStatement();
		System.out.println("Dropping database");
		String sql="DROP DATABASE mydb";
		stmt.executeUpdate(sql);
		myconn.close();
		}catch (SQLException e){
			System.out.println("Couldn't drop database");
		}
	}
}
