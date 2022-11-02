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
import org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.constants.CostantiDB;


/**     
 * AccordoServizioParteSpecificaFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteSpecificaFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			GenericJDBCParameterUtilities jdbcParameterUtilities =  
					new GenericJDBCParameterUtilities(tipoDatabase);

			if(model.equals(AccordoServizioParteSpecifica.model())){
				AccordoServizioParteSpecifica object = new AccordoServizioParteSpecifica();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setTipo", AccordoServizioParteSpecifica.model().TIPO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_servizio", AccordoServizioParteSpecifica.model().TIPO.getFieldType()));
				setParameter(object, "setNome", AccordoServizioParteSpecifica.model().NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_servizio", AccordoServizioParteSpecifica.model().NOME.getFieldType()));
				setParameter(object, "setVersione", AccordoServizioParteSpecifica.model().VERSIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "versione_servizio", AccordoServizioParteSpecifica.model().VERSIONE.getFieldType()));
				setParameter(object, "setPortType", AccordoServizioParteSpecifica.model().PORT_TYPE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "port_type", AccordoServizioParteSpecifica.model().PORT_TYPE.getFieldType()));
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

			if(model.equals(AccordoServizioParteSpecifica.model())){
				AccordoServizioParteSpecifica object = new AccordoServizioParteSpecifica();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setTipo", AccordoServizioParteSpecifica.model().TIPO.getFieldType(),
					this.getObjectFromMap(map,"tipo"));
				setParameter(object, "setNome", AccordoServizioParteSpecifica.model().NOME.getFieldType(),
					this.getObjectFromMap(map,"nome"));
				setParameter(object, "setVersione", AccordoServizioParteSpecifica.model().VERSIONE.getFieldType(),
					this.getObjectFromMap(map,"versione"));
				setParameter(object, "setPortType", AccordoServizioParteSpecifica.model().PORT_TYPE.getFieldType(),
					this.getObjectFromMap(map,"port-type"));
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

			if(model.equals(AccordoServizioParteSpecifica.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject(CostantiDB.SERVIZI,"id","seq_"+CostantiDB.SERVIZI,CostantiDB.SERVIZI+"_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
