/**
 * Simple Stack Machine
 *
 * Written by Atze Dijkstra, atze@cs.uu.nl,
 * Copyright Utrecht University.
 *
 */

package nl.uu.cs.ssm ;

import java.util.Hashtable;

public class Memory extends AbstractMemoryCellModel
	implements MemoryCellModel
{
    private int             cells[]         ;
    private Hashtable<Integer,MemoryAnnotation>       annotations     ;
    private int             nUsedForCode    ;
    
    private Messenger messenger             ;
    
    protected Memory( int initCapacity, Messenger m )
    {
        cells = new int[ initCapacity ] ;
        for ( int i = 0 ; i < cells.length ; i++ )
            cells[ i ] = 0 ;        
               
        messenger = m ;
        
        reset() ;
    }
    
    public void reset()
    {
    	nUsedForCode = 0 ;
    	annotations = new Hashtable<Integer,MemoryAnnotation>() ;
        for ( int i = 0 ; i < cells.length ; i++ )
            setAt( i, 0 ) ;
    }
    
    public int getCapacity()
    {
        return cells.length ;
    }
    
    public int getUsedForCode()
    {
        return nUsedForCode ;
    }
    
    private boolean checkWithinMemory( int addr )
    {
        boolean res ;
        if ( res = ( addr < 0 || addr >= cells.length ) )
        {
            messenger.println
            	( java.text.MessageFormat.format
            		( "attempt to access location {0} outside {1}"
            		, new Object[] { Utils.asHex(addr), ""+this }
            		) ) ;
        }
        return ! res ;
    }
    
    class UndoMemoryModification implements Modification
    {
    	private int offset, value ;
    	
    	UndoMemoryModification( int o, int v )
    	{
    		offset = o ;
    		value = v ;
    	}
    	
    	public void modify()
    	{
    		setAt( offset, value ) ;
    	}
    }
    
    public void setAt( int addr, int v )
    {
        if ( checkWithinMemory( addr ) )
        {
            int oldv = cells[ addr ] ;
            cells[ addr ] = v ;
            fireCellChange( this, addr, oldv, new UndoMemoryModification( addr, oldv ) ) ;
        }
    }
    
    public void setAt( int addr, String v )
    {
        setAt( addr, Utils.fromHex( v ) ) ;
    }
    
    public int getAt( int addr )
    {
        int res = 0 ;
        if ( checkWithinMemory( addr ) )
        {
            res = cells[ addr ] ;
        }
        return res ;
    }
    
    public int[] getAt( int addr, int len )
    {
        int res[] = new int[ len ] ;
        for ( int i = 0 ; i < len && checkWithinMemory( addr + i ) ; i++ )
        {
            res[ i ] = cells[ addr + i ] ;
        }
        return res ;
    }
    
    public String getAsHexAt( int addr )
    {
        return Utils.asHex( getAt( addr ) ) ;
    }
    
    class UndoAnnotationModification implements Modification
    {
    	private int offset ;
    	private MemoryAnnotation value ;
    	
    	UndoAnnotationModification( int o, MemoryAnnotation v )
    	{
    		offset = o ;
    		value = v ;
    	}
    	
    	public void modify()
    	{
    		setAnnotationAt( offset, value ) ;
    	}
    }
    
    public void setAnnotationAt( int addr, MemoryAnnotation v )
    {
        if ( checkWithinMemory( addr ) )
        {
            //System.out.println( "mem set annote at " + addr + "=" + v ) ;
            MemoryAnnotation oldv ;
            if ( v != null )
                oldv = annotations.put( addr, v ) ;
            else
                oldv = annotations.remove( addr ) ;
            if ( oldv != v )
                fireCellChange( this, addr, oldv, new UndoAnnotationModification( addr, oldv ) ) ;
        }
    }
    
    public MemoryAnnotation getAnnotationAt( int addr )
    {
        MemoryAnnotation res ;
        res = annotations.get( addr ) ;
        //System.out.println( "mem got annote at " + addr + "=" + res ) ;
        return res ;
    }
    
    public void ensureCapacity( int nCells )
    {
        if ( nCells > (cells.length - nUsedForCode) )
        {
            int newCells[] = new int[ 2 * (nCells + 10) + cells.length ] ;
            System.arraycopy( cells, 0, newCells, 0, cells.length ) ;
            cells = newCells ;
        }
    }
    
    public void deleteAt( int pos, int n )
    {
        for( int i = pos + n ; i < nUsedForCode ; i++ )
            cells[ i-n ] = cells[ i ] ;
        nUsedForCode -= n ;
    }
    
    public void reserveAt( int pos, int n )
    {
    	// Disable the automated expansion of the memory since the start address of the heap is fixed 
        // in MachineState.java and because the references to cells in the heap cannot change. 
        //ensureCapacity( n ) ;
        for( int i = nUsedForCode - 1 ; i >= pos ; i-- )
        {
            cells[ i+n ] = cells[ i ] ;
        }
        for( int i = 0 ; i < n ; i++ )
            cells[ i+pos ] = 0 ;
        nUsedForCode += n ;
    }
    
    public void shiftAt( int pos, int n )
    {
        if ( n > 0 )
            reserveAt( pos, n ) ;
        else if ( n < 0 )
            deleteAt( pos, -n ) ;
    }
    
    public void copyAt( int pos, int[] vals )
    {
        for( int i = 0 ; i < vals.length ; i++ )
            cells[ i+pos ] = vals[ i ] ;
    }
    
    public void insertAt( int pos, int[] vals )
    {
        reserveAt( pos, vals.length ) ;
        copyAt( pos, vals ) ;
    }
    
    public String toString()
    {
    	return "memory [0(" + Utils.asHex(nUsedForCode,false) + ").." +  Utils.asHex(getCapacity()-1,false) + "]" ;
    }
    
}
