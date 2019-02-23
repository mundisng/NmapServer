package com.kostas4949.mundis.nmapserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Iterator;
/**
 * All android connections go through this class.
 * @author Kostas Skivalos and Nikos Gkountas
 *
 */
@Path("android")
public class Android {
    /**
     * Checking android login credentials sent as jsonobject
     * @param mys Jsonobject string with credentials+some identification 
     * @return Status.Unauthorized if wrong credentials, status.ok if correct credentials,status.forbidden if server declined user,status.internal_server_error if exception occurs.
     */
    @POST
	@Path("/identification")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response accept_credentials(String mys){
	    System.out.println("android got here");
	    try {
	        JSONObject jjson = (JSONObject)new JSONParser().parse(mys); //parsing json object string
	        System.out.println("Device Name: "+jjson.get("Name").toString());
	        System.out.println("MAC: "+jjson.get("MAC").toString());
	        System.out.println("IP: "+jjson.get("IP").toString());
	        System.out.println("OS: "+jjson.get("OS").toString());
	        System.out.println("Username: "+jjson.get("Username").toString());
	        System.out.println("Password: "+jjson.get("Password").toString());
	        Connection myconn=mysqlconnector.connect();
	        PreparedStatement myPreState=null;
			String myQuery="select * from androiduser where username=? and password=? "; //Sanitize sql query that checks if username/pass exists in database
			myPreState=myconn.prepareStatement(myQuery);
			myPreState.setString(1,jjson.get("Username").toString());
			myPreState.setString(2, jjson.get("Password").toString());
			ResultSet myResS=myPreState.executeQuery();
			if (!myResS.next()){  //if there is no result returned it means username/pass does not exist.
				myPreState.close();
				myconn.close();
				myResS.close();
				return Response.status(Status.UNAUTHORIZED).entity("Wrong Credentials!").build();
			}
			myPreState.close();
			myconn.close();
			myResS.close();
			int reply=JOptionPane.showConfirmDialog(null,"Do you want to accept android device with username "+jjson.get("Username").toString()+"?","Client Request", JOptionPane.YES_NO_OPTION);
	        if(reply==JOptionPane.YES_OPTION){  //Gui dialog to accept android device or not
	    	    return Response.status(Status.OK).entity("Accepted information!").build();
	        }
	        	else{
	 	        	return Response.status(Status.FORBIDDEN).entity("Access Denied!").build();
	            }
	        }catch (ParseException | SQLException ee){
	    	    ee.printStackTrace();
	    	    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server Error").build();
	         }
	}
    /**
    * Registering a new android device with username/pass
    * @param mys Jsonobject in string format containing username and pass
    * @return Status.forbidden if registered data already exists in database,status.internal_server_error in case of exception, else status.ok for accepted registration
    */
	@POST
	@Path("/registration")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response android_registration(String mys){
	    System.out.println("registration");
	    try {
	        JSONObject jjson = (JSONObject)new JSONParser().parse(mys);
	        System.out.println("Username: "+jjson.get("Username").toString());
	        System.out.println("Password: "+jjson.get("Password").toString());
	        Connection myconn=mysqlconnector.connect();
	        PreparedStatement myPreState=null;
	        String myQuery="select * from androiduser where username=?";
	        myPreState=myconn.prepareStatement(myQuery);  //Check if username/pass already exists in database
			myPreState.setString(1,jjson.get("Username").toString());
			ResultSet myResS=myPreState.executeQuery();
			if (myResS.next()){
				myPreState.close();
				myconn.close();
				myResS.close();
				System.out.println("Registration details already exist in database!");
				return Response.status(Status.FORBIDDEN).entity("Declined!").build();
			}
			myPreState.close();
			myconn.close();
			myResS.close();
	        mysqlconnector.insertandroidcredentials(jjson.get("Username").toString(), jjson.get("Password").toString());
	        System.out.println("Android credentials inserted into sql database successfully!");
	    }catch (ParseException | SQLException e){
	    	e.printStackTrace();
	    	return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server Error").build();
	    }
		return Response.status(Status.OK).entity("Accepted Information!").build();
	}
    /**
     * Returns SA results based on SA and amount of results.
     * @param SA SA to return the results of, default is none 
     * @param results Amount of last results returned
     * @return Status.OK in all cases. If there were no results an empty jarray returns 
     */
    @GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
    public Response SendSA(@DefaultValue("none")@QueryParam ("SA") String SA,@DefaultValue("0")@QueryParam("results") String results){
    	JSONArray ja = new JSONArray();
		Connection myconn=mysqlconnector.connect();
		PreparedStatement myPreState=null;
		try {
    	    if (SA.equals("all")){  //if we want results from all SA
    	        if(results.equals("all")){ //if we want all results
    	            String myQuery="SELECT * FROM nmapresults";
    	            myPreState=myconn.prepareStatement(myQuery);
    	        }
    	        else{ //if we wants certain amount of results
    	        	String myQuery="SELECT * FROM nmapresults ORDER BY time DESC LIMIT ?";
    	        	myPreState=myconn.prepareStatement(myQuery);
    	        	myPreState.setInt(1,Integer.parseInt(results));
    	        }
    	    }
    	    else { //if we want results from a certain SA
    	    	if(results.equals("all")){ //if we want all its results
    	    		String myQuery="SELECT * FROM nmapresults WHERE nmapjobs_SAgents_hash=?";
    	    		myPreState=myconn.prepareStatement(myQuery);
    	    		myPreState.setInt(1,Integer.parseInt(SA));
    	    	}
    	    	else{ //if we want a certain amount of it's results
    	    		String myQuery="SELECT * FROM nmapresults WHERE nmapjobs_SAgents_hash=? ORDER BY time DESC LIMIT ?";
    	    		myPreState=myconn.prepareStatement(myQuery);
    	    		myPreState.setInt(1,Integer.parseInt(SA));
    	    		myPreState.setInt(2,Integer.parseInt(results));
    	    	}
    	    }
    		ResultSet rs=myPreState.executeQuery();
    		while (rs.next()){ //Add those results in a jarray
    			JSONObject jobb= new JSONObject();
    			jobb.put("hash",Integer.toString(rs.getInt("nmapjobs_SAgents_hash")));
    			jobb.put("id",Integer.toString(rs.getInt("nmapjobs_id")));
    			jobb.put("time",rs.getString("time"));
    			jobb.put("hostname",rs.getString("hostname"));
    			jobb.put("tasks",rs.getString("tasks"));
    			jobb.put("results",rs.getString("results"));
    			jobb.put("periodic",Integer.toString(rs.getInt("periodic")));
           	   	ja.add(jobb);
    		}
    		myPreState.close();
    		rs.close();
    		myconn.close();
		}catch (SQLException e){
			e.printStackTrace();
		}
    	System.out.println(ja.toString());
    	return Response.status(Status.OK).entity(ja.toString()).build();
    }
    /**
     * Send all SA's with their respective info
     * @return Jarray containing all SA's, or status.internal_server_error in case there was an exception.
     */
    @GET
 	@Path("/getSAInfo")
 	@Produces(MediaType.APPLICATION_JSON)
    public Response SendSAInfo(){
    	Connection myconn=mysqlconnector.connect();
		PreparedStatement myPreState=null;
		String myQuery="select * from SAgents ";
    	JSONArray ja = new JSONArray();
		try {
			myPreState=myconn.prepareStatement(myQuery);
			ResultSet rs=myPreState.executeQuery();
			while (rs.next()){
				JSONObject jobb= new JSONObject();
				jobb.put("hash",Integer.toString(rs.getInt("hash")));
				jobb.put("device",rs.getString("device_name"));
				jobb.put("ip",rs.getString("interface_ip"));
				jobb.put("mac",rs.getString("interface_macaddr"));
				jobb.put("os",rs.getString("os_version"));
				jobb.put("nmap",rs.getString("nmap_version"));
				if (rs.getInt("active")==1){
					jobb.put("active","true");
				}
				else {
					jobb.put("active","false");
				}
				ja.add(jobb);
			}
			myPreState.close();
			rs.close();
			myconn.close();
		}catch (SQLException e){
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Rekt").build();
		}
    	return Response.status(Status.OK).entity(ja.toString()).build();
    }
	/**
	 * Receiving either one or multiple: stop sa's,stop periodic jobs, new jobs android client created
	 * @param mys Accepts a jarray containing all the jobs. Each jsonobject in jarray has a {[hash],[job]} structure.
	 * @return A jarray {[hash],[response]} with the response for every jsonobject job that was sent. In case of at least 1 job causing an exception, return status.internal_server_error.
	 */
	@POST
	@Path("/sendjobs")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response stopSA(String mys){
		String respons=null,hashh=null;
		boolean except=false;
    	JSONArray jresponse = new JSONArray();
    	try {
			JSONParser parser=new JSONParser();
			Object obj=parser.parse(mys);
			JSONArray jarray=(JSONArray)obj;
			for (int re=0; re<jarray.size(); re++){  //read jsonobject jobs from jarray
				JSONObject jjson=(JSONObject)jarray.get(re);
			    String jobz,jobz2;
			    hashh=jjson.get("hash").toString();
			    jobz=jjson.get("job").toString();
			    jobz2=jobz.substring(jobz.indexOf(",")+1);
			    if (jobz.substring(0,jobz.indexOf(",")).equals("-1")){ //If the job is a stop SA job
			    	System.out.println("Gonna stop SA with hash: "+hashh);
				    boolean found_sa=false;
					int my_hash=Integer.parseInt(hashh);
					SAnode my_sanode=null;
					Iterator<SAnode> ia =Main.my_salist.iterator();
					while (ia.hasNext()){
						my_sanode=ia.next();
						if (my_sanode.get_hash()==my_hash){
							found_sa=true;
							break;
						}
					}
					if(found_sa){ //If same job was already found for this stop sa job
						if(my_sanode.sa_stop_flag()){
							respons="Stop msg is already (being) sent to SA: "+my_hash;
							System.out.println(respons);
							JSONObject jobresp= new JSONObject();
							jobresp.put("hash",hashh);
							jobresp.put("responss", respons);
							jresponse.add(jobresp);
						}
					    else{ //Stop this SA based on job
							my_sanode.stop_sa();
							respons="Stop msg will be sent to SA: "+my_hash;
							System.out.println(respons);
							JSONObject jobresp= new JSONObject();
							jobresp.put("hash",hashh);
							jobresp.put("responss", respons);
							jresponse.add(jobresp);
						}
					}
				    else{ //Couldn't find SA with hash that was sent
						respons="There isnt a SA with hash: "+my_hash;
						System.out.println(respons);
						JSONObject jobresp= new JSONObject();
						jobresp.put("hash",hashh);
						jobresp.put("responss", respons);
						jresponse.add(jobresp);
			    	}
			    }
		    	else if (jobz2.substring(0, jobz2.indexOf(",")).equals("Stop")){ //In case job is stop periodic
			    	boolean found_sa=false;
                	int my_hash=Integer.parseInt(hashh);
                	int periodic_id=Integer.parseInt(jobz.substring(0,jobz.indexOf(",")));
                	SAnode my_sanode=null;
                	Iterator<SAnode> i =Main.my_salist.iterator();
                	while (i.hasNext()){
                		my_sanode=i.next();
                		if (my_sanode.get_hash()==my_hash){
                			found_sa=true;
                			break;
                		}
                	}
                	if(found_sa){ //if SA was found, stop the periodic job.
                			my_sanode.add_periodicjobstop(periodic_id);
                			respons="Job stop msg will be sent to SA: "+my_hash;
                			System.out.println(respons);
                			JSONObject jobresp= new JSONObject();
						       jobresp.put("hash",hashh);
						       jobresp.put("responss", respons);
						       jresponse.add(jobresp);
                		}
                		else{//No SA found with the hash given.
                			respons="There isnt a SA with hash: "+my_hash;
                			System.out.println("There isnt a SA: "+my_hash);
                			JSONObject jobresp= new JSONObject();
						       jobresp.put("hash",hashh);
						       jobresp.put("responss", respons);
						       jresponse.add(jobresp);
                		}
			    	
			    	}
			    	else { //New job for a SA.
			    		boolean found_sa=false;
                		int my_hash=Integer.parseInt(hashh);
                		SAnode my_sanode=null;
                		Iterator<SAnode> i =Main.my_salist.iterator();
                		while (i.hasNext()){
                			my_sanode=i.next();
                			if (my_sanode.get_hash()==my_hash){
                				found_sa=true;
                				break;
                			}
                		}
                		if(found_sa){ //If found SA with hash
                			String mytxt=jobz;
                    		String[] line_array=mytxt.split("\n");
                    		for(String line: line_array){
                    			int id=mysqlconnector.insertautonmapjob(line, my_hash); //Insert new job with autoIncrement ID
                    			if(id>0){
                    				my_sanode.add_guijobs(id,line);
                    			}
                    		}
                    		respons="Jobs will be sent to SA: "+my_hash;
                			System.out.println(respons);
                			JSONObject jobresp= new JSONObject();
						    jobresp.put("hash",hashh);
						    jobresp.put("responss", respons);
						    jresponse.add(jobresp);
                		}
                		else{ //No SA was found with the hash given
                			respons="There isnt a SA with hash: "+my_hash;
                			System.out.println(respons);
                			JSONObject jobresp= new JSONObject();
						    jobresp.put("hash",hashh);
						    jobresp.put("responss", respons);
						    jresponse.add(jobresp);
                		}
			    	}
			    }
		    }catch (Exception e){
			    e.printStackTrace();
			    JSONObject jobresp= new JSONObject();
			    respons="Something went wrong on the server on this hash,sent request sent may be invalid";
		        jobresp.put("hash",hashh);
		        jobresp.put("responss", respons);
		        jresponse.add(jobresp);
		        except=true;
		    }
		    if (except){ //If at least one job caused an exception
			    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(jresponse.toString()).build();
		    }
		    else { //Else
		        return Response.status(Status.OK).entity(jresponse.toString()).build();	
		    }
	}
    /**
     * Sending all jobs that have been given by server to SA's.
     * @return jarray with jsonobject {[hash],[job]} containing all jobs given for every SA. In case of exception return Internal_server_error
     */
    @GET
 	@Path("/getJobs")
 	@Produces(MediaType.APPLICATION_JSON)
    public Response SendJobs(){
    	System.out.println("Returning all Jobs");
    	Connection myconn=mysqlconnector.connect();
		PreparedStatement myPreState=null;
		String myQuery="select * from nmapjobs ";
    	JSONArray ja = new JSONArray();
    	try {
    		myPreState=myconn.prepareStatement(myQuery);
    		ResultSet rs=myPreState.executeQuery();
    		while (rs.next()){
    	       JSONObject jobb= new JSONObject();
    	       jobb.put("hash",Integer.toString(rs.getInt("SAgents_hash")));
    	       jobb.put("id",Integer.toString(rs.getInt("id")));
        	   jobb.put("job",rs.getString("job"));
        	   ja.add(jobb);
    		}
    		myPreState.close();
    		rs.close();
    		myconn.close();
    		}catch (SQLException e){
    			e.printStackTrace();
    			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Rekt").build();
    		}
    	return Response.status(Status.OK).entity(ja.toString()).build();
    }
}
