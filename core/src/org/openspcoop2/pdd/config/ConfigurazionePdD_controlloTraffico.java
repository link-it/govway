/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.config;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.dao.IAttivazionePolicyServiceSearch;
import org.openspcoop2.core.controllo_traffico.dao.IConfigurazionePolicyServiceSearch;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;

/**     
 * ConfigurazionePdD_controlloTraffico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePdD_controlloTraffico extends AbstractConfigurazionePdDConnectionResourceManager {

	private ServiceManagerProperties smp;
	
	
	public ConfigurazionePdD_controlloTraffico(OpenSPCoop2Properties openspcoopProperties, DriverConfigurazioneDB driver, boolean useConnectionPdD) {
		super(openspcoopProperties, driver, useConnectionPdD, OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTrafficoSql(openspcoopProperties.isControlloTrafficoDebug()));
			
		this.smp = new ServiceManagerProperties();
		this.smp.setShowSql(this.openspcoopProperties.isControlloTrafficoDebug());
		this.smp.setDatabaseType(this.driver.getTipoDB());
	}
	
	
	
	
	private static ConfigurazioneGenerale configurazioneGenerale = null;
	public ConfigurazioneGenerale getConfigurazioneControlloTraffico(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		if( this.configurazioneDinamica || ConfigurazionePdD_controlloTraffico.configurazioneGenerale==null){
			ConfigurazionePdDConnectionResource cr = null;
			try{
				cr = this.getConnection(connectionPdD, "ControlloTraffico.getConfigurazioneGenerale");
				org.openspcoop2.core.controllo_traffico.dao.IServiceManager sm = 
						(org.openspcoop2.core.controllo_traffico.dao.IServiceManager) DAOFactory.getInstance(this.log).
						getServiceManager(org.openspcoop2.core.controllo_traffico.utils.ProjectInfo.getInstance(),
								cr.connectionDB,this.smp,this.log);
				
				ConfigurazionePdD_controlloTraffico.configurazioneGenerale = sm.getConfigurazioneGeneraleServiceSearch().get();
			}
			catch(NotFoundException e) {
				String errorMsg = "Configurazione del Controllo del Traffico non trovata: "+e.getMessage();
				this.log.debug(errorMsg,e);
				throw new DriverConfigurazioneNotFound(errorMsg,e);
			}
			catch(Exception e){
				String errorMsg = "Errore durante la configurazione del Controllo del Traffico: "+e.getMessage();
				this.log.error(errorMsg,e);
				throw new DriverConfigurazioneException(errorMsg,e);
			}
			finally {
				this.releaseConnection(cr);
			}
		}

		return ConfigurazionePdD_controlloTraffico.configurazioneGenerale;

	}
	
	
	
	
	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveAPI(Connection connectionPdD, TipoPdD tipoPdD, String nomePorta) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this._getElencoIdPolicyApiAttive(connectionPdD, tipoPdD, nomePorta, true);
	}
	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveGlobali(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this._getElencoIdPolicyApiAttive(connectionPdD, null, null, false);
	}
	private Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> _getElencoIdPolicyApiAttive(Connection connectionPdD, TipoPdD tipoPdD, String nomePorta, boolean api) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		if(api) {
			if(tipoPdD==null) {
				throw new DriverConfigurazioneException("Tipo PdD non fornito; richiesto per una policy API");
			}
			if(nomePorta==null) {
				throw new DriverConfigurazioneException("Nome Porta non fornito; richiesto per una policy API");
			}
		}
		
		ConfigurazionePdDConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "ControlloTraffico.getElencoIdPolicyAttive");
			org.openspcoop2.core.controllo_traffico.dao.IServiceManager sm = 
					(org.openspcoop2.core.controllo_traffico.dao.IServiceManager) DAOFactory.getInstance(this.log).
					getServiceManager(org.openspcoop2.core.controllo_traffico.utils.ProjectInfo.getInstance(),
							cr.connectionDB,this.smp,this.log);
			
			IAttivazionePolicyServiceSearch search =  sm.getAttivazionePolicyServiceSearch();
			IPaginatedExpression expression = search.newPaginatedExpression();
			
			if(api) {
				if(TipoPdD.DELEGATA.equals(tipoPdD)) {
					expression.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA, RuoloPolicy.DELEGATA.getValue());
				}
				else {
					expression.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA, RuoloPolicy.APPLICATIVA.getValue());
				}
				expression.equals(AttivazionePolicy.model().FILTRO.NOME_PORTA,nomePorta);
			}
			else {
				expression.isNull(AttivazionePolicy.model().FILTRO.NOME_PORTA);
			}
			
			expression.limit(100000); // non dovrebbero esistere tante regole
			expression.addOrder(AttivazionePolicy.model().POSIZIONE, SortOrder.ASC);
			List<IdActivePolicy> list = search.findAllIds(expression);
			if(list!=null && list.size()>0){
				
				// Devo raggrupparle per risorse.
				// Ordinate lo sono gia' poiche' ho fatto l'ordine precedentemente
				Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> map = new HashMap<>();
				for (IdActivePolicy idActivePolicy : list) {
					
					ConfigurazionePolicy confPolicy = null;
					try {
						confPolicy = this.getConfigurazionePolicy(connectionPdD, idActivePolicy.getIdPolicy());
					}
					catch(DriverConfigurazioneNotFound e) {
						this.log.error("Configurazione Policy '' non esistente ? ",e);
					}
					
					ElencoIdPolicyAttive elencoIdPolicy = null;
					
					TipoRisorsaPolicyAttiva tipoPolicyAttiva = TipoRisorsaPolicyAttiva.getTipo(confPolicy.getRisorsa(), confPolicy.isSimultanee());
					if(map.containsKey(tipoPolicyAttiva)) {
						elencoIdPolicy = map.get(tipoPolicyAttiva);
					}
					else {
						elencoIdPolicy = new ElencoIdPolicyAttive();
						map.put(tipoPolicyAttiva, elencoIdPolicy);
					}
					
					elencoIdPolicy.addIdActivePolicy(idActivePolicy);
				}
				
				return map;
			}
			else {
				if(api) {
					throw new NotFoundException("policy API non esistenti per la porta '"+nomePorta+"' (ruolo: "+tipoPdD.getTipo()+")");
				}
				else {
					throw new NotFoundException("policy globali non esistenti");	
				}
			}
		}
		catch(NotFoundException e) {
			String errorMsg = "ElencoIdPolicyAttive del Controllo del Traffico non trovata: "+e.getMessage();
			this.log.debug(errorMsg,e);
			throw new DriverConfigurazioneNotFound(errorMsg,e);
		}
		catch(Exception e){
			String errorMsg = "Errore durante la lettura dell'ElencoIdPolicyAttive del Controllo del Traffico: "+e.getMessage();
			this.log.error(errorMsg,e);
			throw new DriverConfigurazioneException(errorMsg,e);
		}
		finally {
			this.releaseConnection(cr);
		}

	}
	
	
	
	
	
	public AttivazionePolicy getAttivazionePolicy(Connection connectionPdD, String id) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		ConfigurazionePdDConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "ControlloTraffico.getAttivazionePolicy_"+id);
			org.openspcoop2.core.controllo_traffico.dao.IServiceManager sm = 
					(org.openspcoop2.core.controllo_traffico.dao.IServiceManager) DAOFactory.getInstance(this.log).
					getServiceManager(org.openspcoop2.core.controllo_traffico.utils.ProjectInfo.getInstance(),
							cr.connectionDB,this.smp,this.log);
			
			IAttivazionePolicyServiceSearch search =  sm.getAttivazionePolicyServiceSearch();
			IdActivePolicy policyId = new IdActivePolicy();
			policyId.setNome(UniqueIdentifierUtilities.extractIdActivePolicy(id));
			return search.get(policyId);
		}
		catch(NotFoundException e) {
			String errorMsg = "AttivazionePolicy del Controllo del Traffico non trovata: "+e.getMessage();
			this.log.debug(errorMsg,e);
			throw new DriverConfigurazioneNotFound(errorMsg,e);
		}
		catch(Exception e){
			String errorMsg = "Errore durante la lettura dell'AttivazionePolicy del Controllo del Traffico: "+e.getMessage();
			this.log.error(errorMsg,e);
			throw new DriverConfigurazioneException(errorMsg,e);
		}
		finally {
			this.releaseConnection(cr);
		}

	}
	
	
	
	
	public ElencoIdPolicy getElencoIdPolicy(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		ConfigurazionePdDConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "ControlloTraffico.getElencoIdPolicy");
			org.openspcoop2.core.controllo_traffico.dao.IServiceManager sm = 
					(org.openspcoop2.core.controllo_traffico.dao.IServiceManager) DAOFactory.getInstance(this.log).
					getServiceManager(org.openspcoop2.core.controllo_traffico.utils.ProjectInfo.getInstance(),
							cr.connectionDB,this.smp,this.log);
			
			ElencoIdPolicy elencoIdPolicy = new ElencoIdPolicy();
			IConfigurazionePolicyServiceSearch search =  sm.getConfigurazionePolicyServiceSearch();
			IPaginatedExpression expression = search.newPaginatedExpression();
			expression.limit(100000); // non dovrebbero esistere tante regole
			List<IdPolicy> list = search.findAllIds(expression);
			if(list!=null && list.size()>0){
				elencoIdPolicy.setIdPolicyList(list);
			}
			return elencoIdPolicy;
		}
		catch(NotFoundException e) {
			String errorMsg = "ElencoIdPolicy del Controllo del Traffico non trovata: "+e.getMessage();
			this.log.debug(errorMsg,e);
			throw new DriverConfigurazioneNotFound(errorMsg,e);
		}
		catch(Exception e){
			String errorMsg = "Errore durante la lettura dell'ElencoIdPolicy del Controllo del Traffico: "+e.getMessage();
			this.log.error(errorMsg,e);
			throw new DriverConfigurazioneException(errorMsg,e);
		}
		finally {
			this.releaseConnection(cr);
		}

	}
	
	
	
	
	
	public ConfigurazionePolicy getConfigurazionePolicy(Connection connectionPdD, String id) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		ConfigurazionePdDConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "ControlloTraffico.getConfigurazionePolicy_"+id);
			org.openspcoop2.core.controllo_traffico.dao.IServiceManager sm = 
					(org.openspcoop2.core.controllo_traffico.dao.IServiceManager) DAOFactory.getInstance(this.log).
					getServiceManager(org.openspcoop2.core.controllo_traffico.utils.ProjectInfo.getInstance(),
							cr.connectionDB,this.smp,this.log);
			
			IConfigurazionePolicyServiceSearch search =  sm.getConfigurazionePolicyServiceSearch();
			IdPolicy policyId = new IdPolicy();
			policyId.setNome(id);
			return search.get(policyId);

		}
		catch(NotFoundException e) {
			String errorMsg = "ConfigurazionePolicy del Controllo del Traffico non trovata: "+e.getMessage();
			this.log.debug(errorMsg,e);
			throw new DriverConfigurazioneNotFound(errorMsg,e);
		}
		catch(Exception e){
			String errorMsg = "Errore durante la lettura della ConfigurazionePolicy del Controllo del Traffico: "+e.getMessage();
			this.log.error(errorMsg,e);
			throw new DriverConfigurazioneException(errorMsg,e);
		}
		finally {
			this.releaseConnection(cr);
		}

	}
	
	
}
