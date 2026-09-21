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


package org.openspcoop2.core.protocolli.trasparente.testsuite.registrazione_messaggi.classes;

import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutRequestContext;

/**
* Fotografa il repository di overflow subito prima che la richiesta venga serializzata verso il backend.
*
* E' il punto in cui il buffer della richiesta, gia' riempito dalla validazione dei contenuti, non e'
* ancora stato rilasciato: se il payload supera la soglia il file deve essere presente.
*
* @author $Author$
* @version $Rev$, $Date$
*/
public class DumpBinarioSnapshotOutRequestHandler extends AbstractDumpBinarioSnapshotHandler
		implements org.openspcoop2.pdd.core.handlers.OutRequestHandler {

	public static final String FASE = "outRequest";

	@Override
	public void invoke(OutRequestContext context) throws HandlerException {
		snapshot(context.getPddContext(), FASE);
	}

}
