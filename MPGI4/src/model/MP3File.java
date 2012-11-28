package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * a class to hold all necessary informations of an MP3 file
 * @author MPGI
 */
public class MP3File {
	//some fields to store all needed informations
	private String title;
	private String interpret;
	private String album;
	private String year;
	
	private static BufferedImage defCover;
	private BufferedImage cover;
	
	public MP3File() {
		title = interpret = album = year = "Unbekannt";
		if(defCover == null) {
			try {
				defCover = ImageIO.read(new File("./Content/nofile.jpg"));
			}catch (IOException e) {
				System.out.println(e.toString());
				e.printStackTrace();
			}
		}
	}
	
	public MP3File(String title, String interpret, String album, String year) {
		this.title = title;
		this.interpret = interpret;
		this.album = album;
		this.year = year;
	}
	
	public MP3File(String title, String interpret, String album, String year, BufferedImage image) {
		this.title = title;
		this.interpret = interpret;
		this.album = album;
		this.year = year;
		this.cover = image;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the interpret
	 */
	public String getInterpret() {
		return interpret;
	}

	/**
	 * @param interpret the interpret to set
	 */
	public void setInterpret(String interpret) {
		this.interpret = interpret;
	}

	/**
	 * @return the album
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * @param album the album to set
	 */
	public void setAlbum(String album) {
		this.album = album;
	}

	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @return the cover
	 */
	public BufferedImage getCover() {
		if(cover == null) {
			return defCover;
		}
		return cover;
	}

	/**
	 * @param cover the cover to set
	 */
	public void setCover(BufferedImage cover) {
		this.cover = cover;
	}
}
