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

package org.openspcoop2.protocol.sdk.config;

import java.util.List;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.protocol.sdk.BypassMustUnderstandCheck;
import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.InitialIdConversationType;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.transport.TransportRequestContext;

/**
 * Interfaccia di Configurazione del Protocollo, valori prelevati dal file openspcoop2-manifest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IProtocolConfiguration extends IComponentFactory {


	/**
	 * Restituisce la configurazione del service binding generale del protocollo, indipendente dal servizio 
	 * (se sono presenti più binding verrà usato quello di default)
	 * 
	 * @param transportRequest informazioni di trasporto
	 * @return configurazione del service binding generale del protocollo
	 * @throws ProtocolException
	 */
	public ServiceBindingConfiguration getDefaultServiceBindingConfiguration(TransportRequestContext transportRequest) throws ProtocolException;
	
	/**
	 * Restituisce il service binding associato al servizio
	 * 
	 * @param idServizio servizio richiesto
	 * @param registryReader Reader delle informazioni interne al registro
	 * @return service binding associato al servizio per l'integrazione
	 * @throws ProtocolException
	 */
	public ServiceBinding getIntegrationServiceBinding(IDServizio idServizio, IRegistryReader registryReader) throws ProtocolException, RegistryNotFound;
	
	/**
	 * Restituisce il ServiceBinding del Protocollo
	 * 
	 * @param transportRequest informazioni di trasporto
	 * @return service binding associato al servizio per il protocollo
	 * @throws ProtocolException
	 */
	public ServiceBinding getProtocolServiceBinding(ServiceBinding integrationServiceBinding, TransportRequestContext transportRequest) throws ProtocolException;
		
	/**
	 * Restituisce la configurazione del service binding compatibile con il servizio richiesto
	 * 
	 * @param idServizio servizio richiesto
	 * @param registryReader Reader delle informazioni interne al registro
	 * @return configurazione del service binding compatibile con il servizio richiesto
	 * @throws ProtocolException
	 */
	public ServiceBindingConfiguration getServiceBindingConfiguration(TransportRequestContext transportRequest, ServiceBinding serviceBinding,
			IDServizio idServizio, IRegistryReader registryReader) throws ProtocolException, RegistryNotFound;
		
	/**
	 * Ritorna la lista delle interfacce supportate dal protocollo, rispetto al service binding
	 * 
	 * @return lista delle interfacce supportate dal protocollo, rispetto al service binding
	 */
	public List<InterfaceType> getInterfacceSupportate(ServiceBinding serviceBinding);
	
	/**
	 * Ritorna l'indicazione se la specifica delle interfacce prevede uno schema esterno all'interfaccia
	 * 
	 * @return True se la specifica delle interfacce prevede uno schema esterno all'interfaccia
	 */
	public boolean isSupportoSchemaEsternoInterfaccia(ServiceBinding serviceBinding, InterfaceType interfaceType);
	
	/**
	 * Ritorna l'indicazione se la specifica delle conversazioni viene supportata dal protocollo
	 * 
	 * @return True se la specifica delle conversazioni viene supportata dal protocollo
	 */
	public boolean isSupportoSpecificaConversazioni(ServiceBinding serviceBinding, InterfaceType interfaceType);
	
	/**
	 * Ritorna l'indicazione se l'implementazione delle interfaccia è supportato nella parte specifica
	 * 
	 * @return True se l'implementazione delle interfaccia è supportato nella parte specifica
	 */
	public boolean isSupportoPortiAccessoAccordiParteSpecifica(ServiceBinding serviceBinding, InterfaceType interfaceType);
	
	/**
	 * Restituisce la lista dei tipi associabili ai soggetti
	 * 
	 * @return la lista dei tipi associabili ai soggetti
	 */
	public List<String> getTipiSoggetti() throws ProtocolException;
	
	/**
	 * Restituisce il tipo di default dei soggetti
	 * 
	 * @return Restituisce il tipo di default dei soggetti
	 * @throws ProtocolException
	 */
	public String getTipoSoggettoDefault() throws ProtocolException;
		
	/**
	 * Restituisce la lista dei tipi associabili ai servizi compatibili con il service binding fornito
	 * 
	 * @return la lista dei tipi associabili ai servizi
	 */
	public List<String> getTipiServizi(ServiceBinding serviceBinding) throws ProtocolException;
	
	/**
	 * Restituisce il tipo di default dei servizi
	 * 
	 * @return Restituisce il tipo di default dei servizi
	 * @throws ProtocolException
	 */
	public String getTipoServizioDefault(ServiceBinding serviceBinding) throws ProtocolException;

	/**
	 * Restituisce la lista delle versioni del protocollo
	 * 
	 * @return la lista delle versioni del protocollo
	 */
	public List<String> getVersioni() throws ProtocolException;
	
	/**
	 * Restituisce la versione di default
	 * 
	 * @return Restituisce la versione di default
	 * @throws ProtocolException
	 */
	public String getVersioneDefault() throws ProtocolException;
	
	/**
	 * Restituisce l'indicazione se il protocollo fornito come parametro viene supportato dal protocollo
	 * 
	 * @return True se il protocollo fornito come parametro viene supportato dal protocollo
	 */
	public boolean isSupportato(ServiceBinding serviceBinding, ProfiloDiCollaborazione profiloCollaborazione) throws ProtocolException;
	
	/**
	 * Restituisce l'indicazione se la funzionalita' fornita come parametro viene supportata dal protocollo
	 * 
	 * @return True se la funzionalita' fornita come parametro viene supportata dal protocollo
	 */
	public boolean isSupportato(ServiceBinding serviceBinding, FunzionalitaProtocollo funzionalitaProtocollo) throws ProtocolException;
	
	/**
	 * Restituisce l'indicazione se la funzionalita' richiede che venga fornita l'informazione tramite i meccanismi di integrazione
	 * 
	 * @return True se la funzionalita' richiede che venga fornita l'informazione tramite i meccanismi di integrazione
	 */
	public boolean isIntegrationInfoRequired(TipoPdD tipoPdD, ServiceBinding serviceBinding, FunzionalitaProtocollo funzionalitaProtocollo) throws ProtocolException;
	
	/**
	 * Restituisce l'indicazione se generare un id di collaborazione capostipite, quando la funzionalita' è abilitata e non viene fornito un identificativo tra i meccanismi di integrazione 
	 * 
	 * @return True se la funzionalita' genera un id di collaborazione capostipite
	 */
	public InitialIdConversationType isGenerateInitialIdConversation(TipoPdD tipoPdD, FunzionalitaProtocollo funzionalitaProtocollo) throws ProtocolException;
		
	/**
	 * Ritorna l'indicazione se l'autenticazione dei soggetti viene supportato dal protocollo
	 * 
	 * @return True se l'autenticazione dei soggetti viene supportato dal protocollo
	 */
	public boolean isSupportoAutenticazioneSoggetti();
	
	/**
	 * Ritorna l'indicazione se l'autenticazione degli applicativi sulle erogazioni viene supportato dal protocollo
	 * 
	 * @return True se l'autenticazione degli applicativi sulle erogazioni viene supportato dal protocollo
	 */
	public boolean isSupportoAutenticazioneApplicativiErogazioni();
	
	/**
	 * Ritorna l'indicazione se l'autorizzazione puntuale sui richiedenti è abilitata senza l'autenticazione
	 * 
	 * @return True se l'autorizzazione puntuale sui richiedenti è abilitata senza l'autenticazione
	 */
	public boolean isSupportatoAutorizzazioneRichiedenteSenzaAutenticazioneErogazioni();
	
	/**
	 * Ritorna l'indicazione se il codice IPA viene supportato dal protocollo
	 * 
	 * @return True se il codice IPA viene supportato dal protocollo
	 */
	public boolean isSupportoCodiceIPA();
	
	/**
	 * Ritorna l'indicazione se il dominio del soggetto viene supportato dal protocollo
	 * 
	 * @return True se il dominio del soggetto viene supportato dal protocollo
	 */
	public boolean isSupportoIdentificativoPortaSoggetto();
	
	/**
	 * Ritorna l'indicazione se la gestione dell'indirizzo di risposta viene supportata dal protocollo
	 * 
	 * @return True se la gestione dell'indirizzo di risposta viene supportata dal protocollo
	 */
	public boolean isSupportoIndirizzoRisposta();
			
	/**
	 * Ritorna l'indicazione se il soggetto referente degli accordi di servizio parte comune viene supportata dal protocollo
	 * 
	 * @return True se il soggetto referente degli accordi di servizio parte comune viene supportata dal protocollo
	 */
	public boolean isSupportoSoggettoReferenteAccordiParteComune();
	
	/**
	 * Ritorna l'indicazione se il versionamento degli accordi di servizio parte specifica viene supportata dal protocollo
	 * 
	 * @return True se il versionamento degli accordi di servizio parte specifica viene supportata dal protocollo
	 */
	public boolean isSupportoVersionamentoAccordiParteSpecifica();
		
	/**
	 * Ritorna l'indicazione se lo sbustamento del protocollo è supportato
	 * 
	 * @return True se lo sbustamento del protocollo è supportato
	 */
	public boolean isSupportoSbustamentoProtocollo();
	
	/**
	 * Ritorna l'indicazione se il protocollo, per la tipologia SOAP, supporta la scelta del fault
	 * 
	 * @return True se il protocollo, per la tipologia SOAP, supporta la scelta del fault
	 */
	public boolean isSupportoSceltaFault();
	
	/**
	 * Ritorna l'indicazione se il protocollo, per la correlazione applicativa, supporta il riuso dell'id di protocollo
	 * 
	 * @return True se il protocollo, per la correlazione applicativa, supporta il riuso dell'id di protocollo
	 */
	public boolean isAbilitatoRiusoIdCorrelazioneApplicativa();
	
	/**
	 * Ritorna l'indicazione se il protocollo, supporta la generazione delle tracce
	 * 
	 * @return True se il protocollo, supporta la generazione delle tracce
	 */
	public boolean isAbilitataGenerazioneTracce();
	
	/**
	 * Ritorna l'indicazione se il protocollo, aggiunge il salvataggio degli header di protocollo nelle tracce
	 * 
	 * @return True se il protocollo, aggiunge il salvataggio degli header di protocollo nelle tracce
	 */
	public boolean isAbilitatoSalvataggioHeaderProtocolloTracce();
	
	/**
	 * Ritorna l'indicazione se il protocollo, prevede un identificativo messaggio che contiene una data
	 * 
	 * @return True se l'identificativo del protocollo contiene un identificativo messaggio. 
	 */
	public boolean isDataPresenteInIdentificativoMessaggio();
		
	/**
	 * Ritorna i bypass da attivare sulla Porta relativi al protocollo
	 * 
	 * @return lista di BypassMustUnderstandCheck
	 */
	public List<BypassMustUnderstandCheck> getBypassMustUnderstandCheck();
	
	
}
