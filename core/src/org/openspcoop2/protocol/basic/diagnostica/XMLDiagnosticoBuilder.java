/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.basic.diagnostica;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.openspcoop2.core.diagnostica.Dominio;
import org.openspcoop2.core.diagnostica.DominioSoggetto;
import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;
import org.openspcoop2.core.diagnostica.Protocollo;
import org.openspcoop2.core.diagnostica.utils.XMLUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.w3c.dom.Element;

/**
 * XMLDiagnosticoBuilder
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLDiagnosticoBuilder implements org.openspcoop2.protocol.sdk.diagnostica.IXMLDiagnosticoBuilder {

	protected org.openspcoop2.message.XMLUtils xmlUtils;
	/** Logger utilizzato per debug. */

	protected Logger log;
	protected IProtocolFactory factory;
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.factory;
	}
	
	public XMLDiagnosticoBuilder(IProtocolFactory protocolFactory){
		this.log = protocolFactory.getLogger();
		this.factory = protocolFactory;
		this.xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
	}

	/* --------------------- MESSAGGI DIAGNOSTICI -----------------------*/

	@Override
	public Element toElement(MsgDiagnostico msgDiag) throws ProtocolException{
		try{
			
			MessaggioDiagnostico messaggioDiagnosticoBase = new MessaggioDiagnostico();
			
			// Dati Porta di Comunicazione che ha emesso i diagnostici
			if(msgDiag.getIdSoggetto()!=null){
				Dominio dominio = new Dominio();
				dominio.setIdentificativoPorta(msgDiag.getIdSoggetto().getCodicePorta());
				DominioSoggetto dominioSoggetto = new DominioSoggetto();
				dominioSoggetto.setTipo(msgDiag.getIdSoggetto().getTipo());
				dominioSoggetto.setBase(msgDiag.getIdSoggetto().getNome());
				dominio.setSoggetto(dominioSoggetto);
				dominio.setModulo(msgDiag.getIdFunzione());
				messaggioDiagnosticoBase.setDominio(dominio);
			}
			
			
			// Identificativi
			messaggioDiagnosticoBase.setIdentificativoRichiesta(msgDiag.getIdBusta());
			messaggioDiagnosticoBase.setIdentificativoRisposta(msgDiag.getIdBustaRisposta());
			
			// Altro
			if(msgDiag.getGdo()!=null){
				messaggioDiagnosticoBase.setOraRegistrazione(msgDiag.getGdo().getTime());
			}
			messaggioDiagnosticoBase.setCodice(msgDiag.getCodice());
			messaggioDiagnosticoBase.setMessaggio(msgDiag.getMessaggio());
			messaggioDiagnosticoBase.setSeverita(new BigInteger(msgDiag.getSeverita()+""));
			
			// Protocol Info
			if(msgDiag.getProtocollo()!=null){
				Protocollo protocollo = new Protocollo();
				protocollo.setIdentificativo(msgDiag.getProtocollo());
			}
			
			byte[] xmlDiagnostico = XMLUtils.generateMessaggioDiagnostico(messaggioDiagnosticoBase);
			Element diagnostico = this.xmlUtils.newElement(xmlDiagnostico);

			return  diagnostico;

		} catch(Exception e) {
			this.log.error("XMLBuilder.buildElement_Diagnostico error: "+e.getMessage(),e);
			throw new ProtocolException("XMLBuilder.buildElement_Diagnostico error: "+e.getMessage(),e);
		}
	}

	@Override
	public String toString(MsgDiagnostico msgDiag) throws ProtocolException {
		Element diagnostico = this.toElement(msgDiag);	
		if(diagnostico == null){
			throw new ProtocolException("Conversione in element non riuscita");
		}
		try{
			String xml = this.xmlUtils.toString(diagnostico,true);
			if(xml==null){
				throw new Exception("Conversione in stringa non riuscita");
			}
			return xml;
		}catch(ProtocolException pe){
			throw pe;
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}

	@Override
	public byte[] toByteArray(MsgDiagnostico msgDiag) throws ProtocolException {
		Element diagnostico = this.toElement(msgDiag);	
		if(diagnostico == null){
			throw new ProtocolException("Conversione in element non riuscita");
		}
		try{
			byte[] xml = this.xmlUtils.toByteArray(diagnostico,true);
			if(xml==null){
				throw new ProtocolException("Conversione in bytes non riuscita");
			}
			return xml;
		}catch(ProtocolException pe){
			throw pe;
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
}