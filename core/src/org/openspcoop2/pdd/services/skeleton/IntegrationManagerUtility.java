/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.pdd.services.skeleton;

import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.sdk.AbstractEccezioneBuilderParameter;
import org.openspcoop2.protocol.sdk.EccezioneIntegrazioneBuilderParameters;
import org.openspcoop2.protocol.sdk.EccezioneProtocolloBuilderParameters;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.w3c.dom.Node;

/**
 * IntegrationManagerUtility
 *
 * @author apoli@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationManagerUtility {

	public static void readAndSetProtocol(javax.servlet.http.HttpServletRequest req,String pathinfo) throws IntegrationManagerException{
		
		//System.out.println("PATH INFO["+pathinfo+"]");
		String protocol = (String) req.getAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME);
		//System.out.println("PROTOCOLLO ["+protocol+"]");
		if(protocol!=null){
			
			//System.out.println("INIZIALIZZO IM con INPUT PROTOCOL ["+protocol+"]");
			// verifico che sia un protocollo
			try{
				ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocol);
			} catch(ProtocolException e) {
				
				// vedo se quello che mi è errivato è un contesto altrimeni rilancio la prima eccezione
				boolean ok = false;
				try{
					Openspcoop2 manifest = ProtocolFactoryManager.getInstance().getProtocolManifest(protocol);
					req.setAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME, manifest.getProtocol().getName());
					ok = true;
				} catch(ProtocolException eInternal) {}
				
				if(ok==false){
					throw new RuntimeException(e);
				}
			}
			
		}
		else  {
		
			// rimuovo il nome del contesto, rimane protocol/IntegrationManager
			pathinfo = pathinfo.substring(pathinfo.indexOf("/", 1) + 1);
			// prendo la prima parola: protocol
			String protocolName = pathinfo;
			if(pathinfo.indexOf("/") != -1){
				try{
					String servletPath = pathinfo.substring(0, pathinfo.indexOf("/"));
					if(servletPath.equals("PA") || servletPath.equals("PD") || servletPath.equals("PDtoSOAP") || servletPath.equals("IntegrationManager")) {
						servletPath = "@EMPTY@";//CostantiPdD.OPENSPCOOP_PROTOCOL_EMPTY_CONTEXT;
					}
					//System.out.println("INIZIALIZZO IM con SERVLET PATH ["+servletPath+"]");
					Openspcoop2 manifest = ProtocolFactoryManager.getInstance().getProtocolManifest(servletPath);
					protocolName = manifest.getProtocol().getName();
				} catch(ProtocolException e) {
					throw new RuntimeException(e);
				}
			}
			
			req.setAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME, protocolName);
			
		}
		
	}
	
	
    /**
	 * Mappa una risposta di errore applicativo XML in una eccezione IntegrationManagerException
	 *
	 * @param xml XML su cui effettuare il mapping
	 * @return la protocol exception
	 * 
	 */
	public static IntegrationManagerException mapXMLIntoProtocolException(IProtocolFactory<?> protocolFactory,String xml,String prefixCodiceErroreApplicativoIntegrazione) throws Exception{
		org.openspcoop2.message.xml.XMLUtils xmlUtils = org.openspcoop2.message.xml.XMLUtils.DEFAULT;
		org.w3c.dom.Document document = xmlUtils.newDocument(xml.getBytes());
		return IntegrationManagerUtility.mapXMLIntoProtocolException(protocolFactory,document.getFirstChild(),prefixCodiceErroreApplicativoIntegrazione);
	}
	
	public static IntegrationManagerException mapXMLIntoProtocolException(IProtocolFactory<?> protocolFactory,Node xml,String prefixCodiceErroreApplicativoIntegrazione) throws Exception{
		
		AbstractEccezioneBuilderParameter eccezione = 
				protocolFactory.createErroreApplicativoBuilder().readErroreApplicativo(xml, prefixCodiceErroreApplicativoIntegrazione);
		IntegrationManagerException exc = null;
		if(eccezione instanceof EccezioneProtocolloBuilderParameters){
			EccezioneProtocolloBuilderParameters eccBusta = (EccezioneProtocolloBuilderParameters) eccezione;
			exc = new IntegrationManagerException(protocolFactory, eccBusta.getEccezioneProtocollo());
		}
		else{
			EccezioneIntegrazioneBuilderParameters eccIntegrazione = (EccezioneIntegrazioneBuilderParameters) eccezione;
			exc = new IntegrationManagerException(protocolFactory, eccIntegrazione.getErroreIntegrazione());
		}
		
		exc.setOraRegistrazione(protocolFactory.createTraduttore().getDate_protocolFormat(eccezione.getOraRegistrazione()));
		exc.setIdentificativoFunzione(eccezione.getIdFunzione());
		exc.setIdentificativoPorta(eccezione.getDominioPorta().getCodicePorta());

		return exc;

	}
}
