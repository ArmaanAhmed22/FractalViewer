package mandelbrot.math.fractal;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

import mandelbrot.math.ComplexNumber;

public class FractalInterpolate extends Fractal {
	
	private double S;
	private Transition transitioner;

	public FractalInterpolate(int NUM_ITERATIONS, int RADIUS_OF_DIVERGENCE, Fractal a, Fractal b, FractalInterpolateType type) {
		super(NUM_ITERATIONS, RADIUS_OF_DIVERGENCE);
		setIterator(getInterpolator(type).apply(a, b));
		
		int steps = 100;
		transitioner = new Transition((int curStep) -> ( (Math.sin(Math.PI*curStep / steps - Math.PI/2)+1)/2 ), steps, true);
	}
	
	
	
	public static enum FractalInterpolateType {
		ADDITIVE,MULTIPLICATIVE;
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
		case MULTIPLICATIVE: {
			yield (Fractal fracA, Fractal fracB) -> {
				return (ComplexNumber z, ComplexNumber c) -> {
					return fracA.getIterator().apply(z,c).setScale(1-getS()).setMult(fracB.getIterator().apply(z,c).setScale(getS()));
				};
			};
		}
			//throw new UnsupportedOperationException("Unimplemented case: " + type);
			
		};
	}
	
	private class Transition {
		private int curStep;
		private int maxSteps;
		private double[] stepSValues;
		private IntToDoubleFunction sFunction;
		
		private boolean direction;
		
		public Transition(IntToDoubleFunction sFunction, int steps, boolean cache) {
			
			this.maxSteps = steps;
			if (cache) {
				stepSValues = new double[this.maxSteps];
				for (int i = 0; i < this.maxSteps; i++) {
					stepSValues[i] = sFunction.applyAsDouble(i+1);
				}
			}
			this.sFunction = sFunction;
			this.direction = true;
		}
		
		public void step() {
			
			setS(stepSValues[curStep]);
			
			if (curStep >= maxSteps -1) {
				direction = false;
			} else if (curStep <= 0) {
				direction = true;
			}
			curStep+=direction ? 1 : -1;
			
			
			
		}
		
	}
	
	public void transitionUpdateS() {
		transitioner.step();
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
