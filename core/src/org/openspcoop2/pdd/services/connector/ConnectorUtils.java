package org.openspcoop2.pdd.services.connector;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.resources.MapReader;

public class ConnectorUtils {

	public static String generateError404Message(String code){
		return "OpenSPCoop2-"+code;
	}
	
	public static String generateErrorMessage(HttpServletRequest req, String msgErrore, boolean erroreGenerale, boolean htmlMessage){
		
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
		// versione
		String versione = "Porta di Dominio "+CostantiPdD.OPENSPCOOP2_PRODUCT_VERSION;
		if(op2Properties!=null){
			versione = "Porta di Dominio "+op2Properties.getPddDetailsForServices();
		}
		if(htmlMessage){
			versione = StringEscapeUtils.escapeHtml(versione);
		}
		
		
		StringBuffer risposta = new StringBuffer();
		risposta.append("<html>\n");
		risposta.append("<head>\n");
		risposta.append("<title>"+versione+"</title>\n");
		risposta.append("</head>\n");
		risposta.append("<body>\n");
		
		risposta.append("<h1>"+versione+"</h1>\n");
			
		// url
		String function = null;
		String parameters = null;
		try{
			URLProtocolContext protocolContext = new URLProtocolContext(req, logCore);
			String url = protocolContext.getUrlInvocazione_formBased();
			if(url.endsWith("?wsdl=")){
				// richiesta di un wsdl
				url = url.substring(0, url.length()-"=".length());
			}
			if(htmlMessage){
				url = StringEscapeUtils.escapeHtml( url );
			}
			risposta.append("<p>" +url+"</p>\n");
			function = protocolContext.getFunction();
			parameters = protocolContext.getFunctionParameters();
		}catch(Exception e){
			logCore.error(e.getMessage(),e);
			String context = req.getContextPath();
			if(htmlMessage){
				context = StringEscapeUtils.escapeHtml( context );
			}
			risposta.append("<p>" +context+"</p>\n");
		}

		// errore
		String errore = msgErrore;
		if(htmlMessage){
			errore = StringEscapeUtils.escapeHtml(errore);
		}
		risposta.append("<p>"+errore+"</p>\n");	
		
		
		// other infos
		if(!erroreGenerale && URLProtocolContext.PD_FUNCTION.equals(function)){
			risposta.append("<i>Servizio utilizzabile per l'invocazione di Porte Delegate esposte dalla PdD OpenSPCoop v2</i><br/><br/>\n");
		}
		else if(!erroreGenerale && URLProtocolContext.PA_FUNCTION.equals(function)){
			risposta.append("<i>Servizio utilizzabile per l'invocazione di Porte Applicative esposte dalla PdD OpenSPCoop v2</i><br/><br/>\n");
		}
		else if(!erroreGenerale && URLProtocolContext.PDtoSOAP_FUNCTION.equals(function)){
			risposta.append("<i>Servizio utilizzabile per l'invocazione di Porte Delegate esposte dalla PdD OpenSPCoop v2, con messaggi xml non imbustati nel protocollo SOAP</i><br/><br/>\n");
		}
		else if(!erroreGenerale && URLProtocolContext.IntegrationManager_FUNCTION.equals(function)){
			if(parameters==null){
				risposta.append("<i>Servizio IntegrationManager</i><br/><br/>\n");
			}
			else if( (function+"/"+parameters).equals(URLProtocolContext.IntegrationManager_FUNCTION_PD) ){
				risposta.append("<i>Servizio utilizzabile per l'invocazione di Porte Delegate esposte dalla PdD OpenSPCoop v2</i><br/><br/>\n");
			}
			else if( (function+"/"+parameters).startsWith(URLProtocolContext.IntegrationManager_FUNCTION_PD+"/") ){
				risposta.append("<i>Servizio utilizzabile per l'invocazione di Porte Delegate esposte dalla PdD OpenSPCoop v2</i><br/><br/>\n");
			}
			else if( (function+"/"+parameters).equals(URLProtocolContext.IntegrationManager_FUNCTION_MessageBox) ){
				risposta.append("<i>Servizio utilizzabile per accedere alla MessageBox esposta dalla PdD OpenSPCoop v2</i><br/><br/>\n");
			}
			else if( (function+"/"+parameters).startsWith(URLProtocolContext.IntegrationManager_FUNCTION_MessageBox+"/") ){
				risposta.append("<i>Servizio utilizzabile per accedere alla MessageBox esposta dalla PdD OpenSPCoop v2</i><br/><br/>\n");
			}
			else{
				risposta.append("<i>Servizio IntegrationManager della PdD OpenSPCoop v2</i><br/><br/>\n");
			}
		}
		else{
			if(htmlMessage){
				// use as
				String useAs = "Use as http[s]://<server>"+ req.getContextPath()+"/<protocol-context>/<service>[/...]";
				useAs = StringEscapeUtils.escapeHtml(useAs);
				risposta.append("<i>"+useAs+"</i><br/>\n");
			}
				
			// protocolli
			try{
				MapReader<String, IProtocolFactory> prots = ProtocolFactoryManager.getInstance().getProtocolFactories();
				if(prots.size()<=0){
					risposta.append("<i>ERROR: No protocol installed</i><br/>\n");
				}
				else{
					StringBuffer bfProtocols = new StringBuffer();
					Enumeration<String> keys = prots.keys();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						IProtocolFactory pf = prots.get(key);
						if(pf.getManifest().getWeb().getEmptyContext()!=null && pf.getManifest().getWeb().getEmptyContext().isEnabled()){
							if(bfProtocols.length()>0){
								bfProtocols.append(", ");
							}
							bfProtocols.append("\"\" (protocol:"+key+")");
						}
						if(pf.getManifest().getWeb().sizeContextList()>0){
							for (String context : pf.getManifest().getWeb().getContextList()) {
								if(bfProtocols.length()>0){
									bfProtocols.append(", ");
								}
								bfProtocols.append(context+" (protocol:"+key+")");
							}
						}
					}
					String enabledProtocols = "Enabled protocol-contexts: "+bfProtocols.toString();
					if(htmlMessage){
						enabledProtocols = StringEscapeUtils.escapeHtml(enabledProtocols);
					}
					risposta.append("<i>"+enabledProtocols+"</i><br/>\n");
				}
			}catch(Exception e){
				logCore.error(e.getMessage(),e);
				risposta.append("<i>ERROR: No protocol installed</i><br/>\n");
			}
			
			if(htmlMessage){
				// servizi
				risposta.append("<i>Enabled services: PD, PA, PDtoSOAP, checkPdD, IntegrationManager</i><br/><br/>\n");
			
				// web site
				risposta.append("<i>Official website: http://www.openspcoop.org</i><br/><br/>\n");
			}
		}
			
		risposta.append("</body>\n");
		risposta.append("</html>\n");
		return risposta.toString();
	}
	
}
