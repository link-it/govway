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



package org.openspcoop2.core.config.driver;

import java.io.Serializable;

import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaPorteApplicative extends FiltroRicercaBase implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** TipoSoggetto */
	private String tipoSoggetto;
	
	/** NomeSoggetto */
	private String nomeSoggetto;
		
	/** TipoSoggettoVirtuale */
	private String tipoSoggettoVirtuale;
	
	/** NomeSoggetto */
	private String nomeSoggettoVirtuale;
	
	/** TipoServizio */
	private String tipoServizio;
	
	/** NomeServizio */
	private String nomeServizio;
	
	/** VersioneServizio */
	private Integer versioneServizio;

	/** Azione */
	private String azione;

	/** Autorizzazione Ruolo */
	private IDRuolo idRuolo;
	
	/** Autorizzazione Scope */
	private IDScope idScope;
	
	/** Autorizzazione Soggetto */
	private IDSoggetto idSoggettoAutorizzato;
	
	/** Autorizzazione Servizio Applicativo */
	private IDServizioApplicativo idServizioApplicativoAutorizzato;
	
	/** Trasformazioni Applicabilita Soggetto */
	private IDSoggetto idSoggettoRiferitoApplicabilitaTrasformazione;
	
	/** Trasformazioni Applicabilita Servizio Applicativo */
	private IDServizioApplicativo idServizioApplicativoRiferitoApplicabilitaTrasformazione;
	
	/** Stato */
	private StatoFunzionalita stato;
	
	/** Trova tutte le porte associate a quella indicata nel parametro con funzione DelegatedBy */
	private String nomePortaDelegante;
	

	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append(super.toString(false));
		if(this.tipoSoggetto!=null)
			bf.append(" [tipoSoggetto:"+this.tipoSoggetto+"]");
		if(this.nomeSoggetto!=null)
			bf.append(" [nomeSoggetto:"+this.nomeSoggetto+"]");
		if(this.tipoSoggettoVirtuale!=null)
			bf.append(" [tipoSoggettoVirtuale:"+this.tipoSoggettoVirtuale+"]");
		if(this.nomeSoggettoVirtuale!=null)
			bf.append(" [nomeSoggettoVirtuale:"+this.nomeSoggettoVirtuale+"]");
		if(this.tipoServizio!=null)
			bf.append(" [tipoServizio:"+this.tipoServizio+"]");
		if(this.nomeServizio!=null)
			bf.append(" [nomeServizio:"+this.nomeServizio+"]");
		if(this.versioneServizio!=null)
			bf.append(" [versioneServizio:"+this.versioneServizio+"]");
		if(this.azione!=null)
			bf.append(" [azione:"+this.azione+"]");
		if(this.idRuolo!=null)
			bf.append(" [ruolo:"+this.idRuolo+"]");
		if(this.idScope!=null)
			bf.append(" [scope:"+this.idScope+"]");
		if(this.idSoggettoAutorizzato!=null)
			bf.append(" [soggettoAutorizzato:"+this.idSoggettoAutorizzato+"]");
		if(this.idServizioApplicativoAutorizzato!=null)
			bf.append(" [servizioApplicativoAutorizzato:"+this.idServizioApplicativoAutorizzato+"]");
		if(this.idSoggettoRiferitoApplicabilitaTrasformazione!=null)
			bf.append(" [soggettoRiferitoApplicabilitaTrasformazione:"+this.idSoggettoRiferitoApplicabilitaTrasformazione+"]");
		if(this.idServizioApplicativoRiferitoApplicabilitaTrasformazione!=null)
			bf.append(" [servizioApplicativoRiferitoApplicabilitaTrasformazione:"+this.idServizioApplicativoRiferitoApplicabilitaTrasformazione+"]");
		if(this.stato!=null)
			bf.append(" [stato:"+this.stato+"]");
		if(this.nomePortaDelegante!=null)
			bf.append(" [nomePortaDelegante:"+this.nomePortaDelegante+"]");
		if(bf.length()=="Filtro:".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}
	
	public String getTipoSoggetto() {
		return this.tipoSoggetto;
	}


	public void setTipoSoggetto(String tipoSoggetto) {
		this.tipoSoggetto = tipoSoggetto;
	}


	public String getNomeSoggetto() {
		return this.nomeSoggetto;
	}


	public void setNomeSoggetto(String nomeSoggetto) {
		this.nomeSoggetto = nomeSoggetto;
	}


	public String getTipoServizio() {
		return this.tipoServizio;
	}


	public void setTipoServizio(String tipoServizio) {
		this.tipoServizio = tipoServizio;
	}


	public String getNomeServizio() {
		return this.nomeServizio;
	}


	public void setNomeServizio(String nomeServizio) {
		this.nomeServizio = nomeServizio;
	}

	
	public Integer getVersioneServizio() {
		return this.versioneServizio;
	}

	public void setVersioneServizio(Integer versioneServizio) {
		this.versioneServizio = versioneServizio;
	}


	public String getAzione() {
		return this.azione;
	}


	public void setAzione(String azione) {
		this.azione = azione;
	}
	
	public String getTipoSoggettoVirtuale() {
		return this.tipoSoggettoVirtuale;
	}

	public void setTipoSoggettoVirtuale(String tipoSoggettoVirtuale) {
		this.tipoSoggettoVirtuale = tipoSoggettoVirtuale;
	}

	public String getNomeSoggettoVirtuale() {
		return this.nomeSoggettoVirtuale;
	}

	public void setNomeSoggettoVirtuale(String nomeSoggettoVirtuale) {
		this.nomeSoggettoVirtuale = nomeSoggettoVirtuale;
	}
	
	public IDRuolo getIdRuolo() {
		return this.idRuolo;
	}

	public void setIdRuolo(IDRuolo idRuolo) {
		this.idRuolo = idRuolo;
	}
	
	public IDScope getIdScope() {
		return this.idScope;
	}

	public void setIdScope(IDScope idScope) {
		this.idScope = idScope;
	}
	
	public StatoFunzionalita getStato() {
		return this.stato;
	}

	public void setStato(StatoFunzionalita stato) {
		this.stato = stato;
	}
	
	public String getNomePortaDelegante() {
		return this.nomePortaDelegante;
	}

	public void setNomePortaDelegante(String nomePortaDelegante) {
		this.nomePortaDelegante = nomePortaDelegante;
	}
	
	public IDSoggetto getIdSoggettoAutorizzato() {
		return this.idSoggettoAutorizzato;
	}

	public void setIdSoggettoAutorizzato(IDSoggetto idSoggettoAutorizzato) {
		this.idSoggettoAutorizzato = idSoggettoAutorizzato;
	}
	
	public IDServizioApplicativo getIdServizioApplicativoAutorizzato() {
		return this.idServizioApplicativoAutorizzato;
	}

	public void setIdServizioApplicativoAutorizzato(IDServizioApplicativo idServizioApplicativoAutorizzato) {
		this.idServizioApplicativoAutorizzato = idServizioApplicativoAutorizzato;
	}
	
	public IDSoggetto getIdSoggettoRiferitoApplicabilitaTrasformazione() {
		return this.idSoggettoRiferitoApplicabilitaTrasformazione;
	}

	public void setIdSoggettoRiferitoApplicabilitaTrasformazione(IDSoggetto idSoggettoRiferitoApplicabilitaTrasformazione) {
		this.idSoggettoRiferitoApplicabilitaTrasformazione = idSoggettoRiferitoApplicabilitaTrasformazione;
	}

	public IDServizioApplicativo getIdServizioApplicativoRiferitoApplicabilitaTrasformazione() {
		return this.idServizioApplicativoRiferitoApplicabilitaTrasformazione;
	}

	public void setIdServizioApplicativoRiferitoApplicabilitaTrasformazione(
			IDServizioApplicativo idServizioApplicativoRiferitoApplicabilitaTrasformazione) {
		this.idServizioApplicativoRiferitoApplicabilitaTrasformazione = idServizioApplicativoRiferitoApplicabilitaTrasformazione;
	}
}
