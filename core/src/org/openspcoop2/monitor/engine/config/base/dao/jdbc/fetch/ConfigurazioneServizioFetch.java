/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.monitor.engine.config.base.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio;


/**     
 * ConfigurazioneServizioFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneServizioFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(ConfigurazioneServizio.model())){
				ConfigurazioneServizio object = new ConfigurazioneServizio();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setAccordo", ConfigurazioneServizio.model().ACCORDO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "accordo", ConfigurazioneServizio.model().ACCORDO.getFieldType()));
				setParameter(object, "setTipoSoggettoReferente", ConfigurazioneServizio.model().TIPO_SOGGETTO_REFERENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_soggetto_referente", ConfigurazioneServizio.model().TIPO_SOGGETTO_REFERENTE.getFieldType()));
				setParameter(object, "setNomeSoggettoReferente", ConfigurazioneServizio.model().NOME_SOGGETTO_REFERENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_soggetto_referente", ConfigurazioneServizio.model().NOME_SOGGETTO_REFERENTE.getFieldType()));
				setParameter(object, "setVersione", ConfigurazioneServizio.model().VERSIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "versione", ConfigurazioneServizio.model().VERSIONE.getFieldType()));
				setParameter(object, "setServizio", ConfigurazioneServizio.model().SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "servizio", ConfigurazioneServizio.model().SERVIZIO.getFieldType()));
				return object;
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by fetch: "+this.getClass().getName());
			}	
					
		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in fetch: "+e.getMessage(),e);
		}
		
	}
	
	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , Map<String,Object> map ) throws ServiceException {
		
		try{

			if(model.equals(ConfigurazioneServizio.model())){
				ConfigurazioneServizio object = new ConfigurazioneServizio();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setAccordo", ConfigurazioneServizio.model().ACCORDO.getFieldType(),
					this.getObjectFromMap(map,"accordo"));
				setParameter(object, "setTipoSoggettoReferente", ConfigurazioneServizio.model().TIPO_SOGGETTO_REFERENTE.getFieldType(),
					this.getObjectFromMap(map,"tipo-soggetto-referente"));
				setParameter(object, "setNomeSoggettoReferente", ConfigurazioneServizio.model().NOME_SOGGETTO_REFERENTE.getFieldType(),
					this.getObjectFromMap(map,"nome-soggetto-referente"));
				setParameter(object, "setVersione", ConfigurazioneServizio.model().VERSIONE.getFieldType(),
					this.getObjectFromMap(map,"versione"));
				setParameter(object, "setServizio", ConfigurazioneServizio.model().SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"servizio"));
				return object;
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by fetch: "+this.getClass().getName());
			}	
					
		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in fetch: "+e.getMessage(),e);
		}
		
	}
	
	
	@Override
	public IKeyGeneratorObject getKeyGeneratorObject( IModel<?> model )  throws ServiceException {
		
		try{

			if(model.equals(ConfigurazioneServizio.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("plugins_conf_servizi","id","seq_plugins_conf_servizi","plugins_conf_servizi_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
