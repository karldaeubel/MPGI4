package tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import model.MP3File;

import org.junit.Test;

import view.DirectoryNode;
import view.MP3Node;

import controler.MP3Parser;
import controler.XMLCache;

public class TestControler {

	@Test
	public void testMP3Parser() {
		Path file = Paths.get("./Content/The Whind Whistles/Animals are people too/01_Turtle.mp3");
		MP3Parser p = new MP3Parser(file);
		MP3File f = p.parseMP3();
		assertNotNull(f);
		
		Path control = Paths.get("./Content/The Whind Whistles/Animals are people too/01_Turtle.mp3.properties");
		
		assertTrue(isValid(f, control));
		
		file = Paths.get("./Content/Bryyn/House Plants/01_What_i_hope.mp3");
		p = new MP3Parser(file);
		f = p.parseMP3();
		control = Paths.get("./Content/Bryyn/House Plants/01_What_i_hope.mp3.properties");
		
		assertTrue(isValid(f, control));
	}
	
	private boolean isValid(MP3File mp3, Path property) {
		BufferedReader r = null;
		try {
			r = Files.newBufferedReader(property, Charset.defaultCharset());
			
			String str = null;
			while((str = r.readLine()) != null) {
				if(str.startsWith("artist")) {
					if(!str.endsWith(mp3.getInterpret())) {
						return false;
					}
				}else if(str.startsWith("album")) {
					if(!str.endsWith(mp3.getAlbum())) {
						return false;
					}
				}else if(str.startsWith("year")) {
					if(!str.endsWith(mp3.getYear())) {
						return false;
					}
				}else if(str.startsWith("title")) {
					if(!str.endsWith(mp3.getTitle())) {
						return false;
					}
				}
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(r != null) {
				try {
					r.close();
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
	@Test
	public void testWriteMP3() {
		Random r = new Random();
		int i = r.nextInt();
		MP3File file = new MP3File("title" + i, "interpret" + i, "album" +i , "year" + i);
		Path path = Paths.get("./Content/Bryyn/House Plants/02_Quiet.mp3");
		MP3Parser p = new MP3Parser(path, file);
		
		p.writeMP3();
		
		MP3Parser p2 = new MP3Parser(path);
		MP3File f = p2.parseMP3();
		
		assertTrue(f.getAlbum().equals("album" +i));
		assertTrue(f.getInterpret().equals("interpret" +i));
		assertTrue(f.getTitle().equals("title" +i));
		assertTrue(f.getYear().equals("year" +i));
	}
	
	@Test
	public void testXML() {
		DirectoryNode root = new DirectoryNode(Paths.get("./Content/The Whind Whistles/Animals are people too"));
		
		MP3Node file = new MP3Node(new MP3File("title", "interpret", "album", "year"), Paths.get("./Content/The Whind Whistles/Animals are people too/01_Turtle.mp3"), null);
		root.add(file);
		
		XMLCache.writeToXmlFile(root, root.p.toString());
		assertTrue(new File("./Content/The Whind Whistles/Animals are people too/mp3cache.xml").exists());
		
		DirectoryNode d = new DirectoryNode(Paths.get("./Content/The Whind Whistles/Animals are people too"));
		XMLCache.readFromXmlFile(d,
				"./Content/The Whind Whistles/Animals are people too/mp3cache.xml", 
				new File("./Content/The Whind Whistles/Animals are people too"));
		
		
		assertTrue(d.getChildCount() == 1);
		
		MP3Node val = (MP3Node) d.getFirstChild();
		
		assertTrue(val.mp3.getAlbum().equals("album"));
		assertTrue(val.mp3.getInterpret().equals("interpret"));
		assertTrue(val.mp3.getTitle().equals("title"));
		assertTrue(val.mp3.getYear().equals("year"));
		assertTrue(val.mp3.getCover() == null);
		assertTrue(val.mp3.getCoverArray() == null);
		
		new File("./Content/The Whind Whistles/Animals are people too/mp3cache.xml").delete();
	}
}
