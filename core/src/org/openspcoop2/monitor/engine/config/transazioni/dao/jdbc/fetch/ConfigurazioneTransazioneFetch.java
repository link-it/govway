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
package org.openspcoop2.monitor.engine.config.transazioni.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione;


/**     
 * ConfigurazioneTransazioneFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneTransazioneFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(ConfigurazioneTransazione.model())){
				ConfigurazioneTransazione object = new ConfigurazioneTransazione();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setEnabled", ConfigurazioneTransazione.model().ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "enabled", ConfigurazioneTransazione.model().ENABLED.getFieldType()));
				return object;
			}
			if(model.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN)){
				ConfigurazioneTransazionePlugin object = new ConfigurazioneTransazionePlugin();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setIdConfigurazioneTransazionePlugin", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_conf_trans_plugin", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN.getFieldType()));
				setParameter(object, "setEnabled", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "enabled", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ENABLED.getFieldType()));
				return object;
			}
			if(model.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO)){
				ConfigurazioneTransazioneStato object = new ConfigurazioneTransazioneStato();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setEnabled", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "enabled", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.ENABLED.getFieldType()));
				setParameter(object, "setNome", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME.getFieldType()));
				setParameter(object, "set_value_tipoControllo", String.class,
					jdbcParameterUtilities.readParameter(rs, "tipo_controllo", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_CONTROLLO.getFieldType())+"");
				setParameter(object, "set_value_tipoMessaggio", String.class,
					jdbcParameterUtilities.readParameter(rs, "tipo_messaggio", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_MESSAGGIO.getFieldType())+"");
				setParameter(object, "setValore", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.VALORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "valore", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.VALORE.getFieldType()));
				setParameter(object, "setXpath", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.XPATH.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "xpath", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.XPATH.getFieldType()));
				return object;
			}
			if(model.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO)){
				ConfigurazioneTransazioneRisorsaContenuto object = new ConfigurazioneTransazioneRisorsaContenuto();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setAbilitaAnonimizzazione", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_ANONIMIZZAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "abilita_anonimizzazione", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_ANONIMIZZAZIONE.getFieldType()));
				setParameter(object, "setAbilitaCompressione", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_COMPRESSIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "abilita_compressione", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_COMPRESSIONE.getFieldType()));
				setParameter(object, "set_value_tipoCompressione", String.class,
					jdbcParameterUtilities.readParameter(rs, "tipo_compressione", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_COMPRESSIONE.getFieldType())+"");
				setParameter(object, "setCarattereMaschera", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.CARATTERE_MASCHERA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "carattere_maschera", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.CARATTERE_MASCHERA.getFieldType()));
				setParameter(object, "setNumeroCaratteriMaschera", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NUMERO_CARATTERI_MASCHERA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "num_char_maschera", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NUMERO_CARATTERI_MASCHERA.getFieldType()));
				setParameter(object, "set_value_posizionamentoMaschera", String.class,
					jdbcParameterUtilities.readParameter(rs, "posizionamento_maschera", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.POSIZIONAMENTO_MASCHERA.getFieldType())+"");
				setParameter(object, "set_value_tipoMascheramento", String.class,
					jdbcParameterUtilities.readParameter(rs, "tipo_mascheramento", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MASCHERAMENTO.getFieldType())+"");
				setParameter(object, "setEnabled", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "enabled", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ENABLED.getFieldType()));
				setParameter(object, "setNome", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NOME.getFieldType()));
				setParameter(object, "set_value_tipoMessaggio", String.class,
					jdbcParameterUtilities.readParameter(rs, "tipo_messaggio", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MESSAGGIO.getFieldType())+"");
				setParameter(object, "setXpath", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.XPATH.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "xpath", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.XPATH.getFieldType()));
				setParameter(object, "setStatEnabled", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.STAT_ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "stat_enabled", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.STAT_ENABLED.getFieldType()));
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

			if(model.equals(ConfigurazioneTransazione.model())){
				ConfigurazioneTransazione object = new ConfigurazioneTransazione();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setEnabled", ConfigurazioneTransazione.model().ENABLED.getFieldType(),
					this.getObjectFromMap(map,"enabled"));
				return object;
			}
			if(model.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN)){
				ConfigurazioneTransazionePlugin object = new ConfigurazioneTransazionePlugin();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"configurazione-transazione-plugin.id"));
				setParameter(object, "setIdConfigurazioneTransazionePlugin", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN.getFieldType(),
					this.getObjectFromMap(map,"configurazione-transazione-plugin.id-configurazione-transazione-plugin"));
				setParameter(object, "setEnabled", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ENABLED.getFieldType(),
					this.getObjectFromMap(map,"configurazione-transazione-plugin.enabled"));
				return object;
			}
			if(model.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO)){
				ConfigurazioneTransazioneStato object = new ConfigurazioneTransazioneStato();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"configurazione-transazione-stato.id"));
				setParameter(object, "setEnabled", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.ENABLED.getFieldType(),
					this.getObjectFromMap(map,"configurazione-transazione-stato.enabled"));
				setParameter(object, "setNome", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME.getFieldType(),
					this.getObjectFromMap(map,"configurazione-transazione-stato.nome"));
				setParameter(object, "set_value_tipoControllo", String.class,
					this.getObjectFromMap(map,"configurazione-transazione-stato.tipo-controllo"));
				setParameter(object, "set_value_tipoMessaggio", String.class,
					this.getObjectFromMap(map,"configurazione-transazione-stato.tipo-messaggio"));
				setParameter(object, "setValore", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.VALORE.getFieldType(),
					this.getObjectFromMap(map,"configurazione-transazione-stato.valore"));
				setParameter(object, "setXpath", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.XPATH.getFieldType(),
					this.getObjectFromMap(map,"configurazione-transazione-stato.xpath"));
				return object;
			}
			if(model.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO)){
				ConfigurazioneTransazioneRisorsaContenuto object = new ConfigurazioneTransazioneRisorsaContenuto();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"configurazione-transazione-risorsa-contenuto.id"));
				setParameter(object, "setAbilitaAnonimizzazione", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_ANONIMIZZAZIONE.getFieldType(),
					this.getObjectFromMap(map,"configurazione-transazione-risorsa-contenuto.abilita-anonimizzazione"));
				setParameter(object, "setAbilitaCompressione", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_COMPRESSIONE.getFieldType(),
					this.getObjectFromMap(map,"configurazione-transazione-risorsa-contenuto.abilita-compressione"));
				setParameter(object, "set_value_tipoCompressione", String.class,
					this.getObjectFromMap(map,"configurazione-transazione-risorsa-contenuto.tipo-compressione"));
				setParameter(object, "setCarattereMaschera", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.CARATTERE_MASCHERA.getFieldType(),
					this.getObjectFromMap(map,"configurazione-transazione-risorsa-contenuto.carattere-maschera"));
				setParameter(object, "setNumeroCaratteriMaschera", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NUMERO_CARATTERI_MASCHERA.getFieldType(),
					this.getObjectFromMap(map,"configurazione-transazione-risorsa-contenuto.numero-caratteri-maschera"));
				setParameter(object, "set_value_posizionamentoMaschera", String.class,
					this.getObjectFromMap(map,"configurazione-transazione-risorsa-contenuto.posizionamento-maschera"));
				setParameter(object, "set_value_tipoMascheramento", String.class,
					this.getObjectFromMap(map,"configurazione-transazione-risorsa-contenuto.tipo-mascheramento"));
				setParameter(object, "setEnabled", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ENABLED.getFieldType(),
					this.getObjectFromMap(map,"configurazione-transazione-risorsa-contenuto.enabled"));
				setParameter(object, "setNome", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NOME.getFieldType(),
					this.getObjectFromMap(map,"configurazione-transazione-risorsa-contenuto.nome"));
				setParameter(object, "set_value_tipoMessaggio", String.class,
					this.getObjectFromMap(map,"configurazione-transazione-risorsa-contenuto.tipo-messaggio"));
				setParameter(object, "setXpath", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.XPATH.getFieldType(),
					this.getObjectFromMap(map,"configurazione-transazione-risorsa-contenuto.xpath"));
				setParameter(object, "setStatEnabled", ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.STAT_ENABLED.getFieldType(),
					this.getObjectFromMap(map,"configurazione-transazione-risorsa-contenuto.stat-enabled"));
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

			if(model.equals(ConfigurazioneTransazione.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("config_transazioni","id","seq_config_transazioni","config_transazioni_init_seq");
			}
			if(model.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("config_tran_plugins","id","seq_config_tran_plugins","config_tran_plugins_init_seq");
			}
			if(model.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("configurazione_stati","id","seq_configurazione_stati","configurazione_stati_init_seq");
			}
			if(model.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("conf_risorse_contenuti","id","seq_conf_risorse_contenuti","conf_risorse_contenuti_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
