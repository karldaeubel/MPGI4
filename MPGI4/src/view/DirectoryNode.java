package view;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

public class DirectoryNode extends DefaultMutableTreeNode{
	
	public DirectoryNode(Object path) {
		super(path);
	}

	@Override
	public String toString() {
		String temp[] = this.getUserObject().toString().split("\\" + File.separator);
		if(temp.length > 0) return temp[temp.length -1];
		return "/";
	}
}
