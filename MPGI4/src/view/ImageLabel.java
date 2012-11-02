package view;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImageLabel extends JLabel{
	
	public ImageLabel() {
		super();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		ImageIcon temp = (ImageIcon) getIcon();
		if(temp != null) {
			
			g.drawImage(temp.getImage(), 
					0, 0, 
					getWidth(), getHeight(),
					0, 0,
					temp.getIconWidth(), temp.getIconHeight(),
					null);
		}
	}
	
	
}
