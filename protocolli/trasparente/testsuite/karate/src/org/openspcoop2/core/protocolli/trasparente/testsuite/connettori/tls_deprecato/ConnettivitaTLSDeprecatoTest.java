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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.tls_deprecato;

import static org.junit.Assert.fail;

import java.util.Map;
import java.util.function.Function;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.jmx.ConfigurazionePdD;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * Verifica di connettività dei connettori https, come eseguita dalla console tramite la risorsa JMX 'ConfigurazionePdD'
 * (operazione 'checkConnettoreByNome'), per ogni tipologia SSL contro tutte le varianti del backend {@link TLSDeprecatoMockServer}.
 *
 * La verifica di connettività non usa l'header della testsuite che sceglie la libreria: la libreria è quella impostata
 * sul connettore ('httpImpl') o, se assente, quella di default ('org.openspcoop2.pdd.connettori.remoteAccessUtility.library').
 * Vengono quindi verificati, per ogni tipologia, i connettori:
 * <ul>
 *   <li>con libreria 'java.net.HttpURLConnection' e 'org.apache.hc.client5' (erogazione {@link TipologiaSSL#API_CONNETTIVITA});</li>
 *   <li>senza libreria impostata (erogazione {@link TipologiaSSL#API}, usata anche dai test sul traffico).</li>
 * </ul>
 *
 * Per un esito atteso OK, protocollo e cipher negoziati vengono letti dal backend (ultimo handshake completato).
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettivitaTLSDeprecatoTest extends TLSDeprecatoBase {

	@Test
	public void urlConnection() throws Exception {
		verifica("java.net.HttpURLConnection", TipologiaSSL::getNomeConnettoreURLConnection);
	}

	@Test
	public void httpCore() throws Exception {
		verifica("org.apache.hc.client5", TipologiaSSL::getNomeConnettoreHttpCore);
	}

	@Test
	public void libreriaDefault() throws Exception {
		verifica("libreria di default", TipologiaSSL::getNomeConnettore);
	}

	private void verifica(String libreria, Function<TipologiaSSL, String> connettore) throws Exception {
		StringBuilder scostamenti = new StringBuilder();
		for (TipologiaSSL tipologia : TipologiaSSL.values()) {
			Risultati risultati = new Risultati("Connettività '"+tipologia.getSslType()+"' "+libreria+" ("+informazioniSSL.getProtocolli(tipologia)+")");
			for (VarianteServerTLS variante : VarianteServerTLS.values()) {
				mock.start(variante);
				EsitoHandshake atteso = informazioniSSL.calcolaAtteso(tipologia, variante);
				risultati.add(variante, atteso, check(connettore.apply(tipologia)), null);
			}
			String msg = risultati.report();
			if(msg!=null) {
				scostamenti.append(msg).append("\n");
			}
		}
		if(scostamenti.length()>0) {
			fail(scostamenti.toString());
		}
	}

	private EsitoHandshake check(String nomeConnettore) throws Exception {
		Map<String,String> queryParams = Map.of(
				CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME, CostantiPdD.JMX_CONFIGURAZIONE_PDD,
				CostantiPdD.CHECK_STATO_PDD_METHOD_NAME, ConfigurazionePdD.CHECK_CONNETTORE_BY_NOME,
				CostantiPdD.CHECK_STATO_PDD_PARAM_VALUE, nomeConnettore
			);
		String jmxUrl = Utils.buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
		String esito = new String(HttpUtilities.getHTTPResponse(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password")).getContent());
		logCore.debug("Verifica connettività '{}': {}", nomeConnettore, esito);
		if(JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO.equals(esito.trim())) {
			String[] handshake = mock.getUltimoHandshake();
			if(handshake==null) {
				return EsitoHandshake.ok(null, null);
			}
			return EsitoHandshake.ok(handshake[0], handshake.length>1 ? handshake[1] : null);
		}
		return EsitoHandshake.ko(abbrevia(esito));
	}
}
