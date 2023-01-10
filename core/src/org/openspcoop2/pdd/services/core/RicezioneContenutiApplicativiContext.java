/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.services.core;

import java.util.Date;

import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.id.UniqueIdentifierException;

/**
 * Contesto di attivazione del servizio RicezioneContenutiApplicativi
 * Il contesto e' serializzabile solo se il messaggio viene salvato come byte[].
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RicezioneContenutiApplicativiContext extends AbstractContext implements java.io.Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** InvocazionePortaDelegata per Riferimento */
	private boolean invocazionePDPerRiferimento;
	private String idInvocazionePDPerRiferimento;

	/** Header di trasporto per l'Integrazione della richiesta (IntegrationManager) */
	private HeaderIntegrazione headerIntegrazioneRichiesta;
	/** Indicazione di produrre i messaggi di errore come XML */
	private boolean forceFaultAsXML = false;
	
	/** GestioneErroreApplicativo (per eventuali analisi della risposta)
	 * Le proprieta' dell'errore applicativo sono specifiche del servizio applicativo */
	private ProprietaErroreApplicativo proprietaErroreAppl;
	
	
	
	/** Costruttore */
	public RicezioneContenutiApplicativiContext(IDService idModuloAsIDService,Date dataAccettazioneRichiesta,RequestInfo requestInfo) throws UniqueIdentifierException{
		super(idModuloAsIDService,dataAccettazioneRichiesta,requestInfo);
	}
	private RicezioneContenutiApplicativiContext(IDService idModuloAsIDService){
		super(idModuloAsIDService);
	}
	public static RicezioneContenutiApplicativiContext newRicezioneContenutiApplicativiContext(IDService idModuloAsIDService,Date dataAccettazioneRichiesta,RequestInfo requestInfo){
		RicezioneContenutiApplicativiContext context = new RicezioneContenutiApplicativiContext(idModuloAsIDService);
		context.dataAccettazioneRichiesta=dataAccettazioneRichiesta;
		context.identitaPdD = requestInfo.getIdentitaPdD();
		context.pddContext = new PdDContext();
		context.requestInfo = requestInfo;
		return context;
	}
	

	/**
	 * Ritorna le proprieta' di gestione di un errore Applicativo utilizzate per il servizio applicativo invocante
	 * 
	 * @return proprieta' di gestione di un errore Applicativo utilizzate per il servizio applicativo invocante
	 */
	public ProprietaErroreApplicativo getProprietaErroreAppl() {
		return this.proprietaErroreAppl;
	}

	/**
	 * Imposta le proprieta' di gestione di un errore Applicativo utilizzate per il servizio applicativo invocante
	 * 
	 * @param proprietaErroreAppl le proprieta' di gestione di un errore Applicativo utilizzate per il servizio applicativo invocante
	 */
	public void setProprietaErroreAppl(
			ProprietaErroreApplicativo proprietaErroreAppl) {
		this.proprietaErroreAppl = proprietaErroreAppl;
	}

	/**
	 * Ritorna l'indicazione se l'invocazione della PD avviene per riferimento.
	 * 
	 * @return indicazione se l'invocazione della PD avviene per riferimento.
	 */
	public boolean isInvocazionePDPerRiferimento() {
		return this.invocazionePDPerRiferimento;
	}

	/**
	 * Indicazione se l'invocazione della PD avviene per riferimento.
	 * 
	 * @param invocazionePDPerRiferimento Indicazione se l'invocazione della PD avviene per riferimento.
	 */
	public void setInvocazionePDPerRiferimento(boolean invocazionePDPerRiferimento) {
		this.invocazionePDPerRiferimento = invocazionePDPerRiferimento;
	}

	public String getIdInvocazionePDPerRiferimento() {
		return this.idInvocazionePDPerRiferimento;
	}
	public void setIdInvocazionePDPerRiferimento(
			String idInvocazionePDPerRiferimento) {
		this.idInvocazionePDPerRiferimento = idInvocazionePDPerRiferimento;
	}
	
	/**
	 * @return the headerIntegrazioneRichiesta
	 */
	public HeaderIntegrazione getHeaderIntegrazioneRichiesta() {
		return this.headerIntegrazioneRichiesta;
	}

	/**
	 * @param headerIntegrazioneRichiesta the headerIntegrazioneRichiesta to set
	 */
	public void setHeaderIntegrazioneRichiesta(
			HeaderIntegrazione headerIntegrazioneRichiesta) {
		this.headerIntegrazioneRichiesta = headerIntegrazioneRichiesta;
	}

	
	public boolean isForceFaultAsXML() {
		return this.forceFaultAsXML;
	}

	public void setForceFaultAsXML(boolean forceFaultAsXML) {
		this.forceFaultAsXML = forceFaultAsXML;
	}
	
}
