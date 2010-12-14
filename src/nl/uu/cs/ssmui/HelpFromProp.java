/* 
	Runner.java

	Title:			Simple Stack Machine Runner
	Author:			atze
	Description:	
*/

package nl.uu.cs.ssmui;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import nl.uu.cs.ssm.HelpAccumulator;
import nl.uu.cs.ssm.HelpSupplier;
import nl.uu.cs.ssm.Utils;

public class HelpFromProp
	implements HelpSupplier
{
	private static final char 		US 				= '_' 			;
	private static final char 		SEP 			= ',' 			;
	private static final char 		INDIRECT		= '@' 			;
	
	private static final String 	PROP_DESCR 		= "descr" 		;
	private static final String 	PROP_PREPOST 	= "prepost" 	;
	private static final String 	PROP_EXAMPLE 	= "example" 	;
	
	private boolean                 doForInstr                       ;
	
	private static Properties getProps( String nm )
	{
		URL url = nm.getClass().getResource( "/Help/" + nm + ".prop" ) ;
		Properties props = new Properties() ;
		try
		{
			props.load( url.openStream() ) ;
		}
		catch( IOException ex )
		{
		}
		return props ;
	}
	
	private Properties props ;
	private Vector<String> topics = null ;
	private HelpSupplier instrHelpSupplier ;
	
	protected HelpFromProp( String propnm, HelpSupplier ih, boolean doForInstr )
	{
		this.doForInstr = doForInstr ;
		props = getProps( propnm ) ;
		instrHelpSupplier = ih ;
	}

	private void checkTopics()
	{
		if ( topics == null )
		{
			Hashtable<String,String> ts = new Hashtable<String,String>() ;
			for ( Enumeration ps = props.propertyNames() ; ps.hasMoreElements() ; )
			{
				String s = (String)ps.nextElement() ;
				if ( doForInstr )
				{
    				int i = s.indexOf( US ) ;
    				if ( i > 0 )
    					s = s.substring( 0, i ) ;
				}
				ts.put( s, s ) ;
			}
			topics = new Vector<String>( ) ;
			Utils.addAllTo( topics, ts.elements() ) ;
		}
	}
	
    public Enumeration<String> getTopics()
    {
    	checkTopics() ;
    	return topics.elements() ;
    }
    
    public String getHelpSupplierName()
    {
    	return doForInstr ? "Semantics" : "General" ;
    }
    
    public String getShortSummaryForTopic( String topic )
    {
        return topic + " " + getHelpSupplierName().toLowerCase() ;
    }
    
    private String getTopicProperty( String topicProp )
    {
    	String p = props.getProperty( topicProp ) ;
    	if ( p != null && p.length() > 0 && p.charAt(0) == INDIRECT )
    	{
    		p = getTopicProperty( p.substring(1) ) ;
    	}
    	if ( p == null )
    	    p = "No info" ;
    	return p ;
    }
    
    private String getTopicProperty( String topic, String prop )
    {
    	return getTopicProperty( topic + US + prop ) ;
    }
    
    /*
    private void commasep( String s, HelpAccumulator acc )
    {
        Vector v = Utils.splitAt( s, SEP ) ;
        if ( v.size() < 1 )
            return ;
            
        int max = v.size() - 1 ;
        for ( int i = 0  ; i < max ; i++ )
        {
            acc.append( ((String)v.elementAt(i)).trim() ) ;
            acc.linebreak() ;
        }
        acc.append( ((String)v.elementAt( max )).trim() ) ;
    }
    */
    
    private Enumeration<String> splitByComma( String s )
    {
        Vector<String> v = Utils.splitAt( s, SEP ) ;
        Vector<String> vv = new Vector<String>() ;
        for ( Enumeration<String> e = v.elements() ; e.hasMoreElements() ; )
        	vv.addElement( e.nextElement().trim() ) ;
        return vv.elements() ;
    }
    
    public void getHelpForTopic( String topic, HelpAccumulator acc )
    {
        Enumeration<String> e ;
        
        if ( doForInstr )
        {
            instrHelpSupplier.getHelpForTopic( topic, acc ) ;
            acc.linebreak() ;
            
            acc.beginAttributeList() ;
            acc.attributeName( "Description" ) ;
            acc.beginAttributeValue() ;
            acc.append( getTopicProperty( topic, PROP_DESCR ) ) ;
            acc.endAttributeValue() ;

            acc.attributeName( "Pre and Post State" ) ;
            acc.beginAttributeValue() ;
            e = splitByComma( getTopicProperty( topic, PROP_PREPOST) ) ;
            acc.mathEquationList( e, US ) ;
            acc.endAttributeValue() ;

            acc.attributeName( "Example" ) ;
            acc.beginAttributeValue() ;
            e = splitByComma( getTopicProperty( topic, PROP_EXAMPLE) ) ;
            acc.verbatimList( e ) ;
            acc.endAttributeValue() ;
            acc.endAttributeList() ;
        }
        else
        {
            acc.beginBlockQuote() ;
            acc.append( getTopicProperty( topic ) ) ;
            acc.endBlockQuote() ;
        }
    }
    
}
