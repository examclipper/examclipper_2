package br.ufsm.examclipper.test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.pdfbox.pdmodel.PDDocument;

import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.Rectangle;
import technology.tabula.Table;
import technology.tabula.detectors.NurminenDetectionAlgorithm;
import technology.tabula.extractors.BasicExtractionAlgorithm;

public class TestTableDetection {

    private static int numTests = 0;
    private static int numPassingTests = 0;
    private static int totalExpectedTables = 0;
    private static int totalCorrectlyDetectedTables = 0;
    private static int totalErroneouslyDetectedTables = 0;

    private static Level defaultLogLevel;

    private static final class TestStatus {
        public int numExpectedTables;
        public int numCorrectlyDetectedTables;
        public int numErroneouslyDetectedTables;
        public boolean expectedFailure;

        private transient boolean firstRun;
        private transient String pdfFilename;

        public TestStatus(String pdfFilename) {
            this.numExpectedTables = 0;
            this.numCorrectlyDetectedTables = 0;
            this.expectedFailure = false;
            this.pdfFilename = pdfFilename;
        }

        public static TestStatus load(String pdfFilename) {
            TestStatus status;

            try {
                FileReader f = new FileReader(new File(jsonFilename(pdfFilename)));
                status = new Gson().fromJson(f, TestStatus.class);
                status.pdfFilename = pdfFilename;
            } catch (IOException ioe) {
                status = new TestStatus(pdfFilename);
                status.firstRun = true;
            }

            return status;
        }

        public void save() {
            try (FileWriter w = new FileWriter(jsonFilename(this.pdfFilename))) {
                Gson gson = new Gson();
                w.write(gson.toJson(this));
                w.close();
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        public boolean isFirstRun() {
            return this.firstRun;
        }

        private static String jsonFilename(String pdfFilename) {
            return pdfFilename.replace(".pdf", ".json");
        }
    }

    public static Collection<Object[]> data() {
        String[] regionCodes = {"eu", "us"};

        ArrayList<Object[]> data = new ArrayList<>();

        for (String regionCode : regionCodes) {
            String directoryName = "src/test/resources/technology/tabula/icdar2013-dataset/competition-dataset-" + regionCode + "/";
            File dir = new File(directoryName);

            File[] pdfs = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".pdf");
                }
            });

            for (File pdf : pdfs) {
                data.add(new Object[] {pdf});
            }
        }

        return data;
    }

    private File pdf;
    private DocumentBuilder builder;
    private TestStatus status;

    private int numCorrectlyDetectedTables = 0;
    private int numErroneouslyDetectedTables = 0;

    public TestTableDetection(File pdf) {
        this.pdf = pdf;
        this.status = TestStatus.load(pdf.getAbsolutePath());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            this.builder = factory.newDocumentBuilder();
        } catch (Exception e) {
            // ignored
        }
    }

    private void printTables(Map<Integer, List<Rectangle>> tables) {
        for (Integer page : tables.keySet()) {
            System.out.println("Page " + page.toString());
            for (Rectangle table : tables.get(page)) {
                System.out.println(table);
            }
        }
    }

    public void testDetectionOfTables() throws Exception {
        numTests++;

        // xml parsing stuff for ground truth

        // tabula extractors
        PDDocument pdfDocument = PDDocument.load(this.pdf);
        ObjectExtractor extractor = new ObjectExtractor(pdfDocument);

        // now find tables detected by tabula-java
        Map<Integer, List<Rectangle>> detectedTables = new HashMap<>();

        // the algorithm we're going to be testing
        NurminenDetectionAlgorithm detectionAlgorithm = new NurminenDetectionAlgorithm();

        PageIterator pages = extractor.extract();
        while (pages.hasNext()) {
            Page page = pages.next();
            List<Rectangle> tablesOnPage = detectionAlgorithm.detect(page);
            if (tablesOnPage.size() > 0) {
                detectedTables.put(new Integer(page.getPageNumber()), tablesOnPage);
            }
        }
        extractor.close();
        ObjectExtractor o2 = new ObjectExtractor(PDDocument.load(this.pdf));
        for (Map.Entry<Integer, List<Rectangle>> entry : detectedTables.entrySet()) {
            for(Rectangle r: entry.getValue()) {
                System.out.println(entry.getKey() + "/" + r);
	            Page page;
	            try {
	            	BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();
	    	        page = o2.extract(entry.getKey()).getArea(r);
	    	        Table table = bea.extract(page).get(0);
	    	        String[][] mat = UtilsForTesting.tableToArrayOfRows(table);
	    	        for(int x=0;x<mat.length;x++) {
	    	        	for(int y=0;y<mat[x].length;y++) {
	    	        		System.out.print(mat[x][y]+((y!=mat[x].length-1)?" | ":""));
	    	        	}
	    	        	System.out.println();
	    	        }
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}

	        	System.out.println("-----------------------------------");
	        	System.out.println();
            }
        }
        o2.close();

		
        
    }
}