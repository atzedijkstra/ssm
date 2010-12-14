/**
 * Simple Stack Machine
 *
 * Written by Atze Dijkstra, atze@cs.uu.nl,
 * Copyright Utrecht University.
 *
 */

package nl.uu.cs.ssm ;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Instruction
	implements HelpSupplier
{
    /**
     * Misc constants
     */
	protected final static int		UNKNOWN     = -1 	    ;
	
	protected final static int      nWordBits   = 32        ;

	protected final static int      CONST_TRUE  = 0xFFFFFFFF;
	protected final static int      CONST_FALSE = 0x00000000;

    /**
     * Condition codes
     */
	protected final static int		SR_Z		= 0 	    ;
	protected final static int		SR_C		= 1 	    ;
	protected final static int		SR_V		= 2 	    ;
	protected final static int		SR_N		= 3 	    ;
	
	protected final static int		ZZ		    = 1<<SR_Z	;
	protected final static int		CC		    = 1<<SR_C	;
	protected final static int		VV		    = 1<<SR_V	;
	protected final static int		NN		    = 1<<SR_N	;
	
	private final static String     ccNames[]   = { "Z", "C", "V", "N" } ;

    /**
     * Categories of inline operands
     */
	protected final static int		OPND_INL_PC_REL		= 0		;
	protected final static int		OPND_INL_REG		= 1		;
	protected final static int		OPND_INL_AS_IS		= 2		;
     
    /**
     * Categories of instructions
     */
	protected final static int		CTG_OP		= 0		;
	protected final static int		CTG_BINOP	= 1		;
	protected final static int		CTG_UNOP	= 2		;
	protected final static int		CTG_PRAGMA	= 3		;
	protected final static int		CTG_TRAP	= 4		;
	protected final static int		CTG_BRCC	= 5		;
	protected final static int		CTG_META	= 6		;
	
	private final static String[]   categNames  = { "Normal instruction", "Binary operator", "Unary operator", "Pragma", "Trap", "Branch on condition", "Meta" } ;
    
    /**
     * Binary int instructions
     */
	protected final static int		BI_ADD		= 1		;
	protected final static int		BI_AND		= 2		;
	//protected final static int		BI_CMP		= 3		;
	protected final static int		BI_DIV		= 4		;
	protected final static int		BI_LSL		= 5		;
	protected final static int		BI_LSR		= 6		;
	protected final static int		BI_MOD		= 7		;
	protected final static int		BI_MUL		= 8		;
	protected final static int		BI_OR		= 9		;
	protected final static int		BI_ROL		= 10	;
	protected final static int		BI_ROR		= 11	;
	protected final static int		BI_SUB		= 12	;
	protected final static int		BI_XOR		= 13	;
	
	protected final static int		BI_EQ		= 14	;
	protected final static int		BI_NE		= 15	;
	protected final static int		BI_LT		= 16	;
	protected final static int		BI_GT		= 17	;
	protected final static int		BI_LE		= 18	;
	protected final static int		BI_GE		= 19	;
    
    /**
     * Unary int instructions
     */
	protected final static int		UI_NEG		= 32	;
	protected final static int		UI_NOT		= 33	;

    /**
     * Branch instructions
     */
	/*
	protected final static int		BR_CC		= 64	;
	protected final static int		BR_CS		= 65	;
	protected final static int		BR_EQ		= 66	;
	protected final static int		BR_GE		= 67	;
	protected final static int		BR_GT		= 68	;
	protected final static int		BR_HI		= 69	;
	protected final static int		BR_LE		= 70	;
	protected final static int		BR_LS		= 71	;
	protected final static int		BR_LT		= 72	;
	protected final static int		BR_MI		= 73	;
	protected final static int		BR_NE		= 74	;
	protected final static int		BR_PL		= 75	;
	*/
    
    /**
     * Other instructions
     */
	protected final static int		I_ADJS		= 100	;
	protected final static int		I_BRA		= 104	;
	protected final static int		I_BRF		= 108	;
	protected final static int		I_BRT		= 109	;
	protected final static int		I_BSR		= 112	;
	protected final static int		I_HALT		= 116	;
	protected final static int		I_JSR		= 120	;
	protected final static int		I_LDA		= 124	;
	protected final static int		I_LDMA		= 126	;
	protected final static int		I_LDAA		= 128	;
	protected final static int		I_LDC		= 132	;
	protected final static int		I_LDL		= 136	;
	protected final static int		I_LDML		= 138	;
	protected final static int		I_LDLA		= 140	;
	protected final static int		I_LDR		= 144	;
	protected final static int		I_LDRR		= 148	;
	protected final static int		I_LDS		= 152	;
	protected final static int		I_LDMS		= 154	;
	protected final static int		I_LDSA		= 156	;
	protected final static int		I_LINK		= 160	;
	protected final static int		I_NOP		= 164	;
	protected final static int		I_RET		= 168	;
	protected final static int		I_STA		= 172	;
	protected final static int		I_STMA		= 174	;
	protected final static int		I_STL		= 176	;
	protected final static int		I_STML		= 178	;
	protected final static int		I_STR		= 180	;
	protected final static int		I_STS		= 184	;
	protected final static int		I_STMS		= 186	;
	protected final static int		I_SWP		= 188	;
	protected final static int		I_SWPR		= 192	;
	protected final static int		I_SWPRR		= 196	;
	protected final static int		I_TRAP		= 200	;
	protected final static int		I_UNLINK	= 204	;
	protected final static int      I_LDH       = 208   ;
	protected final static int      I_LDMH      = 212   ;
	protected final static int      I_STH       = 214   ;
	protected final static int      I_STMH      = 216   ;
    
    /**
     * Pragma's
     */
	protected final static int		PR_DB		= 250	;
	protected final static int		PR_DC		= 251	;
	protected final static int		PR_DS		= 252	;
	protected final static int		PR_DW		= 253	;
    
    /**
     * Traps
     */
	protected final static int		TR_PR_INT	= 0 	;

    /**
     * Metas
     */
	protected final static int		M_ANNOTE	= 255	;

    private static Hashtable<Integer,Instruction>        codeToInstr ;
    private static Hashtable<String,Instruction>        reprToInstr ;
    
    private static void defBinOp( String repr, int code )
    {
        new Instruction( repr, CTG_BINOP, code, 0, 2, 1, OPND_INL_AS_IS, null ) ;
    }
    
    private static void defMeta( String repr, int code, int nI, MetaInstrInstantiator mii )
    {
        new Instruction( repr, CTG_META, code, nI, 0, 0, OPND_INL_AS_IS, mii ) ;
    }
    
    private static Instruction defBranchOp( String repr, int code )
    {
        return new Instruction( repr, CTG_BRCC, code, 1, 1, 0, OPND_INL_PC_REL, null ) ;
    }
    
    private static void defInstr( String repr, int code, int nI, int nS, int nR, int inlOpndKind )
    {
        new Instruction( repr, CTG_OP, code, nI, nS, nR, inlOpndKind, null ) ;
    }
    
    private static void defPragma( String repr, int code, int nI, int nS, int nR )
    {
        new Instruction( repr, CTG_PRAGMA, code, nI, nS, nR, OPND_INL_AS_IS, null ) ;
    }
    
    private static void defRegInstr( String repr, int code, int nI, int nS, int nR )
    {
        new Instruction( repr, CTG_OP, code, nI, nS, nR, OPND_INL_REG, null ) ;
    }
    
    private static void defUnOp( String repr, int code )
    {
        new Instruction( repr, CTG_UNOP, code, 0, 1, 1, OPND_INL_AS_IS, null ) ;
    }
    
    private static void defTrap( String repr, int code )
    {
        new Instruction( repr, CTG_TRAP, code, 0, 0, 0, OPND_INL_AS_IS, null ) ;
    }
    
    static
    {
        codeToInstr = new Hashtable<Integer,Instruction>() ;
        reprToInstr = new Hashtable<String,Instruction>() ;
        
        defBinOp      ( "add" , BI_ADD    ) ;
        defBinOp      ( "and" , BI_AND    ) ;
        //defBinOp      ( "cmp" , BI_CMP    ) ;
        defBinOp      ( "div" , BI_DIV    ) ;
        defBinOp      ( "lsl" , BI_LSL    ) ;
        defBinOp      ( "lsr" , BI_LSR    ) ;
        defBinOp      ( "mod" , BI_MOD    ) ;
        defBinOp      ( "mul" , BI_MUL    ) ;
        defBinOp      ( "or"  , BI_OR     ) ;
        defBinOp      ( "rol" , BI_ROL    ) ;
        defBinOp      ( "ror" , BI_ROR    ) ;
        defBinOp      ( "sub" , BI_SUB    ) ;
        defBinOp      ( "xor" , BI_XOR    ) ;
        
        defBinOp      ( "eq"  , BI_EQ     ) ;
        defBinOp      ( "ne"  , BI_NE     ) ;
        defBinOp      ( "lt"  , BI_LT     ) ;
        defBinOp      ( "gt"  , BI_GT     ) ;
        defBinOp      ( "le"  , BI_LE     ) ;
        defBinOp      ( "ge"  , BI_GE     ) ;
        
        /*
        defBranchOp   ( "bcc" , BR_CC     ).defCCForBranch(  0,    CC    							) ;
        defBranchOp   ( "bcs" , BR_CS     ).defCCForBranch( CC,    CC    							) ;
        defBranchOp   ( "beq" , BR_EQ     ).defCCForBranch( ZZ,    ZZ    							) ;
        defBranchOp   ( "bge" , BR_GE     ).defCCForBranch( NN|VV, NN|VV,     0,  NN|VV    			) ;
        defBranchOp   ( "bgt" , BR_GT     ).defCCForBranch( NN|VV, NN|VV|ZZ,  0,  NN|VV|ZZ 			) ;
        defBranchOp   ( "bhi" , BR_HI     ).defCCForBranch(  0,    ZZ|CC 							) ;
        defBranchOp   ( "ble" , BR_LE     ).defCCForBranch( NN,    NN|VV,     VV, NN|VV,    ZZ, ZZ 	) ;
        defBranchOp   ( "bls" , BR_LS     ).defCCForBranch( CC,    CC,        ZZ, ZZ       			) ;
        defBranchOp   ( "blt" , BR_LT     ).defCCForBranch( NN,    NN|VV,     VV, NN|VV    			) ;
        defBranchOp   ( "bmi" , BR_MI     ).defCCForBranch( NN,    NN    							) ;
        defBranchOp   ( "bne" , BR_NE     ).defCCForBranch(  0,    ZZ    							) ;
        defBranchOp   ( "bpl" , BR_PL     ).defCCForBranch(  0,    NN    							) ;
        */
        
        defInstr      ( "ajs" , I_ADJS    , 1, UNKNOWN, 0, OPND_INL_AS_IS ) ;
        defInstr      ( "bra" , I_BRA     , 1, 0, 0, OPND_INL_PC_REL ) ;
        defInstr      ( "brf" , I_BRF     , 1, 1, 0, OPND_INL_PC_REL ) ;
        defInstr      ( "brt" , I_BRT     , 1, 1, 0, OPND_INL_PC_REL ) ;
        defInstr      ( "bsr" , I_BSR     , 1, 0, 1, OPND_INL_PC_REL ) ;
        defInstr      ( "halt", I_HALT    , 0, 0, 0, OPND_INL_AS_IS ) ;
        defInstr      ( "jsr" , I_JSR     , 0, 1, 1, OPND_INL_AS_IS ) ;
        defInstr      ( "lds" , I_LDS     , 1, 0, 1, OPND_INL_AS_IS ) ;
        defInstr      ( "ldms", I_LDMS    , 2, 0, UNKNOWN, OPND_INL_AS_IS ) ;
        defInstr      ( "ldsa", I_LDSA    , 1, 0, 1, OPND_INL_AS_IS ) ;
        defInstr      ( "lda" , I_LDA     , 1, 1, 1, OPND_INL_AS_IS ) ;
        defInstr      ( "ldma", I_LDMA    , 2, 1, UNKNOWN, OPND_INL_AS_IS ) ;
        defInstr      ( "ldaa", I_LDAA    , 1, 1, 1, OPND_INL_AS_IS ) ;
        defInstr      ( "ldc" , I_LDC     , 1, 0, 1, OPND_INL_AS_IS ) ;
        defInstr      ( "ldl" , I_LDL     , 1, 0, 1, OPND_INL_AS_IS ) ;
        defInstr      ( "ldml", I_LDML    , 2, 0, UNKNOWN, OPND_INL_AS_IS ) ;
        defInstr      ( "ldla", I_LDLA    , 1, 0, 1, OPND_INL_AS_IS ) ;
        defInstr      ( "link", I_LINK    , 1, 0, UNKNOWN, OPND_INL_AS_IS ) ;
        defInstr      ( "nop" , I_NOP     , 0, 0, 0, OPND_INL_AS_IS ) ;
        defInstr      ( "ret" , I_RET     , 0, 1, 0, OPND_INL_AS_IS ) ;
        defInstr      ( "sts" , I_STS     , 1, 1, 0, OPND_INL_AS_IS ) ;
        defInstr      ( "stms", I_STMS    , 2, UNKNOWN, 0, OPND_INL_AS_IS ) ;
        defInstr      ( "sta" , I_STA     , 1, 2, 0, OPND_INL_AS_IS ) ;
        defInstr      ( "stma", I_STMA    , 2, UNKNOWN, 0, OPND_INL_AS_IS ) ;
        defInstr      ( "stl" , I_STL     , 1, 1, 0, OPND_INL_AS_IS ) ;
        defInstr      ( "stml", I_STML    , 2, UNKNOWN, 0, OPND_INL_AS_IS ) ;
        defInstr      ( "swp" , I_SWP     , 0, 2, 2, OPND_INL_AS_IS ) ;
        defInstr      ( "trap", I_TRAP    , 1, UNKNOWN, UNKNOWN, OPND_INL_AS_IS ) ;
        defInstr      ( "unlink", I_UNLINK, 0, UNKNOWN, 0, OPND_INL_AS_IS ) ;
        defInstr      ( "ldh", I_LDH      , 1, 1, 1, OPND_INL_AS_IS);
        defInstr      ( "ldmh", I_LDMH    , 2, 1, UNKNOWN, OPND_INL_AS_IS);
        defInstr      ( "sth", I_STH      , 0, 1, 1, OPND_INL_AS_IS);
        defInstr      ( "stmh", I_STMH    , 1, UNKNOWN, 1, OPND_INL_AS_IS);
        
        //defPragma     ( ".db" , PR_DB , UNKNOWN, 0, 0 ) ;
        //defPragma     ( ".dc" , PR_DC , 1, 0, 0 ) ;
        //defPragma     ( ".ds" , PR_DS , 1, 0, 0 ) ;
        //defPragma     ( ".dw" , PR_DW , UNKNOWN, 0, 0 ) ;
        
        defRegInstr   ( "ldr"  , I_LDR     , 1, 0, 1 ) ;
        defRegInstr   ( "ldrr" , I_LDRR    , 2, 0, 0 ) ;
        defRegInstr   ( "str"  , I_STR     , 1, 1, 0 ) ;
        defRegInstr   ( "swpr" , I_SWPR    , 1, 1, 1 ) ;
        defRegInstr   ( "swprr", I_SWPRR   , 2, 0, 0 ) ; // ??
        
        //defTrap       ( "printf"  , TR_PRINTF ) ;
        
        defUnOp       ( "neg" , UI_NEG    ) ;
        defUnOp       ( "not" , UI_NOT    ) ;

        defMeta       ( "annote"  , M_ANNOTE, 5, MetaInstrInstantiator.newAnnoteInstantiator() ) ;

    }
    
    public static Instruction findByRepr( String repr )
    {
        return reprToInstr.get( repr ) ;
    }
    
    public static Instruction findByCode( int code )
    {
        return codeToInstr.get( new Integer( code ) ) ;
    }
    
    
    
    private class CCInfo
    {
    	int check, mask ;
    	CCInfo( int c, int m )
    	{
    		check = c ;
    		mask = m ;
    	}
    }
    
    private class OpndInfo
    {
    	int inlineOpndKind ;
    	OpndInfo( int io ) { inlineOpndKind = io ; }
    }
    

    private String                  repr                    ;
    private int                     categ                   ;
    private int                     code                    ;
    private int                     nrInlineOpnds           ;
    private int                     nrStackOpnds            ;
    private int                     nrStackResults          ;
    private CCInfo[]	            brccInfo			    ;
    private OpndInfo[]	            opndInfo			    ;
    private MetaInstrInstantiator   metaInstrInstantiator   ;
    
    /**
     * Define an instruction
     */
    private Instruction
                ( String repr, int categ, int code
                , int nInl, int nSt, int nR
                , int inlOpndKind
                , MetaInstrInstantiator mii
                )
    {
        this.repr = repr ;
        this.categ = categ ;
        this.code = code ;
        this.nrInlineOpnds = nInl ;
        this.nrStackOpnds = nSt ;
        this.nrStackResults = nR ;
        
        codeToInstr.put( new Integer( code ), this ) ;
        reprToInstr.put( repr, this ) ;
        
        brccInfo = null ;
        
        metaInstrInstantiator = mii ;
        opndInfo = new OpndInfo[ nrInlineOpnds ] ;
        for ( int i = 0 ; i < opndInfo.length ; i++ )
            opndInfo[ i ] = new OpndInfo( inlOpndKind ) ;
    }
    
    private void defCCForBranch( int check1, int mask1, int check2, int mask2, int check3, int mask3 )
    {
    	brccInfo = new CCInfo[] { new CCInfo(check1, mask1), new CCInfo(check2, mask2), new CCInfo(check3, mask3) } ;
    }
    
    private void defCCForBranch( int check1, int mask1, int check2, int mask2 )
    {
    	brccInfo = new CCInfo[] { new CCInfo(check1, mask1), new CCInfo(check2, mask2) } ;
    }
    
    private void defCCForBranch( int check1, int mask1 )
    {
    	brccInfo = new CCInfo[] { new CCInfo(check1, mask1) } ;
    }
    
    protected boolean srMatchesCCInfo( int sr )
    {
    	if ( brccInfo != null )
    	{
    		for ( int i = 0 ; i < brccInfo.length ; i++ )
    		{
    			if ( ( sr & brccInfo[i].mask ) == brccInfo[i].check )
    				return true ;
    		}
    	}
    	return false ;
    } 
    
    public int getNrInlineOpnds()
    {
        return nrInlineOpnds ;
    }
    
    public String getRepr()
    {
        return repr ;
    }
    
    public String getRepr( int[] args )
    {
        String s = getRepr() ;
        for ( int i = 0 ; i < args.length ; i++ )
        {
        	String a ;
        	switch( opndInfo[ i ].inlineOpndKind )
        	{
        		case OPND_INL_REG :
        			a = Registers.getRegOrAliasName( args[i] ) ;
        			break ;
         		default :
        			a = Utils.asHex( args[i], false, true, false ) ;
        			break ;
	       	}
        	s = s + " " + a ;
        }
        return s ;
    }
    
    public String getRepr( String usedLabel )
    {
        return getRepr() + " " + usedLabel ;
    }
    
    public int getCode()
    {
        return code ;
    }
    
    public int getNrMemCells()
    {
        return getNrInlineOpnds() + 1 ;
    }
    
    protected int getNrStackOpnds()
    {
        return nrStackOpnds ;
    }
    
    protected int getNrStackResults()
    {
        return nrStackResults ;
    }
    
    public boolean isRelativeOpnd( int arg )
    {
        return opndInfo[ arg ].inlineOpndKind == OPND_INL_PC_REL ;
    }
    
    public boolean isMeta( )
    {
        return getCategory() == CTG_META ;
    }
    
    public MetaInstruction instantiateMeta( Vector<String> args )
    {
        return metaInstrInstantiator.instantiate( this, args ) ;
    }
    
    protected int getCategory()
    {
        return categ ;
    }
    
    private String getCategoryName()
    {
        return categNames[ getCategory() ] ;
    }
    
    private static Enumeration<String> getInstructionNames()
    {
    	return reprToInstr.keys() ;
    }
    
    public static HelpSupplier getHelpSupplier()
    {
    	return findByRepr( "halt" ) ;
    }
    
    public Enumeration<String> getTopics()
    {
    	return getInstructionNames() ;
    }
    
    public String getHelpSupplierName()
    {
    	return "Internal administration" ;
    }
    
    public String getShortSummaryForTopic( String topic )
    {
    	Instruction instr ;
    	if ( ( instr = findByRepr( topic ) ) != null )
    	{
    		return instr.getRepr() + " internal administration only" ;
        }
        return "" ;
    }
    
    private static String textForNr( int n )
    {
        return n == UNKNOWN ? "instruction dependent" : "" + n ;
    }
    
    private void getHelpOnCC( HelpAccumulator acc )
    {
        for ( int i = 0 ; i < brccInfo.length ; i++ )
        {
            acc.append( "(" ) ;
            for ( int cc = SR_Z ; cc <= SR_N ; cc++ )
            {
                if ( ((1<<cc) & brccInfo[i].mask) > 0 )
                {
                    acc.append( ccNames[cc] + "=" + ( (brccInfo[i].check >> cc) & 1 ) ) ;
                    if ( brccInfo[i].mask >> (cc+1) > 0 )
                    {
                        acc.append( " and " ) ;
                    }
                }
            }
            acc.append( ")" ) ;
            if ( i < (brccInfo.length - 1) )
            {
                acc.append( " or " ) ;
            }
        }
    }
    
    public void getHelpForTopic( String topic, HelpAccumulator acc )
    {
    	Instruction instr ;
    	if ( ( instr = findByRepr( topic ) ) == null )
    	{
    		acc.append( "?? should not happen, no help for " + topic ) ;
        }
        else
        {
            acc.beginBlockQuote() ;
            acc.beginTable( 5, 85, null ) ;
            acc.tableHeaderRow	( new Object[] 
           							{ "Instruction"
           							, "Nr of inline Opnds"
           							, "Nr of stack Opnds"
           							, "Nr of stack Results"
           							, "Instr code (hex)"
           							}
           						) ;
            acc.tableRow
                    ( new Object[] 
                        { topic
                        , textForNr( instr.getNrInlineOpnds() )
        				, textForNr( instr.getNrStackOpnds() )
        				, textForNr( instr.getNrStackResults() )
        				, acc.reprAsHex( instr.getCode() )
        				}
        			) ;
            acc.endTable() ;
            /*
            acc.linebreak() ;
            acc.append( "Is a " + instr.getCategoryName() + "." ) ;
            if ( instr.brccInfo != null )
            {
                acc.linebreak() ;
                acc.append( "Branches on " ) ;
                instr.getHelpOnCC( acc ) ;
            }
            */
            acc.endBlockQuote() ;
        }
    }
    
    public String toString()
    {
        return "instr " + repr + " code=(" + Utils.asHex(categ) + "/" + Utils.asHex(code) + ") ninl=" + nrInlineOpnds ;
    }
    
}
