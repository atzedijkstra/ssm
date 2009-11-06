/**
 * Simple Stack Machine
 *
 * Written by Atze Dijkstra, atze@cs.uu.nl,
 * Copyright Utrecht University.
 *
 */

package nl.uu.cs.ssm ;

import java.util.* ;

public class MachineStateEvent extends UndoableEvent
{
    protected MachineStateEvent( Object src, Modification mdf )
    {
        super( src, mdf ) ;
    }
    
}