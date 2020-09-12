package br.ufsm.inf.examclipper;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import br.ufsm.inf.examclipper.controller.PDFConversor;
import br.ufsm.inf.examclipper.model.Page;

public class ExamClipper {
	
	private static final String arquivo = "B:/Java/cc2017.pdf";
	
	
	private static void test() throws FileNotFoundException{
		File f=new File(arquivo);
		System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
		List<Page> lPages = new ArrayList<>();
		PDFConversor p = new PDFConversor(f, lPages);
		p.run();
		int k=0;
		for(Page page:lPages) {
			
			if(k>=0) {
				try {
					/*Mat eventless = Highgui.imread("files/eventless.png");
				    Mat barrier = Highgui.imread("files/barrier/lc-00201.png");
				    Mat difference = new Mat();
				    Mat lines = new Mat();
	
				    Core.absdiff(eventless, barrier, difference);
	
				    Mat grey = new Mat();
				    Imgproc.cvtColor(difference, grey, Imgproc.COLOR_BGR2GRAY);
					*/
					File image = File.createTempFile("imageManipulationJava", "jpg");
					ImageIO.write(page.getBufferedImage(), "jpg", image);
					Mat mat = Imgcodecs.imread(image.getAbsolutePath(),Imgcodecs.IMREAD_GRAYSCALE);
					Mat dst = new Mat(), cdst = new Mat(), cdstP,lines = new Mat();
					
					Imgproc.Canny(mat, dst, 50, 200, 3, false);
			        Imgproc.cvtColor(mat, cdst, Imgproc.COLOR_GRAY2BGR);
			        cdstP = cdst.clone();
			        Imgproc.HoughLines(dst, lines, 1, Math.PI, 600);
			        for (int x = 0; x < lines.rows(); x++) {
			            double rho = lines.get(x, 0)[0],
			                    theta = lines.get(x, 0)[1];
			            double a = Math.cos(theta), b = Math.sin(theta);
			            double x0 = a*rho, y0 = b*rho;
			            Point pt1 = new Point(Math.round(x0 + 1000*(-b)), Math.round(y0 + 1000*(a)));
			            Point pt2 = new Point(Math.round(x0 - 1000*(-b)), Math.round(y0 - 1000*(a)));
			            Imgproc.line(cdst, pt1, pt2, new Scalar(255, 0, 0), 1, Imgproc.LINE_AA, 0);
				    }
			        
			        Mat linesP = new Mat(); // will hold the results of the detection
			        Imgproc.HoughLinesP(dst, linesP, 1, Math.PI, 100, 400, 5); // runs the actual detection
			        // Draw the lines
			        for (int x = 0; x < linesP.rows(); x++) {
			            double[] l = linesP.get(x, 0);
			            Point pt1 = new Point(l[0], l[1]);
			            Point pt2 = new Point(l[2], l[3]);
			            System.out.println(pt1);
			            System.out.println(pt2);
			            Imgproc.line(cdstP, pt1, pt2, new Scalar(0, 0, 255), 1, Imgproc.LINE_AA, 0);
			        }
			        // Show results
			        HighGui.imshow("Source", mat);
			        HighGui.imshow("Detected Lines (in red) - Standard Hough Line Transform", cdst);
			        HighGui.imshow("Detected Lines (in red) - Probabilistic Line Transform", cdstP);
			        // Wait and Exit
			        //HighGui.waitKey();
			        
					BufferedImage imgSave = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
					byte[] data = ((DataBufferByte) imgSave.getRaster().getDataBuffer()).getData();
					cdstP.get(0, 0, data);
					image=new File("B:/Java/imagem"+(k++)+".jpg");
					ImageIO.write(imgSave, "jpg", image);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				File image=new File("B:/Java/imagem"+(k++)+".jpg");
				try {
					ImageIO.write(page.getBufferedImage(), "jpg", image);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(lPages.size());
		
	}
	
	
	public static void main(String[] args) throws FileNotFoundException  {
		test();
		//ExamClipperGUI gui = new ExamClipperGUI();
		//gui.setVisible(true);
	}
}
 