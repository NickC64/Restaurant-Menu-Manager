package gui;

/*
 * EditPanelUpdateListener allows for the menu tab to listen for updates from the edit panel
 *
 *  Author: Nick Chen
 *  Date: June 18
 */

import javax.swing.ImageIcon;

public interface EditPanelUpdateListener {

	public boolean nameExists(String name);
	public void onNameChange(String name);
	public void onPriceChange(Double price);
	public void onImageChange(ImageIcon image);
	public void onNotesChange(String notes);
}
