/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.id.UniqueIdentifierException;

/**
 * Contesto di attivazione del servizio RicezioneBuste.
 * Il contesto e' serializzabile solo se il messaggio viene salvato come byte[].
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RicezioneBusteContext extends AbstractContext implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	/** Tracciamento Abilitato */
	private boolean tracciamentoAbilitato;
	/** IDCorrelazione applicativa */
	private String idCorrelazioneApplicativa = null;
	/** Servizio applicativo fruitore */
	private String identitaServizioApplicativoFruitore = null;
	/** Forza gestione messaggio come messaggio che deve essere ricevuto senza duplicati, in caso il messaggio sia gia' in processamento */
	private boolean forzaFiltroDuplicati_msgGiaInProcessamento = false;
	/** Proprieta' per filtro PA */
	private Map<String, String> filtroPA = new 	HashMap<String, String>();

	
	/** Costruttore */
	public RicezioneBusteContext(IDService idModuloAsIDService,Date dataAccettazioneRichiesta,RequestInfo requestInfo) throws UniqueIdentifierException{
		super(idModuloAsIDService,dataAccettazioneRichiesta,requestInfo);
	}
	private RicezioneBusteContext(IDService idModuloAsIDService){
		super(idModuloAsIDService);
	}
	public static RicezioneBusteContext newRicezioneBusteContext(IDService idModuloAsIDService,Date dataAccettazioneRichiesta,RequestInfo requestInfo){
		RicezioneBusteContext context = new RicezioneBusteContext(idModuloAsIDService);
		context.dataAccettazioneRichiesta = dataAccettazioneRichiesta;
		context.identitaPdD = requestInfo.getIdentitaPdD();
		context.pddContext = new PdDContext();
		context.requestInfo = requestInfo;
		return context;
	}
	
	
	public void addProprietaFiltroPortaApplicativa(String nome,String valore){
		if(nome!=null && valore!=null && (this.filtroPA.containsKey(nome)==false) )
			this.filtroPA.put(nome, valore);
	}	
	public Map<String, String> getProprietaFiltroPortaApplicativa(){
		return this.filtroPA;
	}
	public void setProprietaFiltroPortaApplicativa(Map<String, String> filtroPA) {
		this.filtroPA = filtroPA;
	}
	
	
	/**
	 * Indicazione se il tracciamento e' abilitato
	 * 
	 * @return Indicazione se il tracciamento e' abilitato
	 */
	public boolean isTracciamentoAbilitato() {
		return this.tracciamentoAbilitato;
	}

	/**
	 * Indicazione se il tracciamento e' abilitato
	 * 
	 * @param tracciamentoAbilitato
	 */
	public void setTracciamentoAbilitato(boolean tracciamentoAbilitato) {
		this.tracciamentoAbilitato = tracciamentoAbilitato;
	}

	


	public boolean isForzaFiltroDuplicati_msgGiaInProcessamento() {
		return this.forzaFiltroDuplicati_msgGiaInProcessamento;
	}

	public void setForzaFiltroDuplicati_msgGiaInProcessamento(
			boolean forzaFiltroDuplicati_msgGiaInProcessamento) {
		this.forzaFiltroDuplicati_msgGiaInProcessamento = forzaFiltroDuplicati_msgGiaInProcessamento;
	}



	public String getIdCorrelazioneApplicativa() {
		return this.idCorrelazioneApplicativa;
	}

	public void setIdCorrelazioneApplicativa(String idCorrelazioneApplicativa) {
		this.idCorrelazioneApplicativa = idCorrelazioneApplicativa;
	}

	
	public String getIdentitaServizioApplicativoFruitore() {
		return this.identitaServizioApplicativoFruitore;
	}

	public void setIdentitaServizioApplicativoFruitore(
			String identitaServizioApplicativoFruitore) {
		this.identitaServizioApplicativoFruitore = identitaServizioApplicativoFruitore;
	}

}

