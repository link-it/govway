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
 * TestProviderMigration
 *
 * Verifica che uno spostamento del provider BouncyCastle nella lista dei provider JCE non alteri i dati prodotti.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestProviderMigration {

	private static final String ID_TEST = "SecurityProviderMigration";

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".servizi"})
	public void testDifferenzialeServizi() throws UtilsException {

		TestLogger.info("Run test '"+ID_TEST+".servizi' ...");
		org.openspcoop2.utils.security.test.ProviderMigrationTest.testDifferenzialeServizi();
		TestLogger.info("Run test '"+ID_TEST+".servizi' ok");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".trasformazioni"})
	public void testDifferenzialeTrasformazioni() throws UtilsException {

		TestLogger.info("Run test '"+ID_TEST+".trasformazioni' ...");
		org.openspcoop2.utils.security.test.ProviderMigrationTest.testDifferenzialeTrasformazioni();
		TestLogger.info("Run test '"+ID_TEST+".trasformazioni' ok");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".byok"})
	public void testMigrazioneByok() throws UtilsException {

		TestLogger.info("Run test '"+ID_TEST+".byok' ...");
		org.openspcoop2.utils.security.test.ProviderMigrationTest.testMigrazioneByok();
		TestLogger.info("Run test '"+ID_TEST+".byok' ok");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".jwe"})
	public void testMigrazioneJwe() throws UtilsException {

		TestLogger.info("Run test '"+ID_TEST+".jwe' ...");
		org.openspcoop2.utils.security.test.ProviderMigrationTest.testMigrazioneJwe();
		TestLogger.info("Run test '"+ID_TEST+".jwe' ok");

	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".jwe"})
	public void testInteroperabilitaJweNimbus() throws UtilsException {

		TestLogger.info("Run test '"+ID_TEST+".jwe.nimbus' ...");
		org.openspcoop2.utils.security.test.ProviderMigrationTest.testInteroperabilitaJweNimbus();
		TestLogger.info("Run test '"+ID_TEST+".jwe.nimbus' ok");

	}

}
