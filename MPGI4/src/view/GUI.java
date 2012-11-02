package view;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import layout.TableLayout;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

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
	ImageLabel imageLabel;
	ImageIcon imageIcon;
	
	JTextField titleField;
	JTextField interpretField;
	JTextField albumField;
	JTextField yearField;
	
	JButton save;
	
	// these strings represent the tags
	// later they should be moved to MODEL
	String titleString = "";
	String interpretString = "";
	String albumString = "";
	String yearString = "";
	
	public GUI() {
		
		frame = new JFrame("MP3-Tag Editor");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(600, 400);
		
		setGUI();
		
		frame.setVisible(true);

	}
	
	public void setGUI() {
		//Das Layout für das Hauptpanel
		double[][] layout = {
				{10, TableLayout.FILL, 5, 5, TableLayout.FILL, 10, TableLayout.FILL, 10},
				{10, TableLayout.PREFERRED, TableLayout.PREFERRED, 10, TableLayout.PREFERRED, TableLayout.PREFERRED, 10, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL, 10, 10, TableLayout.PREFERRED, 10}	
		};
		mainPanel = new JPanel(new TableLayout(layout));
		
		//Das Layout für den Button
		double[][] buttonlayout = {
				{TableLayout.FILL, 10, TableLayout.PREFERRED},
				{TableLayout.PREFERRED}
		};
		JPanel buttonPanel = new JPanel(new TableLayout(buttonlayout));
		
		//ein Save button
		save = new JButton("Speichern");
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("SAVE!!");
				
			}
		});
		
		titleLabel = new JLabel("Titel");
		interpretLabel = new JLabel("Interpret");
		albumLabel = new JLabel("Album");
		yearLabel = new JLabel("Jahr");
		coverLabel = new JLabel("Cover");
		
		// the cover - so far hardcoded
		// TODO adjust the size of the image 
		imageLabel = new ImageLabel();
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
			
			/*
			 * Das Bild wird nun in jedem Aufruf von paintComponent neu gezeichnet!
			 * TODO korrekte Skalierung des Bildes!!
			 */
			/////////////////////////////////////////////////////////////////////////////
			// hier wird�s kribblig... die ersten beiden int-parameter m�ssten sich an //
			// der Gr��e von TableLayout.FILL orientieren - f�r jede Idee offen!       //
			/////////////////////////////////////////////////////////////////////////////
			//imageIcon.setImage(imageIcon.getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT));
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
		
		//Seperatoren-> weglassen oder findet ihr das ok?
		mainPanel.add(new JSeparator(JSeparator.VERTICAL), "2,1, 2,12");
		mainPanel.add(new JSeparator(JSeparator.HORIZONTAL), "4,11, 6,11");
		
		//das button panel
		buttonPanel.add(save, "2,0");
		mainPanel.add(buttonPanel, "6,12");
		
		mainPanel.add(titleLabel, "4,1");
		mainPanel.add(interpretLabel, "4,4");
		mainPanel.add(albumLabel, "6,4");
		mainPanel.add(yearLabel, "6,7");
		mainPanel.add(coverLabel, "4,7");
		
		mainPanel.add(imageLabel, "4,8,4,9");
		
		mainPanel.add(titleField, "4,2,6,1");
		mainPanel.add(interpretField, "4,5");
		mainPanel.add(albumField, "6,5, 6,5");
		mainPanel.add(yearField, "6,8, 6,8");
		
		frame.add(mainPanel);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new GUI();
	}
}