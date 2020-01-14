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


package org.openspcoop2.protocol.modipa.config;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.basic.config.BasicVersionManager;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModIPropertiesUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.constants.StatoFunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.slf4j.Logger;

/**
 * Classe che implementa, in base al protocollo ModI, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolVersionManager} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIProtocolVersionManager extends ModIProtocolManager implements IProtocolVersionManager  {
	
	protected ModIProperties modipaProperties = null;
	protected Logger logger = null;
	protected String versione;
	private InstanceVersioneManager basicVersionManager;
	public ModIProtocolVersionManager(IProtocolFactory<?> protocolFactory,String versione) throws ProtocolException{
		super(protocolFactory);
		this.versione = versione;
		this.logger = this.getProtocolFactory().getLogger();
		this.modipaProperties = ModIProperties.getInstance();
		this.basicVersionManager = new InstanceVersioneManager(protocolFactory);
	}
	
	
	
	/* *********** FUNZIONALITA' OFFERTE DALLA Porta di Dominio ******************* */
	
	@Override
	public StatoFunzionalitaProtocollo getFiltroDuplicati(Busta busta) {
		
		String valore = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO);
		if(valore!=null) {
			String securityMessageProfile = ModIPropertiesUtils.convertProfiloSicurezzaToConfigurationValue(valore);
			if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(securityMessageProfile) ||
					ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(securityMessageProfile)) {
				return StatoFunzionalitaProtocollo.ABILITATA;
			}
		}
		return StatoFunzionalitaProtocollo.DISABILITATA;
		
	}
	@Override
	public StatoFunzionalitaProtocollo getFiltroDuplicati(Servizio infoServizio) {
		// Nel builder non viene utilizzata questa informazione poich' l'identificativo univoco viene sempre inserito.
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
		return StatoFunzionalitaProtocollo.REGISTRO;
	}
	@Override
	public StatoFunzionalitaProtocollo getCollaborazione(Servizio infoServizio){
		return StatoFunzionalitaProtocollo.REGISTRO;
	}
	
	@Override
	public StatoFunzionalitaProtocollo getIdRiferimentoRichiesta(Busta busta){
		return StatoFunzionalitaProtocollo.REGISTRO;
	}
	@Override
	public StatoFunzionalitaProtocollo getIdRiferimentoRichiesta(Servizio infoServizio){
		return StatoFunzionalitaProtocollo.REGISTRO;
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
		return this.basicVersionManager.isEccezioniLivelloInfoAbilitato();
	}

	
	@Override
	public boolean isIgnoraEccezioniLivelloNonGrave(){
		return this.basicVersionManager.isIgnoraEccezioniLivelloNonGrave();
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
	
	
	
	
	
	/* *********** CONNETTORE ******************* */
	
	@Override
	public org.openspcoop2.core.registry.Connettore getStaticRoute(IDSoggetto idSoggettoMittente, IDServizio idServizio,
			IRegistryReader registryReader) throws ProtocolException{
		return this.basicVersionManager.getStaticRoute(idSoggettoMittente, idServizio, registryReader);
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
