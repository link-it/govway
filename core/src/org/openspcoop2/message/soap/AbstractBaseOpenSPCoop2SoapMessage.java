/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.message.soap;

import java.util.List;

import javax.xml.soap.SOAPMessage;

import org.openspcoop2.message.AbstractBaseOpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.context.MessageContext;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.soap.mtom.MTOMUtilities;
import org.openspcoop2.message.soap.mtom.MtomXomPackageInfo;
import org.openspcoop2.message.soap.mtom.MtomXomReference;

/**
 * AbstractBaseOpenSPCoop2SoapMessage_impl
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractBaseOpenSPCoop2SoapMessage extends AbstractBaseOpenSPCoop2Message implements OpenSPCoop2SoapMessage {

	/* OpenSPCoop2SoapMessageCore */
	public OpenSPCoop2SoapMessageCore soapCore;
	

	public AbstractBaseOpenSPCoop2SoapMessage(OpenSPCoop2MessageFactory messageFactory) {
		super(messageFactory);
		this.soapCore = new OpenSPCoop2SoapMessageCore();
	}
	
	
	/* Copy Resources to another instance */
	
	public void copyResourceFrom(OpenSPCoop2SoapMessageCore core) {
		core.copyInstanceFieldValueTo(this.soapCore);
	}
	
	@Override
	public MessageContext serializeResourcesTo() throws MessageException{
		MessageContext messageContext = super.serializeResourcesTo();
		return this.soapCore.serializeResourcesTo(messageContext);
	}
	
	@Override
	public void readResourcesFrom(MessageContext messageContext) throws MessageException{
		super.readResourcesFrom(messageContext);
		this.soapCore.readResourcesFrom(messageContext);
	}
	
	
	/* Elementi SOAP */
	
	protected abstract SOAPMessage _getSOAPMessage() throws MessageException;
	
	@Override
	public SOAPMessage getSOAPMessage() throws MessageException,MessageNotSupportedException{
		return this.soapCore.getSOAPMessage(this._getSOAPMessage(), this.getMessageType());
	}
	

	/* SOAPAction */
	
	@Override
	public String getSoapAction(){
		return this.soapCore.getSoapAction();
	}
	
	@Override
	public void setSoapAction(String soapAction){
		this.soapCore.setSoapAction(soapAction);
	}
	
	
	
	/* MTOM */
	
	@Override
	public List<MtomXomReference> mtomUnpackaging() throws MessageException,MessageNotSupportedException{
		return MTOMUtilities.unpackaging(this, false, true);
	}
	@Override
	public List<MtomXomReference> mtomPackaging( List<MtomXomPackageInfo> packageInfos) throws MessageException,MessageNotSupportedException{
		return MTOMUtilities.packaging(this, packageInfos, true);
	}
	@Override
	public List<MtomXomReference> mtomVerify( List<MtomXomPackageInfo> packageInfos) throws MessageException,MessageNotSupportedException{
		return MTOMUtilities.verify(this, packageInfos, true);
	}
	@Override
	public List<MtomXomReference> mtomFastUnpackagingForXSDConformance() throws MessageException,MessageNotSupportedException{
		return MTOMUtilities.unpackaging(this, true, true);
	}
	@Override
	public void mtomRestoreAfterXSDConformance(List<MtomXomReference> references) throws MessageException,MessageNotSupportedException{
		MTOMUtilities.restoreAfterFastUnpackaging(this, references, true);
	}
	
	
	
	
	/* WSSecurity */
	
	@Override
	public boolean isThrowExceptionIfFoundMoreSecurityHeader() {
		return this.soapCore.isThrowExceptionIfFoundMoreSecurityHeader();
	}

	@Override
	public void setThrowExceptionIfFoundMoreSecurityHeader(boolean throwExceptionIfFoundMoreSecurityHeader) {
		this.soapCore.setThrowExceptionIfFoundMoreSecurityHeader(throwExceptionIfFoundMoreSecurityHeader);
	}
}
