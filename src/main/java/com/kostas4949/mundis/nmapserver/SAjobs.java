package com.kostas4949.mundis.nmapserver;

/**
 * Class for nmap job list node
 * 
 *
 */
public class SAjobs {
	private final int id;
	private final String mys;
	/**
	 * Constructor of SAjobs, taking 2 parameters.
	 * @param id of the nmap job
	 * @param mys the rest of the nmap job
	 */
	public SAjobs(int id,String mys){
		this.id=id;
		this.mys=mys;
	}
	/**
	 * 
	 * @return id of a certain nmap job.
	 */
	public int get_id(){
		return id;
	}
	/**
	 * 
	 * @return mys the rest of an nmap job.
	 */
	public String get_mys(){
		return mys;
	}
}
