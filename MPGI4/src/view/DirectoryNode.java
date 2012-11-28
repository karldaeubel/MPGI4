package view;

import java.io.File;
import java.nio.file.Path;

import javax.swing.tree.DefaultMutableTreeNode;

public class DirectoryNode extends DefaultMutableTreeNode{
	
	public DirectoryNode(Object path) {
		super(path);
	}

	@Override
	public String toString() {
		String temp[] = this.getUserObject().toString().split(File.separator);
		return temp[temp.length -1];
	}
}
