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

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.hyperLink;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.tableOfContentsCustomizer;
import static net.sf.dynamicreports.report.builder.DynamicReports.template;

import java.awt.Color;

import org.openspcoop2.web.monitor.core.bean.ApplicationBean;

import net.sf.dynamicreports.report.base.expression.AbstractValueFormatter;
import net.sf.dynamicreports.report.builder.HyperLinkBuilder;
import net.sf.dynamicreports.report.builder.ReportTemplateBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.datatype.BigDecimalType;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.tableofcontents.TableOfContentsCustomizerBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.definition.ReportParameters;

/**
 * Templates
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class Templates {
	private static final String TITOLO_REPORT = "Fruizione Servizi in Cooperazione Applicativa";
	public static final StyleBuilder rootStyle;
	public static final StyleBuilder boldStyle;
	public static final StyleBuilder italicStyle;
	public static final StyleBuilder boldCenteredStyle;
	public static final StyleBuilder bold12CenteredStyle;
	public static final StyleBuilder bold18CenteredStyle;
	public static final StyleBuilder bold22CenteredStyle;
	public static final StyleBuilder boldLeftStyle;
	public static final StyleBuilder bold12LeftStyle;
	public static final StyleBuilder bold18LeftStyle;
	public static final StyleBuilder bold22LeftStyle;
	public static final StyleBuilder columnStyle;
	public static final StyleBuilder columnTitleStyle;
	public static final StyleBuilder groupStyle;
	public static final StyleBuilder subtotalStyle;
	
	public static final FontBuilder categoryAxisFont;
	public static final FontBuilder rootFont;

	public static final ReportTemplateBuilder reportTemplate;
	public static final CurrencyType currencyType;
	public static final ComponentBuilder<?, ?> dynamicReportsComponent;
	public static final ComponentBuilder<?, ?> footerComponent;

	static {
		rootFont = stl.font().setFontSize(8);
		rootStyle           = stl.style().setPadding(2).setFont(rootFont); 
		boldStyle           = stl.style(rootStyle).bold();
		italicStyle         = stl.style(rootStyle).italic();
		boldCenteredStyle   = stl.style(boldStyle)
				.setTextAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.MIDDLE);
		bold12CenteredStyle = stl.style(boldCenteredStyle)
				.setFontSize(10);
		bold18CenteredStyle = stl.style(boldCenteredStyle)
				.setFontSize(16);
		bold22CenteredStyle = stl.style(boldCenteredStyle)
				.setFontSize(20);
		boldLeftStyle   = stl.style(boldStyle)
				.setTextAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.MIDDLE);
		bold12LeftStyle = stl.style(boldLeftStyle)
				.setFontSize(10);
		bold18LeftStyle = stl.style(boldLeftStyle)
				.setFontSize(16);
		bold22LeftStyle = stl.style(boldLeftStyle)
				.setFontSize(20);
		columnStyle         = stl.style(rootStyle).setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
		columnTitleStyle    = stl.style(columnStyle)
				.setBorder(stl.pen1Point())
				.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
				.setBackgroundColor(Color.LIGHT_GRAY)
				.bold();
		groupStyle          = stl.style(boldStyle)
				.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		subtotalStyle       = stl.style(boldStyle)
				.setTopBorder(stl.pen1Point());

		StyleBuilder crosstabGroupStyle      = stl.style(columnTitleStyle);
		StyleBuilder crosstabGroupTotalStyle = stl.style(columnTitleStyle)
				.setBackgroundColor(new Color(170, 170, 170));
		StyleBuilder crosstabGrandTotalStyle = stl.style(columnTitleStyle)
				.setBackgroundColor(new Color(140, 140, 140));
		StyleBuilder crosstabCellStyle       = stl.style(columnStyle)
				.setBorder(stl.pen1Point());

		TableOfContentsCustomizerBuilder tableOfContentsCustomizer = tableOfContentsCustomizer()
				.setHeadingStyle(0, stl.style(rootStyle).bold());

		reportTemplate = template()
				.setLocale(ApplicationBean.getInstance().getLocale())
				.setColumnStyle(columnStyle)
				.setColumnTitleStyle(columnTitleStyle)
				.setGroupStyle(groupStyle)
				.setGroupTitleStyle(groupStyle)
				.setSubtotalStyle(subtotalStyle)
				.highlightDetailEvenRows()
				.crosstabHighlightEvenRows()
				.setCrosstabGroupStyle(crosstabGroupStyle)
				.setCrosstabGroupTotalStyle(crosstabGroupTotalStyle)
				.setCrosstabGrandTotalStyle(crosstabGrandTotalStyle)
				.setCrosstabCellStyle(crosstabCellStyle)
				.setTableOfContentsCustomizer(tableOfContentsCustomizer);

		currencyType = new CurrencyType();

		HyperLinkBuilder link = hyperLink("https://link.it");
		dynamicReportsComponent =
				cmp.horizontalList(
						//  	cmp.image(Templates.class.getResource("images/dynamicreports.png")).setFixedDimension(60, 60),
						cmp.verticalList(
								cmp.text(TITOLO_REPORT).setStyle(bold22CenteredStyle).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
								cmp.text("https://link.it").setStyle(italicStyle).setHyperLink(link))).setFixedWidth(300);

		footerComponent = cmp.pageXofY()
				.setStyle(
						stl.style(boldCenteredStyle)
						.setTopBorder(stl.pen1Point()));
		
		categoryAxisFont = stl.font().setFontSize(10);
	}

	/**
	 * Creates custom component which is possible to add to any report band component
	 */
	public static ComponentBuilder<?, ?> createTitleComponent(String titoloReport, String periodoOsservazione) {
		HyperLinkBuilder link = hyperLink("https://link.it");
		return cmp.horizontalList()
				.add(	
//						cmp.horizontalList(
								//  	cmp.image(Templates.class.getResource("images/dynamicreports.png")).setFixedDimension(60, 60),
						cmp.verticalList(
								cmp.text(titoloReport).setStyle(bold22CenteredStyle).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
								cmp.text(periodoOsservazione).setStyle(bold18CenteredStyle).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
								cmp.text("https://link.it").setStyle(italicStyle).setHyperLink(link))
							)
//				).setFixedWidth(300),
//						)
				.newRow()
				.add(cmp.line())
				.newRow()
				.add(cmp.verticalGap(10));
	}

	/**
	 * Creates custom component which is possible to add to any report band component
	 */
	public static ComponentBuilder<?, ?> creaTitoloSezioneCasisticaErrori(String label) {
		return cmp.horizontalList()
				.add(cmp.text(label).setStyle(bold12LeftStyle).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT))
				.newRow()
				.add(cmp.line())
				.newRow()
				.add(cmp.verticalGap(10));
	}

	public static CurrencyValueFormatter createCurrencyValueFormatter(String label) {
		return new CurrencyValueFormatter(label);
	}

	public static class CurrencyType extends BigDecimalType {
		private static final long serialVersionUID = 1L;

		@Override
		public String getPattern() {
			return "$ #,###.00";
		}
	}

	private static class CurrencyValueFormatter extends AbstractValueFormatter<String, Number> {
		private static final long serialVersionUID = 1L;

		private String label;

		public CurrencyValueFormatter(String label) {
			this.label = label;
		}

		@Override
		public String format(Number value, ReportParameters reportParameters) {
			return this.label + currencyType.valueToString(value, reportParameters.getLocale());
		}
	}
}
