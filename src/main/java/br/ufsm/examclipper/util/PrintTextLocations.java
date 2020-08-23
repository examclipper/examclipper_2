package br.ufsm.examclipper.util;

import java.io.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;

import java.util.List;

public class PrintTextLocations extends PDFTextStripperByArea {

    public PrintTextLocations() throws IOException {
        super.setSortByPosition(true);
    }

    public static void test(String path) throws Exception {

        PDDocument document = null;
        try {
            File input = new File(path);
            document = PDDocument.load(input);
            if (document.isEncrypted()) {
            	throw new Exception("Documento encriptado!");
            }
            PrintTextLocations printer = new PrintTextLocations();
            printer.setSortByPosition(true);
            printer.setStartPage(0);
            printer.setEndPage(document.getNumberOfPages());
            
            OutputStreamWriter dummy = new OutputStreamWriter(new ByteArrayOutputStream());
            printer.writeText(document, dummy);

            
            PDPageTree allPages = document.getDocumentCatalog().getPages();
            printer.processPages(allPages);
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }
	PrintStream out = new PrintStream(new File("B:/Java/log2.txt"));
    
    @Override
    protected void writeString(String str, List<TextPosition> textPositions) throws IOException {
    	super.writeString(str, textPositions);
    	for (TextPosition text : textPositions) {
            out.println(text.getUnicode()+ " [(X=" + text.getXDirAdj() + ",Y=" +
                    text.getYDirAdj() + ") height=" + text.getHeightDir() + " width=" +
                    text.getWidthDirAdj() + "]");
        }
    }
   
    
    

    /**
     * @param text The text to be processed
     */
    
    @Override
    protected void processTextPosition(TextPosition text) {
    	super.processTextPosition(text);
    }
}