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

package org.openspcoop2.pdd.core.dynamic;

import java.io.ByteArrayOutputStream;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.rest.DumpRestMessageUtils;
import org.openspcoop2.message.soap.DumpSoapMessageUtils;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.message.utils.DumpMessaggio;
import org.openspcoop2.message.utils.DumpMessaggioConfig;
import org.slf4j.Logger;

/**
 * ContentExtractor
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentExtractor {

	@SuppressWarnings("unused")
	private Logger log;
	
	private OpenSPCoop2Message message;

	public ContentExtractor(OpenSPCoop2Message message, Logger log) {
		this.message = message; 
		this.log = log;
	}

	
	public boolean isSoap() throws DynamicException {
		if(this.message==null) {
			return false;
		}
		return ServiceBinding.SOAP.equals(this.message.getServiceBinding());
	}
	public boolean isRest() throws DynamicException {
		if(this.message==null) {
			return false;
		}
		return ServiceBinding.REST.equals(this.message.getServiceBinding());
	}
	
	
	public boolean hasContent() throws DynamicException {
		try {
			if(this.message==null) {
				return false;
			}
			if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
				return this.message.castAsSoap().getSOAPPart()!=null && this.message.castAsSoap().getSOAPPart().getEnvelope()!=null;
			}
			else {
				return this.message.castAsRest().hasContent();
			}
		}catch(Throwable t) {
			throw new DynamicException(t.getMessage(),t);
		}
	}
	public byte[] getContent() throws DynamicException {
		if(this.hasContent()==false) {
			return null;
		}
		try {
			if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				this.message.castAsSoap().writeTo(bout, false);
				bout.flush();
				bout.close();
				return bout.toByteArray();
			}
			else {
				return this.message.castAsRest().getContentAsString().getBytes();
			}
		}catch(Throwable t) {
			throw new DynamicException(t.getMessage(),t);
		}
	}
	public String getContentAsString() throws DynamicException {
		if(this.hasContent()==false) {
			return null;
		}
		try {
			if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				this.message.castAsSoap().writeTo(bout, false);
				bout.flush();
				bout.close();
				return bout.toString();
			}
			else {
				return this.message.castAsRest().getContentAsString();
			}
		}catch(Throwable t) {
			throw new DynamicException(t.getMessage(),t);
		}
	}

	public byte[] getContentSoapBody() throws DynamicException {
		if(this.hasContent()==false) {
			return null;
		}
		try {
			if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
				if(this.message.castAsSoap().getSOAPPart()==null || this.message.castAsSoap().getSOAPPart().getEnvelope()==null) {
					throw new Exception("Messaggio senza una busta SOAP");
				}
				return TunnelSoapUtils.sbustamentoSOAPEnvelope(this.message.getFactory(), this.message.castAsSoap().getSOAPPart().getEnvelope(), false);
			}
			else {
				throw new Exception("Funzionalità utilizzabile solamente con service binding soap");
			}
		}catch(Throwable t) {
			throw new DynamicException(t.getMessage(),t);
		}
	}
	
	public DumpMessaggio dumpMessage() throws DynamicException {
		if(this.dumpMessaggioInit==null) {
			this.initDump();
		}
		return this.dumpMessaggio;
	}
	
	
	private DumpMessaggio dumpMessaggio = null;
	private Boolean dumpMessaggioInit = null;
	private synchronized void initDump() throws DynamicException {
		if(this.dumpMessaggioInit==null) {
			
			try{
				if(this.hasContent()) {
	
					DumpMessaggioConfig config = new DumpMessaggioConfig();
					config.setDumpAttachments(true);
					config.setDumpBody(true);
					config.setDumpHeaders(false);
					config.setDumpMultipartHeaders(true);
					if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())) {
						this.dumpMessaggio = DumpSoapMessageUtils.dumpMessage(this.message.castAsSoap(), config, true);
					}
					else {
						this.dumpMessaggio = DumpRestMessageUtils.dumpMessage(this.message.castAsRest(), config, true);
					}
				}
			}catch(Throwable t) {
				throw new DynamicException(t.getMessage(),t);
			}
			
			this.dumpMessaggioInit = true;
		}
	}
	
	
}

