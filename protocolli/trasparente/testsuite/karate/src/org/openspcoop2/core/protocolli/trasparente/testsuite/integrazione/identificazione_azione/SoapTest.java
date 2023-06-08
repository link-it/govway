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
package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.identificazione_azione;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;

/**
* SoapTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {

	private static final String AZIONE1 = "azione1";
	
	private static final String AZIONE2 = "azione2";
	
	private static final String AZIONE_ERRATA_1 = AZIONE1+",http:";

	private static final String AZIONE_ERRATA_2 = AZIONE2+",http:";
	
	private static final String PREFIX_ERRORE_EROGAZIONE = "Azione richiesta non corretta: (azione:";
	private static final String AZIONE_ERRORE_EROGAZIONE = ") Azione '";
	
	private static final String PREFIX_ERRORE_FRUIZIONE = "Azione '";
	
	private static final String SUFFIX_ERRORE = "' non trovata per il servizio [TestIdentificazioneAzione]";
	
	private static final String ERRORE_EROGAZIONE_AZIONE1 = PREFIX_ERRORE_EROGAZIONE+AZIONE_ERRATA_1+AZIONE_ERRORE_EROGAZIONE+AZIONE_ERRATA_1+SUFFIX_ERRORE;
	private static final String ERRORE_EROGAZIONE_AZIONE2 = PREFIX_ERRORE_EROGAZIONE+AZIONE_ERRATA_2+AZIONE_ERRORE_EROGAZIONE+AZIONE_ERRATA_2+SUFFIX_ERRORE;
	
	private static final String ERRORE_FRUIZIONE_AZIONE1 = PREFIX_ERRORE_FRUIZIONE+AZIONE_ERRATA_1+SUFFIX_ERRORE;
	private static final String ERRORE_FRUIZIONE_AZIONE2 = PREFIX_ERRORE_FRUIZIONE+AZIONE_ERRATA_2+SUFFIX_ERRORE;
	
	private static final String AZIONE_ERRATA_1_PATH = AZIONE1+",";

	private static final String AZIONE_ERRATA_2_PATH = AZIONE2+",";
	
	private static final String ERRORE_EROGAZIONE_AZIONE1_PATH = PREFIX_ERRORE_EROGAZIONE+AZIONE_ERRATA_1_PATH+AZIONE_ERRORE_EROGAZIONE+AZIONE_ERRATA_1_PATH+SUFFIX_ERRORE;
	private static final String ERRORE_EROGAZIONE_AZIONE2_PATH = PREFIX_ERRORE_EROGAZIONE+AZIONE_ERRATA_2_PATH+AZIONE_ERRORE_EROGAZIONE+AZIONE_ERRATA_2_PATH+SUFFIX_ERRORE;
	
	private static final String ERRORE_FRUIZIONE_AZIONE1_PATH = PREFIX_ERRORE_FRUIZIONE+AZIONE_ERRATA_1_PATH+SUFFIX_ERRORE;
	private static final String ERRORE_FRUIZIONE_AZIONE2_PATH = PREFIX_ERRORE_FRUIZIONE+AZIONE_ERRATA_2_PATH+SUFFIX_ERRORE;
			
	private static final String AZIONE_ERRATA_1_SPACE_EROGAZIONE = AZIONE1+"%20SoggettoInternoTest";
	private static final String AZIONE_ERRATA_1_SPACE_FRUIZIONE = AZIONE1+"%20out";
	
	private static final String ERRORE_EROGAZIONE_AZIONE1_SPACE = PREFIX_ERRORE_EROGAZIONE+AZIONE_ERRATA_1_SPACE_EROGAZIONE+AZIONE_ERRORE_EROGAZIONE+AZIONE_ERRATA_1_SPACE_EROGAZIONE+SUFFIX_ERRORE;
	private static final String ERRORE_FRUIZIONE_AZIONE1_SPACE = PREFIX_ERRORE_FRUIZIONE+AZIONE_ERRATA_1_SPACE_FRUIZIONE+SUFFIX_ERRORE;
	
	@Test
	public void erogazioneTestOk() throws Exception {
		Utilities.test("/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1", 
				AZIONE1, null);
	}
	@Test
	public void fruizioneTestOk() throws Exception {
		Utilities.test("/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1", 
				AZIONE1, null);
	}
	
	@Test
	public void erogazioneTestOk2() throws Exception {
		Utilities.test("/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione2", 
				AZIONE2, null);
	}
	@Test
	public void fruizioneTestOk2() throws Exception {
		Utilities.test("/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione2", 
				AZIONE2, null);
	}
	
	@Test
	public void erogazioneTestKoMultipleUrls() throws Exception {
		Utilities.test("/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1,http://localhost:8080/govway/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione2,http://localhost:8080/govway/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione3", 
				AZIONE_ERRATA_1, ERRORE_EROGAZIONE_AZIONE1);
	}
	@Test
	public void fruizioneTestKoMultipleUrls() throws Exception {
		Utilities.test("/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1,http://localhost:8080/govway/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione2", 
				AZIONE_ERRATA_1, ERRORE_FRUIZIONE_AZIONE1);
	}
	
	@Test
	public void erogazioneTestKoMultipleUrls2() throws Exception {
		Utilities.test("/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione2,http://localhost:8080/govway/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1,http://localhost:8080/govway/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione3", 
				AZIONE_ERRATA_2, ERRORE_EROGAZIONE_AZIONE2);
	}
	@Test
	public void fruizioneTestKoMultipleUrls2() throws Exception {
		Utilities.test("/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione2,http://localhost:8080/govway/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1", 
				AZIONE_ERRATA_2, ERRORE_FRUIZIONE_AZIONE2);
	}

	@Test
	public void erogazioneTestKoMultipleUrlsServizioSconosciutoAltreUrl() throws Exception {
		Utilities.test("/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1,http://localhost:8080/govway/SoggettoInternoTest/TestAltroServizioIdentificazioneAzione/v1/azione2,http://localhost:8080/govway/SoggettoInternoTest/TestAltroServizioIdentificazioneAzione/v1/azione3", 
				AZIONE_ERRATA_1, ERRORE_EROGAZIONE_AZIONE1);
	}
	@Test
	public void fruizioneTestKoMultipleUrlsServizioSconosciutoAltreUrl() throws Exception {
		Utilities.test("/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1,http://localhost:8080/govway/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestAltroServizioIdentificazioneAzione/v1/azione2", 
				AZIONE_ERRATA_1, ERRORE_FRUIZIONE_AZIONE1);
	}
	
	
	@Test
	public void erogazioneTestKoMultiplePath() throws Exception {
		Utilities.test("/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1,/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione2,/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione3", 
				AZIONE_ERRATA_1_PATH, ERRORE_EROGAZIONE_AZIONE1_PATH);
	}
	@Test
	public void fruizioneTestKoMultiplePath() throws Exception {
		Utilities.test("/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1,/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione2", 
				AZIONE_ERRATA_1_PATH, ERRORE_FRUIZIONE_AZIONE1_PATH);
	}
	
	@Test
	public void erogazioneTestKoMultiplePath2() throws Exception {
		Utilities.test("/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione2,/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1,/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione3", 
				AZIONE_ERRATA_2_PATH, ERRORE_EROGAZIONE_AZIONE2_PATH);
	}
	@Test
	public void fruizioneTestKoMultiplePath2() throws Exception {
		Utilities.test("/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione2,/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1", 
				AZIONE_ERRATA_2_PATH, ERRORE_FRUIZIONE_AZIONE2_PATH);
	}

	@Test
	public void erogazioneTestKoMultiplePathServizioSconosciutoAltreUrl() throws Exception {
		Utilities.test("/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1,/SoggettoInternoTest/TestAltroServizioIdentificazioneAzione/v1/azione2,/SoggettoInternoTest/TestAltroServizioIdentificazioneAzione/v1/azione3", 
				AZIONE_ERRATA_1_PATH, ERRORE_EROGAZIONE_AZIONE1_PATH);
	}
	@Test
	public void fruizioneTestKoMultiplePathServizioSconosciutoAltreUrl() throws Exception {
		Utilities.test("/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1,/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestAltroServizioIdentificazioneAzione/v1/azione2", 
				AZIONE_ERRATA_1_PATH, ERRORE_FRUIZIONE_AZIONE1_PATH);
	}
	
	
	
	
	@Test
	public void erogazioneTestKoMultipleSpace() throws Exception {
		Utilities.test("/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1%20SoggettoInternoTest/TestIdentificazioneAzione/v1/azione2%20SoggettoInternoTest/TestIdentificazioneAzione/v1/azione3", 
				AZIONE_ERRATA_1_SPACE_EROGAZIONE, ERRORE_EROGAZIONE_AZIONE1_SPACE);
	}
	@Test
	public void fruizioneTestKoMultipleSpace() throws Exception {
		Utilities.test("/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione1%20out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestIdentificazioneAzione/v1/azione2", 
				AZIONE_ERRATA_1_SPACE_FRUIZIONE, ERRORE_FRUIZIONE_AZIONE1_SPACE);
	}
	
}
