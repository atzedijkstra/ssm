package nl.uu.cs.ssmui;

import java.awt.Dimension;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import nl.uu.cs.ssm.Config;
import nl.uu.cs.ssm.HelpAccumulator;
import nl.uu.cs.ssm.HelpHTMLAccumulator;
import nl.uu.cs.ssm.HelpLaTeXAccumulator;

public class SSMHelp extends JFrame 
{
	private static final long serialVersionUID = 1L ;

	private abstract class AccumulatorFactory
	{
		public abstract HelpAccumulator make( boolean b, String f ) ;
	}
	
	private class HTMLAccumulatorFactory extends AccumulatorFactory
	{
		public HelpAccumulator make( boolean b, String f )
		{
			return new HelpHTMLAccumulator( b, f ) ;
		}
	}
	
	private Help helper ;
	
// IMPORTANT: Source code between BEGIN/END comment pair will be regenerated
// every time the form is saved. All manual changes will be overwritten.
// BEGIN GENERATED CODE
	// member declarations
	javax.swing.JScrollPane helpTextScrollPane = new javax.swing.JScrollPane();
	javax.swing.JEditorPane helpTextEditorPane = new javax.swing.JEditorPane();
	javax.swing.JPanel findPanel = new javax.swing.JPanel();
	javax.swing.JLabel topicLabel = new javax.swing.JLabel();
	javax.swing.JTextField topicTextField = new javax.swing.JTextField();
	javax.swing.JButton findButton = new javax.swing.JButton();
	javax.swing.JButton allTopicsButton = new javax.swing.JButton();
	javax.swing.JScrollPane foundTopicsScrollPane = new javax.swing.JScrollPane();
	javax.swing.JList<HelpTopic> foundTopicsList = new javax.swing.JList<HelpTopic>();
// END GENERATED CODE

	public SSMHelp( Help h )
	{
		helper = h ;
	    try { initComponents() ; } catch( Exception ex ) {}
	    setVisible( true ) ;
	}

	public void initComponents() throws Exception
	{
// IMPORTANT: Source code between BEGIN/END comment pair will be regenerated
// every time the form is saved. All manual changes will be overwritten.
// BEGIN GENERATED CODE
		// the following code sets the frame's initial state
		helpTextScrollPane.setLocation(new java.awt.Point(10, 140));
		helpTextScrollPane.setVisible(true);
		helpTextScrollPane.setFont(null);
		helpTextScrollPane.setSize(new java.awt.Dimension(600, 470));
		helpTextScrollPane.getViewport().add(helpTextEditorPane);

		helpTextEditorPane.setContentType("text/html");
		helpTextEditorPane.setVisible(true);
		helpTextEditorPane.setFont(null);
		helpTextEditorPane.setEditable(false);

		findPanel.setLocation(new java.awt.Point(0, 10));
		findPanel.setVisible(true);
		findPanel.setFont(null);
		findPanel.setLayout(null);
		findPanel.setSize(new java.awt.Dimension(490, 120));
		findPanel.add(topicLabel);
		findPanel.add(topicTextField);
		findPanel.add(findButton);
		findPanel.add(allTopicsButton);
		findPanel.add(foundTopicsScrollPane);

		topicLabel.setText("Topic:");
		topicLabel.setLocation(new java.awt.Point(10, 10));
		topicLabel.setVisible(true);
		topicLabel.setFont(new java.awt.Font("SansSerif", 1, 10));
		topicLabel.setSize(new java.awt.Dimension(50, 20));

		topicTextField.setLocation(new java.awt.Point(60, 10));
		topicTextField.setVisible(true);
		topicTextField.setFont(new java.awt.Font("SansSerif", 0, 10));
		topicTextField.setMinimumSize(new java.awt.Dimension(150, 20));
		topicTextField.setSize(new java.awt.Dimension(150, 20));

		findButton.setText("Find");
		findButton.setLocation(new java.awt.Point(220, 10));
		findButton.setVisible(true);
		findButton.setFont(new java.awt.Font("SansSerif", 1, 10));
		findButton.setToolTipText("Find Help for Topic");
		findButton.setSize(new java.awt.Dimension(80, 20));

		allTopicsButton.setText("All Topics");
		allTopicsButton.setLocation(new java.awt.Point(360, 10));
		allTopicsButton.setVisible(true);
		allTopicsButton.setFont(new java.awt.Font("SansSerif", 1, 10));
		allTopicsButton.setSize(new java.awt.Dimension(120, 20));

		foundTopicsScrollPane.setLocation(new java.awt.Point(60, 40));
		foundTopicsScrollPane.setVisible(true);
		foundTopicsScrollPane.setFont(new java.awt.Font("SansSerif", 0, 10));
		foundTopicsScrollPane.setMinimumSize(new java.awt.Dimension(430, 70));
		foundTopicsScrollPane.setSize(new java.awt.Dimension(420, 70));
		foundTopicsScrollPane.getViewport().add(foundTopicsList);

		foundTopicsList.setMaximumSize(new java.awt.Dimension(1000, 1000));
		foundTopicsList.setVisible(true);
		foundTopicsList.setFont(null);

		setLocation(new java.awt.Point(0, 0));
		setTitle("Simple Stack Machine Help");
		setResizable(false);
		setFont(new java.awt.Font("SansSerif", 0, 10));
		getContentPane().setLayout(null);
		setSize(new java.awt.Dimension(618, 644));
		getContentPane().add(helpTextScrollPane);
		getContentPane().add(findPanel);


		topicTextField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				topicTextFieldActionPerformed(e);
			}
		});
		findButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent e) {
				findButtonMouseReleased(e);
			}
		});
		allTopicsButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent e) {
				allTopicsButtonMouseReleased(e);
			}
		});
		foundTopicsList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent e) {
				foundTopicsListMouseReleased(e);
			}
		});
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				thisWindowClosing(e);
			}
		});
// END GENERATED CODE

		/*
		getContentPane().setLayout(new java.awt.BorderLayout(0, 0));
		getContentPane().add(findPanel, "North");
		getContentPane().add(helpTextScrollPane, "Center");
		*/

        /*
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        getContentPane().setLayout(gridbag);

        c.fill = GridBagConstraints.BOTH ;
        c.gridwidth = GridBagConstraints.REMAINDER ;
        c.gridheight = 1 ;
        c.weightx = 1.0 ;
        c.weighty = 1.0 ;
        gridbag.setConstraints( findPanel, c ) ;

        c.fill = GridBagConstraints.BOTH ;
        c.gridheight = 3 ;
        gridbag.setConstraints( helpTextScrollPane, c ) ;
        */
        
        /*
        GridBagLayout gridbag = new GridBagLayout() ;
        GridBagConstraints c = new GridBagConstraints() ;
        findPanel.setLayout( gridbag ) ;

        c.insets = new Insets( 4, 4, 4, 4 ) ;
        c.weightx = 0.0 ;
        c.gridx = GridBagConstraints.RELATIVE ;
        c.gridy = 0 ;
        c.anchor = GridBagConstraints.EAST ;
        gridbag.setConstraints( topicLabel, c ) ;
        
        c.weightx = 0.5 ;
        c.anchor = GridBagConstraints.WEST ;
        gridbag.setConstraints( topicTextField, c ) ;
        
        c.anchor = GridBagConstraints.WEST ;
        c.weightx = 0.0 ;
        gridbag.setConstraints( findButton, c ) ;
        
        c.gridwidth = GridBagConstraints.REMAINDER ;
        c.anchor = GridBagConstraints.EAST ;
        gridbag.setConstraints( allTopicsButton, c ) ;
        
        c.gridy = GridBagConstraints.RELATIVE ;
        c.gridwidth = GridBagConstraints.REMAINDER ;
        c.gridheight = GridBagConstraints.REMAINDER ;
        c.weightx = 1.0 ;
        gridbag.setConstraints( foundTopicsScrollPane, c ) ;
        */

        helpTextEditorPane.setContentType( "text/html" ) ;
	}
  
  	private boolean mShown = false;
  	
	public void addNotify() 
	{
		super.addNotify();
		
		if (mShown)
			return;
			
		// resize frame to account for menubar
		JMenuBar jMenuBar = getJMenuBar();
		if (jMenuBar != null) {
			int jMenuBarHeight = jMenuBar.getPreferredSize().height;
			Dimension dimension = getSize();
			dimension.height += jMenuBarHeight;
			setSize(dimension);
		}

		mShown = true;
	}

	// Close the window when the close box is clicked
	void thisWindowClosing(java.awt.event.WindowEvent e)
	{
		setVisible(false);
		dispose();
		//System.exit(0);
	}
	
	private void displayFoundHelp( boolean doAll, AccumulatorFactory accfac, String fileName )
	{
		Vector<HelpTopic> helpTopics = helper.findTopics( doAll ? null : topicTextField.getText().trim() ) ;
		foundTopicsList.setListData( helpTopics ) ;
		if ( helpTopics.size() == 1 || fileName != null )
		{
		    HelpAccumulator acc = outputAllSelectedHelp( helpTopics, accfac, fileName ) ;
		    //displayOneSelectedHelp( (HelpTopic)helpTopics.elementAt( 0 ) ) ;
		    helpTextEditorPane.setText( acc.toString() ) ;
		}
	}
	
	private void output1HelpTopic( HelpAccumulator acc, HelpTopic helpTopic )
	{
		acc.beginSectionTitle() ;
		helpTopic.genTitle( acc ) ;
		acc.append( ": " ) ;
		helpTopic.genTopic( acc ) ;
		acc.endSectionTitle() ;
	    acc.anchor( helpTopic.getTopic() ) ;
		acc.nl() ;
		acc.beginPara( ) ;
		helpTopic.genHelp( acc ) ;
		acc.endPara() ;
	}
	
	private void displayOneSelectedHelp( HelpTopic helpTopic )
	{
	    Vector<HelpTopic> helpTopics = new Vector<HelpTopic>() ;
	    helpTopics.addElement( helpTopic ) ;
		HelpAccumulator acc = outputAllSelectedHelp( helpTopics ) ;
		helpTextEditorPane.setText( acc.toString() ) ;
	}
	
	private HelpAccumulator outputAllSelectedHelp( Vector<HelpTopic> helpTopics, AccumulatorFactory accfac, String fileName )
	{
		HelpAccumulator acc = accfac.make( fileName == null, fileName ) ;
		
		//System.out.println( "Help " + fileName ) ;
		acc.beginHeadTitleBody( "SSM Help" ) ;
		if ( fileName != null )
		{
			acc.para( "Generated by SSM " + Config.version() + " (" + Config.versionDate() + ") at " + (new Date().toString()) ) ;
			acc.sectionTitle( "Topics" ) ;
			acc.beginCentered( ) ;
    		for ( Enumeration<HelpTopic> e = helpTopics.elements() ; e.hasMoreElements() ; )
    		{
    		    HelpTopic helpTopic = e.nextElement() ;
    		    String topic = helpTopic.getTopic() ;
        		acc.anchorA( topic, topic ) ;
        		if ( e.hasMoreElements() )
        			acc.append( ", " ) ;
    		}
			acc.endCentered() ;
		}
		for ( Enumeration<HelpTopic> e = helpTopics.elements() ; e.hasMoreElements() ; )
		{
		    HelpTopic helpTopic = e.nextElement() ;
    		output1HelpTopic( acc, helpTopic ) ;
    		acc.nl() ;
		}
		acc.endHeadTitleBody() ;
		
		return acc ;
	}
	
	private HelpAccumulator outputAllSelectedHelp( Vector<HelpTopic> helpTopics )
	{
		return outputAllSelectedHelp( helpTopics, new HTMLAccumulatorFactory(), null ) ;
	}
	
	public void findButtonMouseReleased(java.awt.event.MouseEvent e)
	{
	    displayFoundHelp( false, new HTMLAccumulatorFactory(), null ) ;
	}
	
	public void topicTextFieldActionPerformed(java.awt.event.ActionEvent e)
	{
	    displayFoundHelp( false, new HTMLAccumulatorFactory(), null ) ;
	}
	
	public void foundTopicsListMouseReleased(java.awt.event.MouseEvent e)
	{
	    HelpTopic helpTopic = foundTopicsList.getSelectedValue() ;
	    if ( helpTopic != null )
	        displayOneSelectedHelp( helpTopic ) ;
	}
	
	/*
	public void allTopicsButtonActionPerformed(java.awt.event.ActionEvent e)
	{
	    //topicTextField.setText( "" ) ;
	    //System.out.println( "all topics event modf " + e.getModifiers() ) ;
	    //displayFoundHelp( true ) ;
	}
	*/
	
	public void allTopicsButtonMouseReleased(java.awt.event.MouseEvent e)
	{
	    if ( e.isAltDown() )
	    {
    	    displayFoundHelp
    	    	( true
    	    	, new HTMLAccumulatorFactory()
    	    	, "ssmtopics.html"
    	    	) ;
	    }
	    else if ( e.isControlDown() || e.isMetaDown() )
	    {
    	    displayFoundHelp
    	    	( true
    	    	, new AccumulatorFactory()
    	    		{
    	    			public HelpAccumulator make( boolean b, String f )
    	    			{
    	    				return new HelpLaTeXAccumulator( f ) ;
    	    			}
    	    		}
    	    	, "ssmtopics.tex"
    	    	) ;
	    }
    	else
    	    displayFoundHelp( true, new HTMLAccumulatorFactory(), null ) ;
	}
	
	
	
	
	
	
	
}
