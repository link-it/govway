/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core;

import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.config.SoggettoVirtuale;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativiMessage;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * EJBUtilsMessaggioInConsegna
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EJBUtilsMessaggioInConsegna {
	
	private List<String> serviziApplicativi;
	private SoggettoVirtuale soggettiRealiMappatiInUnSoggettoVirtuale;
	private RichiestaApplicativa richiestaApplicativa;
	
	private Hashtable<String, Boolean> mapServizioApplicativoConConnettore;
	private Hashtable<String, Boolean> mapSbustamentoSoap;
	private Hashtable<String, Boolean> mapSbustamentoInformazioniProtocollo;
	private Hashtable<String, Boolean> mapGetMessage;
	private Hashtable<String, String> mapTipoConsegna;
	private Hashtable<String, Timestamp> mapOraRegistrazione;
	private Hashtable<String, ConsegnaContenutiApplicativiMessage> mapConsegnaContenutiApplicativiMessage;
	
	private boolean registrazioneMessaggioPerStatelessEffettuata;
	private boolean gestioneSolamenteConIntegrationManager;
	private boolean registrazioneDestinatarioEffettuataPerViaBehaviour;
	private boolean stateless;
	private boolean oneWayVersione11;
	
	private String idBustaPreBehaviourNewMessage;
	private Busta busta;
	private String nomePorta;
	
	private OpenSPCoop2Message requestMessageNullable;

	public List<String> getServiziApplicativi() {
		return this.serviziApplicativi;
	}

	public void setServiziApplicativi(List<String> serviziApplicativi) {
		this.serviziApplicativi = serviziApplicativi;
	}

	public SoggettoVirtuale getSoggettiRealiMappatiInUnSoggettoVirtuale() {
		return this.soggettiRealiMappatiInUnSoggettoVirtuale;
	}

	public void setSoggettiRealiMappatiInUnSoggettoVirtuale(SoggettoVirtuale soggettiRealiMappatiInUnSoggettoVirtuale) {
		this.soggettiRealiMappatiInUnSoggettoVirtuale = soggettiRealiMappatiInUnSoggettoVirtuale;
	}

	public RichiestaApplicativa getRichiestaApplicativa() {
		return this.richiestaApplicativa;
	}

	public void setRichiestaApplicativa(RichiestaApplicativa richiestaApplicativa) {
		this.richiestaApplicativa = richiestaApplicativa;
	}

	public Hashtable<String, Boolean> getMapServizioApplicativoConConnettore() {
		return this.mapServizioApplicativoConConnettore;
	}

	public void setMapServizioApplicativoConConnettore(Hashtable<String, Boolean> mapServizioApplicativoConConnettore) {
		this.mapServizioApplicativoConConnettore = mapServizioApplicativoConConnettore;
	}

	public Hashtable<String, Boolean> getMapSbustamentoSoap() {
		return this.mapSbustamentoSoap;
	}

	public void setMapSbustamentoSoap(Hashtable<String, Boolean> mapSbustamentoSoap) {
		this.mapSbustamentoSoap = mapSbustamentoSoap;
	}

	public Hashtable<String, Boolean> getMapSbustamentoInformazioniProtocollo() {
		return this.mapSbustamentoInformazioniProtocollo;
	}

	public void setMapSbustamentoInformazioniProtocollo(Hashtable<String, Boolean> mapSbustamentoInformazioniProtocollo) {
		this.mapSbustamentoInformazioniProtocollo = mapSbustamentoInformazioniProtocollo;
	}

	public Hashtable<String, Boolean> getMapGetMessage() {
		return this.mapGetMessage;
	}

	public void setMapGetMessage(Hashtable<String, Boolean> mapGetMessage) {
		this.mapGetMessage = mapGetMessage;
	}

	public Hashtable<String, String> getMapTipoConsegna() {
		return this.mapTipoConsegna;
	}

	public void setMapTipoConsegna(Hashtable<String, String> mapTipoConsegna) {
		this.mapTipoConsegna = mapTipoConsegna;
	}

	public Hashtable<String, Timestamp> getMapOraRegistrazione() {
		return this.mapOraRegistrazione;
	}

	public void setMapOraRegistrazione(Hashtable<String, Timestamp> mapOraRegistrazione) {
		this.mapOraRegistrazione = mapOraRegistrazione;
	}

	public Hashtable<String, ConsegnaContenutiApplicativiMessage> getMapConsegnaContenutiApplicativiMessage() {
		return this.mapConsegnaContenutiApplicativiMessage;
	}

	public void setMapConsegnaContenutiApplicativiMessage(
			Hashtable<String, ConsegnaContenutiApplicativiMessage> mapConsegnaContenutiApplicativiMessage) {
		this.mapConsegnaContenutiApplicativiMessage = mapConsegnaContenutiApplicativiMessage;
	}

	public boolean isRegistrazioneMessaggioPerStatelessEffettuata() {
		return this.registrazioneMessaggioPerStatelessEffettuata;
	}

	public void setRegistrazioneMessaggioPerStatelessEffettuata(boolean registrazioneMessaggioPerStatelessEffettuata) {
		this.registrazioneMessaggioPerStatelessEffettuata = registrazioneMessaggioPerStatelessEffettuata;
	}

	public boolean isGestioneSolamenteConIntegrationManager() {
		return this.gestioneSolamenteConIntegrationManager;
	}

	public void setGestioneSolamenteConIntegrationManager(boolean gestioneSolamenteConIntegrationManager) {
		this.gestioneSolamenteConIntegrationManager = gestioneSolamenteConIntegrationManager;
	}

	public boolean isRegistrazioneDestinatarioEffettuataPerViaBehaviour() {
		return this.registrazioneDestinatarioEffettuataPerViaBehaviour;
	}

	public void setRegistrazioneDestinatarioEffettuataPerViaBehaviour(
			boolean registrazioneDestinatarioEffettuataPerViaBehaviour) {
		this.registrazioneDestinatarioEffettuataPerViaBehaviour = registrazioneDestinatarioEffettuataPerViaBehaviour;
	}

	public boolean isStateless() {
		return this.stateless;
	}

	public void setStateless(boolean stateless) {
		this.stateless = stateless;
	}

	public boolean isOneWayVersione11() {
		return this.oneWayVersione11;
	}

	public void setOneWayVersione11(boolean oneWayVersione11) {
		this.oneWayVersione11 = oneWayVersione11;
	}

	public String getIdBustaPreBehaviourNewMessage() {
		return this.idBustaPreBehaviourNewMessage;
	}

	public void setIdBustaPreBehaviourNewMessage(String idBustaPreBehaviourNewMessage) {
		this.idBustaPreBehaviourNewMessage = idBustaPreBehaviourNewMessage;
	}

	public Busta getBusta() {
		return this.busta;
	}

	public void setBusta(Busta busta) {
		this.busta = busta;
	}

	public String getNomePorta() {
		return this.nomePorta;
	}

	public void setNomePorta(String nomePorta) {
		this.nomePorta = nomePorta;
	}

	public OpenSPCoop2Message getRequestMessageNullable() {
		return this.requestMessageNullable;
	}

	public void setRequestMessageNullable(OpenSPCoop2Message requestMessageNullable) {
		this.requestMessageNullable = requestMessageNullable;
	}
	
}
