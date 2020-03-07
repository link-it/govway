/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.sdk.tracciamento;

import java.io.Serializable;

/**
 * Oggetto contenente informazioni per la ricerca di loggedEntry
 * 
 * @author Stefano Corallo (corallo@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
* @author $Author$
* @version $Rev$, $Date$
 */

public class FiltroRicercaTracceConPaginazione extends FiltroRicercaTracce implements Serializable{


	private static final long serialVersionUID = 2103096411857601491L;
    
    protected int limit;
    protected int offset;
    protected boolean asc = true;

	    
    public FiltroRicercaTracceConPaginazione() {
    	super();
	}
    public FiltroRicercaTracceConPaginazione(FiltroRicercaTracce filtroTracce) {
    	super();
    	
    	this.maxDate = filtroTracce.maxDate;
    	this.minDate = filtroTracce.minDate;

    	this.tipoTraccia = filtroTracce.tipoTraccia;
    	this.tipoPdD = filtroTracce.tipoPdD;
    	this.dominio = filtroTracce.dominio;

    	this.idBusta = filtroTracce.idBusta;
    	this.idBustaRichiesta = filtroTracce.idBustaRichiesta;
    	this.idBustaRisposta = filtroTracce.idBustaRisposta;
    	this.riferimentoMessaggio = filtroTracce.riferimentoMessaggio;
    	this.ricercaSoloBusteErrore = filtroTracce.ricercaSoloBusteErrore;
    	this.informazioniProtocollo = filtroTracce.informazioniProtocollo;

    	this.servizioApplicativoFruitore = filtroTracce.servizioApplicativoFruitore;
    	this.servizioApplicativoErogatore = filtroTracce.servizioApplicativoErogatore;

    	this.idCorrelazioneApplicativa = filtroTracce.idCorrelazioneApplicativa;
    	this.idCorrelazioneApplicativaRisposta = filtroTracce.idCorrelazioneApplicativaRisposta;
    	this.idCorrelazioneApplicativaOrMatch = filtroTracce.idCorrelazioneApplicativaOrMatch;

    	this.protocollo = filtroTracce.protocollo;

    	this.properties = filtroTracce.properties;
	}
    
    
	public int getOffset() {
		return this.offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getLimit() {
		return this.limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
    public boolean isAsc() {
		return this.asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}
	
	@Override
	public String toString() {	
		return super.toString() + " " +  
			" offset ["+this.offset+"]" +
			" limit  ["+this.limit+"]" +
			" asc  ["+this.asc+"]";
	}

 
}


