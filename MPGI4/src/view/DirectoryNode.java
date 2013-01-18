package view;

import java.nio.file.Path;

import javax.swing.tree.DefaultMutableTreeNode;

public class DirectoryNode extends DefaultMutableTreeNode{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
