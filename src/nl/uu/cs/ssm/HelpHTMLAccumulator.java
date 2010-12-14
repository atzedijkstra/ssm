/**
 * Simple Stack Machine
 *
 * Written by Atze Dijkstra, atze@cs.uu.nl,
 * Copyright Utrecht University.
 *
 */

package nl.uu.cs.ssm ;

import java.util.Enumeration;

public class HelpHTMLAccumulator extends HelpAccumulator
{
    private boolean isOldHtml ;
    
    public HelpHTMLAccumulator( boolean old, String fileName )
    {
    	super( fileName ) ;
        isOldHtml = old ;
    }
    
    private void beginTag( String t, boolean end )
    {
        append( '<' ) ;
        if ( end )
            append( '/' ) ;
        append( t ) ;
    }
    
    private void endTag( )
    {
        append( '>' ) ;
    }
    
    private void tag( String t, boolean end, String params )
    {
        beginTag( t, end ) ;
        if ( params != null )
        {
            append( " " ) ;
            append( params ) ;
        }
        endTag() ;
    }
    
    private void tag( String t, boolean end )
    {
        tag( t, end, null ) ;
    }
    
    private void tagged( String t, String s )
    {
        tag( t, false ) ;
        append( s ) ;
        tag( t, true ) ;
    }
    
    public void beginHeadTitleBody( String t )
    {
        if ( ! isOldHtml )
        {
            tag( "!doctype", false, "html public \"-//w3c//dtd html 4.0 transitional//en\"" ) ;
            tag( "html", false ) ;
            tag( "head", false ) ;
            tag( "meta", false, "http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"" ) ;
            tag( "meta", false, "name=\"GENERATOR\" content=\"Simple Stack Machine, " + Config.version() + ", " + Config.versionDate() + "\"" ) ;
            tag( "title", false ) ;
            append( t ) ;
            tag( "title", true ) ;
            tag( "head", true ) ;
        }
        tag( "body", false ) ;
    }
    
    public void endHeadTitleBody()
    {
        tag( "body", true ) ;
        if ( ! isOldHtml )
        {
            tag( "html", true ) ;
        }
    }
    
    private void beginA( String name, String href )
    {
        beginTag( "a", false ) ;
        if ( href != null )
            define( "href", href ) ;
        if ( name != null )
            define( "name", name ) ;
        endTag() ;
    }
    
    private void endA()
    {
        tag( "a", true ) ;
    }
    
    public void anchor( String a )
    {
        beginA( a, null ) ;
        endA( ) ;
    }
    
    public void a( String href, String title )
    {
        beginA( null, href ) ;
        append( title ) ;
        endA( ) ;
    }
    
    public void anchorA( String href, String title )
    {
        a( "#" + href, title ) ;
    }
    
    private void beginPara( String align )
    {
        beginTag( "p", false ) ;
        if ( align != null )
            define( "align", align ) ;
        endTag() ;
    }
    
    public void beginPara(  )
    {
        beginPara( null ) ;
    }
    
    public void endPara()
    {
        tag( "p", true ) ;
    }
    
    public void beginCentered(  )
    {
        beginPara( "center" ) ;
    }
    
    public void endCentered()
    {
        endPara() ;
    }
    
    public void beginSectionTitle()
    {
        tag( "h2", false ) ;
    }
    
    public void endSectionTitle()
    {
        tag( "h2", true ) ;
    }
    
    public void beginSubsectionTitle()
    {
        tag( "h3", false ) ;
    }
    
    public void endSubsectionTitle()
    {
        tag( "h3", true ) ;
    }
    
    public void beginTable( int nCols, int widthPerc, int[] colWidthWeight )
    {
        tag( "table", false, "BORDER COLS=" + nCols + " WIDTH=\"" + (widthPerc == 0 ? 100 : widthPerc) + "%\"" ) ;
    }
    
    public void endTable()
    {
        tag( "table", true ) ;
    }
    
    public void beginTableRow( )
    {
        tag( "tr", false ) ;
    }
    
    public void endTableRow()
    {
        tag( "tr", true ) ;
    }
    
    public void beginTableData( )
    {
        tag( "td", false ) ;
    }
    
    public void endTableData()
    {
        tag( "td", true ) ;
    }
    
    public void beginBlockQuote( )
    {
        tag( "blockquote", false ) ;
    }
    
    public void endBlockQuote()
    {
        tag( "blockquote", true ) ;
    }
    
    public void linebreak()
    {
        tag( "br", false ) ;
    }
    
    private void font( String f, String s )
    {
        tag( f, false ) ;
        append( s ) ;
        tag( f, true ) ;
    }
    
    public void bold( String s )
    {
        font( "b", s ) ;
    }
    
    private void define( String nm, String val )
    {
        append( " " ) ;
        append( nm ) ;
        append( "=" ) ;
        string( val ) ;
    }
    
    public void mathEquationList( Enumeration<String> e, char subscript )
    {
        for ( ; e.hasMoreElements() ; )
        {
            append( e.nextElement() ) ;
            if ( e.hasMoreElements() )
	            linebreak() ;
        }
    }

    public void beginAttributeList( )
    {
    }
    
    public void endAttributeList( )
    {
    }
    
    public void attributeName( String s )
    {
    	subsectionTitle( s ) ;
    }
    
    public void beginAttributeValue( )
    {
    	beginBlockQuote() ;
    }
    
    public void endAttributeValue( )
    {
    	endBlockQuote() ;
    }
    
    public void beginVerbatimList()
    {
        tag( "pre", false ) ;
        nl() ;
    }
    
    public void endVerbatimList()
    {
        tag( "pre", true ) ;
        nl() ;
    }
    
    public void verbatimLine( String s )
    {
    	append( s ) ;
    	nl() ;
    }
    
    
}