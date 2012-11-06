package controler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import resources.TestImages;
import view.GUI;
import view.ImageLabel;
import view.MP3Node;

import model.MP3File;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
		
		GUI tmp = new GUI();
		/*
		 * Hardcoded example of a Tree
		 */
		MP3Node node1 = new MP3Node(file1, "File1.mp3");//das steht nachher auch im Node
		MP3Node node2 = new MP3Node(file2, "File2.mp3");
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Musik");
		DefaultMutableTreeNode directory1 = new DefaultMutableTreeNode("Beispiel1");
		DefaultMutableTreeNode directory2 = new DefaultMutableTreeNode("Beispiel2");
		root.add(directory1);
		root.add(directory2);
		directory1.add(node1);
		directory2.add(node2);
		JTree t = new JTree(root);
		tmp.setTree(t);
		
		tmp.setGUI();
	}
		

}
