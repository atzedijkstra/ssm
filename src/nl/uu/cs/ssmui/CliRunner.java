package nl.uu.cs.ssmui;

import java.io.Reader;
import java.util.Vector;

import nl.uu.cs.ssm.Machine;
import nl.uu.cs.ssm.MachineState;
import nl.uu.cs.ssm.Messenger;

public class CliRunner implements Messenger {
    private static final long STEPS_INFINITE = -1;

    private long steps;
    private MachineState machineState = new MachineState(5000, 2000, this);
    private Machine machine = new Machine(machineState, this);
    private StepManager stepManager = new StepManager(machine, false);
    private CodeTableModel codeTableModel= new CodeTableModel(null, machineState);

    public CliRunner(long steps) {
        this.steps = steps;
    }

    public void run() {
           long count = 0;
           while (count != steps) {
               if(doAStepForward()) {
                   return;
               }

               if(count != STEPS_INFINITE) {
                   count ++;
               }
           }
    }

    /**
     * @return True if we are halted
     */
    protected boolean doAStepForward()
    {
        stepManager.beginForwardStep() ;
        machine.executeOne() ;
        if ( machineState.isHalted() )
            return true;

        stepManager.endForwardStep() ;
        return false;
    }

    private void reset()
    {
        codeTableModel.beforeReset() ;

        machine.reset() ;
        machineState = machine.getMachineState() ;

        codeTableModel.reset() ;
    }

    private void resetToInitialState()
    {
        machineState.resetToInitialState() ;
    }

    public void load( Reader r )
    {
        String msg ;
        try
        {
            Vector<String> leftOverLabels ;
            AssemblyParseResult apr ;
            AssemblyParser ap = new AssemblyParser( r ) ;
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
    }

    @Override
    public void println(String s) {
        System.out.println(s);
    }

    @Override
    public void print(String s) {
        System.out.print(s);
    }

    @Override
    public int promptInt() {
        System.out.print("Please enter an integer: ");
        return Integer.parseInt(System.console().readLine());
    }

    @Override
    public int promptChar() {
        System.out.print("Please enter a character: ");
        String line = System.console().readLine();
        return line.charAt(0);
    }

    @Override
    public int[] promptCharArray() {
        System.out.print("Please enter a string: ");
        String s = System.console().readLine();
        int[] result = new int[s.length()];
        for(int i = 0; i < s.length(); i++)
        {
            result[i] = s.codePointAt(i);
        }
        return result;
    }
}
