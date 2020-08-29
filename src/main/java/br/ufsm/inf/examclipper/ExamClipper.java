package br.ufsm.inf.examclipper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import br.ufsm.examclipper.util.EnadeTable;
import br.ufsm.examclipper.util.PdfManager;
import br.ufsm.examclipper.util.PrintTextLocations;
import technology.tabula.Rectangle;

public class ExamClipper {
	
	private static final String arquivo = "B:/Java/enade2.pdf";
	
	public static void main(String[] args) throws FileNotFoundException {
		File f=new File(arquivo);
		File log = new File("B:/Java/log.txt");
		PrintStream out = new PrintStream(log);
		System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
		try {
			Map<Integer, List<Rectangle>> map =PdfManager.findTables(f);
			for(Map.Entry<Integer, List<Rectangle>> entry: map.entrySet()) {
				for(Rectangle r:entry.getValue()) {
					try {
						EnadeTable e = new EnadeTable(f, r, entry.getKey());
						out.println("Página: "+entry.getKey());
						String[][] mat = e.extract(); 
						for(int x=0;x<mat.length;x++) {
		    	        	for(int y=0;y<mat[x].length;y++) {
		    	        		out.print(mat[x][y]+((y!=mat[x].length-1)?" | ":""));
		    	        	}
		    	        	out.println();
		    	        }
						out.println();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
    	        	/*
    			    PDFTextStripperByArea stripper = new PDFTextStripperByArea();
    			    stripper.addRegion("texto", r);
    			    stripper.setSortByPosition( true );
    			    OutputStreamWriter dummy = new OutputStreamWriter(new ByteArrayOutputStream());
    			    stripper.extractRegions(PDDocument.load(f).getPage(entry.getKey()-1));
    			    out.println("Texto da area: "+ stripper.getTextForRegion("texto"));
    			    */
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			PrintTextLocations.test(arquivo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.close();
		
		
		//ExamClipperGUI gui = new ExamClipperGUI();
		//gui.setVisible(true);
	}
}
 