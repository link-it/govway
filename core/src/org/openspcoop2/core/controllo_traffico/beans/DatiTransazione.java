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

package org.openspcoop2.core.controllo_traffico.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * DatiTransazione 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiTransazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TipoPdD tipoPdD;
	private String nomePorta;
	private IDSoggetto dominio;
	private String modulo;
	private String idTransazione;
	
	private String protocollo;
	
	private List<String> tagsAccordoServizioParteComune;
	private IDAccordo idAccordoServizioParteComune;
	
	private IDSoggetto soggettoFruitore;
	private IDServizio idServizio;
	
	private String servizioApplicativoFruitore;
	private List<String> listServiziApplicativiErogatori = new ArrayList<String>();
	
	private String identificativoAutenticato;
	
	private String tokenSubject;
	private String tokenIssuer;
	private String tokenClientId;
	private String tokenUsername;
	private String tokenEMail; 
	

	
	public String getServiziApplicativiErogatoreAsString(){
		StringBuilder bf = new StringBuilder();
		if(this.listServiziApplicativiErogatori==null || this.listServiziApplicativiErogatori.size()<=0){
			return null;
		}
		for (int i = 0; i < this.listServiziApplicativiErogatori.size(); i++) {
			if(bf.length()>0){
				bf.append(",");
			}
			bf.append(this.listServiziApplicativiErogatori.get(i));
		}
		return bf.toString();
	}
	
	public TipoPdD getTipoPdD() {
		return this.tipoPdD;
	}
	public void setTipoPdD(TipoPdD tipoPdD) {
		this.tipoPdD = tipoPdD;
	}
	public String getProtocollo() {
		return this.protocollo;
	}
	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
	public List<String> getTagsAccordoServizioParteComune() {
		return this.tagsAccordoServizioParteComune;
	}
	public void setTagsAccordoServizioParteComune(List<String> tagAccordoServizioParteComune) {
		this.tagsAccordoServizioParteComune = tagAccordoServizioParteComune;
	}
	public IDAccordo getIdAccordoServizioParteComune() {
		return this.idAccordoServizioParteComune;
	}
	public void setIdAccordoServizioParteComune(IDAccordo idAccordoServizioParteComune) {
		this.idAccordoServizioParteComune = idAccordoServizioParteComune;
	}
	public IDSoggetto getSoggettoFruitore() {
		return this.soggettoFruitore;
	}
	public void setSoggettoFruitore(IDSoggetto soggettoFruitore) {
		this.soggettoFruitore = soggettoFruitore;
	}
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}
	public String getServizioApplicativoFruitore() {
		return this.servizioApplicativoFruitore;
	}
	public void setServizioApplicativoFruitore(String servizioApplicativoFruitore) {
		this.servizioApplicativoFruitore = servizioApplicativoFruitore;
	}
	public List<String> getListServiziApplicativiErogatori() {
		return this.listServiziApplicativiErogatori;
	}
	public void setListServiziApplicativiErogatori(List<String> listServiziApplicativiErogatori) {
		this.listServiziApplicativiErogatori = listServiziApplicativiErogatori;
	}
	public IDSoggetto getDominio() {
		return this.dominio;
	}

	public void setDominio(IDSoggetto dominio) {
		this.dominio = dominio;
	}

	public String getModulo() {
		return this.modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public String getIdTransazione() {
		return this.idTransazione;
	}

	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}
	
	public String getNomePorta() {
		return this.nomePorta;
	}

	public void setNomePorta(String nomePorta) {
		this.nomePorta = nomePorta;
	}
	
	public String getIdentificativoAutenticato() {
		return this.identificativoAutenticato;
	}

	public void setIdentificativoAutenticato(String identificativoAutenticato) {
		this.identificativoAutenticato = identificativoAutenticato;
	}

	public String getTokenSubject() {
		return this.tokenSubject;
	}

	public void setTokenSubject(String tokenSubject) {
		this.tokenSubject = tokenSubject;
	}

	public String getTokenIssuer() {
		return this.tokenIssuer;
	}

	public void setTokenIssuer(String tokenIssuer) {
		this.tokenIssuer = tokenIssuer;
	}

	public String getTokenClientId() {
		return this.tokenClientId;
	}

	public void setTokenClientId(String tokenClientId) {
		this.tokenClientId = tokenClientId;
	}

	public String getTokenUsername() {
		return this.tokenUsername;
	}

	public void setTokenUsername(String tokenUsername) {
		this.tokenUsername = tokenUsername;
	}

	public String getTokenEMail() {
		return this.tokenEMail;
	}

	public void setTokenEMail(String tokenEMail) {
		this.tokenEMail = tokenEMail;
	}
}
