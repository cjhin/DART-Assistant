import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Properties;
import java.net.URL;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class tester
{
	public static void main (String[] args) throws Exception
	{
		settings();
		
		//read in the urls from the file and send in to analyze();
		try{			
			
			BufferedReader br = new BufferedReader(new FileReader("courseInfo.txt"));
			//first run through the file will be empty, thus needs to be seeded with htmls
			if(!br.ready())
			{
				seed();
				System.out.println("Thank you.  Commencing monitoring of the DART system.\n");
				br = new BufferedReader(new FileReader("courseInfo.txt"));
			}
			//read in the htmls from the .txt, store for further use
			ArrayList<String> htmlArray = new ArrayList<String>();
			while(br.ready())
			{
				htmlArray.add(br.readLine());
			}
			running=true;
			while(running)
			{
				System.out.println(((System.currentTimeMillis()-startTime)/60000)+" minutes.");
				//check every minute.
				for(int i=0;i<htmlArray.size();i++)
				{
					analyze(htmlArray.get(i));
				}
				Thread.sleep(timeInterval);
			}
			br.close();
		}
		//on the first run there will be no file
		catch(FileNotFoundException e){
			seed();
		}	
	}
	
	//in the first run through of the program, the user is going to need to input the CRN(s)
	//this will be converted to the proper html link and then saved to a .txt file 
	public static void seed()
	{
		 
		 try {
            //Construct the BufferedWriter object
			 BufferedWriter bw = new BufferedWriter(new FileWriter("courseInfo.txt"));
            
	   		 Scanner in = new Scanner(System.in);
	   		 
	         System.out.println("Please Enter the CRN(s) of the course(s) you would like to track \n(type and enter 'Q' when finished):");
	         
	         String CRN = in.next();
	         String html ="";
	         while(!CRN.toUpperCase().equals("Q"))
	         {
	        	 html="https://was.nd.edu/reg/srch/ClassSearchServlet?CRN="+CRN+"&TERM="+year+term+"&P=1";
	        	 bw.write(html);
	        	 bw.newLine();
	        	 CRN=in.next();
	         }
	         bw.close();
		 } 
		 catch (IOException e) {
	            e.printStackTrace();
	     }
	}
	
	//given the html of a course page, mine the desired course data
	public static void analyze(String site) throws IOException
	{
		String courseInfo="";
		String spotsOpen="";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(site).openStream()));
		String line = reader.readLine();

		while (line != null) {
			//Mine the title and description of the class.
			//A user-friendly addition.
			if(line.equals(classNameDesignation))
			{
				line=reader.readLine();
				courseInfo=line.replaceAll("&nbsp;", "");
				System.out.println(courseInfo);
			}
			
			//Find the html code preceding the amount of spots max 
			//and available in the desired course and saves the data.
			if(line.equals(classSpotsDesignation))
			{
				reader.readLine();
				reader.readLine();
				line = reader.readLine();
				
				line = line.substring(4, line.length());
				spotsOpen = line.substring(0,line.indexOf("<"));
				
				if(spotsOpen.equals("0"))
				
					System.out.println("Sorry, there are no open slots at this time");	
				
				else
				{
					System.out.println("There are "+spotsOpen+" slots open in this class");
					String message = courseInfo+ "has "+spotsOpen+" spots open.";
					/*String recipient = "csjhin@gmail.com";
					try {
						postMail(recipient, "JavaEmail", message, recipient);
					} catch (MessagingException e) {
						e.printStackTrace();
					}
					*/
					if(stopOnOpening)
						running = false;
				}
				break;
			}
			line = reader.readLine(); 
		}

		System.out.println();
		
	}
	public static void postMail(String recipient, String subject, String message , String from) throws MessagingException
	{
	     boolean debug = false;
/*	     Authenticator authenticator = new Authenticator() {
		     public PasswordAuthentication getPasswordAuthentication()
		     {  
		    	 return new PasswordAuthentication("cjhin", "4894jfdH");
		     }
	     };*/
	     
	     //Set the host smtp address
	     Properties props = new Properties();
	      
	    // create some properties and get the default Session
	    Session session = Session.getDefaultInstance(props);
	    session.setDebug(debug);

	    // create a message
	    Message msg = new MimeMessage(session);

	    // set the from and to address
	    InternetAddress addressFrom = new InternetAddress(from);
	    msg.setFrom(addressFrom);

	    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

	    // Setting the Subject and Content Type
	    msg.setSubject(subject);
	    msg.setContent(message, "text/plain");
	    Transport.send(msg);   
	   
	}
	//grab the settings from settings.txt
	public static void settings() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("settings.txt"));
		
		//Stop on opening: 
		stopOnOpening = Boolean.getBoolean(br.readLine().substring(16));
		//Time interval:
		timeInterval = Integer.valueOf(br.readLine().substring(14));
		//Year
		year = br.readLine().substring(5);
		//Term
		term = br.readLine().substring(5);
		br.close();
	}
	
	private static long startTime=System.currentTimeMillis();
	private static boolean running;
	private static boolean stopOnOpening;
	private static int timeInterval =60000;
	private static String year;
	private static String term;
	
	private static String classNameDesignation="<TR><TH CLASS=\"ddlabel\" scope=\"row\" >";
	private static String classSpotsDesignation="<TH CLASS=\"ddlabel\" scope=\"row\" colspan=\" 3 \"><SPAN class=fieldlabeltext>TOTAL</SPAN></TH>";
}

