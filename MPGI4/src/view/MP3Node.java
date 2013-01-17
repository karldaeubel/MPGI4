package view;

import java.io.File;
import java.nio.file.Path;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import model.MP3File;

/**
 * A class to hold the leaf in the tree that is a MP3File
 */
public class MP3Node extends DefaultMutableTreeNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//The MP3 file to store
	MP3File mp3;
	Path p;
	/**
	 * Default constructor
	 * @param file, 
	 */
	public MP3Node(MP3File file) {
		super();
		mp3 = file;
	}
	
	/**
	 * Default constructor
	 * @param file, the MP3file to store in the leaf
	 * @param obj, the Object to send it to super()
	 */
	public MP3Node(MP3File file, Path myPath, Object obj) {
		super(obj);
		p = myPath;
		mp3 = file;
	}
	
	public Element getMp3Information(Document doc) {
		Element file = doc.createElement("file");
		file.setAttribute("name", p.getFileName().toString());
		file.setAttribute("size", "" + p.toFile().length());
		
		Element tags = doc.createElement("tags");
		
		
		Element text = doc.createElement("text");
		text.setAttribute("encoding", "UTF-16LE");
		
		Element title = doc.createElement("title");
		title.setAttribute("size" , "" + (mp3.getTitle().length() * 2));
		text.setTextContent(mp3.getTitle());
		title.appendChild(text);
		
		
		text = doc.createElement("text");
		text.setAttribute("encoding", "UTF-16LE");

		Element artist = doc.createElement("artist");
		artist.setAttribute("size", "" + (mp3.getInterpret().length() * 2));
		artist.setAttribute("frameid", "1");
		text.setTextContent(mp3.getInterpret());
		artist.appendChild(text);
		
		text = doc.createElement("text");
		text.setAttribute("encoding", "UTF-16LE");
		
		Element album = doc.createElement("album");
		album.setAttribute("size", "" + (mp3.getAlbum().length() * 2));
		text.setTextContent(mp3.getAlbum());
		album.appendChild(text);
		
		text = doc.createElement("text");
		text.setAttribute("encoding", "ISO1");
		
		Element year = doc.createElement("year");
		year.setAttribute("size", "" + mp3.getYear().length());
		text.setTextContent(mp3.getYear());
		year.appendChild(text);
		
		
		Element cover = doc.createElement("cover");
		cover.setAttribute("size", mp3.getCoverArray().length + "");
		
		Element mimetype = doc.createElement("mimetype");
		mimetype.setTextContent(mp3.getMimeType());
		
		Element pictype = doc.createElement("pictype");
		pictype.setTextContent("3");
		
		Element description = doc.createElement("description");
		description.setTextContent("");
		
		Element data = doc.createElement("data");
		data.setTextContent(Base64.encodeBase64String(mp3.getCoverArray()));
		
		file.appendChild(tags);
		tags.appendChild(title);
		tags.appendChild(artist);
		tags.appendChild(album);
		tags.appendChild(year);
		tags.appendChild(cover);
		
		cover.appendChild(mimetype);
		cover.appendChild(pictype);
		cover.appendChild(description);
		cover.appendChild(data);
		
		return file;
	}
	
	@Override
	public String toString() {
		return p.getFileName().toString();
	}
}
