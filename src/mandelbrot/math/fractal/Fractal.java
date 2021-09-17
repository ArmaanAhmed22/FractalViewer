package mandelbrot.math.fractal;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import mandelbrot.math.ComplexNumber;

public class Fractal {
	
	private static Map<String,BiFunction<ComplexNumber, ComplexNumber, ComplexNumber>> fractals;
	
	static {
		fractals = new HashMap<String,BiFunction<ComplexNumber, ComplexNumber, ComplexNumber>>(FractalID.values().length);
		for (var fractalID : FractalID.values())
			fractals.put(fractalID.toString(), getFractalIterator(fractalID));
	}
	
	private int numIterations;
	private int radiusDiv;
	private int radiusDivSq;
	private BiFunction<ComplexNumber, ComplexNumber, ComplexNumber> iterate;
	
	public Fractal(int numIterations,int radiusDiv, BiFunction<ComplexNumber, ComplexNumber, ComplexNumber> iterate) {
		this.numIterations = numIterations;
		this.radiusDiv = radiusDiv;
		this.radiusDivSq = radiusDiv*radiusDiv;
		this.iterate = iterate;
	}
	
	public Fractal(int numIterations,int radiusDiv) {
		this.numIterations = numIterations;
		this.radiusDiv = radiusDiv;
		this.radiusDivSq = radiusDiv*radiusDiv;
		//this.iterate = iterate;
	}
	
	public Fractal(String name, int numIterations, int radiusDiv) {
		this.numIterations = numIterations;
		this.radiusDiv = radiusDiv;
		this.radiusDivSq = radiusDiv*radiusDiv;
		this.iterate = fractals.get(name);
	}
	
	public static class FractalInfo {
		public final ComplexNumber z;
		public final int iteration;
		public final int totalIterations;
		
		private FractalInfo(ComplexNumber z, int iteration, int totalIterations) {
			this.z = z;
			this.iteration = iteration;
			this.totalIterations = totalIterations;
		}
	}
	
	public FractalInfo inSet(ComplexNumber c) {
		var zn = new ComplexNumber(0, 0);
		for (var i = 0; i < numIterations; i++) {
			var zn1 = iterate.apply(zn,c);
			
			if (zn1.absSquared() > radiusDivSq) {
				return new FractalInfo(zn1,i + 1,numIterations);
			}
			
			zn = zn1;
		}
		return null;
	}
	
	public static enum FractalID {
		MANDELBROT,BURNING_SHIP,TEST;
	}
	
	public static Fractal getFractal(FractalID id, int numIterations, int radiusOfDiv) {
		return switch (id) {
			case MANDELBROT: {
				
				yield new Fractal(numIterations, radiusOfDiv, getFractalIterator(id));
			}
			case BURNING_SHIP: {
				yield new Fractal(numIterations, radiusOfDiv, getFractalIterator(id));
			}
			case TEST: {
				yield new Fractal(numIterations, radiusOfDiv,getFractalIterator(id));
			}
		};
	}
	
	public static BiFunction<ComplexNumber, ComplexNumber, ComplexNumber> getFractalIterator(FractalID id) {
		return switch (id) {
			case MANDELBROT: {
				yield (ComplexNumber z, ComplexNumber c) -> z.getSquare().setAdd(c);
			} case BURNING_SHIP: {
				yield (ComplexNumber z, ComplexNumber c) -> new ComplexNumber(z.getReal()*z.getReal()-z.getImaginary()*z.getImaginary()+c.getReal(),2*Math.abs(z.getReal()*z.getImaginary())+c.getImaginary());
			}
			case TEST: {
				yield (ComplexNumber z, ComplexNumber c) -> z.getSquare().setCos().setAdd(c);
			}
		};
	}
	
	public final BiFunction<ComplexNumber, ComplexNumber, ComplexNumber> getIterator() {
		return iterate;
	}
	
	final void setIterator(BiFunction<ComplexNumber, ComplexNumber, ComplexNumber> iterate) {
		this.iterate = iterate;
	}
	
	public final void addIterations(int num) {
		numIterations = Math.max(0, numIterations+num);
	}
	
	public final int getIterations() {
		return numIterations;
	}
	
	public final int getRadiusDiv() {
		return radiusDiv;
	}
	
	
	
	

}
