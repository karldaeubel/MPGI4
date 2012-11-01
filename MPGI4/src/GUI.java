import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import info.clearthought.layout.TableLayout;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileSystemView;

public class GUI{
	
	JFrame frame;
	
	JPanel mainPanel;
	
	JLabel titleLabel;
	JLabel albumLabel;
	JLabel interpretLabel;
	JLabel yearLabel;
	JLabel coverLabel;

	// these variables are necessary for displaying the cover
	// short guide: new ImageIcon(cover.jpg) -> new JLabel(imageIcon)
	JLabel imageLabel;
	ImageIcon imageIcon;
	
	JTextField titleField;
	JTextField interpretField;
	JTextField albumField;
	JTextField yearField;
	
	// these strings represent the tags
	// later they should be moved to MODEL
	String titleString = "";
	String interpretString = "";
	String albumString = "";
	String yearString = "";
	
	public GUI() {
		
		frame = new JFrame("MP3-Tag Editor");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(400, 300);
		
		setGUI();
		
		frame.setVisible(true);

	}
	
	public void setGUI() {
		double[][] layout = {
				{10, TableLayout.FILL, 10, TableLayout.FILL, 10, TableLayout.FILL, 10},
				{10, TableLayout.PREFERRED, TableLayout.PREFERRED, 10, TableLayout.PREFERRED, TableLayout.PREFERRED, 10, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL, 10}	
		};
		mainPanel = new JPanel(new TableLayout(layout));
		
		frame.add(mainPanel);
		
		
		titleLabel = new JLabel("Titel");
		interpretLabel = new JLabel("Interpret");
		albumLabel = new JLabel("Album");
		yearLabel = new JLabel("Jahr");
		coverLabel = new JLabel("Cover");
		
		// the cover - so far hardcoded
		// TODO adjust the size of the image 
		imageLabel = new JLabel();
		File file = new File("./Content/thriller-cover-michael-jackson.jpg");
		BufferedImage originalImage = null;
		try {
			originalImage = ImageIO.read(file);
		} catch (IOException e1) {
		}
		// if no image is available
		if (originalImage == null){
			imageIcon = new ImageIcon();
		}
		// if an image exists
		else {
			imageIcon = new ImageIcon(originalImage);
			imageLabel.setIcon(imageIcon);

			/////////////////////////////////////////////////////////////////////////////
			// hier wird´s kribblig... die ersten beiden int-parameter müssten sich an //
			// der Größe von TableLayout.FILL orientieren - für jede Idee offen!       //
			/////////////////////////////////////////////////////////////////////////////
			imageIcon.setImage(imageIcon.getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT));
		}
		imageLabel.setBorder(new LineBorder(Color.BLACK));
				
		// the following actionListeners only react on "ENTER"
		titleField = new JTextField(20);
		titleField.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				titleString = titleField.getText();
				System.out.println(titleString);
			}
		});
		
		interpretField = new JTextField(20);
		interpretField.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				interpretString = interpretField.getText();
				System.out.println(interpretString);
			}
		});
		
		albumField = new JTextField(20);
		albumField.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				albumString = albumField.getText();
				System.out.println(albumString);
			}
		});
		
		yearField = new JTextField(20);
		yearField.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				yearString = yearField.getText();
				System.out.println(yearString);
			}
		});
		
		mainPanel.add(titleLabel, "3,1");
		mainPanel.add(interpretLabel, "3,4");
		mainPanel.add(albumLabel, "5,4");
		mainPanel.add(yearLabel, "5,7");
		mainPanel.add(coverLabel, "3,7");
		
		mainPanel.add(imageLabel, "3,8,3,9");
		mainPanel.add(titleField, "3,2,5,1");
		mainPanel.add(interpretField, "3,5");
		mainPanel.add(albumField, "5,5");
		mainPanel.add(yearField, "5,8");
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new GUI();
	}
}