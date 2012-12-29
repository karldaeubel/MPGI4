package controler;




import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import model.MP3File;

public class MP3Parser {
    
	public byte header[];
	public Path file;
	
	LinkedList<byte[]> frames;
	
	RandomAccessFile f;
	
	MP3File mp3f;
	
	public MP3Parser(Path p, MP3File MP3file) {
		file = p;
		mp3f = MP3file;
	}
	
	public MP3Parser(Path f) {
		this.file = f;
	}
	
	/**
	 * 
	 */
	public void writeMP3() {
		try {
			f = new RandomAccessFile(file.toFile(), "rw");
			f.seek(0);
			header = new byte[10];
			f.read(header, 0, 10);
			
			int tagSize = 0;
			tagSize = tagSize | (header[6] << 21) | (header[7] << 14) | (header[8] << 7) | (header[9]);
			tagSize += 10;
			
			frames = new LinkedList<byte[]>();
			frames.add(header);
			int offset = 10;
			while(offset < tagSize ) {
				getFrames(offset);
				offset += frames.getLast().length;
				f.seek(offset);
				if(!Character.isLetterOrDigit(f.readByte()) || !Character.isLetterOrDigit(f.readByte()) || !Character.isLetterOrDigit(f.readByte()) || !Character.isLetterOrDigit(f.readByte())) {
					break;
				}
			}
			/*000e dcba
			 * a TALB was visited
			 * b TPE1 was visited
			 * ...
			 * e APIC was visited
			 */
			int flag = 0;
			for(int i = 1; i < frames.size(); i++) {
				byte[] copy = frames.get(i);
				String info = new String(copy, 0, 4, "ISO-8859-1");
				if(info.equals("TALB")) {//Album
					flag = flag | (1 << 0);
					if(mp3f.getAlbum() == null || mp3f.getAlbum().equals("")) {
						frames.remove(i);
						i--;
						continue;
					}
					frames.set(i, createNewFrame(mp3f.getAlbum(), frames.get(i)));
				}else if(info.equals("TPE1")) {//Interpret!
					flag = flag | (1 << 1);
					if(mp3f.getInterpret() == null || mp3f.getInterpret().equals("")) {
						frames.remove(i);
						i--;
						continue;
					}
					frames.set(i, createNewFrame(mp3f.getInterpret(), frames.get(i)));
				}else if(info.equals("TIT2")) {//Title
					flag = flag | (1 << 2);
					if(mp3f.getTitle() == null || mp3f.getTitle().equals("")) {//
						frames.remove(i);
						i--;
						continue;
					}
					frames.set(i, createNewFrame(mp3f.getTitle(), frames.get(i)));
				}else if(info.equals("TYER")) {//Year
					flag = flag | (1 << 3);
					if(mp3f.getYear() == null || mp3f.getYear().equals("")) {
						frames.remove(i);
						i--;
						continue;
					}
					frames.set(i, createNewFrame(mp3f.getYear(), frames.get(i)));
				}else if(info.equals("APIC")) {
					flag = flag | (1 << 4);
					if(mp3f.getCover() == null) {
						frames.remove(i);
						i--;
						continue;
					}
					byte[] pic = mp3f.getCoverArray();
					byte[] retval = new byte[10 + 1 + 10 + 1 + 1 + 1 + pic.length];
					int length = 1 + 10 + 1 + 1 + 1 + pic.length;
					retval[0] = copy[0];
					retval[1] = copy[1];
					retval[2] = copy[2];
					retval[3] = copy[3];
					
					retval[4] = (byte)(length >> 3*8);
					retval[5] = (byte)(length >> 2*8);
					retval[6] = (byte)(length >> 1*8);
					retval[7] = (byte)(length);
					
					retval[10] = 0;
					
					//TODO genaues Format einlesen!!!!
					String temp = "image/jpeg";
					for(int j = 0; j < temp.length(); j++) {
						retval[11 +j] = temp.getBytes()[j];
					}
					retval[21] = 0;
					retval[22] = 3;
					retval[23] = 0;
					for(int j = 0; j < pic.length; j++) {
						retval[24 +j] = pic[j];
					}
					frames.set(i, retval);
				}
			}
			if(flag != 31) {//not all relevant frames are set!
				for(int j = 0; j < 4; j++) {
					String val = "";
					byte[] text = null;
					switch(j) {
						case 0:
							if((flag & (1 << j)) == 0) {
								val = "TALB";
								text = mp3f.getAlbum().getBytes("UTF-16LE");
							}
							break;
						case 1:
							if((flag & (1 << j)) == 0) {
								val = "TPE1";
								text = mp3f.getInterpret().getBytes("UTF-16LE");
							}
							break;
						case 2:
							if((flag & (1 << j)) == 0) {
								val = "TIT2";
								text = mp3f.getTitle().getBytes("UTF-16LE");
							}
							break;
						case 3:
							if((flag & (1 << j)) == 0) {
								val = "TYER";
								text = mp3f.getYear().getBytes("UTF-16LE");
							}
							break;
					}
					if(text == null || text.length == 0 || val.equals("")) {
						continue;
					}
					int length = text.length +1 +2;
					byte[] retval = new byte[length + 10];
					retval[0] = val.getBytes()[0];
					retval[1] = val.getBytes()[1];
					retval[2] = val.getBytes()[2];
					retval[3] = val.getBytes()[3];
					
					retval[4] = (byte)(length >> 3*8);
					retval[5] = (byte)(length >> 2*8);
					retval[6] = (byte)(length >> 1*8);
					retval[7] = (byte)(length);
					
					retval[10] = 1;
					retval[11] = (byte) 0xFF;
					retval[12] = (byte) 0xFE;
					
					for(int k = 13; k < retval.length; k++) {
						retval[k] = text[k -13];
					}
					frames.add(retval);
				}
				if((flag & (1 << 4)) == 0) {//APIC
					if(mp3f.getCover() != null) {

						byte[] pic = mp3f.getCoverArray();
						byte[] retval = new byte[10 + 1 + 10 + 1 + 1 + 1 + pic.length];
						int length = 1 + 10 + 1 + 1 + 1 + pic.length;
						retval[0] = 'A';
						retval[1] = 'P';
						retval[2] = 'I';
						retval[3] = 'C';
						
						retval[4] = (byte)(length >> 3*8);
						retval[5] = (byte)(length >> 2*8);
						retval[6] = (byte)(length >> 1*8);
						retval[7] = (byte)(length);
						
						retval[10] = 0;
						
						//TODO genaues Format einlesen!!!!
						String temp = "image/jpeg";
						for(int j = 0; j < temp.length(); j++) {
							retval[11 +j] = temp.getBytes()[j];
						}
						retval[21] = 0;
						retval[22] = 3;
						retval[23] = 0;
						for(int j = 0; j < pic.length; j++) {
							retval[24 +j] = pic[j];
						}
						frames.add(retval);
					}
				}
			}
			int newTagSize = 0;
			for(int i = 0; i < frames.size(); i++) {
				newTagSize += frames.get(i).length;
			}
			if(tagSize >= newTagSize) {//only overwrite everything
				int off = 0;
				for(int i = 0; i < frames.size(); i++) {
					f.seek(off);
					f.write(frames.get(i));
					off += frames.get(i).length;
				}
				for(int i = off; i < tagSize; i++) {
					f.seek(off);
					f.writeByte(0);
					off++;
				}
			}else {
				byte[] music = new byte[(int)f.length() - tagSize];
				f.seek(tagSize);
				f.read(music);
				f.setLength(newTagSize + music.length);
				byte[] newHeader = new byte[10];
				newTagSize -= 10;
				for(int i = 0; i < 4; i++) {
					newHeader[9 -i] = (byte)(newTagSize >> (i*8));
				}
				for(int i = 3; i > 0; i--) {
					newHeader[9 -i] <<= i;
					newHeader[9 -i] |= (byte) ((newHeader[9 -i +1] & (~((1<<(7 -i +1))-1))) >> (7 -i+1));
					newHeader[9 -i] &= (1 << 7)-1;
				}
				newHeader[9] &= (1<<7)-1;
				for(int i = 0; i < 6; i++) {
					newHeader[i] = header[i];
				}
				frames.set(0, newHeader);
				int off = 0;
				for(int i = 0; i < frames.size(); i++) {
					f.seek(off);
					f.write(frames.get(i));
					off +=frames.get(i).length;
				}
				for(int i = off; i < newTagSize +10; i++) {
					f.seek(i);
					f.writeByte(0);
				}
				f.seek(newTagSize +10);
				f.write(music);
			}
		} catch (IOException e) {
			System.err.println(e.toString());
		}finally {
			try {
				f.close();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private byte[] createNewFrame(String info, byte[] copy) {
		int bom = 0;
		String charset = "";
		if(copy[10] == 0) {
			bom = 0;
			charset = "ISO-8859-1";
		}else if(copy[10] == 1) {
			//UNICODE
			bom = 2;
			if((copy[11] & 0xFF) == 0xFF && (copy[12] & 0xFF) == 0xFE) {//LE
				charset = "UTF-16LE";
			}else if((copy[11] & 0xFF) == 0xFE && (copy[12] & 0xFF) == 0xFF) {//BE
				charset = "UTF-16BE";
			}
		}
		byte[] text = null;
		try {
			text = info.getBytes(charset);
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] retval = new byte[10 + 1 + bom + text.length];
		for(int j = 0; j < 4; j++) {//the TALB String
			retval[j] = copy[j];
		}
		int length = text.length +1 +bom;
		
		//length of the frame without the header(10 bytes)
		retval[4] = (byte)(length >> 3*8);
		retval[5] = (byte)(length >> 2*8);
		retval[6] = (byte)(length >> 1*8);
		retval[7] = (byte)(length);
		
		for(int j = 10; j < 11 + bom; j++) {
			//1 for UNICODE 0 for ISO
			//FF FE for LE or FE FF for BE
			retval[j] = copy[j];
		}
		for(int j = 11+bom; j < retval.length; j++) {
			retval[j] = text[j -11-bom];
		}
		return retval;
	}
	
	/**
	 * 
	 * @return the MP3FIle to read from
	 */
	public MP3File parseMP3() {
		mp3f = null;
		try {
			f = new RandomAccessFile(file.toFile(), "r");
			f.seek(0);
			byte header[] = new byte[10];
			f.read(header, 0, 10);
			if(((char)header[0]) != 'I' && ((char)header[1]) != 'D' && ((char)header[2]) != '3') {
				//not a valid MP3-Tag!
				return null;
			}
			if(header[3] != 3 || header[4] != 0) {
				//not the valid version!
				return null;
			}
			if((header[5] & (1 << 6)) == 1) {
				//extended header flags are set -> ignore!
				return null;
			}
			
			int tagSize = 0;
			tagSize = tagSize | (header[6] << 21) | (header[7] << 14) | (header[8] << 7) | (header[9]);
			tagSize += 10;

			frames = new LinkedList<byte[]>();
			int offset = 10;
			while(offset < tagSize ) {
				getFrames(offset);
				offset += frames.getLast().length;
				f.seek(offset);
				if(!Character.isLetterOrDigit(f.readByte()) || !Character.isLetterOrDigit(f.readByte()) || !Character.isLetterOrDigit(f.readByte()) || !Character.isLetterOrDigit(f.readByte())) {
					break;
				}
			}

			mp3f = new MP3File();
			for(int i = 0; i < frames.size(); i++) {
				String val = "" + (char)frames.get(i)[0] + (char)frames.get(i)[1] + (char)frames.get(i)[2] + (char)frames.get(i)[3];
				if(val.equalsIgnoreCase("TALB")) {
					String retval = getVal(frames.get(i));
					mp3f.setAlbum(retval);
				}else if(val.equalsIgnoreCase("TPE1")) {
					String retval = getVal(frames.get(i));
					mp3f.setInterpret(retval);
				}else if(val.equalsIgnoreCase("TIT2")) {
					String retval = getVal(frames.get(i));
					mp3f.setTitle(retval);
				}else if(val.equalsIgnoreCase("TYER")) {
					String retval = getVal(frames.get(i));
					mp3f.setYear(retval);
				}else if(val.equalsIgnoreCase("APIC")) {
					byte copy[] = frames.get(i);
					int pointer = 11;
					
					if(copy[10] == 0) {
						//ASCII
						String mime = "";
						while(copy[pointer] != 0) {
							mime += (char)copy[pointer++];
						}
						if(copy[++pointer] != 3) {
							//kein Frontcover!
							continue;
						}
						while(copy[++pointer] != 0) {//Description NULL-Terminated
							
						}
						
						byte picture[] = new byte[copy.length - pointer -1];
						for(int k = 0; k < picture.length; k++) {
							picture[k] = copy[k + pointer +1];
						}
						mp3f.setCoverArray(picture);
						if(mime.equalsIgnoreCase("image/jpeg")|| mime.equals("image/png")) {
							
							MemoryCacheImageInputStream stream = new MemoryCacheImageInputStream(new ByteArrayInputStream(picture));
							
							BufferedImage image1 = null;
							try {
								image1 = ImageIO.read(stream);
							}catch (IOException e) {
								System.err.println(e.toString() + "bild konnte nicht geladen werden");
								e.printStackTrace();
							}
							if(image1 != null) {
								mp3f.setCover(image1);
							}
						}
					}else if(copy[10] == 1) {
						//UNICODE
						String mime = "";
						pointer = 13;
						if((copy[11] & 0xFF) == 0xFF && (copy[12] & 0xFF) == 0xFE) {
							while(copy[pointer]!= 0 && copy[pointer +1] != 0) {
								mime += (char)(copy[pointer +1] << 8 | copy[pointer]);
								pointer += 2;
							}
						}else if((copy[11] & 0xFF) == 0xFE && (copy[12] & 0xFF) == 0xFF) {
							while(copy[pointer]!= 0 && copy[pointer +1] != 0) {
								mime += (char)(copy[pointer] << 8 | copy[pointer +1]);
								pointer += 2;
							}
						}
						pointer += 2;
						if(copy[pointer] != 3) {
							//kein Frontcover!
							continue;
						}
						while(copy[pointer] != 0 && copy[pointer +1] != 0) {//Description NULL-Terminated
							pointer += 2;
						}
						pointer +=2;
						
						byte picture[] = new byte[copy.length - pointer -1];
						for(int k = 0; k < picture.length; k++) {
							picture[k] = copy[k + pointer +1];
						}
						mp3f.setCoverArray(picture);
						if(mime.equalsIgnoreCase("image/jpeg")|| mime.equalsIgnoreCase("image/png")) {
							
							MemoryCacheImageInputStream stream = new MemoryCacheImageInputStream(new ByteArrayInputStream(picture));
							
							BufferedImage image1 = null;
							try {
								image1 = ImageIO.read(stream);
							}catch (IOException e) {
								System.err.println(e.toString() + "unicode picture konnte nicht geladen werden");
								e.printStackTrace();
							}
							if(image1 != null) {
								mp3f.setCover(image1);
							}
						}
					}
				}
			}
		}catch (FileNotFoundException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}finally {
			try {
				f.close();
			}catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return mp3f;
	}
	
	private String getVal(byte copy[]) {
		String retval = "";
		if(copy[10] == 0) {
			//ASCII
			try {
				retval = new String(copy, 11, copy.length -11, "ISO-8859-1");
			}catch (UnsupportedEncodingException e) {
				System.out.println("Falsche ISO-Eingabe");
				e.printStackTrace();
			}
		}else if(copy[10] == 1) {
			//UNICODE
			if((copy[11] & 0xFF) == 0xFF && (copy[12] & 0xFF) == 0xFE) {
				try {
					retval = new String(copy, 13, copy.length -13, "UTF-16LE");
				}catch (UnsupportedEncodingException e) {
					System.out.println("Falsche UTF-16-Eingabe");
					e.printStackTrace();
				}
			}else if((copy[11] & 0xFF) == 0xFE && (copy[12] & 0xFF) == 0xFF) {
				
				try {
					retval = new String(copy, 13, copy.length -13, "UTF-16BE");
				}catch (UnsupportedEncodingException e) {
					System.out.println("Falsche UTF-16-Eingabe");
					e.printStackTrace();
				}
			}
		}
		return retval;
	}
	
	private void getFrames(int off) {
		byte[] save = null;
		
		long length = 10;
		try {
			f.seek(off);
			f.skipBytes(4);
			length += f.readInt();
			save = new byte[(int)length];
			f.seek(off);
			f.read(save);
			frames.add(save);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Element getDataForXML(Document document) {
		
		Element file = document.createElement("file");
        Element tags = document.createElement("tags");
        file.appendChild(tags);

        for(int i = 0; i < frames.size(); i++) {
            Element tag;
            String val = "" + (char)frames.get(i)[0] + (char)frames.get(i)[1] + (char)frames.get(i)[2] + (char)frames.get(i)[3]; //von oben ¸bernommen
			if(val.equalsIgnoreCase("APIC")) {//Frontcover
						
				// cover (mimetype,pictype,description,data)
                tag = document.createElement("cover");         
                Element mimeType = document.createElement("mimetype");
        //ToDo  mimeType.setTextContent(new String(?));  'APIC-1'--> Name des entsprechenden Tags, aber	wo kriege ich das her?

                Element picType  = document.createElement("pictype"); 
                byte[] picTypeContent = new byte[1];
        //ToDo  picTypeContent[0] = ?;  'APIC-2' 	
                picType.setTextContent(Base64.encodeBase64String(picTypeContent));

                Element description = document.createElement("description");
        //ToDo  description.setTextContent(); //'APIC-3' 	
                
                Element data = document.createElement("data");
        //ToDo  data.setTextContent(Base64.encodeBase64String(getImageData ???));
                
             // Adds the node newChild to the end of the list of children of this node
                tag.appendChild(mimeType);
                tag.appendChild(picType);
                tag.appendChild(description);
                tag.appendChild(data);
                
                
         	}else if(val.equalsIgnoreCase("TIT2")) {
         	    tag = document.createElement("title");	
         	}else if(val.equalsIgnoreCase("TPE1")) {
         	    tag = document.createElement("artist");		
         	}else if(val.equalsIgnoreCase("TALB")) {
         	    tag = document.createElement("album");		
         	}else if(val.equalsIgnoreCase("TYER")) {
         	    tag = document.createElement("year");	
         	} else {
                tag = document.createElement("ignoredtag");
                byte[] data = header;
                tag.setTextContent(Base64.encodeBase64String(data));
                tag.setAttribute("frames", new String(data));

           
                
             }
            if (!tag.getTagName().equals("ignoredtag")) {
                 Element text = document.createElement("text");
         //ToDo  text.setTextContent(?); --> ich weiﬂ noch nich, was darein kommt
                 tag.appendChild(text);
            }
            tags.appendChild(tag);
        }

        return file;
		
		
	}
	
	// create data from xml file
	public MP3Parser(Element xmlElement, File file) {

  //      try {
            NodeList tags = xmlElement.getElementsByTagName("tags").item(0).getChildNodes();
            for (int i = 0; i < tags.getLength(); i++) {
                Element tag = (Element) tags.item(i);

//                <!ELEMENT title (text)>
//                <!ELEMENT artist (text)>
//                <!ELEMENT album (text)>
//                <!ELEMENT year (text)>
//                <!ELEMENT cover (mimetype,pictype,description,data)>
//                <!ELEMENT ignoredtag (#PCDATA)>

                
        /*ToDo wie kann diese Daten in den Header schreiben?
                String name = "";
                if (tag.getTagName().equals("title")) {
                    Header = new Header(name = "TIT2");
                } else if (tag.getTagName().equals("artist")) {
                    Header = new Header(name = "TPE1");
                } else if (tag.getTagName().equals("album")) {
                    Header = new Header(name = "TALB");
                } else if (tag.getTagName().equals("year")) {
                    Header = new Header(name = "TYER");
                } else if (tag.getTagName().equals("cover")) {
                    Header = new Header("APIC");
                } else if (tag.getTagName().equals("ignoredtag")) {
                    Header = new Header(tag.getAttribute("frameid"));
                } else {
                    continue;
                }
        */
              
        /* ToDo hier will ich den header zu dem frame array hinzuf¸gen
                if (tag.getTagName().equals("ignoredtag")) {
                    frames = new Frame(Base64.decodeBase64(tag.getTextContent()), 0, frameHeader);
                } else if (tag.getTagName().equals("cover")) {
                    frames = new FramePicture(Base64.decodeBase64(tag.getElementsByTagName("data").item(0).getTextContent()),
                     tag.getElementsByTagName("description").item(0).getTextContent(),FramePicture.PictureType.fromByte(
                       Base64.decodeBase64(tag.getElementsByTagName("pictype").item(0).getTextContent())[0]),
                        tag.getElementsByTagName("mimetype").item(0).getTextContent(),Header);
               
         
        
                } else {
                    frames = tag.getElementsByTagName("text").item(0).getTextContent(),frameHeader);
                }
                this.frames.add(frame);
            }

        } catch (Exception e) {
            System.out.println("Invalid XML data for file ");
            System.out.println(e.getMessage());
            this.parseFile(file);           --> wie kann ich diese file parsen?
        }
    */
    
    }   
     
  }
}
