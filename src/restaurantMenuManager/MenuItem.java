package restaurantMenuManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.ImageIcon;

import util.ImageUtil;

/*
 * MenuItem is the building block for menus
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public class MenuItem implements Serializable {

	private String name;
	private double price;
	private ImageIcon image;
	private String notes;
	private boolean available;

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public double getPrice() {

		return price;
	}

	public void setPrice(double price) {

		this.price = price;
	}

	public ImageIcon getImage(boolean saturated) {

		if (saturated) {
			return image;
		}

		return ImageUtil.desaturate(image);
	}

	public ImageIcon getImage(boolean saturated, int sideLength) {

		if (image == null) {

			return null;
		}

		return ImageUtil.squareify(getImage(saturated), sideLength);
	}

	public void setImage(ImageIcon icon) {

		this.image = icon;
	}

	public String getNotes() {

		return notes;
	}

	public void setNotes(String notes) {

		this.notes = notes;
	}

	public boolean isAvailable() {

		return available;
	}

	public void setAvailable(boolean available) {

		this.available = available;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, price, image, notes, available);

	}

	// Compares two MenuItems to see if they're equal, necessary for saving
	// functions

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj instanceof MenuItem) {

			MenuItem item = (MenuItem) obj;

			if (!Arrays.asList(getName(), getPrice(), isAvailable(), getNotes())
					.equals(Arrays.asList(item.getName(), item.getPrice(), item.isAvailable(), item.getNotes()))) {

				return false;
			}

			try {

				ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
				ObjectOutputStream oos1 = new ObjectOutputStream(bos1);
				ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
				ObjectOutputStream oos2 = new ObjectOutputStream(bos2);
				oos1.writeObject(item.getImage(true));
				oos2.writeObject(this.getImage(true));

				byte[] ba1 = bos1.toByteArray();
				byte[] ba2 = bos2.toByteArray();

				if (!Arrays.equals(ba1, ba2)) {
					return false;
				}

			} catch (IOException e) {

				e.printStackTrace();
				return false;

			}
		}

		return true;

	}

}
