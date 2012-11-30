package controler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;

import model.MP3File;

public class MP3Parser {

	public Path file;
	
	LinkedList<byte[]> frames;
	
	RandomAccessFile f;
	
	public static void main(String[] args) {
		String fs = File.separator;
		//Path file = Paths.get(fs + "home" + fs + "karl" + fs + "Desktop" + fs + "mp3s"+ fs + "Bryyn"+ fs + "House Plants" + fs + "01_What_i_hope.mp3");	
		Path file = Paths.get(fs + "home" + fs + "karl" + fs + "Desktop" + fs + "mp3s"+ fs + "Bryyn"+ fs + "House Plants" + fs + "02_Quiet.mp3");	
		
		//Path file = Paths.get(fs + "home" + fs + "karl" + fs + "Musik" + fs + "Green Day" + fs + "Dookie" + fs + "01 - Burnout.mp3");
		//MP3Parser p = new MP3Parser(file.toFile());
		MP3Parser p = new MP3Parser(file);
		p.parseMP3();
		//MP3Parser.parseMP3(file);
	}
	
	public MP3Parser(Path f) {
		this.file = f;
	}
	
	public MP3File parseMP3() {
		MP3File mp3f = null;
		try {
			f = new RandomAccessFile(file.toFile(), "r");
			f.seek(0);
			byte header[] = new byte[10];
			f.read(header, 0, 10);
			if(((char)header[0]) != 'I' && ((char)header[1]) != 'D' && ((char)header[2]) != '3') {
				//not a valid MP3-Tag!
				return null;
			}
			if(header[3] != 3 && header[4] != 0) {
				//not the valid version!
				return null;
			}
			if((header[5] & (1 << 6)) == 1) {
				//extended header flags are set -> ignore!
				return null;
			}
			
			int tagSize = 0;
			for(int i = 0; i < 4; i++) {
				tagSize = tagSize | (header[6] << 21) | (header[7] << 14) | (header[8] << 7) | (header[9]);
			}
			tagSize += 10;

			frames = new LinkedList<byte[]>();
			int offset = 10;
			while(offset < tagSize ) {
				getFrames(offset);
				offset += frames.getLast().length;
				f.seek(offset);
				if(!Character.isLetterOrDigit(f.readByte())) {
					break;
				}
			}

			mp3f = new MP3File();
			for(int i = 0; i < frames.size(); i++) {
				String val = "" + (char)frames.get(i)[0] + (char)frames.get(i)[1] + (char)frames.get(i)[2] + (char)frames.get(i)[3];
				if(val.equalsIgnoreCase("TALB")) {
					byte copy[] = frames.get(i);
					String retval = "";
					if(copy[10] == 0) {
						for(int k = 11; k < copy.length; k++) {
							retval += (char)copy[k];
						}
					}else if(copy[10] == 1) {
						if((copy[11] & 0xFF) == 0xFF && (copy[12] & 0xFF) == 0xFE) {
							for(int k = 13; k < copy.length; k +=2) {
								retval += (char)(copy[k +1] << 8 | copy[k]);
							}
						}else if((copy[11] & 0xFF) == 0xFE && (copy[12] & 0xFF) == 0xFF) {
							
							for(int k = 13; k < copy.length; k +=2) {
								retval += (char)(copy[k] << 8 | copy[k +1]);
							}
						}
					}
					mp3f.setAlbum(retval);
				}else if(val.equalsIgnoreCase("TPE2")) {
					byte copy[] = frames.get(i);
					String retval = "";
					if(copy[10] == 0) {
						for(int k = 11; k < copy.length; k++) {
							retval += (char)copy[k];
						}
					}else if(copy[10] == 1) {
						if((copy[11] & 0xFF) == 0xFF && (copy[12] & 0xFF) == 0xFE) {
							for(int k = 13; k < copy.length; k +=2) {
								retval += (char)(copy[k +1] << 8 | copy[k]);
							}
						}else if((copy[11] & 0xFF) == 0xFE && (copy[12] & 0xFF) == 0xFF) {
							
							for(int k = 13; k < copy.length; k +=2) {
								retval += (char)(copy[k] << 8 | copy[k +1]);
							}
						}
					}
					mp3f.setInterpret(retval);
				}else if(val.equalsIgnoreCase("TIT2")) {
					byte copy[] = frames.get(i);
					String retval = "";
					if(copy[10] == 0) {
						for(int k = 11; k < copy.length; k++) {
							retval += (char)copy[k];
						}
					}else if(copy[10] == 1) {
						if((copy[11] & 0xFF) == 0xFF && (copy[12] & 0xFF) == 0xFE) {
							for(int k = 13; k < copy.length; k +=2) {
								retval += (char)(copy[k +1] << 8 | copy[k]);
							}
						}else if((copy[11] & 0xFF) == 0xFE && (copy[12] & 0xFF) == 0xFF) {
							
							for(int k = 13; k < copy.length; k +=2) {
								retval += (char)(copy[k] << 8 | copy[k +1]);
							}
						}
					}
					mp3f.setTitle(retval);
				}else if(val.equalsIgnoreCase("TYER")) {
					byte copy[] = frames.get(i);
					String retval = "";
					if(copy[10] == 0) {
						for(int k = 11; k < copy.length; k++) {
							retval += (char)copy[k];
						}
					}else if(copy[10] == 1) {
						if((copy[11] & 0xFF) == 0xFF && (copy[12] & 0xFF) == 0xFE) {
							for(int k = 13; k < copy.length; k +=2) {
								retval += (char)(copy[k +1] << 8 | copy[k]);
							}
						}else if((copy[11] & 0xFF) == 0xFE && (copy[12] & 0xFF) == 0xFF) {
							
							for(int k = 13; k < copy.length; k +=2) {
								retval += (char)(copy[k] << 8 | copy[k +1]);
							}
						}
					}
					mp3f.setYear(retval);
				}else if(val.equalsIgnoreCase("APIC")) {
					byte copy[] = frames.get(i);
					BufferedImage retval = null;
					int pointer = 11;
					
					if(copy[10] == 0) {
						String mime = "";
						while(copy[pointer] != 0) {
							mime += (char)copy[pointer++];
						}
						if(copy[++pointer] != 3) {
							//kein Frontcover!
							continue;
						}
						while(copy[++pointer] != 0) {}
						
						byte picture[] = new byte[copy.length - pointer -1];
						for(int k = 0; k < picture.length; k++) {
							picture[k] = copy[k + pointer +1];
						}
						if(mime.equalsIgnoreCase("image/jpeg")|| mime.equals("image/png")) {
							
							MemoryCacheImageInputStream stream = new MemoryCacheImageInputStream(new ByteArrayInputStream(picture));
							
							BufferedImage image1 = null;
							try {
								image1 = ImageIO.read(stream);
							}catch (IOException e) {
								e.printStackTrace();
							}
							if(image1 != null) {
								mp3f.setCover(image1);
							}
						}
					}else if(copy[10] == 1) {
					 //TODO: unicode!=?!?!?!	
					}
				}
			}
			
			f.close();
		}catch (FileNotFoundException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		return mp3f;
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
	
	public MP3Parser(File file) {
		RandomAccessFile f;
		try {
			f = new RandomAccessFile(file, "r");
			f.seek(0);
			for(int i = 0; i < 32779; i++) {
				int aByte = f.read();
				System.out.println(Integer.toBinaryString(aByte) + ", " + aByte + ", " + (char)aByte);
			}
			
			f.close();
		}catch (FileNotFoundException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
