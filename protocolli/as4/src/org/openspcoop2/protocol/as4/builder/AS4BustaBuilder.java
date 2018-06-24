/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.as4.builder;

import java.util.Date;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.basic.builder.BustaBuilder;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * Classe che implementa, in base al protocollo AS4, l'interfaccia {@link org.openspcoop2.protocol.sdk.builder.IBustaBuilder} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4BustaBuilder extends BustaBuilder<SOAPElement> {


	private AS4Properties as4Properties;
	public AS4BustaBuilder(IProtocolFactory<?> factory, IState state) throws ProtocolException {
		super(factory, state);
		this.as4Properties = AS4Properties.getInstance();
	}

	@Override
	public ProtocolMessage imbustamento(OpenSPCoop2Message msg, Busta busta,
			RuoloMessaggio ruoloMessaggio,
			ProprietaManifestAttachments proprietaManifestAttachments)
			throws ProtocolException {
		
		if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)) {
			AS4Imbustamento imbustamento = new AS4Imbustamento();
			return imbustamento.buildASMessage(msg, busta, ruoloMessaggio, proprietaManifestAttachments, 
					this.getProtocolFactory().getCachedRegistryReader(this.state), this.getProtocolFactory());
		}
		else {
			OpenSPCoop2Message soapMsg = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(MessageType.SOAP_12, MessageRole.RESPONSE);
			ProtocolMessage response = new ProtocolMessage();
			response.setMessage(soapMsg);
			//return super.imbustamento(soapMsg, busta, ruoloMessaggio, proprietaManifestAttachments);
			return response;
		}
		
	}
	
	
	@Override
	public String newID(IDSoggetto idSoggetto, String idTransazione, RuoloMessaggio ruoloMessaggio) throws ProtocolException {
		String id = super.newID(idSoggetto, idTransazione, ruoloMessaggio, this.as4Properties.generateIDasUUID());
		id = id + AS4Costanti.AS4_SUFFIX_ID_OPENSPCOOP2;
		return id;
	}
	
	
	@Override
	public Date extractDateFromID(String id) throws ProtocolException {
		return extractDateFromID(id, this.as4Properties.generateIDasUUID());
		
	}
	
	@Override
	public ProtocolMessage sbustamento(OpenSPCoop2Message msg, Busta busta,
			RuoloMessaggio ruoloMessaggio, ProprietaManifestAttachments proprietaManifestAttachments,
			FaseSbustamento faseSbustamento, ServiceBinding integrationServiceBinding, ServiceBindingConfiguration serviceBindingConfiguration) throws ProtocolException {
		
		if(FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RICHIESTA.equals(faseSbustamento) ){
		
			AS4Sbustamento sbustamento = new AS4Sbustamento();
			
			IRegistryReader registryReader = this.protocolFactory.getCachedRegistryReader(this.state);
			
			return sbustamento.buildMessage(this.state, msg, busta, ruoloMessaggio, proprietaManifestAttachments, 
					faseSbustamento, integrationServiceBinding, serviceBindingConfiguration, 
					registryReader, this.protocolFactory);
		
		}
		else {
			return super.sbustamento(msg, busta, ruoloMessaggio, proprietaManifestAttachments, 
					faseSbustamento, integrationServiceBinding, serviceBindingConfiguration);
		}
	}
}
