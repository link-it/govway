/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.testsuite.zap;

import java.util.List;

/**
 * OpenAPI
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ReportInfos {

	public static void main(String[] args) throws Exception {
				
		ZAPContext context = new ZAPContext(args, ReportInfos.class.getName(), "");
		
		List<String> templates = ZAPReport.getTemplates(context.getClientApi());
		for (String template : templates) {
			
			System.out.println("=======================");
			System.out.println("Template: '"+template+"'");
			
			List<String> themas = ZAPReport.getThemes(context.getClientApi(), template);
			System.out.println("Themas: '"+themas.size()+"'");
			for (String thema : themas) {
				System.out.println("\t'"+thema+"'");
			}
			
			List<String> sections = ZAPReport.getSections(context.getClientApi(), template);
			System.out.println("Sections: '"+sections.size()+"'");
			for (String section : sections) {
				System.out.println("\t'"+section+"'");
			}
			
			System.out.println("=======================\n\n");
			
		}
		
		System.out.println("=======================");
		List<String> confidences = ZAPReport.getAllIncludedConfidences();
		System.out.println("Confidence: '"+confidences.size()+"'");
		for (String confidence : confidences) {
			System.out.println("\t'"+confidence+"'");
		}
		System.out.println("=======================\n\n");
		
		System.out.println("=======================");
		List<String> risks = ZAPReport.getAllIncludedRisks();
		System.out.println("Risk: '"+risks.size()+"'");
		for (String risk : risks) {
			System.out.println("\t'"+risk+"'");
		}
		System.out.println("=======================\n\n");
		
	}
	
}
