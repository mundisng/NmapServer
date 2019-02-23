package com.kostas4949.mundis.nmapserver;
/**
 * 
 * All results we get from SAs.
 *
 */
public class SAResults {
	private final int id,periodic,hash;
	private final String hostname,time,jobresults,task;
	/**
	 * Creates result with certain data.
	 * @param id Id of job that produced this result
	 * @param periodic Periodicity of job that produced this result.
	 * @param hostname Hostname from result xml.
	 * @param time     Time result arrived to server.
	 * @param result   Ports that are open on target machine from result xml.
	 * @param hash     Hash of SA that sent this result
	 * @param task     Tasks that were done from the result xml.
	 */
	public SAResults(int id,int periodic,String hostname,String time,String result,int hash,String task){
		this.id=id;
		this.periodic=periodic;
		this.hostname=hostname;
		this.time=time;
		this.jobresults=result;
		this.hash=hash;
		this.task=task;
	}
	/**
	 * 
	 * @return Id of job that produced this result.
	 */
	public int GetId(){
		return id;
	}
	/**
	 * 
	 * @return Periodicity of job that produced this result.
	 */
	public int GetPeriodic(){
		return periodic;
	}
	/**
	 * 
	 * @return Hostname from xml result.
	 */
	public String GetHostname(){
		return hostname;
	}
	/**
	 * 
	 * @return Time this result entered the server.
	 */
	public String GetTime(){
		return time;
	}
	/**
	 *
	 * @return String with open ports from xml result.
	 */
	public String GetResult(){
		return jobresults;
	}
	/**
	 * 
	 * @return Hash of SA that produced this result.
	 */
	public int GetHash(){
		return hash;
	}
	/**
	 * 
	 * @return Tasks from xml result.
	 */
	public String GetTask(){
		return task;
	}
}
