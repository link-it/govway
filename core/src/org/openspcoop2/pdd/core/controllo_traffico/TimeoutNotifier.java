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
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.ITimeoutNotifier;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

/**
 * TimeoutNotifier
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimeoutNotifier implements ITimeoutNotifier{

	private Context context;
	private SogliaReadTimeout soglia;
	private TimeoutNotifierType type;
	private Logger log;
	private IProtocolFactory<?> protocolFactory;
	private boolean saveInContext;
	public TimeoutNotifier(Context context, IProtocolFactory<?> protocolFactory,
			SogliaReadTimeout soglia, TimeoutNotifierType type, Logger log,
			boolean saveInContext) {
		this.context = context;
		this.protocolFactory = protocolFactory;
		this.soglia = soglia;
		this.type = type;
		this.log = log;
		this.saveInContext = saveInContext;
	}
	
	@Override
	public void notify(long count) {
		if(this.context!=null) {
			
			if(this.saveInContext) {
				boolean alreadyExists = false;
				if(this.type!=null) {
					switch (this.type) {
					case CONNECTION:
						alreadyExists = !GeneratoreMessaggiErrore.addPddContextInfoControlloTrafficoConnectionTimeout(this.context);
						break;
					case REQUEST:
						alreadyExists = !GeneratoreMessaggiErrore.addPddContextInfoControlloTrafficoReadRequestTimeout(this.context, true);
						break;
					case WAIT_RESPONSE:
						alreadyExists = !GeneratoreMessaggiErrore.addPddContextInfoControlloTrafficoReadTimeout(this.context, true);
						break;
					case RECEIVE_RESPONSE:
						alreadyExists = !GeneratoreMessaggiErrore.addPddContextInfoControlloTrafficoReadResponseTimeout(this.context, true);
						break;
					}
				}else {
					alreadyExists = !GeneratoreMessaggiErrore.addPddContextInfoControlloTrafficoReadTimeout(this.context, true);
				}
				
				if(alreadyExists) {
					return; // già registrato (succede in caso di timeout mentre si sta consegnando verso il client con metodo disconnect())
				}
			}
			
			registraEvento();
		}
		
		Date dataEventoPolicyViolated = DateManager.getDate();
		
		CategoriaEventoControlloTraffico tipoEvento = TimeoutNotifierType.toCategoriaEventoControlloTraffico(this.type);
					
		try {
			
			EsitiProperties esitiProperties = EsitiProperties.getInstance(this.log, this.protocolFactory);
			EsitoTransazioneName esito = TimeoutNotifierType.toEsitoTransazioneName(this.type);
			String descriptionPolicyViolated = esitiProperties.getEsitoDescription(esitiProperties.convertoToCode(esito));
			descriptionPolicyViolated = descriptionPolicyViolated+" (soglia:"+this.soglia.getSogliaMs()+" ms)";
			
			NotificatoreEventi.getInstance().log(tipoEvento, 
					this.soglia.getIdConfigurazione(), this.soglia.getConfigurazione(),
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
				TipoEvento tipoEvento = TimeoutNotifierType.toTipoEvento(this.type);
				try {
					tr.addEventoGestione(tipoEvento.getValue()
							+"_"+
							CodiceEventoControlloTraffico.VIOLAZIONE.getValue()
							+"_"+
							this.soglia.getIdConfigurazione()
							);
				}catch(Exception t) {
					this.log.error("Associazione evento alla transazione non riuscita: "+t.getMessage(),t);
				}
			}
		}
	}
}
