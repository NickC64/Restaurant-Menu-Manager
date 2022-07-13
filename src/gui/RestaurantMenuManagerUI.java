package gui;

/*
 * RestaurantMenuManagerUI allows for RestaurantMenuManager to send updates to GUIFrame
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public interface RestaurantMenuManagerUI extends MenuUpdater, OrderUpdater {

	public void displayInfoMessage(String str);
	public void displayErrorMessage(String str);
}