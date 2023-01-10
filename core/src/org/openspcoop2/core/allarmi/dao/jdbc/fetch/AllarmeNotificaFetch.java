/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.allarmi.AllarmeNotifica;
import org.openspcoop2.core.constants.CostantiDB;


/**     
 * AllarmeNotificaFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmeNotificaFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			GenericJDBCParameterUtilities jdbcParameterUtilities =  
					new GenericJDBCParameterUtilities(tipoDatabase);

			if(model.equals(AllarmeNotifica.model())){
				AllarmeNotifica object = new AllarmeNotifica();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setDataNotifica", AllarmeNotifica.model().DATA_NOTIFICA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_notifica", AllarmeNotifica.model().DATA_NOTIFICA.getFieldType()));
				setParameter(object, "setOldStato", AllarmeNotifica.model().OLD_STATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "old_stato", AllarmeNotifica.model().OLD_STATO.getFieldType()));
				setParameter(object, "setOldDettaglioStato", AllarmeNotifica.model().OLD_DETTAGLIO_STATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "old_stato_dettaglio", AllarmeNotifica.model().OLD_DETTAGLIO_STATO.getFieldType()));
				setParameter(object, "setNuovoStato", AllarmeNotifica.model().NUOVO_STATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nuovo_stato", AllarmeNotifica.model().NUOVO_STATO.getFieldType()));
				setParameter(object, "setNuovoDettaglioStato", AllarmeNotifica.model().NUOVO_DETTAGLIO_STATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nuovo_stato_dettaglio", AllarmeNotifica.model().NUOVO_DETTAGLIO_STATO.getFieldType()));
				setParameter(object, "setHistoryEntry", AllarmeNotifica.model().HISTORY_ENTRY.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "history_entry", AllarmeNotifica.model().HISTORY_ENTRY.getFieldType()));
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

			if(model.equals(AllarmeNotifica.model())){
				AllarmeNotifica object = new AllarmeNotifica();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setDataNotifica", AllarmeNotifica.model().DATA_NOTIFICA.getFieldType(),
					this.getObjectFromMap(map,"data-notifica"));
				setParameter(object, "setOldStato", AllarmeNotifica.model().OLD_STATO.getFieldType(),
					this.getObjectFromMap(map,"old-stato"));
				setParameter(object, "setOldDettaglioStato", AllarmeNotifica.model().OLD_DETTAGLIO_STATO.getFieldType(),
					this.getObjectFromMap(map,"old-dettaglio-stato"));
				setParameter(object, "setNuovoStato", AllarmeNotifica.model().NUOVO_STATO.getFieldType(),
					this.getObjectFromMap(map,"nuovo-stato"));
				setParameter(object, "setNuovoDettaglioStato", AllarmeNotifica.model().NUOVO_DETTAGLIO_STATO.getFieldType(),
					this.getObjectFromMap(map,"nuovo-dettaglio-stato"));
				setParameter(object, "setHistoryEntry", AllarmeNotifica.model().HISTORY_ENTRY.getFieldType(),
					this.getObjectFromMap(map,"history-entry"));
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

			if(model.equals(AllarmeNotifica.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject(CostantiDB.ALLARMI_NOTIFICHE,"id","seq_"+CostantiDB.ALLARMI_NOTIFICHE,CostantiDB.ALLARMI_NOTIFICHE+"_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
