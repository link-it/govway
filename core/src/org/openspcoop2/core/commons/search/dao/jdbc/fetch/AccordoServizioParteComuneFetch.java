/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.Operation;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.PortType;
import org.openspcoop2.core.commons.search.Resource;


/**     
 * AccordoServizioParteComuneFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteComuneFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(AccordoServizioParteComune.model())){
				AccordoServizioParteComune object = new AccordoServizioParteComune();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", AccordoServizioParteComune.model().NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", AccordoServizioParteComune.model().NOME.getFieldType()));
				setParameter(object, "setVersione", AccordoServizioParteComune.model().VERSIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "versione", AccordoServizioParteComune.model().VERSIONE.getFieldType()));
				setParameter(object, "setServiceBinding", AccordoServizioParteComune.model().SERVICE_BINDING.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "service_binding", AccordoServizioParteComune.model().SERVICE_BINDING.getFieldType()));
				setParameter(object, "setCanale", AccordoServizioParteComune.model().CANALE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "canale", AccordoServizioParteComune.model().CANALE.getFieldType()));
				return object;
			}
			if(model.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE)){
				AccordoServizioParteComuneAzione object = new AccordoServizioParteComuneAzione();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.NOME.getFieldType()));
				return object;
			}
			if(model.equals(AccordoServizioParteComune.model().PORT_TYPE)){
				PortType object = new PortType();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", AccordoServizioParteComune.model().PORT_TYPE.NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", AccordoServizioParteComune.model().PORT_TYPE.NOME.getFieldType()));
				return object;
			}
			if(model.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION)){
				Operation object = new Operation();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", AccordoServizioParteComune.model().PORT_TYPE.OPERATION.NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", AccordoServizioParteComune.model().PORT_TYPE.OPERATION.NOME.getFieldType()));
				return object;
			}
			if(model.equals(AccordoServizioParteComune.model().RESOURCE)){
				Resource object = new Resource();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", AccordoServizioParteComune.model().RESOURCE.NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", AccordoServizioParteComune.model().RESOURCE.NOME.getFieldType()));
				setParameter(object, "setHttpMethod", AccordoServizioParteComune.model().RESOURCE.HTTP_METHOD.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "http_method", AccordoServizioParteComune.model().RESOURCE.HTTP_METHOD.getFieldType()));
				setParameter(object, "setPath", AccordoServizioParteComune.model().RESOURCE.PATH.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "path", AccordoServizioParteComune.model().RESOURCE.PATH.getFieldType()));
				return object;
			}
			if(model.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO)){
				AccordoServizioParteComuneGruppo object = new AccordoServizioParteComuneGruppo();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
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

			if(model.equals(AccordoServizioParteComune.model())){
				AccordoServizioParteComune object = new AccordoServizioParteComune();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setNome", AccordoServizioParteComune.model().NOME.getFieldType(),
					this.getObjectFromMap(map,"nome"));
				setParameter(object, "setVersione", AccordoServizioParteComune.model().VERSIONE.getFieldType(),
					this.getObjectFromMap(map,"versione"));
				setParameter(object, "setServiceBinding", AccordoServizioParteComune.model().SERVICE_BINDING.getFieldType(),
					this.getObjectFromMap(map,"service-binding"));
				setParameter(object, "setCanale", AccordoServizioParteComune.model().CANALE.getFieldType(),
					this.getObjectFromMap(map,"canale"));
				return object;
			}
			if(model.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE)){
				AccordoServizioParteComuneAzione object = new AccordoServizioParteComuneAzione();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"accordo-servizio-parte-comune-azione.id"));
				setParameter(object, "setNome", AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.NOME.getFieldType(),
					this.getObjectFromMap(map,"accordo-servizio-parte-comune-azione.nome"));
				return object;
			}
			if(model.equals(AccordoServizioParteComune.model().PORT_TYPE)){
				PortType object = new PortType();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"port-type.id"));
				setParameter(object, "setNome", AccordoServizioParteComune.model().PORT_TYPE.NOME.getFieldType(),
					this.getObjectFromMap(map,"port-type.nome"));
				return object;
			}
			if(model.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION)){
				Operation object = new Operation();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"port-type.operation.id"));
				setParameter(object, "setNome", AccordoServizioParteComune.model().PORT_TYPE.OPERATION.NOME.getFieldType(),
					this.getObjectFromMap(map,"port-type.operation.nome"));
				return object;
			}
			if(model.equals(AccordoServizioParteComune.model().RESOURCE)){
				Resource object = new Resource();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"resource.id"));
				setParameter(object, "setNome", AccordoServizioParteComune.model().RESOURCE.NOME.getFieldType(),
					this.getObjectFromMap(map,"resource.nome"));
				setParameter(object, "setHttpMethod", AccordoServizioParteComune.model().RESOURCE.HTTP_METHOD.getFieldType(),
					this.getObjectFromMap(map,"resource.http-method"));
				setParameter(object, "setPath", AccordoServizioParteComune.model().RESOURCE.PATH.getFieldType(),
					this.getObjectFromMap(map,"resource.path"));
				return object;
			}
			if(model.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO)){
				AccordoServizioParteComuneGruppo object = new AccordoServizioParteComuneGruppo();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"accordo-servizio-parte-comune-gruppo.id"));
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

			if(model.equals(AccordoServizioParteComune.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("accordi","id","seq_accordi","accordi_init_seq");
			}
			if(model.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("accordi_azioni","id","seq_accordi_azioni","accordi_azioni_init_seq");
			}
			if(model.equals(AccordoServizioParteComune.model().PORT_TYPE)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("port_type","id","seq_port_type","port_type_init_seq");
			}
			if(model.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("port_type_azioni","id","seq_port_type_azioni","port_type_azioni_init_seq");
			}
			if(model.equals(AccordoServizioParteComune.model().RESOURCE)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("api_resources","id","seq_api_resources","api_resources_init_seq");
			}
			if(model.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("accordi_gruppi","id","seq_accordi_gruppi","accordi_gruppi_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
