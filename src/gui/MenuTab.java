package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import restaurantMenuManager.Menu;
import restaurantMenuManager.MenuItem;

/*
 * MenuTab allows the user to edit the menu that orders are created from
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public class MenuTab extends JPanel implements Supplier<Menu>, MenuUpdater {

	// JComponents Displayed on MenuTab
	private JComboBox<SortFilterOptions> sortComboBox;
	private JTextField searchField;
	private JButton saveButton;
	private Table menuTable;
	private MenuTableModel menuTableModel;
	private TableRowSorter<MenuTableModel> menuTableSorter;
	private JScrollPane editPanelScrollPane;
	private EditPanel editPanel;
	private JButton addButton;
	private JButton deleteButton;

	// Keeps track of the selected item that is being edited by EditPanel
	int selectedMenuItemIndex;

	// Keeps track of the saved menu before the user made changes
	private Menu savedMenu;

	private MenuSubscribable menuSubscribable;

	// Allows MenuTab to provide GUIFrame with updates
	private MenuUpdateListener menuListener;

	/* Supplier */

	@Override
	public Menu get() {

		return menuSubscribable.getMenuData();
	}

	/* MenuUpdater */

	@Override
	public void set(Menu menu) {

		savedMenu = menu.cloneMenu();
		menuSubscribable.setMenuData(menu.cloneMenu());
	}

	@Override
	public void setUpdateListener(MenuUpdateListener listener) {

		this.menuListener = listener;
	}
	/* TabbedPaneChangeListener */

	public MenuTab() {

		menuSubscribable = new MenuSubscribable();

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

		menuTable = new Table("Customize Menu") {

			// Calls custom price renderer on the price column to display dollar sign
			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {

				if (column == 1) {

					return new PriceCellRenderer();
				}

				return super.getCellRenderer(row, column);
			}

		};

		menuTable.getSelectionModel().addListSelectionListener(new MenuTableListener());

		menuTableModel = new MenuTableModel(this) {

			// Allows users to check or uncheck the isAvaialble checkbox while the other columns remain uneditable directly
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {

				if (columnIndex == MenuField.AVAILABLE.ordinal()) {

					return true;
				}

				return false;
			}

			// Allows changes to the isAvailable checkbox to reflect in the menuSubscribable
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

				if (columnIndex == MenuField.AVAILABLE.ordinal()) {

					menuSubscribable.setAvailable(rowIndex, (Boolean) aValue);
				} else {

					throw new IllegalArgumentException("Unexpected value set at column index " + columnIndex);
				}
			}
		};
		menuTable.setModel(menuTableModel);
		menuTableModel.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {

				menuListener.onChange(menuSubscribable.getMenuData().cloneMenu());
			}
		});

		menuSubscribable.addMenuSubscriber(new SaveUpdater());
		menuSubscribable.addMenuSubscriber(menuTableModel);

		menuTableSorter = new TableRowSorter<>(menuTableModel);
		menuTable.setRowSorter(menuTableSorter);

		editPanel = new EditPanel();

		// Listens to value updates from the edit panel that are then passed to the menuSubscribable

		editPanel.setEditPanelUpdateListener(new EditPanelUpdateListener() {

			@Override
			public void onNameChange(String name) {

				menuSubscribable.setName(selectedMenuItemIndex, name);
			}

			@Override
			public void onPriceChange(Double price) {

				menuSubscribable.setPrice(selectedMenuItemIndex, price);
			}

			@Override
			public void onImageChange(ImageIcon image) {

				menuSubscribable.setImage(selectedMenuItemIndex, image);
			}

			@Override
			public void onNotesChange(String notes) {

				menuSubscribable.setNotes(selectedMenuItemIndex, notes);
			}

			@Override
			public boolean nameExists(String name) {

				for (int i = 0; i < menuSubscribable.size(); i++) {


					if (i == selectedMenuItemIndex) {

						continue;
					}

					if (menuSubscribable.getName(i).equalsIgnoreCase(name)) {

						return true;
					}
				}
				return false;
			}

		});

		editPanelScrollPane = new JScrollPane(editPanel);
		editPanelScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		editPanelScrollPane.getVerticalScrollBar().setUnitIncrement(6);
		editPanelScrollPane.setMinimumSize(editPanel.getPreferredSize());

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());

		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.BOTH;
		c1.weighty = 1;

		c1.weightx = 1;
		c1.gridx = 0;
		c1.gridy = 0;
		centerPanel.add(menuTable.scrollPane, c1);

		c1.weightx = 0;
		c1.gridx = 1;
		c1.gridy = 0;
		centerPanel.add(editPanelScrollPane, c1);

		// Bottom Panel

		addButton = new JButton("Add Item");
		addButton.addActionListener(new AddListener());

		deleteButton = new JButton("Delete Item");
		deleteButton.addActionListener(new DeleteListener());

		saveButton = new JButton("Save Changes");
		saveButton.setEnabled(false);

		saveButton.addActionListener(new SaveListener());

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));
		bottomPanel.add(addButton);
		bottomPanel.add(deleteButton);
		bottomPanel.add(saveButton);

		setLayout(new BorderLayout());
		add(searchPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);

		deleteButton.setEnabled(false);
	}

	/* Listener for search by combobox */

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

	/* Listener for selection events of menu table */

	class MenuTableListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {

			if (!e.getValueIsAdjusting() && menuTable.isFocusOwner()) {
				int numRowsSelected = menuTable.getSelectedRowCount();

				switch (numRowsSelected) {
				case 0:

					deleteButton.setEnabled(false);
					editPanel.clearMenuItem();
					editPanel.setComponentsEnabled(false);
					break;
				case 1:

					// Populates the edit panel with the selected menu item
					deleteButton.setEnabled(true);
					editPanel.setComponentsEnabled(true);
					selectedMenuItemIndex = menuTable.convertRowIndexToModel(menuTable.getSelectedRow());
					MenuItem selectedMenuItem = menuSubscribable.get(selectedMenuItemIndex);
					editPanel.displayMenuItem(selectedMenuItem);
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + numRowsSelected);
				}
			}
		}

	}

	/* Listener for add button of menu table */

	class AddListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			String nameInput = JOptionPane.showInputDialog(MenuTab.this, "Please enter a name:");
			if (nameInput != null) {

				String nameToAdd = nameInput.trim();
				if (!nameToAdd.equals("")) {

					if (!nameExists(nameToAdd)) {

						MenuItem newItem = new MenuItem();
						newItem.setName(nameToAdd);
						menuSubscribable.addItem(newItem);

						// Scrolls down to the end of the menu table and selects the item that was added
						menuTable.setRowSelectionInterval(menuTable.getRowCount() - 1, menuTable.getRowCount() - 1);
						menuTable.scrollRectToVisible(menuTable.getCellRect(menuTable.getRowCount() - 1, 0, true));
					} else {

						JOptionPane.showMessageDialog(MenuTab.this, "Name already exists");
					}

				} else {

					JOptionPane.showMessageDialog(MenuTab.this, "Name cannot be blank");
				}

			}


		}

		private boolean nameExists(String name) {

			for (int i = 0; i < menuSubscribable.size(); i++) {

				if (menuSubscribable.getName(i).equalsIgnoreCase(name)) {

					return true;
				}
			}
			return false;
		}

	}

	/* Listener for delete button of menu table */

	class DeleteListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			int tableSelectedRow = menuTable.getSelectedRow();
			int realSelectedRow = menuTable.convertRowIndexToModel(tableSelectedRow);
			menuSubscribable.removeItem(realSelectedRow);

			// Sets the selection to the item above the deleted item
			if (tableSelectedRow == 0) {

				if (menuTable.getRowCount() != 0) {

					menuTable.setRowSelectionInterval(0, 0);
				}
			} else {

				menuTable.setRowSelectionInterval(tableSelectedRow - 1, tableSelectedRow - 1);
			}

		}

	}

	/* Listener for save button of menu table */

	class SaveListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			Menu menuToSave = menuSubscribable.getMenuData();
			menuListener.onSave(menuToSave.cloneMenu());
			savedMenu = menuToSave.cloneMenu();
			saveButton.setEnabled(false);
		}

	}

	class SaveUpdater implements MenuModelListener {

		@Override
		public void rowAdded(int index) {

			onChange();
		}

		@Override
		public void rowRemoved(int index) {

			onChange();
		}

		@Override
		public void fieldUpdated(int index, MenuField field) {

			onChange();
		}

		@Override
		public void menuDataChanged() {

			onChange();
		}

		private void onChange() {

			if (savedMenu.equals(menuSubscribable.getMenuData())) {

				saveButton.setEnabled(false);
			} else {

				saveButton.setEnabled(true);
			}

		}
	}

}
