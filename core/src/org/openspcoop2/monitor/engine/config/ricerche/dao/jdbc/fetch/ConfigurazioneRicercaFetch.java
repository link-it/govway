/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.config.ricerche.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;


/**     
 * ConfigurazioneRicercaFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneRicercaFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			GenericJDBCParameterUtilities jdbcParameterUtilities =  
					new GenericJDBCParameterUtilities(tipoDatabase);

			if(model.equals(ConfigurazioneRicerca.model())){
				ConfigurazioneRicerca object = new ConfigurazioneRicerca();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "pid", Long.class));
				setParameter(object, "setIdConfigurazioneRicerca", ConfigurazioneRicerca.model().ID_CONFIGURAZIONE_RICERCA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id", ConfigurazioneRicerca.model().ID_CONFIGURAZIONE_RICERCA.getFieldType()));
				setParameter(object, "setEnabled", ConfigurazioneRicerca.model().ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "enabled", ConfigurazioneRicerca.model().ENABLED.getFieldType()));
				setParameter(object, "setLabel", ConfigurazioneRicerca.model().LABEL.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "label", ConfigurazioneRicerca.model().LABEL.getFieldType()));
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

			if(model.equals(ConfigurazioneRicerca.model())){
				ConfigurazioneRicerca object = new ConfigurazioneRicerca();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setIdConfigurazioneRicerca", ConfigurazioneRicerca.model().ID_CONFIGURAZIONE_RICERCA.getFieldType(),
					this.getObjectFromMap(map,"id-configurazione-ricerca"));
				setParameter(object, "setEnabled", ConfigurazioneRicerca.model().ENABLED.getFieldType(),
					this.getObjectFromMap(map,"enabled"));
				setParameter(object, "setLabel", ConfigurazioneRicerca.model().LABEL.getFieldType(),
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

			if(model.equals(ConfigurazioneRicerca.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("ricerche_personalizzate","pid","seq_ricerche_personalizzate","ricerche_personalizzate_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
