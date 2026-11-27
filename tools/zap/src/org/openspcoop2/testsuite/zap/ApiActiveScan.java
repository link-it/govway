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
package org.openspcoop2.testsuite.zap;

import org.openspcoop2.utils.Utilities;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

/**
 * Scansione attiva delle API, eseguita sulle richieste generate dall'import del contratto (OpenAPI o WSDL).
 * Le richieste usano i valori di esempio prodotti da ZAP: la scansione verifica soprattutto la validazione
 * dei contratti e la gestione degli errori di GovWay.
 * Si abilita con la proprietà di sistema 'zaproxy.api.active=true'; la durata massima (minuti) è indicata
 * da 'zaproxy.api.active.maxDurationMinutes'.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiActiveScan {
	
	private ApiActiveScan() {}

	private static final String PROP_ENABLED = "zaproxy.api.active";
	private static final String PROP_MAX_DURATION = "zaproxy.api.active.maxDurationMinutes";
	private static final int DEFAULT_MAX_DURATION_MINUTES = 20;
	
	public static boolean isEnabled() {
		return "true".equalsIgnoreCase(readProperty(PROP_ENABLED));
	}
	
	private static String readProperty(String name) {
		String v = System.getProperty(name);
		if(v==null || v.trim().isEmpty() || v.startsWith("${")) {
			return null;
		}
		return v.trim();
	}
	
	public static void scan(ClientApi api, String targetUrl, String contextId) throws ClientApiException {
		
		// Le risposte delle richieste importate devono essere state analizzate dallo scanner passivo
		ConsoleUtils.waitPassiveScanInProgress(api);
		
		if(!isEnabled()) {
			LoggerManager.info("Scansione attiva delle API non abilitata ('"+PROP_ENABLED+"')");
			return;
		}
		
		int maxDuration = ConsoleUtils.readIntProperty(PROP_MAX_DURATION, DEFAULT_MAX_DURATION_MINUTES);
		api.ascan.setOptionMaxScanDurationInMins(maxDuration);
		
		LoggerManager.info("Active (max "+maxDuration+" minuti) su '"+targetUrl+"' ...");
		ApiResponse resp = api.ascan.scan(targetUrl, ConsoleParams.RECURSE_ENABLED, ConsoleParams.INSCOPE_ONLY_ENABLED, null, null, null, Integer.valueOf(contextId));
		String scanId = ConsoleUtils.getScanId(resp);
		
		// ZAP ferma da solo la scansione allo scadere della durata massima; il margine copre la chiusura delle regole in corso
		long timeout = (maxDuration + 2) * 60000l;
		long start = System.currentTimeMillis();
		int lastProgress = -1;
		while(true) {
			Utilities.sleep(5000);
			int progress = Integer.parseInt(((ApiResponseElement) api.ascan.status(scanId)).getValue());
			if(progress!=lastProgress) {
				LoggerManager.info("Active scan progress (id:"+scanId+") : " + progress + "%");
				lastProgress = progress;
			}
			if(progress>=100) {
				break;
			}
			if((System.currentTimeMillis() - start) > timeout) {
				api.ascan.stop(scanId);
				LoggerManager.info("Active scan (id:"+scanId+") fermata per timeout ("+(timeout/60000l)+" minuti) al "+progress+"%");
				break;
			}
		}
		
		ConsoleUtils.waitPassiveScanInProgress(api);
		LoggerManager.info("Active complete");
	}
}
