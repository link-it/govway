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



package org.openspcoop2.core.registry.driver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.registry.constants.ServiceBinding;

/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaAccordi implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Intervallo inferiore */
	private Date minDate;
	
	/** Intervallo superiore */
	private Date maxDate;
	
	/** Nome Accordo */
	private String nomeAccordo;
	
	/** Versione */
	private Integer versione;
	
	/** Nome */
	private String nomeSoggettoReferente;
	
	/** Tipo */
	private String tipoSoggettoReferente;

	/** Service Binding */
	private ServiceBinding serviceBinding;
	
	private IDAccordoCooperazione idAccordoCooperazione;
	private Boolean servizioComposto;

	private IDGruppo idGruppo;
	
	private String protocollo;
	private List<String> protocolli;

	private boolean order = false;
	
	public boolean isOrder() {
		return this.order;
	}

	public void setOrder(boolean order) {
		this.order = order;
	}

	public List<String> getProtocolli() {
		return this.protocolli;
	}

	public void setProtocolli(List<String> protocolli) {
		this.protocolli = protocolli;
	}
	
	public String getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
	
	public ServiceBinding getServiceBinding() {
		return this.serviceBinding;
	}
	public void setServiceBinding(ServiceBinding serviceBinding) {
		this.serviceBinding = serviceBinding;
	}
	
	/** ProtocolProperty */
	private List<FiltroRicercaProtocolProperty> protocolPropertiesAccordo = new ArrayList<FiltroRicercaProtocolProperty>();

	public List<FiltroRicercaProtocolProperty> getProtocolPropertiesAccordo() {
		return this.protocolPropertiesAccordo;
	}

	public void setProtocolPropertiesAccordo(
			List<FiltroRicercaProtocolProperty> list) {
		this.protocolPropertiesAccordo = list;
	}

	public void addProtocolPropertyAccordo(FiltroRicercaProtocolProperty filtro){
		this.protocolPropertiesAccordo.add(filtro);
	}
	
	/**
	 * @return the maxDate
	 */
	public Date getMaxDate() {
		return this.maxDate;
	}

	/**
	 * @param maxDate the maxDate to set
	 */
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	/**
	 * @return the minDate
	 */
	public Date getMinDate() {
		return this.minDate;
	}

	/**
	 * @param minDate the minDate to set
	 */
	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}


	public String getNomeAccordo() {
		return this.nomeAccordo;
	}

	public void setNomeAccordo(String nomeAccordo) {
		this.nomeAccordo = nomeAccordo;
	}

	public Integer getVersione() {
		return this.versione;
	}

	public void setVersione(Integer versione) {
		this.versione = versione;
	}

	public String getNomeSoggettoReferente() {
		return this.nomeSoggettoReferente;
	}

	public void setNomeSoggettoReferente(String nomeSoggettoReferente) {
		this.nomeSoggettoReferente = nomeSoggettoReferente;
	}

	public String getTipoSoggettoReferente() {
		return this.tipoSoggettoReferente;
	}

	public void setTipoSoggettoReferente(String tipoSoggettoReferente) {
		this.tipoSoggettoReferente = tipoSoggettoReferente;
	}

	public IDAccordoCooperazione getIdAccordoCooperazione() {
		return this.idAccordoCooperazione;
	}

	public void setIdAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione) {
		this.idAccordoCooperazione = idAccordoCooperazione;
	}
	
	public Boolean isServizioComposto() {
		return this.servizioComposto;
	}

	public void setServizioComposto(Boolean servizioComposto) {
		this.servizioComposto = servizioComposto;
	}
	
	public IDGruppo getIdGruppo() {
		return this.idGruppo;
	}
	public void setIdGruppo(IDGruppo gruppo) {
		this.idGruppo = gruppo;
	}
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append("Filtro Accordi:");
		this.addDetails(bf);
		if(bf.length()=="Filtro Accordi:".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}
	public void addDetails(StringBuilder bf){
		if(this.minDate!=null)
			bf.append(" [intervallo-inferiore-data:"+this.minDate+"]");
		if(this.maxDate!=null)
			bf.append(" [intervallo-superiore-data:"+this.maxDate+"]");
		if(this.nomeAccordo!=null)
			bf.append(" [nome-accordo:"+this.nomeAccordo+"]");
		if(this.tipoSoggettoReferente!=null)
			bf.append(" [tipo-soggetto-referente:"+this.tipoSoggettoReferente+"]");
		if(this.nomeSoggettoReferente!=null)
			bf.append(" [nome-soggetto-referente:"+this.nomeSoggettoReferente+"]");
		if(this.versione!=null)
			bf.append(" [versione:"+this.versione+"]");
		if(this.idAccordoCooperazione!=null)
			bf.append(" [id-accordo-cooperazione:"+this.idAccordoCooperazione+"]");
		if(this.servizioComposto!=null)
			bf.append(" [servizio-composto:"+this.servizioComposto+"]");
		if(this.idGruppo!=null)
			bf.append(" [id-gruppo:"+this.idGruppo+"]");
		if(this.protocollo!=null)
			bf.append(" [protocollo:"+this.protocollo+"]");
		if(this.protocolli!=null && this.protocolli.size()>0) {
			bf.append(" [protocolli:"+this.protocolli+"]");
		}
		bf.append(" [order:"+this.order+"]");
		if(this.protocolPropertiesAccordo!=null && this.protocolPropertiesAccordo.size()>0){
			bf.append(" [protocol-properties-accordo:"+this.protocolPropertiesAccordo.size()+"]");
			for (int i = 0; i < this.protocolPropertiesAccordo.size(); i++) {
				bf.append(" [protocol-properties-accordo["+i+"]:");
				this.protocolPropertiesAccordo.get(i).addDetails(bf);
				bf.append("]");
			}
		}
	}

}
