package view;

import java.nio.file.Path;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * a class to repesent a directory in the filesystem tree
 * @author MPGI
 *
 */
public class DirectoryNode extends DefaultMutableTreeNode{

	private static final long serialVersionUID = 1L;
	
	/**
	 * the path the directory represents
	 */
	public Path p;
	
	/**
	 * a constructor to initialize the directory node
	 * @param path the path of the directory in the file system
	 */
	public DirectoryNode(Path path) {
		super();
		p = path;
	}

	@Override
	public String toString() {
		return p.getFileName().toString();
	}
}
