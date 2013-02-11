package tests;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.nio.file.Paths;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import model.MP3File;

import org.junit.Test;

import view.GUI;
import view.MP3Node;


/**
 * a class to test other methods
 * @author MPGI
 *
 */
public class TestGUI {
	
	@Test
	public void testCreateGUI() {
		GUI g = new GUI();
		
		assertNotNull(g.getFrame());
		assertTrue(g.getFrame().isVisible());
	}
	
	@Test
	public void testCloseButton() {
		GUI g = new GUI();
		
		assertNotNull(g.getClose());
		assertTrue(g.getClose().getActionListeners().length > 0);
		
		ActionEvent e = new ActionEvent(g.getClose(), ActionEvent.ACTION_FIRST +1, "0");
		g.getClose().getActionListeners()[0].actionPerformed(e);

		assertNull(g.getFrame());
		assertTrue(g.getChangedFiles().size() == 0);
	}
	
	@Test
	public void testSaveButton() {
		GUI g = new GUI();
		
		assertNotNull(g.getSave());
		assertTrue(g.getSave().getActionListeners().length > 0);
		
		MP3File f = new MP3File("title", "interpret", "album", "1900");
		MP3Node n = new MP3Node(f, Paths.get("/fakePath"), null);
		
		g.setCurrNode(n);
		
		g.getAlbumField().setText("TestAlbum");
		g.getInterpretField().setText("TestInterpret");
		g.getTitleField().setText("TestTitle");
		g.getYearField().setText("1999");
		
		int lenBefore = g.getChangedFiles().size();
		
		ActionEvent e = new ActionEvent(g.getSave(), ActionEvent.ACTION_FIRST +1, "0");
		g.getSave().getActionListeners()[0].actionPerformed(e);

		assertTrue(lenBefore +1 == g.getChangedFiles().size());
		assertTrue(g.getCurrNode().mp3.getAlbum().equals("TestAlbum"));
		assertTrue(g.getCurrNode().mp3.getInterpret().equals("TestInterpret"));
		assertTrue(g.getCurrNode().mp3.getTitle().equals("TestTitle"));
		assertTrue(g.getCurrNode().mp3.getYear().equals("1999"));
		
	}
	
	@Test
	public void testValidYear() {
		GUI g = new GUI();
		
		g.getYearField().setText("ab");
		assertFalse(g.isValidYear());
		
		g.getYearField().setText("rofl");
		assertFalse(g.isValidYear());
		
		g.getYearField().setText("1999");
		assertTrue(g.isValidYear());
		
		g.getYearField().setText("10230");
		assertFalse(g.isValidYear());
		
		g.getYearField().setText("-102");
		assertFalse(g.isValidYear());
	}
	
	@Test
	public void testSetTree() {
		MP3File file1 = new MP3File("titel1", "interpret1", "album1", "jahr1");
		
		//the second MP3file initialisation
		MP3File file2 = new MP3File();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("/root");
		
		DefaultMutableTreeNode folder1 = new DefaultMutableTreeNode("/folder1");
		MP3Node node1 = new MP3Node(file1, Paths.get("/mp3_1"), "/mp3_1");
		folder1.add(node1);

		DefaultMutableTreeNode folder2 = new DefaultMutableTreeNode("/folder2");
		MP3Node node2 = new MP3Node(file2, Paths.get("/mp3_2"), "/mp3_2");
		folder2.add(node2);
		
		root.add(folder1);
		root.add(folder2);
		
		GUI g = new GUI();
		
		assertNull(g.getTree());
		
		g.setTree(root);
		
		assertNotNull(g.getTree());
		assertTrue(g.getTitleField().getText().equals(""));
		assertTrue(g.getInterpretField().getText().equals(""));
		assertTrue(g.getAlbumField().getText().equals(""));
		assertTrue(g.getYearField().getText().equals(""));
		
		//test tree selection listener!
		TreePath p = new TreePath(new Object[] {root, folder1, node1});
		g.getTree().setSelectionPath(p);
		
		assertTrue(g.getTitleField().getText().equals("titel1"));
		assertTrue(g.getInterpretField().getText().equals("interpret1"));
		assertTrue(g.getAlbumField().getText().equals("album1"));
		assertTrue(g.getYearField().getText().equals("jahr1"));
		
		p = new TreePath(new Object[] {root, folder2, node2});
		g.getTree().setSelectionPath(p);
		
		assertTrue(g.getTitleField().getText().equals("Unbekannt"));
		assertTrue(g.getInterpretField().getText().equals("Unbekannt"));
		assertTrue(g.getAlbumField().getText().equals("Unbekannt"));
		assertTrue(g.getYearField().getText().equals("Unbekannt"));
		
		p = new TreePath(new Object[] {root});
		g.getTree().setSelectionPath(p);
		
		assertTrue(g.getTitleField().getText().equals("Unbekannt"));
		assertTrue(g.getInterpretField().getText().equals("Unbekannt"));
		assertTrue(g.getAlbumField().getText().equals("Unbekannt"));
		assertTrue(g.getYearField().getText().equals("Unbekannt"));
		
	}
}
