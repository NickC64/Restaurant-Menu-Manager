package gui;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import restaurantMenuManager.Menu;
import restaurantMenuManager.MenuItem;

// A convenient way for MenuTableModel to implement its abstract methods

enum MenuField {

	NAME(String.class),
	PRICE(Double.class),
	IMAGE(ImageIcon.class),
	AVAILABLE(Boolean.class),
	NOTES(String.class);

	Class<?> clz;

	MenuField(Class<?> clz) {
		this.clz = clz;
	}

	static Object getFieldFromItem(MenuField field, MenuItem item) {

		switch (field) {

		case NAME:

			return item.getName();
		case PRICE:

			return item.getPrice();
		case IMAGE:

			return item.getImage(true);
		case AVAILABLE:

			return item.isAvailable();
		case NOTES:

			return item.getNotes();
		default:

			throw new IllegalArgumentException("Invalid menu field: " + field);
		}
	}

	@Override
	public String toString() {

		String str = super.toString();
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}
}

// Classes that are notified by changes in menu data implement this interface

interface MenuModelListener {

	public void rowAdded(int index);

	public void rowRemoved(int index);

	public void menuDataChanged();

	public void fieldUpdated(int index, MenuField field);
}

/*
 * MenuSubscribable is a wrapper class for the menu, allowing for classes to be notified when menu data changes.
 * Classes that wish to notify others of changes to the menu will update the menu through this class.
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public class MenuSubscribable implements Cloneable {

	// Internal menu data

	private Menu menuData;

	public MenuSubscribable() {

		menuData = new Menu();
	}

	// List of listeners that will be notified when a change to the menu is made

	private ArrayList<MenuModelListener> listeners = new ArrayList<>();


	// Getter and setter for menuData

	public Menu getMenuData() {

		return menuData;
	}

	public void setMenuData(Menu menuData) {

		this.menuData = menuData;

		for (MenuModelListener listener : listeners) {

			listener.menuDataChanged();
		}
	}

	// Adds a listener to the listener list

	public void addMenuSubscriber(MenuModelListener listener) {

		listeners.add(listener);
	}

	// Adds a MenuItem

	public void addItem(int index, MenuItem item) {

		menuData.add(index, item);

		for (MenuModelListener listener : listeners) {

			listener.rowAdded(index);
		}
	}

	public void addItem(MenuItem item) {

		addItem(size(), item);
	}

	// Removes a MenuItem

	public void removeItem(int index) {

		menuData.remove(index);

		for (MenuModelListener listener : listeners) {

			listener.rowRemoved(index);
		}
	}

	public MenuItem get(int index) {

		return menuData.get(index);
	}

	public Object getField(MenuField field, int index) {

		MenuItem item = menuData.get(index);
		return MenuField.getFieldFromItem(field, item);
	}

	public String getName(int index) {

		return menuData.get(index).getName();
	}

	public double getPrice(int index) {

		return menuData.get(index).getPrice();
	}

	public ImageIcon getImage(int index, boolean saturated) {

		return menuData.get(index).getImage(saturated);
	}

	public ImageIcon getImage(int index, boolean saturated, int sideLength) {

		return menuData.get(index).getImage(saturated, sideLength);
	}


	public boolean isAvailable(int index) {

		return menuData.get(index).isAvailable();
	}

	public String getNotes(int index) {

		return menuData.get(index).getNotes();
	}

	public int size() {

		return menuData.size();
	}


	public void setName(int index, String name) {

		menuData.get(index).setName(name);

		for (MenuModelListener listener : listeners) {

			listener.fieldUpdated(index, MenuField.NAME);
		}
	}

	public void setPrice(int index, double price) {

		menuData.get(index).setPrice(price);

		for (MenuModelListener listener : listeners) {

			listener.fieldUpdated(index, MenuField.PRICE);
		}
	}

	public void setImage(int index, ImageIcon image) {

		menuData.get(index).setImage(image);

		for (MenuModelListener listener : listeners) {

			listener.fieldUpdated(index, MenuField.IMAGE);
		}
	}

	public void setAvailable(int index, boolean available) {

		menuData.get(index).setAvailable(available);

		for (MenuModelListener listener : listeners) {

			listener.fieldUpdated(index, MenuField.AVAILABLE);
		}
	}

	public void setNotes(int index, String notes) {

		menuData.get(index).setNotes(notes);

		for (MenuModelListener listener : listeners) {

			listener.fieldUpdated(index, MenuField.NOTES);
		}
	}


}
