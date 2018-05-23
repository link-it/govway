package org.openspcoop2.pdd.core.controllo_traffico.policy;

import java.util.Enumeration;
import java.util.Properties;

import javax.xml.soap.SOAPEnvelope;

import org.slf4j.Logger;
import org.openspcoop2.message.xml.DynamicNamespaceContextFactory;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathNotFoundException;

import org.openspcoop2.core.controllo_congestione.beans.DatiTransazione;
import org.openspcoop2.core.controllo_congestione.beans.ID;
import org.openspcoop2.core.controllo_congestione.constants.TipoFiltroApplicativo;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.Dati;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.IRateLimiting;

public class PolicyFiltroApplicativoUtilities {

	public static String getValore(Logger log,String tipo, String nome, InRequestProtocolContext context, 
			DatiTransazione datiTransazione, boolean forFilter) throws Exception{
		
		
		TipoFiltroApplicativo tipoFiltro = TipoFiltroApplicativo.toEnumConstant(tipo);
		
		switch (tipoFiltro) {
		case CONTENT_BASED:
			
			XPathExpressionEngine xpathEngine = new XPathExpressionEngine();
			try{
				SOAPEnvelope soapEnvelope = context.getMessaggio().castAsSoap().getSOAPPart().getEnvelope();
				DynamicNamespaceContext dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContext(soapEnvelope);
				return xpathEngine.getStringMatchPattern(soapEnvelope, dnc, nome);
			}catch(XPathNotFoundException notFound){
				log.debug(notFound.getMessage(),notFound);
				return null;
			}
			
		case URLBASED:
			
			String urlInvocazionePD = context.getConnettore().getUrlProtocolContext().getUrlInvocazione_formBased();
			try{
				return RegularExpressionEngine.getStringMatchPattern(urlInvocazionePD, nome);
			}catch(RegExpNotFoundException notFound){
				return null;
			}

		case FORM_BASED:
			
			Properties pForm = context.getConnettore().getUrlProtocolContext().getParametersFormBased();
			if(pForm.containsKey(nome)){
				return pForm.getProperty(nome);
			}
			else{
				return null;
			}
			
		case HEADER_BASED:
			
			Properties pTrasporto = context.getConnettore().getUrlProtocolContext().getParametersTrasporto();
		
			Enumeration<?> en = pTrasporto.keys();
			while (en.hasMoreElements()) {
				Object object = (Object) en.nextElement();
				if(object!=null && object instanceof String){
					String key = (String) object;
					if(key.equals(nome)){
						return pTrasporto.getProperty(key);
					}
					else if(key.equals(nome.toLowerCase())){
						return pTrasporto.getProperty(key);
					}
					else if(key.equals(nome.toUpperCase())){
						return pTrasporto.getProperty(key);
					}
				}
			}
			
			return null;
						
		case SOAPACTION_BASED:
			
			return context.getConnettore().getSoapAction();
			
		case PLUGIN_BASED:
			
			String className = ClassNameProperties.getInstance().getExtended(ID.EXTENDED_CLASSNAME_PROPERTIES_CATEGORY, nome);
			if(className==null){
				throw new Exception("Instance plugin ["+nome+"] error: il tipo non è stato registrato nel registro delle classi ('org.openspcoop2.pdd.extended."+
						ID.EXTENDED_CLASSNAME_PROPERTIES_CATEGORY+"."+nome+"' non esiste)");
			}
			try{
				Class<?> classPlugin = Class.forName(className);
				Object o = classPlugin.newInstance();
				if(o instanceof IRateLimiting){
					
					Dati datiRichiesta = new Dati();
					datiRichiesta.setConnettore(context.getConnettore());
					datiRichiesta.setDatiTransazione(datiTransazione);
					datiRichiesta.setMessaggio(context.getMessaggio());
					datiRichiesta.setPddContext(context.getPddContext());
					
					if(forFilter){
						return ((IRateLimiting)o).estraiValoreFiltro(log,datiRichiesta);
					}
					else{
						return ((IRateLimiting)o).estraiValoreCollezionamentoDati(log,datiRichiesta);
					}
				}else{
					throw new Exception("ClassType ["+o.getClass().getName()+"] unknown");
				}
			}catch(Exception e){
				throw new Exception("Instance plugin ["+nome+"] [class:"+className+"] error: "+e.getMessage(),e);
			}

		}
		
		throw new Exception("TipoFiltro ["+tipo+"] non gestito");
	}
	
}
