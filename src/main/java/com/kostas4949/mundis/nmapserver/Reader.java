package com.kostas4949.mundis.nmapserver;

import java.io.*;
import java.util.*;
/**
 * Reads a file till eof.
 * 
 *
 */
public class Reader {
	private String line,filetoread;
	private int i;
	private BufferedReader mybr;
	private ArrayList<String> list = new ArrayList<String>();
	/**
	 * Constructor of reader, taking 1 parameter.
	 * @param filetoread String of file to be read.
	 */
	public Reader(String filetoread){        //Create BufferedReader item for file given
		this.filetoread=filetoread;
		try{
			this.mybr=new BufferedReader(new FileReader(this.filetoread));
		}catch(IOException e){
			System.out.println("Thrown exception inputjobsfile: "+e);
			System.exit(3);
		}
	}
	/**
	 * Reads the file that was given.
	 * @param linesgiven Amount of lines to read from the file.
	 * @return Returns array of strings that have been read.
	 */
	public synchronized ArrayList<String> readthefile(int linesgiven){
		i=0;
		while(i<linesgiven){ //attempt to read linesgiven lines
			try {
				if ((line=mybr.readLine()) != null){   //If not EOF
					if (line.trim().length()>0){        //Get rid of whitespace lines
						list.add(line);
						System.out.println("The line (reader) is: "+line);
						i++;
					}
				}
				else{              //end of stream
					mybr.close();
					return list;
				}
			} catch (IOException e) {
				System.out.println("Thrown exception inputjobs readline: "+e);
				e.printStackTrace();
			}
		}
		return list;
	}
}
