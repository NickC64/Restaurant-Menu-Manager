package gui;

import restaurantMenuManager.Menu;

/*
 * MenuUpdateListener allows for the GUIFrame other classes above MenuTab to listen for updates from it
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public interface MenuUpdateListener {

	public void onChange(Menu menu);
	public void onSave(Menu menu);
}