package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

import layout.TableLayout;
import model.MyTree;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import controler.XMLCache;

import controler.MP3Parser;


/**
 * a class to show the User Interface of the Tag-Editor and to handle all events inside the user interface
 * @author MPGI
 */
public class GUI {
	
	private File choosenFile = null;
    private BufferedImage image=null;
    private byte[] imageArray;
    private String mimeType;
    
    //the main frame
	private JFrame frame;
	
	//some Components to show at the frame
	private JPanel mainPanel;

	private JLabel titleLabel;
	private JLabel albumLabel;
	private JLabel interpretLabel;
	private JLabel yearLabel;
	private JLabel coverLabel;

	private ImageLabel imageLabel;

	private JTextField titleField;
	private JTextField interpretField;
	private JTextField albumField;
	private JTextField yearField;

	private JButton newFolder;
	private JButton save;
	private JButton close;
	
	private JScrollPane pane;
	private JTree tree;
	
	private MP3Node currNode;

	private LinkedList<MP3Node> changedFiles;
	
	/**
	 * the main method to initialize the user interface
	 * @param args is not handled
	 */
	public static void main(String[] args) {
		new GUI();
	}
	
	/**
	 * a constructor to initialize the main frame
	 */
	public GUI() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		frame = new JFrame("MP3-Tag Editor");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(600, 400);

		setGUI();
	}

	/**
	 * the method is called to initialize all UI elements
	 */
	public void setGUI() {
		// Das Layout für das Hauptpanel
		
		changedFiles = new LinkedList<MP3Node>();
		
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
				{ 10, TableLayout.FILL, 10, TableLayout.PREFERRED, 10, TableLayout.PREFERRED },
				{ TableLayout.PREFERRED } };
		JPanel buttonPanel = new JPanel(new TableLayout(buttonlayout));


		newFolder = new JButton("Ordner Einlesen...");
		newFolder.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnval = chooser.showOpenDialog(tree);
				if(returnval == JFileChooser.APPROVE_OPTION) {
					//write all changes you done
					for(int i = 0; i < changedFiles.size(); i++) {
						MP3Parser p = new MP3Parser(changedFiles.get(i).p, changedFiles.get(i).mp3);
						p.writeMP3();
					}
					changedFiles.clear(); //clear the list of all changed mp3files.
					
					//write to XML file!
					if(tree != null) {
						if(((DirectoryNode)tree.getModel().getRoot()).p != null) {
							XMLCache.writeToXmlFile((DirectoryNode) (tree.getModel().getRoot()), ((DirectoryNode) tree.getModel().getRoot()).p.toString());
						}
					}
					
					//create new Tree
					Path p = Paths.get(chooser.getSelectedFile().getPath());
					File p1 = new File(p.toString() + System.getProperty("file.separator") + "mp3cache.xml");

					MyTree tr;
					if(p1.exists()) {//read from XML?!
						DirectoryNode d = new DirectoryNode(p);
						//TODO -> real pathname for dtd file!!!!!!!!!
						XMLCache.readFromXmlFile(d, p1.toString(), p.toFile());
						
						setTree(d);
					}else {//dont read from XML
						tr = new MyTree(p, false);
						setTree(tr.root);
						try {
							Files.walkFileTree(p, tr);
						}catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
					if(((DirectoryNode)tree.getModel().getRoot()).p != null) {
						XMLCache.writeToXmlFile((DirectoryNode) (tree.getModel().getRoot()), ((DirectoryNode) tree.getModel().getRoot()).p.toString());
					}
				}
			}
		});
		
		// ein Save button
		save = new JButton("Speichern");
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(currNode != null) {
					changedFiles.add(currNode);
					currNode.mp3.setTitle(titleField.getText());
					currNode.mp3.setInterpret(interpretField.getText());
					currNode.mp3.setAlbum(albumField.getText());
					if(isValidYear()) {
						currNode.mp3.setYear(yearField.getText());
					}else {
						JOptionPane.showMessageDialog(frame, "Die Jahreszahl ist nicht korrekt!", "Falsche Eingabe", JOptionPane.INFORMATION_MESSAGE);
					}
					if(image == null){                      
	                    currNode.mp3.setCoverArray(null);
						currNode.mp3.setCover(null); 
						currNode.mp3.setMimeType("");
					} else {
						currNode.mp3.setCoverArray(imageArray);
						currNode.mp3.setCover(image);
						currNode.mp3.setMimeType(mimeType);
					}
				}
			}
		});

		close = new JButton("Beenden");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < changedFiles.size(); i++) {
					MP3Parser p = new MP3Parser(changedFiles.get(i).p, changedFiles.get(i).mp3);
					p.writeMP3();
				}
				changedFiles.clear();
				
				if(tree != null) {
					if(((DirectoryNode)tree.getModel().getRoot()).p != null) {
						XMLCache.writeToXmlFile((DirectoryNode) (tree.getModel().getRoot()), ((DirectoryNode) tree.getModel().getRoot()).p.toString());
					}
				}
				
				if(frame != null) {
					frame.dispose();
					frame = null;
				}
			}
		});
		
		titleLabel = new JLabel("Titel");
		interpretLabel = new JLabel("Interpret");
		albumLabel = new JLabel("Album");
		yearLabel = new JLabel("Jahr");
		coverLabel = new JLabel("Cover");

		imageLabel = new ImageLabel();
		imageLabel.setToolTipText("links Klick um ein neues Bild zu laden, rechts Klick um das Bild zu löschen");
		// Event for choosing a new cover or deleting the existing one
		imageLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				switch (e.getButton()) {
				case MouseEvent.BUTTON1:
					JFileChooser chooser = new JFileChooser("./Content");
					int returnVa1 = chooser.showOpenDialog(imageLabel);
					choosenFile = chooser.getSelectedFile();				
					try {
						if(returnVa1 == JFileChooser.APPROVE_OPTION) {
							String[] m = choosenFile.getName().split("\\.");
							if(m[m.length -1].equalsIgnoreCase("jpeg") || m[m.length -1].equalsIgnoreCase("jpg")) {
								mimeType = "image/jpeg";
							}else if(m[m.length -1].equalsIgnoreCase("png")) {
								mimeType = "image/png";
							}
							image = ImageIO.read(choosenFile);
							RandomAccessFile f = new RandomAccessFile(choosenFile, "r");
							imageArray = new byte[(int)f.length()];
							f.read(imageArray);
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
					imageArray = null;
				default:
					break;
				}
			}

		});

		titleField = new JTextField(20);
		interpretField = new JTextField(20);
		albumField = new JTextField(20);
		yearField = new JTextField(20);

		//add everything to the main panel
		mainPanel.add(new JSeparator(JSeparator.VERTICAL), "3,1, 3,12");
		mainPanel.add(new JSeparator(JSeparator.HORIZONTAL), "4,11, 6,11");

		mainPanel.add(newFolder,"1,12");
		// das button panel
		buttonPanel.add(save, "3,0");
		buttonPanel.add(close, "5,0");
		mainPanel.add(buttonPanel, "4,12,6,12");

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
		
		pane = new JScrollPane(tree);
		
		mainPanel.add(pane, "1,1, 1,10");
		
		frame.add(mainPanel);
		frame.setVisible(true);
	}
	
	/**
	 * @return the changedFiles
	 */
	public LinkedList<MP3Node> getChangedFiles() {
		return changedFiles;
	}

	/**
	 * @param changedFiles the changedFiles to set
	 */
	public void setChangedFiles(LinkedList<MP3Node> changedFiles) {
		this.changedFiles = changedFiles;
	}

	/**
	 * a method to check weather the String inside the 'yearField' is a valid year or not
	 * @return true if it is a valid year number consisting of 4 numbers, else otherwise
	 */
	public boolean isValidYear() {
		char[] year = yearField.getText().toCharArray();
		if(year.length != 4) {
			return false;
		}
		for(int i = 0; i < year.length; i++) {
			if(!Character.isDigit(year[i])) {
				return false;
			}
		}
		return true;
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
	 * a method to create a tree from a root node and initialize an actionlistener to perform all needed actions. Will redraw the UI afterwards.
	 * @param root the root node to initialize the tree
	 */
	public void setTree(DefaultMutableTreeNode root) {
		mainPanel.remove(pane);
		
		tree = new JTree(root);
		
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if(tree.getLastSelectedPathComponent() instanceof MP3Node) {
					currNode = (MP3Node) tree.getLastSelectedPathComponent();
					titleField.setText(currNode.mp3.getTitle());
					interpretField.setText(currNode.mp3.getInterpret());
					albumField.setText(currNode.mp3.getAlbum());
					yearField.setText(currNode.mp3.getYear());
					if(currNode.mp3.getCover() != null) {
						imageLabel.setIcon(new ImageIcon(currNode.mp3.getCover()));
					}else {
						imageLabel.setIcon(new ImageIcon());
					}
					image = currNode.mp3.getCover();
					imageArray = currNode.mp3.getCoverArray();
					mimeType = currNode.mp3.getMimeType();
				}
				
			}
		});
		pane = new JScrollPane(tree);
		mainPanel.add(pane, "1,1, 1,10");
		mainPanel.validate();
	}

	/**
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * @return the newFolder
	 */
	public JButton getNewFolder() {
		return newFolder;
	}

	/**
	 * @return the save
	 */
	public JButton getSave() {
		return save;
	}

	/**
	 * @return the close
	 */
	public JButton getClose() {
		return close;
	}

	/**
	 * @return the currNode
	 */
	public MP3Node getCurrNode() {
		return currNode;
	}

	/**
	 * @param currNode the currNode to set
	 */
	public void setCurrNode(MP3Node currNode) {
		this.currNode = currNode;
	}

}