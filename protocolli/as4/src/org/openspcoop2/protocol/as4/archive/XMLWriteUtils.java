/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.as4.archive;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.openspcoop2.protocol.as4.pmode.Translator;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.slf4j.Logger;

/**
 * XMLWriteUtils 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12566 $, $Date: 2017-01-11 15:21:56 +0100 (Wed, 11 Jan 2017) $
 */
public class XMLWriteUtils {

	protected Logger log = null;
	protected IProtocolFactory<?> protocolFactory;	
	protected IRegistryReader registryReader;
	protected IConfigIntegrationReader configIntegrationReader;
	protected Translator translator;
	
	public XMLWriteUtils(Logger log,IProtocolFactory<?> protocolFactory,IRegistryReader registryReader,IConfigIntegrationReader configIntegrationReader) throws ProtocolException {
		this.log = log;
		this.protocolFactory = protocolFactory;
		
		this.registryReader = registryReader;
		this.configIntegrationReader = configIntegrationReader;
		
		this.translator = new Translator(registryReader, protocolFactory);
	}

	
	public byte[] generate(String nomeSoggetto) throws ProtocolException {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.generate(bout, nomeSoggetto);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public void generate(OutputStream out,String nomeSoggetto) throws ProtocolException {
		try {
			this.translator.translate(out, nomeSoggetto);
		}catch(Exception e) {
			throw new ProtocolException("Il soggetto '"+nomeSoggetto+"' non risulta configurato correttamente al fine di generazione una configurazione 'pmode' corrispondente: "+e.getMessage(),e);
		}
	}
	
	

	
}
