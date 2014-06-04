/**
 * Simple Stack Machine
 *
 * Written by Atze Dijkstra, atze@cs.uu.nl,
 * Copyright Utrecht University.
 *
 */

package nl.uu.cs.ssm ;

public interface Messenger
{
    public void println( String s ) ;
    
    /**
     * Asks the user for an integer value (i.e. via a dialog)
     * @return Returns the integer value as provided by the user
     */
    public int promptInt();

    /**
     * Asks the user for a character (i.e. via a dialog)
     * @return Returns the unicode integer code point of the provided character
     */
    public int promptChar();

}