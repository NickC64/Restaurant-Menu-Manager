package gui;

import restaurantMenuManager.Order;

/*
 * OrderUpdateListener allows the GUIFrame to set itself as a listener of the OrderTab
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public interface OrderUpdater {

	public void set(Order order);
	public void setUpdateListener(OrderUpdateListener listener);
}
