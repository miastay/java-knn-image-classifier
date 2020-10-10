package imgrec;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class FileImage implements Serializable {

	public final File file;
	public double pixVals;
	public final String identifier;
	
	public FileImage(File f, String i){
		this.file = f;
		this.identifier = i;
	}
	//reads pixels of image and returns 2D 28x28 double array corresponding to grayscale values
	public double[][] genPixArray() throws IOException{
		
		double[][] pixels = new double[28][28];
		
		BufferedImage image = ImageIO.read(file);
		  // Getting pixel color by position x and y 
		  for(int x = 0; x < 28; x++){
			  for(int y = 0; y < 28; y++){
				  int clr=  image.getRGB(x,y); 
				  int  blue  =  clr & 0x000000ff;
				  pixels[x][y] = blue/255.0;
				  pixVals += blue/255.0;
				  Client.ops++;
			  }
			  Client.ops++;
		  }
		  
		  return pixels;
	}
	
}
