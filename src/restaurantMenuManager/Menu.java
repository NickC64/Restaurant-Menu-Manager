package restaurantMenuManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/*
 * Menu is the data class for MenuItems in the menu
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public class Menu extends ArrayList<MenuItem> implements Serializable {

	// Creates a deep clone of the menu, necessary for saving functions
	public Menu cloneMenu() {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(this);
			byte[] byteData = bos.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
			return (Menu) new ObjectInputStream(bais).readObject();
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}

	}

}
