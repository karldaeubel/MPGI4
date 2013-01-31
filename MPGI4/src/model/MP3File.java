package model;

import java.awt.image.BufferedImage;

/**
 * a class to hold all necessary informations of an MP3 file
 * @author MPGI
 */
public class MP3File {
	//some fields to store all needed informations as Strings
	private String title;
	private String interpret;
	private String album;
	private String year;
	//informations for the Picture
	private String mimeType;
	private BufferedImage cover;
	private byte[] coverArray;
	
	/**
	 * default construcor for "Unknown" identifier
	 */
	public MP3File() {
		title = interpret = album = year = "Unbekannt";
	}
	
	/**
	 * a constructor to initialize the MP3File Object
	 * @param title the title of the song
	 * @param interpret the interpret of the song
	 * @param album the album the song is on
	 * @param year the year the song is published
	 */
	public MP3File(String title, String interpret, String album, String year) {
		this.title = title;
		this.interpret = interpret;
		this.album = album;
		this.year = year;
	}
	
	/**
	 * a contructor to initialize the MP3File Object with Picture
	 * @param title the title of the song
	 * @param interpret the interpret of the song
	 * @param album the album the song is on
	 * @param year the year the song is published
	 * @param image the Buffered Image to store the picture
	 */
	public MP3File(String title, String interpret, String album, String year, BufferedImage image) {
		this.title = title;
		this.interpret = interpret;
		this.album = album;
		this.year = year;
		this.cover = image;
	}

	//getters and setters
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
		return cover;
	}

	/**
	 * @param cover the cover to set
	 */
	public void setCover(BufferedImage cover) {
		this.cover = cover;
	}
	
	/**
	 * @return the coverArray
	 */
	public byte[] getCoverArray() {
		return coverArray;
	}

	/**
	 * @param coverArray the coverArray to set
	 */
	public void setCoverArray(byte[] coverArray) {
		this.coverArray = coverArray;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * the toString method whitch is caled when the Object have to be presented as a String
	 */
	public String toString() {
		return "Titel: " + title+ " Interpret: " + interpret + " Album: " + album + " Jahr: " + year + " Cover: " + (cover == null? false : true);
	}
}
