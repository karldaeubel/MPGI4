package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import layout.TableLayout;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

//import exceptions.YearOutOfTimePeriodException;


public class GUI {
	
	static File choosenFile = null;
    BufferedImage image=null;
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
	
	JTree tree;
	
	MP3Node currNode;

	public GUI() {

		frame = new JFrame("MP3-Tag Editor");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(600, 400);

	}

	public void setGUI() {
		// Das Layout für das Hauptpanel
		double[][] layout = {
				{ 10, TableLayout.FILL, 5, 5, TableLayout.FILL, 10,
						TableLayout.FILL, 10 },
				{ 10, TableLayout.PREFERRED, TableLayout.PREFERRED, 10,
						TableLayout.PREFERRED, TableLayout.PREFERRED, 10,
						TableLayout.PREFERRED, TableLayout.PREFERRED,
						TableLayout.FILL, 10, 10, TableLayout.PREFERRED, 10 } };
		mainPanel = new JPanel(new TableLayout(layout));

		// Das Layout für den Button
		double[][] buttonlayout = {
				{ TableLayout.FILL, 10, TableLayout.PREFERRED },
				{ TableLayout.PREFERRED } };
		JPanel buttonPanel = new JPanel(new TableLayout(buttonlayout));

		// ein Save button
		save = new JButton("Speichern");
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("SAVE!!");
				//TODO Bilder hinzufügen! und bei änderung in currNode ändern
				if(currNode != null) {
					currNode.mp3.setTitle(titleField.getText());
					currNode.mp3.setInterpret(interpretField.getText());
					currNode.mp3.setAlbum(albumField.getText());
					currNode.mp3.setYear(yearField.getText());
					  if(image == null){                      
	                    	currNode.mp3.setCover(new BufferedImage(2,2,2));  					
						}
	                    else
						currNode.mp3.setCover(image);
						
				}
				
				/*
				try {
					int year = Integer.parseInt(yearString);
					if (year <= 1905 || year > 2012)
						throw new YearOutOfTimePeriodException();
				}
				// if year is not a number
				catch (NumberFormatException formatException) {
				System.out.println("Wrong fromat!");
				}
				// if year is not between 1905 and 2012
				catch (YearOutOfTimePeriodException yearException) {
					System.out.println("Year is not valid ");
				}
				*/
				// for debug only
				
			}
		});

		titleLabel = new JLabel("Titel");
		interpretLabel = new JLabel("Interpret");
		albumLabel = new JLabel("Album");
		yearLabel = new JLabel("Jahr");
		coverLabel = new JLabel("Cover");

		imageLabel = new ImageLabel();
		// Event for choosing a new cover or deleting the existing one
		imageLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				switch (e.getButton()) {
				case MouseEvent.BUTTON1:
					JFileChooser chooser = new JFileChooser("./Content");
					int returnVA1 = chooser.showOpenDialog(imageLabel);
					choosenFile = chooser.getSelectedFile();				
					try {
						if(choosenFile != null) {
							image = ImageIO.read(choosenFile);
						}
					} catch (IOException ex) {
					}
					if(image != null) {
						imageLabel.setIcon(new ImageIcon(image));
					}
					break;
				// if right mouse button is pressed, delete existing Icon
				case MouseEvent.BUTTON3:
					ImageIcon imageI = new ImageIcon();
					imageLabel.setIcon(imageI);	
					image=null;
				default:
					break;
				}
			}

		});

		
		titleField = new JTextField(20);

		interpretField = new JTextField(20);

		albumField = new JTextField(20);

		yearField = new JTextField(20);

		// Seperatoren-> weglassen oder findet ihr das ok?
		mainPanel.add(new JSeparator(JSeparator.VERTICAL), "3,1, 3,12");
		mainPanel.add(new JSeparator(JSeparator.HORIZONTAL), "4,11, 6,11");

		// das button panel
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
		
		if(tree != null) {
			tree.addTreeSelectionListener(new TreeSelectionListener() {
				
				@Override
				public void valueChanged(TreeSelectionEvent e) {
					if(tree.getLastSelectedPathComponent() instanceof MP3Node) {
						currNode = (MP3Node) tree.getLastSelectedPathComponent();
						titleField.setText(currNode.mp3.getTitle());
						interpretField.setText(currNode.mp3.getInterpret());
						albumField.setText(currNode.mp3.getAlbum());
						yearField.setText(currNode.mp3.getYear());
						imageLabel.setIcon(new ImageIcon(currNode.mp3.getCover()));
					}
					
				}
			});
			
			mainPanel.add(new JScrollPane(tree), "1,1, 1,12");
		}

		frame.add(mainPanel);
		frame.setVisible(true);
	}

	/**
	 * @return the imageLabel
	 */
	public ImageLabel getImageLabel() {
		return imageLabel;
	}

	/**
	 * @param imageLabel the imageLabel to set
	 */
	public void setImageLabel(ImageLabel imageLabel) {
		this.imageLabel = imageLabel;
	}

	/**
	 * @return the titleField
	 */
	public JTextField getTitleField() {
		return titleField;
	}

	/**
	 * @param titleField the titleField to set
	 */
	public void setTitleField(JTextField titleField) {
		this.titleField = titleField;
	}

	/**
	 * @return the interpretField
	 */
	public JTextField getInterpretField() {
		return interpretField;
	}

	/**
	 * @param interpretField the interpretField to set
	 */
	public void setInterpretField(JTextField interpretField) {
		this.interpretField = interpretField;
	}

	/**
	 * @return the albumField
	 */
	public JTextField getAlbumField() {
		return albumField;
	}

	/**
	 * @param albumField the albumField to set
	 */
	public void setAlbumField(JTextField albumField) {
		this.albumField = albumField;
	}

	/**
	 * @return the yearField
	 */
	public JTextField getYearField() {
		return yearField;
	}

	/**
	 * @param yearField the yearField to set
	 */
	public void setYearField(JTextField yearField) {
		this.yearField = yearField;
	}

	/**
	 * @return the tree
	 */
	public JTree getTree() {
		return tree;
	}

	/**
	 * @param tree the tree to set
	 */
	public void setTree(JTree tree) {
		this.tree = tree;
	}

}