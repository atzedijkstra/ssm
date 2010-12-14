/* 
	Title:			Simple Stack Machine Runner
	Author:			atze
	Description:	
*/

package nl.uu.cs.ssmui;

interface CodeRowSupplier
{
    public int memLocOfRow( int row ) ;
    
    public void setInstrArgAt( int row, int argOffset, int val ) ;

}
