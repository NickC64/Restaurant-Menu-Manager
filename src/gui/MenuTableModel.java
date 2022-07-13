package gui;

import java.util.function.Supplier;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import restaurantMenuManager.Menu;
import restaurantMenuManager.MenuItem;
import util.ImageUtil;

/*
 * MenuTableModel manages how the menu table displays menu data
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

class MenuTableModel extends AbstractTableModel implements MenuModelListener {

	Supplier<Menu> ms;

	public MenuTableModel(Supplier<Menu> ms) {

		this.ms = ms;
	}

	// Prevents table from being edited directly

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		return false;
	}

	@Override
	public String getColumnName(int column) {

		return MenuField.values()[column].toString();
	}

	// Retrieves menu values to display on the table

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		MenuItem item = ms.get().get(rowIndex);
		Object value = MenuField.getFieldFromItem(MenuField.values()[columnIndex], item);

		if (value instanceof ImageIcon) {

			value = ImageUtil.squareify((ImageIcon) value, Table.ROW_HEIGHT);
		}

		return value;
	}

	// Retrieves the class of each column (MenuItem field)

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		return MenuField.values()[columnIndex].clz;
	}

	@Override
	public int getRowCount() {

		return ms.get().size();
	}

	@Override
	public int getColumnCount() {

		return 4;
	}

	// Updates what the menu table displays whenever there is a change to the menu data

	@Override
	public void rowAdded(int index) {

		fireTableRowsInserted(index, index);
	}

	@Override
	public void rowRemoved(int index) {

		fireTableRowsDeleted(index, index);
	}

	@Override
	public void fieldUpdated(int index, MenuField field) {

		if (field.ordinal() < getColumnCount()) {

			fireTableCellUpdated(index, field.ordinal());
		}
	}

	@Override
	public void menuDataChanged() {

		fireTableDataChanged();
	}

}