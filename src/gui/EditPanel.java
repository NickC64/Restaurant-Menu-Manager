package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import restaurantMenuManager.MenuItem;
import util.ImageUtil;
import util.PriceUtil;

/*
 * EditPanel allows for the user to edit the fields of the menu item that was clicked on the menu tab
 *
 *  Author: Nick Chen
 *  Date: June 18
 */

public class EditPanel extends JPanel implements EditPanelUpdater {

	/* JComponents in EditPanel */

	private JTextField nameField;
	private JLabel errorLabel; // Displays when value in nameField is invalid
	private JSpinner priceSpinner;
	private JButton clearImageButton;
	private JButton uploadImageButton;
	private JLabel imageLabel;
	private JTextArea notesField;

	private boolean listenersEnabled; // Prevents listeners from triggering when not desired

	EditPanelUpdateListener listener; // Allows EditPanel to communicate with MenuTab

	@Override
	public void setEditPanelUpdateListener(EditPanelUpdateListener listener) {

		this.listener = listener;
	}

	/* Initiates components, adds their listeners and disables the relevant ones */

	public EditPanel() {
		JLabel nameLabel = new JLabel("Name:");

		nameField = new JTextField(10);
		nameField.getDocument().addDocumentListener(new NameListener());
		nameField.addFocusListener(new NameListener());

		errorLabel = new JLabel();
		errorLabel.setVisible(false);
		errorLabel.setForeground(Color.red);
		errorLabel.setFont(new Font(errorLabel.getFont().getName(), Font.PLAIN, 9));

		JLabel priceLabel = new JLabel("Price:");

		priceSpinner = new JSpinner();
		priceSpinner.setPreferredSize(nameField.getPreferredSize());

		SpinnerNumberModel priceSpinnerModel = new SpinnerNumberModel( // Adds constraints on what can be inputed into
																		// price field
				Double.valueOf(0d), // Sets minimum price to 0
				Double.valueOf(0d), // Sets maximum price to unlimited
				null, Double.valueOf(0.01d)); // Sets increment value by 1 cent
		priceSpinner.setModel(priceSpinnerModel);
		priceSpinner.setEditor(new JSpinner.NumberEditor(priceSpinner, "0.00")); // Formats values to money decimals
		priceSpinner.addChangeListener(new PriceListener());

		clearImageButton = new JButton("X");
		clearImageButton.setPreferredSize(new Dimension(25, 25));
		clearImageButton.setMargin(new Insets(0, 0, 0, 0));
		clearImageButton.addActionListener(new ClearImageListener());

		uploadImageButton = new JButton("Upload Image");
		uploadImageButton.addActionListener(new UploadListener());

		imageLabel = new JLabel();
		imageLabel.setPreferredSize(new Dimension(160, 160));
		imageLabel.setBorder(BorderFactory.createDashedBorder(null, 5, 5));
		imageLabel.setOpaque(true);
		imageLabel.setBackground(Color.lightGray);
		imageLabel.addPropertyChangeListener("icon", new ImageListener()); // To disable/enable the clear image button
																			// depending on the presence of an image

		JLabel notesLabel = new JLabel("Notes:");
		notesField = new JTextArea();
		notesField.setLineWrap(true);
		notesField.setWrapStyleWord(true);
		notesField.getDocument().addDocumentListener(new NotesListener());

		JScrollPane notesScrollPane = new JScrollPane(notesField);
		notesScrollPane.setPreferredSize(new Dimension(155, 155));
		notesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints c1 = new GridBagConstraints();

		c1.gridx = 0;
		c1.gridy = 0;
		c1.gridwidth = 1;
		c1.anchor = GridBagConstraints.LINE_END;
		add(nameLabel, c1);

		c1.gridx = 1;
		c1.gridy = 0;
		c1.gridwidth = 1;
		c1.insets = new Insets(0, 5, 0, 0);
		c1.anchor = GridBagConstraints.LINE_START;
		add(nameField, c1);

		c1.gridx = 1;
		c1.gridy = 2;
		c1.gridheight = 1;
		c1.gridwidth = 1;
		c1.insets = new Insets(0, 10, 0, 0);
		c1.anchor = GridBagConstraints.FIRST_LINE_START;
		add(errorLabel, c1);

		c1.gridx = 0;
		c1.gridy = 2;
		c1.gridheight = 1;
		c1.gridwidth = 1;
		c1.insets = new Insets(13, 0, 0, 0);
		c1.anchor = GridBagConstraints.LINE_END;
		add(priceLabel, c1);

		c1.gridx = 1;
		c1.gridy = 2;
		c1.gridwidth = 1;
		c1.insets = new Insets(13, 5, 0, 0);
		c1.anchor = GridBagConstraints.LINE_START;
		add(priceSpinner, c1);

		c1.gridx = 0;
		c1.gridy = 3;
		c1.gridwidth = 2;
		c1.insets = new Insets(10, 0, 0, 0);
		c1.anchor = GridBagConstraints.LINE_END;
		add(imageLabel, c1);

		c1.gridx = 0;
		c1.gridy = 9;
		c1.gridwidth = 1;
		c1.insets = new Insets(0, 0, 0, 0);
		c1.anchor = GridBagConstraints.FIRST_LINE_END;
		add(clearImageButton, c1);

		c1.gridx = 1;
		c1.gridy = 9;
		c1.gridwidth = 1;
		c1.anchor = GridBagConstraints.FIRST_LINE_START;
		add(uploadImageButton, c1);

		c1.gridx = 1;
		c1.gridy = 10;
		c1.gridwidth = 1;
		c1.ipady = 0;
		c1.insets = new Insets(5, 0, 0, 0);
		c1.anchor = GridBagConstraints.LAST_LINE_END;
		add(notesLabel, c1);

		c1.gridx = 0;
		c1.gridy = 11;
		c1.gridwidth = 2;
		c1.insets = new Insets(0, 0, 0, 0);
		c1.anchor = GridBagConstraints.FIRST_LINE_END;
		add(notesScrollPane, c1);

		for (Component c : getComponents()) {

			c.setMinimumSize(c.getPreferredSize());
		}
		setOpaque(true);
		setComponentsEnabled(false);
		Border border = BorderFactory.createTitledBorder("Customize Menu Item");
		Border padding = new EmptyBorder(10, 15, 10, 20);
		setBorder(new CompoundBorder(border, padding));

		listenersEnabled = true;

	}

	/*
	 * Enables components, method is used when user clicks on a MenuItem on the
	 * MenuTab
	 */

	public void setComponentsEnabled(boolean enabled) {

		for (Component cp : getComponents()) {

			cp.setEnabled(enabled);
		}

		notesField.setEnabled(enabled);

		if (enabled) {

			setBackground(Color.white);
		} else {

			setBackground(Color.lightGray);
		}
	}

	/* Populates EditPanel fields with values of clicked MenuItem on MenuTab */

	public void displayMenuItem(MenuItem item) {

		listenersEnabled = false;
		nameField.setText(item.getName());
		nameField.requestFocus();
		nameField.setBackground(Color.white);
		errorLabel.setVisible(false);
		priceSpinner.setValue(item.getPrice());
		imageLabel.setIcon(item.getImage(true, imageLabel.getWidth()));
		notesField.setText(item.getNotes());
		listenersEnabled = true;
	}

	/* De-populates EditPanel fields */

	public void clearMenuItem() {

		listenersEnabled = false;
		nameField.setText("");
		priceSpinner.setValue(Double.valueOf(0d));
		imageLabel.setIcon(null);
		notesField.setText("");
		listenersEnabled = true;
	}

	/* Listener for nameField */

	class NameListener implements DocumentListener, FocusListener {

		String lastValidName;

		/* Document Listener Methods */

		@Override
		public void insertUpdate(DocumentEvent e) {

			onChange();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {

			onChange();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {

			onChange();
		}

		private void onChange() {

			// Name must not already exist and not be blank to be valid

			if (listenersEnabled) {

				listener.onNameChange(nameField.getText());

				if (listener.nameExists(nameField.getText().trim())) {

					errorLabel.setText("Name already exists");
					errorLabel.setVisible(true);
					nameField.setBackground(new Color(255, 222, 222));
				} else if (nameField.getText().trim().equals("")) {


					errorLabel.setText("Name cannot be blank");
					errorLabel.setVisible(true);
					nameField.setBackground(new Color(255, 222, 222));
				} else {

					errorLabel.setVisible(false);
					nameField.setBackground(Color.white);
				}
			}

		}

		/* Keeps a record of the original name of the MenuItem */

		@Override
		public void focusGained(FocusEvent e) {

			lastValidName = nameField.getText();
		}

		/*
		 * Reverts to the original name if the user exits the nameField without entering
		 * a valid name
		 */

		@Override
		public void focusLost(FocusEvent e) {

			String correctedText = nameField.getText().trim();

			if (!correctedText.equals("")) {

				if (!listener.nameExists(correctedText)) {

					lastValidName = correctedText;
					nameField.setText(correctedText);
					listener.onNameChange(correctedText);
				} else {

					listener.onNameChange(lastValidName);
				}

			} else {

				nameField.setText(lastValidName);
				listener.onNameChange(lastValidName);
			}

		}

	}

	/* Listener for priceSpinner */

	class PriceListener implements ChangeListener {

		// Shortens user input to two decimals in case they entered more (eg. 11.324
		// instead of 11.32)

		@Override
		public void stateChanged(ChangeEvent e) {

			if (listenersEnabled) {

				SpinnerModel model = priceSpinner.getModel();
				model.setValue(PriceUtil.roundMoney((Double) model.getValue()));
				listener.onPriceChange((Double) model.getValue());
			}

		}

	}

	/* Listener for imageLabel */

	class ImageListener implements PropertyChangeListener {

		// If no image is present, disable the clear image button

		@Override
		public void propertyChange(PropertyChangeEvent evt) {

			if (imageLabel.getIcon() == null) {

				clearImageButton.setEnabled(false);
			} else {

				clearImageButton.setEnabled(true);
			}

		}

	}

	/* Listener for clearImageButton */

	class ClearImageListener implements ActionListener {

		// Clears image when pressed
		@Override
		public void actionPerformed(ActionEvent e) {

			listener.onImageChange(null);
			imageLabel.setIcon(null);
		}

	}

	/* Listener for uploadButton */

	class UploadListener implements ActionListener {

		// Gets new image from user selection of JFileChooser

		@Override
		public void actionPerformed(ActionEvent e) {

			UIManager.put("FileChooser.readOnly", Boolean.TRUE); // Disable folder creation
			ImageFileChooser fileChooser = new ImageFileChooser();
			fileChooser.showOpenDialog(null);
			File f = fileChooser.getSelectedFile();
			if (f != null) {

				String fileName = f.getAbsolutePath();
				Image image = null;
				try {

					image = ImageIO.read(new File(fileName));
				} catch (IOException e1) {

					JOptionPane.showMessageDialog(null, "Unable to load image", null, JOptionPane.INFORMATION_MESSAGE);
				}

				ImageIcon imageIcon = new ImageIcon(image);
				listener.onImageChange(imageIcon);
				imageLabel.setIcon(ImageUtil.squareify(imageIcon, imageLabel.getWidth()));
			}

		}

	}

	/* Listener for notesField */

	class NotesListener implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {

			onChange();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {

			onChange();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {

			onChange();
		}

		private void onChange() {

			if (listenersEnabled) {

				listener.onNotesChange(notesField.getText());
			}

		}

	}

}
