package br.ufsm.examclipper.util;

import java.io.File;
import java.util.ArrayList;

import technology.tabula.Rectangle;
import technology.tabula.Table;

public class EnadeExtractorPDF {
	

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
	
	
	

	public static String[][] getTable(File f,int page, Rectangle r,int type) throws Exception {
		Table t=PdfManager.getTable(f, page, r);
		Table newT= Table.empty();
		if(type!=0)System.out.println("Pagina: "+page+" - Type: "+type);
		switch(type) {
		case 8:{
			String [][] mat=null;
			int mx=1;
			int my=0;
			
			for(int x=1;x<t.getRowCount();x++) {
				if(mat==null&&!t.getCell(x, 0).getText().isBlank()) {
					if(isStringInt(t.getCell(x, 0).getText())) {
						int aux = 0;
						for(int y= 0;y<t.getColCount();y++) {
							String[] saux=t.getCell(x, y).getText().split(" ");
							if(saux.length==1 && saux[0].isBlank())continue;
							aux+=saux.length;
						}
						mat= new String[t.getRowCount()-x+1][aux];
						for(int ax=0;ax<mat.length;ax++)
							for(int ay=0;ay<mat[0].length;ay++)
								mat[ax][ay]="";
						
					}
				}
				if(mat!=null) {
					my=0;
					for(int y= 0;y<t.getColCount();y++) {
						String[] saux=t.getCell(x, y).getText().split(" ");
						for(String s:saux) {
							if(s.isBlank())continue;
							mat[mx][my++]=s;
						}
					}
					mx++;
				}
			}
			switch(mat[0].length) {
			case 14:
				mat[0][0]="Questão";
				mat[0][1]="Curso";
				mat[0][2]="UF";
				mat[0][3]="Região";
				mat[0][4]="Cat. Adm.";
				mat[0][5]="Org. Acad.";
				mat[0][6]="Brasil";
				mat[0][7]="Gabarito";
				mat[0][8]="A";
				mat[0][9]="B";
				mat[0][10]="C";
				mat[0][11]="D";
				mat[0][12]="E";
				mat[0][13]="SI";
				break;
			case 13:
				mat[0][0]="Questão";
				mat[0][1]="Média";
				mat[0][2]="Desvio Padrão";
				mat[0][3]="Média";
				mat[0][4]="Desvio Padrão";
				mat[0][5]="Média";
				mat[0][6]="Desvio Padrão";
				mat[0][7]="Média";
				mat[0][8]="Desvio Padrão";
				mat[0][9]="Média";
				mat[0][10]="Desvio Padrão";
				mat[0][11]="Média";
				mat[0][12]="Desvio Padrão";
				break;
			case 10:
				mat[0][0]="Questão";
				mat[0][1]="A";
				mat[0][2]="B";
				mat[0][3]="C";
				mat[0][4]="D";
				mat[0][5]="E";
				mat[0][6]="F";
				mat[0][7]="G";
				mat[0][8]="H";
				mat[0][9]="SI*";
				break;
			case 9:
				mat[0][0]="Questão";
				mat[0][1]="Discordo Totalmente";
				mat[0][2]="Discordo";
				mat[0][3]="Discordo Parcialmente";
				mat[0][4]="Concordo Parcialmente";
				mat[0][5]="Concordo";
				mat[0][6]="Concordo Totalmente";
				mat[0][7]="Não sei responder/Não se aplica";
				mat[0][8]="SI*";
				break;
			default:
				break;
			}
			return mat;
			}
		case 7:{
			String[][] mat = null;
			int mx=2;
			
			for(int x=2;x<t.getRowCount();x++) {
				if(mat==null&&!t.getCell(x, 0).getText().isBlank()) {
					if(isStringInt(t.getCell(x, 0).getText())) {
						mat=new String[t.getRowCount()-x+2][13];
						for(int ax=0;ax<mat.length;ax++)
							for(int ay=0;ay<mat[0].length;ay++)
								mat[ax][ay]="";
						mat[0][1]="Curso";
						mat[0][3]="UF";
						mat[0][5]="Região";
						mat[0][7]="Cat. Adm.";
						mat[0][9]="Org. Acad.";
						mat[0][11]="Brasil";
						for(int z=1;z<13;z+=2) {
							mat[1][z]="Média";
							mat[1][z+1]="Desvio Padrão";
						}
					}
				}
				if(mat!=null) {
					int my=0;
					mat[mx][my++]=t.getCell(x, 0).getText();
					for(int y=1;y<t.getColCount();y++) {
						String text=t.getCell(x, y).getText();
						String splited[]=text.split(" ");
						for(int z=0;z<splited.length;z++) {
							mat[mx][my++]=splited[z];							
						}
					}
					mx++;
				}
			}
			return mat;
			}
		case 6:{
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
			throw new Exception("Tipo de tabela não encontrado!!");
		}
		return PdfManager.getArrayFromTable(t);
	}
}
