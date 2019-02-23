package com.kostas4949.mundis.nmapserver;
import org.apache.commons.codec.digest.DigestUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
/**
 * 
 * @author Nikos Gkountas and Konstantinos Skyvalos
 *Sets up grizzly server,connects to database to check login credentials in gui.
 */
public class Main {
	public final static AMPropertyReader propreader=new AMPropertyReader("src/main/resources/AMproject.properties");
	private static boolean incorrectlogin=true;   //properties from property file
	public static boolean database_mode=false;
	public static boolean inputfile_flag=false;
	public static volatile boolean EOF=false;
	public static final ConcurrentLinkedQueue<SAnode> my_salist=new ConcurrentLinkedQueue<SAnode>();
	public static final ConcurrentLinkedQueue<SAResults> results=new ConcurrentLinkedQueue<SAResults>();
	public static Reader re;
	public static HttpServer startServer() {  //Start grizzly http server on get_BASE_URI from property file
		final ResourceConfig rc = new ResourceConfig().packages("com.kostas4949.mundis.nmapserver");
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(propreader.get_BASE_URI()),rc);
	}
	private JFrame frame;
	/**
	 * Starts first window asking for credentials, reads from propertyfile options.
	 */
	public static void main(String[] args) throws IOException{
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();  //Create first gui window
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		while(incorrectlogin){
			try {
				Thread.sleep(1000*propreader.get_sleeptime());  //Keep showing login screen till correct credentials
			} catch (InterruptedException e) {
				System.out.println("Tried to interrupt my sleep");
				break;
			}
		}
		if(!propreader.get_Inputjobs().equals("null")){
			re=new Reader(propreader.get_Inputjobs());
			inputfile_flag=true;                        //we are using InputJobs.txt
		}
		if(propreader.get_databasemode().equals("true")){
			database_mode=true;                         //We are using database
		}
		ExecutorService ActiveSAChecker=Executors.newSingleThreadExecutor(); //Create Thread which periodically checks for NmapClient states and alters them in database and cache
		ActiveSAChecker.execute(new SATimeChecker(my_salist,propreader.get_sleeptime()));
        final HttpServer server = startServer(); //Start up grizzly server
		System.out.println("Grizzly server started at address " +propreader.get_BASE_URI()+ "\nHit enter to stop it...");
		System.in.read(); //Shutdown on enter
		server.shutdownNow();
	}
	private Connection myconn=null;
	private JTextField UsrnameTextField;
	private JPasswordField PwdPasswordField;
	/**
	 * Create first window and connect to database
	 */
	public Main() {
		propreader.readpropertyfile();
		initialize(); //Create gui windows
		myconn=mysqlconnector.connect();
	}
	/**
	 * Initialize the contents of the frame and in case of correct password proceed with opening main GUI window.
	 */
	private void initialize() {
		frame = new JFrame("Nmap Server");
		frame.setBounds(100, 100, 634, 389);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		//frame.setResizable(false);
		
		JLabel UsrnameLabel = new JLabel("Username");  //Username label
		UsrnameLabel.setFont(new Font("Dialog", Font.BOLD, 15));
		UsrnameLabel.setBounds(41, 45, 93, 25);
		frame.getContentPane().add(UsrnameLabel);
		
		UsrnameTextField = new JTextField();   //Username box for input
		UsrnameTextField.setBounds(167, 45, 351, 25);
		frame.getContentPane().add(UsrnameTextField);
		UsrnameTextField.setColumns(10);
		
		JLabel PwdLabel = new JLabel("Password"); //Password label
		PwdLabel.setFont(new Font("Dialog", Font.BOLD, 15));
		PwdLabel.setBounds(41, 143, 93, 18);
		frame.getContentPane().add(PwdLabel);
		
		PwdPasswordField = new JPasswordField(); //Password box for input
		PwdPasswordField.setBounds(167, 139, 351, 25);
		frame.getContentPane().add(PwdPasswordField);
		
		final JButton btnLoginButton = new JButton("Login");
		btnLoginButton.setFont(new Font("Dialog", Font.BOLD, 15));
		btnLoginButton.setBounds(266, 261, 112, 36);
		frame.getContentPane().add(btnLoginButton);
		btnLoginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnLoginButton.setEnabled(false);
				try {
					String myQuery="select * from user where username=? and password=? "; //Check if credentials are correct by connecting to the database
					PreparedStatement myPreState=myconn.prepareStatement(myQuery);
					myPreState.setString(1,UsrnameTextField.getText());
					String passwordgivenhashed = DigestUtils.sha256Hex(String.copyValueOf(PwdPasswordField.getPassword()));
					myPreState.setString(2,passwordgivenhashed);
					ResultSet myResS=myPreState.executeQuery();
					int counter=0;
					while(myResS.next()){
						counter++;
					}
					if(counter>=1){ //if correct credentials
						incorrectlogin=false;
						System.out.println("Username and Password are correct!");
						JOptionPane.showMessageDialog(null,"Username and Password are correct!");
						myconn.close(); //close connection to database
						frame.dispose();
						Mygui mygui=new Mygui(); //Create main GUI window
						mygui.setVisible(true);
					}
					else{
						System.out.println("Username and Password are incorrect!");
						JOptionPane.showMessageDialog(null,"Username and/or Password are incorrect!");
					}
					myPreState.close();
					myResS.close();
				}catch (Exception e){
					System.out.println("Login exception: "+e);
					JOptionPane.showMessageDialog(null,e);
				}
				btnLoginButton.setEnabled(true);
			}
		});
	}
}