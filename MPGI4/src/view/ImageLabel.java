package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * a class to hold a picture in a Label and resize it to fit it into the size of the label
 * @author MPGI
 */
public class ImageLabel extends JLabel {
	//the picture to show on the label
	private ImageIcon imageIcon;

	public ImageLabel() {
		super();
		imageIcon = new ImageIcon();
	}

	public void setIcon(ImageIcon icon) {
		super.setIcon(icon);
		imageIcon = icon;
		super.setIcon(null);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		super.paintComponent(g2d);

		// to maintain ratio
		int width = getWidth();
		int height = getHeight();
		//calculate the right size of the picture
		if (width <= height)
			height = width;
		else
			width = height;

		if (imageIcon != null) {
			//resize the picture
			g2d.drawImage(imageIcon.getImage(), 0, 0, width, height, 0, 0,
					imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);
			
			// painting a border
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(1));
			g2d.drawRect(1, 1, width - 2, height - 2);
		}else{
			
			g2d.drawImage(imageIcon.getImage(), 0, 0, width, height, 0, 0,
					imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);
			// painting a border
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(1));
			g2d.drawRect(2, 2, width - 2, height - 2);
		}
	}
}
