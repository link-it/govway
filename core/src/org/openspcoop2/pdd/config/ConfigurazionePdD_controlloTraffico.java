package org.openspcoop2.pdd.config;

import java.sql.Connection;
import java.util.List;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.dao.IAttivazionePolicyServiceSearch;
import org.openspcoop2.core.controllo_traffico.dao.IConfigurazionePolicyServiceSearch;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.slf4j.Logger;

public class ConfigurazionePdD_controlloTraffico {

	private OpenSPCoop2Properties openspcoopProperties;
	private boolean configurazioneDinamica = false;
	private boolean useConnectionPdD = false;
	private DriverConfigurazioneDB driver;
	private Logger log;
	
	public ConfigurazionePdD_controlloTraffico(OpenSPCoop2Properties openspcoopProperties, DriverConfigurazioneDB driver, boolean useConnectionPdD) {
		this.openspcoopProperties = openspcoopProperties;
		this.configurazioneDinamica = this.openspcoopProperties.isConfigurazioneDinamica();
		this.useConnectionPdD = useConnectionPdD;
		this.driver = driver;
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTrafficoSql(this.openspcoopProperties.isControlloTrafficoDebug());
	}
	
	
	
	
	
	// IMPL
	
	private ConnectionResource getConnection(Connection connectionPdD, String methodName) throws Exception{
		ConnectionResource cr = new ConnectionResource();
		if(connectionPdD!=null && this.useConnectionPdD){
			cr.connectionDB = connectionPdD;
			cr.connectionPdD = true;
		}
		else{
			cr.connectionDB = this.driver.getConnection(methodName);
			cr.connectionPdD = false;
		}
		return cr;
	}
	private void releaseConnection(ConnectionResource cr) {
		if(cr!=null && cr.connectionDB!=null && !cr.connectionPdD) {
			this.driver.releaseConnection(cr.connectionDB);
		}
	}
	
	
	
	private static ConfigurazioneGenerale configurazioneGenerale = null;
	protected ConfigurazioneGenerale getConfigurazioneControlloTraffico(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		if( this.configurazioneDinamica || ConfigurazionePdD_controlloTraffico.configurazioneGenerale==null){
			ConnectionResource cr = null;
			try{
				cr = this.getConnection(connectionPdD, "ControlloTraffico.getConfigurazioneGenerale");
				org.openspcoop2.core.controllo_traffico.dao.IServiceManager sm = 
						(org.openspcoop2.core.controllo_traffico.dao.IServiceManager) DAOFactory.getInstance(this.log).
						getServiceManager(org.openspcoop2.core.controllo_traffico.utils.ProjectInfo.getInstance(),
								cr.connectionDB,this.log);
				
				ConfigurazionePdD_controlloTraffico.configurazioneGenerale = sm.getConfigurazioneGeneraleServiceSearch().get();
			}
			catch(NotFoundException e) {
				String errorMsg = "Configurazione del Controllo del Traffico non trovata: "+e.getMessage();
				this.log.error(errorMsg,e);
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
	
	
	
	
	protected ElencoIdPolicyAttive getElencoIdPolicyAttive(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
				
		ConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "ControlloTraffico.getElencoIdPolicyAttive");
			org.openspcoop2.core.controllo_traffico.dao.IServiceManager sm = 
					(org.openspcoop2.core.controllo_traffico.dao.IServiceManager) DAOFactory.getInstance(this.log).
					getServiceManager(org.openspcoop2.core.controllo_traffico.utils.ProjectInfo.getInstance(),
							cr.connectionDB,this.log);
			
			ElencoIdPolicyAttive elencoIdPolicy = new ElencoIdPolicyAttive();
			IAttivazionePolicyServiceSearch search =  sm.getAttivazionePolicyServiceSearch();
			IPaginatedExpression expression = search.newPaginatedExpression();
			expression.limit(100000); // non dovrebbero esistere tante regole
			List<IdActivePolicy> list = search.findAllIds(expression);
			if(list!=null && list.size()>0){
				elencoIdPolicy.setIdActivePolicyList(list);
			}
			return elencoIdPolicy;
		}
		catch(NotFoundException e) {
			String errorMsg = "ElencoIdPolicyAttive del Controllo del Traffico non trovata: "+e.getMessage();
			this.log.error(errorMsg,e);
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
	
	
	
	
	
	protected AttivazionePolicy getAttivazionePolicy(Connection connectionPdD, String id) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		ConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "ControlloTraffico.getAttivazionePolicy_"+id);
			org.openspcoop2.core.controllo_traffico.dao.IServiceManager sm = 
					(org.openspcoop2.core.controllo_traffico.dao.IServiceManager) DAOFactory.getInstance(this.log).
					getServiceManager(org.openspcoop2.core.controllo_traffico.utils.ProjectInfo.getInstance(),
							cr.connectionDB,this.log);
			
			IAttivazionePolicyServiceSearch search =  sm.getAttivazionePolicyServiceSearch();
			IdActivePolicy policyId = new IdActivePolicy();
			policyId.setNome(UniqueIdentifierUtilities.extractIdActivePolicy(id));
			return search.get(policyId);
		}
		catch(NotFoundException e) {
			String errorMsg = "AttivazionePolicy del Controllo del Traffico non trovata: "+e.getMessage();
			this.log.error(errorMsg,e);
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
	
	
	
	
	protected ElencoIdPolicy getElencoIdPolicy(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		ConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "ControlloTraffico.getElencoIdPolicy");
			org.openspcoop2.core.controllo_traffico.dao.IServiceManager sm = 
					(org.openspcoop2.core.controllo_traffico.dao.IServiceManager) DAOFactory.getInstance(this.log).
					getServiceManager(org.openspcoop2.core.controllo_traffico.utils.ProjectInfo.getInstance(),
							cr.connectionDB,this.log);
			
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
			this.log.error(errorMsg,e);
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
	
	
	
	
	
	protected ConfigurazionePolicy getConfigurazionePolicy(Connection connectionPdD, String id) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		ConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "ControlloTraffico.getConfigurazionePolicy_"+id);
			org.openspcoop2.core.controllo_traffico.dao.IServiceManager sm = 
					(org.openspcoop2.core.controllo_traffico.dao.IServiceManager) DAOFactory.getInstance(this.log).
					getServiceManager(org.openspcoop2.core.controllo_traffico.utils.ProjectInfo.getInstance(),
							cr.connectionDB,this.log);
			
			IConfigurazionePolicyServiceSearch search =  sm.getConfigurazionePolicyServiceSearch();
			IdPolicy policyId = new IdPolicy();
			policyId.setNome(id);
			return search.get(policyId);

		}
		catch(NotFoundException e) {
			String errorMsg = "ConfigurazionePolicy del Controllo del Traffico non trovata: "+e.getMessage();
			this.log.error(errorMsg,e);
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

class ConnectionResource{
	
	Connection connectionDB = null;
	boolean connectionPdD = false;
	
}
