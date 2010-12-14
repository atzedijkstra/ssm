/* 
	Title:			Simple Stack Machine Runner
	Author:			atze
	Description:	
*/

package nl.uu.cs.ssmui;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class Labels
{
    public static final int UNDEFINED = Integer.MAX_VALUE ;
    
    private class Label
    {
        String      label ;
        boolean     isAbsolute ;
        boolean     isWhatItIs ;
        boolean     isResolved ;
        int         row ;
        
        Label( String nm, int r, boolean reslv )
        {
            label = nm ;
            row = r ;
            isAbsolute = false ;
            isResolved = reslv ;
            isWhatItIs = false ;
        }
    }
    
    protected class UnresolvedLabelUsage
    {
        Label       label ;
        int         row ;
        int         insideInstOffset ;
        int         pcOffset ;
        boolean     isRelative ;
        
        UnresolvedLabelUsage( Label l, int r, int offs, int pcoffs, boolean isRel )
        {
            label = l ;
            row = r ;
            insideInstOffset = offs ;
            pcOffset = pcoffs ;
            isRelative = isRel ;
        }
        
        public String toString()
        {
            return label.label ;
        }
    }
    
    private Hashtable<String,Label>       labels              ;
    private Vector<UnresolvedLabelUsage>          unresolvedUsages    ;
    
    private CodeRowSupplier codeSupplier        ;
    
    protected Labels( CodeRowSupplier cs )
    {
        codeSupplier = cs ;
        reset(null) ;
    }
    
    protected void reset( Enumeration<String> regNames )
    {
        if ( regNames != null )
        {
            labels = new Hashtable<String,Label>() ;
            for ( ; regNames.hasMoreElements() ; )
            {
                String nm = regNames.nextElement() ;
                defineRegName( nm, nl.uu.cs.ssm.Registers.findRegOfName( nm ) ) ; // not neat, ??? TBD
            }
        }
        unresolvedUsages = new Vector<UnresolvedLabelUsage>() ;
    }
    
    protected void shiftAt( int row, int v )
    {
    }
    
    private Label findLabelByName( String nm )
    {
        Label res = labels.get( nm ) ;
        return res ;
    }
    
    protected String findLabelNameAtRow( int row )
    {
        for ( Enumeration<Label> e = labels.elements() ; e.hasMoreElements() ; )
        {
            Label l = e.nextElement() ;
            if ( l.row == row )
                return l.label ;
        }
        return "" ;
    }
    
    protected boolean labelIsDefined( String nm )
    {
        return findLabelByName( nm ) != null ;
    }
    
    private void defineRegName( String nm, int val )
    {
        Label l = new Label( nm, val, true ) ;
        l.isAbsolute = l.isWhatItIs = true ;
        labels.put( nm, l ) ;
    }
    
    private Label defineLabel( String nm, int row, boolean resolved )
    {
        Label l ;
        if ( ( l = findLabelByName( nm ) ) != null )
        {
            if ( ! l.isResolved )
            {
                l.isResolved = true ;
                l.row = row ;
            }
        }
        else
        {
            l = new Label( nm, row, resolved ) ;
            labels.put( nm, l ) ;
        }
        //System.out.println( "defined label " + nm + " row=" + row + " loc=" + codeSupplier.memLocOfRow(row) ) ;
        return l ;
    }
    
    protected void defineLabel( String nm, int row )
    {
        defineLabel( nm, row, true ) ;
    }
    
    protected int useLabel( String nm, int row, int offset, int pcOffset, boolean isRelative )
    {
        return useLabel( nm, row, offset, pcOffset, true, isRelative) ;
    }
    
    private int useLabel( String nm, int row, int offset, int pcOffset, boolean doAddUnresolved, boolean isRelative )
    {
        int res ;
        Label l = findLabelByName( nm ) ;
        if ( l != null && l.isResolved )
        {
            if ( l.isWhatItIs )
            {
                res = l.row ;
            }
            else
            {
                int labelLoc = codeSupplier.memLocOfRow( l.row ) ;
                int useLoc = codeSupplier.memLocOfRow( row ) + pcOffset ;
                res = ( (l.isAbsolute || (! isRelative)) ? labelLoc : labelLoc - useLoc ) ;
                //System.out.println( "use label " + nm + " res=" + res + " useloc=" + useLoc ) ;
            }
        }
        else
        {
            if ( l == null )
            {
                l = defineLabel( nm, 0, false ) ;
            }
            if ( doAddUnresolved )
                unresolvedUsages.addElement( new UnresolvedLabelUsage( l, row, offset, pcOffset, isRelative ) ) ;
            res = UNDEFINED ;
        }
        return res ;
    }
    
    protected Vector<UnresolvedLabelUsage> resolveUnresolved()
    {
        Vector<UnresolvedLabelUsage> stillUnresolved = new Vector<UnresolvedLabelUsage>() ;
        
        for ( Enumeration<UnresolvedLabelUsage> ls = unresolvedUsages.elements() ; ls.hasMoreElements() ; )
        {
            UnresolvedLabelUsage ul = ls.nextElement() ;
            int val = useLabel( ul.label.label, ul.row, ul.insideInstOffset, ul.pcOffset, false, ul.isRelative ) ;
            if ( val == UNDEFINED )
                stillUnresolved.addElement( ul ) ;
            else
                codeSupplier.setInstrArgAt( ul.row, ul.insideInstOffset, val ) ;
        }
        
        return stillUnresolved ;
    }
    
}
