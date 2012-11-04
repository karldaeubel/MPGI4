package view;

import javax.swing.tree.DefaultMutableTreeNode;

import model.MP3File;

public class MP3Node extends DefaultMutableTreeNode{
	
	MP3File mp3;
	
	public MP3Node(MP3File file) {
		super();
		mp3 = file;
	}
	
	public MP3Node(MP3File file, Object obj) {
		super(obj);
		mp3 = file;
	}
}
