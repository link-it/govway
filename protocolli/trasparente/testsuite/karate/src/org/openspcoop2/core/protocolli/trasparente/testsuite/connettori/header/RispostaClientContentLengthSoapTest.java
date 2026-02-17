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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* RispostaClientContentLengthSoapTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RispostaClientContentLengthSoapTest extends ConfigLoader {

	private static final String API = "TestRispostaClientContentLengthSoap";
	
	private static final String OP_FORCE = "force";
	private static final String OP_FORCE_RECALCULATE = "force.recalculate";
	private static final String OP_PRESERVE = "preserve";
	private static final String OP_PRESERVE_RECALCULATE = "preserve.recalculate";


	
	// *** ERRORI GOVWAY - SOAP 1.1 ***

	@Test
	public void soap11_erogazione_force_erroreGovWay() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_FORCE, false, false, false, true, true);
	}
	@Test
	public void soap11_fruizione_force_erroreGovWay() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_FORCE, false, false, false, true, true);
	}
	@Test
	public void soap11_erogazione_forceRecalculate_erroreGovWay() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_FORCE_RECALCULATE, false, false, false, true, true);
	}
	@Test
	public void soap11_fruizione_forceRecalculate_erroreGovWay() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_FORCE_RECALCULATE, false, false, false, true, true);
	}
	@Test
	public void soap11_erogazione_preserve_erroreGovWay() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_PRESERVE, false, false, false, false, true);
	}
	@Test
	public void soap11_fruizione_preserve_erroreGovWay() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_PRESERVE, false, false, false, false, true);
	}
	@Test
	public void soap11_erogazione_preserveRecalculate_erroreGovWay() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_PRESERVE_RECALCULATE, false, false, false, false, true);
	}
	@Test
	public void soap11_fruizione_preserveRecalculate_erroreGovWay() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_PRESERVE_RECALCULATE, false, false, false, false, true);
	}

	// *** ERRORI GOVWAY - SOAP 1.2 ***

	@Test
	public void soap12_erogazione_force_erroreGovWay() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_FORCE, false, false, false, true, true);
	}
	@Test
	public void soap12_fruizione_force_erroreGovWay() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_FORCE, false, false, false, true, true);
	}
	@Test
	public void soap12_erogazione_forceRecalculate_erroreGovWay() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_FORCE_RECALCULATE, false, false, false, true, true);
	}
	@Test
	public void soap12_fruizione_forceRecalculate_erroreGovWay() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_FORCE_RECALCULATE, false, false, false, true, true);
	}
	@Test
	public void soap12_erogazione_preserve_erroreGovWay() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_PRESERVE, false, false, false, false, true);
	}
	@Test
	public void soap12_fruizione_preserve_erroreGovWay() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_PRESERVE, false, false, false, false, true);
	}
	@Test
	public void soap12_erogazione_preserveRecalculate_erroreGovWay() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_PRESERVE_RECALCULATE, false, false, false, false, true);
	}
	@Test
	public void soap12_fruizione_preserveRecalculate_erroreGovWay() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_PRESERVE_RECALCULATE, false, false, false, false, true);
	}


	// *** FORCE - SOAP 1.1 ***

	// -- con contenuto, senza trasformazione, senza backend CL
	@Test
	public void soap11_erogazione_force_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_FORCE, false, false, false, true);
	}
	@Test
	public void soap11_fruizione_force_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_FORCE, false, false, false, true);
	}

	// -- con contenuto, senza trasformazione, con backend CL
	@Test
	public void soap11_erogazione_force_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_FORCE, false, false, true, true);
	}
	@Test
	public void soap11_fruizione_force_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_FORCE, false, false, true, true);
	}

	// -- con contenuto, con trasformazione, senza backend CL
	@Test
	public void soap11_erogazione_force_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_FORCE, false, true, false, true);
	}
	@Test
	public void soap11_fruizione_force_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_FORCE, false, true, false, true);
	}

	// -- con contenuto, con trasformazione, con backend CL
	@Test
	public void soap11_erogazione_force_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_FORCE, false, true, true, true);
	}
	@Test
	public void soap11_fruizione_force_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_FORCE, false, true, true, true);
	}

	// -- senza contenuto, senza backend CL
	@Test
	public void soap11_erogazione_force_senzaContenuto_senzaBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_FORCE, true, false, false, true);
	}
	@Test
	public void soap11_fruizione_force_senzaContenuto_senzaBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_FORCE, true, false, false, true);
	}

	// -- senza contenuto, con backend CL
	@Test
	public void soap11_erogazione_force_senzaContenuto_conBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_FORCE, true, false, true, true);
	}
	@Test
	public void soap11_fruizione_force_senzaContenuto_conBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_FORCE, true, false, true, true);
	}

	// *** FORCE - SOAP 1.2 ***

	// -- con contenuto, senza trasformazione, senza backend CL
	@Test
	public void soap12_erogazione_force_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_FORCE, false, false, false, true);
	}
	@Test
	public void soap12_fruizione_force_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_FORCE, false, false, false, true);
	}

	// -- con contenuto, senza trasformazione, con backend CL
	@Test
	public void soap12_erogazione_force_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_FORCE, false, false, true, true);
	}
	@Test
	public void soap12_fruizione_force_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_FORCE, false, false, true, true);
	}

	// -- con contenuto, con trasformazione, senza backend CL
	@Test
	public void soap12_erogazione_force_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_FORCE, false, true, false, true);
	}
	@Test
	public void soap12_fruizione_force_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_FORCE, false, true, false, true);
	}

	// -- con contenuto, con trasformazione, con backend CL
	@Test
	public void soap12_erogazione_force_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_FORCE, false, true, true, true);
	}
	@Test
	public void soap12_fruizione_force_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_FORCE, false, true, true, true);
	}

	// -- senza contenuto, senza backend CL
	@Test
	public void soap12_erogazione_force_senzaContenuto_senzaBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_FORCE, true, false, false, true);
	}
	@Test
	public void soap12_fruizione_force_senzaContenuto_senzaBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_FORCE, true, false, false, true);
	}

	// -- senza contenuto, con backend CL
	@Test
	public void soap12_erogazione_force_senzaContenuto_conBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_FORCE, true, false, true, true);
	}
	@Test
	public void soap12_fruizione_force_senzaContenuto_conBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_FORCE, true, false, true, true);
	}


	// *** FORCE RECALCULATE - SOAP 1.1 ***

	// -- con contenuto, senza trasformazione, senza backend CL
	@Test
	public void soap11_erogazione_forceRecalculate_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_FORCE_RECALCULATE, false, false, false, true);
	}
	@Test
	public void soap11_fruizione_forceRecalculate_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_FORCE_RECALCULATE, false, false, false, true);
	}

	// -- con contenuto, senza trasformazione, con backend CL
	@Test
	public void soap11_erogazione_forceRecalculate_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_FORCE_RECALCULATE, false, false, true, true);
	}
	@Test
	public void soap11_fruizione_forceRecalculate_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_FORCE_RECALCULATE, false, false, true, true);
	}

	// -- con contenuto, con trasformazione, senza backend CL
	@Test
	public void soap11_erogazione_forceRecalculate_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_FORCE_RECALCULATE, false, true, false, true);
	}
	@Test
	public void soap11_fruizione_forceRecalculate_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_FORCE_RECALCULATE, false, true, false, true);
	}

	// -- con contenuto, con trasformazione, con backend CL
	@Test
	public void soap11_erogazione_forceRecalculate_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_FORCE_RECALCULATE, false, true, true, true);
	}
	@Test
	public void soap11_fruizione_forceRecalculate_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_FORCE_RECALCULATE, false, true, true, true);
	}

	// -- senza contenuto, senza backend CL
	@Test
	public void soap11_erogazione_forceRecalculate_senzaContenuto_senzaBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_FORCE_RECALCULATE, true, false, false, true);
	}
	@Test
	public void soap11_fruizione_forceRecalculate_senzaContenuto_senzaBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_FORCE_RECALCULATE, true, false, false, true);
	}

	// -- senza contenuto, con backend CL
	@Test
	public void soap11_erogazione_forceRecalculate_senzaContenuto_conBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_FORCE_RECALCULATE, true, false, true, true);
	}
	@Test
	public void soap11_fruizione_forceRecalculate_senzaContenuto_conBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_FORCE_RECALCULATE, true, false, true, true);
	}

	// *** FORCE RECALCULATE - SOAP 1.2 ***

	// -- con contenuto, senza trasformazione, senza backend CL
	@Test
	public void soap12_erogazione_forceRecalculate_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_FORCE_RECALCULATE, false, false, false, true);
	}
	@Test
	public void soap12_fruizione_forceRecalculate_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_FORCE_RECALCULATE, false, false, false, true);
	}

	// -- con contenuto, senza trasformazione, con backend CL
	@Test
	public void soap12_erogazione_forceRecalculate_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_FORCE_RECALCULATE, false, false, true, true);
	}
	@Test
	public void soap12_fruizione_forceRecalculate_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_FORCE_RECALCULATE, false, false, true, true);
	}

	// -- con contenuto, con trasformazione, senza backend CL
	@Test
	public void soap12_erogazione_forceRecalculate_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_FORCE_RECALCULATE, false, true, false, true);
	}
	@Test
	public void soap12_fruizione_forceRecalculate_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_FORCE_RECALCULATE, false, true, false, true);
	}

	// -- con contenuto, con trasformazione, con backend CL
	@Test
	public void soap12_erogazione_forceRecalculate_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_FORCE_RECALCULATE, false, true, true, true);
	}
	@Test
	public void soap12_fruizione_forceRecalculate_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_FORCE_RECALCULATE, false, true, true, true);
	}

	// -- senza contenuto, senza backend CL
	@Test
	public void soap12_erogazione_forceRecalculate_senzaContenuto_senzaBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_FORCE_RECALCULATE, true, false, false, true);
	}
	@Test
	public void soap12_fruizione_forceRecalculate_senzaContenuto_senzaBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_FORCE_RECALCULATE, true, false, false, true);
	}

	// -- senza contenuto, con backend CL
	@Test
	public void soap12_erogazione_forceRecalculate_senzaContenuto_conBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_FORCE_RECALCULATE, true, false, true, true);
	}
	@Test
	public void soap12_fruizione_forceRecalculate_senzaContenuto_conBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_FORCE_RECALCULATE, true, false, true, true);
	}


	// *** PRESERVE - SOAP 1.1 ***

	// -- con contenuto, senza trasformazione, senza backend CL
	@Test
	public void soap11_erogazione_preserve_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_PRESERVE, false, false, false, false);
	}
	@Test
	public void soap11_fruizione_preserve_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_PRESERVE, false, false, false, false);
	}

	// -- con contenuto, senza trasformazione, con backend CL
	@Test
	public void soap11_erogazione_preserve_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_PRESERVE, false, false, true, true);
	}
	@Test
	public void soap11_fruizione_preserve_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_PRESERVE, false, false, true, true);
	}

	// -- con contenuto, con trasformazione, senza backend CL
	@Test
	public void soap11_erogazione_preserve_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_PRESERVE, false, true, false, false);
	}
	@Test
	public void soap11_fruizione_preserve_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_PRESERVE, false, true, false, false);
	}

	// -- con contenuto, con trasformazione, con backend CL
	@Test
	public void soap11_erogazione_preserve_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_PRESERVE, false, true, true, true);
	}
	@Test
	public void soap11_fruizione_preserve_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_PRESERVE, false, true, true, true);
	}

	// -- senza contenuto, senza backend CL
	@Test
	public void soap11_erogazione_preserve_senzaContenuto_senzaBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_PRESERVE, true, false, false, false);
	}
	@Test
	public void soap11_fruizione_preserve_senzaContenuto_senzaBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_PRESERVE, true, false, false, false);
	}

	// -- senza contenuto, con backend CL
	@Test
	public void soap11_erogazione_preserve_senzaContenuto_conBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_PRESERVE, true, false, true, true);
	}
	@Test
	public void soap11_fruizione_preserve_senzaContenuto_conBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_PRESERVE, true, false, true, true);
	}

	// *** PRESERVE - SOAP 1.2 ***

	// -- con contenuto, senza trasformazione, senza backend CL
	@Test
	public void soap12_erogazione_preserve_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_PRESERVE, false, false, false, false);
	}
	@Test
	public void soap12_fruizione_preserve_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_PRESERVE, false, false, false, false);
	}

	// -- con contenuto, senza trasformazione, con backend CL
	@Test
	public void soap12_erogazione_preserve_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_PRESERVE, false, false, true, true);
	}
	@Test
	public void soap12_fruizione_preserve_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_PRESERVE, false, false, true, true);
	}

	// -- con contenuto, con trasformazione, senza backend CL
	@Test
	public void soap12_erogazione_preserve_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_PRESERVE, false, true, false, false);
	}
	@Test
	public void soap12_fruizione_preserve_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_PRESERVE, false, true, false, false);
	}

	// -- con contenuto, con trasformazione, con backend CL
	@Test
	public void soap12_erogazione_preserve_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_PRESERVE, false, true, true, true);
	}
	@Test
	public void soap12_fruizione_preserve_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_PRESERVE, false, true, true, true);
	}

	// -- senza contenuto, senza backend CL
	@Test
	public void soap12_erogazione_preserve_senzaContenuto_senzaBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_PRESERVE, true, false, false, false);
	}
	@Test
	public void soap12_fruizione_preserve_senzaContenuto_senzaBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_PRESERVE, true, false, false, false);
	}

	// -- senza contenuto, con backend CL
	@Test
	public void soap12_erogazione_preserve_senzaContenuto_conBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_PRESERVE, true, false, true, true);
	}
	@Test
	public void soap12_fruizione_preserve_senzaContenuto_conBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_PRESERVE, true, false, true, true);
	}


	// *** PRESERVE RECALCULATE - SOAP 1.1 ***

	// -- con contenuto, senza trasformazione, senza backend CL
	@Test
	public void soap11_erogazione_preserveRecalculate_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_PRESERVE_RECALCULATE, false, false, false, false);
	}
	@Test
	public void soap11_fruizione_preserveRecalculate_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_PRESERVE_RECALCULATE, false, false, false, false);
	}

	// -- con contenuto, senza trasformazione, con backend CL
	@Test
	public void soap11_erogazione_preserveRecalculate_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_PRESERVE_RECALCULATE, false, false, true, true);
	}
	@Test
	public void soap11_fruizione_preserveRecalculate_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_PRESERVE_RECALCULATE, false, false, true, true);
	}

	// -- con contenuto, con trasformazione, senza backend CL
	@Test
	public void soap11_erogazione_preserveRecalculate_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_PRESERVE_RECALCULATE, false, true, false, false);
	}
	@Test
	public void soap11_fruizione_preserveRecalculate_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_PRESERVE_RECALCULATE, false, true, false, false);
	}

	// -- con contenuto, con trasformazione, con backend CL
	@Test
	public void soap11_erogazione_preserveRecalculate_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_PRESERVE_RECALCULATE, false, true, true, true);
	}
	@Test
	public void soap11_fruizione_preserveRecalculate_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_PRESERVE_RECALCULATE, false, true, true, true);
	}

	// -- senza contenuto, senza backend CL
	@Test
	public void soap11_erogazione_preserveRecalculate_senzaContenuto_senzaBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_PRESERVE_RECALCULATE, true, false, false, false);
	}
	@Test
	public void soap11_fruizione_preserveRecalculate_senzaContenuto_senzaBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_PRESERVE_RECALCULATE, true, false, false, false);
	}

	// -- senza contenuto, con backend CL
	@Test
	public void soap11_erogazione_preserveRecalculate_senzaContenuto_conBackendCL() throws Exception {
		test(true, TipoServizio.EROGAZIONE, OP_PRESERVE_RECALCULATE, true, false, true, true);
	}
	@Test
	public void soap11_fruizione_preserveRecalculate_senzaContenuto_conBackendCL() throws Exception {
		test(true, TipoServizio.FRUIZIONE, OP_PRESERVE_RECALCULATE, true, false, true, true);
	}

	// *** PRESERVE RECALCULATE - SOAP 1.2 ***

	// -- con contenuto, senza trasformazione, senza backend CL
	@Test
	public void soap12_erogazione_preserveRecalculate_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_PRESERVE_RECALCULATE, false, false, false, false);
	}
	@Test
	public void soap12_fruizione_preserveRecalculate_conContenuto_senzaTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_PRESERVE_RECALCULATE, false, false, false, false);
	}

	// -- con contenuto, senza trasformazione, con backend CL
	@Test
	public void soap12_erogazione_preserveRecalculate_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_PRESERVE_RECALCULATE, false, false, true, true);
	}
	@Test
	public void soap12_fruizione_preserveRecalculate_conContenuto_senzaTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_PRESERVE_RECALCULATE, false, false, true, true);
	}

	// -- con contenuto, con trasformazione, senza backend CL
	@Test
	public void soap12_erogazione_preserveRecalculate_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_PRESERVE_RECALCULATE, false, true, false, false);
	}
	@Test
	public void soap12_fruizione_preserveRecalculate_conContenuto_conTrasformazione_senzaBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_PRESERVE_RECALCULATE, false, true, false, false);
	}

	// -- con contenuto, con trasformazione, con backend CL
	@Test
	public void soap12_erogazione_preserveRecalculate_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_PRESERVE_RECALCULATE, false, true, true, true);
	}
	@Test
	public void soap12_fruizione_preserveRecalculate_conContenuto_conTrasformazione_conBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_PRESERVE_RECALCULATE, false, true, true, true);
	}

	// -- senza contenuto, senza backend CL
	@Test
	public void soap12_erogazione_preserveRecalculate_senzaContenuto_senzaBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_PRESERVE_RECALCULATE, true, false, false, false);
	}
	@Test
	public void soap12_fruizione_preserveRecalculate_senzaContenuto_senzaBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_PRESERVE_RECALCULATE, true, false, false, false);
	}

	// -- senza contenuto, con backend CL
	@Test
	public void soap12_erogazione_preserveRecalculate_senzaContenuto_conBackendCL() throws Exception {
		test(false, TipoServizio.EROGAZIONE, OP_PRESERVE_RECALCULATE, true, false, true, true);
	}
	@Test
	public void soap12_fruizione_preserveRecalculate_senzaContenuto_conBackendCL() throws Exception {
		test(false, TipoServizio.FRUIZIONE, OP_PRESERVE_RECALCULATE, true, false, true, true);
	}
	
	public static void test(
			boolean soap11, TipoServizio tipoServizio, String operazione,
			boolean rispostaBackendVuota, boolean trasformaRisposta, boolean generaBackendContentLength, boolean expectedContentLength) throws Exception {
		test(
				soap11, tipoServizio, operazione,
				rispostaBackendVuota, trasformaRisposta, generaBackendContentLength, expectedContentLength, false);
	}
	public static void test(
			boolean soap11, TipoServizio tipoServizio, String operazioneParam,
			boolean rispostaBackendVuota, boolean trasformaRisposta, boolean generaBackendContentLength, boolean expectedContentLength, boolean erroreGovWay) throws Exception {
		
		String api = API;
		
		String operazione = operazioneParam;
		if(rispostaBackendVuota) {
			operazione = "ping."+operazioneParam;
		}
		
		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione+"/"
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+operazione+"/";
		
		HttpRequest request = new HttpRequest();
		
		request.setReadTimeout(20000);

		String applicaTrasformazioneRisposta = "<trasforma>true</trasforma>";
		
		String contentType= null;
		byte[]content=null;
		if(soap11) {
			content =
					trasformaRisposta ? 
							Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE, applicaTrasformazioneRisposta).getBytes()
							:
							Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes();
			contentType= HttpConstants.CONTENT_TYPE_SOAP_1_1;
		}
		else {
			content =
					trasformaRisposta ? 
							Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE, applicaTrasformazioneRisposta).getBytes()
							:
							Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes();
			contentType= HttpConstants.CONTENT_TYPE_SOAP_1_2;
		}
		
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		if(soap11) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, operazione);
		}
		
		request.setContent(content);

		url = url + "?";
		if(generaBackendContentLength) {
			url = url + "forceContentLength=true&";
		}
		if(erroreGovWay) {
			url = url + "generoErroreNonMettendoTipo=errore";
		}
		else {
			if(rispostaBackendVuota) {
				url = url + "tipoTest=ping";
			}
			else {
				url = url + "tipoTest=echo";
			}
		}
		
		request.setUrl(url);
		
		HttpResponse response = HttpUtilities.httpInvoke(request);

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		String msg = "idTransazione:"+idTransazione;
		
		byte[]responseContent = response.getContent();
		if(erroreGovWay) {
			assertNotNull(msg, responseContent);
			assertTrue(msg, responseContent.length>0);	
		}
		else if(rispostaBackendVuota) {
			boolean empty = responseContent==null || responseContent.length==0;
			assertTrue(msg, empty);
		}
		else {
			assertNotNull(msg, responseContent);
			assertTrue(msg, responseContent.length>0);
			if(trasformaRisposta) {
				assertNotEquals(msg, content.length,responseContent.length);
			}
			else {
				assertEquals(msg, content.length,responseContent.length);
			}
		}
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		int esito = 200;
		if(erroreGovWay) {
			esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA);
			esito = 500;
		}
		verifyOk(soap11, response, esito, rispostaBackendVuota, msg); // il codice http e' gia' stato impostato
		HttpLibraryMode lib = null;
		DBVerifier.verify(idTransazione, esitoExpected, lib);
		
		if(erroreGovWay) {
			if(expectedContentLength) {
				String cL = response.getHeaderFirstValue(HttpConstants.CONTENT_LENGTH);
				assertNotNull(msg+" contentLength["+cL+"]", cL);
				long cLong = Long.parseLong(cL);
				assertEquals((msg+" responseContentLength["+responseContent.length+"] contentLength["+cLong+"]"), responseContent.length,cLong);
			}
			else {
				String encoding = response.getHeaderFirstValue(HttpConstants.TRANSFER_ENCODING);
				assertEquals(msg+" encoding["+encoding+"]", encoding,HttpConstants.TRANSFER_ENCODING_VALUE_CHUNCKED);
			}
		}
		else if(!rispostaBackendVuota) {
			if(expectedContentLength) {
				String cL = response.getHeaderFirstValue(HttpConstants.CONTENT_LENGTH);
				assertNotNull(msg+" contentLength["+cL+"]", cL);
				long cLong = Long.parseLong(cL);
				if(trasformaRisposta) {
					assertNotEquals((msg+" requestContentLength["+responseContent.length+"] contentLength["+cLong+"]"), content.length,cLong);
				}
				else {
					assertEquals((msg+" requestContentLength["+responseContent.length+"] contentLength["+cLong+"]"), content.length,cLong);
				}
				assertEquals((msg+" responseContentLength["+responseContent.length+"] contentLength["+cLong+"]"), responseContent.length,cLong);
			}
			else {
				String encoding = response.getHeaderFirstValue(HttpConstants.TRANSFER_ENCODING);
				assertEquals(msg+" encoding["+encoding+"]", encoding,HttpConstants.TRANSFER_ENCODING_VALUE_CHUNCKED);
			}
		}
		else {
			String cL = response.getHeaderFirstValue(HttpConstants.CONTENT_LENGTH);
			String encoding = response.getHeaderFirstValue(HttpConstants.TRANSFER_ENCODING);
			boolean emptyContentLength = (cL==null) || ("0".equals(cL));
			boolean emptyChunked = (encoding==null) || (HttpConstants.TRANSFER_ENCODING_VALUE_CHUNCKED.equals(encoding));
			assertTrue(msg + "emptyContentLength["+cL+"]:"+emptyContentLength+" emptyChunked["+encoding+"]:"+emptyChunked, (emptyContentLength || emptyChunked));
		}

	}
	public static void verifyOk(boolean soap11, HttpResponse response, int code, boolean rispostaBackendVuota, String msg) {
		
		assertEquals(code, response.getResultHTTPOperation());
		if(rispostaBackendVuota) {
			assertNull(msg+" contentType["+response.getContentType()+"]", response.getContentType());
		}
		else {
			String contentTypeAtteso = null;
			if(soap11) {
				contentTypeAtteso = HttpConstants.CONTENT_TYPE_SOAP_1_1;
			}
			else {
				contentTypeAtteso = HttpConstants.CONTENT_TYPE_SOAP_1_2;
			}
			if(code==500) {
				String contentTypeAtteso2 = contentTypeAtteso;
				contentTypeAtteso2 =contentTypeAtteso2+";charset=utf-8";
				boolean esito = contentTypeAtteso.equals(response.getContentType()) ||  contentTypeAtteso2.equals(response.getContentType());
				assertTrue(msg + "contentType["+response.getContentType()+"]", esito);
			}
			else {
				assertEquals(msg + "contentType["+response.getContentType()+"]", contentTypeAtteso, response.getContentType());
			}
		}
		
	}
}
