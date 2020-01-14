/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.controllo_traffico.dao.jdbc.fetch;

import org.openspcoop2.core.controllo_traffico.Cache;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting;
import org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione;
import org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;


/**     
 * ConfigurazioneGeneraleFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneGeneraleFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(ConfigurazioneGenerale.model())){
				ConfigurazioneGenerale object = new ConfigurazioneGenerale();
				object.setControlloTraffico(new ConfigurazioneControlloTraffico());
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object.getControlloTraffico(), "setControlloMaxThreadsEnabled", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "max_threads_enabled", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_ENABLED.getFieldType()));
				setParameter(object.getControlloTraffico(), "setControlloMaxThreadsWarningOnly", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_WARNING_ONLY.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "max_threads_warning_only", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_WARNING_ONLY.getFieldType()));
				setParameter(object.getControlloTraffico(), "setControlloMaxThreadsSoglia", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_SOGLIA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "max_threads", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_SOGLIA.getFieldType()));
				setParameter(object.getControlloTraffico(), "setControlloMaxThreadsTipoErrore", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "max_threads_tipo_errore", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE.getFieldType()));
				setParameter(object.getControlloTraffico(), "setControlloMaxThreadsTipoErroreIncludiDescrizione", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE_INCLUDI_DESCRIZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "max_threads_includi_errore", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE_INCLUDI_DESCRIZIONE.getFieldType()));
				setParameter(object.getControlloTraffico(), "setControlloCongestioneEnabled", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cc_enabled", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_ENABLED.getFieldType()));
				setParameter(object.getControlloTraffico(), "setControlloCongestioneThreshold", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_THRESHOLD.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cc_threshold", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_THRESHOLD.getFieldType()));
				object.setTempiRispostaFruizione(new TempiRispostaFruizione());
				setParameter(object.getTempiRispostaFruizione(), "setConnectionTimeout", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.CONNECTION_TIMEOUT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "pd_connection_timeout", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.CONNECTION_TIMEOUT.getFieldType()));
				setParameter(object.getTempiRispostaFruizione(), "setReadTimeout", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.READ_TIMEOUT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "pd_read_timeout", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.READ_TIMEOUT.getFieldType()));
				setParameter(object.getTempiRispostaFruizione(), "setTempoMedioRisposta", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.TEMPO_MEDIO_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "pd_avg_time", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.TEMPO_MEDIO_RISPOSTA.getFieldType()));
				object.setTempiRispostaErogazione(new TempiRispostaErogazione());
				setParameter(object.getTempiRispostaErogazione(), "setConnectionTimeout", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.CONNECTION_TIMEOUT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "pa_connection_timeout", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.CONNECTION_TIMEOUT.getFieldType()));
				setParameter(object.getTempiRispostaErogazione(), "setReadTimeout", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.READ_TIMEOUT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "pa_read_timeout", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.READ_TIMEOUT.getFieldType()));
				setParameter(object.getTempiRispostaErogazione(), "setTempoMedioRisposta", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.TEMPO_MEDIO_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "pa_avg_time", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.TEMPO_MEDIO_RISPOSTA.getFieldType()));
				object.setRateLimiting(new ConfigurazioneRateLimiting());
				setParameter(object.getRateLimiting(), "setTipoErrore", ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "rt_tipo_errore", ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE.getFieldType()));
				setParameter(object.getRateLimiting(), "setTipoErroreIncludiDescrizione", ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE_INCLUDI_DESCRIZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "rt_includi_errore", ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE_INCLUDI_DESCRIZIONE.getFieldType()));
				object.setCache(new Cache());
				setParameter(object.getCache(), "setCache", ConfigurazioneGenerale.model().CACHE.CACHE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cache", ConfigurazioneGenerale.model().CACHE.CACHE.getFieldType()));
				setParameter(object.getCache(), "setSize", ConfigurazioneGenerale.model().CACHE.SIZE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cache_size", ConfigurazioneGenerale.model().CACHE.SIZE.getFieldType()));
				setParameter(object.getCache(), "set_value_algorithm", String.class,
					jdbcParameterUtilities.readParameter(rs, "cache_algorithm", ConfigurazioneGenerale.model().CACHE.ALGORITHM.getFieldType())+"");
				setParameter(object.getCache(), "setIdleTime", ConfigurazioneGenerale.model().CACHE.IDLE_TIME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cache_idle_time", ConfigurazioneGenerale.model().CACHE.IDLE_TIME.getFieldType()));
				setParameter(object.getCache(), "setLifeTime", ConfigurazioneGenerale.model().CACHE.LIFE_TIME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cache_life_time", ConfigurazioneGenerale.model().CACHE.LIFE_TIME.getFieldType()));
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

			if(model.equals(ConfigurazioneGenerale.model())){
				ConfigurazioneGenerale object = new ConfigurazioneGenerale();
				object.setControlloTraffico(new ConfigurazioneControlloTraffico());
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"controllo-traffico.id"));
				setParameter(object.getControlloTraffico(), "setControlloMaxThreadsEnabled", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_ENABLED.getFieldType(),
					this.getObjectFromMap(map,"controllo-traffico.controllo-max-threads-enabled"));
				setParameter(object.getControlloTraffico(), "setControlloMaxThreadsWarningOnly", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_WARNING_ONLY.getFieldType(),
					this.getObjectFromMap(map,"controllo-traffico.controllo-max-threads-warning-only"));
				setParameter(object.getControlloTraffico(), "setControlloMaxThreadsSoglia", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_SOGLIA.getFieldType(),
					this.getObjectFromMap(map,"controllo-traffico.controllo-max-threads-soglia"));
				setParameter(object.getControlloTraffico(), "setControlloMaxThreadsTipoErrore", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE.getFieldType(),
					this.getObjectFromMap(map,"controllo-traffico.controllo-max-threads-tipo-errore"));
				setParameter(object.getControlloTraffico(), "setControlloMaxThreadsTipoErroreIncludiDescrizione", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE_INCLUDI_DESCRIZIONE.getFieldType(),
					this.getObjectFromMap(map,"controllo-traffico.controllo-max-threads-tipo-errore-includi-descrizione"));
				setParameter(object.getControlloTraffico(), "setControlloCongestioneEnabled", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_ENABLED.getFieldType(),
					this.getObjectFromMap(map,"controllo-traffico.controllo-congestione-enabled"));
				setParameter(object.getControlloTraffico(), "setControlloCongestioneThreshold", ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_THRESHOLD.getFieldType(),
					this.getObjectFromMap(map,"controllo-traffico.controllo-congestione-threshold"));
				object.setTempiRispostaFruizione(new TempiRispostaFruizione());
				setParameter(object.getTempiRispostaFruizione(), "setConnectionTimeout", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.CONNECTION_TIMEOUT.getFieldType(),
					this.getObjectFromMap(map,"tempi-risposta-fruizione.connection-timeout"));
				setParameter(object.getTempiRispostaFruizione(), "setReadTimeout", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.READ_TIMEOUT.getFieldType(),
					this.getObjectFromMap(map,"tempi-risposta-fruizione.read-timeout"));
				setParameter(object.getTempiRispostaFruizione(), "setTempoMedioRisposta", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.TEMPO_MEDIO_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"tempi-risposta-fruizione.tempo-medio-risposta"));
				object.setTempiRispostaErogazione(new TempiRispostaErogazione());
				setParameter(object.getTempiRispostaErogazione(), "setConnectionTimeout", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.CONNECTION_TIMEOUT.getFieldType(),
					this.getObjectFromMap(map,"tempi-risposta-erogazione.connection-timeout"));
				setParameter(object.getTempiRispostaErogazione(), "setReadTimeout", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.READ_TIMEOUT.getFieldType(),
					this.getObjectFromMap(map,"tempi-risposta-erogazione.read-timeout"));
				setParameter(object.getTempiRispostaErogazione(), "setTempoMedioRisposta", ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.TEMPO_MEDIO_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"tempi-risposta-erogazione.tempo-medio-risposta"));
				object.setRateLimiting(new ConfigurazioneRateLimiting());
				setParameter(object.getRateLimiting(), "setTipoErrore", ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE.getFieldType(),
					this.getObjectFromMap(map,"rate-limiting.tipo-errore"));
				setParameter(object.getRateLimiting(), "setTipoErroreIncludiDescrizione", ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE_INCLUDI_DESCRIZIONE.getFieldType(),
					this.getObjectFromMap(map,"rate-limiting.tipo-errore-includi-descrizione"));
				object.setCache(new Cache());
				setParameter(object.getCache(), "setCache", ConfigurazioneGenerale.model().CACHE.CACHE.getFieldType(),
					this.getObjectFromMap(map,"cache.cache"));
				setParameter(object.getCache(), "setSize", ConfigurazioneGenerale.model().CACHE.SIZE.getFieldType(),
					this.getObjectFromMap(map,"cache.size"));
				setParameter(object.getCache(), "set_value_algorithm", String.class,
					this.getObjectFromMap(map,"cache.algorithm"));
				setParameter(object.getCache(), "setIdleTime", ConfigurazioneGenerale.model().CACHE.IDLE_TIME.getFieldType(),
					this.getObjectFromMap(map,"cache.idle-time"));
				setParameter(object.getCache(), "setLifeTime", ConfigurazioneGenerale.model().CACHE.LIFE_TIME.getFieldType(),
					this.getObjectFromMap(map,"cache.life-time"));
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

			if(model.equals(ConfigurazioneGenerale.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("ct_config","id","seq_ct_config","ct_config_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
