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

//import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.csv.Format;
import org.openspcoop2.utils.csv.FormatReader;
import org.openspcoop2.utils.csv.Printer;
import org.openspcoop2.web.monitor.core.constants.CostantiGrafici;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.datatable.DataTable;
import be.quodlibet.boxable.line.LineStyle;

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

	// Utilities per gli export in xls

	public static XSSFFont createHeaderFont(XSSFWorkbook workbook) {
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setColor(IndexedColors.BLACK.getIndex());
		headerFont.setFontHeight(10);
		return headerFont;
	}

	public static XSSFFont createCellFont(XSSFWorkbook workbook) {
		XSSFFont headerFont = workbook.createFont();
		short blackIndex = IndexedColors.BLACK.getIndex();
		headerFont.setColor(blackIndex);
		headerFont.setFontHeight(10);
		return headerFont;
	}

	public static XSSFCellStyle createHeaderStyle(XSSFWorkbook workbook) {
		XSSFCellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerCellStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
		headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerCellStyle.setFont(createHeaderFont(workbook));
		// bordo
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);

		short blackIndex = IndexedColors.BLACK.getIndex();
		headerCellStyle.setTopBorderColor(blackIndex);
		headerCellStyle.setRightBorderColor(blackIndex);
		headerCellStyle.setBottomBorderColor(blackIndex);
		headerCellStyle.setLeftBorderColor(blackIndex);

		return headerCellStyle;
	}

	public static XSSFCellStyle createCellStyle(XSSFWorkbook workbook) {
		XSSFCellStyle cellStyle = workbook.createCellStyle();
        DataFormat dataFormat = workbook.createDataFormat();
        cellStyle.setDataFormat(dataFormat.getFormat("@"));
		cellStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setFont(createCellFont(workbook));
		return cellStyle;
	}

	public static void autoSizeColumn(XSSFWorkbook workbook) {
		for (Sheet sheet : workbook) {
			if (sheet.getPhysicalNumberOfRows() > 0) {
				org.apache.poi.ss.usermodel.Row row = sheet.getRow(sheet.getFirstRowNum());
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					int columnIndex = cell.getColumnIndex();
					sheet.autoSizeColumn(columnIndex);
				}
			}
		}
	}
	
	public static void writeDataIntoXls(List<List<String>> dati, List<String> labelColonna, XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.createSheet("Report"); // Creazione di un nuovo foglio
		
		// creazione header
		XSSFRow header = sheet.createRow(0);
		
		XSSFCellStyle headerStyle = Templates.createHeaderStyle(workbook);
		XSSFCellStyle cellStyle = Templates.createCellStyle(workbook);
		
		for (int i = 0; i < labelColonna.size(); i++) {
			String label = labelColonna.get(i);
			XSSFCell headerCell = header.createCell(i);
			headerCell.setCellStyle(headerStyle);
			headerCell.setCellValue(label);
			headerCell.setCellType(CellType.STRING);
		}
		
		// inserimento dati nelle righe
		for (int i = 0; i < dati.size(); i++) {
			List<String> riga = dati.get(i);
			XSSFRow row = sheet.createRow((i+1));
				
			for (int j = 0; j < riga.size(); j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(cellStyle);
		        cell.setCellValue(riga.get(j));
		        cell.setCellType(CellType.STRING);
			}
		}
		
		// autosize colonne
		Templates.autoSizeColumn(workbook);
	}
	
	// Utilities per gli export in csv
	
	private static Properties propertiesWriterCsv;
	
	public static Properties getPropertiesWriterCsv() {
		if(propertiesWriterCsv == null) {
			init();
		}
		
		return propertiesWriterCsv;
	}
	
	private static synchronized void init() {
		if(propertiesWriterCsv == null) {
			propertiesWriterCsv = new Properties();
			
			propertiesWriterCsv.put(FormatReader.CSV_FORMAT, "Default");
			propertiesWriterCsv.put(FormatReader.CSV_COMMENT_MARKER, "#");
			propertiesWriterCsv.put(FormatReader.CSV_DELIMITER, ",");
			propertiesWriterCsv.put(FormatReader.CSV_WITH_HEADER, false);
			propertiesWriterCsv.put(FormatReader.CSV_WITH_IGNORE_EMPTY_LINES, true);
			propertiesWriterCsv.put(FormatReader.CSV_WITH_IGNORE_SURROUNDING_SPACES, true);
			propertiesWriterCsv.put(FormatReader.CSV_WITH_NULL_STRING, "");
			propertiesWriterCsv.put(FormatReader.CSV_SKIP_EMPTY_RECORD, true);
		}
	}
	
	public static Format getFormat() throws UtilsException {
		FormatReader formatWriter = new FormatReader(Templates.getPropertiesWriterCsv());
		return formatWriter.getFormat();
	}
	
	public static Printer getPrinter(OutputStream outputStream) throws UtilsException {
		return new Printer(getFormat(), outputStream);
	}
	
	public static void writeDataIntoCsv(List<List<String>> dati, List<String> labelColonna, Printer printer) throws UtilsException {
		
		//Intestazione
		printer.printRecord(labelColonna);
		
		// record dati
		for (List<String> list : dati) {
			printer.printRecord(list);
		}
	}
}
