package imgrec;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.ConsoleHandler;

import javax.imageio.IIOException;
import javax.swing.JApplet;
import javax.swing.JPanel;

public class Client {

	/**
	 * k-Nearest Neighbor Classifier
	 * Ryan Taylor, June 11 2019
	 * 
	 */
	
	static ArrayList<FileImage> images = new ArrayList<FileImage>();
	static ArrayList<double[][]> imageDecomps = new ArrayList<double[][]>();
		//testDecomps was exclusively used for 7 heatmap, not necessary for function
		static ArrayList<double[][]> testDecomps = new ArrayList<double[][]>();
	static FileImage testImg;
	static FileImage trainImg;
	static double[][] testDecomp;
	static FileImage[] results = new FileImage[10];
	static ArrayList<String> rests = new ArrayList<String>();
	static File f;
	static PrintWriter idents;
	static PrintWriter dataPoints;
		//see above comment
		static PrintWriter testDecompArray;	
	static int ops;
	
	public static void main(String[] args){
		
		//creates FileImages from filepath and constructs 2D double arrays for each, stored in imageDecomps
		initImages();
		
		//only used for 7s output (not central to code)
		try {
			testDecompArray = new PrintWriter("test-decomps.txt", "UTF-8");
		} catch (Throwable e2) {
			e2.printStackTrace();
		}
		//printTestDecomps();
		//
		
		/*
		 * 
		 * Failed set (L1, k=1):
		 * 29, 76, 77*, 129*, 139*, 148*, 178, 215*, 224*, 276, 306, 329*, 342
		 * [96.28571429% accurate]
		 * 
		 * Failed set (L2, k=1):
		 * 77*, 129*, 139*, 148*, 215*, 224*, 227, 329*
		 * [97.7142857143% accurate]
		 * 	
		 */
		
		try {
			idents = new PrintWriter("test-set-identifiers", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		//iterates through all test images, specifying k-nearest neighbor through "runImgs" algorithm
		//prints results to idents > test-set-identifiers
		int numImages = 1;
		for(int i = 0; i < numImages; i++){
			
			if(i == 148)
				i++;
			try { 
//				testImg = new FileImage(new File("./testSample/img_" + (i+1) + ".jpg"), "Unknown");
				testImg = new FileImage(new File("./testSample/5y65.jpg"), "Unknown");
			} catch (NullPointerException e){
				i++;
			}
			trainImg = images.get(0);
			try {
				testDecomp = testImg.genPixArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//idents.println(testImg.pixVals + ",");
		
			runImgs(testDecomp);
			//idents.println(rests.get(rests.size() - 1));
		}
		idents.close();
//			System.out.println(images.get(1).file.getAbsolutePath() + ": " + results[0].file.getAbsolutePath() + ", " + results[9]);
	}
	
	private static void printTestDecomps() {
		
		System.out.println(testDecomps.size());
		// TODO Auto-generated method stub
		double[][] testPixs = new double[28][28];
		double sum = 0;
		for(int r = 0; r < 28; r++){
			for(int c = 0; c < 28; c++){
				
				for(double[][] d : testDecomps){
					sum += d[r][c];
					System.out.println(sum);
				}
				
				testPixs[r][c] = (sum/testDecomps.size());
				testDecompArray.println(testPixs[r][c]);
				sum = 0;
			}
		}
		testDecompArray.close();
	}

	public static void initImages() {
		
		f = new File("./IMAGES/0");
		for(File p : f.listFiles()){
			images.add(new FileImage(p, "0"));
			ops++;
		}
		f = new File("./IMAGES/1");
		for(File p : f.listFiles()){
			images.add(new FileImage(p, "1"));
			ops++;
		}
		f = new File("./IMAGES/2");
		for(File p : f.listFiles()){
			images.add(new FileImage(p, "2"));
			++ops;
		}
		f = new File("./IMAGES/3");
		for(File p : f.listFiles()){
			images.add(new FileImage(p, "3"));
			++ops;
		}
		f = new File("./IMAGES/4");
		for(File p : f.listFiles()){
			images.add(new FileImage(p, "4"));
			++ops;
		}
		f = new File("./IMAGES/5");
		for(File p : f.listFiles()){
			images.add(new FileImage(p, "5"));
			++ops;
		}
		f = new File("./IMAGES/6");
		for(File p : f.listFiles()){
			images.add(new FileImage(p, "6"));
			++ops;
		}
		f = new File("./IMAGES/7");
		for(File p : f.listFiles()){
			images.add(new FileImage(p, "7"));
			++ops;
		}
		f = new File("./IMAGES/8");
		for(File p : f.listFiles()){
			images.add(new FileImage(p, "8"));
			++ops;
		}
		f = new File("./IMAGES/9");
		for(File p : f.listFiles()){
			images.add(new FileImage(p, "9"));
			++ops;
		}
		
		images.remove(0);
		Collections.shuffle(images);
		
		images.remove(0);
		
		try {
			dataPoints = new PrintWriter("datapoints", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(FileImage i : images){
			try {
				double[][] pixArray = i.genPixArray();
				dataPoints.println(i.pixVals + "," + i.identifier);
				imageDecomps.add(pixArray);
				
					if(i.identifier == "7"){
						testDecomps.add(pixArray);
					}
				
				System.out.println("Indexing " + (++ops/34185559.0)*100 + "%");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void runImgs(double[][] test){
		//declare 2D-double array to compare with -test-
		double[][] curs;
		//initialize variables
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		int mIndex = 0;
		int xIndex = 0;
		
		ArrayList<Double> totals = new ArrayList<Double>();
			//iterate through training images
			for(int c = 0; c < imageDecomps.size()-1; c++){
				//assign second array to given training FileImage pixels
				curs = imageDecomps.get(c);
				//check that indices of two arrays are not equal (testing image is not training image)
				if(!curs.equals(test)){
					//assign value of compare (0.0 means identical images), using L2 distance
					double total = compareArrayL2(curs, test);
					trainImg = images.get(c);
					totals.add(total);
					if(total < min){
						min = total;
						mIndex = c;
					}
					if(total > max){
						max = total;
						xIndex = c;
					}
				}
			}
		//total iterations is equal to -num- * size of imageDecomps (~43,000) * size of images (1456) * k iteration (10)
		// = num * 617,480,000
		rests.add(images.get(mIndex).identifier);
		System.out.println(rests.get(rests.size()-1));
	}
	public static double compareArrayL1(double[][] comp, double[][] orig){
		double sum = 0.0;
		for(int x = 0; x < 28; x++){
			for(int y = 0; y < 28; y++){
				sum += Math.abs(orig[x][y] - comp[x][y]);
			}
		}
		return sum;
	}
	public static double compareArrayL2(double[][] comp, double[][] orig){
		double sum = 0.0;
		for(int x = 0; x < 28; x++){
			for(int y = 0; y < 28; y++){
				sum += (orig[x][y] - comp[x][y]) * (orig[x][y] - comp[x][y]);
			}
		}
		return Math.sqrt(sum);
	}
}
