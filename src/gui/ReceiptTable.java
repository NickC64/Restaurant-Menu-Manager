package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.Timestamp;

import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import util.PriceUtil;

/*
 * ReceiptTable creates a summary of the order on checkout
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public class ReceiptTable extends Table {

	private OrderSubscribable om;
	private DefaultTableModel dm;

	public ReceiptTable(OrderSubscribable om) {

		this.om = om;

		dm = new DefaultTableModel();

		setModel(dm);
		setRowHeight(50);

		String[] columnNames = {"Item", "Quantity", "Price"};
		dm.setColumnCount(3);
		dm.setColumnIdentifiers(columnNames);

		int i = 0;
		while (i < om.size()) {

			Object[] rowData = new Object[3];
			rowData[0] = om.getName(i);
			rowData[1] = om.getQuantity(i);

			// Displays an arithmetic operation multiplying price by quantity
			rowData[2] = om.getQuantity(i) + " x " + PriceUtil.moneyFormat(om.getPrice(i)) + " = " + PriceUtil.moneyFormat(om.getItemTotalPrice(i));

			dm.addRow(rowData);
			i++;
		}

		for (int j = 0; j < 4; j++) {

			dm.addRow(new Object[3]);
		}

		dm.setValueAt("Subtotal", i, 0);
		dm.setValueAt(PriceUtil.moneyFormat(om.getTotalPrice()), i, 2);
		dm.setValueAt("HST", i + 1, 0);
		dm.setValueAt(PriceUtil.moneyFormat(om.getTotalPrice() * 0.13), i + 1, 2);
		dm.setValueAt("Total", i + 2, 0);
		dm.setValueAt(PriceUtil.roundAndFormat(om.getTotalPrice() * 1.13), i + 2, 2);
		Long currentDate = System.currentTimeMillis();
		Timestamp currentTimestamp = new Timestamp(currentDate);

		// Remove milliseconds from timestamp
		String s = currentTimestamp.toString().split("\\.")[0];
		dm.setValueAt("Timestamp", i + 3, 0);
		dm.setValueAt(s, i + 3, 2);
	}

	@Override
	public boolean isCellEditable(int row, int column) {

		return false;
	}

	// Adjust font visuals

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {

		JComponent c = (JComponent) super.prepareRenderer(renderer, row, column);

		if (row < om.size()) {

			c.setForeground(Color.darkGray);

		} else if (row < dm.getRowCount() - 1) {

			Font f = c.getFont();
			c.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
		} else {

			c.setForeground(Color.darkGray);

			Font f = c.getFont();
			c.setFont(f.deriveFont(f.getStyle() | Font.ITALIC));
		}

		return c;
	}

}

