package gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import util.PriceUtil;

/*
 * Table customizes the JTable for repeated use for all tables in the program
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public class Table extends JTable {

	JScrollPane scrollPane;
	static final int ROW_HEIGHT = 60;
	boolean textOverlaid = false;

	// Creates an alternating blue and white row pattern

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {

		Component c = super.prepareRenderer(renderer, row, column);
		c.setForeground(Color.black);

		if (isRowSelected(row)) {

			return c;
		} else {
			if (row % 2 == 0) {

				c.setBackground(new Color(235, 245, 255));
			} else {

				c.setBackground(Color.white);
			}

			return c;
		}
	}

	public Table(String title) {

		setRowHeight(ROW_HEIGHT);
		setDragEnabled(false);
		setDragEnabled(false);
		scrollPane = new JScrollPane(this);
		scrollPane.setBorder(BorderFactory.createTitledBorder(title));
		setFillsViewportHeight(true);
		tableHeader.setReorderingAllowed(false);
		tableHeader.setEnabled(false);
		tableHeader.setResizingAllowed(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public Table() {

		this("");
	}

}

class PriceCellRenderer extends DefaultTableCellRenderer {

	// Custom cell renderer for price values

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		Double d = (Double) value;
		String fd = PriceUtil.moneyFormat(d);
		JLabel c = (JLabel) super.getTableCellRendererComponent(table, fd, isSelected, hasFocus, row, column);

		c.setHorizontalAlignment(SwingConstants.RIGHT);
		return c;

	}
}