/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import org.openspcoop2.protocol.sdk.BypassMustUnderstandCheck;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;

/**
 * Interfaccia di Configurazione del Protocollo, valori prelevati dal file openspcoop2-manifest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IProtocolConfiguration {

	public IProtocolFactory getProtocolFactory();
	
	
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
	 * Restituisce la lista dei tipi associabili ai servizi
	 * 
	 * @return la lista dei tipi associabili ai servizi
	 */
	public List<String> getTipiServizi() throws ProtocolException;
	
	/**
	 * Restituisce il tipo di default dei servizi
	 * 
	 * @return Restituisce il tipo di default dei servizi
	 * @throws ProtocolException
	 */
	public String getTipoServizioDefault() throws ProtocolException;

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
	public boolean isSupportato(ProfiloDiCollaborazione profiloCollaborazione) throws ProtocolException;
	
	/**
	 * Restituisce l'indicazione se la funzionalita' fornita come parametro viene supportata dal protocollo
	 * 
	 * @return True se la funzionalita' fornita come parametro viene supportata dal protocollo
	 */
	public boolean isSupportato(FunzionalitaProtocollo funzionalitaProtocollo) throws ProtocolException;
		
	/**
	 * Ritorna l'indicazione se il codice IPA viene supportato dal protocollo
	 * 
	 * @return True se il codice IPA viene supportato dal protocollo
	 */
	public boolean isSupportoCodiceIPA();
	
	/**
	 * Ritorna l'indicazione se la gestione dell'indirizzo di risposta viene supportata dal protocollo
	 * 
	 * @return True se la gestione dell'indirizzo di risposta viene supportata dal protocollo
	 */
	public boolean isSupportoIndirizzoRisposta();
	
	/**
	 * Ritorna l'indicazione se la specifica delle interfacce prevede il wsdl definitorio
	 * 
	 * @return True se la specifica delle interfacce prevede il wsdl definitorio
	 */
	public boolean isSupportoWsdlDefinitorio();
	
	/**
	 * Ritorna l'indicazione se la specifica delle conversazioni viene supportata dal protocollo
	 * 
	 * @return True se la specifica delle conversazioni viene supportata dal protocollo
	 */
	public boolean isSupportoSpecificaConversazioni();
	
	/**
	 * Ritorna l'indicazione sulla supporto del protocollo SOAP 1.1
	 * 
	 * @return True se il protocllo SOAP 1.1 e' supportato. False altrimenti
	 */
	public boolean isSupportoSOAP11();
	
	/**
	 * Ritorna l'indicazione sulla supporto del protocollo SOAP 1.2
	 * 
	 * @return True se il protocllo SOAP 1.2 e' supportato. False altrimenti
	 */
	public boolean isSupportoSOAP12();
	
	/**
	 * Ritorna i bypass da attivare sulla Porta relativi al protocollo
	 * 
	 * @return lista di BypassMustUnderstandCheck
	 */
	public List<BypassMustUnderstandCheck> getBypassMustUnderstandCheck();
	
}
