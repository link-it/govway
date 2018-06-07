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

import org.openspcoop2.core.transazioni.DumpMultipartHeader;
import org.openspcoop2.core.transazioni.DumpHeaderAllegato;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;


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
				setParameter(object, "setProtocollo", DumpMessaggio.model().PROTOCOLLO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "protocollo", DumpMessaggio.model().PROTOCOLLO.getFieldType()));
				setParameter(object, "set_value_tipoMessaggio", String.class,
					jdbcParameterUtilities.readParameter(rs, "tipo_messaggio", DumpMessaggio.model().TIPO_MESSAGGIO.getFieldType())+"");
				setParameter(object, "setContentType", DumpMessaggio.model().CONTENT_TYPE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "content_type", DumpMessaggio.model().CONTENT_TYPE.getFieldType()));
				setParameter(object, "setMultipartContentType", DumpMessaggio.model().MULTIPART_CONTENT_TYPE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "multipart_content_type", DumpMessaggio.model().MULTIPART_CONTENT_TYPE.getFieldType()));
				setParameter(object, "setMultipartContentId", DumpMessaggio.model().MULTIPART_CONTENT_ID.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "multipart_content_id", DumpMessaggio.model().MULTIPART_CONTENT_ID.getFieldType()));
				setParameter(object, "setMultipartContentLocation", DumpMessaggio.model().MULTIPART_CONTENT_LOCATION.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "multipart_content_location", DumpMessaggio.model().MULTIPART_CONTENT_LOCATION.getFieldType()));
				setParameter(object, "setBody", DumpMessaggio.model().BODY.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "body", DumpMessaggio.model().BODY.getFieldType()));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().DUMP_TIMESTAMP.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "dump_timestamp", DumpMessaggio.model().DUMP_TIMESTAMP.getFieldType()));
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
			if(model.equals(DumpMessaggio.model().MULTIPART_HEADER)){
				DumpMultipartHeader object = new DumpMultipartHeader();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", DumpMessaggio.model().MULTIPART_HEADER.NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", DumpMessaggio.model().MULTIPART_HEADER.NOME.getFieldType()));
				setParameter(object, "setValore", DumpMessaggio.model().MULTIPART_HEADER.VALORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "valore", DumpMessaggio.model().MULTIPART_HEADER.VALORE.getFieldType()));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().MULTIPART_HEADER.DUMP_TIMESTAMP.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "dump_timestamp", DumpMessaggio.model().MULTIPART_HEADER.DUMP_TIMESTAMP.getFieldType()));
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
			if(model.equals(DumpMessaggio.model().ALLEGATO)){
				DumpAllegato object = new DumpAllegato();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setContentType", DumpMessaggio.model().ALLEGATO.CONTENT_TYPE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "content_type", DumpMessaggio.model().ALLEGATO.CONTENT_TYPE.getFieldType()));
				setParameter(object, "setContentId", DumpMessaggio.model().ALLEGATO.CONTENT_ID.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "content_id", DumpMessaggio.model().ALLEGATO.CONTENT_ID.getFieldType()));
				setParameter(object, "setContentLocation", DumpMessaggio.model().ALLEGATO.CONTENT_LOCATION.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "content_location", DumpMessaggio.model().ALLEGATO.CONTENT_LOCATION.getFieldType()));
				setParameter(object, "setAllegato", DumpMessaggio.model().ALLEGATO.ALLEGATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "allegato", DumpMessaggio.model().ALLEGATO.ALLEGATO.getFieldType()));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "dump_timestamp", DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP.getFieldType()));
				return object;
			}
			if(model.equals(DumpMessaggio.model().ALLEGATO.HEADER)){
				DumpHeaderAllegato object = new DumpHeaderAllegato();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", DumpMessaggio.model().ALLEGATO.HEADER.NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", DumpMessaggio.model().ALLEGATO.HEADER.NOME.getFieldType()));
				setParameter(object, "setValore", DumpMessaggio.model().ALLEGATO.HEADER.VALORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "valore", DumpMessaggio.model().ALLEGATO.HEADER.VALORE.getFieldType()));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().ALLEGATO.HEADER.DUMP_TIMESTAMP.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "dump_timestamp", DumpMessaggio.model().ALLEGATO.HEADER.DUMP_TIMESTAMP.getFieldType()));
				return object;
			}
			if(model.equals(DumpMessaggio.model().CONTENUTO)){
				DumpContenuto object = new DumpContenuto();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", DumpMessaggio.model().CONTENUTO.NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", DumpMessaggio.model().CONTENUTO.NOME.getFieldType()));
				setParameter(object, "setValore", DumpMessaggio.model().CONTENUTO.VALORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "valore", DumpMessaggio.model().CONTENUTO.VALORE.getFieldType()));
				setParameter(object, "setValoreAsBytes", DumpMessaggio.model().CONTENUTO.VALORE_AS_BYTES.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "valore_as_bytes", DumpMessaggio.model().CONTENUTO.VALORE_AS_BYTES.getFieldType()));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().CONTENUTO.DUMP_TIMESTAMP.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "dump_timestamp", DumpMessaggio.model().CONTENUTO.DUMP_TIMESTAMP.getFieldType()));
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
				setParameter(object, "setProtocollo", DumpMessaggio.model().PROTOCOLLO.getFieldType(),
					this.getObjectFromMap(map,"protocollo"));
				setParameter(object, "set_value_tipoMessaggio", String.class,
					this.getObjectFromMap(map,"tipo-messaggio"));
				setParameter(object, "setContentType", DumpMessaggio.model().CONTENT_TYPE.getFieldType(),
					this.getObjectFromMap(map,"content-type"));
				setParameter(object, "setMultipartContentType", DumpMessaggio.model().MULTIPART_CONTENT_TYPE.getFieldType(),
					this.getObjectFromMap(map,"multipart-content-type"));
				setParameter(object, "setMultipartContentId", DumpMessaggio.model().MULTIPART_CONTENT_ID.getFieldType(),
					this.getObjectFromMap(map,"multipart-content-id"));
				setParameter(object, "setMultipartContentLocation", DumpMessaggio.model().MULTIPART_CONTENT_LOCATION.getFieldType(),
					this.getObjectFromMap(map,"multipart-content-location"));
				setParameter(object, "setBody", DumpMessaggio.model().BODY.getFieldType(),
					this.getObjectFromMap(map,"body"));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().DUMP_TIMESTAMP.getFieldType(),
					this.getObjectFromMap(map,"dump-timestamp"));
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
			if(model.equals(DumpMessaggio.model().MULTIPART_HEADER)){
				DumpMultipartHeader object = new DumpMultipartHeader();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"multipart-header.id"));
				setParameter(object, "setNome", DumpMessaggio.model().MULTIPART_HEADER.NOME.getFieldType(),
					this.getObjectFromMap(map,"multipart-header.nome"));
				setParameter(object, "setValore", DumpMessaggio.model().MULTIPART_HEADER.VALORE.getFieldType(),
					this.getObjectFromMap(map,"multipart-header.valore"));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().MULTIPART_HEADER.DUMP_TIMESTAMP.getFieldType(),
					this.getObjectFromMap(map,"multipart-header.dump-timestamp"));
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
			if(model.equals(DumpMessaggio.model().ALLEGATO)){
				DumpAllegato object = new DumpAllegato();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"allegato.id"));
				setParameter(object, "setContentType", DumpMessaggio.model().ALLEGATO.CONTENT_TYPE.getFieldType(),
					this.getObjectFromMap(map,"allegato.content-type"));
				setParameter(object, "setContentId", DumpMessaggio.model().ALLEGATO.CONTENT_ID.getFieldType(),
					this.getObjectFromMap(map,"allegato.content-id"));
				setParameter(object, "setContentLocation", DumpMessaggio.model().ALLEGATO.CONTENT_LOCATION.getFieldType(),
					this.getObjectFromMap(map,"allegato.content-location"));
				setParameter(object, "setAllegato", DumpMessaggio.model().ALLEGATO.ALLEGATO.getFieldType(),
					this.getObjectFromMap(map,"allegato.allegato"));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP.getFieldType(),
					this.getObjectFromMap(map,"allegato.dump-timestamp"));
				return object;
			}
			if(model.equals(DumpMessaggio.model().ALLEGATO.HEADER)){
				DumpHeaderAllegato object = new DumpHeaderAllegato();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"allegato.header.id"));
				setParameter(object, "setNome", DumpMessaggio.model().ALLEGATO.HEADER.NOME.getFieldType(),
					this.getObjectFromMap(map,"allegato.header.nome"));
				setParameter(object, "setValore", DumpMessaggio.model().ALLEGATO.HEADER.VALORE.getFieldType(),
					this.getObjectFromMap(map,"allegato.header.valore"));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().ALLEGATO.HEADER.DUMP_TIMESTAMP.getFieldType(),
					this.getObjectFromMap(map,"allegato.header.dump-timestamp"));
				return object;
			}
			if(model.equals(DumpMessaggio.model().CONTENUTO)){
				DumpContenuto object = new DumpContenuto();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"contenuto.id"));
				setParameter(object, "setNome", DumpMessaggio.model().CONTENUTO.NOME.getFieldType(),
					this.getObjectFromMap(map,"contenuto.nome"));
				setParameter(object, "setValore", DumpMessaggio.model().CONTENUTO.VALORE.getFieldType(),
					this.getObjectFromMap(map,"contenuto.valore"));
				setParameter(object, "setValoreAsBytes", DumpMessaggio.model().CONTENUTO.VALORE_AS_BYTES.getFieldType(),
					this.getObjectFromMap(map,"contenuto.valore-as-bytes"));
				setParameter(object, "setDumpTimestamp", DumpMessaggio.model().CONTENUTO.DUMP_TIMESTAMP.getFieldType(),
					this.getObjectFromMap(map,"contenuto.dump-timestamp"));
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
			if(model.equals(DumpMessaggio.model().MULTIPART_HEADER)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("dump_multipart_header","id","seq_dump_multipart_header","dump_multipart_header_init_seq");
			}
			if(model.equals(DumpMessaggio.model().HEADER_TRASPORTO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("dump_header_trasporto","id","seq_dump_header_trasporto","dump_header_trasporto_init_seq");
			}
			if(model.equals(DumpMessaggio.model().ALLEGATO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("dump_allegati","id","seq_dump_allegati","dump_allegati_init_seq");
			}
			if(model.equals(DumpMessaggio.model().ALLEGATO.HEADER)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("dump_header_allegato","id","seq_dump_header_allegato","dump_header_allegato_init_seq");
			}
			if(model.equals(DumpMessaggio.model().CONTENUTO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("dump_contenuti","id","seq_dump_contenuti","dump_contenuti_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
