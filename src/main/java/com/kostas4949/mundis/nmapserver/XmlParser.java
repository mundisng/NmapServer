package com.kostas4949.mundis.nmapserver;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
/**
 * 
 * Xml parser that gets the most important data from the xml.
 *
 */
public class XmlParser {
     private String tasktemp,task,hostname,result;
	/**
	 * 
	 * @param xml The xml in string format.
	 */
	public XmlParser(String xml){
		try {
	    ArrayList<String> allresults=new ArrayList<String>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    DocumentBuilder builder; 
		builder = factory.newDocumentBuilder();    //xmlparser
        Document document = builder.parse( new InputSource( new StringReader( xml) ) );
        document.getDocumentElement().normalize();   //Create Document from the xml string.
        NodeList nList = document.getElementsByTagName("taskbegin"); //Search for element taskbegin in xml document.
        if (!(nList.getLength()==0)){    //If there is at least 1 result
        for (int temp = 0; temp < nList.getLength(); temp++) {
        	Node nNode=nList.item(temp); 
        	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        		Element eElement = (Element) nNode;
        		tasktemp=eElement.getAttribute("task"); 
        		if (task==null){ //if first task, change null to first task
        			task=tasktemp;
        		}
        		else {
        		tasktemp=" "+tasktemp; //Create string with tasks seperated by 1 space
        		task=task+tasktemp;
        		}
        	}
        	
        }
        }
 
        nList = document.getElementsByTagName("hostname"); //Same as with taskbegin
        if (!(nList.getLength()==0)){
        	Node nNode = nList.item(0);
        	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        		Element eElement = (Element) nNode;
        		hostname=eElement.getAttribute("name");
        	}
        }
        //Same as with taskbegin
        	nList = document.getElementsByTagName("port");
        	if (!(nList.getLength()==0)){
        	for (int temp = 0; temp < nList.getLength(); temp++){
        		Node nNode = nList.item(temp);
        		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        			Element eElement = (Element) nNode;
        			result="Protocol: "+eElement.getAttribute("protocol");
        			result=result+" Port: "+eElement.getAttribute("portid");
        			NodeList nList2 = document.getElementsByTagName("state");
        			if (!(nList2.getLength()==0)){
        			   Node nNode2 = nList2.item(temp);
        			   if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
        			      Element eElement2 = (Element) nNode2;
        			      result=result+" State: "+ eElement2.getAttribute("state");
        			      result=result+" Reason: "+ eElement2.getAttribute("reason");
        			      }
        			   }
        			else {
        				result=result+" State: "+ "null";
        				result=result+" Reason: "+ "null";
        			}
        			NodeList nList3 = document.getElementsByTagName("service");
        			if (!(nList3.getLength()==0)){
        			   Node nNode3 = nList3.item(temp);
        			   if (nNode3.getNodeType() == Node.ELEMENT_NODE) {
        			      Element eElement3 = (Element) nNode3;
        			      result=result+" Service name: "+ eElement3.getAttribute("name");
        			     }
        			   }
        			else {
        				result=result+" Service name: "+ "null";
        			}
        			allresults.add(result);
        		}
        		
        	}
        	}
        	for (int temp=0; temp<allresults.size(); temp++){ //Change created arraylist of strings to 1 long string
            	if (result==null){
            		result=allresults.get(temp);
            	}
            	else {
            	result=result+allresults.get(temp);
            	}
            	result=result+"\n";
            }
		}catch (Exception e){
			e.printStackTrace();
			
		}
	}
	/**
	 * 
	 * @return Hostname stated in xml document.
	 */
	public String getHostname(){
		return hostname;
	}
	/**
	 * 
	 * @return Ports open states in xml document.
	 */
	public String getResult(){
		return result;
	}
	/**
	 * 
	 * @return Tasks stated in xml document.
	 */
	public String getTasks(){
		return task;
	}
		
}
