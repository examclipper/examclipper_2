package br.ufsm.examclipper.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import br.ufsm.inf.examclipper.controller.PDFConversor;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.Rectangle;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.detectors.NurminenDetectionAlgorithm;

public final class PdfManager {

	
	public static Map<Integer, List<Rectangle>> findTables(File f) throws IOException{
		ObjectExtractor extractor =null;
		try {
			//Carrega documento pdf e o extrator
			PDDocument pdfDocument = PDDocument.load(f);
	        extractor = new ObjectExtractor(pdfDocument);
	
	        //Lugar onde ficarão armazenados as tabelas detectadas;
	        Map<Integer, List<Rectangle>> detectedTables = new HashMap<>();
	
	        //O algoritmo do tabula-java que é recomendado para detectar tabelas
	        NurminenDetectionAlgorithm detectionAlgorithm = new NurminenDetectionAlgorithm();
	
	        //Processo de extração das tabelas de cada página;
	        PageIterator pages = extractor.extract();
	        while (pages.hasNext()) {
	            Page page = pages.next();
	            //Detecta uma ou mais tabelas por página
	            List<Rectangle> tablesOnPage = detectionAlgorithm.detect(page);
	            if (tablesOnPage.size() > 0) {
	                detectedTables.put(page.getPageNumber(), tablesOnPage);
	            }
	        }
	        return detectedTables;
		} finally {
            if (extractor != null)
            	extractor.close();
        }
	}
	
	public static int getPages(File f) throws IOException {
		ObjectExtractor extractor =null;
		try {
			//Carrega documento pdf e o extrator
			PDDocument pdfDocument = PDDocument.load(f);
	        return pdfDocument.getNumberOfPages();
		} finally {
            if (extractor != null)
            	extractor.close();
        }
	}
	
	public static Table getTable(File f, int page, Rectangle r) throws IOException{
		ObjectExtractor o2 =null;
		try {
			o2=new ObjectExtractor(PDDocument.load(f));
			Page pagina;
			AdvancedExtractionAlgorith bea = new AdvancedExtractionAlgorith();
	    	pagina = o2.extract(page).getArea(r);
	        return bea.extract(pagina).get(0);
        } finally {
            if (o2 != null)
            	o2.close();
        }
	}
	
	public static int getColumnsAmount(File f, int pageSelected){
		return 1;
	}
	
	public static Page getPage(String path, int pageNumber) throws IOException {
        ObjectExtractor oe = null;
        try {
            PDDocument document = PDDocument
                    .load(new File(path));
            oe = new ObjectExtractor(document);
            Page page = oe.extract(pageNumber);
            return page;
        } finally {
            if (oe != null)
                oe.close();
        }
    }
	
	public static List<Integer> getColumnsPerPage(File f){
		List<br.ufsm.inf.examclipper.model.Page> lPages = new ArrayList<>();
		ArrayList<Integer> arr = new ArrayList<Integer>();
		PDFConversor p = new PDFConversor(f, lPages);
		p.run();
		for(br.ufsm.inf.examclipper.model.Page page:lPages) {
			try {
				// Cria um arquivo temporario contendo o jpg
				Mat dst = new Mat(), cdst = new Mat(), cdstP;
				File image = File.createTempFile("imageManipulationJava", "jpg");
				ImageIO.write(page.getBufferedImage(), "jpg", image);
				// Le o arquivo com tons de cinza
				Mat mat = Imgcodecs.imread(image.getAbsolutePath(),Imgcodecs.IMREAD_GRAYSCALE);
				Integer aux=1;
				Imgproc.Canny(mat, dst, 50, 200, 3, false);
		        Imgproc.cvtColor(mat, cdst, Imgproc.COLOR_GRAY2BGR);
		        cdstP = cdst.clone();
		        /*
		        Mat lines = new Mat();
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
		        */
		        // Execução do arquivo de detecção de linhas probabilisto mas que funciona melhor q o normal
		        Mat linesP = new Mat();
		        Imgproc.HoughLinesP(dst, linesP,1,Math.PI,100,400,5);
		        for (int x = 0; x < linesP.rows(); x++) {
		            double[] l = linesP.get(x, 0);
		            Point pt1 = new Point(l[0], l[1]);
		            Point pt2 = new Point(l[2], l[3]);
		            Imgproc.line(cdstP, pt1, pt2, new Scalar(0, 0, 255), 1, Imgproc.LINE_AA, 0);
		            aux++;
		        }
		        if(aux>0)aux=(aux)/2 + 1;
		        else aux=0;
				/*
		        BufferedImage imgSave = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
				byte[] data = ((DataBufferByte) imgSave.getRaster().getDataBuffer()).getData();
				cdstP.get(0, 0, data);
				image=new File("B:/Java/imagem"+(k++)+".jpg");
				ImageIO.write(imgSave, "jpg", image);
		        */
				arr.add(aux);
			} catch (Exception e) {
				// e.printStackTrace();
				arr.add(1);
			}
		}
		return arr;
	}
	
	@SuppressWarnings("rawtypes")
	public static String[][] getArrayFromTable(Table table){
		List<List<RectangularTextContainer>> tableRows = table.getRows();

        int maxColCount = 0;

        for (int i = 0; i < tableRows.size(); i++) {
            List<RectangularTextContainer> row = tableRows.get(i);
            if (maxColCount < row.size()) {
                maxColCount = row.size();
            }
        }
        String[][] rv = new String[tableRows.size()][maxColCount];
        for (int i = 0; i < tableRows.size(); i++) {
            List<RectangularTextContainer> row = tableRows.get(i);
            for (int j = 0; j < row.size(); j++) {
                rv[i][j] = table.getCell(i, j).getText();
            }
        }
        return rv;
	}
}
