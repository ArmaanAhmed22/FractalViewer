package mandelbrot.math.fractal;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Function;

import mandelbrot.math.ComplexNumber;
import mandelbrot.math.fractal.Fractal.FractalInfo;
import mandelbrot.program.ScreenImage;

public class FractalRenderer {
	
	private ScreenImage to;
	private int width;
	private int height;
	
	public FractalRenderer(ScreenImage to, int width, int height) {
		this.to = to;
		this.width = width;
		this.height = height;
	}
	
	public void render(ComplexNumber topLeft,ComplexNumber bottomRight, Fractal frac, Function<FractalInfo,Integer> colorer) {
		double xDistance = bottomRight.getReal() - topLeft.getReal();
		double yDistance = topLeft.getImaginary() - bottomRight.getImaginary(); 
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				var curReal = topLeft.getReal()+(i / (width - 1d))*xDistance;
				var curImaginary = topLeft.getImaginary() - (j / (height - 1d))*yDistance;
				renderSingleNumber(new ComplexNumber(curReal,curImaginary),frac,colorer,i+j*width);
			}
		}
	}
	
	public void renderSingleNumber(ComplexNumber z, Fractal frac, Function<FractalInfo,Integer> colorer,int index) {
		to.setPixel(index, colorer.apply(frac.inSet(z)));
	}

}
