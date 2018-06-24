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
package org.openspcoop2.core.transazioni.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.transazioni.TransazioneExport;


/**     
 * TransazioneExportFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneExportFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(TransazioneExport.model())){
				TransazioneExport object = new TransazioneExport();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setIntervalloInizio", TransazioneExport.model().INTERVALLO_INIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "intervallo_inizio", TransazioneExport.model().INTERVALLO_INIZIO.getFieldType()));
				setParameter(object, "setIntervalloFine", TransazioneExport.model().INTERVALLO_FINE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "intervallo_fine", TransazioneExport.model().INTERVALLO_FINE.getFieldType()));
				setParameter(object, "setNome", TransazioneExport.model().NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", TransazioneExport.model().NOME.getFieldType()));
				setParameter(object, "set_value_exportState", String.class,
					jdbcParameterUtilities.readParameter(rs, "export_state", TransazioneExport.model().EXPORT_STATE.getFieldType())+"");
				setParameter(object, "setExportError", TransazioneExport.model().EXPORT_ERROR.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "export_error", TransazioneExport.model().EXPORT_ERROR.getFieldType()));
				setParameter(object, "setExportTimeStart", TransazioneExport.model().EXPORT_TIME_START.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "export_time_start", TransazioneExport.model().EXPORT_TIME_START.getFieldType()));
				setParameter(object, "setExportTimeEnd", TransazioneExport.model().EXPORT_TIME_END.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "export_time_end", TransazioneExport.model().EXPORT_TIME_END.getFieldType()));
				setParameter(object, "set_value_deleteState", String.class,
					jdbcParameterUtilities.readParameter(rs, "delete_state", TransazioneExport.model().DELETE_STATE.getFieldType())+"");
				setParameter(object, "setDeleteError", TransazioneExport.model().DELETE_ERROR.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "delete_error", TransazioneExport.model().DELETE_ERROR.getFieldType()));
				setParameter(object, "setDeleteTimeStart", TransazioneExport.model().DELETE_TIME_START.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "delete_time_start", TransazioneExport.model().DELETE_TIME_START.getFieldType()));
				setParameter(object, "setDeleteTimeEnd", TransazioneExport.model().DELETE_TIME_END.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "delete_time_end", TransazioneExport.model().DELETE_TIME_END.getFieldType()));
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

			if(model.equals(TransazioneExport.model())){
				TransazioneExport object = new TransazioneExport();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setIntervalloInizio", TransazioneExport.model().INTERVALLO_INIZIO.getFieldType(),
					this.getObjectFromMap(map,"intervallo-inizio"));
				setParameter(object, "setIntervalloFine", TransazioneExport.model().INTERVALLO_FINE.getFieldType(),
					this.getObjectFromMap(map,"intervallo-fine"));
				setParameter(object, "setNome", TransazioneExport.model().NOME.getFieldType(),
					this.getObjectFromMap(map,"nome"));
				setParameter(object, "set_value_exportState", String.class,
					this.getObjectFromMap(map,"export-state"));
				setParameter(object, "setExportError", TransazioneExport.model().EXPORT_ERROR.getFieldType(),
					this.getObjectFromMap(map,"export-error"));
				setParameter(object, "setExportTimeStart", TransazioneExport.model().EXPORT_TIME_START.getFieldType(),
					this.getObjectFromMap(map,"export-time-start"));
				setParameter(object, "setExportTimeEnd", TransazioneExport.model().EXPORT_TIME_END.getFieldType(),
					this.getObjectFromMap(map,"export-time-end"));
				setParameter(object, "set_value_deleteState", String.class,
					this.getObjectFromMap(map,"delete-state"));
				setParameter(object, "setDeleteError", TransazioneExport.model().DELETE_ERROR.getFieldType(),
					this.getObjectFromMap(map,"delete-error"));
				setParameter(object, "setDeleteTimeStart", TransazioneExport.model().DELETE_TIME_START.getFieldType(),
					this.getObjectFromMap(map,"delete-time-start"));
				setParameter(object, "setDeleteTimeEnd", TransazioneExport.model().DELETE_TIME_END.getFieldType(),
					this.getObjectFromMap(map,"delete-time-end"));
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

			if(model.equals(TransazioneExport.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("transazioni_export","id","seq_transazioni_export","transazioni_export_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
