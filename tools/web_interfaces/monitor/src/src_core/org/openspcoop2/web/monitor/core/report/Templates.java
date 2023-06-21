/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.web.monitor.core.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.openspcoop2.web.monitor.core.constants.CostantiGrafici;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.datatable.DataTable;
import be.quodlibet.boxable.line.LineStyle;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.jasperreports.engine.JRException;

/**
 * Templates
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class Templates {
	
	private Templates(){}
	
	// Costanti per i report in formato xls e csv
	public static final StyleBuilder rootStyle;
	public static final StyleBuilder columnStyle;
	public static final StyleBuilder columnTitleStyle;
	public static final FontBuilder rootFont;
	
	static {
		rootFont = stl.font().setFontSize(8);
		rootStyle           = stl.style().setPadding(2).setFont(rootFont); 
		columnStyle         = stl.style(rootStyle).setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
		columnTitleStyle    = stl.style(columnStyle)
				.setBorder(stl.pen1Point())
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
				.setBackgroundColor(Color.LIGHT_GRAY)
				.bold();
	}
	
	// Costanti per i report in formato pdf
    public static final float MARGIN = 10;
    public static final float PDF_SPACE_TEXT_FONT_SIZE = 6;
    public static final float PDF_TEXT_FONT_SIZE = 8;
    public static final float PDF_TABLE_FONT_SIZE = 8;
    public static final float PDF_TITLE_FONT_SIZE = 18;
    public static final float PDF_SUBTITLE_FONT_SIZE = 14;
    public static final String PDF_LINKIT_URL = "https://govway.org";
    
    public static final Color TABLE_HEADER_BACKGROUND_COLOR = new Color(192, 192, 192);
    public static final Color TABLE_EVEN_ROWS_BACKGROUND_COLOR = new Color(255, 255, 255);
    public static final Color TABLE_ODD_ROWS_BACKGROUND_COLOR = new Color(240, 240, 240);
    public static final Color BORDER_COLOR_BLACK = new Color(0, 0, 0);
    public static final LineStyle SOLID_BORDER_BLACK_1F = new LineStyle(BORDER_COLOR_BLACK,1f);

	/**
	 * Creates custom component which is possible to add to any report band component
	 * @throws IOException 
	 */
	public static void createTitleComponent(String titoloReport, String periodoOsservazione, PDDocument document) throws IOException {
		
		// Fix: correggo periodo Osservazione
		if( (periodoOsservazione==null || StringUtils.isEmpty(periodoOsservazione)) &&
				titoloReport.contains(CostantiGrafici.DAL_PREFIX)){
			int indexOf = titoloReport.indexOf(CostantiGrafici.DAL_PREFIX);
			periodoOsservazione = titoloReport.substring(indexOf);
			titoloReport = titoloReport.substring(0,indexOf);
		}
		
		titoloReport = titoloReport.trim();
		if(periodoOsservazione!=null) {
			periodoOsservazione = periodoOsservazione.trim();
		}
		
		PDPage page = document.getPage(0);
		
		float width = page.getMediaBox().getWidth();
        float height = page.getMediaBox().getHeight();
		
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        
		// Titolo Report BOLD 20
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA_BOLD, PDF_TITLE_FONT_SIZE);
		contentStream.newLineAtOffset(MARGIN, height - 40);
		contentStream.showText(titoloReport);
		contentStream.endText();
				
		// Periodo osservazione BOLD 16
		contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, PDF_SUBTITLE_FONT_SIZE);
        contentStream.newLineAtOffset(MARGIN, height - 60);
        contentStream.showText(periodoOsservazione);
        contentStream.endText();
		
		// link
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, PDF_TEXT_FONT_SIZE);
        contentStream.newLineAtOffset(MARGIN, height - 110);
        contentStream.showText(PDF_LINKIT_URL);
        contentStream.endText();
        
        // linea
        float yLine = 110 + PDF_SPACE_TEXT_FONT_SIZE;
        contentStream.setLineWidth(1.5f);
        contentStream.moveTo(MARGIN, height - yLine); // Posizione iniziale della linea, considerando i margini
        contentStream.lineTo(width - MARGIN, height - yLine); // Posizione finale della linea, considerando i margini
        contentStream.stroke();
        contentStream.close(); 
		
	}
	
	@SuppressWarnings("rawtypes")
	private static List<List> convert(List<List<String>> src, List<Colonna> colonne){
		List<List> lReturnNull = null;
		if(src==null || src.isEmpty()) {
			return lReturnNull;
		}
		if(colonne==null || colonne.isEmpty()) {
			return lReturnNull;
		}
		List<List> data = new ArrayList<>();
		
		List<String> headers = new ArrayList<>();
		for (Colonna colonna : colonne) {
			headers.add(colonna.getLabel());
		}
		data.add(headers);
		
		for (List<String> srcList : src) {
			data.add(srcList);
		}
		
		return data;
	}

	public static void createTableComponent(List<Colonna> colonne, List<List<String>> dati, PDDocument document) throws IOException {
		
		if(colonne==null || colonne.isEmpty()) {
			throw new IOException("Colonne non fornite");
		}
		if(dati==null || dati.isEmpty()) {
			throw new IOException("Dati non forniti");
		}
		if(document==null) {
			throw new IOException("Document non fornito");
		}
		
		PDPage page = document.getPage(0);
		
		float height = page.getMediaBox().getHeight();
		
		float yStart =  height - 130;
		float startNewPageY = height -20;
		float bottomMargin = MARGIN;

		float tableWidth = (page.getMediaBox().getWidth() - (2 * MARGIN));
		
		BaseTable dataTable = new BaseTable(yStart, startNewPageY, bottomMargin, tableWidth, MARGIN, document, page, true, true);
		
		DataTable t = new DataTable(dataTable, page);
		t.addListToTable(convert(dati, colonne), DataTable.HASHEADER);
		
		int numeroColonne = colonne.size();
		float preferredColumWidth =  tableWidth / numeroColonne;
		
		for (int jColonna = 0; jColonna < dataTable.getHeader().getCells().size(); jColonna++) {
			
			Colonna colonna = colonne.get(jColonna);
			
			dataTable.getHeader().getCells().get(jColonna).setFillColor(TABLE_HEADER_BACKGROUND_COLOR);
			dataTable.getHeader().getCells().get(jColonna).setAlign(colonna.getAlignment());
			dataTable.getHeader().getCells().get(jColonna).setFontSize(PDF_TABLE_FONT_SIZE);
			dataTable.getHeader().getCells().get(jColonna).setWidth(preferredColumWidth);
			dataTable.getHeader().getCells().get(jColonna).setFont(PDType1Font.HELVETICA_BOLD);
		}
		
		for (int iRiga = 1; iRiga < dataTable.getRows().size(); iRiga++) {
						
			for (int jColonna = 0; jColonna < dataTable.getRows().get(iRiga).getCells().size(); jColonna++) {
				
				Colonna colonna = colonne.get(jColonna);
				
				if(iRiga % 2 == 0) { // righe pari
					dataTable.getRows().get(iRiga).getCells().get(jColonna).setFillColor(TABLE_EVEN_ROWS_BACKGROUND_COLOR);
					dataTable.getRows().get(iRiga).getCells().get(jColonna).setBorderStyle(new LineStyle(TABLE_EVEN_ROWS_BACKGROUND_COLOR, 0));
				}
				else {
					dataTable.getRows().get(iRiga).getCells().get(jColonna).setFillColor(TABLE_ODD_ROWS_BACKGROUND_COLOR);
					dataTable.getRows().get(iRiga).getCells().get(jColonna).setBorderStyle(new LineStyle(TABLE_ODD_ROWS_BACKGROUND_COLOR, 0));
				}
				dataTable.getRows().get(iRiga).getCells().get(jColonna).setAlign(colonna.getAlignment());
				dataTable.getRows().get(iRiga).getCells().get(jColonna).setFontSize(PDF_TABLE_FONT_SIZE);
				dataTable.getRows().get(iRiga).getCells().get(jColonna).setWidth(preferredColumWidth);
				dataTable.getRows().get(iRiga).getCells().get(jColonna).setFont(PDType1Font.HELVETICA);
			
			}
			
		}
		dataTable.draw();
		
	}
	
	public static List<List<String>> estraiDatiPerTabellaPdfDaDataSource(JasperReportBuilder report, List<Colonna> colonne) throws JRException {
		DRDataSource dataSource = (DRDataSource) report.getDataSource();
		
		
		List<List<String>> dati = new ArrayList<>();
		while (dataSource.next()) {
			List<String> riga = new ArrayList<>();
			for (Colonna colonna : colonne) {
				String name = colonna.getName();
				CustomJRField field = new CustomJRField(name);
				String fieldValue = (String) dataSource.getFieldValue(field);
				riga.add(fieldValue);
			}
			dati.add(riga);
		}
		return dati;
	}
}
