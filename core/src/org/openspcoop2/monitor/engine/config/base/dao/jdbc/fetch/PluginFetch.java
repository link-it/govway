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

import org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita;
import org.openspcoop2.monitor.engine.config.base.PluginFiltroCompatibilita;
import org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita;
import org.openspcoop2.monitor.engine.config.base.Plugin;


/**     
 * PluginFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PluginFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(Plugin.model())){
				Plugin object = new Plugin();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "set_value_tipo", String.class,
					jdbcParameterUtilities.readParameter(rs, "tipo", Plugin.model().TIPO.getFieldType())+"");
				setParameter(object, "setClassName", Plugin.model().CLASS_NAME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "class_name", Plugin.model().CLASS_NAME.getFieldType()));
				setParameter(object, "setDescrizione", Plugin.model().DESCRIZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "descrizione", Plugin.model().DESCRIZIONE.getFieldType()));
				setParameter(object, "setLabel", Plugin.model().LABEL.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "label", Plugin.model().LABEL.getFieldType()));
				return object;
			}
			if(model.equals(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA)){
				PluginServizioCompatibilita object = new PluginServizioCompatibilita();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setUriAccordo", Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.URI_ACCORDO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "uri_accordo", Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.URI_ACCORDO.getFieldType()));
				setParameter(object, "setServizio", Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "servizio", Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.SERVIZIO.getFieldType()));
				return object;
			}
			if(model.equals(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA)){
				PluginServizioAzioneCompatibilita object = new PluginServizioAzioneCompatibilita();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setAzione", Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA.AZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "azione", Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA.AZIONE.getFieldType()));
				return object;
			}
			if(model.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA)){
				PluginFiltroCompatibilita object = new PluginFiltroCompatibilita();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setTipoMittente", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_MITTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_mittente", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_MITTENTE.getFieldType()));
				setParameter(object, "setNomeMittente", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_MITTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_mittente", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_MITTENTE.getFieldType()));
				setParameter(object, "setIdportaMittente", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_MITTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "idporta_mittente", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_MITTENTE.getFieldType()));
				setParameter(object, "setTipoDestinatario", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_DESTINATARIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_destinatario", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_DESTINATARIO.getFieldType()));
				setParameter(object, "setNomeDestinatario", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_DESTINATARIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_destinatario", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_DESTINATARIO.getFieldType()));
				setParameter(object, "setIdportaDestinatario", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_DESTINATARIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "idporta_destinatario", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_DESTINATARIO.getFieldType()));
				setParameter(object, "setTipoServizio", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_servizio", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_SERVIZIO.getFieldType()));
				setParameter(object, "setNomeServizio", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_servizio", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_SERVIZIO.getFieldType()));
				setParameter(object, "setVersioneServizio", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.VERSIONE_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "versione_servizio", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.VERSIONE_SERVIZIO.getFieldType()));
				setParameter(object, "setAzione", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.AZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "azione", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.AZIONE.getFieldType()));
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

			if(model.equals(Plugin.model())){
				Plugin object = new Plugin();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "set_value_tipo", String.class,
					this.getObjectFromMap(map,"tipo"));
				setParameter(object, "setClassName", Plugin.model().CLASS_NAME.getFieldType(),
					this.getObjectFromMap(map,"class-name"));
				setParameter(object, "setDescrizione", Plugin.model().DESCRIZIONE.getFieldType(),
					this.getObjectFromMap(map,"descrizione"));
				setParameter(object, "setLabel", Plugin.model().LABEL.getFieldType(),
					this.getObjectFromMap(map,"label"));
				return object;
			}
			if(model.equals(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA)){
				PluginServizioCompatibilita object = new PluginServizioCompatibilita();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"plugin-servizio-compatibilita.id"));
				setParameter(object, "setUriAccordo", Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.URI_ACCORDO.getFieldType(),
					this.getObjectFromMap(map,"plugin-servizio-compatibilita.uri-accordo"));
				setParameter(object, "setServizio", Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"plugin-servizio-compatibilita.servizio"));
				return object;
			}
			if(model.equals(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA)){
				PluginServizioAzioneCompatibilita object = new PluginServizioAzioneCompatibilita();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"plugin-servizio-compatibilita.plugin-servizio-azione-compatibilita.id"));
				setParameter(object, "setAzione", Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA.AZIONE.getFieldType(),
					this.getObjectFromMap(map,"plugin-servizio-compatibilita.plugin-servizio-azione-compatibilita.azione"));
				return object;
			}
			if(model.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA)){
				PluginFiltroCompatibilita object = new PluginFiltroCompatibilita();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"plugin-filtro-compatibilita.id"));
				setParameter(object, "setTipoMittente", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_MITTENTE.getFieldType(),
					this.getObjectFromMap(map,"plugin-filtro-compatibilita.tipo-mittente"));
				setParameter(object, "setNomeMittente", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_MITTENTE.getFieldType(),
					this.getObjectFromMap(map,"plugin-filtro-compatibilita.nome-mittente"));
				setParameter(object, "setIdportaMittente", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_MITTENTE.getFieldType(),
					this.getObjectFromMap(map,"plugin-filtro-compatibilita.idporta-mittente"));
				setParameter(object, "setTipoDestinatario", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_DESTINATARIO.getFieldType(),
					this.getObjectFromMap(map,"plugin-filtro-compatibilita.tipo-destinatario"));
				setParameter(object, "setNomeDestinatario", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_DESTINATARIO.getFieldType(),
					this.getObjectFromMap(map,"plugin-filtro-compatibilita.nome-destinatario"));
				setParameter(object, "setIdportaDestinatario", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_DESTINATARIO.getFieldType(),
					this.getObjectFromMap(map,"plugin-filtro-compatibilita.idporta-destinatario"));
				setParameter(object, "setTipoServizio", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"plugin-filtro-compatibilita.tipo-servizio"));
				setParameter(object, "setNomeServizio", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"plugin-filtro-compatibilita.nome-servizio"));
				setParameter(object, "setVersioneServizio", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.VERSIONE_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"plugin-filtro-compatibilita.versione-servizio"));
				setParameter(object, "setAzione", Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.AZIONE.getFieldType(),
					this.getObjectFromMap(map,"plugin-filtro-compatibilita.azione"));
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

			if(model.equals(Plugin.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("plugins","id","seq_plugins","plugins_init_seq");
			}
			if(model.equals(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("plugins_servizi_comp","id","seq_plugins_servizi_comp","plugins_servizi_comp_init_seq");
			}
			if(model.equals(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("plugins_azioni_comp","id","seq_plugins_azioni_comp","plugins_azioni_comp_init_seq");
			}
			if(model.equals(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("plugins_filtro_comp","id","seq_plugins_filtro_comp","plugins_filtro_comp_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
