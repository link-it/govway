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


package org.openspcoop2.protocol.sdi.config;

import org.openspcoop2.protocol.basic.config.BasicVersionManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.constants.StatoFunzionalitaProtocollo;

/**
 * Classe che implementa, in base al protocollo SdI, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolVersionManager} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIProtocolVersionManager extends SDIProtocolManager implements IProtocolVersionManager {
	
	protected String versione;
	private InstanceVersioneManager basicVersionManager;
	public SDIProtocolVersionManager(IProtocolFactory<?> protocolFactory,String versione) throws ProtocolException{
		super(protocolFactory);
		this.versione = versione;
		this.basicVersionManager = new InstanceVersioneManager(protocolFactory);
	}
	
	
	
	/* *********** FUNZIONALITA' OFFERTE DALLA Porta di Dominio ******************* */
	
	@Override
	public StatoFunzionalitaProtocollo getFiltroDuplicati(Busta busta) {
		return this.basicVersionManager.getFiltroDuplicati(busta);
	}
	@Override
	public StatoFunzionalitaProtocollo getFiltroDuplicati(Servizio infoServizio) {
		return this.basicVersionManager.getFiltroDuplicati(infoServizio);
	}
	
	@Override
	public StatoFunzionalitaProtocollo getConsegnaAffidabile(Busta busta){
		return this.basicVersionManager.getConsegnaAffidabile(busta);
	}
	@Override
	public StatoFunzionalitaProtocollo getConsegnaAffidabile(Servizio infoServizio){
		return this.basicVersionManager.getConsegnaAffidabile(infoServizio);
	}
	
	@Override
	public StatoFunzionalitaProtocollo getConsegnaInOrdine(Busta busta){
		return this.basicVersionManager.getConsegnaInOrdine(busta);
	}
	@Override
	public StatoFunzionalitaProtocollo getConsegnaInOrdine(Servizio infoServizio){
		return this.basicVersionManager.getConsegnaInOrdine(infoServizio);
	}
	
	@Override
	public StatoFunzionalitaProtocollo getCollaborazione(Busta busta){
		return this.basicVersionManager.getCollaborazione(busta);
	}
	@Override
	public StatoFunzionalitaProtocollo getCollaborazione(Servizio infoServizio){
		return this.basicVersionManager.getCollaborazione(infoServizio);
	}
	
	@Override
	public StatoFunzionalitaProtocollo getIdRiferimentoRichiesta(Busta busta){
		return this.basicVersionManager.getIdRiferimentoRichiesta(busta);
	}
	@Override
	public StatoFunzionalitaProtocollo getIdRiferimentoRichiesta(Servizio infoServizio){
		return this.basicVersionManager.getIdRiferimentoRichiesta(infoServizio);
	}
	
	
	
	
	/* *********** PROFILI ASINCRONI ******************* */
	
	@Override
	public boolean isCorrelazioneRichiestaPresenteRispostaAsincronaSimmetrica(){
		return this.basicVersionManager.isCorrelazioneRichiestaPresenteRispostaAsincronaSimmetrica();
	}
	
	@Override
	public boolean isCorrelazioneRichiestaPresenteRichiestaStatoAsincronaAsimmetrica(){
		return this.basicVersionManager.isCorrelazioneRichiestaPresenteRichiestaStatoAsincronaAsimmetrica();
	}
	
	@Override
	public boolean isGenerazioneInformazioniServizioCorrelatoAsincronoSimmetrico(){
		return this.basicVersionManager.isGenerazioneInformazioniServizioCorrelatoAsincronoSimmetrico();
	}
	
	@Override
	public boolean isGenerazioneInformazioniServizioCorrelatoAsincronoAsimmetrico(){
		return this.basicVersionManager.isGenerazioneInformazioniServizioCorrelatoAsincronoAsimmetrico();
	}
	
	@Override
	public String getIdCorrelazioneAsincrona(Busta richiesta){
		return this.basicVersionManager.getIdCorrelazioneAsincrona(richiesta);
	}
	
	@Override
	public String getIdCorrelazioneAsincrona(String rifMsg,String collaborazione){
		return this.basicVersionManager.getIdCorrelazioneAsincrona(rifMsg,collaborazione);
	}
	
	
	

	
	
	/* *********** ECCEZIONI ******************* */

	@Override
	public boolean isEccezioniLivelloInfoAbilitato(){
		return true; // true per il protocollo SDI
	}

	
	@Override
	public boolean isIgnoraEccezioniLivelloNonGrave(){
		return true; // true per il protocollo SDI
	}
	
	
	
	
	
	
	/* *********** VALIDAZIONE/GENERAZIONE BUSTE ******************* */
	
	@Override
	public boolean isGenerazioneBusteErrore_strutturaMalformataHeaderProtocollo(){
		return this.basicVersionManager.isGenerazioneBusteErrore_strutturaMalformataHeaderProtocollo();
	}
		
	@Override
	public boolean isGenerazioneErroreMessaggioOnewayDuplicato(){
		return this.basicVersionManager.isGenerazioneErroreMessaggioOnewayDuplicato();
	}
	
	
	

	
	/* *********** ALTRO ******************* */
    
	@Override
	public boolean isUtilizzoIndirizzoSoggettoPresenteBusta(){
		return this.basicVersionManager.isUtilizzoIndirizzoSoggettoPresenteBusta();
	}
	

	
}


class InstanceVersioneManager extends BasicVersionManager{

	public InstanceVersioneManager(IProtocolFactory<?> protocolFactory) throws ProtocolException {
		super(protocolFactory);
	}
	
}
