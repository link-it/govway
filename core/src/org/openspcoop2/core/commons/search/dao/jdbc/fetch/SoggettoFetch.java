/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.SoggettoRuolo;
import org.openspcoop2.core.constants.CostantiDB;


/**     
 * SoggettoFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettoFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			GenericJDBCParameterUtilities jdbcParameterUtilities =  
					new GenericJDBCParameterUtilities(tipoDatabase);

			if(model.equals(Soggetto.model())){
				Soggetto object = new Soggetto();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNomeSoggetto", Soggetto.model().NOME_SOGGETTO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_soggetto", Soggetto.model().NOME_SOGGETTO.getFieldType()));
				setParameter(object, "setTipoSoggetto", Soggetto.model().TIPO_SOGGETTO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_soggetto", Soggetto.model().TIPO_SOGGETTO.getFieldType()));
				setParameter(object, "setServer", Soggetto.model().SERVER.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "server", Soggetto.model().SERVER.getFieldType()));
				setParameter(object, "setIdentificativoPorta", Soggetto.model().IDENTIFICATIVO_PORTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "identificativo_porta", Soggetto.model().IDENTIFICATIVO_PORTA.getFieldType()));
				return object;
			}
			if(model.equals(Soggetto.model().SOGGETTO_RUOLO)){
				SoggettoRuolo object = new SoggettoRuolo();
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

			if(model.equals(Soggetto.model())){
				Soggetto object = new Soggetto();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setNomeSoggetto", Soggetto.model().NOME_SOGGETTO.getFieldType(),
					this.getObjectFromMap(map,"nome-soggetto"));
				setParameter(object, "setTipoSoggetto", Soggetto.model().TIPO_SOGGETTO.getFieldType(),
					this.getObjectFromMap(map,"tipo-soggetto"));
				setParameter(object, "setServer", Soggetto.model().SERVER.getFieldType(),
					this.getObjectFromMap(map,"server"));
				setParameter(object, "setIdentificativoPorta", Soggetto.model().IDENTIFICATIVO_PORTA.getFieldType(),
					this.getObjectFromMap(map,"identificativo-porta"));
				return object;
			}
			if(model.equals(Soggetto.model().SOGGETTO_RUOLO)){
				SoggettoRuolo object = new SoggettoRuolo();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"soggetto-ruolo.id"));
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

			if(model.equals(Soggetto.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject(CostantiDB.SOGGETTI,"id","seq_"+CostantiDB.SOGGETTI,CostantiDB.SOGGETTI+"_init_seq");
			}
			if(model.equals(Soggetto.model().SOGGETTO_RUOLO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject(CostantiDB.SOGGETTI_RUOLI,"id","seq_"+CostantiDB.SOGGETTI_RUOLI,CostantiDB.SOGGETTI_RUOLI+"_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
