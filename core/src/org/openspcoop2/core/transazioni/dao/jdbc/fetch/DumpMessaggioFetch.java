/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpAllegato;


/**     
 * DumpMessaggioFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpMessaggioFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(DumpMessaggio.model())){
				DumpMessaggio object = new DumpMessaggio();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setIdTransazione", DumpMessaggio.model().ID_TRANSAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_transazione", DumpMessaggio.model().ID_TRANSAZIONE.getFieldType()));
				setParameter(object, "set_value_tipoMessaggio", String.class,
					jdbcParameterUtilities.readParameter(rs, "tipo_messaggio", DumpMessaggio.model().TIPO_MESSAGGIO.getFieldType())+"");
				setParameter(object, "setBody", DumpMessaggio.model().BODY.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "body", DumpMessaggio.model().BODY.getFieldType()));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().DUMP_TIMESTAMP.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "dump_timestamp", DumpMessaggio.model().DUMP_TIMESTAMP.getFieldType()));
				setParameter(object, "setPostProcessContentType", DumpMessaggio.model().POST_PROCESS_CONTENT_TYPE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "post_process_content_type", DumpMessaggio.model().POST_PROCESS_CONTENT_TYPE.getFieldType()));
				setParameter(object, "setPostProcessHeader", DumpMessaggio.model().POST_PROCESS_HEADER.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "post_process_header", DumpMessaggio.model().POST_PROCESS_HEADER.getFieldType()));
				setParameter(object, "setPostProcessFilename", DumpMessaggio.model().POST_PROCESS_FILENAME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "post_process_filename", DumpMessaggio.model().POST_PROCESS_FILENAME.getFieldType()));
				setParameter(object, "setPostProcessContent", DumpMessaggio.model().POST_PROCESS_CONTENT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "post_process_content", DumpMessaggio.model().POST_PROCESS_CONTENT.getFieldType()));
				setParameter(object, "setPostProcessConfigId", DumpMessaggio.model().POST_PROCESS_CONFIG_ID.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "post_process_config_id", DumpMessaggio.model().POST_PROCESS_CONFIG_ID.getFieldType()));
				setParameter(object, "setPostProcessTimestamp", DumpMessaggio.model().POST_PROCESS_TIMESTAMP.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "post_process_timestamp", DumpMessaggio.model().POST_PROCESS_TIMESTAMP.getFieldType()));
				setParameter(object, "setPostProcessed", DumpMessaggio.model().POST_PROCESSED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "post_processed", DumpMessaggio.model().POST_PROCESSED.getFieldType()));
				return object;
			}
			if(model.equals(DumpMessaggio.model().ALLEGATO)){
				DumpAllegato object = new DumpAllegato();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setIdAllegato", DumpMessaggio.model().ALLEGATO.ID_ALLEGATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_allegato", DumpMessaggio.model().ALLEGATO.ID_ALLEGATO.getFieldType()));
				setParameter(object, "setLocation", DumpMessaggio.model().ALLEGATO.LOCATION.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "location", DumpMessaggio.model().ALLEGATO.LOCATION.getFieldType()));
				setParameter(object, "setMimetype", DumpMessaggio.model().ALLEGATO.MIMETYPE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "mimetype", DumpMessaggio.model().ALLEGATO.MIMETYPE.getFieldType()));
				setParameter(object, "setAllegato", DumpMessaggio.model().ALLEGATO.ALLEGATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "allegato", DumpMessaggio.model().ALLEGATO.ALLEGATO.getFieldType()));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "dump_timestamp", DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP.getFieldType()));
				return object;
			}
			if(model.equals(DumpMessaggio.model().HEADER_TRASPORTO)){
				DumpHeaderTrasporto object = new DumpHeaderTrasporto();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", DumpMessaggio.model().HEADER_TRASPORTO.NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", DumpMessaggio.model().HEADER_TRASPORTO.NOME.getFieldType()));
				setParameter(object, "setValore", DumpMessaggio.model().HEADER_TRASPORTO.VALORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "valore", DumpMessaggio.model().HEADER_TRASPORTO.VALORE.getFieldType()));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().HEADER_TRASPORTO.DUMP_TIMESTAMP.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "dump_timestamp", DumpMessaggio.model().HEADER_TRASPORTO.DUMP_TIMESTAMP.getFieldType()));
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

			if(model.equals(DumpMessaggio.model())){
				DumpMessaggio object = new DumpMessaggio();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setIdTransazione", DumpMessaggio.model().ID_TRANSAZIONE.getFieldType(),
					this.getObjectFromMap(map,"id-transazione"));
				setParameter(object, "set_value_tipoMessaggio", String.class,
					this.getObjectFromMap(map,"tipo-messaggio"));
				setParameter(object, "setBody", DumpMessaggio.model().BODY.getFieldType(),
					this.getObjectFromMap(map,"body"));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().DUMP_TIMESTAMP.getFieldType(),
					this.getObjectFromMap(map,"dump-timestamp"));
				setParameter(object, "setPostProcessContentType", DumpMessaggio.model().POST_PROCESS_CONTENT_TYPE.getFieldType(),
					this.getObjectFromMap(map,"post-process-content-type"));
				setParameter(object, "setPostProcessHeader", DumpMessaggio.model().POST_PROCESS_HEADER.getFieldType(),
					this.getObjectFromMap(map,"post-process-header"));
				setParameter(object, "setPostProcessFilename", DumpMessaggio.model().POST_PROCESS_FILENAME.getFieldType(),
					this.getObjectFromMap(map,"post-process-filename"));
				setParameter(object, "setPostProcessContent", DumpMessaggio.model().POST_PROCESS_CONTENT.getFieldType(),
					this.getObjectFromMap(map,"post-process-content"));
				setParameter(object, "setPostProcessConfigId", DumpMessaggio.model().POST_PROCESS_CONFIG_ID.getFieldType(),
					this.getObjectFromMap(map,"post-process-config-id"));
				setParameter(object, "setPostProcessTimestamp", DumpMessaggio.model().POST_PROCESS_TIMESTAMP.getFieldType(),
					this.getObjectFromMap(map,"post-process-timestamp"));
				setParameter(object, "setPostProcessed", DumpMessaggio.model().POST_PROCESSED.getFieldType(),
					this.getObjectFromMap(map,"post-processed"));
				return object;
			}
			if(model.equals(DumpMessaggio.model().ALLEGATO)){
				DumpAllegato object = new DumpAllegato();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"allegato.id"));
				setParameter(object, "setIdAllegato", DumpMessaggio.model().ALLEGATO.ID_ALLEGATO.getFieldType(),
					this.getObjectFromMap(map,"allegato.id-allegato"));
				setParameter(object, "setLocation", DumpMessaggio.model().ALLEGATO.LOCATION.getFieldType(),
					this.getObjectFromMap(map,"allegato.location"));
				setParameter(object, "setMimetype", DumpMessaggio.model().ALLEGATO.MIMETYPE.getFieldType(),
					this.getObjectFromMap(map,"allegato.mimetype"));
				setParameter(object, "setAllegato", DumpMessaggio.model().ALLEGATO.ALLEGATO.getFieldType(),
					this.getObjectFromMap(map,"allegato.allegato"));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP.getFieldType(),
					this.getObjectFromMap(map,"allegato.dump-timestamp"));
				return object;
			}
			if(model.equals(DumpMessaggio.model().HEADER_TRASPORTO)){
				DumpHeaderTrasporto object = new DumpHeaderTrasporto();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"header-trasporto.id"));
				setParameter(object, "setNome", DumpMessaggio.model().HEADER_TRASPORTO.NOME.getFieldType(),
					this.getObjectFromMap(map,"header-trasporto.nome"));
				setParameter(object, "setValore", DumpMessaggio.model().HEADER_TRASPORTO.VALORE.getFieldType(),
					this.getObjectFromMap(map,"header-trasporto.valore"));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().HEADER_TRASPORTO.DUMP_TIMESTAMP.getFieldType(),
					this.getObjectFromMap(map,"header-trasporto.dump-timestamp"));
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

			if(model.equals(DumpMessaggio.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("dump_messaggi","id","seq_dump_messaggi","dump_messaggi_init_seq");
			}
			if(model.equals(DumpMessaggio.model().ALLEGATO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("dump_allegati","id","seq_dump_allegati","dump_allegati_init_seq");
			}
			if(model.equals(DumpMessaggio.model().HEADER_TRASPORTO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("dump_header_trasporto","id","seq_dump_header_trasporto","dump_header_trasporto_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
