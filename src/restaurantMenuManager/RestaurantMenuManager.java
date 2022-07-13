package restaurantMenuManager;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.SwingUtilities;

import gui.GUIFrame;
import gui.MenuUpdateListener;
import gui.RestaurantMenuManagerUI;

/*
 * RestaurantMenuManager is a program that allows for users to create a menu and create orders from that menu
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public class RestaurantMenuManager implements MenuUpdateListener {

	private RestaurantMenuManagerUI ui;

	// Location of save file for menu

	public final static String FILE_SAVE_PATH = "saved_menu.txt";

	@Override
	public void onChange(Menu menu) {}

	@Override
	public void onSave(Menu menu) {

		saveToDisk(menu);
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

		    @Override
			public void run() {

		    	RestaurantMenuManager rmm = new RestaurantMenuManager();
				rmm.populateGUI();
		    }
		});

	}

	private void populateGUI() {

		ui.set(retrieveMenu());
	}

	public RestaurantMenuManager() {

		ui = new GUIFrame();
		ui.setUpdateListener(this);
	}

	// Retrieves saved menu

	private Menu retrieveMenu() {

		try {

			ObjectInputStream is = new ObjectInputStream(new FileInputStream(FILE_SAVE_PATH));
			Menu menu = (restaurantMenuManager.Menu) is.readObject();
			is.close();
			return menu;
		} catch (Exception e) {

			Menu menu = new Menu();
			return menu;
		}

	}

	// Saves menu to file

	private void saveToDisk(Menu menu) {

		try {

			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(RestaurantMenuManager.FILE_SAVE_PATH));
			os.writeObject(menu);
			ui.displayInfoMessage("Changes saved successfully");
			os.close();
		} catch (Exception e1) {

			ui.displayInfoMessage("Unable to save changes!");
		}
	}

}
