package br.ufsm.examclipper.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;

import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.Rectangle;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.detectors.NurminenDetectionAlgorithm;
import technology.tabula.extractors.BasicExtractionAlgorithm;

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
