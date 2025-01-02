/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.config.dynamic;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.constants.PluginCostanti;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.monitor.engine.dynamic.CorePluginLoader;
import org.openspcoop2.monitor.engine.dynamic.IPluginLoader;
import org.openspcoop2.monitor.engine.dynamic.PluginLoader;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.autenticazione.pa.IAutenticazionePortaApplicativa;
import org.openspcoop2.pdd.core.autenticazione.pd.IAutenticazionePortaDelegata;
import org.openspcoop2.pdd.core.autorizzazione.pa.IAutorizzazioneContenutoPortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.pa.IAutorizzazionePortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.pd.IAutorizzazioneContenutoPortaDelegata;
import org.openspcoop2.pdd.core.autorizzazione.pd.IAutorizzazionePortaDelegata;
import org.openspcoop2.pdd.core.behaviour.IBehaviour;
import org.openspcoop2.pdd.core.connettori.IConnettore;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.IRateLimiting;
import org.openspcoop2.pdd.core.handlers.ExitHandler;
import org.openspcoop2.pdd.core.handlers.InRequestHandler;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolHandler;
import org.openspcoop2.pdd.core.handlers.InResponseHandler;
import org.openspcoop2.pdd.core.handlers.InitHandler;
import org.openspcoop2.pdd.core.handlers.IntegrationManagerRequestHandler;
import org.openspcoop2.pdd.core.handlers.IntegrationManagerResponseHandler;
import org.openspcoop2.pdd.core.handlers.OutRequestHandler;
import org.openspcoop2.pdd.core.handlers.OutResponseHandler;
import org.openspcoop2.pdd.core.handlers.PostOutRequestHandler;
import org.openspcoop2.pdd.core.handlers.PostOutResponseHandler;
import org.openspcoop2.pdd.core.handlers.PreInRequestHandler;
import org.openspcoop2.pdd.core.handlers.PreInResponseHandler;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePA;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePD;
import org.openspcoop2.pdd.core.token.attribute_authority.IRetrieveAttributeAuthorityResponseParser;
import org.openspcoop2.pdd.core.token.parser.IDynamicDiscoveryParser;
import org.openspcoop2.pdd.core.token.parser.INegoziazioneTokenParser;
import org.openspcoop2.pdd.core.token.parser.ITokenParser;
import org.openspcoop2.utils.NameValue;

/**
 * PluginLoader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PddPluginLoader extends PluginLoader implements IPluginLoader {

	public static PddPluginLoader getInstance() {
		return (PddPluginLoader) CorePluginLoader.getInstance();
	}
	
	
	private ClassNameProperties className = null;
	private ConfigurazionePdDManager configPdDManager = null;
	
	public PddPluginLoader() {
		super();
		this.className = ClassNameProperties.getInstance();
		this.configPdDManager = ConfigurazionePdDManager.getInstance();
	}
	

	
	// UTILITY
	
	public String getPluginClassName(TipoPlugin tipoPlugin, String tipo, NameValue ... filtri) throws DriverConfigurazioneException {
		return this.getPluginClassName(tipoPlugin.getValue(), tipo, filtri);
	}
	public String getPluginClassName(String tipoPlugin, String tipo, NameValue ... filtri) throws DriverConfigurazioneException {
		if(this.isPluginManagerEnabled()) {
			try {
				if(filtri!=null && filtri.length>0) {
					return this.configPdDManager.getPluginClassNameByFilter(tipoPlugin, tipo, filtri);
				}
				else {
					return this.configPdDManager.getPluginClassName(tipoPlugin, tipo);
				}
			}catch(DriverConfigurazioneNotFound notFound) {
				return null;
			}
		}
		return null;
	}
	public Class<?> getPddDynamicClass(String className, TipoPlugin tipoPlugin, String tipo, NameValue ... filtri) throws CoreException {
		return getPddDynamicClass(className, tipoPlugin.getValue(), tipo, filtri);
	}
	public Class<?> getPddDynamicClass(String className, String tipoPlugin, String tipo, NameValue ... filtri) throws CoreException {
		StringBuilder sFiltro = new StringBuilder();
		if(filtri!=null && filtri.length>0) {
			for (int i = 0; i < filtri.length; i++) {
				NameValue filtro = filtri[i];
				sFiltro.append(" filter-").append(i).append(" '").append(filtro.toString()).append("'");
			}
		}
		if(className==null) {
			try {
				className = getPluginClassName(tipoPlugin, tipo, filtri);
			}catch(Exception e) {
				throw new CoreException("Class not found in registry ("+super.getObjectName(tipoPlugin)+" type '"+tipo+"' "+sFiltro.toString()+"): "+e.getMessage(),e);
			}
		}
		if(className==null) {
			throw new CoreException("Class not found in registry ("+super.getObjectName(tipoPlugin)+" type '"+tipo+"' "+sFiltro.toString()+")");
		}
		return super.getDynamicClass(className, tipoPlugin, tipo);
	}
	
	
	// TIPI DI PLUGINS
	
	public IConnettore newConnettore(String tipo) throws CoreException {
		String classNameRegistered = this.className.getConnettore(tipo);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.CONNETTORE, tipo);
		return (IConnettore) newInstance(c, TipoPlugin.CONNETTORE, tipo);
	}

	public IAutenticazionePortaDelegata newAutenticazionePortaDelegata(String tipo) throws CoreException {
		String classNameRegistered = this.className.getAutenticazionePortaDelegata(tipo);
		NameValue nv = new NameValue(PluginCostanti.FILTRO_RUOLO_NOME, PluginCostanti.FILTRO_RUOLO_VALORE_FRUIZIONE);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.AUTENTICAZIONE, tipo, nv);
		return (IAutenticazionePortaDelegata) newInstance(c, TipoPlugin.AUTENTICAZIONE, tipo);
	}
	
	public IAutenticazionePortaApplicativa newAutenticazionePortaApplicativa(String tipo) throws CoreException {
		String classNameRegistered = this.className.getAutenticazionePortaApplicativa(tipo);
		NameValue nv = new NameValue(PluginCostanti.FILTRO_RUOLO_NOME, PluginCostanti.FILTRO_RUOLO_VALORE_EROGAZIONE);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.AUTENTICAZIONE, tipo, nv);
		return (IAutenticazionePortaApplicativa) newInstance(c, TipoPlugin.AUTENTICAZIONE, tipo);
	}
	
	public IAutorizzazionePortaDelegata newAutorizzazionePortaDelegata(String tipo) throws CoreException {
		String classNameRegistered = this.className.getAutorizzazionePortaDelegata(tipo);
		NameValue nv = new NameValue(PluginCostanti.FILTRO_RUOLO_NOME, PluginCostanti.FILTRO_RUOLO_VALORE_FRUIZIONE);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.AUTORIZZAZIONE, tipo, nv);
		return (IAutorizzazionePortaDelegata) newInstance(c, TipoPlugin.AUTORIZZAZIONE, tipo);
	}
	
	public IAutorizzazionePortaApplicativa newAutorizzazionePortaApplicativa(String tipo) throws CoreException {
		String classNameRegistered = this.className.getAutorizzazionePortaApplicativa(tipo);
		NameValue nv = new NameValue(PluginCostanti.FILTRO_RUOLO_NOME, PluginCostanti.FILTRO_RUOLO_VALORE_EROGAZIONE);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.AUTORIZZAZIONE, tipo, nv);
		return (IAutorizzazionePortaApplicativa) newInstance(c, TipoPlugin.AUTORIZZAZIONE, tipo);
	}
	
	public IAutorizzazioneContenutoPortaDelegata newAutorizzazioneContenutiPortaDelegata(String tipo) throws CoreException {
		String classNameRegistered = this.className.getAutorizzazioneContenutoPortaDelegata(tipo);
		NameValue nv = new NameValue(PluginCostanti.FILTRO_RUOLO_NOME, PluginCostanti.FILTRO_RUOLO_VALORE_FRUIZIONE);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.AUTORIZZAZIONE_CONTENUTI, tipo, nv);
		return (IAutorizzazioneContenutoPortaDelegata) newInstance(c, TipoPlugin.AUTORIZZAZIONE_CONTENUTI, tipo);
	}
	
	public IAutorizzazioneContenutoPortaApplicativa newAutorizzazioneContenutiPortaApplicativa(String tipo) throws CoreException {
		String classNameRegistered = this.className.getAutorizzazioneContenutoPortaApplicativa(tipo);
		NameValue nv = new NameValue(PluginCostanti.FILTRO_RUOLO_NOME, PluginCostanti.FILTRO_RUOLO_VALORE_EROGAZIONE);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.AUTORIZZAZIONE_CONTENUTI, tipo, nv);
		return (IAutorizzazioneContenutoPortaApplicativa) newInstance(c, TipoPlugin.AUTORIZZAZIONE_CONTENUTI, tipo);
	}
	
	public IGestoreIntegrazionePD newIntegrazionePortaDelegata(String tipo) throws CoreException {
		String classNameRegistered = this.className.getIntegrazionePortaDelegata(tipo); 
		NameValue nv = new NameValue(PluginCostanti.FILTRO_RUOLO_NOME, PluginCostanti.FILTRO_RUOLO_VALORE_FRUIZIONE);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.INTEGRAZIONE, tipo, nv);
		return (IGestoreIntegrazionePD) newInstance(c, TipoPlugin.INTEGRAZIONE, tipo);
	}
	
	public IGestoreIntegrazionePA newIntegrazionePortaApplicativa(String tipo) throws CoreException {
		String classNameRegistered = this.className.getIntegrazionePortaApplicativa(tipo);
		NameValue nv = new NameValue(PluginCostanti.FILTRO_RUOLO_NOME, PluginCostanti.FILTRO_RUOLO_VALORE_EROGAZIONE);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.INTEGRAZIONE, tipo, nv);
		return (IGestoreIntegrazionePA) newInstance(c, TipoPlugin.INTEGRAZIONE, tipo);
	}
	
	public InitHandler newInitHandler(String tipo) throws CoreException {
		String classNameRegistered = this.className.getInitHandler(tipo);
		NameValue nv = new NameValue(PluginCostanti.FILTRO_SERVICE_HANDLER_NOME, PluginCostanti.FILTRO_SERVICE_HANDLER_VALORE_INIT);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.SERVICE_HANDLER, tipo, nv);
		return (InitHandler) newInstance(c, TipoPlugin.SERVICE_HANDLER, tipo);
	}
	
	public ExitHandler newExitHandler(String tipo) throws CoreException {
		String classNameRegistered = this.className.getExitHandler(tipo);
		NameValue nv = new NameValue(PluginCostanti.FILTRO_SERVICE_HANDLER_NOME, PluginCostanti.FILTRO_SERVICE_HANDLER_VALORE_EXIT);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.SERVICE_HANDLER, tipo, nv);
		return (ExitHandler) newInstance(c, TipoPlugin.SERVICE_HANDLER, tipo);
	}
	
	public IntegrationManagerRequestHandler newIntegrationManagerRequestHandler(String tipo) throws CoreException {
		String classNameRegistered = this.className.getIntegrationManagerRequestHandler(tipo);
		NameValue nv = new NameValue(PluginCostanti.FILTRO_SERVICE_HANDLER_NOME, PluginCostanti.FILTRO_SERVICE_HANDLER_VALORE_INTEGRATION_MANAGER_REQUEST);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.SERVICE_HANDLER, tipo, nv);
		return (IntegrationManagerRequestHandler) newInstance(c, TipoPlugin.SERVICE_HANDLER, tipo);
	}
	
	public IntegrationManagerResponseHandler newIntegrationManagerResponseHandler(String tipo) throws CoreException {
		String classNameRegistered = this.className.getIntegrationManagerResponseHandler(tipo);
		NameValue nv = new NameValue(PluginCostanti.FILTRO_SERVICE_HANDLER_NOME, PluginCostanti.FILTRO_SERVICE_HANDLER_VALORE_INTEGRATION_MANAGER_RESPONSE);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.SERVICE_HANDLER, tipo, nv);
		return (IntegrationManagerResponseHandler) newInstance(c, TipoPlugin.SERVICE_HANDLER, tipo);
	}
	
	public PreInRequestHandler newPreInRequestHandler(String tipo) throws CoreException {
		String classNameRegistered = this.className.getPreInRequestHandler(tipo);
		NameValue fase = new NameValue(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_PRE_IN);
		NameValue ruolo = new NameValue(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RICHIESTA);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.MESSAGE_HANDLER, tipo, fase, ruolo);
		return (PreInRequestHandler) newInstance(c, TipoPlugin.MESSAGE_HANDLER, tipo);
	}
	
	public InRequestHandler newInRequestHandler(String tipo) throws CoreException {
		String classNameRegistered = this.className.getInRequestHandler(tipo);
		NameValue fase = new NameValue(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_IN);
		NameValue ruolo = new NameValue(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RICHIESTA);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.MESSAGE_HANDLER, tipo, fase, ruolo);
		return (InRequestHandler) newInstance(c, TipoPlugin.MESSAGE_HANDLER, tipo);
	}
	
	public InRequestProtocolHandler newInRequestProtocolHandler(String tipo) throws CoreException {
		String classNameRegistered = this.className.getInRequestProtocolHandler(tipo);
		NameValue fase = new NameValue(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_IN_PROTOCOL_INFO);
		NameValue ruolo = new NameValue(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RICHIESTA);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.MESSAGE_HANDLER, tipo, fase, ruolo);
		return (InRequestProtocolHandler) newInstance(c, TipoPlugin.MESSAGE_HANDLER, tipo);
	}
	
	public OutRequestHandler newOutRequestHandler(String tipo) throws CoreException {
		String classNameRegistered = this.className.getOutRequestHandler(tipo);
		NameValue fase = new NameValue(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_OUT);
		NameValue ruolo = new NameValue(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RICHIESTA);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.MESSAGE_HANDLER, tipo, fase, ruolo);
		return (OutRequestHandler) newInstance(c, TipoPlugin.MESSAGE_HANDLER, tipo);
	}
	
	public PostOutRequestHandler newPostOutRequestHandler(String tipo) throws CoreException {
		String classNameRegistered = this.className.getPostOutRequestHandler(tipo);
		NameValue fase = new NameValue(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_POST_OUT);
		NameValue ruolo = new NameValue(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RICHIESTA);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.MESSAGE_HANDLER, tipo, fase, ruolo);
		return (PostOutRequestHandler) newInstance(c, TipoPlugin.MESSAGE_HANDLER, tipo);
	}
	
	public PreInResponseHandler newPreInResponseHandler(String tipo) throws CoreException {
		String classNameRegistered = this.className.getPreInResponseHandler(tipo);
		NameValue fase = new NameValue(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_PRE_IN);
		NameValue ruolo = new NameValue(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RISPOSTA);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.MESSAGE_HANDLER, tipo, fase, ruolo);
		return (PreInResponseHandler) newInstance(c, TipoPlugin.MESSAGE_HANDLER, tipo);
	}
	
	public InResponseHandler newInResponseHandler(String tipo) throws CoreException {
		String classNameRegistered = this.className.getInResponseHandler(tipo);
		NameValue fase = new NameValue(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_IN);
		NameValue ruolo = new NameValue(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RISPOSTA);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.MESSAGE_HANDLER, tipo, fase, ruolo);
		return (InResponseHandler) newInstance(c, TipoPlugin.MESSAGE_HANDLER, tipo);
	}
	
	public OutResponseHandler newOutResponseHandler(String tipo) throws CoreException {
		String classNameRegistered = this.className.getOutResponseHandler(tipo);
		NameValue fase = new NameValue(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_OUT);
		NameValue ruolo = new NameValue(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RISPOSTA);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.MESSAGE_HANDLER, tipo, fase, ruolo);
		return (OutResponseHandler) newInstance(c, TipoPlugin.MESSAGE_HANDLER, tipo);
	}
	
	public PostOutResponseHandler newPostOutResponseHandler(String tipo) throws CoreException {
		String classNameRegistered = this.className.getPostOutResponseHandler(tipo);
		NameValue fase = new NameValue(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_POST_OUT);
		NameValue ruolo = new NameValue(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RISPOSTA);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.MESSAGE_HANDLER, tipo, fase, ruolo);
		return (PostOutResponseHandler) newInstance(c, TipoPlugin.MESSAGE_HANDLER, tipo);
	}
	
	public IBehaviour newBehaviour(String tipo) throws CoreException {
		String classNameRegistered = this.className.getBehaviour(tipo);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.BEHAVIOUR, tipo);
		return (IBehaviour) newInstance(c, TipoPlugin.BEHAVIOUR, tipo);
	}
	
	public IRateLimiting newRateLimiting(String tipo) throws CoreException {
		String classNameRegistered = this.className.getRateLimiting(tipo);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.RATE_LIMITING, tipo);
		return (IRateLimiting) newInstance(c, TipoPlugin.RATE_LIMITING, tipo);
	}
	
	public IDynamicDiscoveryParser newDynamicDiscovery(String tipo) throws CoreException {
		String classNameRegistered = this.className.getTokenDynamicDiscovery(tipo);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.TOKEN_DYNAMIC_DISCOVERY, tipo);
		return (IDynamicDiscoveryParser) newInstance(c, TipoPlugin.TOKEN_DYNAMIC_DISCOVERY, tipo);
	}
	
	public ITokenParser newTokenValidazione(String tipo) throws CoreException {
		String classNameRegistered = this.className.getTokenValidazione(tipo);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.TOKEN_VALIDAZIONE, tipo);
		return (ITokenParser) newInstance(c, TipoPlugin.TOKEN_VALIDAZIONE, tipo);
	}
	
	public INegoziazioneTokenParser newTokenNegoziazione(String tipo) throws CoreException {
		String classNameRegistered = this.className.getTokenNegoziazione(tipo);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.TOKEN_NEGOZIAZIONE, tipo);
		return (INegoziazioneTokenParser) newInstance(c, TipoPlugin.TOKEN_NEGOZIAZIONE, tipo);
	}
	
	public IRetrieveAttributeAuthorityResponseParser newAttributeAuthority(String tipo) throws CoreException {
		String classNameRegistered = this.className.getAttributeAuthority(tipo);
		Class<?> c = getPddDynamicClass(classNameRegistered, TipoPlugin.ATTRIBUTE_AUTHORITY, tipo);
		return (IRetrieveAttributeAuthorityResponseParser) newInstance(c, TipoPlugin.ATTRIBUTE_AUTHORITY, tipo);
	}
	
}
