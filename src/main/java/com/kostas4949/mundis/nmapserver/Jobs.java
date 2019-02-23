package com.kostas4949.mundis.nmapserver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.Date;
import java.util.Iterator;
/**
 * 
 * Sends jobs and accepts results on /jobs url.
 *
 */
@Path("jobs")
public class Jobs {
	private static Random randomG=new Random();
	   /**
	    * Sending jobs.
	    * @param hash Hash of SA asking for jobs.
	    * @return Status.OK in all cases, with different messages attached depending on the result(Asked from GUI to stop SA for example).
	    */
	@POST
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public Response SendJobs(String hash){
		int randomI=0,size=0;
		boolean correcthash=false;
		boolean sa_stop=false;
		randomI=randomG.nextInt(2)+1;  //Send up to 2 jobs to client.
		int my_hash=Integer.parseInt(hash);
		System.out.println("Hash "+hash+" is requesting jobs" );
		SAnode my_sanode=null; 
			Iterator<SAnode> ii =Main.my_salist.iterator();   //Search in our SANode cache for the correct node for this SA for editing the cache in the future.
		    while (ii.hasNext()){
		    	my_sanode=ii.next();
			    if (my_sanode.get_hash()==my_hash){
			    	correcthash=true;
			    	if(my_sanode.sa_stop_flag()){
			    		sa_stop=true;
			    	}
			    	break;
			    }
		    }
		if(correcthash){
			my_sanode.SetLastTimeOfRequest(System.nanoTime());   //Set this moment as last request from this SA in nano
		    my_sanode.SetActiveSA(true);                        //Set SA state as active:true in cache
		    mysqlconnector.changesqlactivesagents(my_hash, 1); //Set SA state as active:true  in database
		}
		if (!sa_stop){                                     //if we weren't asked to stop sa from gui
			if(Main.inputfile_flag){                       //if we are using inputjobs.txt
		if (!Main.EOF){                                    //If we haven't reached the EOF for inputjobs.txt yet
		   ArrayList<String>list=Main.re.readthefile(randomI);
		      JSONObject jobb= new JSONObject();  //Create JsonObject to send as a response
		      size=list.size();
		      if (size<randomI){
			     Main.EOF=true;
			     System.out.println("We have reached the EOF from now on");
		      }
		      int i;
		      for (i=0; i<size; i++){
			  try{
				int id=mysqlconnector.insertautonmapjob(list.get(i), my_hash);
				if(id>0){
					my_sanode.AddBasicJob(id, list.get(i));
					String job=Integer.toString(id)+","+list.get(i);
					jobb.put(i,job);   //Send whole job string to SA with AutoIncrement ID as id
				}
			  }catch(Exception e){
				  System.out.println("something went wrong"+e);
			  }
		      }
		      list.clear();
		      while(!my_sanode.guijobs_isEmpty()){   //Add jobs given from gui
		    	  SAjobs guijob=my_sanode.guijobs_poll();
		    	  String mys=String.valueOf(guijob.get_id())+","+guijob.get_mys();
		    	  my_sanode.AddBasicJob(guijob.get_id(),guijob.get_mys()); //insert cache
		    	  jobb.put(i,mys);
		    	  i++;
		      }
		      while(!my_sanode.periodicjobstops_isEmpty()){  //Send message to stop a certain periodic thread we were asked
		    	  jobb.put(i,my_sanode.periodicjobstops_poll()); 
		    	  i++;
		      }
		      return Response.status(Status.OK).entity(jobb.toString()).build();  //Send the response as jsonobject.tostring()
	    }
	    else {  //if we are eof don't send any new jobs except if asked by gui
	    	System.out.println("EOF has already been reached");
	    	JSONObject jobb= new JSONObject();   //Create JsonObject to send as a response
	    	int i = 0;
	    	while(!my_sanode.guijobs_isEmpty()){  //Add jobs given from gui
		    	  SAjobs guijob=my_sanode.guijobs_poll();
		    	  String mys=String.valueOf(guijob.get_id())+","+guijob.get_mys();
		    	  my_sanode.AddBasicJob(guijob.get_id(),guijob.get_mys());  //cache
		    	  jobb.put(i,mys);
		    	  i++;
		     }
	    	while(!my_sanode.periodicjobstops_isEmpty()){ //Stop a periodic thread of this SA
		    	  jobb.put(i,my_sanode.periodicjobstops_poll()); 
		    	  i++;
		     }
	    	return Response.status(Status.OK).entity(jobb.toString()).build();
	   }
		}
		else{
			JSONObject jobb= new JSONObject();
	    	int i = 0;
	    	while(!my_sanode.guijobs_isEmpty()){ //Add jobs given from gui
		    	  SAjobs guijob=my_sanode.guijobs_poll();
		    	  String mys=String.valueOf(guijob.get_id())+","+guijob.get_mys();
		    	  my_sanode.AddBasicJob(guijob.get_id(),guijob.get_mys());
		    	  jobb.put(i,mys);
		    	  i++;
		     }
	    	while(!my_sanode.periodicjobstops_isEmpty()){ //Stop a periodic thread of this SA
		    	  jobb.put(i,my_sanode.periodicjobstops_poll()); 
		    	  //GOT TO REMOVE THE STOPPED PERIODIC JOB FROM DATABASE OF JOBS.
		    	  i++;
		      }
	    	return Response.status(Status.OK).entity(jobb.toString()).build();
		}
		}
		else { //We have to send a job to stop the SA
			JSONObject jobb= new JSONObject();
			System.out.println("Make hash : "+hash+"stop");
			jobb.put(0,"-1, exit(0), true, -1");
			return Response.status(Status.OK).entity(jobb.toString()).build();
		}
	}
	/**
	 * Get results, use a xml parser on them and get the useful data from the xml result sent.
	 * @param results Results in JSON.tostring() format.
	 * @return OK in any case, just to state that the results were accepted.
	 */
	@POST
	@Path("/post")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response AcceptResults(String results){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
	    String purexmlstring,hostname,timesstamp,task,tasktemp,result,hashz;
	    String xmlstring[];
	    int periodic,jobid;
	    ArrayList<String> allresults=new ArrayList<String>();
		try {
			JSONObject jjson = (JSONObject)new JSONParser().parse(results); 
			for (int zz=0; zz<jjson.size(); zz++){  //for as many results as we got
				task=null;
				hostname=null;
				result=null;
			    xmlstring=jjson.get(Integer.toString(zz)).toString().split("\\n"); //Results are split in multiple lines
			    timesstamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
			    hashz=xmlstring[0]; //First line has client hash
			    periodic=Integer.parseInt(xmlstring[1]); //Second line shows if job is periodic or not
			    jobid=Integer.parseInt(xmlstring[2]);//Third line gives the id of the job we got results from
			    purexmlstring=jjson.get(Integer.toString(zz)).toString();
			    purexmlstring=purexmlstring.substring(purexmlstring.indexOf('\n')+1);
			    purexmlstring=purexmlstring.substring(purexmlstring.indexOf('\n')+1);;
			    purexmlstring=purexmlstring.substring(purexmlstring.indexOf('\n')+1);;//4th line till eof is xml result*/
			    System.out.println("THIS IS THE XML STRING: "+purexmlstring);
			    XmlParser xml= new XmlParser(purexmlstring); //Edit the xml file and get the useful data
				Main.results.add(new SAResults(jobid,periodic,xml.getHostname(),timesstamp,xml.getResult(),Integer.parseInt(hashz),xml.getTasks())); //Add the useful data to cache
				mysqlconnector.insertsqlresults(jobid,xml.getHostname(),timesstamp,xml.getTasks(),xml.getResult(),periodic,Integer.parseInt(hashz)); //Add the useful data to the database
				allresults.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(200).build();
	}
}