/**
 * Simple Stack Machine
 *
 * Written by Atze Dijkstra, atze@cs.uu.nl,
 * Copyright Utrecht University.
 *
 */

package nl.uu.cs.ssm ;

import java.io.Closeable;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventListener;

public class MachineState extends Model
{
    protected int           stackBottom     ;
    protected int           stackGrowthDir  ;
    
    private final int startAddressOfHeap;
    
    protected int           code            ;
    protected int           instrPC         ;
    protected Instruction   instr           ;
    protected int           nInlineOpnds    ;
    protected int           inlineOpnds[]   ;
    protected Memory		memory			;
    protected Registers		registers		;
    protected MemoryUser    memoryUser      ;
    protected ArrayList<Closeable> filePtrs;
    
    public    boolean       isHalted        ;
    
    public MachineState( int initMemCapacity, int startAddressOfHeap, Messenger m )
    {
    	memory = new Memory( initMemCapacity, m ) ;
    	registers = new Registers( memory, m ) ;
    	stackGrowthDir = 1 ;
    	this.startAddressOfHeap = startAddressOfHeap;
    	filePtrs = new ArrayList<Closeable>();
    	reset() ;
    }
    
    protected void fireStateChange( MachineStateEvent ev )
    {
    	for( Enumeration<EventListener> e = getListeners() ; e.hasMoreElements() ; )
    	{
    		MachineStateListener l = (MachineStateListener)e.nextElement() ;
    		l.stateChanged( ev ) ;
    	}
    }

    public void addMachineStateListener( MachineStateListener l )
    {
    	addListener( l ) ;
    }
    
    public void removeMachineStateListener( MachineStateListener l )
    {
    	removeListener( l ) ;
    }
    
    public void reset( )
    {
		memory.reset() ;
		registers.reset() ;
		resetToInitialState() ;
    }
    
    public void resetToInitialState()
    {
        registers.setPC( 0 ) ;
        stackBottom = memory.getUsedForCode() + 16 ;
        registers.setSP( stackBottom - stackGrowthDir ) ;
        registers.setMP( registers.getSP() ) ;
        registers.setHP(startAddressOfHeap);
        isHalted = false ;
        try
        {
            for(Closeable f : filePtrs)
            {
                if (f != null)
                {
                    f.close();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        filePtrs = new ArrayList<Closeable>();
    }
    
    public int dir( int v )
    {
    	return v * stackGrowthDir ;
    }
    
    public void setCurrentInstr( int pc, int code, Instruction instr )
    {
        this.code = code ;
        instrPC = pc ;
        this.instr = instr ;
        nInlineOpnds = instr.getNrInlineOpnds() ;
        if ( nInlineOpnds > 0 )
            inlineOpnds = new int[ nInlineOpnds ] ;
    }
    
    public Memory getMemory()
    {
    	return memory ;
    }
    
    public int getStackBottom()
    {
    	return stackBottom ;
    }
    
    public int getStartAddressOfHeap() {
    	
    	return startAddressOfHeap;
    }
        
    public Registers getRegisters()
    {
    	return registers ;
    }
    
    public boolean stackIsEmpty()
    {
        return stackBottom == registers.getSP() ;
    }
    
    public int stackTop()
    {
        return registers.getRegInd( Registers.SP ) ;
    }
    
    public int getSR( )
    {
        return stackIsEmpty() ? 0 : stackTop() ;
    }
    
    public String getSRAsString( )
    {
        int sr = getSR() ;
        return   "Z=" + ((sr & Instruction.ZZ) >> Instruction.SR_Z) +
                " C=" + ((sr & Instruction.CC) >> Instruction.SR_C) +
                " V=" + ((sr & Instruction.VV) >> Instruction.SR_V) +
                " N=" + ((sr & Instruction.NN) >> Instruction.SR_N) ;
    }
    
    class UndoStateModification implements Modification
    {
    	private boolean wasHalted ;
    	
    	UndoStateModification( boolean h )
    	{
    		wasHalted = h ;
    	}
    	
    	public void modify()
    	{
    		isHalted = wasHalted ;
    	}
    }
    
    public void setHalted()
    {
        isHalted = true ;
        fireStateChange( new MachineStateEvent( this, new UndoStateModification( false ) ) ) ;
    }
    
    public boolean isHalted()
    {
        return isHalted ;
    }
    
    public String toString()
    {
        return "state code=" + code + " instr-pc=" + instrPC + " n-inl=" + nInlineOpnds ;
    }

    /**
     * Opens a file
     * @param fname Name of the file
     * @param readOnly True if the file should be open for reading, false otherwise
     * @return The 'file pointer' of the file
     * @throws IOException
     */
    public int openFile(String fname, boolean readOnly) throws IOException {
        if (readOnly)
        {
            filePtrs.add(new FileReader(fname));
        }
        else {
            filePtrs.add(new FileWriter(fname));
        }
        return filePtrs.size() - 1;
    }

    /**
     * Reads a unicode character from the file indicated by index
     * @param index The 'file pointer' of the file
     * @return The unicode codepoint of the read character
     * @throws IOException
     */
    public int readFromFile(int index) throws IOException {
        FileReader file = (FileReader) filePtrs.get(index);
        int i16 = file.read(); // UTF-16 as int
        char c16 = (char)i16; // UTF-16
        if (Character.isHighSurrogate(c16))
        {
            int low_i16 = file.read(); // low surrogate UTF-16 as int
            char low_c16 = (char)low_i16;
            return Character.toCodePoint(c16, low_c16);
        }
        return i16;
    }

    /**
     * Writes a unicode character to the file indicated by the index
     * @param n The unicode codepoint of the character
     * @param index The 'file pointer' of the file
     * @return index The 'file pointer' of the file
     * @throws IOException
     */
    public int writeToFile(int n, int index) throws IOException {
        FileWriter file = (FileWriter) filePtrs.get(index);
        file.write(Utils.codePointToString(n));
        return index;
    }

    /**
     * Closes a file
     * @param index The 'file pointer' of the file
     * @throws IOException
     */
    public void closeFile(int index) throws IOException {
        filePtrs.get(index).close();
        // cannot remove from filePtrs, as that messes up index
        filePtrs.set(index, null);
    }
}
