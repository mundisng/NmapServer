package com.kostas4949.mundis.nmapserver;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * 
 * Thread for periodically checking states of SA's and changing them.
 *
 */
public class SATimeChecker implements Runnable{

     private ConcurrentLinkedQueue<SAnode> SA;
     private int sleep;
     private SAnode my_sanode;
     public SATimeChecker (ConcurrentLinkedQueue<SAnode> SA,int sleeptime){
		this.SA=SA;
		this.sleep=sleeptime;
	}
	/**
	 * Actual job is done here.
	 */
	public void run () {
		try {
		while (true){
			Iterator<SAnode> i =SA.iterator();
			 while (i.hasNext()){  //check for every SANode
				 my_sanode=i.next();
				 long checkTime = System.nanoTime();
			     if ((checkTime-my_sanode.GetLastTimeOfRequest())>3000000000L*sleep && my_sanode.GetActiveSA()==true){ //if more than 3xGivenTime have passed since last request for jobs
			        System.out.println("Changing SA state");
			        my_sanode.SetActiveSA(false);//if it has, change active state to false
			        mysqlconnector.changesqlactivesagents(my_sanode.get_hash(), 0);
			     }
			 }
		Thread.sleep(1000*sleep);
	}
		}catch(InterruptedException ex){
			System.out.println("Tried to interrupt TimeChecker Thread");
		}
	}
}
