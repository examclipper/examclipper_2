package br.ufsm.inf.examclipper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import br.ufsm.examclipper.util.PdfManager;

public class ExamClipper {
	
	private static final String arquivo = "B:/Java/cc2017.pdf";
	
	
	private static void test() throws FileNotFoundException{
		File f=new File(arquivo);
		System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
		
		List<Integer> arr=PdfManager.getColumnsPerPage(f);
		for(int x=0;x<arr.size();x++) {
			System.out.println(x+"-"+arr.get(x));
		}
	}
	
	
	public static void main(String[] args) throws FileNotFoundException  {
		test();
		//ExamClipperGUI gui = new ExamClipperGUI();
		//gui.setVisible(true);
	}
}
 