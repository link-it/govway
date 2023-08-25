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

	private static final String START = "=======================";
	private static final String END = "=======================\n\n";
	
	public static void main(String[] args) throws Exception {
				
		ZAPContext context = new ZAPContext(args, ReportInfos.class.getName(), "");
		
		List<String> templates = ZAPReport.getTemplates(context.getClientApi());
		for (String template : templates) {
			
			LoggerManager.info(START);
			LoggerManager.info("Template: '"+template+"'");
			
			List<String> themas = ZAPReport.getThemes(context.getClientApi(), template);
			LoggerManager.info("Themas: '"+themas.size()+"'");
			for (String thema : themas) {
				LoggerManager.info("\t'"+thema+"'");
			}
			
			List<String> sections = ZAPReport.getSections(context.getClientApi(), template);
			LoggerManager.info("Sections: '"+sections.size()+"'");
			for (String section : sections) {
				LoggerManager.info("\t'"+section+"'");
			}
			
			LoggerManager.info(END);
			
		}
		
		LoggerManager.info(START);
		List<String> confidences = ZAPReport.getAllIncludedConfidences();
		LoggerManager.info("Confidence: '"+confidences.size()+"'");
		for (String confidence : confidences) {
			LoggerManager.info("\t'"+confidence+"'");
		}
		LoggerManager.info(END);
		
		LoggerManager.info(START);
		List<String> risks = ZAPReport.getAllIncludedRisks();
		LoggerManager.info("Risk: '"+risks.size()+"'");
		for (String risk : risks) {
			LoggerManager.info("\t'"+risk+"'");
		}
		LoggerManager.info(END);
		
	}
	
}
