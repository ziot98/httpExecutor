package app.ui.component;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DeleteAction extends AbstractAction {
	
    public void actionPerformed(ActionEvent e)
    {
        JTable table = (JTable)e.getSource();
        int rowCount = table.getRowCount();
        if (rowCount != 1) {
        	int modelRow = Integer.valueOf( e.getActionCommand() );
        	((DefaultTableModel)table.getModel()).removeRow(modelRow);
        } else {
        	table.setValueAt(null, 0, 0);
        	table.setValueAt(null, 0, 1);
        }
    }
}
