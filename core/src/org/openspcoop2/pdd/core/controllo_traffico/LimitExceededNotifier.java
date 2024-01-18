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

package org.openspcoop2.pdd.core.controllo_traffico;

import java.util.Date;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.eventi.constants.CodiceEventoControlloTraffico;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.utils.ILimitExceededNotifier;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

/**
 * LimitExceededNotifier
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LimitExceededNotifier implements ILimitExceededNotifier{

	private Context context;
	private SogliaDimensioneMessaggio soglia;
	private Logger log;
	public LimitExceededNotifier(Context context, SogliaDimensioneMessaggio soglia, Logger log) {
		this.context = context;
		this.soglia = soglia;
		this.log = log;
	}
	
	@Override
	public void notify(long count) {
		if(this.context!=null) {
			GeneratoreMessaggiErrore.addContextInfo_ControlloTrafficoPolicyViolated(this.context, false);
			
			registraEvento();
		}
		
		Date dataEventoPolicyViolated = DateManager.getDate();
		
		CategoriaEventoControlloTraffico tipoEvento = null;
		if(this.soglia.isPolicyGlobale()) {
			tipoEvento = CategoriaEventoControlloTraffico.POLICY_GLOBALE;
		}
		else {
			tipoEvento = CategoriaEventoControlloTraffico.POLICY_API;
		}
		
		String descriptionPolicyViolated = "Rilevato messaggio con una dimensione più grande di quella consentita (soglia:"+this.soglia.getSogliaKb()+" kb)";
			
		try {
			NotificatoreEventi.getInstance().log(tipoEvento, 
					this.soglia.getIdPolicyConGruppo(), this.soglia.getConfigurazione(),
					dataEventoPolicyViolated, descriptionPolicyViolated);
		}catch(Exception t) {
			this.log.error("Emissione evento non riuscito: "+t.getMessage(),t);
		}
	}
	
	private void registraEvento() {
		String idTransazione = (String) this.context.getObject(Costanti.ID_TRANSAZIONE);
		if(idTransazione!=null) {
			Transaction tr = null;
			try {
				tr = TransactionContext.getTransaction(idTransazione);
			}catch(TransactionNotExistsException notExists) {
				// ignore
			}
			if(tr!=null) {
				TipoEvento tipoEvento = null;
				if(this.soglia.isPolicyGlobale()) {
					tipoEvento = TipoEvento.RATE_LIMITING_POLICY_GLOBALE;
				}
				else {
					tipoEvento = TipoEvento.RATE_LIMITING_POLICY_API;
				}
				try {
					tr.addEventoGestione(tipoEvento.getValue()
							+"_"+
							CodiceEventoControlloTraffico.VIOLAZIONE.getValue()
							+"_"+
							this.soglia.getNomePolicy()
							);
				}catch(Exception t) {
					this.log.error("Associazione evento alla transazione non riuscita: "+t.getMessage(),t);
				}
			}
		}
	}

}
