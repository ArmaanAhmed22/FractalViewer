package mandelbrot.math.fractal;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import mandelbrot.math.ComplexNumber;
import mandelbrot.math.fractal.Fractal.FractalID;
import mandelbrot.math.fractal.Fractal.FractalInfo;
import mandelbrot.program.ScreenImage;

public class FractalDisplay {
	
	private final ArrayList<BufferedImage> icons;
	
	//Resolution
	private int numIteration;
	
	public FractalDisplay() {
		icons = new ArrayList<BufferedImage>();
		numIteration = 10;
		
		
		
		//Init
		var leftTop = new ComplexNumber(-1,1);
		var rightBottom = new ComplexNumber(1,-1);
		
		
		Function<FractalInfo,Integer> colorer = (FractalInfo info) -> {
			if (info == null)
				return 0;
			else
				return ScreenImage.getHex(255, 0, 0);
		};
		
		for (var fractalID : FractalID.values()) {
			var image = new ScreenImage(70, 70);
			var renderer = new FractalRenderer(image,70,70);
			
			var fractal = Fractal.getFractal(fractalID, numIteration, 2);
			renderer.render(leftTop, rightBottom, fractal, colorer);
			icons.add(image);
			
			
		}
	}
	
	public void draw(Graphics g) {
		for (int i = 0; i < icons.size(); i++) {
			g.drawImage(icons.get(i), 0, 70*i, null);
		}
	}
	
	
	
	

}
