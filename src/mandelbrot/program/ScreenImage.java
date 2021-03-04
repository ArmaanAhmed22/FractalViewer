package mandelbrot.program;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public final class ScreenImage extends BufferedImage {
	
	private int[] imageBuffer;

	public ScreenImage(int width, int height) {
		super(width, height, TYPE_INT_RGB);
		
		imageBuffer = ((DataBufferInt)this.getRaster().getDataBuffer()).getData();
	}
	
	public void setPixel(int index, int r, int g, int b) {
		imageBuffer[index] = getHex(r, g, b);
	}
	
	public void setPixel(int x, int y, int r, int g, int b) {
		imageBuffer[getIndex(x, y)] = getHex(r, g, b);
	}
	
	public void fill(int r, int g, int b) {
		Arrays.fill(imageBuffer, getHex(r,g,b));
	}
	
	public int getIndex(int x, int y) {
		return y*getLength()+x;
	}
	
	public int getLength() {
		return imageBuffer.length;
	}
	
	public int getHex(int r, int g, int b) {
		return r << 16 | g << 8 | b;
	}

}
