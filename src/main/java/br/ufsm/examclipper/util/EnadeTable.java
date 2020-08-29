package br.ufsm.examclipper.util;

import java.io.File;
import java.io.IOException;

import technology.tabula.Rectangle;
import technology.tabula.Table;

public class EnadeTable {

	private int type,page;
	private File f;
	private Rectangle r;
	
	
	public EnadeTable(File f,Rectangle r,int page) {
		this.r=r;
		this.f=f;
		this.page=page;
		try {
			this.type=getTypeTable(PdfManager.getTable(f, page, r));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String[][] extract() throws Exception{
		return EnadeExtractorPDF.getTable(f, page, r,type);
	}
	
	private static boolean isStringInt(String s)
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
	
	private static int getTypeTable(Table t) {
		if(t.getCell(0, 0).getText().equalsIgnoreCase("Conceito ENADE"))return 1;
		if(t.getCell(0, 0).getText().equalsIgnoreCase("ENADE"))return 2;
		if(t.getCell(1, 1).getText().equalsIgnoreCase("AGRUPAMENTO"))return 3;
		if(t.getCell(0, 0).getText().equalsIgnoreCase("Questão") && t.getCell(1, 5).getText().equalsIgnoreCase("Adm."))return 4;
		if(t.getCell(0, 1).getText().equalsIgnoreCase("Questão")&& t.getCell(1, 6).getText().equalsIgnoreCase("Adm."))return 5;
		if(t.getCell(0, 0).getText().equalsIgnoreCase("Questão")&& t.getCell(1, 1).getText().equalsIgnoreCase("Curso"))return 6;
		if(t.getCell(0, 1).getText().equalsIgnoreCase("Curso")&& 
				(t.getCell(2, 1).getText().equalsIgnoreCase("Média")||t.getCell(2, 2).getText().equalsIgnoreCase("Média")))return 7;
		for(int x=1;x<t.getRowCount();x++) {
			if(isStringInt(t.getCell(x, 0).getText()))
				return 8;
		}
		
		return 0;
	}
	
	
}
