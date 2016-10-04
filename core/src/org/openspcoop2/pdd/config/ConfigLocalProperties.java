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


package org.openspcoop2.pdd.config;

import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Attachments;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.GestioneErroreCodiceTrasporto;
import org.openspcoop2.core.config.GestioneErroreSoapFault;
import org.openspcoop2.core.config.IndirizzoRisposta;
import org.openspcoop2.core.config.InoltroBusteNonRiscontrate;
import org.openspcoop2.core.config.IntegrationManager;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Risposte;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RouteGateway;
import org.openspcoop2.core.config.RouteRegistro;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDefault;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.ValidazioneBuste;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.AlgoritmoCache;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.GestioneErroreComportamento;
import org.openspcoop2.core.config.constants.RegistroTipo;
import org.openspcoop2.core.config.constants.Severita;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoConnessioneRisposte;
import org.openspcoop2.core.config.constants.ValidazioneBusteTipoControllo;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.utils.resources.CollectionProperties;
import org.openspcoop2.utils.resources.InstanceProperties;
import org.openspcoop2.utils.resources.PropertiesUtilities;


/**
* ConfigLocalProperties
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/

public class ConfigLocalProperties extends InstanceProperties {

	private boolean configLocal = false;
	
	public ConfigLocalProperties(Logger log,String confDir,Properties prop) throws Exception {
		
		super(CostantiPdD.OPENSPCOOP2_LOCAL_HOME,new Properties(), log);  // L'originale del file config non esiste, e' su file XML o su database
		
		CollectionProperties properties = 
				PropertiesUtilities.searchLocalImplementation(CostantiPdD.OPENSPCOOP2_LOCAL_HOME,log, CostantiPdD.OPENSPCOOP2_CONFIG_PROPERTIES , CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH, confDir);
		if(properties!=null){
			this.configLocal = true;
			this.setLocalFileImplementation(properties);
		}
		
		if(prop!=null){
			this.configLocal = true;
			this.setLocalObjectImplementation(prop);
		}
		
	}
		
	public RoutingTable updateRouting(RoutingTable routingTable) throws Exception{
		
		if(!this.configLocal)
			return routingTable;
		
		try{
					
			// Abiltata?
			String enabled = this.getValue("routingTable.enabled");
			if(enabled!=null){
				enabled = enabled.trim();
				if("false".equals(enabled)){
					if(routingTable==null)
						routingTable = new RoutingTable();
					routingTable.setAbilitata(false);
					return routingTable;
				}
			}
			
			// Properties
			String defaultRoutesTmp = this.getValue("routingTable.default.routes");
			String [] defaultRoutes = null;
			if(defaultRoutesTmp!=null && defaultRoutesTmp.length()>0){
				defaultRoutes = defaultRoutesTmp.split(",");
				if(defaultRoutes!=null){
					if(defaultRoutes.length>0){
						for (int i = 0; i < defaultRoutes.length; i++) {
							defaultRoutes[i] = defaultRoutes[i].trim();
						}
					}else{
						defaultRoutes = null;
					}
				}
			}
			
			String staticRoutesTmp = this.getValue("routingTable.routes");
			String [] staticRoutes = null;
			if(staticRoutesTmp!=null && staticRoutesTmp.length()>0){
				staticRoutes = staticRoutesTmp.split(",");
				if(staticRoutes!=null){
					if(staticRoutes.length>0){
						for (int i = 0; i < staticRoutes.length; i++) {
							staticRoutes[i] = staticRoutes[i].trim();
						}
					}else{
						staticRoutes = null;
					}
				}
			}
			
			if(defaultRoutes!=null || staticRoutesTmp!=null){
			
				if(enabled==null){
					throw new Exception("Indicazione se la tabella di routing e' abilitata o meno non fornita");
				}
				if(!"true".equals(enabled) && !"false".equals(enabled) ){
					throw new Exception("Indicazione se la tabella di routing e' abilitata o meno fornita non correttamente");
				}
				if(routingTable==null){
					
					// Se la configurazione originale non forniva routing table, il local env deve definire il default
					if(defaultRoutes==null){
						throw new Exception("Rotte di default non definite");
					}
					
					routingTable = new RoutingTable();
				}
				routingTable.setAbilitata("true".equals(enabled));
			}
			
			
			// Default
			if(defaultRoutes!=null){
				
				// elimino vecchie rotte di default
				if(routingTable.getDefault()!=null){
					while(routingTable.getDefault().sizeRouteList()>0){
						routingTable.getDefault().removeRoute(0);
					}
				}
				
				if(defaultRoutes.length>0){
					if(routingTable.getDefault()==null){
						routingTable.setDefault(new RoutingTableDefault());
					}
				}
				
				for (int i = 0; i < defaultRoutes.length; i++) {
					
					Route route = new Route();
					
					String tipo = this.getValue("routingTable.default.route."+defaultRoutes[i]+".tipo");
					if(tipo==null){
						throw new Exception("Tipo della rotta "+defaultRoutes[i]+" non definito");
					}else{
						tipo = tipo.trim();
					}
					if(!CostantiConfigurazione.ROUTE_REGISTRO.equals(tipo) && !CostantiConfigurazione.ROUTE_GATEWAY.equals(tipo)){
						throw new Exception("Tipo della rotta "+defaultRoutes[i]+" non corretto");
					}
					
					if(CostantiConfigurazione.ROUTE_REGISTRO.equals(tipo)){
						
						RouteRegistro rr = new RouteRegistro();					
						String nome = this.getValue("routingTable.default.route."+defaultRoutes[i]+".registro.nome");
						if(nome!=null){
							nome = nome.trim();
							rr.setNome(nome);
						}
						route.setRegistro(rr);
						
					}else{
						
						RouteGateway rg = new RouteGateway();
						String tipoGW = this.getValue("routingTable.default.route."+defaultRoutes[i]+".gateway.tipo");
						if(tipoGW==null){
							throw new Exception("Tipo del soggetto della rotta gateway "+defaultRoutes[i]+" non definito");
						}else{
							tipoGW = tipoGW.trim();
							rg.setTipo(tipoGW);
						}
						String nomeGW = this.getValue("routingTable.default.route."+defaultRoutes[i]+".gateway.nome");
						if(nomeGW==null){
							throw new Exception("Tipo del soggetto della rotta gateway "+defaultRoutes[i]+" non definito");
						}else{
							nomeGW = nomeGW.trim();
							rg.setNome(nomeGW);
						}
						route.setGateway(rg);
					}
					
					routingTable.getDefault().addRoute(route);

				}
			}
			
			
			// Rotte statiche
			if(staticRoutes!=null){
				
				for (int i = 0; i < staticRoutes.length; i++) {
					
					String tipoSoggettoDestinazione = this.getValue("routingTable.route."+staticRoutes[i]+".soggetto.tipo");
					if(tipoSoggettoDestinazione==null){
						throw new Exception("Tipo del soggetto della rotta statica "+staticRoutes[i]+" non definito");
					}else{
						tipoSoggettoDestinazione = tipoSoggettoDestinazione.trim();
					}
					String nomeSoggettoDestinazione = this.getValue("routingTable.route."+staticRoutes[i]+".soggetto.nome");
					if(nomeSoggettoDestinazione==null){
						throw new Exception("Nome del soggetto della rotta statica "+staticRoutes[i]+" non definito");
					}else{
						nomeSoggettoDestinazione = nomeSoggettoDestinazione.trim();
					}
					
					String valueEnable = this.getValue("routingTable.route."+staticRoutes[i]+".enabled");
					if(valueEnable!=null && "false".equalsIgnoreCase(valueEnable.trim())){
						// Voglio eliminare tale rotta statica
						for(int j=0; j<routingTable.sizeDestinazioneList(); j++){
							if(tipoSoggettoDestinazione.equals(routingTable.getDestinazione(j).getTipo()) &&
									nomeSoggettoDestinazione.equals(routingTable.getDestinazione(j).getNome()) ){
								routingTable.removeDestinazione(j);
								break;
							}
						}
					}
					else{
						// Voglio creare/modificare una nuova rotta statica
						RoutingTableDestinazione rtd = null;
						for(int j=0; j<routingTable.sizeDestinazioneList(); j++){
							if(tipoSoggettoDestinazione.equals(routingTable.getDestinazione(j).getTipo()) &&
									nomeSoggettoDestinazione.equals(routingTable.getDestinazione(j).getNome()) ){
								rtd = routingTable.removeDestinazione(j);
								break;
							}
						}
						if(rtd==null){
							// creo
							rtd = new RoutingTableDestinazione();
							rtd.setTipo(tipoSoggettoDestinazione);
							rtd.setNome(nomeSoggettoDestinazione);
						}
						else{
							// elimino le vecchie rotte
							while(rtd.sizeRouteList()>0){
								rtd.removeRoute(0);
							}
						}
	
						Route route = new Route();
						
						String tipo = this.getValue("routingTable.route."+staticRoutes[i]+".tipo");
						if(tipo==null){
							throw new Exception("Tipo della rotta statica "+staticRoutes[i]+" non definito");
						}else{
							tipo = tipo.trim();
						}
						if(!CostantiConfigurazione.ROUTE_REGISTRO.equals(tipo) && !CostantiConfigurazione.ROUTE_GATEWAY.equals(tipo)){
							throw new Exception("Tipo della rotta statica "+staticRoutes[i]+" non corretto");
						}
						
						if(CostantiConfigurazione.ROUTE_REGISTRO.equals(tipo)){
							
							RouteRegistro rr = new RouteRegistro();					
							String nome = this.getValue("routingTable.route."+staticRoutes[i]+".registro.nome");
							if(nome!=null){
								nome = nome.trim();
								rr.setNome(nome);
							}
							route.setRegistro(rr);
							
						}else{
							
							RouteGateway rg = new RouteGateway();
							String tipoGW = this.getValue("routingTable.route."+staticRoutes[i]+".gateway.tipo");
							if(tipoGW==null){
								throw new Exception("Tipo del soggetto della rotta statica di tipo gateway "+staticRoutes[i]+" non definito");
							}else{
								tipoGW = tipoGW.trim();
								rg.setTipo(tipoGW);
							}
							String nomeGW = this.getValue("routingTable.route."+staticRoutes[i]+".gateway.nome");
							if(nomeGW==null){
								throw new Exception("Tipo del soggetto della rotta statica di tipo gateway "+staticRoutes[i]+" non definito");
							}else{
								nomeGW = nomeGW.trim();
								rg.setNome(nomeGW);
							}
							route.setGateway(rg);
						}
						
						rtd.addRoute(route);
						routingTable.addDestinazione(rtd);
					}
					
				}
				
			}
			
			return routingTable;
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura del file "+CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH+" (RoutingTable): "+e.getMessage(),e);
			throw new Exception("Errore durante la lettura del file "+CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH+" (RoutingTable): "+e.getMessage(),e);
		}
	}
	
	private boolean isStatoCacheDisabilitata(String prefix)throws Exception{
		// **** Cache *****
		String cacheAbilitata = this.getValue(prefix+".cache.enabled");
		boolean cacheDisabilitata = false;
		if(cacheAbilitata!=null){
			if("false".equalsIgnoreCase(cacheAbilitata.trim())){
				cacheDisabilitata = true;
			}
		}
		return cacheDisabilitata;
	}
	
	private Cache readDatiCache(String prefix, Cache cacheParam)throws Exception{
		// **** Cache *****
		Cache cache = cacheParam;
		
		if(this.isStatoCacheDisabilitata(prefix)==false){
			String cacheDimensione = this.getValue(prefix+".cache.dimensione");
			if(cacheDimensione!=null){
				cacheDimensione = cacheDimensione.trim();
				try{
					Integer.parseInt(cacheDimensione);
				}catch(Exception e){
					throw new Exception("Valore impostato non corretto per "+prefix+".cache.dimensione");
				}
			}
			String cacheAlgoritmo = this.getValue(prefix+".cache.algoritmo");
			if(cacheAlgoritmo!=null){
				cacheAlgoritmo = cacheAlgoritmo.trim();
				if(!CostantiConfigurazione.CACHE_LRU.equals(cacheAlgoritmo) && !CostantiConfigurazione.CACHE_MRU.equals(cacheAlgoritmo)){
					throw new Exception("Algoritmo impostato per la cache del "+prefix+" non corretto");
				}
			}
			String cacheItemIdleTime = this.getValue(prefix+".cache.item-idle-time");
			if(cacheItemIdleTime!=null){
				cacheItemIdleTime = cacheItemIdleTime.trim();
				try{
					Integer.parseInt(cacheItemIdleTime);
				}catch(Exception e){
					throw new Exception("Valore impostato non corretto per "+prefix+".cache.item-idle-time");
				}
			}
			String cacheItemLifeSecond = this.getValue(prefix+".cache.item-life-second");
			if(cacheItemLifeSecond!=null){
				cacheItemLifeSecond = cacheItemLifeSecond.trim();
				try{
					Integer.parseInt(cacheItemLifeSecond);
				}catch(Exception e){
					throw new Exception("Valore impostato non corretto per "+prefix+".cache.item-life-second");
				}
			}
		
			if(cacheDimensione!=null || cacheAlgoritmo!=null || cacheItemIdleTime!=null || cacheItemLifeSecond!=null){
				if(cache==null){
					cache = new Cache();
				}
				if(cacheDimensione!=null){
					cache.setDimensione(cacheDimensione);
				}
				if(cacheAlgoritmo!=null){
					cache.setAlgoritmo(AlgoritmoCache.toEnumConstant(cacheAlgoritmo));
				}
				if(cacheItemIdleTime!=null){
					cache.setItemIdleTime(cacheItemIdleTime);
				}
				if(cacheItemLifeSecond!=null){
					cache.setItemLifeSecond(cacheItemLifeSecond);
				}
			}
		}
		return cache;
	}
	
	public AccessoRegistro updateAccessoRegistro(AccessoRegistro accessoRegistro)throws Exception{
			
		if(!this.configLocal)
			return accessoRegistro;
		
		try{
			
			// **** Cache *****
			if(this.isStatoCacheDisabilitata("registro")){
				accessoRegistro.setCache(null);
			}
			else{
				// aggiorno dati
				accessoRegistro.setCache(this.readDatiCache("registro", accessoRegistro.getCache()));
			}
			
			
			// **** Registri *****
			String registriTmp = this.getValue("registri");
			String [] registri = null;
			if(registriTmp!=null){
				registriTmp = registriTmp.trim();
				registri = registriTmp.split(",");
				if(registri!=null){
					if(registri.length>0){
						for (int i = 0; i < registri.length; i++) {
							registri[i] = registri[i].trim();
						}
					}else{
						registri = null;
					}
				}
			}
			
			if(registri!=null){
				for (int i = 0; i < registri.length; i++) {
					
					String nomeRegistro = this.getValue("registro."+registri[i]+".nome");
					if(nomeRegistro==null){
						throw new Exception("Nome del registro "+registri[i]+" non definito");
					}else{
						nomeRegistro = nomeRegistro.trim();
					}
					
					String valueEnable = this.getValue("registro."+registri[i]+".enabled");
					if(valueEnable!=null && "false".equalsIgnoreCase(valueEnable.trim())){
						// Voglio eliminare tale registro
						for(int j=0; j<accessoRegistro.sizeRegistroList(); j++){
							if(nomeRegistro.equals(accessoRegistro.getRegistro(j).getNome()) ){
								accessoRegistro.removeRegistro(j);
								break;
							}
						}
					}
					else{
						// Voglio creare/modificare un registro
						AccessoRegistroRegistro registro = null;
						for(int j=0; j<accessoRegistro.sizeRegistroList(); j++){
							if(nomeRegistro.equals(accessoRegistro.getRegistro(j).getNome()) ){
								registro = accessoRegistro.removeRegistro(j);
								break;
							}
						}
						if(registro==null){
							// creo
							registro = new AccessoRegistroRegistro();
							registro.setNome(nomeRegistro);
						}	
									
						String tipo = this.getValue("registro."+registri[i]+".tipo");
						if(tipo==null){
							throw new Exception("Tipo del registro "+registri[i]+" non definito");
						}else{
							tipo = tipo.trim();
						}
						if(!CostantiConfigurazione.REGISTRO_DB.equals(tipo) && 
								!CostantiConfigurazione.REGISTRO_UDDI.equals(tipo) && 
								!CostantiConfigurazione.REGISTRO_WEB.equals(tipo) && 
								!CostantiConfigurazione.REGISTRO_WS.equals(tipo) && 
								!CostantiConfigurazione.REGISTRO_XML.equals(tipo)){
							throw new Exception("Tipo del registro "+registri[i]+" non corretto");
						}
						registro.setTipo(RegistroTipo.toEnumConstant(tipo));
						
						String location = this.getValue("registro."+registri[i]+".location");
						if(location==null){
							throw new Exception("Location del registro "+registri[i]+" non definito");
						}else{
							location = location.trim();
						}
						registro.setLocation(location);
						
						String username = this.getValue("registro."+registri[i]+".user");
						if(username!=null){
							username = username.trim();
							if("".equals(username)){
								registro.setUser(null);
							}else{
								registro.setUser(username);
							}
						}
						
						String password = this.getValue("registro."+registri[i]+".password");
						if(password!=null){
							password = password.trim();
							if("".equals(password)){
								registro.setPassword(null);
							}else{
								registro.setPassword(password);
							}
						}
						
						accessoRegistro.addRegistro(registro);
					}
					
				}
			}
			
			
			return accessoRegistro;
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura del file "+CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH+" (AccessoRegistro): "+e.getMessage(),e);
			throw new Exception("Errore durante la lettura del file "+CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH+" (AccessoRegistro): "+e.getMessage(),e);
		}
		
	}
	
	
	
	public AccessoConfigurazione updateAccessoConfigurazione(AccessoConfigurazione accessoConfigurazione)throws Exception{
		
		if(!this.configLocal)
			return accessoConfigurazione;
		
		try{
			
			// **** Cache *****
			if(this.isStatoCacheDisabilitata("config")){
				accessoConfigurazione.setCache(null);
			}
			else{
				// aggiorno dati
				accessoConfigurazione.setCache(this.readDatiCache("config", accessoConfigurazione.getCache()));
			}
			
			return accessoConfigurazione;
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura del file "+CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH+" (AccessoConfigurazione): "+e.getMessage(),e);
			throw new Exception("Errore durante la lettura del file "+CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH+" (AccessoConfigurazione): "+e.getMessage(),e);
		}
		
	}
	
	public AccessoDatiAutorizzazione updateAccessoDatiAutorizzazione(AccessoDatiAutorizzazione accessoDatiConfigurazione)throws Exception{
		
		if(!this.configLocal)
			return accessoDatiConfigurazione;
		
		try{
			
			// **** Cache *****
			if(this.isStatoCacheDisabilitata("autorizzazione")){
				accessoDatiConfigurazione.setCache(null);
			}
			else{
				// aggiorno dati
				accessoDatiConfigurazione.setCache(this.readDatiCache("autorizzazione", accessoDatiConfigurazione.getCache()));
			}
			
			return accessoDatiConfigurazione;
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura del file "+CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH+" (AccessoDatiAutorizzazione): "+e.getMessage(),e);
			throw new Exception("Errore durante la lettura del file "+CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH+" (AccessoDatiAutorizzazione): "+e.getMessage(),e);
		}
		
	}
	
	public GestioneErrore updateGestioneErroreCooperazione(GestioneErrore gestioneErrore)throws Exception{
		
		if(!this.configLocal)
			return gestioneErrore;
		
		try{
			return updateGestioneErrore(gestioneErrore,"cooperazione");
		}catch(Exception e){
			this.log.error("Errore durante la lettura del file "+CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH+" (GestioneErrore Cooperazione): "+e.getMessage(),e);
			throw new Exception("Errore durante la lettura del file "+CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH+" (GestioneErrore Cooperazione): "+e.getMessage(),e);
		}
		
	}
	
	public GestioneErrore updateGestioneErroreIntegrazione(GestioneErrore gestioneErrore)throws Exception{
		
		if(!this.configLocal)
			return gestioneErrore;
		
		try{
			return updateGestioneErrore(gestioneErrore,"integrazione");
		}catch(Exception e){
			this.log.error("Errore durante la lettura del file "+CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH+" (GestioneErrore Integrazione): "+e.getMessage(),e);
			throw new Exception("Errore durante la lettura del file "+CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH+" (GestioneErrore Integrazione): "+e.getMessage(),e);
		}
		
	}
		
	private GestioneErrore updateGestioneErrore(GestioneErrore gestioneErrore,String tipo)throws Exception{
		
		String gestioneErroreDefaultComportamento = this.getValue("gestioneErrore."+tipo+".comportamento");
		if(gestioneErroreDefaultComportamento!=null){
			gestioneErroreDefaultComportamento = gestioneErroreDefaultComportamento.trim();
			if(!CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG.equals(gestioneErroreDefaultComportamento) && 
					!CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG.equals(gestioneErroreDefaultComportamento)  ){
				throw new Exception("Comportamento di default per la gestione errore ("+tipo+") non correttamento fornito");
			}
		}
		String gestioneErroreDefaultCadenzaRispedizione = this.getValue("gestioneErrore."+tipo+".cadenzaRispedizione");
		if(gestioneErroreDefaultCadenzaRispedizione!=null){
			gestioneErroreDefaultCadenzaRispedizione = gestioneErroreDefaultCadenzaRispedizione.trim();
			try{
				Integer.parseInt(gestioneErroreDefaultCadenzaRispedizione);
			}catch(Exception e){
				throw new Exception("Cadenza di rispedizione di default per la gestione errore ("+tipo+") non correttamento fornito");
			}
		}
		String gestioneErroreCodiciTrasportoTmp = this.getValue("gestioneErrore."+tipo+".codiciTrasporto");
		String [] gestioneErroreCodiciTrasporto = null;
		if(gestioneErroreCodiciTrasportoTmp!=null){
			gestioneErroreCodiciTrasportoTmp = gestioneErroreCodiciTrasportoTmp.trim();
			gestioneErroreCodiciTrasporto = gestioneErroreCodiciTrasportoTmp.split(",");
			if(gestioneErroreCodiciTrasporto!=null){
				if(gestioneErroreCodiciTrasporto.length>0){
					for (int i = 0; i < gestioneErroreCodiciTrasporto.length; i++) {
						gestioneErroreCodiciTrasporto[i] = gestioneErroreCodiciTrasporto[i].trim();
					}
				}else{
					gestioneErroreCodiciTrasporto = null;
				}
			}
		}
		String gestioneErroreFaultTmp = this.getValue("gestioneErrore."+tipo+".soapFault");
		String [] gestioneErroreFault = null;
		if(gestioneErroreFaultTmp!=null){
			gestioneErroreFaultTmp = gestioneErroreFaultTmp.trim();
			gestioneErroreFault = gestioneErroreFaultTmp.split(",");
			if(gestioneErroreFault!=null){
				if(gestioneErroreFault.length>0){
					for (int i = 0; i < gestioneErroreFault.length; i++) {
						gestioneErroreFault[i] = gestioneErroreFault[i].trim();
					}
				}else{
					gestioneErroreFault = null;
				}
			}
		}
		
		if(gestioneErroreDefaultComportamento!=null || gestioneErroreDefaultCadenzaRispedizione!=null ||
				gestioneErroreCodiciTrasporto!=null || gestioneErroreFault!=null ){
			
			if(gestioneErroreDefaultComportamento==null){
				throw new Exception("Comportamento di default deve essere fornito");
			}
			
			gestioneErrore = new GestioneErrore(); // Creo una nuova gestione di errore sovrascrivendo completamente quella vecchia!!!
			gestioneErrore.setComportamento(GestioneErroreComportamento.toEnumConstant(gestioneErroreDefaultComportamento));
			
			if(gestioneErroreDefaultCadenzaRispedizione!=null){
				gestioneErrore.setCadenzaRispedizione(gestioneErroreDefaultCadenzaRispedizione);
			}
			
			if(gestioneErroreCodiciTrasporto!=null){
				
				// codici di trasporto
				for (int i = 0; i < gestioneErroreCodiciTrasporto.length; i++) {
					
					GestioneErroreCodiceTrasporto ct = new GestioneErroreCodiceTrasporto();
					
					String codiceTrasportoMinimo = this.getValue("gestioneErrore."+tipo+".codiceTrasporto."+gestioneErroreCodiciTrasporto[i]+".valoreMinimo");
					if(codiceTrasportoMinimo!=null){
						codiceTrasportoMinimo = codiceTrasportoMinimo.trim();
						try{
							Integer.parseInt(codiceTrasportoMinimo);
						}catch(Exception e){
							throw new Exception("Codice di trasporto minimo non correttaemnte definito la gestione errore ("+tipo+"), trasporto "+gestioneErroreCodiciTrasporto[i]);
						}
						ct.setValoreMinimo(new Integer(codiceTrasportoMinimo));
					}
					
					String codiceTrasportoMassimo = this.getValue("gestioneErrore."+tipo+".codiceTrasporto."+gestioneErroreCodiciTrasporto[i]+".valoreMassimo");
					if(codiceTrasportoMassimo!=null){
						codiceTrasportoMassimo = codiceTrasportoMassimo.trim();
						try{
							Integer.parseInt(codiceTrasportoMassimo);
						}catch(Exception e){
							throw new Exception("Codice di trasporto massimo non correttaemnte definito la gestione errore ("+tipo+"), trasporto "+gestioneErroreCodiciTrasporto[i]);
						}
						ct.setValoreMassimo(new Integer(codiceTrasportoMassimo));
					}
					
					if(codiceTrasportoMinimo==null && codiceTrasportoMassimo==null){
						throw new Exception("Ne valore minimo, ne valore massimo fornito per la gestione errore ("+tipo+"), codice di trasporto "+gestioneErroreCodiciTrasporto[i]);
					}
					
					String codiceTrasportoComportamento = this.getValue("gestioneErrore."+tipo+".codiceTrasporto."+gestioneErroreCodiciTrasporto[i]+".comportamento");
					if(codiceTrasportoComportamento!=null){
						codiceTrasportoComportamento = codiceTrasportoComportamento.trim();
						if(!CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG.equals(codiceTrasportoComportamento) && 
								!CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG.equals(codiceTrasportoComportamento)  ){
							throw new Exception("Comportamento  per la gestione errore ("+tipo+"), codice di trasporto "+gestioneErroreCodiciTrasporto[i]+" non correttamento fornito");
						}
						ct.setComportamento(GestioneErroreComportamento.toEnumConstant(codiceTrasportoComportamento));
					}
					
					String codiceTrasportoCadenza = this.getValue("gestioneErrore."+tipo+".codiceTrasporto."+gestioneErroreCodiciTrasporto[i]+".cadenzaRispedizione");
					if(codiceTrasportoCadenza!=null){
						codiceTrasportoCadenza = codiceTrasportoCadenza.trim();
						try{
							Integer.parseInt(gestioneErroreDefaultCadenzaRispedizione);
						}catch(Exception e){
							throw new Exception("Cadenza di rispedizione di default per la gestione errore ("+tipo+"), codice di trasporto "+gestioneErroreCodiciTrasporto[i]+" non correttamento fornito");
						}
						ct.setCadenzaRispedizione(codiceTrasportoCadenza);
					}
					
					gestioneErrore.addCodiceTrasporto(ct);
				}
			}
			
			if(gestioneErroreFault!=null){
				
				// fault soap
				for (int i = 0; i < gestioneErroreFault.length; i++) {
					
					GestioneErroreSoapFault fault = new GestioneErroreSoapFault();
					
					String faultActor = this.getValue("gestioneErrore."+tipo+".soapFault."+gestioneErroreFault[i]+".faultActor");
					if(faultActor!=null){
						faultActor = faultActor.trim();
						fault.setFaultActor(faultActor);
					}
					String faultCode = this.getValue("gestioneErrore."+tipo+".soapFault."+gestioneErroreFault[i]+".faultCode");
					if(faultCode!=null){
						faultCode = faultCode.trim();
						fault.setFaultCode(faultCode);
					}
					String faultString = this.getValue("gestioneErrore."+tipo+".soapFault."+gestioneErroreFault[i]+".faultString");
					if(faultString!=null){
						faultString = faultString.trim();
						fault.setFaultString(faultString);
					}
					
					String faultComportamento = this.getValue("gestioneErrore."+tipo+".soapFault."+gestioneErroreFault[i]+".comportamento");
					if(faultComportamento!=null){
						faultComportamento = faultComportamento.trim();
						if(!CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG.equals(faultComportamento) && 
								!CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG.equals(faultComportamento)  ){
							throw new Exception("Comportamento  per la gestione errore ("+tipo+"), soapFault "+gestioneErroreFault[i]+" non correttamento fornito");
						}
						fault.setComportamento(GestioneErroreComportamento.toEnumConstant(faultComportamento));
					}
					
					String faultCadenza = this.getValue("gestioneErrore."+tipo+".soapFault."+gestioneErroreFault[i]+".cadenzaRispedizione");
					if(faultCadenza!=null){
						faultCadenza = faultCadenza.trim();
						try{
							Integer.parseInt(gestioneErroreDefaultCadenzaRispedizione);
						}catch(Exception e){
							throw new Exception("Cadenza di rispedizione di default per la gestione errore ("+tipo+"), soapFault "+gestioneErroreFault[i]+" non correttamento fornito");
						}
						fault.setCadenzaRispedizione(faultCadenza);
					}
					
					gestioneErrore.addSoapFault(fault);
				}
			}
			
		}
		
		return gestioneErrore;
	}
	
	public Configurazione updateConfigurazione(Configurazione configurazione)throws Exception{
		
		if(!this.configLocal)
			return configurazione;
		
		try{
		
			// validazione-buste
			String validazioneBuste_stato = this.getValue("validazione.protocol.stato");
			if(validazioneBuste_stato!=null){
				validazioneBuste_stato = validazioneBuste_stato.trim();
				if(!CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.toString().equals(validazioneBuste_stato) && 
						!CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO.toString().equals(validazioneBuste_stato) && 
						!CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.toString().equals(validazioneBuste_stato) ){
					throw new Exception("Stato della validazione protocol non corretto");
				}
			}
			String validazioneBuste_controllo = this.getValue("validazione.protocol.controllo");
			if(validazioneBuste_controllo!=null){
				validazioneBuste_controllo = validazioneBuste_controllo.trim();
				if(!CostantiConfigurazione.VALIDAZIONE_PROTOCOL_LIVELLO_NORMALE.equals(validazioneBuste_controllo) && 
						!CostantiConfigurazione.VALIDAZIONE_PROTOCOL_LIVELLO_RIGIDO.equals(validazioneBuste_controllo) ){
					throw new Exception("Tipo di controllo della validazione protocol non corretto");
				}
			}
			String validazioneBuste_profiloCollaborazione = this.getValue("validazione.protocol.profiloCollaborazione");
			if(validazioneBuste_profiloCollaborazione!=null){
				validazioneBuste_profiloCollaborazione = validazioneBuste_profiloCollaborazione.trim();
				if(!CostantiConfigurazione.ABILITATO.equals(validazioneBuste_profiloCollaborazione) && 
						!CostantiConfigurazione.DISABILITATO.equals(validazioneBuste_profiloCollaborazione)  ){
					throw new Exception("Stato della validazione del profilo di collaborazione protocol non corretto");
				}
			}
			String validazioneBuste_manifestAttachments = this.getValue("validazione.protocol.manifestAttachments");
			if(validazioneBuste_manifestAttachments!=null){
				validazioneBuste_manifestAttachments = validazioneBuste_manifestAttachments.trim();
				if(!CostantiConfigurazione.ABILITATO.equals(validazioneBuste_manifestAttachments) && 
						!CostantiConfigurazione.DISABILITATO.equals(validazioneBuste_manifestAttachments)  ){
					throw new Exception("Stato della validazione del manifest degli attachments non corretto");
				}
			}
			if(validazioneBuste_stato!=null || validazioneBuste_controllo!=null || 
					validazioneBuste_profiloCollaborazione!=null || validazioneBuste_manifestAttachments!=null){
				if(configurazione.getValidazioneBuste()==null){
					configurazione.setValidazioneBuste(new ValidazioneBuste());
				}
				if(validazioneBuste_stato!=null){
					configurazione.getValidazioneBuste().setStato(StatoFunzionalitaConWarning.toEnumConstant(validazioneBuste_stato));
				}
				if(validazioneBuste_controllo!=null){
					configurazione.getValidazioneBuste().setControllo(ValidazioneBusteTipoControllo.toEnumConstant(validazioneBuste_controllo));
				}
				if(validazioneBuste_profiloCollaborazione!=null){
					configurazione.getValidazioneBuste().setProfiloCollaborazione(StatoFunzionalita.toEnumConstant(validazioneBuste_profiloCollaborazione));
				}
				if(validazioneBuste_manifestAttachments!=null){
					configurazione.getValidazioneBuste().setManifestAttachments(StatoFunzionalita.toEnumConstant(validazioneBuste_manifestAttachments));
				}
			}
			
			
			
			// validazione-contenuti-applicativi
			String validazioneContenutiApplicativi_stato = this.getValue("validazione.contenutiApplicativi.stato");
			if(validazioneContenutiApplicativi_stato!=null){
				validazioneContenutiApplicativi_stato = validazioneContenutiApplicativi_stato.trim();
				if(!CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.toString().equals(validazioneContenutiApplicativi_stato) && 
						!CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO.toString().equals(validazioneContenutiApplicativi_stato) && 
						!CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.toString().equals(validazioneContenutiApplicativi_stato) ){
					throw new Exception("Stato della validazione dei contenuti applicativi non corretto");
				}
			}
			String validazioneContenutiApplicativi_tipo = this.getValue("validazione.contenutiApplicativi.tipo");
			if(validazioneContenutiApplicativi_tipo!=null){
				validazioneContenutiApplicativi_tipo = validazioneContenutiApplicativi_tipo.trim();
				if(!CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.equals(validazioneContenutiApplicativi_tipo) && 
						!CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL.equals(validazioneContenutiApplicativi_tipo)  && 
						!CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutiApplicativi_tipo) ){
					throw new Exception("Tipo di controllo della validazione dei contenuti applicativi non corretto");
				}
			}
			if(validazioneContenutiApplicativi_stato!=null || validazioneContenutiApplicativi_tipo!=null){
				if(configurazione.getValidazioneContenutiApplicativi()==null){
					configurazione.setValidazioneContenutiApplicativi(new ValidazioneContenutiApplicativi());
				}
				if(validazioneContenutiApplicativi_stato!=null){
					configurazione.getValidazioneContenutiApplicativi().setStato(StatoFunzionalitaConWarning.toEnumConstant(validazioneContenutiApplicativi_stato));
				}
				if(validazioneContenutiApplicativi_tipo!=null){
					configurazione.getValidazioneContenutiApplicativi().setTipo(ValidazioneContenutiApplicativiTipo.toEnumConstant(validazioneContenutiApplicativi_tipo));
				}
			}
			
			
			
			
			// indirizzo telematico
			String indirizzoRisposta_utilizzo = this.getValue("indirizzoRisposta.utilizzo");
			if(indirizzoRisposta_utilizzo!=null){
				indirizzoRisposta_utilizzo = indirizzoRisposta_utilizzo.trim();
				if(!CostantiConfigurazione.ABILITATO.equals(indirizzoRisposta_utilizzo) && 
						!CostantiConfigurazione.DISABILITATO.equals(indirizzoRisposta_utilizzo) ){
					throw new Exception("Impostazione sull'utilizzo dell'indirizzo risposta non corretta");
				}
				if(configurazione.getIndirizzoRisposta()==null){
					configurazione.setIndirizzoRisposta(new IndirizzoRisposta());	
				}
				configurazione.getIndirizzoRisposta().setUtilizzo(StatoFunzionalita.toEnumConstant(indirizzoRisposta_utilizzo));
			}
			
			
			
			// Attachments
			String attachments_gestioneManifest = this.getValue("attachments.gestioneManifest");
			if(attachments_gestioneManifest!=null){
				attachments_gestioneManifest = attachments_gestioneManifest.trim();
				if(!CostantiConfigurazione.ABILITATO.equals(attachments_gestioneManifest) && 
						!CostantiConfigurazione.DISABILITATO.equals(attachments_gestioneManifest) ){
					throw new Exception("Impostazione sulla gestione del manifest non corretta");
				}
				if(configurazione.getAttachments()==null){
					configurazione.setAttachments(new Attachments());	
				}
				configurazione.getAttachments().setGestioneManifest(StatoFunzionalita.toEnumConstant(attachments_gestioneManifest));
			}
			
			
			
			// Risposte
			String risposte_connection = this.getValue("risposte.connessione");
			if(risposte_connection!=null){
				risposte_connection = risposte_connection.trim();
				if(!CostantiConfigurazione.CONNECTION_REPLY.equals(risposte_connection) && 
						!CostantiConfigurazione.NEW_CONNECTION.equals(risposte_connection) ){
					throw new Exception("Impostazione sulla gestione della connessione per le risposte non corretta");
				}
				if(configurazione.getRisposte()==null){
					configurazione.setRisposte(new Risposte());	
				}
				configurazione.getRisposte().setConnessione(TipoConnessioneRisposte.toEnumConstant(risposte_connection));
			}
			
			
			
			
			// InoltroBuste
			String inoltroBuste_cadenza = this.getValue("inoltroBusteNonRiscontrate.cadenza");
			if(inoltroBuste_cadenza!=null){
				inoltroBuste_cadenza = inoltroBuste_cadenza.trim();
				try{
					Integer.parseInt(inoltroBuste_cadenza);
				}catch(Exception e){
					throw new Exception("Impostazione sulla cadenza per l'inoltro di buste non riscontrate non corretta");
				}
				if(configurazione.getInoltroBusteNonRiscontrate()==null){
					configurazione.setInoltroBusteNonRiscontrate(new InoltroBusteNonRiscontrate());	
				}
				configurazione.getInoltroBusteNonRiscontrate().setCadenza(inoltroBuste_cadenza);
			}
			
			
			
			// Messaggi diagnostici
			
			String msgDiagnostici_livelloSeveritaLog4J = this.getValue("messaggiDiagnostici.livelloSeveritaLog4j");
			if(msgDiagnostici_livelloSeveritaLog4J!=null){
				msgDiagnostici_livelloSeveritaLog4J = msgDiagnostici_livelloSeveritaLog4J.trim();
				if(!LogLevels.LIVELLO_ALL.equals(msgDiagnostici_livelloSeveritaLog4J) && 
						!LogLevels.LIVELLO_DEBUG_HIGH.equals(msgDiagnostici_livelloSeveritaLog4J) && 
						!LogLevels.LIVELLO_DEBUG_MEDIUM.equals(msgDiagnostici_livelloSeveritaLog4J)&& 
						!LogLevels.LIVELLO_DEBUG_LOW.equals(msgDiagnostici_livelloSeveritaLog4J)&& 
						!LogLevels.LIVELLO_ERROR_INTEGRATION.equals(msgDiagnostici_livelloSeveritaLog4J)&& 
						!LogLevels.LIVELLO_ERROR_PROTOCOL.equals(msgDiagnostici_livelloSeveritaLog4J)&& 
						!LogLevels.LIVELLO_FATAL.equals(msgDiagnostici_livelloSeveritaLog4J)&& 
						!LogLevels.LIVELLO_INFO_INTEGRATION.equals(msgDiagnostici_livelloSeveritaLog4J)&& 
						!LogLevels.LIVELLO_INFO_PROTOCOL.equals(msgDiagnostici_livelloSeveritaLog4J)&& 
						!LogLevels.LIVELLO_OFF.equals(msgDiagnostici_livelloSeveritaLog4J)){
					throw new Exception("Impostazione sul livello log4j di severita dei messsaggi diagnostici emessi non corretta");
				}
				configurazione.getMessaggiDiagnostici().setSeveritaLog4j(Severita.toEnumConstant(msgDiagnostici_livelloSeveritaLog4J));
			}
			String msgDiagnostici_livelloSeverita = this.getValue("messaggiDiagnostici.livelloSeverita");
			if(msgDiagnostici_livelloSeverita!=null){
				msgDiagnostici_livelloSeverita = msgDiagnostici_livelloSeverita.trim();
				if(!LogLevels.LIVELLO_ALL.equals(msgDiagnostici_livelloSeverita) && 
						!LogLevels.LIVELLO_DEBUG_HIGH.equals(msgDiagnostici_livelloSeverita) && 
						!LogLevels.LIVELLO_DEBUG_MEDIUM.equals(msgDiagnostici_livelloSeverita)&& 
						!LogLevels.LIVELLO_DEBUG_LOW.equals(msgDiagnostici_livelloSeverita)&& 
						!LogLevels.LIVELLO_ERROR_INTEGRATION.equals(msgDiagnostici_livelloSeverita)&& 
						!LogLevels.LIVELLO_ERROR_PROTOCOL.equals(msgDiagnostici_livelloSeverita)&& 
						!LogLevels.LIVELLO_FATAL.equals(msgDiagnostici_livelloSeverita)&& 
						!LogLevels.LIVELLO_INFO_INTEGRATION.equals(msgDiagnostici_livelloSeverita)&& 
						!LogLevels.LIVELLO_INFO_PROTOCOL.equals(msgDiagnostici_livelloSeverita)&& 
						!LogLevels.LIVELLO_OFF.equals(msgDiagnostici_livelloSeverita)){
					throw new Exception("Impostazione sul livello di severita dei messsaggi diagnostici emessi non corretta");
				}
				configurazione.getMessaggiDiagnostici().setSeverita(Severita.toEnumConstant(msgDiagnostici_livelloSeverita));
			}
			
			// Messaggi diagnostici (Appender)
			
			String msgDiagnosticiAppendersDisabledTmp = this.getValue("messaggiDiagnostici.appenders.disabled");
			boolean disabilitatiMsgDiagnosticiAppenderOriginali = false;
			if(msgDiagnosticiAppendersDisabledTmp!=null && "true".equals(msgDiagnosticiAppendersDisabledTmp)){
				disabilitatiMsgDiagnosticiAppenderOriginali = true;
				while(configurazione.getMessaggiDiagnostici().sizeOpenspcoopAppenderList()>0){
					configurazione.getMessaggiDiagnostici().removeOpenspcoopAppender(0);
				}
			}
			
			String msgDiagnosticiAppendersTmp = this.getValue("messaggiDiagnostici.appenders");
			String [] msgDiagnosticiAppenders = null;
			if(msgDiagnosticiAppendersTmp!=null){
				msgDiagnosticiAppendersTmp = msgDiagnosticiAppendersTmp.trim();
				msgDiagnosticiAppenders = msgDiagnosticiAppendersTmp.split(",");
				if(msgDiagnosticiAppenders!=null){
					if(msgDiagnosticiAppenders.length>0){
						for (int i = 0; i < msgDiagnosticiAppenders.length; i++) {
							msgDiagnosticiAppenders[i] = msgDiagnosticiAppenders[i].trim();
						}
					}else{
						msgDiagnosticiAppenders = null;
					}
				}
			}
			if(msgDiagnosticiAppenders!=null){
				for (int i = 0; i < msgDiagnosticiAppenders.length; i++) {
					
					String tipo = this.getValue("messaggiDiagnostici.appender."+msgDiagnosticiAppenders[i]+".tipo");
					if(tipo==null){
						throw new Exception("Tipo dell'appender dei messaggi diagnostici "+msgDiagnosticiAppenders[i]+" non definito");
					}else{
						tipo = tipo.trim();
					}
					
					String valueEnable = this.getValue("messaggiDiagnostici.appender."+msgDiagnosticiAppenders[i]+".enabled");
					if(valueEnable!=null && "false".equalsIgnoreCase(valueEnable.trim())){
						// Voglio eliminare tale appender
						if(disabilitatiMsgDiagnosticiAppenderOriginali==false){
							for(int j=0; j<configurazione.getMessaggiDiagnostici().sizeOpenspcoopAppenderList(); j++){
								if(tipo.equals(configurazione.getMessaggiDiagnostici().getOpenspcoopAppender(j).getTipo()) ){
									configurazione.getMessaggiDiagnostici().removeOpenspcoopAppender(j);
									break;
								}
							}
						}// else ho gia eliminato gli appenders originali
					}
					else{
						// Voglio creare/modificare un appender
						OpenspcoopAppender appender = null;
						for(int j=0; j<configurazione.getMessaggiDiagnostici().sizeOpenspcoopAppenderList(); j++){
							if(tipo.equals(configurazione.getMessaggiDiagnostici().getOpenspcoopAppender(j).getTipo()) ){
								appender = configurazione.getMessaggiDiagnostici().removeOpenspcoopAppender(j);
								break;
							}
						}
						if(appender==null){
							// creo
							appender = new OpenspcoopAppender();
							appender.setTipo(tipo);
						}	
						
						// Elimino vecchie properties
						while(appender.sizePropertyList()>0){
							appender.removeProperty(0);
						}
						
						// Aggiunto nuove
						Properties properties = this.readProperties("messaggiDiagnostici.appender."+msgDiagnosticiAppenders[i]+".property.");
						Enumeration<?> keys = properties.keys();
						while (keys.hasMoreElements()) {
							String key = (String) keys.nextElement();
							Property ap = new Property();
							ap.setNome(key);
							ap.setValore(properties.getProperty(key));
							appender.addProperty(ap);
						}
						
						configurazione.getMessaggiDiagnostici().addOpenspcoopAppender(appender);
					}
					
				}
			}
			
			
			
			// Tracciamento
			
			String tracciamentoBuste = this.getValue("tracciamento.buste");
			if(tracciamentoBuste!=null){
				tracciamentoBuste = tracciamentoBuste.trim();
				if(!CostantiConfigurazione.ABILITATO.equals(tracciamentoBuste) && 
						!CostantiConfigurazione.DISABILITATO.equals(tracciamentoBuste)){
					throw new Exception("Impostazione sul tracciamento buste non corretta");
				}
			}
			String tracciamentoDump = this.getValue("tracciamento.dump");
			if(tracciamentoDump!=null){
				tracciamentoDump = tracciamentoDump.trim();
				if(!CostantiConfigurazione.ABILITATO.equals(tracciamentoDump) && 
						!CostantiConfigurazione.DISABILITATO.equals(tracciamentoDump)){
					throw new Exception("Impostazione sul dump applicativo non corretta");
				}
			}
			
			String tracciamentoAppendersDisabledTmp = this.getValue("tracciamento.appenders.disabled");
			boolean disabilitatiTracciamentoAppenderOriginali = false;
			if(tracciamentoAppendersDisabledTmp!=null && "true".equals(tracciamentoAppendersDisabledTmp)){
				disabilitatiTracciamentoAppenderOriginali = true;
				if(configurazione.getTracciamento()!=null){
					while(configurazione.getTracciamento().sizeOpenspcoopAppenderList()>0){
						configurazione.getTracciamento().removeOpenspcoopAppender(0);
					}
				}
			}
			
			String tracciamentoAppendersTmp = this.getValue("tracciamento.appenders");
			String [] tracciamentoAppenders = null;
			if(tracciamentoAppendersTmp!=null){
				tracciamentoAppendersTmp = tracciamentoAppendersTmp.trim();
				tracciamentoAppenders = tracciamentoAppendersTmp.split(",");
				if(tracciamentoAppenders!=null){
					if(tracciamentoAppenders.length>0){
						for (int i = 0; i < tracciamentoAppenders.length; i++) {
							tracciamentoAppenders[i] = tracciamentoAppenders[i].trim();
						}
					}else{
						tracciamentoAppenders = null;
					}
				}
			}
			if(tracciamentoBuste!=null || tracciamentoDump!=null || tracciamentoAppenders!=null){
				if(configurazione.getTracciamento()==null){
					configurazione.setTracciamento(new Tracciamento());
					if(tracciamentoBuste==null){
						configurazione.getTracciamento().setBuste(CostantiConfigurazione.ABILITATO); // default
					}
					if(tracciamentoDump==null){
						configurazione.getTracciamento().setDump(CostantiConfigurazione.DISABILITATO); // default
					}
				}
			}
			if(tracciamentoBuste!=null){
				configurazione.getTracciamento().setBuste(StatoFunzionalita.toEnumConstant(tracciamentoBuste));
			}
			if(tracciamentoDump!=null){
				configurazione.getTracciamento().setDump(StatoFunzionalita.toEnumConstant(tracciamentoDump));
			}
			if(tracciamentoAppenders!=null){
				
				// Tracciamento appenders
				
				for (int i = 0; i < tracciamentoAppenders.length; i++) {
					
					String tipo = this.getValue("tracciamento.appender."+tracciamentoAppenders[i]+".tipo");
					if(tipo==null){
						throw new Exception("Tipo dell'appender delle tracce "+tracciamentoAppenders[i]+" non definito");
					}else{
						tipo = tipo.trim();
					}
					
					String valueEnable = this.getValue("tracciamento.appender."+tracciamentoAppenders[i]+".enabled");
					if(valueEnable!=null && "false".equalsIgnoreCase(valueEnable.trim())){
						// Voglio eliminare tale appender
						if(disabilitatiTracciamentoAppenderOriginali==false){
							for(int j=0; j<configurazione.getTracciamento().sizeOpenspcoopAppenderList(); j++){
								if(tipo.equals(configurazione.getTracciamento().getOpenspcoopAppender(j).getTipo()) ){
									configurazione.getTracciamento().removeOpenspcoopAppender(j);
									break;
								}
							}
						}// else ho gia eliminato gli appenders originali
					}
					else{
						// Voglio creare/modificare un appender
						OpenspcoopAppender appender = null;
						for(int j=0; j<configurazione.getTracciamento().sizeOpenspcoopAppenderList(); j++){
							if(tipo.equals(configurazione.getTracciamento().getOpenspcoopAppender(j).getTipo()) ){
								appender = configurazione.getTracciamento().removeOpenspcoopAppender(j);
								break;
							}
						}
						if(appender==null){
							// creo
							appender = new OpenspcoopAppender();
							appender.setTipo(tipo);
						}	
						
						// Elimino vecchie properties
						while(appender.sizePropertyList()>0){
							appender.removeProperty(0);
						}
						
						// Aggiunto nuove
						Properties properties = this.readProperties("tracciamento.appender."+tracciamentoAppenders[i]+".property.");
						Enumeration<?> keys = properties.keys();
						while (keys.hasMoreElements()) {
							String key = (String) keys.nextElement();
							Property ap = new Property();
							ap.setNome(key);
							ap.setValore(properties.getProperty(key));
							appender.addProperty(ap);
						}
						
						configurazione.getTracciamento().addOpenspcoopAppender(appender);
					}
					
				}
				
			}
			
			
			
			
			
			
			
			// IntegrationManager
			String integrationManager_autenticazione = this.getValue("integrationManager.autenticazione");
			if(integrationManager_autenticazione!=null){
				integrationManager_autenticazione = integrationManager_autenticazione.trim();
				if(configurazione.getIntegrationManager()==null){
					configurazione.setIntegrationManager(new IntegrationManager());	
				}
				configurazione.getIntegrationManager().setAutenticazione(integrationManager_autenticazione);
			}
			
			
			
			
			
			return configurazione;
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura del file "+CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH+" (Configurazione): "+e.getMessage(),e);
			throw new Exception("Errore durante la lettura del file "+CostantiPdD.OPENSPCOOP2_CONFIG_LOCAL_PATH+" (Configurazione): "+e.getMessage(),e);
		}
		
	}
}
