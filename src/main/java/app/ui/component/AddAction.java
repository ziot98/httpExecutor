package app.ui.component;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AddAction extends AbstractAction {
	
	public static final String[] INITIAL_ROW = {"", "", "ADD", "DELETE"}; // Initial row
	
    public void actionPerformed(ActionEvent e)
    {
        JTable table = (JTable)e.getSource();
        int modelRow = Integer.valueOf( e.getActionCommand() );
        ((DefaultTableModel)table.getModel()).addRow(INITIAL_ROW);
    }


}
