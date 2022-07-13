package gui;

import restaurantMenuManager.Menu;

/*
 * MenuUpdater allows for the upper classes to set the menu data of the MenuTab
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public interface MenuUpdater {

	public void set(Menu menu);
	public void setUpdateListener(MenuUpdateListener listener);
}
