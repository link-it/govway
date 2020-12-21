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
package org.openspcoop2.core.allarmi.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.constants.CostantiDB;


/**     
 * AllarmeHistoryFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmeHistoryFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(AllarmeHistory.model())){
				AllarmeHistory object = new AllarmeHistory();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setEnabled", AllarmeHistory.model().ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "enabled", AllarmeHistory.model().ENABLED.getFieldType()));
				setParameter(object, "setStato", AllarmeHistory.model().STATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "stato", AllarmeHistory.model().STATO.getFieldType()));
				setParameter(object, "setDettaglioStato", AllarmeHistory.model().DETTAGLIO_STATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "stato_dettaglio", AllarmeHistory.model().DETTAGLIO_STATO.getFieldType()));
				setParameter(object, "setAcknowledged", AllarmeHistory.model().ACKNOWLEDGED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "acknowledged", AllarmeHistory.model().ACKNOWLEDGED.getFieldType()));
				setParameter(object, "setTimestampUpdate", AllarmeHistory.model().TIMESTAMP_UPDATE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "timestamp_update", AllarmeHistory.model().TIMESTAMP_UPDATE.getFieldType()));
				setParameter(object, "setUtente", AllarmeHistory.model().UTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "utente", AllarmeHistory.model().UTENTE.getFieldType()));
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

			if(model.equals(AllarmeHistory.model())){
				AllarmeHistory object = new AllarmeHistory();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setEnabled", AllarmeHistory.model().ENABLED.getFieldType(),
					this.getObjectFromMap(map,"enabled"));
				setParameter(object, "setStato", AllarmeHistory.model().STATO.getFieldType(),
					this.getObjectFromMap(map,"stato"));
				setParameter(object, "setDettaglioStato", AllarmeHistory.model().DETTAGLIO_STATO.getFieldType(),
					this.getObjectFromMap(map,"dettaglio-stato"));
				setParameter(object, "setAcknowledged", AllarmeHistory.model().ACKNOWLEDGED.getFieldType(),
					this.getObjectFromMap(map,"acknowledged"));
				setParameter(object, "setTimestampUpdate", AllarmeHistory.model().TIMESTAMP_UPDATE.getFieldType(),
					this.getObjectFromMap(map,"timestamp-update"));
				setParameter(object, "setUtente", AllarmeHistory.model().UTENTE.getFieldType(),
					this.getObjectFromMap(map,"utente"));
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

			if(model.equals(AllarmeHistory.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject(CostantiDB.ALLARMI_HISTORY,"id","seq_allarmi_history","allarmi_history_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
