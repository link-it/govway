/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.monitor.engine.config.base.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro;


/**     
 * ConfigurazioneFiltroFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneFiltroFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(ConfigurazioneFiltro.model())){
				ConfigurazioneFiltro object = new ConfigurazioneFiltro();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", ConfigurazioneFiltro.model().NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", ConfigurazioneFiltro.model().NOME.getFieldType()));
				setParameter(object, "setDescrizione", ConfigurazioneFiltro.model().DESCRIZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "descrizione", ConfigurazioneFiltro.model().DESCRIZIONE.getFieldType()));
				setParameter(object, "setTipoMittente", ConfigurazioneFiltro.model().TIPO_MITTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_mittente", ConfigurazioneFiltro.model().TIPO_MITTENTE.getFieldType()));
				setParameter(object, "setNomeMittente", ConfigurazioneFiltro.model().NOME_MITTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_mittente", ConfigurazioneFiltro.model().NOME_MITTENTE.getFieldType()));
				setParameter(object, "setIdportaMittente", ConfigurazioneFiltro.model().IDPORTA_MITTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "idporta_mittente", ConfigurazioneFiltro.model().IDPORTA_MITTENTE.getFieldType()));
				setParameter(object, "setTipoDestinatario", ConfigurazioneFiltro.model().TIPO_DESTINATARIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_destinatario", ConfigurazioneFiltro.model().TIPO_DESTINATARIO.getFieldType()));
				setParameter(object, "setNomeDestinatario", ConfigurazioneFiltro.model().NOME_DESTINATARIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_destinatario", ConfigurazioneFiltro.model().NOME_DESTINATARIO.getFieldType()));
				setParameter(object, "setIdportaDestinatario", ConfigurazioneFiltro.model().IDPORTA_DESTINATARIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "idporta_destinatario", ConfigurazioneFiltro.model().IDPORTA_DESTINATARIO.getFieldType()));
				setParameter(object, "setTipoServizio", ConfigurazioneFiltro.model().TIPO_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_servizio", ConfigurazioneFiltro.model().TIPO_SERVIZIO.getFieldType()));
				setParameter(object, "setNomeServizio", ConfigurazioneFiltro.model().NOME_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_servizio", ConfigurazioneFiltro.model().NOME_SERVIZIO.getFieldType()));
				setParameter(object, "setVersioneServizio", ConfigurazioneFiltro.model().VERSIONE_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "versione_servizio", ConfigurazioneFiltro.model().VERSIONE_SERVIZIO.getFieldType()));
				setParameter(object, "setAzione", ConfigurazioneFiltro.model().AZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "azione", ConfigurazioneFiltro.model().AZIONE.getFieldType()));
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

			if(model.equals(ConfigurazioneFiltro.model())){
				ConfigurazioneFiltro object = new ConfigurazioneFiltro();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setNome", ConfigurazioneFiltro.model().NOME.getFieldType(),
					this.getObjectFromMap(map,"nome"));
				setParameter(object, "setDescrizione", ConfigurazioneFiltro.model().DESCRIZIONE.getFieldType(),
					this.getObjectFromMap(map,"descrizione"));
				setParameter(object, "setTipoMittente", ConfigurazioneFiltro.model().TIPO_MITTENTE.getFieldType(),
					this.getObjectFromMap(map,"tipo-mittente"));
				setParameter(object, "setNomeMittente", ConfigurazioneFiltro.model().NOME_MITTENTE.getFieldType(),
					this.getObjectFromMap(map,"nome-mittente"));
				setParameter(object, "setIdportaMittente", ConfigurazioneFiltro.model().IDPORTA_MITTENTE.getFieldType(),
					this.getObjectFromMap(map,"idporta-mittente"));
				setParameter(object, "setTipoDestinatario", ConfigurazioneFiltro.model().TIPO_DESTINATARIO.getFieldType(),
					this.getObjectFromMap(map,"tipo-destinatario"));
				setParameter(object, "setNomeDestinatario", ConfigurazioneFiltro.model().NOME_DESTINATARIO.getFieldType(),
					this.getObjectFromMap(map,"nome-destinatario"));
				setParameter(object, "setIdportaDestinatario", ConfigurazioneFiltro.model().IDPORTA_DESTINATARIO.getFieldType(),
					this.getObjectFromMap(map,"idporta-destinatario"));
				setParameter(object, "setTipoServizio", ConfigurazioneFiltro.model().TIPO_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"tipo-servizio"));
				setParameter(object, "setNomeServizio", ConfigurazioneFiltro.model().NOME_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"nome-servizio"));
				setParameter(object, "setVersioneServizio", ConfigurazioneFiltro.model().VERSIONE_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"versione-servizio"));
				setParameter(object, "setAzione", ConfigurazioneFiltro.model().AZIONE.getFieldType(),
					this.getObjectFromMap(map,"azione"));
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

			if(model.equals(ConfigurazioneFiltro.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("plugins_conf_filtri","id","seq_plugins_conf_filtri","plugins_conf_filtri_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
