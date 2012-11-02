package view;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImageLabel extends JLabel{
	
	ImageIcon imageIcon;
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
		super.paintComponent(g);
		
		if(imageIcon != null) {
			
			g.drawImage(imageIcon.getImage(), 
					0, 0, 
					getWidth(), getHeight(),
					0, 0,
					imageIcon.getIconWidth(), imageIcon.getIconHeight(),
					null);
		}
	}
	
	
}
