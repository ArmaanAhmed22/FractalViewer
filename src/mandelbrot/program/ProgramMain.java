package mandelbrot.program;

import java.awt.Color;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.desktop.QuitStrategy;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.print.attribute.standard.MediaName;
import javax.swing.AbstractButton;
import javax.swing.JFrame;

import mandelbrot.events.Event;
import mandelbrot.events.handlers.ComponentEventHandler;
import mandelbrot.events.handlers.KeyBoardEventHandler;
import mandelbrot.events.handlers.MouseEventHandler;
import mandelbrot.events.handlers.WindowEventHandler;
import mandelbrot.math.ComplexNumber;
import mandelbrot.math.fractal.Fractal;
import mandelbrot.math.fractal.Fractal.FractalID;
import mandelbrot.math.fractal.Fractal.FractalInfo;
import mandelbrot.math.fractal.FractalDisplay;
import mandelbrot.math.fractal.FractalInterpolate;
import mandelbrot.math.fractal.FractalInterpolate.FractalInterpolateType;
import mandelbrot.math.fractal.FractalRenderer;

public class ProgramMain {
	
	public static void main(String[] args) {
		
		System.setProperty("apple.eawt.quitStrategy", QuitStrategy.CLOSE_ALL_WINDOWS.name());
		
		final var fps = 30;
		final var ups = 30;
		var program = new FractalProgram(new Dimension(512, 512),fps,ups);
		
		program.start();
	}

}

final class FractalProgram extends Program {
	
	private FractalRenderer fracRender;
	private FractalInterpolate mandelbrot;
	private Function<FractalInfo,Integer> colorer;
	private FractalDisplay sideBar;
	
	private final ThreadPoolExecutor threadPool;
	private final int THREAD_NUM = 10;
	private final List<Runnable> commands;
	private CountDownLatch latch;
	
	private ComplexNumber upperLeft;
	private ComplexNumber bottomRight;
	private final double dragSpeed = 100; //Higher means slower
	private double zoom = 1;
	
	private boolean gamePaused;
	
	private final double getDrag() {
		return dragSpeed*zoom;
	}
	
	private void incrementZoom(double amt) {
		var xChange = bottomRight.getReal() - upperLeft.getReal();
		var yChange = upperLeft.getImaginary() - bottomRight.getImaginary();
		
		var xZoom = xChange*amt;
		var yZoom = yChange*amt;
		zoom+=amt;
		upperLeft.setAddReal(xZoom);
		upperLeft.setAddImaginary(-yZoom);
		
		bottomRight.setAddReal(-xZoom);
		bottomRight.setAddImaginary(yZoom);
	}

	public FractalProgram(Dimension size, int fps,int ups) {
		super(size, fps,ups);
		addEventHandler(KeyBoardEventHandler.class);
		addEventHandler(MouseEventHandler.class);
		addEventHandler(ComponentEventHandler.class);
		addEventHandler(WindowEventHandler.class);
		ehm.bind(Event.EventType.KEY_PRESS, (Event e) -> {
			var charKey = (int)e.getAssociatedData().getInfo();
			if (charKey == 90) { // 'z'
				//upperLeft = new ComplexNumber(upperLeft.getReal().doubleValue() - 0.02, upperLeft.getReal().doubleValue() - 0.02);
				incrementZoom(0.05);
				
			}
			else if (charKey == 88) {// 'x'
				incrementZoom(-0.05);
			}
			
			else if (charKey == 38) {//'up'
				mandelbrot.addIterations(5);
			} else if (charKey == 40) {//'down'
				mandelbrot.addIterations(-5);
			}
			
			else if (charKey == 80) {// 'p'
				gamePaused =! gamePaused;
			}
			
			else if (charKey == 81) {//'q'
				try {
					ImageIO.write(getScreenImage(), "png", new File("final.png"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);
			}
			
			else if (charKey == 39) { //RIGHT
				
			} else if (charKey == 37) { //LEFT
				
			}
			else { //39 RIGHT //37 LEFT
				System.out.println(charKey);
			}
		})
		.bind(Event.EventType.MOUSE_MOVE, (Event e) -> {
			var mouseChange = (MouseEventHandler.MouseChange)e.getAssociatedData().getInfo();
			switch (mouseChange.modifier) {
			case NONE:
				
				
				
				break;
			case DRAG:
				
				var xMouseChange = (mouseChange.after.x - mouseChange.before.x)/20d;
				var yMouseChange = -(mouseChange.after.y - mouseChange.before.y)/20d;
				
				var xChange = bottomRight.getReal() - upperLeft.getReal();
				var yChange = upperLeft.getImaginary() - bottomRight.getImaginary();
				var xInc = xChange/100;
				var yInc = yChange/100;
				upperLeft.setAddReal(xMouseChange*xInc);
				upperLeft.setAddImaginary(yMouseChange*yInc);
				bottomRight.setAddReal(xMouseChange*xInc);
				bottomRight.setAddImaginary(yMouseChange*yInc);
				
				
				
				break;
			default:
				break;
			}
		}).bind(Event.EventType.SCREEN_RESIZE, (Event e) -> adjustSize());
		
		sideBar = new FractalDisplay();
		sideBar.select(FractalID.MANDELBROT.toString());
		mandelbrot = new FractalInterpolate(5,2,Fractal.getFractal(FractalID.TEST, 5, 4),Fractal.getFractal(FractalID.MANDELBROT, 5, 4),FractalInterpolateType.ADDITIVE);
		fracRender = new FractalRenderer(getScreenImage(),getSize().width,getSize().height);
		/*colorer = (FractalInfo info) -> {
			
			if (info==null)
				return 0;
			else {
				var abs = info.z.abs();
				//var norm = (zn1.abs()-UPPER_BOUND)/(UPPER_BOUND_SQUARED-UPPER_BOUND);
				//var norm = ((zn.absSquared()-UPPER_BOUND_SQUARED)/(UPPER_BOUND_SQUARED*UPPER_BOUND_SQUARED))+getTime()/5000000d;
				var log1 = (Math.log10(abs)/Math.log10(mandelbrot.getRadiusDiv()));
				var norm = Math.log10(log1)/Math.log10(2);
				
				//System.out.println(log1);
				
				var rNorm = Math.sqrt((Math.sin(info.iteration-norm)+1));
				var gNorm = Math.sqrt((Math.sin(0.3*(info.iteration-norm))+1));
				var bNorm = Math.sqrt((Math.sin(0.2*(info.iteration-norm))+1));
				
				
				int r = (int) (rNorm*255);
				int g = (int) (gNorm*255);
				int b = (int) (bNorm*255);
				return ScreenImage.getHex(r, g, b);
			}
		};*/
		
		colorer = (FractalInfo inf) -> {
			if (inf==null) {
				return 0;
			} else {
				double nsmooth = (inf.iteration + 1 - Math.log(Math.log(inf.z.abs()))/Math.log(2))/inf.totalIterations;
				
				return ScreenImage.getHex((int)((0.95+10*nsmooth)*255), 151, (int)(255*nsmooth));
			}
		};
		
		upperLeft = new ComplexNumber(-5, 5);
		bottomRight = new ComplexNumber(5, -5);
		
		
		latch = new CountDownLatch(THREAD_NUM);
		
		threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_NUM);
		commands = makeCommands(THREAD_NUM,size);
		
	}
	
	private List<Runnable> makeCommands(int THREAD_NUM,Dimension size) {
		
		var temp = new Runnable[THREAD_NUM];
		var area = size.width*size.height;
		for (var i = 0; i < THREAD_NUM; i++) {
			final var finalI = i;
			temp[i] = () -> {
				var thisStart = (finalI+0d) / THREAD_NUM * getArea();
				var thisEnd = (finalI+1d) / THREAD_NUM * getArea();
				
				double xDistance = bottomRight.getReal() - upperLeft.getReal();
				double yDistance = upperLeft.getImaginary() - bottomRight.getImaginary(); 
				
				for (var j = (int) thisStart; j < thisEnd; j++) {
					var curReal = upperLeft.getReal()+((j % getSize().width)  / (getSize().width - 1d))*xDistance;
					var curImaginary = upperLeft.getImaginary() - ((j / getSize().width) / (getSize().height - 1d))*yDistance;
					fracRender.renderSingleNumber(new ComplexNumber(curReal,curImaginary), mandelbrot, colorer, j);
					//fracRender.renderSingleNumber(bottomRight, mandelbrot, colorer, THREAD_NUM);
					
				}
				latch.countDown();
			};
		}
		return Collections.unmodifiableList(Arrays.asList(temp));
	}


	@Override
	public void onUpdate() {
		ehm.checkAndPerformAction();
		if (!gamePaused)
			mandelbrot.transitionUpdateS();
		
	}

	@Override
	public void onRender(Graphics g) {
		for (var command : commands) {
			threadPool.execute(command);
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		latch = new CountDownLatch(THREAD_NUM);
		//g.setColor(Color.white);
		g.setColor(Color.black);
		
		g.drawImage(getScreenImage(), 0, 0, null);
		sideBar.draw(g);
		
		g.drawString("Iterations: "+mandelbrot.getIterations()+"\nFPS:" + this.getFPS(), 30, 50);
	}
	
}
