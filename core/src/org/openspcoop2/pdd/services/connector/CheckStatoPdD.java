/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.services.connector;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorse;
import org.openspcoop2.pdd.timers.TimerThreshold;
import org.openspcoop2.utils.Identity;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * Servlet che serve per verificare l'installazione di OpenSPCoop.
 * Ritorna 200 in caso l'installazione sia correttamente inizializzata e tutte le risorse disponibili.
 * Ritorna 500 in caso la PdD non sia in funzione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class CheckStatoPdD extends HttpServlet {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	

	@Override public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(log==null)
			log = LoggerWrapperFactory.getLogger(CheckStatoPdD.class);
		
		
		OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
		boolean checkPdDReadEnabled = false;
		if(properties!=null && properties.isCheckPdDReadJMXResourcesEnabled() ){
			checkPdDReadEnabled = true;
		}
			
		// verifico se l'invocazione richiede una lettura di una risorsa jmx
		String resourceName = req.getParameter(CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME);
		if(resourceName!=null && !"".equals(resourceName)){
			
			if(checkPdDReadEnabled==false){
				String msg = "Servizio non abilitato";
				log.error("[CheckStatoPdD] "+msg);
				res.setStatus(500);
				res.getOutputStream().write(msg.getBytes());
				return;
			}
			
			// prima di procedere verifico una eventuale autenticazione
			String username = properties.getCheckPdDReadJMXResourcesUsername();
			String password = properties.getCheckPdDReadJMXResourcesPassword();
			if(username!=null && password!=null){
				Identity identity = new Identity(req);
				if(username.equals(identity.getUsername())==false){
					String msg = "Lettura risorsa ["+resourceName+"] non autorizzata";
					log.error("[CheckStatoPdD] "+msg+". Richiesta effettuata da un username ["+identity.getUsername()+"]");
					res.setStatus(500);
					res.getOutputStream().write(msg.getBytes());	
					return;
				}
				if(password.equals(identity.getPassword())==false){
					String msg = "Lettura risorsa ["+resourceName+"] non autorizzata";
					log.error("[CheckStatoPdD] "+msg+". Richiesta effettuata da un username ["+identity.getUsername()+"] (password errata)");
					res.setStatus(500);
					res.getOutputStream().write(msg.getBytes());
					return;
				}
			}
			
			String attributeName = req.getParameter(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_NAME);
			String attributeValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_VALUE);
			String attributeBooleanValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_BOOLEAN_VALUE);
			String methodName = req.getParameter(CostantiPdD.CHECK_STATO_PDD_METHOD_NAME);
			if(attributeName!=null){
				if(attributeValue!=null || attributeBooleanValue!=null){
					try{
						Object v = attributeValue;
						if(attributeBooleanValue!=null){
							v = Boolean.parseBoolean(attributeBooleanValue);
						}
						OpenSPCoop2Startup.gestoreRisorseJMX_staticInstance.setAttribute(resourceName, attributeName, v);	
					}catch(Exception e){
						String msg = "Aggiornamento attributo ["+attributeName+"] della risorsa ["+resourceName+"] non riuscita (valore:"+attributeValue+"): "+e.getMessage();
						log.error("[CheckStatoPdD] "+msg,e);
						res.setStatus(500);
						res.getOutputStream().write(msg.getBytes());	
						return;
					}
				}
				else{
					try{
						Object value = OpenSPCoop2Startup.gestoreRisorseJMX_staticInstance.getAttribute(resourceName, attributeName);
						res.getOutputStream().write(value.toString().getBytes());	
					}catch(Exception e){
						String msg = "Lettura attributo ["+attributeName+"] della risorsa ["+resourceName+"] non riuscita: "+e.getMessage();
						log.error("[CheckStatoPdD] "+msg,e);
						res.setStatus(500);
						res.getOutputStream().write(msg.getBytes());	
						return;
					}
				}
			}
			else if(attributeValue!=null){
				String msg = "Lettura risorsa ["+resourceName+"] non effettuata, fornito un valore di attributo senza aver indicato il nome";
				log.error("[CheckStatoPdD] "+msg);
				res.setStatus(500);
				res.getOutputStream().write(msg.getBytes());
				return;
			}
			else if(methodName!=null){
				
				String paramValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_PARAM_VALUE);
				Object [] params = null;
				String [] signatures = null;
				if(paramValue!=null && !"".equals(paramValue)){
					params = new Object[] {paramValue};
					signatures = new String[] {String.class.getName()};
				}
				
				try{
					Object value = OpenSPCoop2Startup.gestoreRisorseJMX_staticInstance.invoke(resourceName, methodName, params, signatures);
					res.getOutputStream().write(value.toString().getBytes());	
				}catch(Exception e){
					String msg = "Invocazione metodo ["+methodName+"] della risorsa ["+resourceName+"] non riuscita: "+e.getMessage();
					log.error("[CheckStatoPdD] "+msg,e);
					res.setStatus(500);
					res.getOutputStream().write(msg.getBytes());
					return;
				}
			}
			else{
				String msg = "Lettura risorsa ["+resourceName+"] non effettuata, nessun attributo o metodo richiesto";
				log.error("[CheckStatoPdD] "+msg);
				res.setStatus(500);
				res.getOutputStream().write(msg.getBytes());
				return;
			}
			
		}
			
						
		if( OpenSPCoop2Startup.initialize == false){
			String msg = "Porta di dominio OpenSPCoop non inzializzata";
			log.error("[CheckStatoPdD] "+msg);
			res.setStatus(500);
			res.getOutputStream().write(msg.getBytes());	
		}
		else if( TimerMonitoraggioRisorse.risorseDisponibili == false){
			String msg = "Risorse di sistema non disponibili: "+TimerMonitoraggioRisorse.risorsaNonDisponibile.getMessage();
			log.error("[CheckStatoPdD] "+msg,TimerMonitoraggioRisorse.risorsaNonDisponibile);
			res.setStatus(500);
			res.getOutputStream().write(msg.getBytes());	
		}
		else if( TimerThreshold.freeSpace == false){
			String msg = "Non sono disponibili abbastanza risorse per la gestione della richiesta";
			log.error("[CheckStatoPdD] "+msg);
			res.setStatus(500);
			res.getOutputStream().write(msg.getBytes());	
		}
		else if( Tracciamento.tracciamentoDisponibile == false){
			String msg = "Tracciatura non disponibile: "+Tracciamento.motivoMalfunzionamentoTracciamento.getMessage();
			log.error("[CheckStatoPdD] "+msg,Tracciamento.motivoMalfunzionamentoTracciamento);
			res.setStatus(500);
			res.getOutputStream().write(msg.getBytes());	
		}
		else if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
			String msg = "Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage();
			log.error("[CheckStatoPdD] "+msg,MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			res.setStatus(500);
			res.getOutputStream().write(msg.getBytes());	
		}
		else if( Dump.sistemaDumpDisponibile == false){
			String msg = "Sistema di dump dei contenuti applicativi non disponibile: "+Dump.motivoMalfunzionamentoDump.getMessage();
			log.error("[CheckStatoPdD] "+msg,Dump.motivoMalfunzionamentoDump);
			res.setStatus(500);
			res.getOutputStream().write(msg.getBytes());	
		}
		return;

	}


}
