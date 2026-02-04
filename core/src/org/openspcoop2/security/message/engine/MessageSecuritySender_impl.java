/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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


package org.openspcoop2.security.message.engine;


import java.util.Arrays;

import org.apache.commons.lang3.math.NumberUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.IMessageSecuritySender;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityUtilities;
import org.openspcoop2.security.message.constants.SecurityConstants;

/**
 * Classe per la gestione della Sicurezza (role:Sender)
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author Tommaso Burlon (tommaso.burlom@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MessageSecuritySender_impl extends MessageSecuritySender {

	protected MessageSecuritySender_impl(MessageSecurityContext messageSecurityContext) {
	    super(messageSecurityContext);
    }


    @Override
    protected boolean process(OpenSPCoop2Message message, org.openspcoop2.utils.Map<Object> ctx) {
		try{

			IMessageSecuritySender senderInterface = this.messageSecurityContext.getMessageSecuritySender();


			String codeRanges = MessageUtilities.getOutgoingProperty(this.messageSecurityContext, SecurityConstants.MESSAGE_SECURITY_APPLY_ON_BACKEND_RETURN_CODE);
			if (codeRanges != null) {
				String responseCode = message.getTransportResponseContext() != null ? message.getTransportResponseContext().getCodiceTrasporto() : message.getForcedResponseCode();
				Integer code = NumberUtils.toInt(responseCode, -1);
				if (code >= 0 && !MessageUtilities.isIntegerInRanges(code, codeRanges))
					return true; // non devo applicare la sicurezza.
			}

			// Fix per SOAPFault (quando ci sono le encryptionParts o le signatureParts, la Security fallisce se c'e' un SOAPFault)
			boolean skipSoapFault = ServiceBinding.SOAP.equals(message.getServiceBinding()) 
					&&
					(message.isFault() || message.castAsSoap().getSOAPBody().hasFault())
					&&
					!MessageSecurityUtilities.processSOAPFault(this.messageSecurityContext.getOutgoingProperties());
			boolean skipRestProblemDetail = ServiceBinding.REST.equals(message.getServiceBinding()) 
					&&
					(message.isFault() || message.castAsRest().isProblemDetailsForHttpApis_RFC7807())
					&&
					!MessageSecurityUtilities.processProblemDetails(this.messageSecurityContext.getOutgoingProperties());
			if(skipSoapFault || skipRestProblemDetail
				) {
				return true; // non devo applicare la sicurezza.
			}

			String action = MessageUtilities.getOutgoingProperty(this.messageSecurityContext, SecurityConstants.ACTION);
			if(action==null || "".equals(action.trim())){
				return true; // nessuna action: non devo applicare la sicurezza.
			}

			if (Arrays.stream(action.split(" ")).anyMatch(a -> a.trim().contains(SecurityConstants.SIGNATURE_ACTION))
					&& MessageType.JSON.equals(message.getMessageType())) {
				MessageUtilities.addJSONClaims(this.messageSecurityContext, message, ctx);
			}

			// Utilizzo l'engine di sicurezza
			senderInterface.process(this.messageSecurityContext, message, ctx);

		}
		catch(Exception e){

			String prefix = "Generatosi errore durante il processamento Message-Security(Sender): ";

			this.messageSecurityContext.getLog().error(prefix+e.getMessage(),e);

			this.msgErrore =  prefix+e.getMessage();
    	    this.codiceErrore = CodiceErroreCooperazione.SICUREZZA;

			if(e instanceof SecurityException){
				SecurityException securityException = (SecurityException) e;
				if(securityException.getMsgErrore()!=null){
					this.msgErrore = prefix+securityException.getMsgErrore();
				}
				if(securityException.getCodiceErrore()!=null){
					this.codiceErrore = securityException.getCodiceErrore();
				}
			}

    	    return false;
		}
		return true;
    }

}
