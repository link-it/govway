/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.monitor.engine.config.statistiche.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;


/**     
 * ConfigurazioneStatisticaFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneStatisticaFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(ConfigurazioneStatistica.model())){
				ConfigurazioneStatistica object = new ConfigurazioneStatistica();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "pid", Long.class));
				setParameter(object, "setIdConfigurazioneStatistica", ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_STATISTICA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id", ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_STATISTICA.getFieldType()));
				setParameter(object, "setEnabled", ConfigurazioneStatistica.model().ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "enabled", ConfigurazioneStatistica.model().ENABLED.getFieldType()));
				setParameter(object, "setLabel", ConfigurazioneStatistica.model().LABEL.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "label", ConfigurazioneStatistica.model().LABEL.getFieldType()));
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

			if(model.equals(ConfigurazioneStatistica.model())){
				ConfigurazioneStatistica object = new ConfigurazioneStatistica();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setIdConfigurazioneStatistica", ConfigurazioneStatistica.model().ID_CONFIGURAZIONE_STATISTICA.getFieldType(),
					this.getObjectFromMap(map,"id-configurazione-statistica"));
				setParameter(object, "setEnabled", ConfigurazioneStatistica.model().ENABLED.getFieldType(),
					this.getObjectFromMap(map,"enabled"));
				setParameter(object, "setLabel", ConfigurazioneStatistica.model().LABEL.getFieldType(),
					this.getObjectFromMap(map,"label"));
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

			if(model.equals(ConfigurazioneStatistica.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("stat_personalizzate","pid","seq_stat_personalizzate","stat_personalizzate_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
