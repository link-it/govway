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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;

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
    public static final float PDF_TITLE_FONT_SIZE = 20;
    public static final float PDF_SUBTITLE_FONT_SIZE = 16;
    public static final String PDF_LINKIT_URL = "https://link.it";
    
    public static final com.itextpdf.kernel.colors.Color TABLE_HEADER_BACKGROUND_COLOR = new DeviceRgb(192, 192, 192);
    public static final com.itextpdf.kernel.colors.Color TABLE_ODD_ROWS_BACKGROUND_COLOR = new DeviceRgb(240, 240, 240);
    public static final com.itextpdf.kernel.colors.Color BORDER_COLOR_BLACK = new DeviceRgb(0, 0, 0);
    public static final Border SOLID_BORDER_BLACK_1F = new SolidBorder(BORDER_COLOR_BLACK,1f);

	/**
	 * Creates custom component which is possible to add to any report band component
	 * @throws IOException 
	 */
	public static void createTitleComponent(String titoloReport, String periodoOsservazione, Document document) throws IOException {
		
		// Titolo Report BOLD 20
		PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		Text title = new Text(titoloReport).setFont(bold).setFontSize(PDF_TITLE_FONT_SIZE);
		Paragraph p = new Paragraph().add(title);
		document.add(p);
		
		// Periodo osservazione BOLD 16
		Text subtitle = new Text(periodoOsservazione).setFont(bold).setFontSize(PDF_SUBTITLE_FONT_SIZE);
		Paragraph pSubtitle = new Paragraph().add(subtitle);
		document.add(pSubtitle);
		
		// link
		PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		Text linkit = new Text(PDF_LINKIT_URL).setFont(normal).setFontSize(PDF_TEXT_FONT_SIZE);
		Paragraph pLinkit = new Paragraph().add(linkit);
		pLinkit.setBorderBottom(SOLID_BORDER_BLACK_1F);
		document.add(pLinkit);
		
		Text spazio = new Text(" ").setFont(normal).setFontSize(PDF_SPACE_TEXT_FONT_SIZE);
		Paragraph pSpazio = new Paragraph().add(spazio);
		document.add(pSpazio);
		
	}
	
	public static Table createTableComponent(List<Colonna> colonne, List<List<String>> dati) throws IOException {
		PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		Table table = new Table(colonne.size(), true).useAllAvailableWidth();
		
		for (int i = 0; i < colonne.size(); i++) {
			Colonna colonna = colonne.get(i);
			
			Cell headerCell = new Cell();
			headerCell.setHorizontalAlignment(colonna.getAlignment());
			headerCell.setTextAlignment(TextAlignment.CENTER);
			headerCell.setFont(bold);
			headerCell.setFontSize(8);
			headerCell.add(new Paragraph(colonna.getLabel()));
			headerCell.setBackgroundColor(TABLE_HEADER_BACKGROUND_COLOR);
			table.addHeaderCell(headerCell);
		}
		
		for (int i = 0; i < dati.size(); i++) {
			List<String> riga = dati.get(i);
			for (int j = 0; j < riga.size(); j++) {
				String cella = riga.get(j);
				
				Cell bodyCell = new Cell();
				bodyCell.setHorizontalAlignment(colonne.get(j).getAlignment()); // alignment dall'header corrispondente
				bodyCell.setTextAlignment(TextAlignment.CENTER);
				bodyCell.setFont(normal);
				bodyCell.setFontSize(PDF_TABLE_FONT_SIZE);
				bodyCell.setBorder(Border.NO_BORDER);
				
				if(i == 0) { // bordo superiore prima riga
					bodyCell.setBorderTop(SOLID_BORDER_BLACK_1F);
				}
				
				if(i % 2 == 0) { // righe dispari
					bodyCell.setBackgroundColor(TABLE_ODD_ROWS_BACKGROUND_COLOR);
				}
				
				bodyCell.add(new Paragraph(cella));
				table.addCell(bodyCell);
			}
		}
		return table;
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
