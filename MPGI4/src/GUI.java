import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import layout.TableLayout;


public class GUI{
	
	JFrame frame;
	
	JPanel mainPanel;
	
	JLabel title;
	JLabel album;
	JLabel interpret;
	JLabel year;
	JLabel cover;
	
	JTextField titleField;
	JTextField interpretField;
	JTextField albumField;
	JTextField yearField;
	
	JLabel image;
	
	public GUI() {
		
		frame = new JFrame("MP3-Tag Editor");
		
		frame.setVisible(true);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(400, 300);
		
		setGUI();
	}
	
	public void setGUI() {
		double[][] layout = {
				{10, TableLayout.FILL, 10, TableLayout.FILL, 10, TableLayout.FILL, 10},
				{10, TableLayout.PREFERRED, TableLayout.PREFERRED, 10, TableLayout.PREFERRED, TableLayout.PREFERRED, 10, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL}	
		};
		mainPanel = new JPanel(new TableLayout(layout));
		
		frame.add(mainPanel);
		
		
		title = new JLabel("Titel");
		interpret = new JLabel("Interpret");
		album = new JLabel("Album");
		year = new JLabel("Jahr");
		cover = new JLabel("Cover");
		image = new JLabel();
		
		titleField = new JTextField(20);
		interpretField = new JTextField(20);
		albumField = new JTextField(20);
		yearField = new JTextField(20);
		
		mainPanel.add(title, "3,1");
		mainPanel.add(interpret, "3,4");
		mainPanel.add(album, "5,4");
		mainPanel.add(year, "5,7");
		mainPanel.add(cover, "3,7");
		
		mainPanel.add(image, "3,8,3,9");
		mainPanel.add(titleField, "3,2,5,1");
		mainPanel.add(interpretField, "3,5");
		mainPanel.add(albumField, "5,5");
		mainPanel.add(yearField, "5,8");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GUI test = new GUI();
	}
}