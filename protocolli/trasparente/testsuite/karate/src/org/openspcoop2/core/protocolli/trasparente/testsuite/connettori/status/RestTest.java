/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
 * Classe di test connettore 'status' su rest
 *
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RestTest extends ConfigLoader {
	
	
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
	public void testOkResponsesModI() throws UtilsException {
		
		String statusModi = "/statusModI";
		
		Object[][] provider = new Object[][] {
						
			{MessageType.BINARY, statusModi, TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.MODI},
			{MessageType.JSON, statusModi, TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.MODI},
			{MessageType.XML, statusModi, TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.MODI},
			{MessageType.MIME_MULTIPART, statusModi, TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.MODI},
			
			{MessageType.BINARY, statusModi, TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.MODI},
			{MessageType.JSON, statusModi, TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.MODI},
			{MessageType.XML, statusModi, TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.MODI},
			{MessageType.MIME_MULTIPART, statusModi, TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.MODI},
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
	public void testOkResponsesJson() throws UtilsException {
		
		String statusJson = "/statusJSON";
		
		Object[][] provider = new Object[][] {
			
			{MessageType.BINARY, statusJson, TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.JSON},
			
			{MessageType.BINARY, statusJson, TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.JSON},
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
	public void testOkResponsesXml() throws UtilsException {
		
		String statusXml = "/statusXML";
		
		
		Object[][] provider = new Object[][] {
			
			{MessageType.BINARY, statusXml, TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.XML},
			
			{MessageType.BINARY, statusXml, TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.XML},
			
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
	public void testOkResponsesEmpty() throws UtilsException {
		
		String statusVuoto = "/statusVuoto";
		
		Object[][] provider = new Object[][] {
			
			{MessageType.BINARY, statusVuoto, TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.VUOTO},
			
			{MessageType.BINARY, statusVuoto, TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.VUOTO},
			
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
	public void testOkResponsesText() throws UtilsException {
		
		String statusText = "/statusText";
		
		Object[][] provider = new Object[][] {
			
			{MessageType.BINARY, statusText, TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.TEXT},
						
			{MessageType.BINARY, statusText, TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.TEXT},
						
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
		StatusUtils.testSingleConnettivita(MessageType.BINARY, 
				TipoServizio.EROGAZIONE);
	}
	@Test
	public void testConnettivitaFruizione() throws UtilsException, SQLQueryObjectException {
		StatusUtils.testSingleConnettivita(MessageType.BINARY, 
				TipoServizio.FRUIZIONE);
	}
	
	@Test
	public void testStatisticaErogazione() throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		StatusUtils.testSingleStatistica(MessageType.BINARY,
					TipoServizio.EROGAZIONE);
	}
	@Test
	public void testStatisticaFruizione() throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		StatusUtils.testSingleStatistica(MessageType.BINARY,
					TipoServizio.FRUIZIONE);
	}
	
	
	@Test
	public void testLifetimeErogazione() throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		StatusUtils.testSingleLifetime(MessageType.BINARY,
					TipoServizio.EROGAZIONE);
	}
	@Test
	public void testLifetimeFruizione() throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		StatusUtils.testSingleLifetime(MessageType.BINARY,
					TipoServizio.FRUIZIONE);
	}
}
