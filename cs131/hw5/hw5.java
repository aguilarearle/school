/* Name: Earle Aguilar

   UID: 804501476

   Others With Whom I Discussed Things:

   Other Resources I Consulted:
   sudo update-alternatives --set javaws /usr/local/java/jdk1.8.0_{xx}/jre/bin/
*/

import java.io.*;

import java.util.*;

import java.util.concurrent.*;

// a marker for code that you need to implement
class ImplementMe extends RuntimeException {}

// an RGB triple
class RGB {
    public int R, G, B;

    RGB(int r, int g, int b) {
    	R = r;
	G = g;
	B = b;
    }

    public String toString() { return "(" + R + "," + G + "," + B + ")"; }

}

class Mimage extends RecursiveAction{
    private RGB[] mirr_pixels;
    private PPMImage im;
    private int low, high, size;
    private static final int CUTOFF = 10000/width;
    
    Mimage(RGB[] job, RGB[] image, int low, int high, int size){
	this.low = low;
	this.high = high;
	this.mirr_pixels = image;
	this.im = job;
	this.size = size;
    }


    protected void compute(){
	if( (high - low) <= CUTOFF){
	    for (int r = low; r < high; r++){
		for(int c = 0; c < width; c++){
		    int start_ix = r * width;
		    mirr_pixels[start_ix + c] = im.pixels[start_ix + width - c - 1];
		}
	    }
	}

	int mid = (high + low) / 2;

	Mimage left = new Mimage(curr_pixels, mirr_pixels, low, mid, size);
	Mimage right = new Mimage(curr_pixels, mirr_pixels, mid, high, size);

	left.fork();
	right.fork();	
    }
}

// an object representing a single PPM image
class PPMImage {
    protected int width, height, maxColorVal;
    protected RGB[] pixels;

    public PPMImage(int w, int h, int m, RGB[] p) {
		width = w;
		height = h;
		maxColorVal = m;
		pixels = p;
    }

    // parse a PPM image file named fname and produce a new PPMImage object
    public PPMImage(String fname) 
    	throws FileNotFoundException, IOException {
		FileInputStream is = new FileInputStream(fname);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		br.readLine(); // read the P6
		String[] dims = br.readLine().split(" "); // read width and height
		int width = Integer.parseInt(dims[0]);
		int height = Integer.parseInt(dims[1]);
		int max = Integer.parseInt(br.readLine()); // read max color value
		br.close();

		is = new FileInputStream(fname);
	    // skip the first three lines
		int newlines = 0;
		while (newlines < 3) {
	    	int b = is.read();
	    	if (b == 10)
		    newlines++;
		}

		int MASK = 0xff;
		int numpixels = width * height;
		byte[] bytes = new byte[numpixels * 3];
        is.read(bytes);
		RGB[] pixels = new RGB[numpixels];
		for (int i = 0; i < numpixels; i++) {
	    	int offset = i * 3;
	    	pixels[i] = new RGB(bytes[offset] & MASK, 
				    bytes[offset+1] & MASK, 
				    bytes[offset+2] & MASK);
		}
		is.close();

		this.width = width;
		this.height = height;
		this.maxColorVal = max;
		this.pixels = pixels;
    }

	// write a PPMImage object to a file named fname
    public void toFile(String fname) throws IOException {
		FileOutputStream os = new FileOutputStream(fname);

		String header = "P6\n" + width + " " + height + "\n" 
						+ maxColorVal + "\n";
		os.write(header.getBytes());

		int numpixels = width * height;
		byte[] bytes = new byte[numpixels * 3];
		int i = 0;
		for (RGB rgb : pixels) {
	    	bytes[i] = (byte) rgb.R;
	    	bytes[i+1] = (byte) rgb.G;
	    	bytes[i+2] = (byte) rgb.B;
	    	i += 3;
		}
		os.write(bytes);
		os.close();
    }

	// implement using Java 8 Streams
    public PPMImage negate() {
	RGB[] neg_pixels =
	    Arrays
	    .stream(pixels)
	    .map( (RGB pix) -> { return new RGB(maxColorVal - pix.R, maxColorVal - pix.G, maxColorVal - pix.B);} )
	    .toArray(size -> new RGB[size]);
	return new PPMImage(width, height, maxColorVal,neg_pixels);
    }

	// implement using Java 8 Streams
    public PPMImage greyscale() {
	RGB[] grey_pixels =
	    Arrays
	    .stream(pixels)
	    .map( (RGB pix) ->
		  {
		      float new_col = .299f * pix.R + .587f * pix.G + .114f * pix.B;
		      int g_col = Math.round(new_col);
		      return new RGB(g_col, g_col,g_col);
		  })
	    .toArray(size -> new RGB[size]);
	return new PPMImage(width, height, maxColorVal, grey_pixels);
    }    
    
	// implement using Java's Fork/Join library
    public PPMImage mirrorImage() {
	RGB[] new_pixels = new RGB[pixels.length];
	new Mimage(new_pixels, pixels, 0, pixels.length, pixels.length);
	return new PPMImage(width, height, maxColorVal, new_pixels);
    }

	// implement using Java 8 Streams
    public PPMImage mirrorImage2() {
		throw new ImplementMe();
    }

	// implement using Java's Fork/Join library
    public PPMImage gaussianBlur(int radius, double sigma) {
		throw new ImplementMe();
    }

}

// code for creating a Gaussian filter
class Gaussian {

    protected static double gaussian(int x, int mu, double sigma) {
		return Math.exp( -(Math.pow((x-mu)/sigma,2.0))/2.0 );
    }

    public static double[][] gaussianFilter(int radius, double sigma) {
		int length = 2 * radius + 1;
		double[] hkernel = new double[length];
		for(int i=0; i < length; i++)
	    	hkernel[i] = gaussian(i, radius, sigma);
		double[][] kernel2d = new double[length][length];
		double kernelsum = 0.0;
		for(int i=0; i < length; i++) {
	    	for(int j=0; j < length; j++) {
				double elem = hkernel[i] * hkernel[j];
				kernelsum += elem;
				kernel2d[i][j] = elem;
	    	}
		}
		for(int i=0; i < length; i++) {
	    	for(int j=0; j < length; j++)
				kernel2d[i][j] /= kernelsum;
		}
		return kernel2d;
    }
}



