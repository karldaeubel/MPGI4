package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import view.DirectoryNode;
import view.MP3Node;

public class MyTree extends SimpleFileVisitor<Path>{
	
	public DirectoryNode root;
	
	public MyTree(Path path) {
		String temp[] = path.toString().split(File.separator);
		root = new DirectoryNode(path);
	}
	
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		Enumeration<DefaultMutableTreeNode> temp = root.breadthFirstEnumeration();
		while(temp.hasMoreElements()){
			DefaultMutableTreeNode t = temp.nextElement();
			if(dir.getParent().compareTo((Path) t.getUserObject()) == 0) {
				t.add(new DirectoryNode(dir));
				return FileVisitResult.CONTINUE;
			}
		}
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if(!file.toString().endsWith(".mp3")) {
			return FileVisitResult.CONTINUE;
		}
		Enumeration<DefaultMutableTreeNode> temp = root.breadthFirstEnumeration();
		while(temp.hasMoreElements()){
			DefaultMutableTreeNode t = temp.nextElement();
			if(file.getParent().compareTo((Path) t.getUserObject()) == 0) {
				//TODO: MP3 Parser schreiben, der dieses file einliest!!
				t.add(new MP3Node(new MP3File(), file));
				return FileVisitResult.CONTINUE;
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
