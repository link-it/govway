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
package org.openspcoop2.core.statistiche.server;

import java.util.Properties;

import org.openspcoop2.utils.Costanti;
import org.openspcoop2.utils.properties.InstanceProperties;
import org.slf4j.Logger;

/**
 * StatisticheServerInstanceProperties
 *
 * Gestisce le properties del server statistiche con supporto per override
 * da file esterni in una directory di configurazione.
 *
 * Ordine di ricerca delle properties (prima trovata vince):
 * 1. Variabile di sistema STATISTICHE_SERVER_PROPERTIES (path al file)
 * 2. Proprietà Java -DSTATISTICHE_SERVER_PROPERTIES (path al file)
 * 3. Variabile di sistema OPENSPCOOP2_LOCAL_HOME + /statistiche-server.local.properties
 * 4. Proprietà Java -DOPENSPCOOP2_LOCAL_HOME + /statistiche-server.local.properties
 * 5. Classpath /statistiche-server.properties (dentro il WAR)
 * 6. Directory di configurazione (confDirectory) + /statistiche-server.local.properties
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticheServerInstanceProperties extends InstanceProperties {

	/** Nome della variabile di sistema/java per specificare il path al file di properties */
	private static final String STATISTICHE_SERVER_PROPERTIES = "STATISTICHE_SERVER_PROPERTIES";

	/** Nome del file di properties locale (per override) */
	private static final String LOCAL_PROPERTIES_FILE = "statistiche-server_local.properties";

	/** Path del file di properties locale */
	private static final String LOCAL_PROPERTIES_PATH = "/" + LOCAL_PROPERTIES_FILE;

	StatisticheServerInstanceProperties(Properties reader, Logger log, String confDirectory) {
		super(Costanti.OPENSPCOOP2_LOCAL_HOME, reader, log);

		// Imposta il supporto per file locali di override
		// Cerca in:
		// - variabile sistema/java STATISTICHE_SERVER_PROPERTIES
		// - OPENSPCOOP2_LOCAL_HOME/statistiche-server.local.properties
		// - confDirectory/statistiche-server.local.properties
		super.setLocalFileImplementation(
			STATISTICHE_SERVER_PROPERTIES,
			LOCAL_PROPERTIES_PATH,
			confDirectory
		);
	}
}
