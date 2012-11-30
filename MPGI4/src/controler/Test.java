package controler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.swing.tree.DefaultMutableTreeNode;

import view.GUI;
import view.MP3Node;

import model.MP3File;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*//the first MP3file
		MP3File file1 = new MP3File("titel1", "interpret1", "album1", "jahr1");
		MemoryCacheImageInputStream stream = new MemoryCacheImageInputStream(new ByteArrayInputStream(TestImages.jpeg));
		
		BufferedImage image1 = null;
		try {
			image1 = ImageIO.read(stream);
		}catch (IOException e) {
			e.printStackTrace();
		}
		if(image1 != null) {
			file1.setCover(image1);
		}
		//the second MP3file initialisation
		MP3File file2 = new MP3File();
		stream = new MemoryCacheImageInputStream(new ByteArrayInputStream(TestImages.png));
		BufferedImage image2 = null;
		try {
			image2 = ImageIO.read(stream);
		}catch (IOException e) {
			e.printStackTrace();
		}
		if(image2 != null) {
			file2.setCover(image2);
		}
		*/
		GUI tmp = new GUI();

		tmp.setGUI();
	}
		

}
