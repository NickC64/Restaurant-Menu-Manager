package gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/*
 * ImageFileChooser sets up the JFileChooser that is displayed when the user clicks "upload image" on the menu tab
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public class ImageFileChooser extends JFileChooser {

	public ImageFileChooser() {

		setFileSelectionMode(JFileChooser.FILES_ONLY);
		addChoosableFileFilter(new ImageFilter());
		setAcceptAllFileFilterUsed(false);
		setDialogTitle("Choose an image");
	}

	/* Sets up custom file filter to only accept images */
	class ImageFilter extends FileFilter {

		@Override
		public boolean accept(File f) {

		    if (f.isDirectory()) {
		        return true;
		    }

			String fileName = f.getName();
			String extension = "";

			int i = fileName.lastIndexOf('.');

			if (i > 0) {

			   extension = fileName.substring(i + 1);
			}

		    if (extension != null) {

		    	for (AcceptedFileExtensions acceptedExtension : AcceptedFileExtensions.values()) {

		    		if (acceptedExtension.toString().equals(extension)) {

		    			return true;
		    		}
		    	}

		    	return false;
		    }

		    return false;
		}

		@Override
		public String getDescription() {

			return "Images Only";
		}
	}

	enum AcceptedFileExtensions {
		TIFF,
		TIF,
		GIF,
		JPEG,
		JPG,
		BMP,
		PNG,
		RAW;

		@Override
		public String toString() {

			return super.toString().toLowerCase();
		}

	}
}
