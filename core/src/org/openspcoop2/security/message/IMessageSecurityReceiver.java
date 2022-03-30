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

package org.openspcoop2.security.message;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.security.SecurityException;

/**
 * IMessageSecurityReceiver
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IMessageSecurityReceiver {
	
	public default boolean checkExistsWSSecurityHeader() {
		return true;
	}
	
	/**
	 * Applica la sicurezza al messaggio
	 * 
	 * @param messageSecurityContext MessageSecurityContext
	 * @param message Messaggio
	 * @param busta Busta
	 * @throws SecurityException
	 */
	public void process(MessageSecurityContext messageSecurityContext,OpenSPCoop2Message message,Busta busta) throws SecurityException;
	
	/**
	 * Localizza gli elementi "toccati" dalla sicurezza
	 * 
	 * @param messageSecurityContext MessageSecurityContext
	 * @param message Messaggio
	 * @return reference degli elementi
	 * @throws SecurityException
	 */
	public List<Reference> getDirtyElements(MessageSecurityContext messageSecurityContext,OpenSPCoop2SoapMessage message) throws SecurityException;
	
	/**
	 * Verifica cifratura/firma elementi richiesti dalla configurazione, ritornando una lista di reference per gli elementi che dovrebbero risultare cifrati completamente (Encoding Element)
	 * 
	 * @param messageSecurityContext MessageSecurityContext
	 * @param elementsToClean reference (localizzati con il metodo 'getDirtyElements')
	 * @param message Messaggio
	 * @param codiciErrore Lista di errori da valorizzare se durante il controllo si rileva una inconsistenza 
	 * @return lista di reference per gli elementi che dovrebbero risultare cifrati completamente (Encoding Element)
	 * @throws SecurityException
	 */
	public Map<QName, QName> checkEncryptSignatureParts(MessageSecurityContext messageSecurityContext,List<Reference> elementsToClean, OpenSPCoop2SoapMessage message,
			List<SubErrorCodeSecurity> codiciErrore) throws SecurityException;
		
	/**
	 * Verifica che gli elementi cifrati come element (localizzati con il metodo 'checkEncryptSignatureParts'), siano presenti dopo la decifratura
	 * 
	 * @param notResolved Reference non ancora risolte
	 * @param message Messaggio
	 * @param erroriRilevati Lista di errori da valorizzare se durante il controllo si rileva una inconsistenza
	 * @throws SecurityException
	 */
	public void checkEncryptionPartElements(Map<QName, QName> notResolved, OpenSPCoop2SoapMessage message, List<SubErrorCodeSecurity> erroriRilevati) throws SecurityException;
			
	
	public void cleanDirtyElements(MessageSecurityContext messageSecurityContext,OpenSPCoop2SoapMessage message, List<Reference> elementsToClean,
			boolean detachHeaderWSSecurity, boolean removeAllIdRef) throws SecurityException;
	
	public void detachSecurity(MessageSecurityContext messageSecurityContext,OpenSPCoop2RestMessage<?> message) throws SecurityException;
	
	public String getCertificate() throws SecurityException;
	public X509Certificate getX509Certificate() throws SecurityException;
	public PublicKey getPublicKey() throws SecurityException;
	
	
}
