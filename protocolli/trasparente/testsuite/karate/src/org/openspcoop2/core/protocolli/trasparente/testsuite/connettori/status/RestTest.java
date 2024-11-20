/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
		ConfigLoader.enableCaches(CoreTest.CACHE_CONTROLLO_TRAFFICO);
	}
	
	@After
	public void dispose() throws UtilsException, HttpUtilsException {
		ConfigLoader.disableCaches(CoreTest.CACHE_CONTROLLO_TRAFFICO);
	}
	
	// ************************************* TEST ****************************************
	
	@Test
	public void testOkResponses() throws UtilsException {
		Object[][] provider = new Object[][] {
			{true, "/statusJSON", TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.JSON},
			{true, "/statusXML", TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.XML},
			{true, "/statusVuoto", TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.VUOTO},
			{true, "/statusText", TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.TEXT},
			{true, "/statusModI", TipoServizio.EROGAZIONE, ConnettoreStatusResponseType.MODI},
			{true, "/statusJSON", TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.JSON},
			{true, "/statusXML", TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.XML},
			{true, "/statusVuoto", TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.VUOTO},
			{true, "/statusText", TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.TEXT},
			{true, "/statusModI", TipoServizio.FRUIZIONE, ConnettoreStatusResponseType.MODI},
		};
		
		for (Object[] data : provider) {
			CoreTest.testSingleOkResponse(
					(boolean) data[0],
					(String) data[1], 
					(TipoServizio) data[2],
					(ConnettoreStatusResponseType) data[3]);
		}
	}
	
	@Test
	public void testConnettivita() throws UtilsException, SQLQueryObjectException {
		Object[][] provider = new Object[][] {
			{true, TipoServizio.EROGAZIONE},
			{true, TipoServizio.FRUIZIONE}
		};
		
		for (Object[] data : provider) {
			CoreTest.testSingleConnettivita((boolean) data[0], 
					(TipoServizio) data[1]);
		}
	}
	
	@Test
	public void testStatistica() throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		Object[][] provider = new Object[][] {
			{true, TipoServizio.EROGAZIONE},
			{true, TipoServizio.FRUIZIONE}
		};
		
		for (Object[] data : provider) {
			CoreTest.testSingleStatistica((boolean) data[0],
					(TipoServizio)data[1]);
		}
	}
	
	
	@Test
	public void testLifetime() throws SQLQueryObjectException, UtilsException, HttpUtilsException {
		Object[][] provider = new Object[][] {
			{true, TipoServizio.EROGAZIONE},
			{true, TipoServizio.FRUIZIONE}
		};
		
		for (Object[] data : provider) {
			CoreTest.testSingleLifetime((boolean) data[0],
					(TipoServizio) data[1]);
		}
	}
}
