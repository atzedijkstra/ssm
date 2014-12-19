/**
 * Simple Stack Machine
 *
 * Written by Atze Dijkstra, atze@cs.uu.nl,
 * Copyright Utrecht University.
 *
 */

package nl.uu.cs.ssm ;

import java.util.Enumeration;
import java.util.EventListener;
import java.util.Vector;

public class Model
{
	private Vector<EventListener> listeners = new Vector<EventListener>() ;
	
	protected void addListener( EventListener l )
	{
		listeners.addElement( l ) ;
	}

	protected void removeListener( EventListener l )
	{
		listeners.removeElement( l ) ;
	}
	
	protected Enumeration<EventListener> getListeners()
	{
		return listeners.elements() ;
	}

}
