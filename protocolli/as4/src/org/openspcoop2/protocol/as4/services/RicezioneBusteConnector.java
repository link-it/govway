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



package org.openspcoop2.protocol.as4.services;

import java.util.HashMap;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.servlet.ServletException;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.pdd.services.connector.ConnectorUtils;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.pdd.services.service.RicezioneBusteService;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.services.message.AS4ConnectorInMessage;
import org.openspcoop2.protocol.as4.services.message.AS4ConnectorOutMessage;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.threads.RunnableLogger;

/**
 * RicezioneBusteConnector
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneBusteConnector extends AbstractRicezioneConnector{

	
	public RicezioneBusteConnector(RunnableLogger runnableLog,AS4Properties asProperties) throws Exception {
		super(runnableLog, asProperties, false);
	}
	
	@Override
	protected void check(Message mapParam) throws Exception {
		
		MapMessage map = null;
		if(mapParam instanceof MapMessage) {
			map = (MapMessage) mapParam;
		}
		else {
			throw new Exception("Tipo di messaggio ["+mapParam.getClass().getName()+"] non atteso");
		}
		
		RicezioneBusteConnettoreUtils utils = new RicezioneBusteConnettoreUtils(this.log);
		
		HashMap<String, byte[]> content = new HashMap<String, byte[]>();
		UserMessage userMessage = new UserMessage();
		utils.fillUserMessage(map, userMessage, content);
		
						
		AS4ConnectorInMessage as4In = null;
		try{
			as4In = new AS4ConnectorInMessage(userMessage, content);
		}catch(Exception e){
			ConnectorUtils.getErrorLog().error("AS4ConnectorInMessage init error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
		
		IProtocolFactory<?> protocolFactory = null;
		try{
			protocolFactory = as4In.getProtocolFactory();
		}catch(Throwable e){}
		
		AS4ConnectorOutMessage as4Out = null;
		try{
			as4Out = new AS4ConnectorOutMessage();
		}catch(Exception e){
			ConnectorUtils.getErrorLog().error("AS4ConnectorOutMessage init error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
			
		RicezioneBusteExternalErrorGenerator generatoreErrore = null;
		try{
			generatoreErrore = 
					new RicezioneBusteExternalErrorGenerator(protocolFactory.getLogger(),
							org.openspcoop2.pdd.services.connector.RicezioneBusteConnector.ID_MODULO, as4In.getRequestInfo(), null);
		}catch(Exception e){
			throw new Exception("Inizializzazione Generatore Errore fallita: "+Utilities.readFirstErrorValidMessageFromException(e),e);
		}
			
		
		RicezioneBusteService ricezioneBuste = new RicezioneBusteService(generatoreErrore);
		
		try{
			ricezioneBuste.process(as4In, as4Out);
		}catch(Exception e){
			ConnectorUtils.getErrorLog().error("RicezioneContenutiApplicativi.process error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
		
		if(as4Out.getResponseStatus()!=200 && as4Out.getResponseStatus()!=202) {
			throw new Exception("Servizio Ricezione Buste terminato con codice: "+as4Out.getResponseStatus());
		}
		if(as4Out.getMessage()!=null) {
			OpenSPCoop2SoapMessage soapMsg = as4Out.getMessage().castAsSoap();
			if(soapMsg.getSOAPBody()!=null && soapMsg.getSOAPBody().hasFault()) {
				throw new Exception("Servizio Ricezione Buste terminato con un soapFault: "+
						SoapUtils.toString(soapMsg.getSOAPBody().getFault()));
			}
		}
	}
	
}
