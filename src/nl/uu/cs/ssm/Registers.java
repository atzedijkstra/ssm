/**
 * Simple Stack Machine
 *
 * Written by Atze Dijkstra, atze@cs.uu.nl,
 * Copyright Utrecht University.
 *
 */

package nl.uu.cs.ssm ;

import java.util.Enumeration;
import java.util.Vector;

public class Registers extends AbstractMemoryCellModel
	implements MemoryCellModel
{
    public static final int     	PC = 0      ;
    public static final int     	SP = 1      ;
    public static final int     	MP = 2      ;
    //public static final int     	RR = 3      ;
    public static final int         HP = 4      ;
    
    private static final int     	nrRegs = 8  ;
    
    private static final String[]	regNames = { "R0", "R1", "R2", "R3", "R4", "R5", "R6", "R7" } ;
    
    private static final String[]	regNamesAlias = { "PC", "SP", "MP", "RR", "HP" } ;
    
    //private MemoryCell          	cells[]  	;
    private int          	        cells[]  	;
    
    private Memory					memory		;
    
    private Messenger 				messenger   ;
    
    protected Registers( Memory m, Messenger msgr )
    {
		messenger = msgr ;
    	cells = new int[ nrRegs ] ;
        for ( int i = 0 ; i < cells.length ; i++ )
            //cells[ i ] = new MemoryCell() ;
            cells[ i ] = 0 ;
        memory = m ;
        reset() ;
    }
    
    public void reset()
    {
        for ( int i = 0 ; i < cells.length ; i++ )
            //cells[ i ].setValue( 0 ) ;
            cells[ i ] = 0 ;
    }
    
    public static int getNrRegs( )
    {
        return nrRegs ;
    }
    
    /*
    private static String[] getRegNames( )
    {
        return regNames ;
    }
    */
    
    public static String getRegName( int r )
    {
        return (r < regNames.length && r >= 0) ? regNames[ r ] : ("R"+r) ;
    }
    
    public static String getRegOrAliasName( int r )
    {
    	return r < regNamesAlias.length ? regNamesAlias[r] : getRegName( r ) ;
    }
    
    public static String getRegNAliasName( int r )
    {
        return getRegName(r) + ( r < regNamesAlias.length ? ("(" + regNamesAlias[r] + ")") : "" ) ;
    }
    
    public static Enumeration<String> getRegNAliasNames( )
    {
        Vector<String> v = new Vector<String>() ;
        Utils.addAllTo( v, Utils.asVector( regNames ) ) ;
        Utils.addAllTo( v, Utils.asVector( regNamesAlias ) ) ;
        return v.elements() ;
    }
    
    public static int findRegOfName( String nm ) // should be in some kinda environment
    {
        for ( int i = 0 ; i < regNames.length ; i++ )
        {
            if ( regNames[i].equals( nm ) )
                return i ;
        }
        for ( int i = 0 ; i < regNamesAlias.length ; i++ )
        {
            if ( regNamesAlias[i].equals( nm ) )
                return i ;
        }
        return -1 ;
    }
    
    private boolean checkWithinMemory( int r )
    {
        boolean res ;
        if ( res = ( r < 0 || r >= cells.length ) )
        {
            messenger.println
            	( java.text.MessageFormat.format
            		( "attempt to access nonexisting register {0,number,integer}"
            		, new Object[] { new Integer(r) }
            		) ) ;
        }
        return ! res ;
    }
    
    public int getReg( int r )
    {
        int res = 0 ;
        if ( checkWithinMemory( r ) )
        {
            res = cells[ r ] ;
        }
        return res ;
    }
    
    public int getRegDispl( int r, int d )
    {
        return getReg( r ) + d ;
    }
    
    public int getRegDisplInd( int r, int d )
    {
        return memory.getAt( getRegDispl( r, d ) ) ;
    }
    
    public int getRegInd( int r )
    {
        return getRegDisplInd( r, 0 ) ;
    }
    
    class UndoRegistersModification implements Modification
    {
    	private int offset, value ;
    	
    	UndoRegistersModification( int o, int v )
    	{
    		offset = o ;
    		value = v ;
    	}
    	
    	public void modify()
    	{
    		setReg( offset, value ) ;
    	}
    }
    
    public void setReg( int r, int v )
    {
        if ( checkWithinMemory( r ) )
        {
            int oldv = cells[ r ] ;
            cells[ r ] = v ;
            fireCellChange( this, r, oldv, new UndoRegistersModification( r, oldv ) ) ;
        }
    }
    
    public void setRegDisplInd( int r, int d, int v )
    {
        memory.setAt( getRegDispl( r, d ), v ) ;
    }
    
    public void setRegInd( int r, int v )
    {
        setRegDisplInd( r, 0, v ) ;
    }
    
    public int swapReg( int r, int v )
    {
    	int oldVal = getReg( r ) ;
    	setReg( r, v ) ;
        return oldVal ;
    }
    
    public void adjustReg( int r, int v )
    {
        setReg( r, getReg( r ) + v ) ;
    }
    
    public void setPC( int v )
    {
        setReg( PC, v ) ;
    }
    
    public int getPC( )
    {
        return getReg( PC ) ;
    }
    
    public void adjustPC( int v )
    {
        setPC( getPC() + v ) ;
    }
    
    public void setSP( int v )
    {
        setReg( SP, v ) ;
    }
    
    public int getSP( )
    {
        return getReg( SP ) ;
    }
    
    public void adjustSP( int v )
    {
        setSP( getSP() + v ) ;
    }
    
    public void setMP( int v )
    {
        setReg( MP, v ) ;
    }
    
    public int getMP( )
    {
        return getReg( MP ) ;
    }
    
    public void adjustMP( int v )
    {
        setMP( getMP() + v ) ;
    }
    
    public void setHP(int v) {
    	
    	setReg (HP, v);
    }
    
    public int getHP() {
    	
    	return getReg(HP);
    }
    
    public void adjustHP(int v) {

        setHP(getHP() + v);
    }
    
    public int getTOS()
    {
        return getRegInd( SP ) ;
    }
    
    public String toString()
    {
    	return "Register PC=" + getReg( PC ) + " SP=" + getReg( SP )  + " MP=" + getReg( MP ) ;
    }
    
}
