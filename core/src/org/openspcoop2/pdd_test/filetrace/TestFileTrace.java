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

package org.openspcoop2.pdd_test.filetrace;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.pdd_test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestGenerazioneTraccia
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestFileTrace {

	private static final String ID_TEST = "FileTrace";
	private static final boolean log4j = true;
	private static final boolean requestWithPayload = true;
	
	@DataProvider(name="fileTraceProvider")
	public Object[][] provider(){
		return new Object[][]{
			
			{TipoPdD.APPLICATIVA, !log4j, 0, requestWithPayload},
			{TipoPdD.APPLICATIVA, !log4j, 16, requestWithPayload}, // errore autenticazione
			
			{TipoPdD.DELEGATA, !log4j, 0, requestWithPayload},
			{TipoPdD.DELEGATA, !log4j, 16, requestWithPayload}, // errore autenticazione
			
			{TipoPdD.APPLICATIVA, log4j, 0, requestWithPayload},
			{TipoPdD.APPLICATIVA, log4j, 16, requestWithPayload}, // errore autenticazione
			
			{TipoPdD.DELEGATA, log4j, 0, requestWithPayload},
			{TipoPdD.DELEGATA, log4j, 16, requestWithPayload}, // errore autenticazione
			
			{TipoPdD.APPLICATIVA, !log4j, 0, !requestWithPayload},
			{TipoPdD.APPLICATIVA, !log4j, 16, !requestWithPayload}, // errore autenticazione
			
			{TipoPdD.DELEGATA, !log4j, 0, !requestWithPayload},
			{TipoPdD.DELEGATA, !log4j, 16, !requestWithPayload}, // errore autenticazione
			
			{TipoPdD.APPLICATIVA, log4j, 0, !requestWithPayload},
			{TipoPdD.APPLICATIVA, log4j, 16, !requestWithPayload}, // errore autenticazione
			
			{TipoPdD.DELEGATA, log4j, 0, !requestWithPayload},
			{TipoPdD.DELEGATA, log4j, 16, !requestWithPayload}, // errore autenticazione
			
		};
	}
	
	@Test(groups={Costanti.GRUPPO_PDD,Costanti.GRUPPO_PDD+"."+ID_TEST},dataProvider="fileTraceProvider")
	public void testFileTrace(TipoPdD tipoPdD, boolean log4j, int esito, boolean requestWithPayload) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' ...");
		org.openspcoop2.pdd.logger.filetrace.Test.test(tipoPdD, log4j, esito, requestWithPayload);
		TestLogger.info("Run test '"+ID_TEST+"' ok");
		
	}
	
}
