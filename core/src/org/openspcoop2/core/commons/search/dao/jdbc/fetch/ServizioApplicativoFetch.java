/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.commons.search.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.commons.search.ServizioApplicativo;
import org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo;
import org.openspcoop2.core.commons.search.ServizioApplicativoRuolo;
import org.openspcoop2.core.constants.CostantiDB;


/**     
 * ServizioApplicativoFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServizioApplicativoFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(ServizioApplicativo.model())){
				ServizioApplicativo object = new ServizioApplicativo();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", ServizioApplicativo.model().NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", ServizioApplicativo.model().NOME.getFieldType()));
				setParameter(object, "setTipologiaFruizione", ServizioApplicativo.model().TIPOLOGIA_FRUIZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipologia_fruizione", ServizioApplicativo.model().TIPOLOGIA_FRUIZIONE.getFieldType()));
				setParameter(object, "setTipologiaErogazione", ServizioApplicativo.model().TIPOLOGIA_EROGAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipologia_erogazione", ServizioApplicativo.model().TIPOLOGIA_EROGAZIONE.getFieldType()));
				setParameter(object, "setTipo", ServizioApplicativo.model().TIPO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo", ServizioApplicativo.model().TIPO.getFieldType()));
				setParameter(object, "setAsClient", ServizioApplicativo.model().AS_CLIENT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "as_client", ServizioApplicativo.model().AS_CLIENT.getFieldType()));
				setParameter(object, "setTipoauth", ServizioApplicativo.model().TIPOAUTH.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipoauth", ServizioApplicativo.model().TIPOAUTH.getFieldType()));
				setParameter(object, "setUtente", ServizioApplicativo.model().UTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "utente", ServizioApplicativo.model().UTENTE.getFieldType()));
				setParameter(object, "setTokenPolicy", ServizioApplicativo.model().TOKEN_POLICY.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_policy", ServizioApplicativo.model().TOKEN_POLICY.getFieldType()));
				return object;
			}
			if(model.equals(ServizioApplicativo.model().SERVIZIO_APPLICATIVO_RUOLO)){
				ServizioApplicativoRuolo object = new ServizioApplicativoRuolo();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_RUOLO.NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "ruolo", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_RUOLO.NOME.getFieldType()));
				return object;
			}
			if(model.equals(ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO)){
				ServizioApplicativoProprietaProtocollo object = new ServizioApplicativoProprietaProtocollo();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setName", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO.NAME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "name", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO.NAME.getFieldType()));
				setParameter(object, "setValueString", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO.VALUE_STRING.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "value_string", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO.VALUE_STRING.getFieldType()));
				setParameter(object, "setValueNumber", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO.VALUE_NUMBER.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "value_number", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO.VALUE_NUMBER.getFieldType()));
				setParameter(object, "setValueBoolean", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO.VALUE_BOOLEAN.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "value_boolean", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO.VALUE_BOOLEAN.getFieldType()));
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

			if(model.equals(ServizioApplicativo.model())){
				ServizioApplicativo object = new ServizioApplicativo();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setNome", ServizioApplicativo.model().NOME.getFieldType(),
					this.getObjectFromMap(map,"nome"));
				setParameter(object, "setTipologiaFruizione", ServizioApplicativo.model().TIPOLOGIA_FRUIZIONE.getFieldType(),
					this.getObjectFromMap(map,"tipologia_fruizione"));
				setParameter(object, "setTipologiaErogazione", ServizioApplicativo.model().TIPOLOGIA_EROGAZIONE.getFieldType(),
					this.getObjectFromMap(map,"tipologia_erogazione"));
				setParameter(object, "setTipo", ServizioApplicativo.model().TIPO.getFieldType(),
					this.getObjectFromMap(map,"tipo"));
				setParameter(object, "setAsClient", ServizioApplicativo.model().AS_CLIENT.getFieldType(),
					this.getObjectFromMap(map,"as_client"));
				setParameter(object, "setTipoauth", ServizioApplicativo.model().TIPOAUTH.getFieldType(),
					this.getObjectFromMap(map,"tipoauth"));
				setParameter(object, "setUtente", ServizioApplicativo.model().UTENTE.getFieldType(),
					this.getObjectFromMap(map,"utente"));
				setParameter(object, "setTokenPolicy", ServizioApplicativo.model().TOKEN_POLICY.getFieldType(),
					this.getObjectFromMap(map,"token_policy"));
				return object;
			}
			if(model.equals(ServizioApplicativo.model().SERVIZIO_APPLICATIVO_RUOLO)){
				ServizioApplicativoRuolo object = new ServizioApplicativoRuolo();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"servizio-applicativo-ruolo.id"));
				setParameter(object, "setNome", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_RUOLO.NOME.getFieldType(),
					this.getObjectFromMap(map,"servizio-applicativo-ruolo.nome"));
				return object;
			}
			if(model.equals(ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO)){
				ServizioApplicativoProprietaProtocollo object = new ServizioApplicativoProprietaProtocollo();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"servizio-applicativo-proprieta-protocollo.id"));
				setParameter(object, "setName", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO.NAME.getFieldType(),
					this.getObjectFromMap(map,"servizio-applicativo-proprieta-protocollo.name"));
				setParameter(object, "setValueString", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO.VALUE_STRING.getFieldType(),
					this.getObjectFromMap(map,"servizio-applicativo-proprieta-protocollo.value_string"));
				setParameter(object, "setValueNumber", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO.VALUE_NUMBER.getFieldType(),
					this.getObjectFromMap(map,"servizio-applicativo-proprieta-protocollo.value_number"));
				setParameter(object, "setValueBoolean", ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO.VALUE_BOOLEAN.getFieldType(),
					this.getObjectFromMap(map,"servizio-applicativo-proprieta-protocollo.value_boolean"));
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

			if(model.equals(ServizioApplicativo.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject(CostantiDB.SERVIZI_APPLICATIVI,"id","seq_"+CostantiDB.SERVIZI_APPLICATIVI,CostantiDB.SERVIZI_APPLICATIVI+"_init_seq");
			}
			if(model.equals(ServizioApplicativo.model().SERVIZIO_APPLICATIVO_RUOLO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject(CostantiDB.SERVIZI_APPLICATIVI_RUOLI,"id","seq_"+CostantiDB.SERVIZI_APPLICATIVI_RUOLI,CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"_init_seq");
			}
			if(model.equals(ServizioApplicativo.model().SERVIZIO_APPLICATIVO_PROPRIETA_PROTOCOLLO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject(CostantiDB.PROTOCOL_PROPERTIES,"id","seq_"+CostantiDB.PROTOCOL_PROPERTIES,CostantiDB.PROTOCOL_PROPERTIES+"_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
