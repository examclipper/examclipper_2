package br.ufsm.inf.examclipper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.ufsm.examclipper.util.PdfManager;
import br.ufsm.examclipper.util.PrintTextLocations;
import technology.tabula.Rectangle;
import technology.tabula.Table;

public class ExamClipper {
	
	private static final String arquivo = "B:/Java/enade3.pdf";

	private static int getTypeTable(Table t) {
		if(t.getCell(0, 0).getText().equalsIgnoreCase("Conceito ENADE"))return 1;
		if(t.getCell(0, 0).getText().equalsIgnoreCase("ENADE"))return 2;
		if(t.getCell(1, 1).getText().equalsIgnoreCase("AGRUPAMENTO"))return 3;
		if((t.getCell(0, 0).getText().equalsIgnoreCase("Questão") && t.getCell(1, 5).getText().equalsIgnoreCase("Adm.")))return 4;
		if((t.getCell(0, 1).getText().equalsIgnoreCase("Questão")&& t.getCell(1, 6).getText().equalsIgnoreCase("Adm.")))return 5;
		if((t.getCell(0, 0).getText().equalsIgnoreCase("Questão")&& t.getCell(1, 1).getText().equalsIgnoreCase("Curso")))return 6;
		return 0;
	}
	public static boolean isStringInt(String s)
	{
	    try
	    {
	        Integer.parseInt(s);
	        return true;
	    } catch (NumberFormatException ex)
	    {
	        return false;
	    }
	}
	
	private static String[][] getTable(File f,int page, Rectangle r) throws Exception {
		Table t=PdfManager.getTable(f, page, r);
		Table newT= Table.empty();

		switch(getTypeTable(t)) {
		case 6:{
			System.out.println("Pagina: "+page);
			int start=0;
			for(int x=1;x<t.getRowCount();x++) {
				if(isStringInt(t.getCell(x, 0).getText())) {
					start=x;
					break;
				}
			}
			if(start==0)break; 
			String arr[][] = new String[t.getRowCount()-start+1][t.getColCount()];
			for(int x=0;x<t.getColCount();x++) {
				String col = "";
				for(int y=0;y<start;y++) {
					String s=t.getCell(y, x).getText();
					if(s.isBlank())continue;
					col+=s+" ";
				}
				arr[0][x]=col;
			}
			for(int x=0;x<t.getRowCount()-start;x++) {
				for(int y=0;y<t.getColCount();y++) {
					arr[x+1][y] = t.getCell(x+start, y).getText();
				}
			}
			return arr;
			}
		case 5:{
			ArrayList<String[]> ar = new ArrayList<String[]>();
			ArrayList<String> quests = new ArrayList<String>();
			String text = "";
			for(int x=2;x<t.getRowCount();x++) {
				if(!t.getCell(x, 2).getText().isBlank()) {
					if(t.getCell(x, 3).getText().isBlank()) {
						ar.get(ar.size()-1)[1]+=t.getCell(x, 3).getText();
					}else {
						String[] arr = new String[t.getColCount()-2];
						for(int y=2;y<t.getColCount();y++)
							arr[y-2]=t.getCell(x, y).getText();
						ar.add(arr);
					}
				}
				if(!t.getCell(x, 0).getText().isBlank()) {
					String trab=t.getCell(x, 0).getText()+" "+t.getCell(x, 1).getText();
					if(trab.charAt(trab.length()-1)=='-')text+=trab.substring(0,trab.length()-2);
					else text+=t.getCell(x, 0).getText()+" ";
				}else if(!text.isBlank()){
					quests.add(text);
					text="";
				}
			}
			String mat[][] = new String[ar.size()][ar.get(0).length+1];
			for(int x=0;x<ar.size();x++) {
				String aux[] = ar.get(x);
				for(int y=0;y<aux.length;y++) {
					mat[x][y+1]=aux[y];
				}
				if(x%5==0 && quests.size()> x/5) {
					mat[x][0] = quests.get(x/5);
				}else {
					mat[x][0] = "";
				}
			}
			return mat;}
		case 4:{
			ArrayList<String[]> ar = new ArrayList<String[]>();
			ArrayList<String> quests = new ArrayList<String>();
			String text = "";
			for(int x=2;x<t.getRowCount();x++) {
				if(!t.getCell(x, 1).getText().isBlank()) {
					String[] arr = new String[t.getColCount()-1];
					for(int y=1;y<t.getColCount();y++)
						arr[y-1]=t.getCell(x, y).getText();
					ar.add(arr);
				}
				if(!t.getCell(x, 0).getText().isBlank()) {
					String trab=t.getCell(x, 0).getText();
					if(trab.charAt(trab.length()-1)=='-')text+=trab.substring(0,trab.length()-2);
					else text+=t.getCell(x, 0).getText()+" ";
				}else if(!text.isBlank()){
					quests.add(text);
					text="";
				}
			}
			String mat[][] = new String[ar.size()][ar.get(0).length+1];
			for(int x=0;x<ar.size();x++) {
				String aux[] = ar.get(x);
				for(int y=0;y<aux.length;y++) {
					mat[x][y+1]=aux[y];
				}
				if(x%5==0 && quests.size()> x/5) {
					mat[x][0] = quests.get(x/5);
				}else {
					mat[x][0] = "";
				}
			}
			return mat;}
		case 3:
			String str[][] = new String[t.getRowCount()-3][t.getColCount()-1];
			for(int x=2;x<t.getColCount();x++) 
				for(int y=3;y<t.getRowCount();y++) {
					if(x-2==1) {
						String s[]=t.getCell(y,x).getText().split(" ");
						str[y-3][1]=s[0];
						str[y-3][2]=s[1];
					}else if(x-2==2)str[y-3][x-1]=t.getCell(y,x).getText();	
					else str[y-3][x-2]=t.getCell(y,x).getText();
				}
			return str;
		case 2:
			for(int x=1;x<t.getColCount();x++) 
				for(int y=1;y<t.getRowCount();y++)
					newT.add(t.getCell(y, x), y-1, x-1);
			return PdfManager.getArrayFromTable(newT);
		case 1:
		case 0:
			//throw new Exception("Tipo de tabela não encontrado!!");
		}
		return PdfManager.getArrayFromTable(t);
	}
	
	
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
						out.println("Página: "+entry.getKey());
						String[][] mat =getTable(f, entry.getKey(), r); 
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
		
		
		//ExamClipperGUI gui = new ExamClipperGUI();
		//gui.setVisible(true);
	}
}
 