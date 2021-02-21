/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.plugins.dao.jdbc;

import org.openspcoop2.core.plugins.ConfigurazioneFiltro;
import org.openspcoop2.core.plugins.ConfigurazioneServizioAzione;
import org.openspcoop2.core.plugins.IdConfigurazioneFiltro;
import org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione;

import java.sql.Connection;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;

/**     
 * JDBCConfigurazioneServizioAzioneBaseLib
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCConfigurazioneServizioAzioneBaseLib {

	
	public static Long getIdConfigurazioneServizioAzione(Connection connection, JDBCServiceManagerProperties jdbcProperties, Logger log,
			String azione,String accordo, String tipoSoggettoReferente, String nomeSoggettoReferente,
			Integer versioneServizio, String servizio, boolean throwNotFound) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException, NotFoundException, MultipleResultException{
		
		JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(connection, jdbcProperties, log);
		
		IExpression expressionSearchConfigurazioneServizioAzione = jdbcServiceManager.getConfigurazioneServizioAzioneServiceSearch().newExpression();
		expressionSearchConfigurazioneServizioAzione.
			and().
			equals(ConfigurazioneServizioAzione.model().AZIONE,azione).
			equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.ACCORDO, accordo).
			equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE, tipoSoggettoReferente).
			equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE, nomeSoggettoReferente).
			equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.VERSIONE, versioneServizio).
			equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO, servizio);
		
		Long id_configurazioneServizioAzione = null;
		try{
			ConfigurazioneServizioAzione configurazioneServizioAzione = jdbcServiceManager.getConfigurazioneServizioAzioneServiceSearch().find(expressionSearchConfigurazioneServizioAzione);
			id_configurazioneServizioAzione = configurazioneServizioAzione.getId();
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		
		return id_configurazioneServizioAzione;
	}
	
	public static IdConfigurazioneServizioAzione getIdConfigurazioneServizioAzione(Connection connection, JDBCServiceManagerProperties jdbcProperties, Logger log, Long idConfigurazioneServizioAzioneLong) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException{
		
		JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(connection, jdbcProperties, log);
		
		ConfigurazioneServizioAzione configuraServizioAzione = 
				((org.openspcoop2.core.plugins.dao.IDBConfigurazioneServizioAzioneServiceSearch)jdbcServiceManager.getConfigurazioneServizioAzioneServiceSearch()).get(idConfigurazioneServizioAzioneLong);
		org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione idConfigurazioneServizioAzione = new IdConfigurazioneServizioAzione();
		idConfigurazioneServizioAzione.setAzione(configuraServizioAzione.getAzione());
		idConfigurazioneServizioAzione.setIdConfigurazioneServizio(configuraServizioAzione.getIdConfigurazioneServizio());
		return idConfigurazioneServizioAzione;
	}
	
	
	
	
	public static Long getIdConfigurazioneFiltro(Connection connection, JDBCServiceManagerProperties jdbcProperties, Logger log,
			String nome, boolean throwNotFound) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException, NotFoundException, MultipleResultException{
		
		JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(connection, jdbcProperties, log);
		
		IExpression expressionSearchConfigurazioneFiltro = jdbcServiceManager.getConfigurazioneFiltroServiceSearch().newExpression();
		expressionSearchConfigurazioneFiltro.
			and().
			equals(ConfigurazioneFiltro.model().NOME,nome);
		
		Long id_configurazioneFiltro = null;
		try{
			ConfigurazioneFiltro configurazioneFiltro = jdbcServiceManager.getConfigurazioneFiltroServiceSearch().find(expressionSearchConfigurazioneFiltro);
			id_configurazioneFiltro = configurazioneFiltro.getId();
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		
		return id_configurazioneFiltro;
	}
	
	public static IdConfigurazioneFiltro getIdConfigurazioneFiltro(Connection connection, JDBCServiceManagerProperties jdbcProperties, Logger log, Long idConfigurazioneFiltro) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException{
		
		JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(connection, jdbcProperties, log);
		
		ConfigurazioneFiltro configuraFiltro = 
				((org.openspcoop2.core.plugins.dao.IDBConfigurazioneFiltroServiceSearch)jdbcServiceManager.getConfigurazioneFiltroServiceSearch()).get(idConfigurazioneFiltro);
		org.openspcoop2.core.plugins.IdConfigurazioneFiltro idConfigurazioneFiltroAsObject = new IdConfigurazioneFiltro();
		idConfigurazioneFiltroAsObject.setNome(configuraFiltro.getNome());
		return idConfigurazioneFiltroAsObject;
	}
	
	public static ConfigurazioneFiltro getConfigurazioneFiltro(Connection connection, JDBCServiceManagerProperties jdbcProperties, Logger log, Long idConfigurazioneFiltro) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException{
		
		JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(connection, jdbcProperties, log);
		
		ConfigurazioneFiltro configuraFiltro = 
				((org.openspcoop2.core.plugins.dao.IDBConfigurazioneFiltroServiceSearch)jdbcServiceManager.getConfigurazioneFiltroServiceSearch()).get(idConfigurazioneFiltro);
		return configuraFiltro;
	}
	
}
