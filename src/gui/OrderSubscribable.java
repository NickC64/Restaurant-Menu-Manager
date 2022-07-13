package gui;

import java.util.ArrayList;

import restaurantMenuManager.MenuItem;
import restaurantMenuManager.Order;
import util.PriceUtil;

interface OrderModelListener {

	public void rowAdded(int index);

	public void rowRemoved(int index);

	public void orderCleared();

	public void quantityUpdated(int index);
}

/*
 * OrderSubscribable works similarly to MenuSubscribable in that it communicates
 * changes to the order to its listeners
 *
 * Author: Nick Chen Date: June 18, 2022
 */

public class OrderSubscribable {

	private ArrayList<OrderModelListener> listeners = new ArrayList<>();
	private Order orderData;

	// Keeps track of the quantity of a menu item in an order
	private ArrayList<Integer> itemQuantityList = new ArrayList<>();

	public void newOrder() {
		orderData = new Order();
		itemQuantityList = new ArrayList<>();

		for (OrderModelListener listener : listeners) {

			listener.orderCleared();
		}
	}

	public Order getOrderData() {

		return orderData;
	}

	public MenuItem get(int index) {

		return orderData.get(index);
	}

	public int size() {

		return orderData.size();
	}

	// Adds a listener

	public void addOrderListener(OrderModelListener listener) {

		listeners.add(listener);
	}

	public String getName(int index) {

		return orderData.get(index).getName();
	}

	public double getPrice(int index) {

		return orderData.get(index).getPrice();
	}

	public int getQuantity(int index) {

		return itemQuantityList.get(index);
	}

	public double getItemTotalPrice(int index) {

		return PriceUtil.roundMoney(getPrice(index) * getQuantity(index));
	}

	public double getTotalPrice() {

		double total = 0d;
		for (int i = 0; i < size(); i++) {

			total += getPrice(i) * getQuantity(i);
		}

		return PriceUtil.roundMoney(total);
	}

	public void setQuantity(int index, int value) {

		itemQuantityList.set(index, value);
	}

	// Adds an item to the order

	public void addItem(MenuItem itemToAdd) {

		// Determines if the item is already present in the order
		int flag = searchForItem(itemToAdd);

		// Either adds a new row for the item, or increase the quantity of the item by 1
		// (if it exists)
		if (flag != -1) {

			int newValue = itemQuantityList.get(flag) + 1;
			itemQuantityList.set(flag, newValue);

			for (OrderModelListener listener : listeners) {

				listener.quantityUpdated(flag);
			}

		} else {

			orderData.add(itemToAdd);
			itemQuantityList.add(1);

			for (OrderModelListener listener : listeners) {

				listener.rowAdded(flag);
			}
		}
	}

	// Deletes an item from the order

	public int deleteItem(MenuItem itemToDelete) {

		int flag = searchForItem(itemToDelete);

		// Either removes the row with the item, or decreases quantity by 1 (if quantity
		// is 2 or greater)
		if (flag != -1) {

			if (itemQuantityList.get(flag) == 1) {

				orderData.remove(flag);
				itemQuantityList.remove(flag);

				for (OrderModelListener listener : listeners) {

					listener.rowRemoved(flag);
				}
				return 0;
			}

			int newValue = itemQuantityList.get(flag) - 1;
			itemQuantityList.set(flag, newValue);

			for (OrderModelListener listener : listeners) {

				listener.quantityUpdated(flag);
			}

			return newValue;
		}

		return -1;
	}

	public int searchForItem(MenuItem searchKey) {

		for (int i = 0; i < orderData.size(); i++) {

			if (orderData.get(i).equals(searchKey)) {

				return i;
			}
		}

		return -1;
	}

}
