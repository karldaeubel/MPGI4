package view;

import java.io.File;
import java.nio.file.Path;

import javax.swing.tree.DefaultMutableTreeNode;

import model.MP3File;

/**
 * A class to hold the leaf in the tree that is a MP3File
 */
public class MP3Node extends DefaultMutableTreeNode{
	//The MP3 file to store
	MP3File mp3;
	Path p;
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
		String temp[] = this.getUserObject().toString().split("\\" + File.separator);
		return temp[temp.length -1];
	}
}
