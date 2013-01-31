package model;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import controler.MP3Parser;

import view.DirectoryNode;
import view.MP3Node;

/**
 * a class to traverse a file dircetory recursively
 * @author MPGI 4
 *
 */
public class MyTree extends SimpleFileVisitor<Path>{
	/**
	 * the rootnode of the directory tree;
	 */
	public DirectoryNode root;
		
	/**
	 * a constructor to initialize a tree
	 * @param path
	 * @param re
	 */
	public MyTree(Path path, boolean re) {
		root = new DirectoryNode(path);
	}
	
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		Enumeration<DefaultMutableTreeNode> temp = root.breadthFirstEnumeration();
		while(temp.hasMoreElements()){
			DefaultMutableTreeNode t = temp.nextElement();
			if(t instanceof DirectoryNode) {
				if(dir.getParent().compareTo( ((DirectoryNode) t).p) == 0) {
					t.add(new DirectoryNode(dir));
					return FileVisitResult.CONTINUE;
				}
			}
		}
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if(!file.toString().endsWith(".mp3")) {
			return FileVisitResult.CONTINUE;
		}
		MP3Parser parser = new MP3Parser(file);
		MP3File f = parser.parseMP3();
		if(f == null) return FileVisitResult.CONTINUE;
		
		Enumeration<DefaultMutableTreeNode> temp = root.breadthFirstEnumeration();
		while(temp.hasMoreElements()){
			DefaultMutableTreeNode t = temp.nextElement();
			if(t instanceof DirectoryNode) {
				if(file.getParent().compareTo(((DirectoryNode) t).p) == 0) {
					t.add(new MP3Node(f, file, file));
					return FileVisitResult.CONTINUE;
				}
			}
		}
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		System.err.println(exc.toString() + " in " + file.toString());
		return super.visitFileFailed(file, exc);
	}
}
