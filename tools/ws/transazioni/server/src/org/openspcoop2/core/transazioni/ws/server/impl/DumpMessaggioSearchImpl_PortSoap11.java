package org.openspcoop2.core.transazioni.ws.server.impl;

import org.openspcoop2.core.transazioni.ws.server.exception.TransazioniServiceException_Exception;

/**     
 * DumpMessaggioSearchImpl_PortSoap11
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.jws.WebService(name = "DumpMessaggio",
		serviceName = "DumpMessaggioSoap11Service",
		portName = "DumpMessaggioPortSoap11",
		targetNamespace = "http://www.openspcoop2.org/core/transazioni/management",
		endpointInterface = "org.openspcoop2.core.transazioni.ws.server.DumpMessaggioSearch")
public class DumpMessaggioSearchImpl_PortSoap11 extends DumpMessaggioImpl {

	@javax.annotation.Resource
	javax.xml.ws.WebServiceContext wsc;
	
	@Override
	@javax.jws.WebMethod
	protected javax.servlet.http.HttpServletRequest getHttpServletRequest() throws TransazioniServiceException_Exception{
		
		javax.servlet.http.HttpServletRequest req =  null;
		
		javax.xml.ws.handler.MessageContext msgContext = this.wsc.getMessageContext();

		req = 
			(javax.servlet.http.HttpServletRequest) msgContext.get(javax.xml.ws.handler.MessageContext.SERVLET_REQUEST);
		
		return req;
	}

	@Override
	protected javax.servlet.http.HttpServletResponse getHttpServletResponse()
			throws TransazioniServiceException_Exception {
		
		javax.servlet.http.HttpServletResponse res =  null;
		
		javax.xml.ws.handler.MessageContext msgContext = this.wsc.getMessageContext();

		res = 
			(javax.servlet.http.HttpServletResponse) msgContext.get(javax.xml.ws.handler.MessageContext.SERVLET_RESPONSE);
		
		return res;
	}
}