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


package org.openspcoop2.pdd.core.handlers;

import java.util.Date;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.services.skeleton.Operazione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;

/**
 * Informazioni di consultazione del servizio di IntegrationManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IntegrationManagerResponseContext extends IntegrationManagerRequestContext {
	
	/** Servizio applicativo fruitore */
	private IDServizioApplicativo servizioApplicativo;
	
	/** Dimensione dell'eventuale messaggio ritornato in bytes */
	private Long dimensioneMessaggioBytes;

	/** Esito */
	private EsitoTransazione esito;
	
	/** Tempo di completamento dell'operazione */
	private Date dataCompletamentoOperazione;

	/** Costruttori */
	public IntegrationManagerResponseContext(Date dataRichiestaOperazione,Operazione tipoOperazione,
			PdDContext pddContext,Logger logger,IProtocolFactory<?> protocolFactory) {
		super(dataRichiestaOperazione,tipoOperazione,pddContext,logger,protocolFactory);
	}

	public IDServizioApplicativo getServizioApplicativo() {
		return this.servizioApplicativo;
	}
	public void setServizioApplicativo(IDServizioApplicativo nomeServizioApplicativo) {
		this.servizioApplicativo = nomeServizioApplicativo;
	}
	public Long getDimensioneMessaggioBytes() {
		return this.dimensioneMessaggioBytes;
	}
	public void setDimensioneMessaggioBytes(Long dimensioneMessaggioBytes) {
		this.dimensioneMessaggioBytes = dimensioneMessaggioBytes;
	}
	public EsitoTransazione getEsito() {
		return this.esito;
	}
	public void setEsito(EsitoTransazione esito) {
		this.esito = esito;
	}
	public Date getDataCompletamentoOperazione() {
		return this.dataCompletamentoOperazione;
	}
	public void setDataCompletamentoOperazione(Date dataCompletamentoOperazione) {
		this.dataCompletamentoOperazione = dataCompletamentoOperazione;
	}
}
