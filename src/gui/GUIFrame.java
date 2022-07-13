package gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import restaurantMenuManager.Menu;
import restaurantMenuManager.Order;

/*
 * GUIFrame initiates the GUI and manages the behaviour of the JTabbedPane
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public class GUIFrame extends JFrame implements RestaurantMenuManagerUI {

	// JComponents displayed on JFrame

	private OrderTab orderTab;
	private MenuTab menuTab;
	private JTabbedPane tabbedPane;
	private MenuUpdateListener menuListener;
	private Menu lastSavedMenu;
	private Menu unsavedMenu;
	private Order order;
	private ToolBar toolbar;

	/* MenuUpdater methods */

	@Override
	public void set(Menu menu) {

		lastSavedMenu = menu;
		menuTab.set(menu);
		orderTab.set(menu);
	}

	@Override
	public void setUpdateListener(MenuUpdateListener listener) {

		menuListener = listener;
	}

	/* OrderUpdater methods */

	@Override
	public void set(Order order) {}

	@Override
	public void setUpdateListener(OrderUpdateListener listener) {}

	/* Rest of RestaurantMenuManagerUI methods */

	@Override
	public void displayInfoMessage(String str) {

		JOptionPane.showMessageDialog(this, str, null, JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void displayErrorMessage(String str) {

		JOptionPane.showMessageDialog(this, str, null, JOptionPane.ERROR_MESSAGE);
	}

	public GUIFrame() {

		initUI();
	}

	// Initiates top level components: menu tab, order tab and toolbar

	private void initUI() {

		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());		// Makes program look and feel consistent across all devices
		} catch (Exception e) {

			e.printStackTrace();
			displayErrorMessage("Unable to load look and feel");
		}

		toolbar = new ToolBar();
		toolbar.setUpdateListener(new ToolBarUpdateListener() {

			@Override
			public void onNewOrder() {

				orderTab.set(new Order());
			}
		});

		setJMenuBar(toolbar);
		orderTab = new OrderTab();
		orderTab.setUpdateListener(new OrderUpdateListener() {

			@Override
			public void onChange(Order order) {

				GUIFrame.this.order = order;
			}
		});
		orderTab.set(new Order());

		menuTab = new MenuTab();
		menuTab.setUpdateListener(new MenuUpdateListener() {
			@Override
			public void onChange(Menu menu) {

				unsavedMenu = menu;
			}

			@Override
			public void onSave(Menu menu) {
				lastSavedMenu = menu;
				menuListener.onSave(menu);
				orderTab.set(menu);
			}
		});

		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(new TabbedPaneListener());

		tabbedPane.addTab("Order", null, orderTab, "Add Items to Order");
		tabbedPane.addTab("Edit Menu", null, menuTab, "Customize Restaurant Menu");
		add(tabbedPane);

		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(800, 500));
		setTitle("Restaurant Menu Manager");
		setVisible(true);

	}

	/* Listener for tabbed pane */

	class TabbedPaneListener implements ChangeListener {

		// Notifies the user if they click on the menu tab with an order in progress, or if they click on the order tab with unsaved changes

		@Override
		public void stateChanged(ChangeEvent e) {

			if (lastSavedMenu == null || unsavedMenu == null) {

				return;
			}

			int selectedIndex = tabbedPane.getSelectedIndex();

			if (selectedIndex == 1) {

				if (order.size() != 0) {

					tabbedPane.setSelectedIndex(0);
					int result = JOptionPane.showConfirmDialog(GUIFrame.this, "This will clear the current order, continue?", null, JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {

						orderTab.set(new Order());
						orderTab.clearSelections();
						tabbedPane.setSelectedIndex(1);
					}
				} else {

					orderTab.clearSelections();
				}

				orderTab.clearSelections();
			} else if (selectedIndex == 0) {

				if (!lastSavedMenu.equals(unsavedMenu)) {

					tabbedPane.setSelectedIndex(1);
					int result = JOptionPane.showConfirmDialog(GUIFrame.this, "Discard all changes?", null, JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {

						menuTab.set(lastSavedMenu);
						tabbedPane.setSelectedIndex(0);
					}
				}
			}

		}

	}

}
