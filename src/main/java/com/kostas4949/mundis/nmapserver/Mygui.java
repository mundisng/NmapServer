package com.kostas4949.mundis.nmapserver;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import javax.swing.JTabbedPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Iterator;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.JScrollPane;
import javax.swing.table.*;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
/**
 * 
 * Main gui with 3 tabs that displays the results
 *
 */
public class Mygui extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel showagents_tableModel;
	private DefaultTableModel showresults_tableModel;
	private JTextField fromTime;
	private JTextField toTime;
	private JTable table_1;
	private JTextField satotime;
	private JTextField safromtime;
	private JTextField hashTextField;
	private JTextField textFieldstopSA;
	private JTextField textFieldstopPeriodic;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Mygui frame = new Mygui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Create the frame.
	 */
	public Mygui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 790, 570);
		contentPane.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("SAgent", null, panel, null);
		panel.setLayout(null);
		
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 62, 761, 469);
		panel.add(scrollPane);
		
		satotime = new JTextField();
		satotime.setBounds(561, 12, 114, 38);
		panel.add(satotime);
		satotime.setColumns(10);
		
		safromtime = new JTextField();
		safromtime.setBounds(350, 12, 114, 38);
		panel.add(safromtime);
		safromtime.setColumns(10);
		
		hashTextField = new JTextField();
		hashTextField.setBounds(77, 12, 161, 38);
		panel.add(hashTextField);
		hashTextField.setColumns(10);
		
		final JButton btnShow = new JButton("Show");
		btnShow.setFont(new Font("Dialog", Font.BOLD, 15));
		btnShow.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent e) {
				btnShow.setEnabled(false);
				hashTextField.setEnabled(false);
				safromtime.setEnabled(false);
				satotime.setEnabled(false);
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                    	if(Main.database_mode){	// results from database
                			showagents_tableModel=new DefaultTableModel();
                			table = new JTable(showagents_tableModel);
                			scrollPane.setViewportView(table);
                			Connection myconn=mysqlconnector.connect();
                			PreparedStatement myPreState=null;
                			ResultSet myResS=null;
                			String myQuery;
                			try {
                				if(hashTextField.getText().isEmpty()){
                					myQuery="select * from SAgents ";
                					myPreState=myconn.prepareStatement(myQuery);
                				}
                				else{
                					int my_hash=Integer.parseInt(hashTextField.getText());
                					if((safromtime.getText().isEmpty()) && (satotime.getText().isEmpty())){
                						myQuery="select * from nmapresults where nmapjobs_SAgents_hash=? ";
                						myPreState=myconn.prepareStatement(myQuery);
                						myPreState.setInt(1, my_hash);
                					}
                					else if(safromtime.getText().isEmpty()){
                						myQuery="select * from nmapresults where time<=? and nmapjobs_SAgents_hash=? ";
                						myPreState=myconn.prepareStatement(myQuery);
                						myPreState.setString(1,satotime.getText());
                						myPreState.setInt(2, my_hash);
                					}
                					else if(satotime.getText().isEmpty()){
                						myQuery="select * from nmapresults where time>=? and nmapjobs_SAgents_hash=? ";
                						myPreState=myconn.prepareStatement(myQuery);
                						myPreState.setString(1,safromtime.getText());
                						myPreState.setInt(2, my_hash);
                					}
                					else{
                						myQuery="select * from nmapresults where time>=? and time<=? and nmapjobs_SAgents_hash=? ";
                						myPreState=myconn.prepareStatement(myQuery);
                						myPreState.setString(1,safromtime.getText());
                						myPreState.setString(2,satotime.getText());
                						myPreState.setInt(3, my_hash);
                					}
                				}
                				myResS=myPreState.executeQuery();
                				ResultSetMetaData metaData = myResS.getMetaData();
                				// Names of columns
                				Vector<String> columnNames = new Vector<String>();
                				int columnCount = metaData.getColumnCount();
                				for (int i = 1; i <= columnCount; i++) {
                					columnNames.add(metaData.getColumnName(i));
                				}
                				// Data of the table
                				Vector<Vector<Object>> data = new Vector<Vector<Object>>();
                				while (myResS.next()) {
                					Vector<Object> vector = new Vector<Object>();
                					for (int i = 1; i <= columnCount; i++) {
                						vector.add(myResS.getObject(i));
                					}
                					data.add(vector);
                				}
                				showagents_tableModel.setDataVector(data, columnNames);
                			}catch (Exception e){
                				System.out.println("load exception: "+e);
                				JOptionPane.showMessageDialog(null,e);
                			}finally{
                				try {
                					myPreState.close();
                					myResS.close();
                					myconn.close();
                				} catch (Exception e) {
                					System.out.println("load_finally exception: "+e);
                					e.printStackTrace();
                				}
                			}
                		}
                		else{	//results from cache
                			if(hashTextField.getText().isEmpty()){
                				String col[]={"hash","device_name","ip","macaddr","os_version","nmap_version","active"};
                				showagents_tableModel=new DefaultTableModel(col, 0);
                				table= new JTable(showagents_tableModel);
                				scrollPane.setViewportView(table);
                				SAnode myn=null;
                				Iterator<SAnode> i=Main.my_salist.iterator();
                				while(i.hasNext()){
                					myn=i.next();
                					Object[] objs={myn.get_hash(),myn.GetDeviceName(),myn.GetIP(),myn.GetMAC(),myn.GetOSVersion(),myn.GetNmapVersion(),myn.GetActiveSA()};
                					showagents_tableModel.addRow(objs);
                				}
                			}
                			else{
                				SAResults myr=null;
                				Iterator<SAResults> i=Main.results.iterator();
                				int my_hash=Integer.parseInt(hashTextField.getText());//try catch
                				String col[]={"hash","id","time","hostname","tasks","results","periodic"};
                				showagents_tableModel=new DefaultTableModel(col, 0);
                				table= new JTable(showagents_tableModel);
                				scrollPane.setViewportView(table);
                				if((safromtime.getText().isEmpty()) && (satotime.getText().isEmpty())){
                            		while(i.hasNext()){
                            			myr=i.next();
                            			if(myr.GetHash()==my_hash){
                            				Object[] objs={my_hash,myr.GetId(),myr.GetTime(),myr.GetHostname(),myr.GetTask(),myr.GetResult(),myr.GetPeriodic()};
                            				showagents_tableModel.addRow(objs);
                            			}
                            		}
                				}
                				else if(safromtime.getText().isEmpty()){
                            		while(i.hasNext()){
                            			myr=i.next();
                            			if(myr.GetHash()==my_hash && myr.GetTime().compareTo(satotime.getText())<=0){
                            				Object[] objs={my_hash,myr.GetId(),myr.GetTime(),myr.GetHostname(),myr.GetTask(),myr.GetResult(),myr.GetPeriodic()};
                            				showagents_tableModel.addRow(objs);
                            			}
                            		}
                				}
                				else if(satotime.getText().isEmpty()){
                            		while(i.hasNext()){
                            			myr=i.next();
                            			if(myr.GetHash()==my_hash && myr.GetTime().compareTo(safromtime.getText())>=0){
                            				Object[] objs={my_hash,myr.GetId(),myr.GetTime(),myr.GetHostname(),myr.GetTask(),myr.GetResult(),myr.GetPeriodic()};
                            				showagents_tableModel.addRow(objs);
                            			}
                            		}
                				}
                				else{
                            		while(i.hasNext()){
                            			myr=i.next();
                            			if(myr.GetHash()==my_hash && myr.GetTime().compareTo(safromtime.getText())>=0 && myr.GetTime().compareTo(satotime.getText())<=0){
                            				Object[] objs={my_hash,myr.GetId(),myr.GetTime(),myr.GetHostname(),myr.GetTask(),myr.GetResult(),myr.GetPeriodic()};
                            				showagents_tableModel.addRow(objs);
                            			}
                            		}
                				}
                			}
                		}
                        return null;
                    }
                }.execute();
                btnShow.setEnabled(true);
                hashTextField.setEnabled(true);
                safromtime.setEnabled(true);
                satotime.setEnabled(true);
            }
        });
		btnShow.setBounds(687, 12, 86, 37);
		panel.add(btnShow);
		
		JLabel lblsatotime = new JLabel("to time");
		lblsatotime.setBounds(482, 23, 61, 15);
		panel.add(lblsatotime);
		
		JLabel lblsafromtime = new JLabel("from time");
		lblsafromtime.setBounds(256, 23, 76, 15);
		panel.add(lblsafromtime);
		
		JLabel lblhash = new JLabel("hash");
		lblhash.setBounds(12, 23, 47, 15);
		panel.add(lblhash);
		
		JPanel panelresults = new JPanel();
		tabbedPane.addTab("Results", null, panelresults, null);
		panelresults.setLayout(null);
		
		fromTime = new JTextField();
		fromTime.setBounds(177, 12, 162, 35);
		panelresults.add(fromTime);
		fromTime.setColumns(10);
		
		toTime = new JTextField();
		toTime.setBounds(449, 12, 156, 35);
		panelresults.add(toTime);
		toTime.setColumns(10);
		
		final JScrollPane scrollPane_results = new JScrollPane();
		scrollPane_results.setBounds(12, 59, 761, 472);
		panelresults.add(scrollPane_results);
		
		final JButton btnShowResults = new JButton("Show Results");
		btnShowResults.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	btnShowResults.setEnabled(false);
            	fromTime.setEnabled(false);
            	toTime.setEnabled(false);
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                    	if(Main.database_mode){	//results from database
                    		showresults_tableModel=new DefaultTableModel();
                    		table_1 = new JTable(showresults_tableModel);
                    		scrollPane_results.setViewportView(table_1);
                    		Connection myconn=mysqlconnector.connect();
                    		PreparedStatement myPreState=null;
                    		ResultSet myResS=null;
                    		String myQuery;
                    		try {
                    			if((fromTime.getText().isEmpty()) && (toTime.getText().isEmpty())){
                    				myQuery="select * from nmapresults ";
                    				myPreState=myconn.prepareStatement(myQuery);
                    			}
                    			else if(fromTime.getText().isEmpty()){
                    				myQuery="select * from nmapresults where time<=? ";
                    				myPreState=myconn.prepareStatement(myQuery);
                    				myPreState.setString(1,toTime.getText());
                    			}
                    			else if(toTime.getText().isEmpty()){
                    				myQuery="select * from nmapresults where time>=? ";
                    				myPreState=myconn.prepareStatement(myQuery);
                    				myPreState.setString(1,fromTime.getText());
                    			}
                    			else{
                    				myQuery="select * from nmapresults where time>=? and time<=? ";
                    				myPreState=myconn.prepareStatement(myQuery);
                    				myPreState.setString(1,fromTime.getText());
                    				myPreState.setString(2,toTime.getText());
                    			}
                    			myResS=myPreState.executeQuery();
                    			ResultSetMetaData metaData = myResS.getMetaData();
                    			// Names of columns
                    			Vector<String> columnNames = new Vector<String>();
                    			int columnCount = metaData.getColumnCount();
                    			for (int i = 1; i <= columnCount; i++) {
                    				columnNames.add(metaData.getColumnName(i));
                    			}
                    			// Data of the table
                    			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
                    			while (myResS.next()) {
                    				Vector<Object> vector = new Vector<Object>();
                    				for (int i = 1; i <= columnCount; i++) {
                    					vector.add(myResS.getObject(i));
                    				}
                    				data.add(vector);
                    			}
                    			showresults_tableModel.setDataVector(data, columnNames);
                    		}catch (Exception e){
                    			System.out.println("load exception: "+e);
                    			JOptionPane.showMessageDialog(null,e);
                    		}finally{
                    			try {
                    				myPreState.close();
                    				myResS.close();
                    				myconn.close();
                    			} catch (Exception e) {
                    				System.out.println("load_finally exception: "+e);
                    				e.printStackTrace();
                    			}
                    		}
                    	}
                    	else{	//results from cache
                    		String col[]={"hash","id","time","hostname","tasks","results","periodic"};
                    		showresults_tableModel=new DefaultTableModel(col, 0);
                    		table_1 = new JTable(showresults_tableModel);
                    		scrollPane_results.setViewportView(table_1);
                    		SAResults myr=null;
                    		Iterator<SAResults> i=Main.results.iterator();
                    		if((fromTime.getText().isEmpty()) && (toTime.getText().isEmpty())){
                        		while(i.hasNext()){
                        			myr=i.next();
                        			Object[] objs={myr.GetHash(),myr.GetId(),myr.GetTime(),myr.GetHostname(),myr.GetTask(),myr.GetResult(),myr.GetPeriodic()};
                        			showresults_tableModel.addRow(objs);
                        		}
                			}
                			else if(fromTime.getText().isEmpty()){
                				while(i.hasNext()){
                					myr=i.next();
                					if(myr.GetTime().compareTo(toTime.getText())<=0){
                        				Object[] objs={myr.GetHash(),myr.GetId(),myr.GetTime(),myr.GetHostname(),myr.GetTask(),myr.GetResult(),myr.GetPeriodic()};
                        				showresults_tableModel.addRow(objs);
                        			}
                				}
                			}
                			else if(toTime.getText().isEmpty()){
                				while(i.hasNext()){
                					myr=i.next();
                					if(myr.GetTime().compareTo(fromTime.getText())>=0){
                        				Object[] objs={myr.GetHash(),myr.GetId(),myr.GetTime(),myr.GetHostname(),myr.GetTask(),myr.GetResult(),myr.GetPeriodic()};
                        				showresults_tableModel.addRow(objs);
                        			}
                				}
                			}
                			else{
                				while(i.hasNext()){
                					myr=i.next();
                					if((myr.GetTime().compareTo(fromTime.getText())>=0) && (myr.GetTime().compareTo(toTime.getText())<=0)){
                        				Object[] objs={myr.GetHash(),myr.GetId(),myr.GetTime(),myr.GetHostname(),myr.GetTask(),myr.GetResult(),myr.GetPeriodic()};
                        				showresults_tableModel.addRow(objs);
                        			}
                				}
                			}
                    	}
                        return null;
                    }
                }.execute();
                btnShowResults.setEnabled(true);
        		fromTime.setEnabled(true);
            	toTime.setEnabled(true);
            }
        });
		btnShowResults.setFont(new Font("Dialog", Font.BOLD, 15));
		btnShowResults.setBounds(617, 12, 156, 35);
		panelresults.add(btnShowResults);
		
		JLabel lblfromtime = new JLabel("from time");
		lblfromtime.setBounds(77, 22, 82, 15);
		panelresults.add(lblfromtime);
		
		JLabel lbltotime = new JLabel("to time");
		lbltotime.setBounds(357, 22, 74, 15);
		panelresults.add(lbltotime);
		
		JPanel panelstop = new JPanel();
		tabbedPane.addTab("Stop SA/periodic,add job", null, panelstop, null);
		panelstop.setLayout(null);
		
		textFieldstopSA = new JTextField();
		textFieldstopSA.setBounds(238, 36, 159, 33);
		panelstop.add(textFieldstopSA);
		textFieldstopSA.setColumns(10);
		
		final JButton btnStopPeriodic= new JButton("Stop Periodic of SA hash");;
		final JButton btnStopSA = new JButton("Stop SA");	
                    	         	
		btnStopSA.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				textFieldstopSA.setEnabled(false);
				btnStopSA.setEnabled(false);
				btnStopPeriodic.setEnabled(false);
				new SwingWorker<Void, Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						try{
							boolean found_sa=false;
							int my_hash=Integer.parseInt(textFieldstopSA.getText());
							SAnode my_sanode=null;
							Iterator<SAnode> i =Main.my_salist.iterator();
							while (i.hasNext()){
								my_sanode=i.next();
								if (my_sanode.get_hash()==my_hash){
									found_sa=true;
									break;
								}
							}
							if(found_sa){
								if(my_sanode.sa_stop_flag()){
									System.out.println("Stop msg is already (being) sent to SA: "+my_hash);
									JOptionPane.showMessageDialog(null,"Stop msg is already (being) sent to SA: "+my_hash);
								}
								else{
									my_sanode.stop_sa();
									System.out.println("Stop msg will be sent to SA: "+my_hash);
									JOptionPane.showMessageDialog(null,"Stop msg will be sent to SA: "+my_hash);
								}
							}
							else{
								System.out.println("There isnt a SA: "+my_hash);
								JOptionPane.showMessageDialog(null,"There isnt a SA: "+my_hash);
							}
						}catch(Exception e){
							System.out.println("Please enter a number! "+e);
							JOptionPane.showMessageDialog(null,"Please enter a number!");
						}
						return null;
					}
				}.execute();
				textFieldstopSA.setEnabled(true);
				btnStopSA.setEnabled(true);
				btnStopPeriodic.setEnabled(true);
			}
		});
		btnStopSA.setBounds(460, 36, 207, 33);
		panelstop.add(btnStopSA);
		
		textFieldstopPeriodic = new JTextField();
		textFieldstopPeriodic.setBounds(238, 130, 159, 33);
		panelstop.add(textFieldstopPeriodic);
		textFieldstopPeriodic.setColumns(10);  
		
		btnStopPeriodic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				textFieldstopSA.setEnabled(false);
				btnStopSA.setEnabled(false);
				btnStopPeriodic.setEnabled(false);
				textFieldstopPeriodic.setEnabled(false);
				new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                    	try{
                    		boolean found_sa=false;
                    		int my_hash=Integer.parseInt(textFieldstopSA.getText());
                    		int periodic_id=Integer.parseInt(textFieldstopPeriodic.getText());
                    		SAnode my_sanode=null;
                    		Iterator<SAnode> i =Main.my_salist.iterator();
                    		while (i.hasNext()){
                    			my_sanode=i.next();
                    			if (my_sanode.get_hash()==my_hash){
                    				found_sa=true;
                    				break;
                    			}
                    		}
                    		if(found_sa){
                    			my_sanode.add_periodicjobstop(periodic_id);
                    			System.out.println("Job stop msg will be sent to SA: "+my_hash);
                    			JOptionPane.showMessageDialog(null,"Job stop msg will be sent to SA: "+my_hash);
                    		}
                    		else{
                    			System.out.println("There isnt a SA: "+my_hash);
                    			JOptionPane.showMessageDialog(null,"There isnt a SA: "+my_hash);
                    		}
                    	}catch(Exception e){
                    		System.out.println("Please enter numbers! "+e);
                    		JOptionPane.showMessageDialog(null,"Please enter numbers!");
                    	}
                    	return null;
                    }
				}.execute();
				textFieldstopSA.setEnabled(true);
				btnStopSA.setEnabled(true);
				btnStopPeriodic.setEnabled(true);
				textFieldstopPeriodic.setEnabled(true);
			}
		});
		btnStopPeriodic.setBounds(460, 130, 207, 33);
		panelstop.add(btnStopPeriodic);
		
		JLabel lblSAhashStop = new JLabel("SA hash");
		lblSAhashStop.setBounds(44, 36, 127, 33);
		panelstop.add(lblSAhashStop);
		
		JLabel lblperiodicStop = new JLabel("Periodic job id");
		lblperiodicStop.setBounds(44, 130, 127, 33);
		panelstop.add(lblperiodicStop);
		
		JLabel lblNmapJob = new JLabel("SA hash gets jobs");
		lblNmapJob.setBounds(147, 258, 127, 33);
		panelstop.add(lblNmapJob);  
		
		JScrollPane scrollPane_stop = new JScrollPane();
		scrollPane_stop.setBounds(44, 303, 365, 210);
		panelstop.add(scrollPane_stop);
		
		final JTextArea textArea = new JTextArea();
		scrollPane_stop.setViewportView(textArea);
		
		final JButton btnNmapJob = new JButton("Give Jobs to SA hash");
		btnNmapJob.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				textFieldstopSA.setEnabled(false);
				textArea.setEnabled(false);
				btnNmapJob.setEnabled(false);
				new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                    	try{
                    		boolean found_sa=false;
                    		int my_hash=Integer.parseInt(textFieldstopSA.getText());
                    		SAnode my_sanode=null;
                    		Iterator<SAnode> i =Main.my_salist.iterator();
                    		while (i.hasNext()){
                    			my_sanode=i.next();
                    			if (my_sanode.get_hash()==my_hash){
                    				found_sa=true;
                    				break;
                    			}
                    		}
                    		if(found_sa){
                    			String mytxt=textArea.getText();
                        		String[] line_array=mytxt.split("\n");
                        		for(String line: line_array){
                        			int id=mysqlconnector.insertautonmapjob(line, my_hash);
                        			if(id>0){
                        				my_sanode.add_guijobs(id,line);
                        			}
                        		}
                    			System.out.println("Jobs will be sent to SA: "+my_hash);
                    			JOptionPane.showMessageDialog(null,"Jobs will be sent to SA: "+my_hash);
                    		}
                    		else{
                    			System.out.println("There isnt a SA: "+my_hash);
                    			JOptionPane.showMessageDialog(null,"There isnt a SA: "+my_hash);
                    		}
                    	}catch(Exception e){
                    		System.out.println("Incorrect input (some jobs might have went through) "+e);
                    		JOptionPane.showMessageDialog(null,"Incorrent input (some jobs might have went through");
                    	}
                    	return null;
                    }
				}.execute();
				textFieldstopSA.setEnabled(true);
				textArea.setEnabled(true);
				btnNmapJob.setEnabled(true);
			}
		});
		btnNmapJob.setBounds(460, 388, 207, 33);
		panelstop.add(btnNmapJob);
		
	}
}
