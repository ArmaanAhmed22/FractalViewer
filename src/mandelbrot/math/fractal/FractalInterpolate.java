package mandelbrot.math.fractal;

import java.util.function.BiFunction;
import java.util.function.Function;

import mandelbrot.math.ComplexNumber;

public class FractalInterpolate extends Fractal {
	
	private double S;

	public FractalInterpolate(int NUM_ITERATIONS, int RADIUS_OF_DIVERGENCE, Fractal a, Fractal b, FractalInterpolateType type) {
		super(NUM_ITERATIONS, RADIUS_OF_DIVERGENCE);
		setIterator(getInterpolator(type).apply(a, b));
		// TODO Auto-generated constructor stub
	}
	
	
	public static enum FractalInterpolateType {
		ADDITIVE;
	}
	
	private BiFunction<Fractal, Fractal, BiFunction<ComplexNumber, ComplexNumber, ComplexNumber>> getInterpolator(FractalInterpolateType type) {
		return switch (type) {
			case ADDITIVE: {
				Fractal f = null;
				Fractal f2 = null;
				yield (Fractal fracA, Fractal fracB) -> {
					return (ComplexNumber z, ComplexNumber c) -> {
						return fracA.getIterator().apply(z,c).setScale((1-getS())).setAdd(fracB.getIterator().apply(z,c).setScale((getS())));
					};
				};
			}
		};
	}
	
	public void setS(double newS) {
		S=newS;
	}
	
	
	public void incrementS(double inc) {
		S+=inc;
	}
	
	public double getS() {
		return S;
	}
	
	

}
