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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.tls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.jmx.MonitoraggioRisorse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
 * StatoPoolConnessioni
 *
 * Legge, tramite la risorsa JMX 'MonitoraggioRisorse', lo stato dei ConnectionManager HTTPCore5 (BIO o NIO)
 * mostrato anche in console nella sezione 'Thread Attivi' e restituisce le chiavi dei pool che riguardano un keystore.
 *
 * Formato atteso (con 'closeIdleConnections.debug=true', default):
 * <pre>
 * ConnectionManager attivi: N
 * (&lt;chiave&gt;) stats: [leased: 0; pending: 0; available: 1; max: 400]
 * (&lt;chiave&gt;) route [...]: [...]
 * ConnectionManager dismessi: M
 * (&lt;chiave&gt;) stats: [...]
 * </pre>
 * La sezione 'ConnectionManager dismessi' elenca i pool sostituiti in seguito a un aggiornamento
 * del contenuto dei keystore e non ancora chiusi.
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatoPoolConnessioni {

	static final String PREFIX_ATTIVI = "ConnectionManager attivi:";
	static final String PREFIX_DISMESSI = "ConnectionManager dismessi:";
	private static final String STATS = ") stats: ";

	private List<String> attivi = new ArrayList<>();
	private List<String> dismessi = new ArrayList<>();
	private String stato;

	public static StatoPoolConnessioni read(Logger log, boolean nio, String keystorePath) throws Exception {
		String methodName = nio ? MonitoraggioRisorse.NIO_HTTP_CLIENT_CONNECTION_MANAGER_STATUS : MonitoraggioRisorse.BIO_HTTP_CLIENT_CONNECTION_MANAGER_STATUS;
		Map<String,String> queryParams = Map.of(
				"resourceName", CostantiPdD.JMX_MONITORAGGIO_RISORSE,
				"methodName", methodName
			);
		String jmxUrl = Utils.buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
		String stato = new String(HttpUtilities.getHTTPResponse(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password")).getContent());
		log.info("Stato pool connessioni {}:\n{}", (nio ? "NIO" : "BIO"), stato);
		return new StatoPoolConnessioni(stato, keystorePath);
	}

	StatoPoolConnessioni(String stato, String keystorePath) {
		this.stato = stato;
		boolean sezioneDismessi = false;
		for (String line : stato.split("\n")) {
			String l = line.trim();
			if(l.startsWith(PREFIX_ATTIVI)) {
				sezioneDismessi = false;
			}
			else if(l.startsWith(PREFIX_DISMESSI)) {
				sezioneDismessi = true;
			}
			else if(l.startsWith("(") && l.contains(STATS)) {
				String key = l.substring(1, l.indexOf(STATS));
				if(key.contains(keystorePath)) {
					if(sezioneDismessi) {
						this.dismessi.add(key);
					}
					else {
						this.attivi.add(key);
					}
				}
			}
		}
	}

	public List<String> getAttivi() {
		return this.attivi;
	}
	public List<String> getDismessi() {
		return this.dismessi;
	}
	public String getStato() {
		return this.stato;
	}
}
