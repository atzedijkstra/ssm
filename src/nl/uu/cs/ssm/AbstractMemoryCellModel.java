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

abstract class AbstractMemoryCellModel extends Model
	implements MemoryCellModel
{
    protected void fireCellChange( MemoryCellModel m, MemoryCellEvent ev )
    {
    	for( Enumeration<EventListener> e = getListeners() ; e.hasMoreElements() ; )
    	{
    		MemoryCellListener l = (MemoryCellListener)e.nextElement() ;
    		l.cellChanged( ev ) ;
    	}
    }

    protected void fireCellChange( MemoryCellModel m, int cellIndex, int oldCellValue, Modification mdf )
    {
    	fireCellChange( m, new MemoryCellEvent( m, cellIndex, oldCellValue, mdf ) ) ;
    }

    protected void fireCellChange( MemoryCellModel m, int cellIndex, MemoryAnnotation oldCellValue, Modification mdf )
    {
    	fireCellChange( m, new MemoryCellEvent( m, cellIndex, oldCellValue, mdf ) ) ;
    }

    public void addMemoryCellListener( MemoryCellListener l )
    {
    	addListener( l ) ;
    }
    
    public void removeMemoryCellListener( MemoryCellListener l )
    {
    	removeListener( l ) ;
    }
    
}
