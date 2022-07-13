package util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import javax.swing.ImageIcon;

/*
 * ImageUtil provides useful methods for editing images
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public class ImageUtil {

	// Turns an image gray

	public static ImageIcon desaturate(ImageIcon icon) {

		BufferedImage bi = new BufferedImage(
				icon.getIconWidth(),
				icon.getIconHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0,0);
		g.dispose();

		// Pixel by pixel conversion into grayscale
		ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace
                .getInstance(ColorSpace.CS_GRAY), null);
        colorConvert.filter(bi, bi);
        return new ImageIcon(bi);
	}

	// Turns an image into a square

	public static ImageIcon squareify(ImageIcon icon, int sideLength) {

		Image iconImage = icon.getImage();
		Image squareIconImage = iconImage.getScaledInstance(
				sideLength,
				sideLength,
				Image.SCALE_SMOOTH);
		return new ImageIcon(squareIconImage);
	}

}