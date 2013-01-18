package xmlCache;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import model.MP3File;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;

import controler.MP3Parser;

import view.DirectoryNode;
import view.MP3Node;



public class XMLCache {

	
	
	
    public static void writeToXmlFile (DefaultMutableTreeNode root, String file ){
		
    	file += System.getProperty("file.separator") + "mp3cache.xml";
    	DocumentBuilder builder;
    	
    	// DocumentBuilderFactory is a class to produce DOM object trees in XML documents with its applications
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();         // Obtain a new instance of a DocumentBuilderFactory.	
    	factory.setValidating(true);                                                   // validate while parsing
    	factory.setIgnoringElementContentWhitespace(true);                             // to eliminate whitespaces when parsing the xml document
    	factory.setIgnoringComments(true);                                             // to ignore comments when parsing the xml document    	
    	
    	try {
    		
			builder = factory.newDocumentBuilder();                                    // obtain a new instance of a DOM Document object to build a DOM tree with.
			Document document = builder.newDocument();                                 // this document represents the entire XML document; Document extends from Node
    		Element xmlCache = document.createElement("cache");                     // this element represents the xml file, extends from Node
    		
    		xmlCache.setAttribute("timestamp", new Long( System.currentTimeMillis()).toString());   	// write the time of creation of the cache file 
    		document.appendChild(xmlCache);
    		writeToXmlFile(root, document, xmlCache);
    		
    		TransformerFactory tFactory = TransformerFactory.newInstance();
    		tFactory.setAttribute("indent-number", new Integer(2));
    	    Transformer transformer = tFactory.newTransformer();
    	    
    	    File temp = new File("./Content/cache.dtd");
    	    temp = temp.getAbsoluteFile();
    	    String s = temp.getCanonicalPath();
    	    transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, s); //doctype-system specifies the system identifier to be used in the document type declaration.
    	    transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //add additional whitespace when outputting the result tree
    	    //transformer.setOutputProperty("indent-number", "2");
    	    
    	    
    	    //going to write to the file a number of times -- especially many short writes like a list of a thousand names or something like that
    	    //-- then using the BufferedWriter will be more efficient. The BufferedWriter will save up many of the little writes and send only large 
    	    //chunks of data to the FileWriter. Writing one large chunk to a file is more efficient than many small ones because each call to FileWriter.write()
    	    //involves a call to the operating system, and those are slow. 
    	    
    	    BufferedWriter output = new BufferedWriter(new FileWriter(file)); 
		    transformer.transform(new DOMSource(document), new StreamResult(output)); //Transform the XML Source to a Result.
    	
    	} catch (IOException e){
    		System.err.println("IO-Exception" + e.getMessage());
    	} catch (TransformerConfigurationException e) {
    		System.err.println("TransF Error" + e.getMessage());
		} catch (TransformerException e) {
			System.err.println("Transformer Exception" + e.getMessage());
    	} catch (ParserConfigurationException e) {
    		System.err.println("Parser Configuration Error: " + e.getMessage());
			
   		}                                    	
	}

	public static void writeToXmlFile (DefaultMutableTreeNode node, Document document, Element element ){
		
		//An object that implements the Enumeration interface generates a series of elements, one at a time. 
		//Successive calls to the nextElement method return successive elements of the series. 
		
		
    	if (node instanceof MP3Node) {
    		Element file = getMp3Information(document, (MP3Node) node);
            element.appendChild(file);

        } else {
        	Enumeration<DefaultMutableTreeNode> leafs = node.children(); //    Creates and returns a forward-order enumeration of this node's children.
    		
    		DefaultMutableTreeNode leafNode;
    		
            Element folder = document.createElement("folder");
            folder.setAttribute("name", ((DirectoryNode) node).p.toString());
            element.appendChild(folder);
            while(leafs.hasMoreElements()) {
            	leafNode = leafs.nextElement();
                writeToXmlFile(leafNode, document, folder);
            }
        }
	}

	private static Element getMp3Information(Document doc, MP3Node node) {
		Element file = doc.createElement("file");
		file.setAttribute("name", node.p.toAbsolutePath().toString());
		file.setAttribute("size", "" + node.p.toFile().length());
		
		Element tags = doc.createElement("tags");
		
		
		Element text = doc.createElement("text");
		text.setAttribute("encoding", "UTF16LE");
		
		Element title = doc.createElement("title");
		title.setAttribute("size" , "" + (node.mp3.getTitle().length() * 2));
		text.setTextContent(node.mp3.getTitle());
		title.appendChild(text);
		
		
		text = doc.createElement("text");
		text.setAttribute("encoding", "UTF16LE");

		Element artist = doc.createElement("artist");
		artist.setAttribute("size", "" + (node.mp3.getInterpret().length() * 2));
		artist.setAttribute("frameid", "1");
		text.setTextContent(node.mp3.getInterpret());
		artist.appendChild(text);
		
		text = doc.createElement("text");
		text.setAttribute("encoding", "UTF16LE");
		
		Element album = doc.createElement("album");
		album.setAttribute("size", "" + (node.mp3.getAlbum().length() * 2));
		text.setTextContent(node.mp3.getAlbum());
		album.appendChild(text);
		
		text = doc.createElement("text");
		text.setAttribute("encoding", "UTF16LE");
		
		Element year = doc.createElement("year");
		year.setAttribute("size", "" + node.mp3.getYear().length());
		text.setTextContent(node.mp3.getYear());
		year.appendChild(text);
		
		
		Element cover = doc.createElement("cover");
		cover.setAttribute("size", node.mp3.getCoverArray().length + "");
		
		Element mimetype = doc.createElement("mimetype");
		mimetype.setTextContent(node.mp3.getMimeType());
		
		Element pictype = doc.createElement("pictype");
		pictype.setTextContent("3");
		
		Element description = doc.createElement("description");
		description.setAttribute("encoding", "UTF16LE");
		description.setTextContent("dont use!!");
		
		Element data = doc.createElement("data");
		data.setTextContent(Base64.encodeBase64String(node.mp3.getCoverArray()));
		
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
	
	public static boolean readFromXmlFile(DirectoryNode root,String file,  File baseDir) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setIgnoringComments(true);
		DocumentBuilder builder;
	
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(file));
			Element xmlCache = document.getDocumentElement();
			long changed = Long.parseLong(xmlCache.getAttribute("timestamp"));
			readFromXmlFile(root, baseDir, xmlCache.getElementsByTagName("folder").item(0), changed);
			return true;
		} catch (ParserConfigurationException e) {
            System.out.println("parser: " + e.getMessage());
			return false;
		} catch (SAXException e) {
            System.out.println("sax: " + e.getMessage());
			return false;
		} catch (IOException e) {
            System.out.println("io: " + e.getMessage());
			return false;
		}
	}

	private static void readFromXmlFile(DirectoryNode rootNode,File baseDir, Node element, long changed) {

		//  Tests whether the file denoted by this abstract pathname is a directory.
		if (baseDir.isDirectory()) {
			File newFile;
			
			//Instances of classes that implement this interface are used to filter filenames.
		
			FilenameFilter filter = new FilenameFilter() {
				
				//method of the interface filenamefilter
				// Tests if a specified file should be included in a file list.
				public boolean accept(File dir, String name) {
					return (new File(dir, name)).isDirectory() || name.endsWith(".mp3");
				}
			};

			// iterate  all files and sub-directories
			//.list(filter) --> Returns an array of strings naming the files and directories in the directory denoted by this abstract pathname that satisfy the specified filter.
			int i = 0;
			for (String children : baseDir.list(filter)) {
				i++;

                newFile = new File(baseDir, children);

                Element element2 = null;
                for (int j = 0; j < element.getChildNodes().getLength(); j++) {
                	System.out.println(((Element)element.getChildNodes().item(j)).getAttribute("name") + " " + newFile.getAbsolutePath());
                    if (((Element)element.getChildNodes().item(j)).getAttribute("name").equals(newFile.getAbsolutePath())) {
                        element2 = (Element)element.getChildNodes().item(j);
                    }
                }

                if (newFile.isDirectory()) { 
                	
                    DirectoryNode newNode = new DirectoryNode(newFile.toPath());
                   
                    //  go through sub directories
                    if (element2 == null) {
                        readFileTree(newNode, newFile);
                    } else {
                        readFromXmlFile(newNode, newFile, element2, changed);
                    }
                    rootNode.add(newNode);
                } else {
                    
                
                    if (element2 == null || newFile.lastModified() > changed) {
                    	//TODO ich habs geändert karl
                        //rootNode.add(new DefaultMutableTreeNode(mp3File = new MP3File(newFile),false));
                    	MP3Parser p = new MP3Parser(newFile.toPath());
                    	
                    	rootNode.add(new MP3Node(p.parseMP3(), newFile.toPath(), newFile.toPath()));
                    } else {
                    	//TODO ich habs geändert karl
                        //rootNode.add(new DefaultMutableTreeNode(mp3File = new MP3File(element2, newFile),false));
                    	rootNode.add(setMp3Information(element2, newFile.toPath()));
                    }
    //ToDo mp3File erzeugen etwa mit  addMp3File(mp3File);
				}
			}
		
			// add node for empty directory
			if (i == 0) {
				rootNode.add(new DefaultMutableTreeNode("(empty directory)",true));
			}
		}
	}
	
	private static MP3Node setMp3Information(Element element, Path myPath) {
		MP3File file = new MP3File();
		
		Node temp = element.getElementsByTagName("title").item(0); // the tags
		file.setTitle(getContent(temp));
		
		temp = element.getElementsByTagName("artist").item(0);
		file.setInterpret(getContent(temp));
		
		temp = element.getElementsByTagName("album").item(0);
		file.setAlbum(getContent(temp));
		
		temp = element.getElementsByTagName("year").item(0);
		file.setYear(getContent(temp));
		
		temp = element.getElementsByTagName("cover").item(0);
		NodeList nl = temp.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++) {
			if(nl.item(i).getNodeName().equalsIgnoreCase("mimetype")) {
				file.setMimeType(nl.item(i).getTextContent());
			}else if(nl.item(i).getNodeName().equalsIgnoreCase("data")) {
				file.setCoverArray(Base64.decodeBase64(nl.item(i).getTextContent()));
				InputStream in = new ByteArrayInputStream(file.getCoverArray());
				try {
					file.setCover(ImageIO.read(in));
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return new MP3Node(file, myPath, myPath);
	}
	
	private static String getContent(Node n) {
		Node text = n.getFirstChild();
		return text.getTextContent();
	}
	
	public static void readFileTree(DefaultMutableTreeNode dirNode, File dir) {

	        File file;
	        MP3File mp3File;

	        if (dir.isDirectory()) {
	            // filter all files with an ending  .mp3
	            FilenameFilter ff = new FilenameFilter() {
	                public boolean accept(File dir, String name) {
	                    return (new File(dir, name)).isDirectory() || name.endsWith(".mp3");
	                }
	            };

	            // iterate 
	            int i = 0;
	            for (String child: dir.list(ff)) {
	                i++;

	                file = new File(dir, child);
	                if (file.isDirectory()) {   
	                	// go through sub directories
	                	DirectoryNode newNode = new DirectoryNode(file.toPath());
	                    dirNode.add(newNode);
	                    readFileTree(newNode, file);
	               
	                } else {
	                
	                	//TODO ich habs geändert karl...
	                    //dirNode.add(new DefaultMutableTreeNode(mp3File = new MP3File(file),false));
	                	MP3Parser p = new MP3Parser(file.toPath());
	                	mp3File = p.parseMP3();
	                	dirNode.add(new MP3Node(mp3File, file.toPath(), file.toPath()));
	 //ToDo mp3File erzeugen etwa mit  addMp3File(mp3File);            
	                }
	            }
	            // add node for empty directory
	            if (i == 0) {
	                dirNode.add(new DefaultMutableTreeNode("(empty directory)", true));
	            }
	        }
	    }

}
