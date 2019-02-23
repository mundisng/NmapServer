package com.kostas4949.mundis.nmapserver;

import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * Keeping all data for every SA.Functions as a cache too.
 *
 */
public class SAnode {
	private final ConcurrentLinkedQueue <String> myperiodicjobstops;
	private final ConcurrentLinkedQueue<SAjobs> alljobs;
	private final ConcurrentLinkedQueue<SAjobs> guijobs;
	private final int SA_hash;
	private boolean guistop;
	private final String device_name,interface_ip,interface_mac,os_version,nmap_version; 
	private boolean active;
	private long lastreq;
	/**
	 * Create SANode with all data for it.
	 * @param SA_hash The hash of the SA created.
	 * @param device The device name of the SA created.
	 * @param ip The ip of the SA created.
	 * @param mac The mac address of the SA created.
	 * @param os  The OS version of the SA created.
	 * @param nmap The nmap version of the SA created.
	 */
	public SAnode(int SA_hash,String device,String ip,String mac,String os,String nmap){
		this.myperiodicjobstops=new ConcurrentLinkedQueue<String>();
		this.alljobs=new ConcurrentLinkedQueue<SAjobs>();
		this.guijobs=new ConcurrentLinkedQueue<SAjobs>();
		this.SA_hash=SA_hash;
		this.guistop=false;
		this.device_name=device;
		this.interface_ip=ip;
		this.interface_mac=mac;
		this.os_version=os;
		this.nmap_version=nmap;
		this.active=true;
		this.lastreq=System.nanoTime();
	}
	/**
	 * 
	 * @return Device name of a certain SA.
	 */
	public String GetDeviceName(){
		return device_name;
	}
	/**
	 * 
	 * @return IP of a certain SA.
	 */
	public String GetIP(){
		return interface_ip;
	}
	/**
	 * 
	 * @return Mac Address of a certain SA.
	 */
	public String GetMAC(){
		return interface_mac;
	}
	/**
	 * 
	 * @return Os version of a certain SA.
	 */
	public String GetOSVersion(){
		return os_version;
	}
	/**
	 * 
	 * @return Nmap version of a certain SA.
	 */
	public String GetNmapVersion(){
		return nmap_version;
	}
	/**
	 * Sets active state of a certain SA.
	 * @param active 1 for active, 0 for not active.
	 */
	public void SetActiveSA(boolean active){
		this.active=active;
	}
	/**
	 * 
	 * @return Active state of a certain SA.
	 */
	public boolean GetActiveSA(){
		return active;
	}
	/**
	 * 
	 * @return All periodic jobs for this certain SA. 
	 */
	public ConcurrentLinkedQueue<String> get_myjobs(){
		return myperiodicjobstops;
	}
	/**
	 * 
	 * @return Hash of a certain SA.
	 */
	public int get_hash(){
		return SA_hash;
	}
	/**
	 * 
	 * @return True for stopping this SA, else false.
	 */
	public boolean sa_stop_flag(){
		return guistop;
	}
	/**
	 * Setting this SA to be stopped.
	 */
	public void stop_sa(){
		guistop=true;
	}
	/**
	 * Add job to certain SA for cache purposes.
	 * @param id Id of job.
	 * @param mys String of job.
	 */
	public void AddBasicJob(int id,String mys){
		alljobs.add(new SAjobs(id,mys));
	}
	/**
	 * 
	 * @return All jobs this SA has assigned to it.
	 */
	public ConcurrentLinkedQueue<SAjobs> GetAllJobs(){
		return alljobs;
	}
	/**
	 * Stops a periodic job based on it's id for this SA.
	 * @param periodic_id The id of this periodic job.
	 */
	public void add_periodicjobstop(int periodic_id){
		String mys=String.valueOf(periodic_id)+", Stop, true, periodic";
		myperiodicjobstops.add(mys);
	}
	public void add_guijobs(int id,String mys){
		guijobs.add(new SAjobs(id,mys));
	}
	public SAjobs guijobs_poll(){
		return guijobs.poll();
	}
	public boolean guijobs_isEmpty(){
		return guijobs.isEmpty();
	}
	/**
	 * Check if we have to stop any periodic jobs for this SA.
	 * @return True if we have periodic jobs to stop for this SA, else false.
	 */
	public boolean periodicjobstops_isEmpty(){
		
		return myperiodicjobstops.isEmpty();
	}
	/**
	 * 
	 * @return 1 periodic job we need to stop for this SA and removes it from the list too.
	 */
	public String periodicjobstops_poll(){
		return myperiodicjobstops.poll();
	}
	/**
	 * Set last time this SA made a request for jobs.
	 * @param time Time in nanoseconds since last request.
	 */
	public void SetLastTimeOfRequest(long time){
		this.lastreq=time;
	}
	/**
	 * 
	 * @return Time in nanoseconds of last job request from this SA.
	 */
	public long GetLastTimeOfRequest(){
		return lastreq;
	}
}
