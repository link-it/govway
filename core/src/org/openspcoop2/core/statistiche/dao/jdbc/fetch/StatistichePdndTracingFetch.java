/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.statistiche.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.statistiche.StatistichePdndTracing;


/**     
 * StatistichePdndTracingFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatistichePdndTracingFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			GenericJDBCParameterUtilities jdbcParameterUtilities =  
					new GenericJDBCParameterUtilities(tipoDatabase);

			if(model.equals(StatistichePdndTracing.model())){
				StatistichePdndTracing object = new StatistichePdndTracing();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setDataTracciamento", StatistichePdndTracing.model().DATA_TRACCIAMENTO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_tracciamento", StatistichePdndTracing.model().DATA_TRACCIAMENTO.getFieldType()));
				setParameter(object, "setDataRegistrazione", StatistichePdndTracing.model().DATA_REGISTRAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_registrazione", StatistichePdndTracing.model().DATA_REGISTRAZIONE.getFieldType()));
				setParameter(object, "setDataPubblicazione", StatistichePdndTracing.model().DATA_PUBBLICAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_pubblicazione", StatistichePdndTracing.model().DATA_PUBBLICAZIONE.getFieldType()));
				setParameter(object, "setPddCodice", StatistichePdndTracing.model().PDD_CODICE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "pdd_codice", StatistichePdndTracing.model().PDD_CODICE.getFieldType()));
				/** Fix out of memory: #1720 
				setParameter(object, "setCsv", StatistichePdndTracing.model().CSV.getFieldType(),
				        jdbcParameterUtilities.readParameter(rs, "csv", StatistichePdndTracing.model().CSV.getFieldType()));*/
				setParameter(object, "setMethodRawEnumValue", String.class,
					jdbcParameterUtilities.readParameter(rs, "method", StatistichePdndTracing.model().METHOD.getFieldType())+"");
				setParameter(object, "setStatoPdndRawEnumValue", String.class,
					jdbcParameterUtilities.readParameter(rs, "stato_pdnd", StatistichePdndTracing.model().STATO_PDND.getFieldType())+"");
				setParameter(object, "setTentativiPubblicazione", StatistichePdndTracing.model().TENTATIVI_PUBBLICAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tentativi_pubblicazione", StatistichePdndTracing.model().TENTATIVI_PUBBLICAZIONE.getFieldType()));
				setParameter(object, "setForcePublish", StatistichePdndTracing.model().FORCE_PUBLISH.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "force_publish", StatistichePdndTracing.model().FORCE_PUBLISH.getFieldType()));
				setParameter(object, "setStatoRawEnumValue", String.class,
					jdbcParameterUtilities.readParameter(rs, "stato", StatistichePdndTracing.model().STATO.getFieldType())+"");
				setParameter(object, "setTracingId", StatistichePdndTracing.model().TRACING_ID.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tracing_id", StatistichePdndTracing.model().TRACING_ID.getFieldType()));
				setParameter(object, "setErrorDetails", StatistichePdndTracing.model().ERROR_DETAILS.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "error_details", StatistichePdndTracing.model().ERROR_DETAILS.getFieldType()));
				setParameter(object, "setHistory", StatistichePdndTracing.model().HISTORY.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "history", StatistichePdndTracing.model().HISTORY.getFieldType()));
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

			if(model.equals(StatistichePdndTracing.model())){
				StatistichePdndTracing object = new StatistichePdndTracing();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setDataTracciamento", StatistichePdndTracing.model().DATA_TRACCIAMENTO.getFieldType(),
					this.getObjectFromMap(map,"data-tracciamento"));
				setParameter(object, "setDataRegistrazione", StatistichePdndTracing.model().DATA_REGISTRAZIONE.getFieldType(),
					this.getObjectFromMap(map,"data-registrazione"));
				setParameter(object, "setDataPubblicazione", StatistichePdndTracing.model().DATA_PUBBLICAZIONE.getFieldType(),
					this.getObjectFromMap(map,"data-pubblicazione"));
				setParameter(object, "setPddCodice", StatistichePdndTracing.model().PDD_CODICE.getFieldType(),
					this.getObjectFromMap(map,"pdd-codice"));
				/** Fix out of memory: #1720 
				setParameter(object, "setCsv", StatistichePdndTracing.model().CSV.getFieldType(),
				        this.getObjectFromMap(map,"csv"));*/
				setParameter(object, "setMethodRawEnumValue", String.class,
					this.getObjectFromMap(map,"method"));
				setParameter(object, "setStatoPdndRawEnumValue", String.class,
					this.getObjectFromMap(map,"stato-pdnd"));
				setParameter(object, "setTentativiPubblicazione", StatistichePdndTracing.model().TENTATIVI_PUBBLICAZIONE.getFieldType(),
					this.getObjectFromMap(map,"tentativi-pubblicazione"));
				setParameter(object, "setForcePublish", StatistichePdndTracing.model().FORCE_PUBLISH.getFieldType(),
					this.getObjectFromMap(map,"force-publish"));
				setParameter(object, "setStatoRawEnumValue", String.class,
					this.getObjectFromMap(map,"stato"));
				setParameter(object, "setTracingId", StatistichePdndTracing.model().TRACING_ID.getFieldType(),
					this.getObjectFromMap(map,"tracing-id"));
				setParameter(object, "setErrorDetails", StatistichePdndTracing.model().ERROR_DETAILS.getFieldType(),
					this.getObjectFromMap(map,"error-details"));
				setParameter(object, "setHistory", StatistichePdndTracing.model().HISTORY.getFieldType(),
					this.getObjectFromMap(map,"history"));
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

			if(model.equals(StatistichePdndTracing.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject(CostantiDB.STATISTICHE_PDND_TRACING,"id","seq_"+CostantiDB.STATISTICHE_PDND_TRACING,CostantiDB.STATISTICHE_PDND_TRACING+"_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
