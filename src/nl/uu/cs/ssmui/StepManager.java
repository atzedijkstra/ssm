/**
 * Simple Stack Machine
 *
 * Written by Atze Dijkstra, atze@cs.uu.nl,
 * Copyright Utrecht University.
 *
 */

package nl.uu.cs.ssmui ;

import nl.uu.cs.ssm.* ;
import java.util.* ;

public class StepManager
	implements MemoryCellListener, MachineStateListener
{
	private MachineState machineState ;
	private Memory memory ;
	private Registers registers ;
	
	private Vector history ;
	private Vector curStepHistory ;
	
	private StepManager( Memory m, Registers r )
	{
		memory = m ;
		registers = r ;
		history = new Vector() ;
	}
	
	protected StepManager( Machine m )
	{
		this( m.memory(), m.registers() ) ;
		machineState = m.state() ;
	}
	
    public void cellChanged( MemoryCellEvent e )
    {
    	curStepHistory.addElement( e ) ;
    }
    
    public void stateChanged( MachineStateEvent e )
    {
    	curStepHistory.addElement( e ) ;
    }

	protected void beginForwardStep()
	{
		curStepHistory = new Vector() ;
		memory.addMemoryCellListener( this ) ;
		registers.addMemoryCellListener( this ) ;
		machineState.addMachineStateListener( this ) ;
	}
	
	protected void endForwardStep()
	{
		machineState.removeMachineStateListener( this ) ;
		memory.removeMemoryCellListener( this ) ;
		registers.removeMemoryCellListener( this ) ;
		history.addElement( curStepHistory ) ;
	}
	
	protected boolean canDoBackStep()
	{
		return history.size() > 0 ;
	}
	
	protected void backStep()
	{
		int sz ;
		if ( (sz=history.size()) > 0 )
		{
			Vector events = (Vector)history.elementAt( sz-1 ) ;
			for ( int i = events.size() - 1 ; i >= 0 ; i-- )
			{
				UndoableEvent e = (UndoableEvent)events.elementAt( i ) ;
				e.undoModification().modify() ;
			}
			history.removeElementAt( sz-1 ) ;
		}
	}
	
	
}

