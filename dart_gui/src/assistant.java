import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.net.URL;

/*import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;*/

//assistant object stores all the information and processes it, scanning for course availability
//myFrame grabs information from the assistant through the various get methods
//and sends back in information as well.
public class assistant
{
	//Read in all settings and existing courseInfo from the appropriate text files
	public assistant()
	{		
		try {
			settings();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		readIn();
	}
	//update htmlArray with info from courseInfo.text
	public void readIn()
	{
		//read in the urls from the file and store for future use;
		try{				
			BufferedReader br = new BufferedReader(new FileReader("courseInfo.txt"));
			//if a file has been created but no classes added, still need to wait for CRNs to be added
			if(!br.ready())
			{
				//TODO this is moot because the stream hasn't been switched yet...
		        System.out.println("Welcome to Dart Assistant. Please input classes by CRN on the right to begin monitoring");
			}
			//read in the htmls from the .txt, store for further use
			htmlArray = new ArrayList<String>();
			while(br.ready())
			{
				htmlArray.add(br.readLine());
			}
			br.close();
		}
		//on the first run there will be no file, prompt to add classes
		catch(FileNotFoundException e){
	        System.out.println("Welcome to Dart Assistant. Please input classes by CRN on the right to begin monitoring");
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	//called from tester, go through htmlArray and send to analyze()
	public void scan()
	{
		System.out.println((((System.currentTimeMillis()-startTime)/60000))+" min");
		for(int i=0;i<htmlArray.size();i++)
		{
			try {
				analyze(htmlArray.get(i));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	
	//given the html of a course page, mine the desired course data
	//NOTICE:
	//If the assistant ever starts failing to find classes, the issue is probably here
	//the html of the page has probably been updated.
	public void analyze(String site) throws IOException
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
			if(line.equals(classSpotsDesignation)||line.equals(classSpotsDesignation2))
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
					/*String message = courseInfo+ "has "+spotsOpen+" spots open.";
					String recipient = "csjhin@gmail.com";
					try {
						postMail(recipient, "JavaEmail", message, recipient);
					} catch (MessagingException e) {
						e.printStackTrace();
					}
					*/
				}
				break;
			}
			line = reader.readLine(); 
		}
		
		reader.close();
		System.out.println();		
	}
	//add a course to track, given adequate information, called from myFrame
	public void addClass(String CRN, String year, String term)
	{	 
		 try {
			 //Construct the BufferedWriter object, because you are adding classes, you append
			 BufferedWriter bw = new BufferedWriter(new FileWriter("courseInfo.txt", true));
            	   			         
	         //code to convert CRN's to URLs
	         String html="https://wlsx-prod.nd.edu/reg/srch/ClassSearchServlet?CRN="+CRN+"&TERM="+year+term+"&P=1";
	         bw.write(html);
	       	 bw.newLine();	         
	         bw.close();
	         
	         System.out.println("Succesfully added CRN#: "+CRN+"\n");
		 } 
		 catch (IOException e) {
	            e.printStackTrace();
	     }
		 //update htmlArray
		 readIn();
	}
	//return the current classes being tracked for aesthetic info
	public String returnClasses()
	{
		String temp="";
		for(int i=0;i<htmlArray.size();i++)
		{
			temp.concat(htmlArray.get(i)+"\n");
		}
		return(temp);
	}
	//hopefully a method that can be used to send email notifications upon
	//the openening of a class spot
/*	public static void postMail(String recipient, String subject, String message , String from) throws MessagingException
	{
	     boolean debug = false;
	     Authenticator authenticator = new Authenticator() {
		     public PasswordAuthentication getPasswordAuthentication()
		     {  
		    	 return new PasswordAuthentication("cjhin", "4894jfdH");
		     }
	     };
	     /*
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
	   
	}*/
	//grab the settings from settings.txt
	public void settings() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("settings.txt"));
		
		//Time interval:
		timeInterval = Integer.valueOf(br.readLine().substring(14));
		
		//DEPRECATED for now, i might try to add in a setting to set the default
		//year/ term but as long as those are options... we'll see?
		/*//Year
		year = br.readLine().substring(5);
		//Term
		term = br.readLine().substring(5);*/
		br.close();
	}
	//return TimeInterval for use in scan Thread
	public int getTimeInterval()
	{
		return timeInterval;
	}

	private ArrayList<String> htmlArray;
	private long startTime=System.currentTimeMillis();
	private int timeInterval =1000;
	
	private static String classNameDesignation="<TR><TH CLASS=\"ddlabel\" scope=\"row\" >";
	private static String classSpotsDesignation="<TH CLASS=\"ddlabel\" scope=\"row\" colspan=\" 3 \"><SPAN class=fieldlabeltext>TOTAL</SPAN></TH>";
	private static String classSpotsDesignation2="<TH CLASS=\"ddlabel\" scope=\"row\" colspan=\" 2 \"><SPAN class=fieldlabeltext>TOTAL</SPAN></TH>";
}

