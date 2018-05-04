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
package org.openspcoop2.core.eventi.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.eventi.Evento;


/**     
 * EventoFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EventoFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(Evento.model())){
				Evento object = new Evento();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setTipo", Evento.model().TIPO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo", Evento.model().TIPO.getFieldType()));
				setParameter(object, "setCodice", Evento.model().CODICE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "codice", Evento.model().CODICE.getFieldType()));
				setParameter(object, "setSeverita", Evento.model().SEVERITA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "severita", Evento.model().SEVERITA.getFieldType()));
				setParameter(object, "setOraRegistrazione", Evento.model().ORA_REGISTRAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "ora_registrazione", Evento.model().ORA_REGISTRAZIONE.getFieldType()));
				setParameter(object, "setDescrizione", Evento.model().DESCRIZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "descrizione", Evento.model().DESCRIZIONE.getFieldType()));
				setParameter(object, "setIdTransazione", Evento.model().ID_TRANSAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_transazione", Evento.model().ID_TRANSAZIONE.getFieldType()));
				setParameter(object, "setIdConfigurazione", Evento.model().ID_CONFIGURAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_configurazione", Evento.model().ID_CONFIGURAZIONE.getFieldType()));
				setParameter(object, "setConfigurazione", Evento.model().CONFIGURAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "configurazione", Evento.model().CONFIGURAZIONE.getFieldType()));
				setParameter(object, "setClusterId", Evento.model().CLUSTER_ID.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cluster_id", Evento.model().CLUSTER_ID.getFieldType()));
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

			if(model.equals(Evento.model())){
				Evento object = new Evento();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setTipo", Evento.model().TIPO.getFieldType(),
					this.getObjectFromMap(map,"tipo"));
				setParameter(object, "setCodice", Evento.model().CODICE.getFieldType(),
					this.getObjectFromMap(map,"codice"));
				setParameter(object, "setSeverita", Evento.model().SEVERITA.getFieldType(),
					this.getObjectFromMap(map,"severita"));
				setParameter(object, "setOraRegistrazione", Evento.model().ORA_REGISTRAZIONE.getFieldType(),
					this.getObjectFromMap(map,"ora-registrazione"));
				setParameter(object, "setDescrizione", Evento.model().DESCRIZIONE.getFieldType(),
					this.getObjectFromMap(map,"descrizione"));
				setParameter(object, "setIdTransazione", Evento.model().ID_TRANSAZIONE.getFieldType(),
					this.getObjectFromMap(map,"id-transazione"));
				setParameter(object, "setIdConfigurazione", Evento.model().ID_CONFIGURAZIONE.getFieldType(),
					this.getObjectFromMap(map,"id-configurazione"));
				setParameter(object, "setConfigurazione", Evento.model().CONFIGURAZIONE.getFieldType(),
					this.getObjectFromMap(map,"configurazione"));
				setParameter(object, "setClusterId", Evento.model().CLUSTER_ID.getFieldType(),
					this.getObjectFromMap(map,"cluster-id"));
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

			if(model.equals(Evento.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("notifiche_eventi","id","seq_notifiche_eventi","notifiche_eventi_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
