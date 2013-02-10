package tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import model.MP3File;

import org.junit.Test;

import controler.MP3Parser;

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
		
	}
}
