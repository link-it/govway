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
package org.openspcoop2.core.commons.search.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.PortaApplicativaServizioApplicativo;


/**     
 * PortaApplicativaFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativaFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(PortaApplicativa.model())){
				PortaApplicativa object = new PortaApplicativa();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", PortaApplicativa.model().NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_porta", PortaApplicativa.model().NOME.getFieldType()));
				setParameter(object, "setStato", PortaApplicativa.model().STATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "stato", PortaApplicativa.model().STATO.getFieldType()));
				setParameter(object, "setTipoServizio", PortaApplicativa.model().TIPO_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_servizio", PortaApplicativa.model().TIPO_SERVIZIO.getFieldType()));
				setParameter(object, "setNomeServizio", PortaApplicativa.model().NOME_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "servizio", PortaApplicativa.model().NOME_SERVIZIO.getFieldType()));
				setParameter(object, "setVersioneServizio", PortaApplicativa.model().VERSIONE_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "versione_servizio", PortaApplicativa.model().VERSIONE_SERVIZIO.getFieldType()));
				setParameter(object, "setNomeAzione", PortaApplicativa.model().NOME_AZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "azione", PortaApplicativa.model().NOME_AZIONE.getFieldType()));
				return object;
			}
			if(model.equals(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO)){
				PortaApplicativaServizioApplicativo object = new PortaApplicativaServizioApplicativo();
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

			if(model.equals(PortaApplicativa.model())){
				PortaApplicativa object = new PortaApplicativa();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setNome", PortaApplicativa.model().NOME.getFieldType(),
					this.getObjectFromMap(map,"nome"));
				setParameter(object, "setStato", PortaApplicativa.model().STATO.getFieldType(),
					this.getObjectFromMap(map,"stato"));
				setParameter(object, "setTipoServizio", PortaApplicativa.model().TIPO_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"tipo_servizio"));
				setParameter(object, "setNomeServizio", PortaApplicativa.model().NOME_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"nome_servizio"));
				setParameter(object, "setVersioneServizio", PortaApplicativa.model().VERSIONE_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"versione_servizio"));
				setParameter(object, "setNomeAzione", PortaApplicativa.model().NOME_AZIONE.getFieldType(),
					this.getObjectFromMap(map,"nome_azione"));
				return object;
			}
			if(model.equals(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO)){
				PortaApplicativaServizioApplicativo object = new PortaApplicativaServizioApplicativo();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"porta-applicativa-servizio-applicativo.id"));
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

			if(model.equals(PortaApplicativa.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("porte_applicative","id","seq_porte_applicative","porte_applicative_init_seq");
			}
			if(model.equals(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("porte_applicative_sa","id","seq_porte_applicative_sa","porte_applicative_sa_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
