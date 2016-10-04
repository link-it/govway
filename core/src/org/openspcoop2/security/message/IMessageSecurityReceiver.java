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

package org.openspcoop2.security.message;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.reference.Reference;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.security.SecurityException;

/**
 * IMessageSecurityReceiver
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IMessageSecurityReceiver {
	
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
	public List<Reference> getDirtyElements(MessageSecurityContext messageSecurityContext,OpenSPCoop2Message message) throws SecurityException;
	
	/**
	 * Verifica cifratura/firma elementi richiesti dalla configurazione, ritornando una lista di reference per gli elementi che dovrebbero risultare cifrati completamente (Encoding Element)
	 * 
	 * @param messageSecurityContext MessageSecurityContext
	 * @param elementsToClean reference (localizzati con il metodo 'getDirtyElements')
	 * @param numAttachmentsInMsg Numero di attachments presenti nel messaggio
	 * @param codiciErrore Lista di errori da valorizzare se durante il controllo si rileva una inconsistenza 
	 * @return lista di reference per gli elementi che dovrebbero risultare cifrati completamente (Encoding Element)
	 * @throws SecurityException
	 */
	public Map<QName, QName> checkEncryptSignatureParts(MessageSecurityContext messageSecurityContext,List<Reference> elementsToClean, int numAttachmentsInMsg,
			List<SubErrorCodeSecurity> codiciErrore) throws SecurityException;
		
	/**
	 * Verifica che gli elementi cifrati come element (localizzati con il metodo 'checkEncryptSignatureParts'), siano presenti dopo la decifratura
	 * 
	 * @param notResolved Reference non ancora risolte
	 * @param message Messaggio
	 * @param erroriRilevati Lista di errori da valorizzare se durante il controllo si rileva una inconsistenza
	 * @throws SecurityException
	 */
	public void checkEncryptionPartElements(Map<QName, QName> notResolved, OpenSPCoop2Message message, List<SubErrorCodeSecurity> erroriRilevati) throws SecurityException;
			
	
	public void cleanDirtyElements(MessageSecurityContext messageSecurityContext,OpenSPCoop2Message message, List<Reference> elementsToClean) throws SecurityException;
	
	public String getCertificate() throws SecurityException;
	
}
