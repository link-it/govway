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

package org.openspcoop2.utils.test.security;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.Test;

/**
 * TestProviderBenchmark
 *
 * Verifica la posizione in cui viene registrato il provider BouncyCastle e misura i servizi che, per effetto di tale
 * posizione, vengono serviti da un provider del jdk anziche' da BouncyCastle.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestProviderBenchmark {

	private static final String ID_TEST = "SecurityProviderBenchmark";

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".serviziCondivisi"})
	public void testServiziCondivisi() throws UtilsException {

		TestLogger.info("Run test '"+ID_TEST+".serviziCondivisi' ...");
		org.openspcoop2.utils.security.test.ProviderBenchmarkTest.testServiziCondivisi();
		TestLogger.info("Run test '"+ID_TEST+".serviziCondivisi' ok");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".oaep"})
	public void testParametriOaep() throws UtilsException {

		TestLogger.info("Run test '"+ID_TEST+".oaep.parametri' ...");
		org.openspcoop2.utils.security.test.ProviderBenchmarkTest.testParametriOaep();
		TestLogger.info("Run test '"+ID_TEST+".oaep.parametri' ok");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".oaep"})
	public void testInteroperabilitaOaep() throws UtilsException {

		TestLogger.info("Run test '"+ID_TEST+".oaep.interoperabilita' ...");
		org.openspcoop2.utils.security.test.ProviderBenchmarkTest.testInteroperabilitaOaep();
		TestLogger.info("Run test '"+ID_TEST+".oaep.interoperabilita' ok");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".benchmark"})
	public void testBenchmarkServiziCondivisi() throws UtilsException {

		TestLogger.info("Run test '"+ID_TEST+".benchmark' ...");
		org.openspcoop2.utils.security.test.ProviderBenchmarkTest.testBenchmarkServiziCondivisi();
		TestLogger.info("Run test '"+ID_TEST+".benchmark' ok");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".benchmark"})
	public void testBenchmarkDimensioniMessaggio() throws UtilsException {

		TestLogger.info("Run test '"+ID_TEST+".benchmark.dimensioniMessaggio' ...");
		org.openspcoop2.utils.security.test.ProviderBenchmarkTest.testBenchmarkDimensioniMessaggio();
		TestLogger.info("Run test '"+ID_TEST+".benchmark.dimensioniMessaggio' ok");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".benchmark"})
	public void testBenchmarkTls() throws UtilsException {

		TestLogger.info("Run test '"+ID_TEST+".benchmark.tls' ...");
		org.openspcoop2.utils.security.test.ProviderBenchmarkTest.testBenchmarkTls();
		TestLogger.info("Run test '"+ID_TEST+".benchmark.tls' ok");

	}

}
