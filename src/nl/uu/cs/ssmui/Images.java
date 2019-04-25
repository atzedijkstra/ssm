/* 
	Runner.java

	Title:			Simple Stack Machine Runner
	Author:			atze
	Description:	
*/

package nl.uu.cs.ssmui;

import javax.swing.ImageIcon;

public class Images 
{
	public static ImageIcon redball ;
	public static ImageIcon white ;
	public static ImageIcon check ;
	
	public static ImageIcon tbStart ;
	public static ImageIcon tbStartBack ;
	public static ImageIcon tbPause ;
	public static ImageIcon tbReset ;
	public static ImageIcon tbStep1 ;
	public static ImageIcon tbStep1Back ;
	
	public static ImageIcon tbNew ;
	public static ImageIcon tbLoad ;
	public static ImageIcon tbReload ;
	public static ImageIcon tbSave ;
	
	public static ImageIcon tbNewInstr ;
	
	private static ImageIcon getImage( String nm )
	{
		return new ImageIcon( Images.class.getResource( "/Images/" + nm + ".gif" ) ) ;
	}
	
	static
	{
		redball 	= getImage( "redball" 	    ) ;
		white 		= getImage( "white" 	    ) ;
		check 		= getImage( "check" 	    ) ;
		
		tbStart 	= getImage( "tb-start" 	    ) ;
		tbStartBack	= getImage( "tb-startback" 	) ;
		tbPause 	= getImage( "tb-pause" 	    ) ;
		tbReset     = getImage( "tb-reset" 	    ) ;
		tbStep1 	= getImage( "tb-step1" 	    ) ;
		tbStep1Back	= getImage( "tb-step1back"  ) ;
		
		tbNew 	    = getImage( "tb-new" 	    ) ;
		tbLoad 	    = getImage( "tb-load" 	    ) ;
		tbReload    = getImage( "tb-reload"     ) ;
		tbSave 	    = getImage( "tb-save" 	    ) ;
		
		tbNewInstr 	= getImage( "tb-newinstr" 	) ;
	}
}
