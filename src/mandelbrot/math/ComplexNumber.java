package mandelbrot.math;

import java.math.BigDecimal;
import java.math.MathContext;

public class ComplexNumber {
	
	public static final double REAL_PER_PIXEL = 0.001;
	public static final double IMAGINARY_PER_PIXEL = 0.001;
	
	private double real;
	private double imaginary;
	
	public ComplexNumber(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}
	
	public double abs() {
		return Math.sqrt(absSquared());
	}
	
	public double absSquared() {
		return real*real+imaginary*imaginary;
	}
	
	public double getReal() {
		return real;
	}
	
	public double getImaginary() {
		return imaginary;
	}
	
	public ComplexNumber getSquare() {
		var newReal = real*real - imaginary*imaginary;
		var newImaginary = 2*real*imaginary;
		return new ComplexNumber(newReal, newImaginary);
	}
	
	
	public ComplexNumber setToSquare() {
		real = real*real - imaginary*imaginary;
		imaginary = 2*real*imaginary;
		return this;
	}
	
	public ComplexNumber add(ComplexNumber c) {
		return new ComplexNumber(real+c.real, imaginary+c.imaginary);
	}
	
	public ComplexNumber setAdd(ComplexNumber c) {
		real+=c.real;
		imaginary+=c.imaginary;
		return this;
	}
	
	public ComplexNumber difference(ComplexNumber c) {
		return new ComplexNumber(real-c.real, imaginary-c.imaginary);
	}
	
	public ComplexNumber setAddReal(double realChange) {
		real+=realChange;
		return this;
	}
	
	public ComplexNumber setAddImaginary(double imaginaryChange) {
		imaginary+=imaginaryChange;
		return this;
	}
	
	public ComplexNumber setMult(ComplexNumber c) {
		this.real=real*c.real-imaginary*c.imaginary;
		this.imaginary=imaginary*c.real+real*c.imaginary;
		return this;
	}
	
	public ComplexNumber mult(ComplexNumber c) {
		return new ComplexNumber(real*c.real-imaginary*c.imaginary, imaginary*c.real+real*c.imaginary);
	}
	
	public ComplexNumber getPow(double num) {
		double theta = Math.atan(imaginary/real);
		double r = abs();
		var com = new ComplexNumber(Math.pow(r, num)*Math.cos(num*theta), Math.pow(r, num)*Math.sin(num*theta));
		return com;
	}
	
	public ComplexNumber setPow(double num) {
		double theta = Math.atan(imaginary/real);
		double r = abs();
		this.real = Math.pow(r, num)*Math.cos(num*theta);
		this.imaginary =  Math.pow(r, num)*Math.sin(num*theta);
		
		return this;
	}
	
	public ComplexNumber setScale(double scaler) {
		real*=scaler;
		imaginary*=scaler;
		return this;
	}
	
	public ComplexNumber setCos() {
		var temp = Math.sin(real)*Math.cosh(imaginary);
		imaginary = Math.cos(real) * Math.sinh(imaginary);
		real = temp;
		return this;
	}
	
	@Override
	public String toString() {
		return getReal()+" + "+getImaginary()+"i";
	}

}
