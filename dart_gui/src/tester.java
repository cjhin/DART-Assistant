import javax.swing.SwingUtilities;

//Chas Jhin
//DART asssistant will hopefully be used to help with darting.  
//And stuff.  Or something.  Yeah.
//also my first foray into threads.... I hope this works out ok...
public class tester
{	
	private static myFrame f;
	private static assistant as;
	
	public static void main(String[] a) throws Exception 
	{
		//create assistant to be passed into frame
  	  	as = new assistant();
				f = new myFrame(as);	
        f.setVisible(true);
        
        //though it might help for thread asynchronization, 
        //it significantly slows down printing to the center text area
        //I'm not sure if this has to do with the scanThread sleeping so the GUI keeps getting 
        //held up, or if there is another thread running that is stopping this from running
		/*SwingUtilities.invokeLater(new Runnable() {
	         public void run() {
	             f = new myFrame(as);	
        		as.redirectSystemStreams(f.getTextArea());
        		f.setVisible(true);
	         }
		}); */
		
		//Scan for openings and update the center text area with data
        //runs constantly, sleeping for the given time interval specified in settings.txt
        class scanThread extends Thread {
        	@SuppressWarnings("static-access")
			public void run() {
        		while(true){
        			as.scan();
        			try {
        				this.sleep(as.getTimeInterval());
      			  	} catch (InterruptedException e) {
      			  		e.printStackTrace();
      			  	}
      		  	}
      	  	}
        }
        Thread scan = new scanThread();
        scan.start(); 
        
        //TODO a thread for the right panel text area, that will update
        //the classes currently being monitored and is called.... only when the
        //add class button is pressed... or at run time?
	}
}