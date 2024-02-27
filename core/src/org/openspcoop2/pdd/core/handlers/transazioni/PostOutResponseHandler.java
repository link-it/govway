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
package org.openspcoop2.pdd.core.handlers.transazioni;

import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.logger.transazioni.FaseTracciamento;
import org.openspcoop2.pdd.logger.transazioni.InformazioniTransazione;
import org.openspcoop2.pdd.logger.transazioni.TracciamentoManager;

/**     
 * PostOutResponseHandler
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PostOutResponseHandler extends LastPositionHandler implements  org.openspcoop2.pdd.core.handlers.PostOutResponseHandler{

	/**
	 * Indicazione sull'inizializzazione dell'handler
	 */
	private Boolean initialized = false;
	
	/**
	 * TracciamentoManager
	 */
	private TracciamentoManager tracciamentoManager = null;

	private synchronized void init() throws HandlerException {

		if(this.initialized==null || !this.initialized.booleanValue()){

			try{
				this.tracciamentoManager = new TracciamentoManager(FaseTracciamento.POST_OUT_RESPONSE);				
			}catch(Exception e){
				throw new HandlerException("Errore durante l'inizializzazione del gestore del tracciamento: "+e.getMessage(),e);
			}

			this.initialized = true;
		}

	}



	@Override
	public void invoke(PostOutResponseContext context) throws HandlerException {
		
		if(this.initialized==null || !this.initialized.booleanValue()){
			init();
		}
		if(!this.tracciamentoManager.isTransazioniEnabled()) {
			return;
		}
		
		InformazioniTransazione info = new InformazioniTransazione(context);
		
		this.tracciamentoManager.invoke(info, context.getEsito());
		
	}
	
}

