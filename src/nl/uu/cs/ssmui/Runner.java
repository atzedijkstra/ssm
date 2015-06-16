/* 
	Runner.java

	Title:			Simple Stack Machine Runner
	Author:			atze
	Description:	
*/

package nl.uu.cs.ssmui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.UIManager;

import nl.uu.cs.ssm.Config;

public class Runner extends Thread
{
    protected int delay = 50 ;
    
    SSMRunner  ssmRunner  ;
    
	public Runner(int delay)
	{
		this.delay = delay;
		try {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} 
			catch (Exception e) { 
			}
		    ssmRunner = new SSMRunner( this );
			ssmRunner.initComponents();
			ssmRunner.setVisible(true);
			ssmRunner.requestFocus() ;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
   public void loadFile(File initialFile) {
		ssmRunner.loadFile( initialFile ) ;
   }

   public void loadReader(Reader reader) {
		ssmRunner.load( reader ) ;
   }

   public static void usage() {
	   System.out.println("Simple Stack Machine Interpreter");
	   System.out.println("Version " + Config.version() + ", " + Config.versionDate());
	   System.out.println("usage: [--clisteps <steps>] [--cli] [--file <path> OR --stdin]");
	   System.out.println("  --help             : Print this help");
	   System.out.println("  --version          : Print version");
	   System.out.println("  --clisteps <steps> : The amount of steps to run. -1 for infinite(default). Only in cli mode");
	   System.out.println("  --stdin            : Read code from stdin");
	   System.out.println("  --file <path>      : Read code from path");
	   System.out.println("  --cli              : No GUI, runs code and exits on halt");
	   System.out.println("  --guidelay         : Amount of time to sleep in milliseconds between steps in the GUI. Default: 50");
	   System.exit(1);
   }

	public void run()
	{
		while( true )
		{
			int steppingState = ssmRunner.steppingState() ;
			if ( steppingState != SSMRunner.STEP_BY_STEP )
			{
				if ( ssmRunner.hasBreakpointAtPC() )
					ssmRunner.stopContinuouslyDoingSteps() ;
				else if ( steppingState == SSMRunner.STEP_CONT_FORWARD )
					ssmRunner.doAStepForward() ;
				else if ( steppingState == SSMRunner.STEP_CONT_BACKWARD )
					ssmRunner.doAStepBack() ;
			}
			try { sleep( delay ) ; } catch ( InterruptedException e ) {}
		}
	}

	// Main entry point
	static public void main(String[] args) throws IOException {
		File initialFile = null;
		long steps = -1;
		boolean stdin = false;
		boolean cli = false;
		int guiDelay =50;
		for (int i = 0; i< args.length; i++) {
			String key = args[i];
			switch(key) {
			case "--help":
				usage();
				break;
			case "--version":
				System.out.println( Config.version() );
				System.exit(0);
				break;
			case "--clisteps":
				i++;
				steps = Long.parseLong(args[i]);
				break;
			case "--stdin":
				if(initialFile != null) {
					System.out.println("--stdin cannot be used with --file");
					usage();
				}
				stdin=true;
				break;
			case "--file":
				if(stdin) {
					System.out.println("--file cannot be used with --stdin");
					usage();
				} else {
					i++;
					initialFile = new File(args[i]);
				}
				break;
			case "--cli":
				cli=true;
				break;
			case "--guidelay":
				i++;
				guiDelay = Integer.parseInt(args[i]);
				break;
			default:
				usage();
			}
		}

		if(initialFile != null && !initialFile.exists()) {
			System.out.println("Input file does not exist");
			usage();
		}

		if(cli) {
			CliRunner cliRunner = new CliRunner( steps);
			if(stdin) {
		        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		        cliRunner.load(reader);
		        reader.close();
			} else {
				if(!stdin && initialFile == null) {
					System.out.println("Need some input in CLI mode");
				}
		        FileReader fr = new FileReader( initialFile ) ;
		        cliRunner.load(fr);
		        fr.close();
			}
			cliRunner.run();
		} else {
			Runner r = new Runner(guiDelay);
			if(stdin) {
		        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		        r.loadReader(reader);
		        reader.close();
			} else if(initialFile != null) {
				r.loadFile(initialFile);
			}
		}
	}
	
}
