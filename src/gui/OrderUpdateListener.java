package gui;

import restaurantMenuManager.Order;

/*
 * OrderUpdateListener allows the GUIFrame to know when the order changes
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public interface OrderUpdateListener {

	public void onChange(Order order);
}
