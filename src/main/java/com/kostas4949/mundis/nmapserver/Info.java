package com.kostas4949.mundis.nmapserver;

import javax.swing.JOptionPane;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 * 
 * Everything needed for SAs to register with the server.
 *
 */
@Path("info")
public class Info {
	/**
	 * 
	 * @param resp Has all credentials the SA sent.
	 * @return Either Status.OK for accepting the registration, Status.Forbidden if registration was not accepted or internal_server_error if something went wrong with the server processing the request.
	 */
@POST
@Path("/post")	
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
	public Response Accepted(String resp){
	
		try {
			JSONObject jjson = (JSONObject)new JSONParser().parse(resp);
			String hash=jjson.get("Hash").toString(); //Get string SA sent.
			int reply=JOptionPane.showConfirmDialog(null,"Do you want to accept client with hash "+hash+"?","Client Request", JOptionPane.YES_NO_OPTION);
	        if(reply==JOptionPane.YES_OPTION){
	        	System.out.println("Pressed yes!");
	        	System.out.println("Accepted information from hash : " + hash);
	        	String device=jjson.get("hostname").toString();
	        	String IP=jjson.get("IP").toString();
	        	String MAC=jjson.get("Mac").toString();
	        	String OS=jjson.get("OS").toString();
	        	String nmapver=jjson.get("Nmap").toString();
	        	Main.my_salist.add(new SAnode(Integer.parseInt(hash),device,IP,MAC,OS,nmapver)); //Add this SA to SA cache
	        	mysqlconnector.insertsqlsagents(Integer.parseInt(hash), device, IP, MAC, OS, nmapver); //Add this SA to sql database
	        	String result="Accepted information!";
	        	return Response.status(Status.OK).entity(result).build();
	        }
	        else{
	        	System.out.println("Pressed no or X!");
	        	System.out.println("Declined information from hash : " + hash);
	        	return Response.status(Status.FORBIDDEN).entity("Access Denied").build();
	        }
		} catch (ParseException e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Server Error").build();
		}
	}
}