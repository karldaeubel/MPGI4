package xmlCache;






import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;



public class XMLCache {

	
	
	
    public static void writeToXmlFile (DefaultMutableTreeNode root, String file ){
		
    	DocumentBuilder builder;
    	
    	// DocumentBuilderFactory is a class to produce DOM object trees in XML documents with its applications
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();         // Obtain a new instance of a DocumentBuilderFactory.	
    	factory.setValidating(true);                                                   // validate while parsing
    	factory.setIgnoringElementContentWhitespace(true);                             // to eliminate whitespaces when parsing the xml document
    	factory.setIgnoringComments(true);                                             // to ignore comments when parsing the xml document
    	
    	
    	try {
    		
			builder = factory.newDocumentBuilder();                                    // obtain a new instance of a DOM Document object to build a DOM tree with.
			Document document = builder.newDocument();                                 // this document represents the entire XML document; Document extends from Node
    		Element xmlCache = document.createElement("XMLCache");                     // this element represents the xml file, extends from Node
    		
    		xmlCache.setAttribute("timestamp", new Long( System.currentTimeMillis()).toString());   	// write the time of creation of the cache file 
    		document.appendChild(xmlCache);
    		writeToXmlFile(root, document, xmlCache);
    		
    		TransformerFactory tFactory = TransformerFactory.newInstance();
    	    Transformer transformer = tFactory.newTransformer();
    	    transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "cache.dtd"); //doctype-system specifies the system identifier to be used in the document type declaration.
    	    transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //add additional whitespace when outputting the result tree
    	    transformer.setOutputProperty("indent", "2");
    	    
    	    
    	    //going to write to the file a number of times -- especially many short writes like a list of a thousand names or something like that
    	    //-- then using the BufferedWriter will be more efficient. The BufferedWriter will save up many of the little writes and send only large 
    	    //chunks of data to the FileWriter. Writing one large chunk to a file is more efficient than many small ones because each call to FileWriter.write()
    	    //involves a call to the operating system, and those are slow. 
    	    
    	    BufferedWriter output = new BufferedWriter(new FileWriter(file)); 
		    transformer.transform(new DOMSource(document), new StreamResult(output)); //Transform the XML Source to a Result.
    	
    	} catch (IOException e){
    	} catch (TransformerConfigurationException e) {
		} catch (TransformerException e) {
    	} catch (ParserConfigurationException e) {
    		System.err.println("Parser Configuration Error: " + e.getMessage());
			
   		}                                    	
	}
	
	public static void writeToXmlFile (DefaultMutableTreeNode node, Document document, Element element ){
		
		//An object that implements the Enumeration interface generates a series of elements, one at a time. 
		//Successive calls to the nextElement method return successive elements of the series. 
		
		Enumeration<DefaultMutableTreeNode> leafs = node.children(); //    Creates and returns a forward-order enumeration of this node's children.
		
		DefaultMutableTreeNode leafNode = null;
    	if (node.getUserObject() instanceof MP3File) {
    		Element file = ((MP3File) node.getUserObject()).getDataForXML(document);
            element.appendChild(file);

        } else {
            Element folder = document.createElement("folder");
            folder.setAttribute("name", node.getUserObject().toString());
            element.appendChild(folder);
            while(leafs.hasMoreElements()) {
            	leafNode = (DefaultMutableTreeNode) leafs.nextElement();
                writeToXmlFile(leafNode, document, folder);
            }
        }
	}
	
	public static boolean readFromXmlFile(DefaultMutableTreeNode root,String file,  File baseDir) {

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

	private static void readFromXmlFile(DefaultMutableTreeNode rootNode,File baseDir, Node element, long changed) {

				
        MP3File mp3File;

        //  Tests whether the file denoted by this abstract pathname is a directory.
		if (baseDir.isDirectory()) {
			File newFile;
			DefaultMutableTreeNode newNode;
			
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
                    if (((Element)element.getChildNodes().item(j)).getAttribute("name").equals(newFile.getName())) {
                        element2 = (Element)element.getChildNodes().item(j);
                    }
                }

                if (newFile.isDirectory()) { 
                    newNode = new DefaultMutableTreeNode(newFile.getName(), true);
                   
                    //  go through sub directories
                    if (element2 == null) {
                        readFileTree(newNode, newFile);
                    } else {
                        readFromXmlFile(newNode, newFile, element2, changed);
                    }
                    rootNode.add(newNode);
                } else {
                    
                
                    if (element2 == null || newFile.lastModified() > changed) {
                        rootNode.add(new DefaultMutableTreeNode(mp3File = new MP3File(newFile),false)); 
                    } else {
                        rootNode.add(new DefaultMutableTreeNode(mp3File = new MP3File(element2, newFile),false));
                    }
    //ToDo mp3File erzeugen etwa mit  addMp3File(mp3File);
				}
			}
		
			if (i == 0) {
				rootNode.add(new DefaultMutableTreeNode("(empty directory)",true));
			}
		}
	}
	 public static void readFileTree(DefaultMutableTreeNode dirNode, File dir) {

	        File file;
	        DefaultMutableTreeNode newNode;
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
	                    dirNode.add(newNode = new DefaultMutableTreeNode(file.getName(), true));
	                    readFileTree(newNode, file);
	               
	                } else {
	                
	                    dirNode.add(new DefaultMutableTreeNode(mp3File = new MP3File(file),false));
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
