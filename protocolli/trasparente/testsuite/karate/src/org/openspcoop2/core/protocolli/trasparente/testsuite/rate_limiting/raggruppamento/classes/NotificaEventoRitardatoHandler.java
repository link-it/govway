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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.raggruppamento.classes;

import java.util.Date;

import org.openspcoop2.pdd.core.controllo_traffico.CategoriaEventoControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.NotificatoreEventi;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InRequestContext;
import org.openspcoop2.utils.date.DateManager;

/**
* Registra presso il notificatore degli eventi una violazione di policy con una data antecedente all'ultima elaborazione del timer degli eventi.
*
* Riproduce in modo deterministico la sequenza che si verifica quando il timer degli eventi viene eseguito tra la rilevazione
* della violazione di una policy (in cui viene presa la data) e la sua registrazione in memoria.
*
* L'handler non effettua alcuna operazione se la richiesta non contiene l'header che indica l'identificativo dell'evento.
*
* @author $Author$
* @version $Rev$, $Date$
*/
public class NotificaEventoRitardatoHandler implements org.openspcoop2.pdd.core.handlers.InRequestHandler {

	public static final String HEADER_ID_EVENTO = "govway-testsuite-evento-ritardato-id";
	public static final String HEADER_SECONDI_RITARDO = "govway-testsuite-evento-ritardato-secondi";

	@Override
	public void invoke(InRequestContext context) throws HandlerException {

		if(context==null || context.getConnettore()==null || context.getConnettore().getUrlProtocolContext()==null) {
			return;
		}

		String idEvento = context.getConnettore().getUrlProtocolContext().getHeaderFirstValue(HEADER_ID_EVENTO);
		if(idEvento==null || idEvento.isEmpty()) {
			return;
		}
		String secondiRitardo = context.getConnettore().getUrlProtocolContext().getHeaderFirstValue(HEADER_SECONDI_RITARDO);

		try {
			long ritardoMs = Long.parseLong(secondiRitardo) * 1000l;
			Date dataViolazione = new Date(DateManager.getTimeMillis() - ritardoMs);
			NotificatoreEventi.getInstance().log(CategoriaEventoControlloTraffico.POLICY_API,
					idEvento, null,
					dataViolazione, "Violazione registrata in ritardo dalla testsuite");
		}catch(Exception e) {
			throw new HandlerException(e.getMessage(),e);
		}

	}

}
