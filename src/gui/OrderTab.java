package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.util.function.Supplier;
import java.util.regex.PatternSyntaxException;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import restaurantMenuManager.Menu;
import restaurantMenuManager.MenuItem;
import restaurantMenuManager.Order;
import util.ImageUtil;
import util.PriceUtil;

/*
 * OrderTab allows the user to create orders from the menu
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public class OrderTab extends JPanel implements Supplier<Menu>, OrderUpdater, MenuUpdater {

	// JComponents displayed on OrderTab

	private JComboBox<SortFilterOptions> sortComboBox;
	private JTextField searchField;
	private Table menuTable;
	private MenuTableModel menuTableModel;
	private TableRowSorter<MenuTableModel> menuTableSorter;
	private Table orderTable;
	private OrderTableModel orderTableModel;
	private JButton addButton;
	private JButton deleteButton;
	private JButton checkoutButton;
	private JTextField totalTextField;

	private MenuSubscribable menuSubscribable;
	private OrderSubscribable orderSubcribable;

	// Allows for updates to be sent to GUIFrame

	private OrderUpdateListener listener;


	@Override
	public Menu get() {
		return menuSubscribable.getMenuData();
	}

	@Override
	public void set(Menu menu) {

		this.menuSubscribable.setMenuData(menu);
	}

	@Override
	public void setUpdateListener(MenuUpdateListener listener) {}		// OrderTab does not change the menu so this method is not used by OrderTab

	@Override
	public void set(Order order) {

		orderSubcribable.newOrder();
	}

	@Override
	public void setUpdateListener(OrderUpdateListener listener) {

		this.listener = listener;
	}

	public OrderTab() {

		menuSubscribable = new MenuSubscribable();
		orderSubcribable = new OrderSubscribable();

		initComponents();
	}

	private void initComponents() {

		// Search Panel

		JLabel searchByLabel = new JLabel("Sort by:");

		sortComboBox = new JComboBox<>(SortFilterOptions.values());
		sortComboBox.addActionListener(new SearchByListener());

		JLabel searchLabel = new JLabel("Search:");

		searchField = new JTextField(20);
		searchField.getDocument().addDocumentListener(new SearchListener());

		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));

		searchPanel.add(searchByLabel);
		searchPanel.add(sortComboBox);
		searchPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		searchPanel.add(searchLabel);
		searchPanel.add(searchField);

		// Center Panel
		addButton = new JButton(">");
		addButton.addActionListener(new AddButtonListener());

		deleteButton = new JButton("<");
		deleteButton.addActionListener(new DeleteButtonListener());

		menuTable = new Table("Menu") {

			// Greys out text in rows that have unavailable menu items

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {

				Component c = super.prepareRenderer(renderer, row, column);
				if (!menuSubscribable.isAvailable(menuTable.convertRowIndexToModel(row))) {

					c.setForeground(Color.gray);
				}

				return c;
			}


			// Uses custom cell renderer for price column

			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {

				if (column == MenuField.PRICE.ordinal()) {

					return new PriceCellRenderer();
				}

				return super.getCellRenderer(row, column);
			}
		};

		menuTable.getSelectionModel().addListSelectionListener(new MenuSelectionListener());

		menuTableModel = new MenuTableModel(this) {

			// Instead of a checkbox, displays "yes or no" for availability column

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {

				Object value = super.getValueAt(rowIndex, columnIndex);

				if (columnIndex == MenuField.AVAILABLE.ordinal()) {

					if (ms.get().get(rowIndex).isAvailable()) {

						return "yes";
					}

					return "no";
				} else if (value instanceof ImageIcon) {
					
					if (!ms.get().get(rowIndex).isAvailable()) {
						
						value = ImageUtil.desaturate((ImageIcon) value);
					}
				}

				return value;
			}

			// Ensures "yes or no" is interpreted as a String instead of a boolean

			@Override
			public Class<?> getColumnClass(int columnIndex) {

				Class<?> clz = super.getColumnClass(columnIndex);

				if (columnIndex == MenuField.AVAILABLE.ordinal()) {

					return String.class;
				}

				return clz;
			}
		};
		menuSubscribable.addMenuSubscriber(menuTableModel);

		menuTable.setModel(menuTableModel);

		menuTableSorter = new TableRowSorter<>(menuTableModel);
		menuTable.setRowSorter(menuTableSorter);

		orderTable = new Table("Order") {

			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {

				if (column == 1) {

					return new PriceCellRenderer();
				}

				return super.getCellRenderer(row, column);
			}

		};

		orderTable.getSelectionModel().addListSelectionListener(new OrderSelectionListener());

		orderTableModel = new OrderTableModel();
		orderTable.setModel(orderTableModel);
		orderTableModel.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {

				listener.onChange(orderSubcribable.getOrderData());
			}
		});

		orderSubcribable.addOrderListener(orderTableModel);
		orderSubcribable.addOrderListener(new TotalFieldUpdater());
		orderSubcribable.addOrderListener(new ButtonUpdater());
		orderSubcribable.addOrderListener(new OrderTableTextUpdater());

		JPanel tablePanel = new JPanel();
		tablePanel.setLayout((new GridBagLayout()));

		GridBagConstraints c1 = new GridBagConstraints();
		c1.weighty = 1;

		c1.fill = GridBagConstraints.BOTH;
		c1.gridheight = 2;
		c1.ipadx = 40;
		c1.weightx = 0.5;
		c1.gridx = 0;
		c1.gridy = 0;
		tablePanel.add(menuTable.scrollPane, c1);

		c1.fill = GridBagConstraints.VERTICAL;
		c1.gridheight = 1;
		c1.weightx = 0;
		c1.ipadx = 0;
		c1.gridx = 1;
		c1.gridy = 0;
		tablePanel.add(addButton, c1);

		c1.fill = GridBagConstraints.VERTICAL;
		c1.gridheight = 1;
		c1.weightx = 0;
		c1.ipadx = 0;
		c1.gridx = 1;
		c1.gridy = 1;
		tablePanel.add(deleteButton, c1);

		c1.fill = GridBagConstraints.BOTH;
		c1.gridheight = 2;
		c1.weightx = 0.5;
		c1.ipadx = 0;
		c1.gridx = 2;
		c1.gridy = 0;
		tablePanel.add(orderTable.scrollPane, c1);

		// Bottom Panel

		checkoutButton = new JButton("Checkout");
		checkoutButton.setPreferredSize(new Dimension(150, 30));
		checkoutButton.addActionListener(new CheckoutListener());

		JLabel totalLabel = new JLabel("Subtotal: ");

		totalTextField = new JTextField(10);
		totalTextField.setEditable(false);
		totalTextField.setText("$0.00");


		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 5));
		bottomPanel.add(checkoutButton);
		bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		bottomPanel.add(totalLabel);
		bottomPanel.add(totalTextField);

		// Overall Layout

		setLayout(new BorderLayout());
		add(searchPanel, BorderLayout.NORTH);
		add(tablePanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);

		addButton.setEnabled(false);
		deleteButton.setEnabled(false);
		checkoutButton.setEnabled(false);
	}

	public void clearSelections() {

		searchField.setText("");
		sortComboBox.setSelectedIndex(SortFilterOptions.NONE.ordinal());
		menuTable.clearSelection();
		orderTable.clearSelection();
	}

	// Manages layout of the order table

	class OrderTableModel extends AbstractTableModel implements OrderModelListener {

		public static final int NAME = 0;
		public static final int PRICE = 1;
		public static final int QUANTITY = 2;

		@Override
		public Class<?> getColumnClass(int columnIndex) {

			switch (columnIndex) {
			case 0:
				return String.class;
			case 1:
				return Double.class;
			case 2:
				return Integer.class;
			default:
				throw new IllegalArgumentException("Unexpected value: " + columnIndex);
			}
		}

		@Override
		public String getColumnName(int column) {

			switch (column) {
			case 0:
				return "Name";
			case 1:
				return "Price";
			case 2:
				return "Quantity";
			default:
				throw new IllegalArgumentException("Unexpected value: " + column);
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {

			switch (columnIndex) {
			case 0:
				return orderSubcribable.getName(rowIndex);
			case 1:
				return orderSubcribable.getPrice(rowIndex);
			case 2:
				return orderSubcribable.getQuantity(rowIndex);
			default:
				throw new IllegalArgumentException("Unexpected value: " + columnIndex);
			}
		}

		@Override
		public int getRowCount() {

			return orderSubcribable.size();
		}

		@Override
		public int getColumnCount() {

			return 3;
		}

		@Override
		public void rowAdded(int index) {

			orderTableModel.fireTableRowsInserted(index, index);
		}

		@Override
		public void rowRemoved(int index) {

			orderTableModel.fireTableRowsDeleted(index, index);
		}

		@Override
		public void orderCleared() {

			orderTableModel.fireTableDataChanged();
		}

		@Override
		public void quantityUpdated(int index) {

			orderTableModel.fireTableCellUpdated(index, OrderTableModel.QUANTITY);
		}
	}



	class SearchByListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			SortFilterOptions sortFilter = (SortFilterOptions) sortComboBox.getSelectedItem();
			menuTableSorter.setSortKeys(SortFilterOptions.getSortKey(sortFilter));

		}

	}

	/* Listener for search bar */

	class SearchListener implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {

			onChange();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {

			onChange();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {

			onChange();
		}

		private void onChange() {

			RowFilter<MenuTableModel, Object> rf = null;

			try {
		        rf = RowFilter.regexFilter("(?i)" + searchField.getText(), 0);
		    } catch (PatternSyntaxException e) {

		        return;
		    }
		    menuTableSorter.setRowFilter(rf);
		}
	}

	/* Listener for add button */

	class AddButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			MenuItem itemToAdd;

			// User can click on an item on either the menu or order table to add more, this statement accounts for that

			if (menuTable.getSelectedRow() != -1) {

				itemToAdd = menuSubscribable.get(menuTable.convertRowIndexToModel(menuTable.getSelectedRow()));

			} else if (orderTable.getSelectedRow() != -1) {

				itemToAdd = orderSubcribable.get(orderTable.convertRowIndexToModel(orderTable.getSelectedRow()));
			} else {

				return;
			}
			orderSubcribable.addItem(itemToAdd);
			deleteButton.setEnabled(true);
		}

	}

	/* Listener for delete button */

	class DeleteButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			MenuItem itemToDelete;
			if (menuTable.getSelectedRow() != -1) {

				itemToDelete = menuSubscribable.get(menuTable.convertRowIndexToModel(menuTable.getSelectedRow()));

			} else if (orderTable.getSelectedRow() != -1) {

				itemToDelete = orderSubcribable.get(orderTable.convertRowIndexToModel(orderTable.getSelectedRow()));
			} else {

				return;
			}

			orderSubcribable.deleteItem(itemToDelete);
		}

	}

	/*
	 * Listener for selection events on menu table
	 * Enables or disables add and delete buttons depending on selection
	 */

	class MenuSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {

			if (!e.getValueIsAdjusting()) {

				int numRowsSelected = menuTable.getSelectedRowCount();

				switch (numRowsSelected) {
				case 0:

					if (orderTable.getSelectedRow() == -1) {

						addButton.setEnabled(false);
						deleteButton.setEnabled(false);
					}
					break;
				case 1:

					orderTable.clearSelection();

					int selectedItemIndex = menuTable.convertRowIndexToModel(menuTable.getSelectedRow());

					if (menuSubscribable.isAvailable(selectedItemIndex)) {

						addButton.setEnabled(true);
						deleteButton.setEnabled(true);

						if (orderSubcribable.searchForItem(menuSubscribable.get(selectedItemIndex)) == -1) {

							deleteButton.setEnabled(false);
						}

					} else {

						addButton.setEnabled(false);
						deleteButton.setEnabled(false);
					}

					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + numRowsSelected);
				}
			}
		}

	}

	/*
	 * Listener for selection events on order table
	 * Enables or disables add and delete buttons depending on selection
	 */

	class OrderSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {

			int numRowsSelected = orderTable.getSelectedRowCount();

			if (!e.getValueIsAdjusting()) {

				switch (numRowsSelected) {
				case 0:

					if (menuTable.getSelectedRow() == -1) {

						addButton.setEnabled(false);
						deleteButton.setEnabled(false);
					}
					break;
				case 1:

					menuTable.clearSelection();

					addButton.setEnabled(true);
					deleteButton.setEnabled(true);

					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + numRowsSelected);
				}
			}
		}

	}

	/* Listener for checkout button */

	class CheckoutListener implements ActionListener {

		// Displays a printable order summary and starts a new order
		@Override
		public void actionPerformed(ActionEvent e) {

			Order orderToCheckout = orderSubcribable.getOrderData();

			ReceiptTable rt = new ReceiptTable(orderSubcribable);
			JScrollPane rsp = new JScrollPane(rt);

			String[] options = {"Print", "Close"};

			UIManager.put("OptionPane.minimumSize",new Dimension(500,100));
			int result = JOptionPane.showOptionDialog(
					OrderTab.this,
					rsp,
					"Order Summary",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE,
					null,
					options,
					options[1]);

			if (result == JOptionPane.YES_OPTION) {

				try {

					rt.print();

				} catch (PrinterException e1) {

					JOptionPane.showMessageDialog(OrderTab.this, "Unable to print summary");
					e1.printStackTrace();
				}
			}

			orderSubcribable.newOrder();
			clearSelections();
		}

	}

	/* Updates the total that is displayed at the bottom right corner */

	class TotalFieldUpdater implements OrderModelListener {

		@Override
		public void rowAdded(int index) {

			onChange();
		}

		@Override
		public void rowRemoved(int index) {

			onChange();
		}

		@Override
		public void quantityUpdated(int index) {

			onChange();
		}

		@Override
		public void orderCleared() {

			onChange();
		}

		private void onChange() {

	        Double d = orderSubcribable.getTotalPrice();
	        String fd = PriceUtil.moneyFormat(d);
			totalTextField.setText(fd);
		}

	}

	/* Updates buttons depending on whether or not order is empty */

	class ButtonUpdater implements OrderModelListener {

		@Override
		public void rowAdded(int index) {

			onChange();
		}

		@Override
		public void rowRemoved(int index) {

			onChange();
		}

		@Override
		public void quantityUpdated(int index) {

			onChange();
		}

		@Override
		public void orderCleared() {

			onChange();
		}

		private void onChange() {

			if (orderSubcribable.size() == 0) {

				checkoutButton.setEnabled(false);
			} else {

				checkoutButton.setEnabled(true);
			}

			if (menuTable.getSelectedRow() != -1) {

				MenuItem key = menuSubscribable.get(menuTable.convertRowIndexToModel(menuTable.getSelectedRow()));
				if (orderSubcribable.searchForItem(key) == -1) {

					deleteButton.setEnabled(false);
				}
			}
		}

	}

	class OrderTableTextUpdater implements OrderModelListener {

		@Override
		public void rowAdded(int index) {}

		@Override
		public void rowRemoved(int index) {

			onRemoval();
		}

		@Override
		public void orderCleared() {

			onRemoval();
		}

		@Override
		public void quantityUpdated(int index) {}

		private void onRemoval() {

			if (orderTable.getRowCount() == 0 && orderTable.getGraphics() != null) {


			}
		}
	}

	class MenuTableTextUpdater implements MenuModelListener {

		@Override
		public void rowAdded(int index) {
			// TODO Auto-generated method stub

		}

		@Override
		public void rowRemoved(int index) {
			// TODO Auto-generated method stub

		}

		@Override
		public void menuDataChanged() {
			// TODO Auto-generated method stub

		}

		@Override
		public void fieldUpdated(int index, MenuField field) {
			// TODO Auto-generated method stub

		}

	}

}
