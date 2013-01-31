package controler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;

import model.MP3File;

/**
 * A class to read and write from and into a MP3 file in the MP3v2 format
 * @author karl
 *
 */
public class MP3Parser {
    
	//the header of the MP3 file
	public byte header[];
	public Path file;
	//all frames in one linked list
	LinkedList<byte[]> frames;
	
	RandomAccessFile f;
	
	MP3File mp3f;
	
	/**
	 * a constructor to create an instance of the MP3parser Obejct
	 * @param p the Path to the file to write to
	 * @param MP3file the MP3 file to read from
	 */
	public MP3Parser(Path p, MP3File MP3file) {
		file = p;
		mp3f = MP3file;
	}
	
	/**
	 * a constructor to create an instance of the MP3Parser Object
	 * @param f the path of the mp3 file to read from
	 */
	public MP3Parser(Path f) {
		this.file = f;
	}
	
	/**
	 * a method to write all informations contained in the 'mp3f' into the path 'p'
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
			/* int flag = 000e dcba; //(binär)
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
				}else if(info.equals("APIC")) {//Picture
					flag = flag | (1 << 4);
					if(mp3f.getCover() == null || mp3f.getCoverArray() == null) {
						frames.remove(i);
						i--;
						continue;
					}
					byte[] pic = mp3f.getCoverArray();
					String temp = mp3f.getMimeType();
					byte[] retval = new byte[10 + 1 + temp.length() + 1 + 1 + 1 + pic.length];
					int length = 1 + temp.length() + 1 + 1 + 1 + pic.length;
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
					
					for(int j = 0; j < temp.length(); j++) {
						retval[11 +j] = temp.getBytes()[j];
					}
					retval[11 + temp.length()] = 0;
					retval[12 + temp.length()] = 3;
					retval[13 + temp.length()] = 0;
					for(int j = 0; j < pic.length; j++) {
						retval[14 + temp.length() +j] = pic[j];
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
						String temp = "image/jpeg";
						byte[] retval = new byte[10 + 1 + temp.length() + 1 + 1 + 1 + pic.length];
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
						
						for(int j = 0; j < temp.length(); j++) {
							retval[11 +j] = temp.getBytes()[j];
						}
						retval[11 + temp.length()] = 0;
						retval[12 + temp.length()] = 3;
						retval[13 + temp.length()] = 0;
						for(int j = 0; j < pic.length; j++) {
							retval[14 +temp.length() +j] = pic[j];
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

	/**
	 * a method to create a byte array witch represents a completely 'TEXT' frame defined in the mp3v2 specification with ASCII or UNICODE (LE or BE)
	 * @param info the information to write into the frame
	 * @param copy the original byte array that should be changed
	 * @return a new byte array containing all nessesary informations
	 */
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
	 * a method to read all informations contained in the MP3 file and write it to the 'mp3f'
	 * @return the MP3File containing all needed informations
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
							mp3f.setMimeType(mime);
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
							mp3f.setMimeType(mime);
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
	
	/**
	 * a method to get the information from a 'TEXT' frame with the special encoding
	 * @param copy the frame containing the information
	 * @return the information stored in the frame
	 */
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
	
	/**
	 * a method to get one frames at a given offset in the file
	 * @param off the offset to start reading at
	 */
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
}
