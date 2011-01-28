package nl.uu.cs.ssmui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import nl.uu.cs.ssm.ColoredText;
import nl.uu.cs.ssm.Config;
import nl.uu.cs.ssm.HelpSupplier;
import nl.uu.cs.ssm.Instruction;
import nl.uu.cs.ssm.Machine;
import nl.uu.cs.ssm.MachineState;
import nl.uu.cs.ssm.Messenger;
import nl.uu.cs.ssm.MetaInstruction;
import nl.uu.cs.ssm.Registers;
import nl.uu.cs.ssm.Utils;

public class SSMRunner extends JFrame
    implements Messenger, FocusListener, TableModelListener
{
	private String title = "Simple Stack Machine Interpreter";
	
	private static final long serialVersionUID = 1L ;

	public final static int STEP_BY_STEP		= 0 ;
	public final static int STEP_CONT_FORWARD	= 1 ;
	public final static int STEP_CONT_BACKWARD	= 2 ;
	
	public final static int SETUP_BUSY			= 0 ;
	public final static int SETUP_READY			= 1 ;
	
	
    public static Class tableModelColumnClass ;
    
    static
    {
        try
        {
            tableModelColumnClass = Class.forName( "java.lang.String" ) ;
        }
        catch( ClassNotFoundException ex )
        {
        }
    }
    
    private Machine             machine                 ;
    
    private MachineState        machineState            ;
    
    private CodeTableModel      codeTableModel          ;
    private StackTableModel     stackTableModel         ;
    private StatusTableModel    statusTableModel        ;
    private HeapTableModel      heapTableModel          ;
    
    private int             	steppingState        	;

    private Runner              runner                  ;
    private StepManager			stepManager				;
    private int					setupState				;
    
    private Help		        helper            		;
    
    private File                recentLoadedFile        ;
    
    private boolean				hasFocus				;

	javax.swing.JMenuBar JMenuBar = new javax.swing.JMenuBar();
	javax.swing.JMenu jMenuFile = new javax.swing.JMenu();
	javax.swing.JMenuItem jMenuFileNew = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuFileLoad = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuFileReload = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuFileQuit = new javax.swing.JMenuItem();
	javax.swing.JMenu jMenuRunner = new javax.swing.JMenu();
	javax.swing.JMenuItem jMenuRunnerStart = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuRunnerPause = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuRunnerOneStep = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuRunnerReset = new javax.swing.JMenuItem();
	javax.swing.JMenu jMenuHelp = new javax.swing.JMenu();
	javax.swing.JMenuItem jMenuHelpAbout = new javax.swing.JMenuItem();
	javax.swing.JSeparator jMenuHelpSeparator1 = new javax.swing.JSeparator();
	javax.swing.JMenuItem jMenuHelpHelponTopic = new javax.swing.JMenuItem();
	javax.swing.JToolBar ssmToolBar = new javax.swing.JToolBar();
	javax.swing.JButton tbNewButton = new javax.swing.JButton();
	javax.swing.JButton tbLoadButton = new javax.swing.JButton();
	javax.swing.JButton tbReloadButton = new javax.swing.JButton();
	javax.swing.JButton tbSaveButton = new javax.swing.JButton();
	javax.swing.JButton tbStep1ForwardButton = new javax.swing.JButton();
	javax.swing.JButton tbStep1BackButton = new javax.swing.JButton();
	javax.swing.JButton tbStartBackButton = new javax.swing.JButton();
	javax.swing.JButton tbStartForwardButton = new javax.swing.JButton();
	javax.swing.JButton tbPauseButton = new javax.swing.JButton();
	javax.swing.JButton tbResetButton = new javax.swing.JButton();
	javax.swing.JButton tbNewInstrButton = new javax.swing.JButton();
	javax.swing.JSplitPane memNRestSplitPane = new javax.swing.JSplitPane();
	javax.swing.JSplitPane codeNStackNHeapSplitPane = new javax.swing.JSplitPane();
	javax.swing.JSplitPane stackNHeapSplitPane = new javax.swing.JSplitPane();
	javax.swing.JScrollPane codeScrollPane = new javax.swing.JScrollPane();
	javax.swing.JTable codeTable = new javax.swing.JTable();
	javax.swing.JScrollPane stackScrollPane = new javax.swing.JScrollPane();
	javax.swing.JTable stackTable = new JTable();
	javax.swing.JScrollPane heapScrollPane = new javax.swing.JScrollPane();
	javax.swing.JTable heapTable = new JTable();
	javax.swing.JSplitPane statusNOutputSplitPane = new javax.swing.JSplitPane();
	javax.swing.JScrollPane statusScrollPane = new javax.swing.JScrollPane();
	javax.swing.JTable statusTable = new javax.swing.JTable();
	javax.swing.JScrollPane outputScrollPane = new javax.swing.JScrollPane();
	javax.swing.JTextArea outputTextArea = new javax.swing.JTextArea();

	private JMenuItem jMenuRunnerOneStepBack = new JMenuItem();

	private JMenu jMenuLookAndFeel = new JMenu() ;
	private JMenu jMenuPrefs = new JMenu() ;

	public SSMRunner( Runner runner )
	{
		setupState = SETUP_BUSY ;
		machineState = new MachineState( 5000, 2000, this ) ; // TBD: automatic increase with reasonable increments
		machine = new Machine( machineState, this ) ;
		stepManager = new StepManager( machine ) ;
		
	    codeTableModel = new CodeTableModel( this, machineState ) ;
	    stackTableModel = new StackTableModel( machineState ) ;
	    statusTableModel = new StatusTableModel( machineState ) ;
	    heapTableModel = new HeapTableModel(machineState);
	    
	    stopContinuouslyDoingSteps() ;
	    this.runner = runner ;
	    
	    recentLoadedFile = null ;
	    
	    helper = new Help() ;
	    HelpSupplier instrHelpSupplier = Instruction.getHelpSupplier() ;
	    //helper.addHelpSupplier( instrHelpSupplier ) ;
	    helper.addHelpSupplier( new HelpFromProp( "ssminstrhelp", instrHelpSupplier, true ) ) ;
	    helper.addHelpSupplier( new HelpFromProp( "ssmhelp", instrHelpSupplier, false ) ) ;
	}

	public void initComponents() throws Exception
	{
// IMPORTANT: Source code between BEGIN/END comment pair will be regenerated
// every time the form is saved. All manual changes will be overwritten.
// BEGIN GENERATED CODE
		// the following code sets the frame's initial state
		JMenuBar.setVisible(true);
		JMenuBar.setFont(new java.awt.Font("SansSerif", 1, 10));
		jMenuFile.setVisible(true);
		jMenuFile.setRolloverEnabled(false);
		jMenuFile.setText("File");
		jMenuFileNew.setVisible(true);
		jMenuFileNew.setRolloverEnabled(false);
		jMenuFileNew.setText("New");
		jMenuFileLoad.setToolTipText("Load an Assembly File");
		jMenuFileLoad.setVisible(true);
		jMenuFileLoad.setRolloverEnabled(false);
		jMenuFileLoad.setText("Open ...");
		jMenuFileReload.setToolTipText("Reload latest loaded File");
		jMenuFileReload.setVisible(true);
		jMenuFileReload.setRolloverEnabled(false);
		jMenuFileReload.setText("Reload");
		jMenuFileQuit.setToolTipText("Quit");
		jMenuFileQuit.setVisible(true);
		jMenuFileQuit.setRolloverEnabled(false);
		jMenuFileQuit.setText("Quit");
		jMenuRunner.setVisible(true);
		jMenuRunner.setRolloverEnabled(false);
		jMenuRunner.setText("Runner");
		jMenuRunnerStart.setToolTipText("Start Continuous Execution");
		jMenuRunnerStart.setVisible(true);
		jMenuRunnerStart.setRolloverEnabled(false);
		jMenuRunnerStart.setText("Start");
		jMenuRunnerPause.setToolTipText("Pause Runner");
		jMenuRunnerPause.setVisible(true);
		jMenuRunnerPause.setRolloverEnabled(false);
		jMenuRunnerPause.setText("Pause");
		jMenuRunnerOneStep.setToolTipText("Step 1 forward");
		jMenuRunnerOneStep.setVisible(true);
		jMenuRunnerOneStep.setRolloverEnabled(false);
		jMenuRunnerOneStep.setText("One Step");
		jMenuRunnerOneStepBack.setToolTipText("Step one back");
		jMenuRunnerOneStepBack.setVisible(true);
		jMenuRunnerOneStepBack.setRolloverEnabled(false);
		jMenuRunnerOneStepBack.setText("One Step Back");
		jMenuRunnerReset.setToolTipText("Reset State of Machine");
		jMenuRunnerReset.setVisible(true);
		jMenuRunnerReset.setRolloverEnabled(false);
		jMenuRunnerReset.setText("Reset");
		jMenuHelp.setVisible(true);
		jMenuHelp.setRolloverEnabled(false);
		jMenuHelp.setText("Help");
		jMenuHelpAbout.setToolTipText("About this Program");
		jMenuHelpAbout.setVisible(true);
		jMenuHelpAbout.setRolloverEnabled(false);
		jMenuHelpAbout.setText("About");
		jMenuHelpSeparator1.setVisible(true);
		jMenuHelpHelponTopic.setToolTipText("Help on a Topic");
		jMenuHelpHelponTopic.setVisible(true);
		jMenuHelpHelponTopic.setRolloverEnabled(false);
		jMenuHelpHelponTopic.setText("Help on Topic ...");
		ssmToolBar.setVisible(true);
		ssmToolBar.setLayout(new java.awt.FlowLayout(0, 2, 2));
		ssmToolBar.setFont(new java.awt.Font("SansSerif", 1, 10));
		tbNewButton.setToolTipText("New Code, erase current");
		tbNewButton.setVisible(true);
		tbNewButton.setRolloverEnabled(false);
		tbNewButton.setFont(null);
		tbLoadButton.setToolTipText("Load Code from File");
		tbLoadButton.setVisible(true);
		tbLoadButton.setRolloverEnabled(false);
		tbLoadButton.setFont(null);
		tbReloadButton.setToolTipText("Reload Code from File");
		tbReloadButton.setVisible(true);
		tbReloadButton.setRolloverEnabled(false);
		tbReloadButton.setFont(null);
		tbSaveButton.setToolTipText("Save Code to File");
		tbSaveButton.setVisible(false);
		tbSaveButton.setRolloverEnabled(false);
		tbSaveButton.setFont(null);
		tbStep1ForwardButton.setToolTipText("One Step Forward");
		tbStep1ForwardButton.setVisible(true);
		tbStep1ForwardButton.setRolloverEnabled(false);
		tbStep1ForwardButton.setFont(null);
		tbStep1BackButton.setToolTipText("One Step Back");
		tbStep1BackButton.setVisible(true);
		tbStep1BackButton.setRolloverEnabled(false);
		tbStep1BackButton.setFont(null);
		tbStartBackButton.setToolTipText("Start Stepping Backward");
		tbStartBackButton.setVisible(true);
		tbStartBackButton.setRolloverEnabled(false);
		tbStartBackButton.setFont(null);
		tbStartForwardButton.setToolTipText("Start Stepping Forward");
		tbStartForwardButton.setVisible(true);
		tbStartForwardButton.setRolloverEnabled(false);
		tbStartForwardButton.setFont(null);
		tbPauseButton.setToolTipText("Pause Runner");
		tbPauseButton.setVisible(true);
		tbPauseButton.setRolloverEnabled(false);
		tbPauseButton.setFont(null);
		tbResetButton.setVisible(true);
		tbResetButton.setRolloverEnabled(false);
		tbResetButton.setFont(null);
		tbNewInstrButton.setToolTipText("New Instruction");
		tbNewInstrButton.setVisible(true);
		tbNewInstrButton.setRolloverEnabled(false);
		tbNewInstrButton.setFont(null);
		memNRestSplitPane.setDividerLocation(465);
		memNRestSplitPane.setDividerSize(8);
		memNRestSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		memNRestSplitPane.setVisible(true);
		memNRestSplitPane.setForeground(java.awt.Color.black);
		memNRestSplitPane.setFont(new java.awt.Font("Monospaced", 0, 12));
		memNRestSplitPane.setBorder(null);
		codeNStackNHeapSplitPane.setDividerLocation(500);
		codeNStackNHeapSplitPane.setDividerSize(6);
		codeNStackNHeapSplitPane.setVisible(true);
		codeNStackNHeapSplitPane.setForeground(java.awt.Color.black);
		codeNStackNHeapSplitPane.setBorder(null);
		stackNHeapSplitPane.setDividerLocation(400);
		stackNHeapSplitPane.setDividerSize(6);
		stackNHeapSplitPane.setVisible(true);
		stackNHeapSplitPane.setForeground(java.awt.Color.black);
		stackNHeapSplitPane.setBorder(null);
		codeScrollPane.setVisible(true);
		codeScrollPane.setFont(null);
		codeScrollPane.setBorder(createTitledBorder("Code"));
		codeTable.setVisible(true);
		codeTable.setPreferredScrollableViewportSize(new java.awt.Dimension(500, 600));
		codeTable.setFont(null);
		stackScrollPane.setVisible(true);
		stackScrollPane.setFont(null);
		stackScrollPane.setBorder(createTitledBorder("Stack"));
		stackTable.setVisible(true);
		stackTable.setPreferredScrollableViewportSize(new java.awt.Dimension(400, 600));
		stackTable.setFont(null);
		heapScrollPane.setVisible(true);
		heapScrollPane.setFont(null);
		heapScrollPane.setBorder(createTitledBorder("Heap"));
		heapTable.setVisible(true);
		heapTable.setPreferredScrollableViewportSize(new java.awt.Dimension(300, 600));
		heapTable.setFont(null);
		statusNOutputSplitPane.setDividerLocation(100);
		statusNOutputSplitPane.setDividerSize(6);
		statusNOutputSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		statusNOutputSplitPane.setVisible(true);
		statusNOutputSplitPane.setForeground(java.awt.Color.black);
		statusNOutputSplitPane.setBorder(null);
		statusScrollPane.setVisible(true);
		statusScrollPane.setFont(null);
		statusScrollPane.setBorder(createTitledBorder("Status"));
		statusTable.setVisible(true);
		statusTable.setFont(null);
		outputScrollPane.setVisible(true);
		outputScrollPane.setFont(null);
		outputScrollPane.setBorder(createTitledBorder("Output"));
		outputTextArea.setVisible(true);
		outputTextArea.setEditable(false);
		outputTextArea.setFont(new java.awt.Font("SansSerif", 0, 10));
		setLocation(new java.awt.Point(0, 0));
		setFont(new java.awt.Font("SansSerif", 0, 12));
		setJMenuBar(JMenuBar);
		getContentPane().setLayout(new java.awt.BorderLayout(0, 0));
		setTitle(title);

		JMenuBar.add(jMenuFile);
		JMenuBar.add(jMenuRunner);
		JMenuBar.add(jMenuHelp);
		jMenuFile.add(jMenuFileNew);
		jMenuFile.add(jMenuFileLoad);
		jMenuFile.add(jMenuFileReload);
		jMenuFile.add(jMenuFileQuit);
		jMenuRunner.add(jMenuRunnerStart);
		jMenuRunner.add(jMenuRunnerPause);
		jMenuRunner.add(jMenuRunnerOneStep);
		jMenuRunner.add(jMenuRunnerOneStepBack);
		jMenuRunner.add(jMenuRunnerReset);
		jMenuHelp.add(jMenuHelpAbout);
		jMenuHelp.add(jMenuHelpSeparator1);
		jMenuHelp.add(jMenuHelpHelponTopic);
		ssmToolBar.add(tbNewButton);
		ssmToolBar.add(tbLoadButton);
		ssmToolBar.add(tbReloadButton);
		ssmToolBar.add(tbSaveButton);
		ssmToolBar.add(tbStartBackButton);
		ssmToolBar.add(tbStep1BackButton);
		ssmToolBar.add(tbStep1ForwardButton);
		ssmToolBar.add(tbStartForwardButton);
		ssmToolBar.add(tbPauseButton);
		ssmToolBar.add(tbResetButton);
		ssmToolBar.add(tbNewInstrButton);
		memNRestSplitPane.add(codeNStackNHeapSplitPane, javax.swing.JSplitPane.TOP);
		memNRestSplitPane.add(statusNOutputSplitPane, javax.swing.JSplitPane.BOTTOM);
		codeNStackNHeapSplitPane.add(codeScrollPane, javax.swing.JSplitPane.LEFT);
		codeNStackNHeapSplitPane.add(stackNHeapSplitPane, javax.swing.JSplitPane.RIGHT);
		codeScrollPane.getViewport().add(codeTable);
		stackNHeapSplitPane.add(stackScrollPane, javax.swing.JSplitPane.LEFT);
		stackNHeapSplitPane.add(heapScrollPane, javax.swing.JSplitPane.RIGHT);
		stackScrollPane.getViewport().add(stackTable);
		heapScrollPane.getViewport().add(heapTable);
		statusNOutputSplitPane.add(statusScrollPane, javax.swing.JSplitPane.TOP);
		statusNOutputSplitPane.add(outputScrollPane, javax.swing.JSplitPane.BOTTOM);
		statusScrollPane.getViewport().add(statusTable);
		outputScrollPane.getViewport().add(outputTextArea);
		getContentPane().add(ssmToolBar, "North");
		getContentPane().add(memNRestSplitPane, "Center");

		setSize(new java.awt.Dimension(1200, 800));

		// event handling
		tbNewButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				tbNewButtonActionPerformed(e);
			}
		});
		tbLoadButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				tbLoadButtonActionPerformed(e);
			}
		});
		tbReloadButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				tbReloadButtonActionPerformed(e);
			}
		});
		tbSaveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				tbSaveButtonActionPerformed(e);
			}
		});
		tbStep1BackButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				tbStep1BackButtonActionPerformed(e);
			}
		});
		tbStep1ForwardButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				tbStep1ForwardButtonActionPerformed(e);
			}
		});
		tbStartForwardButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				tbStartForwardButtonActionPerformed(e);
			}
		});
		tbStartBackButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				tbStartBackButtonActionPerformed(e);
			}
		});
		tbPauseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				tbPauseButtonActionPerformed(e);
			}
		});
		tbResetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				tbResetButtonActionPerformed(e);
			}
		});
		tbNewInstrButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				tbNewInstrButtonActionPerformed(e);
			}
		});
		codeTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent e) {
				codeTableMouseReleased(e);
			}
		});
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				thisWindowClosing(e);
			}
		});

// END GENERATED CODE

		tbStartForwardButton.setIcon( Images.tbStart ) ;
		tbStartBackButton.setIcon( Images.tbStartBack ) ;
		tbPauseButton.setIcon( Images.tbPause ) ;
		tbResetButton.setIcon( Images.tbReset ) ;
		tbStep1ForwardButton.setIcon( Images.tbStep1 ) ;
		tbStep1BackButton.setIcon( Images.tbStep1Back ) ;
		
		tbNewButton.setIcon   ( Images.tbNew    ) ;
		tbLoadButton.setIcon  ( Images.tbLoad   ) ;
		tbReloadButton.setIcon( Images.tbReload ) ;
		tbSaveButton.setIcon  ( Images.tbSave   ) ;
		
		tbNewInstrButton.setIcon( Images.tbNewInstr ) ;
		
		//getContentPane().setLayout(new java.awt.BorderLayout());
		//getContentPane().add(ssmToolBar, "North");
		//getContentPane().add(memNRestSplitPane, "Center");

        MouseAdapter ma = 
		    new java.awt.event.MouseAdapter() {
    			public void mouseReleased(java.awt.event.MouseEvent e) {
    				thisMenuBarMouseReleased(e);
    			}
    		} ;
		jMenuFileQuit.addMouseListener( ma );
		jMenuHelpAbout.addMouseListener( ma );
		jMenuHelpHelponTopic.addMouseListener( ma );
		setupMenuItem( jMenuFileLoad, ma, Config.keysLoad[0] ) ;
		setupMenuItem( jMenuFileReload, ma, Config.keysReload[0] ) ;
		setupMenuItem( jMenuRunnerOneStep, ma, Config.keysStep1Forward[0] ) ;
		setupMenuItem( jMenuRunnerOneStepBack, ma, Config.keysStep1Backward[0] ) ;
		setupMenuItem( jMenuRunnerReset, ma, Config.keysFullBackward[0] ) ;
		setupMenuItem( jMenuRunnerStart, ma, Config.keysFullForward[0] ) ;
		setupMenuItem( jMenuRunnerPause, ma, Config.keysPause[0] ) ;

        codeTable.setModel( codeTableModel ) ;
        stackTable.setModel( stackTableModel ) ;
        statusTable.setModel( statusTableModel ) ;
        heapTable.setModel( heapTableModel );
        
        codeTableModel.setPCNBWidths( codeTable ) ;
        
        // adapt stack view
        TableColumn annoteStackColumn = stackTable.getColumn( stackTableModel.getColumnName( StackTableModel.C_ANNOTE ) ) ;
        final Font annoteStackColumnFont = new Font( "SansSerif", Font.ITALIC, 12 ) ;
        DefaultTableCellRenderer annoteStackColumnRenderer = new DefaultTableCellRenderer()
        {
			private static final long serialVersionUID = 1L ;
		    public void setValue(Object value) {
		    	ColoredText ct = (ColoredText)value ;
		    	Color c = ct.getColor() ;
		        setForeground( c == null ? Color.gray : c ) ;
		        setFont( annoteStackColumnFont ) ;
		        setText( ct.getText() ) ;
		    }
	    } ;
        annoteStackColumn.setCellRenderer( annoteStackColumnRenderer ) ;
        
        // adapt heap view
        TableColumn annoteHeapColumn = heapTable.getColumn(heapTableModel.getColumnName(HeapTableModel.C_ANNOTE));
        final Font annoteHeapColumnFont = new Font( "SansSerif", Font.ITALIC, 12 ) ;
        DefaultTableCellRenderer annoteHeapColumnRenderer = new DefaultTableCellRenderer()
        {
			private static final long serialVersionUID = 1L ;
		    public void setValue(Object value) {
		    	ColoredText ct = (ColoredText)value ;
		    	Color c = ct.getColor() ;
		        setForeground( c == null ? Color.gray : c ) ;
		        setFont( annoteHeapColumnFont ) ;
		        setText( ct.getText() ) ;
		    }
	    } ;
        annoteHeapColumn.setCellRenderer( annoteHeapColumnRenderer ) ;
        
        JScrollBar codeScrollPaneScrollBar = codeScrollPane.getVerticalScrollBar() ;
        BoundedRangeModel codeScrollPaneScrollBarModel = codeScrollPaneScrollBar.getModel() ;
        codeTableModel.setScrollBarModel( codeScrollPaneScrollBarModel ) ;

		// Preferences
		jMenuPrefs.setText( "Preferences" ) ;
		JMenuBar.add( jMenuPrefs ) ;
		
		// Look and Feel
		final UIManager.LookAndFeelInfo lookAndFeels[] = UIManager.getInstalledLookAndFeels() ;
		final JMenuItem lookAndFeelMenuItems[] = new JMenuItem[ lookAndFeels.length ] ;
        MouseAdapter lfl = 
		    new MouseAdapter() {
    			public void mouseReleased( MouseEvent e )
    			{
    				int mItem = Arrays.asList( lookAndFeelMenuItems ).indexOf( e.getSource() ) ;
    				if ( mItem >= 0 )
    					try
    					{
    						UIManager.setLookAndFeel( lookAndFeels[mItem].getClassName() ) ;
							SwingUtilities.updateComponentTreeUI( getContentPane() ) ;
							pack() ;
    					}
    					catch( Exception ex )
    					{
    						println( "Failed to switch look&feel: " + ex.toString() ) ;
    					}
    			}
    		} ;
		for ( int i = 0 ; i < lookAndFeels.length ; i++ )
		{
			JMenuItem it = new JMenuItem( lookAndFeels[i].getName() ) ;
			jMenuLookAndFeel.add( it ) ;
			lookAndFeelMenuItems[ i ] = it ;
			it.addMouseListener( lfl ) ;
		}
		jMenuLookAndFeel.setText( "Look & Feel" ) ;
		jMenuPrefs.add( jMenuLookAndFeel ) ;
		
		// Key, focus listening
		addKeyListener( new SSMKeyListener() ) ;
		hasFocus = false ;
		addFocusListener( this ) ;
		
	    runner.start() ;
	    
		// Listening to cell changes
		// Does not work
        //stackTableModel.addTableModelListener( this ) ;

	}
	
	private TitledBorder createTitledBorder(String title) {
		
		return BorderFactory.createTitledBorder(null, title, TitledBorder.CENTER, TitledBorder.TOP, new java.awt.Font("SansSerif", 0, 14));
	}
	
	private void setupMenuItem( JMenuItem mi, MouseAdapter ma, int keyCode )
	{
		mi.addMouseListener( ma ) ;
		//mi.setAccelerator( KeyStroke.getKeyStroke( keyCode, Event.META_MASK ) ) ;
		mi.setAccelerator( KeyStroke.getKeyStroke( keyCode, 0 ) ) ;
	}
	
	private void reset()
	{
	    stopContinuouslyDoingSteps() ;

		codeTableModel.beforeReset() ;
		
		machine.reset() ;
		machineState = machine.getMachineState() ;
		
		codeTableModel.reset() ;
		stackTableModel.reset() ;
		statusTableModel.reset() ;
		heapTableModel.reset();
	}
  
	private void resetToInitialState()
	{
		machineState.resetToInitialState() ;
		stackTableModel.reset() ;
		heapTableModel.reset();
	}
	
	protected int steppingState()
	{
		return steppingState ;
	}
	
	protected boolean hasBreakpointAtPC()
	{
		return codeTableModel.hasBreakpointAtPC() ;
	}
	
	protected boolean isSettingUp()
	{
		return setupState == SETUP_BUSY ;
	}
	
	protected void stopContinuouslyDoingSteps()
	{
		steppingState = STEP_BY_STEP ;
	}
	
	protected void doAStepBack()
	{
		if ( stepManager.canDoBackStep() )
	        stepManager.backStep() ;
	    else
	    	stopContinuouslyDoingSteps() ;
	}
	
	protected void doAStepForward()
	{
        stepManager.beginForwardStep() ;
        Vector<MetaInstruction> metaInstructions = codeTableModel.getMetaInstructionsAtPC() ;
        machine.executeOne() ;
        if ( machineState.isHalted() )
            stopContinuouslyDoingSteps() ;
        if ( metaInstructions != null )
        {
            for ( Enumeration<MetaInstruction> e = metaInstructions.elements() ; e.hasMoreElements() ; )
            {
                MetaInstruction mi = e.nextElement() ;
                mi.exec( machineState ) ;
            }
        }
        stepManager.endForwardStep() ;
	}
	
	public void println( String s )
	{
	    outputTextArea.append( s ) ;
	    outputTextArea.append( "\n" ) ;
	}
  	
	// Close the window when the close box is clicked
	void thisWindowClosing(java.awt.event.WindowEvent e)
	{
		setVisible(false);
		dispose();
		System.exit(0);
	}
	
	class SSMKeyListener extends KeyAdapter
	{
		public void keyReleased( KeyEvent e )
		{
			//if ( hasFocus )
			{
				//System.out.println( "Key=" + e ) ;
				int kc = e.getKeyCode() ;
				if	( Utils.contains( Config.keysPause, kc )
					&&	(  (steppingState == STEP_CONT_FORWARD) 
						|| (steppingState == STEP_CONT_BACKWARD)
						)
					)
				    tbPauseButtonActionPerformed( null ) ;
				if ( Utils.contains( Config.keysStep1Forward, kc ) )
				    tbStep1ForwardButtonActionPerformed( null ) ;
				else if ( Utils.contains( Config.keysStep1Backward, kc ) )
				    tbStep1BackButtonActionPerformed( null ) ;
				else if ( Utils.contains( Config.keysFullForward, kc ) )
				    tbStartForwardButtonActionPerformed( null ) ;
				else if ( Utils.contains( Config.keysFullBackward, kc ) )
				    tbResetButtonActionPerformed( null ) ;
				else if ( Utils.contains( Config.keysLoad, kc ) )
				    tbLoadButtonActionPerformed( null ) ;
				else if ( Utils.contains( Config.keysReload, kc ) )
				    tbReloadButtonActionPerformed( null ) ;
			}
		}
	}
	
	public void focusGained( FocusEvent e )
	{
		hasFocus = true ;
	}
		
	public void focusLost( FocusEvent e )
	{
		hasFocus = false ;
	}
		
	public void tbStartForwardButtonActionPerformed(java.awt.event.ActionEvent e)
	{
	    doAStepForward() ;
	    steppingState = STEP_CONT_FORWARD ;
	}
	
	public void tbStartBackButtonActionPerformed(java.awt.event.ActionEvent e)
	{
	    doAStepBack() ;
	    steppingState = STEP_CONT_BACKWARD ;
	}
	
	public void tbNewInstrButtonActionPerformed(java.awt.event.ActionEvent e)
	{
	    codeTableModel.insertNewInstrAt( codeTableModel.getRowCount()-1, false ) ;
	}
	
	public void tbResetButtonActionPerformed(java.awt.event.ActionEvent e)
	{
	    resetToInitialState() ;
	    outputTextArea.setText( "" ) ;
	}
	
	public void tbStep1ForwardButtonActionPerformed(java.awt.event.ActionEvent e)
	{
		stopContinuouslyDoingSteps() ;
	    doAStepForward() ;
	}
	
	public void tbStep1BackButtonActionPerformed(java.awt.event.ActionEvent e)
	{
		stopContinuouslyDoingSteps() ;
	    doAStepBack() ;
	}
	
	public void tbSaveButtonActionPerformed(java.awt.event.ActionEvent e)
	{
	}
	
	public void tbReloadButtonActionPerformed(java.awt.event.ActionEvent e)
	{
	    if ( recentLoadedFile != null )
	        loadFile( recentLoadedFile ) ;
	}
	
	protected void loadFile( File f )
	{
		setupState = SETUP_BUSY ;

		String msg ;
		recentLoadedFile = f ;
	    try
	    {
	    	Vector<String> leftOverLabels ;
	    	AssemblyParseResult apr ;
	        FileReader fr = new FileReader( f ) ;
	        AssemblyParser ap = new AssemblyParser( fr ) ;
			reset() ;
			codeTableModel.parseInitialize() ;
	        for ( apr = null ; ! ap.isAtEOF() ; )
	        {
	            apr = ap.parse1Line( apr ) ;
	            if ( apr.message != null )
	                println( "Line " + apr.lineNr + ": " + apr.message ) ;
	            else if ( apr.instrNArgs.size() > 0 )
	            {
	                leftOverLabels = new Vector<String>() ;
	                msg = codeTableModel.enterParsedLine( apr.definedLabels, apr.instrNArgs, leftOverLabels ) ;
    			    if ( msg != null )
        			    println( "Line " + apr.lineNr + ": " + msg ) ;
	                if ( leftOverLabels.size() == 0 )
	                    apr = null ;
	                else
	                    apr.addLabels( leftOverLabels ) ;
	            }
	        }
			fr.close() ;
			setTitle(title + " - " + f.getName());
		}
		catch ( Exception ex )
		{
		    ex.printStackTrace() ;
		}
		finally
		{
		    msg = codeTableModel.parseFinalize() ;
		    if ( msg != null )
		        println( msg ) ;
		}
		resetToInitialState() ;

		setupState = SETUP_READY ;
	}
	
	class SSMFileFilter extends javax.swing.filechooser.FileFilter
	{
		public boolean accept( File f )
		{
			boolean res = true ;
			if ( ! f.isDirectory() )
			{
			    String extension = Utils.getExtension( f ) ;
			    res = (extension != null) && (extension.equals(Config.extensionSSM)) ;
		    }
		    return res ;
		}
		
		public String getDescription()
		{
			return "SSM files" ;
		}
	}
	
	public void tbLoadButtonActionPerformed(java.awt.event.ActionEvent e)
	{
		JFileChooser chooser = new JFileChooser( recentLoadedFile ) ;
		if (recentLoadedFile == null) {
			chooser.setCurrentDirectory(new File ("."));
		}
		Utils.ExtensionFileFilter ff = new Utils.ExtensionFileFilter( "ssm" ) ;
		chooser.setFileFilter( ff ) ;
		if( chooser.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION )
		    loadFile( chooser.getSelectedFile() ) ;
	}
	
	public void tbNewButtonActionPerformed(java.awt.event.ActionEvent e)
	{
	    reset() ;
	}

	public void thisMenuBarMouseReleased(java.awt.event.MouseEvent e)
	{
	    Object src = e.getSource() ;
	    
	    if ( src == jMenuFileLoad )
	    {
	        tbLoadButtonActionPerformed( null ) ;
	    }
	    else if ( src == jMenuFileReload )
	    {
	        tbReloadButtonActionPerformed( null ) ;
	    }
	    else if ( src == jMenuFileNew )
	    {
	        tbNewInstrButtonActionPerformed( null ) ;
	    }
	    else if ( src == jMenuFileQuit )
	    {
	        thisWindowClosing( null ) ;
	    }
	    else if ( src == jMenuRunnerStart )
	    {
	        tbStartForwardButtonActionPerformed( null ) ;
	    }
	    else if ( src == jMenuRunnerReset )
	    {
	        tbResetButtonActionPerformed( null ) ;
	    }
	    else if ( src == jMenuRunnerOneStep )
	    {
	        tbStep1ForwardButtonActionPerformed( null ) ;
	    }
	    else if ( src == jMenuHelpAbout )
	    {
	        new SSMAbout() ;
	    }
	    else if ( src == jMenuHelpHelponTopic )
	    {
	        new SSMHelp( helper ) ;
	    }
	}
	
	public void tbPauseButtonActionPerformed(java.awt.event.ActionEvent e)
	{
	    stopContinuouslyDoingSteps() ;
	}
	
	public void codeTableMouseReleased(java.awt.event.MouseEvent e)
	{
		Point p = e.getPoint() ;
		int row = codeTable.rowAtPoint( p ) ;
		int column = codeTable.columnAtPoint( p ) ;
	    codeTableModel.handleBreakpointMouseEvent( row, column ) ;
	}
	
	/*
	public boolean isFocusTraversable()
	{
		return true ;
	}
	*/
	
    public void tableChanged( TableModelEvent e )
    {
    	Object src = e.getSource() ;
    	if ( src == stackTableModel && e.getColumn() == StackTableModel.C_REGPTRS && stackTableModel.isSPChanged() )
    	{
	    	Registers r = machine.registers() ;
		    int loc = r.getReg( Registers.SP ) ;
		    int row = stackTableModel.memLocToRow( loc ) ;
		    Rectangle cellRect = stackTable.getCellRect( row, StackTableModel.C_REGPTRS, false ) ;
		    //System.out.println( "SP=" + loc + ", Row=" + row + ", Rect=" + cellRect ) ;
		    Utils.scrollComponentTo( stackTable, cellRect ) ;
	    }
    }
	
	
}
