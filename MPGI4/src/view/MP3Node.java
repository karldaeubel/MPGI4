package view;

import java.io.File;
import java.nio.file.Path;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import model.MP3File;

/**
 * A class to hold the leaf in the tree that is a MP3File
 */
public class MP3Node extends DefaultMutableTreeNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//The MP3 file to store
	public MP3File mp3;
	public Path p;
	/**
	 * Default constructor
	 * @param file, 
	 */
	public MP3Node(MP3File file) {
		super();
		mp3 = file;
	}
	
	/**
	 * Default constructor
	 * @param file, the MP3file to store in the leaf
	 * @param obj, the Object to send it to super()
	 */
	public MP3Node(MP3File file, Path myPath, Object obj) {
		super(obj);
		p = myPath;
		mp3 = file;
	}
	
	@Override
	public String toString() {
		return p.getFileName().toString();
	}
}
