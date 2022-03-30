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

package org.openspcoop2.protocol.spcoop.builder;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.BasicStateComponentFactory;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.FaseImbustamento;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.spcoop.SPCoopBustaRawContent;
import org.openspcoop2.utils.date.DateUtils;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.builder.IBustaBuilder} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopBustaBuilder extends BasicStateComponentFactory implements org.openspcoop2.protocol.sdk.builder.IBustaBuilder<SOAPHeaderElement> {

	private SPCoopImbustamento spcoopImbustamento = null;
	private SPCoopSbustamento spcoopSbustamento = null;
	
	public SPCoopBustaBuilder(IProtocolFactory<SOAPHeaderElement> factory, IState state) throws ProtocolException{
		super(factory,state);
		this.spcoopImbustamento = new SPCoopImbustamento(factory, this.state);
		this.spcoopSbustamento = new SPCoopSbustamento(factory, this.state);
		this.log = factory.getLogger();
	}
	

	@Override
	public String newID(IDSoggetto idSoggetto, String idTransazione,
			RuoloMessaggio ruoloMessaggio)
			throws ProtocolException {
		return this.spcoopImbustamento.buildID(idSoggetto, idTransazione, ruoloMessaggio);
	}

	@Override
	public Date extractDateFromID(String id) throws ProtocolException {
		try{
			
			// es. MinisteroErogatore_MinisteroErogatoreSPCoopIT_0000215_2011-08-04_09:43
			
			if(id==null){
				return null;
			}
			String [] split = id.split("_");
			if(split.length!=5){
				return null;
			}
			
			SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMinute();
			String tmp = split[3]+"_"+split[4];
			Date d = dateformat.parse(tmp);
			return d;
			
		}catch(Exception e){
			throw new ProtocolException("Errore durante l'estrazione della data dall'identificativo ["+id+"]: "+ e.getMessage(),e);
		}
	}
	
	@Override
	public ProtocolMessage imbustamento(OpenSPCoop2Message msg, Context context,
			Busta busta, Busta bustaRichiesta,
			RuoloMessaggio ruoloMessaggio,
			ProprietaManifestAttachments proprietaManifestAttachments,
			FaseImbustamento faseImbustamento)
			throws ProtocolException {
		
		if(FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO.equals(faseImbustamento)) {
			ProtocolMessage protocolMessage = new ProtocolMessage();
			protocolMessage.setPhaseUnsupported(true);
			return protocolMessage;
		}
		
		SOAPHeaderElement element =  this.spcoopImbustamento.imbustamento(msg, busta, 
				ruoloMessaggio, 
				proprietaManifestAttachments);
		ProtocolMessage protocolMessage = new ProtocolMessage();
		protocolMessage.setBustaRawContent(new SPCoopBustaRawContent(element));
		protocolMessage.setMessage(msg);
		return protocolMessage;
	}

	@Override
	public ProtocolMessage addTrasmissione(OpenSPCoop2Message message,
			Trasmissione trasmissione,
			FaseImbustamento faseImbustamento) throws ProtocolException {
		
		if(FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO.equals(faseImbustamento)) {
			ProtocolMessage protocolMessage = new ProtocolMessage();
			protocolMessage.setPhaseUnsupported(true);
			return protocolMessage;
		}
		
		SOAPHeaderElement element =  this.spcoopImbustamento.addTrasmissione(message, trasmissione);
		ProtocolMessage protocolMessage = new ProtocolMessage();
		protocolMessage.setBustaRawContent(new SPCoopBustaRawContent(element));
		protocolMessage.setMessage(message);
		return protocolMessage;
	}

	@Override
	public ProtocolMessage sbustamento(OpenSPCoop2Message msg, Context context,
			Busta busta,
			RuoloMessaggio ruoloMessaggio, ProprietaManifestAttachments proprietaManifestAttachments,
			FaseSbustamento faseSbustamento, ServiceBinding integrationServiceBinding, ServiceBindingConfiguration serviceBindingConfiguration) throws ProtocolException {
		
		ProtocolMessage protocolMessage = new ProtocolMessage();
		protocolMessage.setMessage(msg);
		
		if(FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RICHIESTA.equals(faseSbustamento) == false &&
				FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RISPOSTA.equals(faseSbustamento) == false){
			
			// Lo sbustamento effettivo in spcoop viene ritardato fino alla consegna del servizio applicativo
			// il servizio applicativo può richiederlo di non effettuarlo
			SOAPHeaderElement element =  this.spcoopSbustamento.sbustamento(msg, proprietaManifestAttachments);
			protocolMessage.setBustaRawContent(new SPCoopBustaRawContent(element));
			protocolMessage.setMessage(msg);
		}
		
		protocolMessage.setUseBustaRawContentReadByValidation(true);
		return protocolMessage;
	}

}
