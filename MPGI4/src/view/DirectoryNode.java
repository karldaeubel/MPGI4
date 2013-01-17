package view;

import java.io.File;
import java.nio.file.Path;

import javax.swing.tree.DefaultMutableTreeNode;

public class DirectoryNode extends DefaultMutableTreeNode{
	
	public Path p;
	
	public DirectoryNode(Path path) {
		super();
		p = path;
	}

	@Override
	public String toString() {
		return p.getFileName().toString();
	}
}
