package nl.uu.cs.ssmui;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import nl.uu.cs.ssm.ColoredText;
import nl.uu.cs.ssm.MachineState;
import nl.uu.cs.ssm.Memory;
import nl.uu.cs.ssm.MemoryAnnotation;
import nl.uu.cs.ssm.MemoryCellEvent;
import nl.uu.cs.ssm.MemoryCellListener;
import nl.uu.cs.ssm.Utils;

public class HeapTableModel extends AbstractTableModel implements MemoryCellListener {
	
	private static final long serialVersionUID = 1L;

    private static final int C_ADDRESS = 0;
    private static final int C_VALUE = 1;
    protected static final int C_ANNOTE = 2;
    
    private static final String[] columnNames = {"Address", "Value", "Structure"};
    
    private MachineState machineState ;
    private Memory memory ;
        
    public HeapTableModel(MachineState machineState) {
    	
    	this.machineState = machineState;
    	reset();
    }
    
    public void reset() {
    	
    	if (memory != null) {
    		memory.removeMemoryCellListener(this);
    	}
    	    	
    	memory = machineState.getMemory();
    	memory.addMemoryCellListener(this);
    	
    	fireTableChanged(new TableModelEvent(this));
    }

	public int getColumnCount() {
		
		return columnNames.length;
	}

	public int getRowCount() {
		
		return machineState.getRegisters().getHP() - machineState.getStartAddressOfHeap();
	}
	
	public boolean isCellEditable(int row, int column) {
		
        return column == C_VALUE ;
    }
	
	private int rowToMemLoc(int row) {
        
		return row + machineState.getStartAddressOfHeap();
    }

	public Object getValueAt(int row, int column) {
		
		Object res = "" ;
        int memLoc = rowToMemLoc(row);
        switch(column) {
        
            case C_ADDRESS :
                res = Utils.asHex(memLoc);
                break;
                
            case C_VALUE :
                res = Utils.asHex(memory.getAt(memLoc)) ;
                break;
            case C_ANNOTE :
            	MemoryAnnotation ann = memory.getAnnotationAt(memLoc);
                res = ann == null ? ColoredText.blankDefault : ann;
                break;
        }
        
        return res;
	}
	
	public void setValueAt(Object aValue, int row, int column) {
		
		if (column == C_VALUE) {

            String strValue = null;

            if (aValue instanceof String)
                strValue = (String)aValue;
            else
                strValue = aValue.toString();

            memory.setAt(rowToMemLoc(row), strValue);
        }
	}

	public String getColumnName(int column) {
        
		return columnNames[column] ;
    }

    public Class<?> getColumnClass(int column) {
    	
    	if (column == C_ANNOTE)
	    	return ColoredText.class;
    	else
    		return SSMRunner.tableModelColumnClass;
    }
    
	public void cellChanged(MemoryCellEvent e) {
		
		fireTableDataChanged();
	}
}
