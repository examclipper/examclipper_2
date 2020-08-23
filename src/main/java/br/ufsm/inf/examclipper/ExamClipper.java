package br.ufsm.inf.examclipper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import br.ufsm.examclipper.util.PdfManager;
import br.ufsm.examclipper.util.PrintTextLocations;
import technology.tabula.Rectangle;
import technology.tabula.Table;

public class ExamClipper {
	
	private static final String arquivo = "B:/Java/enade.pdf";

	
	
	public static void main(String[] args) throws FileNotFoundException {
		/*File f=new File(arquivo);
		File log = new File("B:/Java/log.txt");
		PrintStream out = new PrintStream(log);
		System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
		try {
			Map<Integer, List<Rectangle>> map =PdfManager.findTables(f);
			for(Map.Entry<Integer, List<Rectangle>> entry: map.entrySet()) {
				for(Rectangle r:entry.getValue()) {
					Table t=PdfManager.getTable(f, entry.getKey(), r);
					out.println("Página: "+entry.getKey());
					String[][] mat = PdfManager.getArrayFromTable(t);
					for(int x=0;x<mat.length;x++) {
	    	        	for(int y=0;y<mat[x].length;y++) {
	    	        		out.print(mat[x][y]+((y!=mat[x].length-1)?" | ":""));
	    	        	}
	    	        	out.println();
	    	        }
					out.println();
    	        	

    			    PDFTextStripperByArea stripper = new PrintTextLocations();
    			    stripper.addRegion("texto", r);
    			    stripper.setSortByPosition( true );
    			    OutputStreamWriter dummy = new OutputStreamWriter(new ByteArrayOutputStream());
    			    stripper.extractRegions(PDDocument.load(f).getPage(entry.getKey()-1));
    			    out.println("Texto da area: "+ stripper.getTextForRegion("texto"));
    			    
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
		*/
		
		
		ExamClipperGUI gui = new ExamClipperGUI();
		gui.setVisible(true);
	}
}
 