import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
@SuppressWarnings("serial")

//Contains all the graphical work for the DARTassistant
//Uses an instant of assistant @as, passes information when necessary
//also receives updates to the JTextArea centerText through the method @getTextArea()
public class myFrame extends JFrame 
{
	//various initializations.  Also calls redirectSystemStreams()
	public myFrame(assistant asIn)
	{
		as = asIn;
		
		redirectSystemStreams();
	
		menuBar(); 
		rightPanel();
		centerPanel(); 	
				
	    setTitle("DART Assistant");
	    setSize(700, 500);
	    setLocationRelativeTo(null);
	    setDefaultCloseOperation(EXIT_ON_CLOSE); 
	    
        updateCourseList();
	}
	//the right panel includes the add class area, as well as a hopefully dynamic list of 
	//current classes being tracked.
	public void rightPanel()
	{
		JPanel rightPanel = new JPanel();
		
		//////////////////
        //add courses
		JPanel addCoursesPanel = new JPanel();      
        
        //textField for CRN Input
        final JTextField crnField = new JTextField(10);
        JLabel crnLabel = new JLabel("CRN: ");
        addCoursesPanel.add(crnLabel);
        addCoursesPanel.add(crnField);
        
        //add textfield for year
        final JTextField yearField = new JTextField(10);
        JLabel yearLabel = new JLabel("Year (YYYY): ");
        addCoursesPanel.add(yearLabel);
        addCoursesPanel.add(yearField);
        
        //radio buttons for semester (fall or spring)
        final JRadioButton fallButton = new JRadioButton("Fall");
        final JRadioButton springButton = new JRadioButton("Spring");
        
        ButtonGroup semester = new ButtonGroup();
        semester.add(fallButton);
        semester.add(springButton);
        
        JPanel radioPanel = new JPanel(new GridLayout(1, 0));
        radioPanel.add(fallButton);
        radioPanel.add(springButton);  
        
        addCoursesPanel.add(radioPanel);
        
        //submit button
        JButton submitButton = new JButton("Add Course");
        submitButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e)
            {
        		String term ="";
        		if(fallButton.isSelected())
        			term="10";
        		else
        			term="20";
        			
        		as.addClass(crnField.getText(), yearField.getText(), term);
        		
        		updateCourseList();
            }
        }); 
        
        addCoursesPanel.add(submitButton);
        
        //cleanup work on the addcourses panel
        addCoursesPanel.setBorder(
        		BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Add Courses"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        		
        addCoursesPanel.setPreferredSize(new Dimension(250, 170));
        rightPanel.add(addCoursesPanel, BorderLayout.NORTH);
		//////////////////

		//////////////////
        //List of courses being tracked
		JPanel courseListPanel = new JPanel();      

		JScrollPane courseListPane = new JScrollPane();
        courseListArea = new JTextArea();

        courseListArea.setLineWrap(true);
        courseListArea.setWrapStyleWord(true);

        courseListPane.getViewport().add(courseListArea);
        
        courseListPane.setPreferredSize(new Dimension(240, 210));  //TODO is there another way?
        courseListPanel.add(courseListPane, BorderLayout.NORTH);
        
        //cleanup work on the courseList panel
		courseListPanel.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Currently Tracked"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        
		courseListPanel.setPreferredSize(new Dimension(250, 250));
        rightPanel.add(courseListPanel, BorderLayout.SOUTH);
		//////////////////
        
        //TODO finish rightPanel
        rightPanel.setPreferredSize(new Dimension(250, 500));
        add(rightPanel, BorderLayout.EAST);
	}
	//displays current tracking info, is dynamically updated whenever anything calls
	//System.out, due to @redirectSystemStreams()
	public void centerPanel()
	{
        // display current tracking info
		JScrollPane pane = new JScrollPane();
        centerText = new JTextArea();

        centerText.setLineWrap(true);
        centerText.setWrapStyleWord(true);

        pane.getViewport().add(centerText);
        
        add(pane, BorderLayout.CENTER);
	}
	//menu bar, nuff said
	public void menuBar()
	{
		JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("File");
        
        JMenuItem about = new JMenuItem("About");
        about.setToolTipText("About DART Assistant");
        about.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		JOptionPane.showMessageDialog(null,
        			    "Hopefully this will help you dart good. And stuff.\n"+
        			    "Probably won't though.\n"+
        			    "I cannot be held responsible for any class you fail to get into.\n"+
        			    "Or fail for that matter.", 
        			    "About DART Assistant", JOptionPane.INFORMATION_MESSAGE);
        	}
        });
        file.add(about);
        
        JMenuItem exit = new JMenuItem("Exit");
        exit.setToolTipText("Exit application");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        file.add(exit);

        menubar.add(file);
        setJMenuBar(menubar);
	}
	//TODO updates courese list panel on the right
	//called when a new course is added and at init
	public void updateCourseList()
	{
		//courseListArea.append(temp);
	}
	//redirects all System.out to JTextArea centerText
	public void redirectSystemStreams() 
	{
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				centerText.append(String.valueOf((char) b));
		    }
		    @Override
		    public void write(byte[] b, int off, int len) throws IOException {
		    	centerText.append(new String(b, off, len));
		    }
		    @Override
		    public void write(byte[] b) throws IOException {
		        write(b, 0, b.length);
		    }
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}
	
	private assistant as;
	private JTextArea centerText;
	private JTextArea courseListArea;
}