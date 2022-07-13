package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/*
 * Toolbar allows for the user to start a new order and exit the program
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public class ToolBar extends JMenuBar implements ToolBarUpdater {

	private ToolBarUpdateListener toolBarListener;

	@Override
	public void setUpdateListener(ToolBarUpdateListener listener) {

		toolBarListener = listener;
	}

	public ToolBar() {

		initComponents();
	}

	private void initComponents() {

		JMenu file = new JMenu("File");

		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ExitListener());
		file.add(exit);

		JMenuItem viewOrderStatistics = new JMenuItem("New Order");
		viewOrderStatistics.addActionListener(new NewOrderListener());
		file.add(viewOrderStatistics);

		add(file);
	}

	class ExitListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			System.exit(0);
		}

	}

	class NewOrderListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			toolBarListener.onNewOrder();
		}
	}

}
