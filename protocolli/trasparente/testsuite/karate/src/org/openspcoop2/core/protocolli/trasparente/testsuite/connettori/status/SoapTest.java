/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.connettori.ConnettoreStatusResponseType;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.openspcoop2.utils.transport.http.HttpUtilsException;

/**
 * Classe di test connettore 'status' su soap
 *
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SoapTest extends ConfigLoader {
	
	
	@Before
	public void init() throws UtilsException, HttpUtilsException {
		ConfigLoader.enableCaches(StatusUtils.CACHE_CONTROLLO_TRAFFICO);
	}
	
	@After
	public void dispose() throws UtilsException, HttpUtilsException {
		ConfigLoader.disableCaches(StatusUtils.CACHE_CONTROLLO_TRAFFICO);
	}
	
	// ************************************* TEST ****************************************
	
	@Test
	public void testOkResponsesSoap11() throws UtilsException {
		
		String statusModi = "/statusModI";
		
		Object[][] provider = new Object[][] {
			{MessageType.SOAP_11, statusModi, TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.MODI},
			{MessageType.SOAP_11, statusModi, TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.MODI},
		};
		
		for (Object[] data : provider) {
			StatusUtils.testSingleOkResponse(
					(MessageType) data[0],
					(String) data[1], 
					(TipoServizio) data[2],
					(ConnettoreStatusResponseType) data[3]);
		}
	}
	
	@Test
	public void testOkResponsesSoap12() throws UtilsException {
		
		String statusModi = "/statusModI";
		
		Object[][] provider = new Object[][] {
			{MessageType.SOAP_12, statusModi, TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.MODI},
			{MessageType.SOAP_12, statusModi, TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.MODI}
		};
		
		for (Object[] data : provider) {
			StatusUtils.testSingleOkResponse(
					(MessageType) data[0],
					(String) data[1], 
					(TipoServizio) data[2],
					(ConnettoreStatusResponseType) data[3]);
		}
	}
	
	@Test
	public void testConnettivitaErogazione() throws UtilsException, SQLQueryObjectException {
		StatusUtils.testSingleConnettivita(MessageType.SOAP_11, 
					TipoServizio.EROGAZIONE);
	}
	@Test
	public void testConnettivitaFruizione() throws UtilsException, SQLQueryObjectException {
		StatusUtils.testSingleConnettivita(MessageType.SOAP_12, 
					TipoServizio.FRUIZIONE);
	}
	
	@Test
	public void testStatisticaErogazione() throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		StatusUtils.testSingleStatistica(MessageType.SOAP_12, 
				TipoServizio.EROGAZIONE);
	}
	@Test
	public void testStatisticaFruizione() throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		StatusUtils.testSingleStatistica(MessageType.SOAP_11, 
				TipoServizio.FRUIZIONE);
	}
	
	
	@Test
	public void testLifetimeErogazione() throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		StatusUtils.testSingleLifetime(MessageType.SOAP_11, 
				TipoServizio.EROGAZIONE);
	}
	@Test
	public void testLifetimeFruizione() throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		StatusUtils.testSingleLifetime(MessageType.SOAP_12, 
				TipoServizio.FRUIZIONE);
	}
}
