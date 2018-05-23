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
package org.openspcoop2.core.controllo_traffico.dao.jdbc.fetch;

import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;


/**     
 * ConfigurazionePolicyFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePolicyFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(ConfigurazionePolicy.model())){
				ConfigurazionePolicy object = new ConfigurazionePolicy();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setIdPolicy", ConfigurazionePolicy.model().ID_POLICY.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "policy_id", ConfigurazionePolicy.model().ID_POLICY.getFieldType()));
				setParameter(object, "setDescrizione", ConfigurazionePolicy.model().DESCRIZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "rt_descrizione", ConfigurazionePolicy.model().DESCRIZIONE.getFieldType()));
				setParameter(object, "setRisorsa", ConfigurazionePolicy.model().RISORSA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "rt_risorsa", ConfigurazionePolicy.model().RISORSA.getFieldType()));
				setParameter(object, "setSimultanee", ConfigurazionePolicy.model().SIMULTANEE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "rt_simultanee", ConfigurazionePolicy.model().SIMULTANEE.getFieldType()));
				setParameter(object, "setValore", ConfigurazionePolicy.model().VALORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "rt_valore", ConfigurazionePolicy.model().VALORE.getFieldType()));
				setParameter(object, "set_value_valoreTipoBanda", String.class,
					jdbcParameterUtilities.readParameter(rs, "rt_bytes_type", ConfigurazionePolicy.model().VALORE_TIPO_BANDA.getFieldType())+"");
				setParameter(object, "set_value_valoreTipoLatenza", String.class,
					jdbcParameterUtilities.readParameter(rs, "rt_latency_type", ConfigurazionePolicy.model().VALORE_TIPO_LATENZA.getFieldType())+"");
				setParameter(object, "set_value_modalitaControllo", String.class,
					jdbcParameterUtilities.readParameter(rs, "rt_modalita_controllo", ConfigurazionePolicy.model().MODALITA_CONTROLLO.getFieldType())+"");
				setParameter(object, "set_value_tipoIntervalloOsservazioneRealtime", String.class,
					jdbcParameterUtilities.readParameter(rs, "rt_interval_type_real", ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_REALTIME.getFieldType())+"");
				setParameter(object, "set_value_tipoIntervalloOsservazioneStatistico", String.class,
					jdbcParameterUtilities.readParameter(rs, "rt_interval_type_stat", ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO.getFieldType())+"");
				setParameter(object, "setIntervalloOsservazione", ConfigurazionePolicy.model().INTERVALLO_OSSERVAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "rt_interval", ConfigurazionePolicy.model().INTERVALLO_OSSERVAZIONE.getFieldType()));
				setParameter(object, "set_value_finestraOsservazione", String.class,
					jdbcParameterUtilities.readParameter(rs, "rt_finestra", ConfigurazionePolicy.model().FINESTRA_OSSERVAZIONE.getFieldType())+"");
				setParameter(object, "set_value_tipoApplicabilita", String.class,
					jdbcParameterUtilities.readParameter(rs, "rt_applicabilita", ConfigurazionePolicy.model().TIPO_APPLICABILITA.getFieldType())+"");
				setParameter(object, "setApplicabilitaConCongestione", ConfigurazionePolicy.model().APPLICABILITA_CON_CONGESTIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "rt_applicabilita_con_cc", ConfigurazionePolicy.model().APPLICABILITA_CON_CONGESTIONE.getFieldType()));
				setParameter(object, "setApplicabilitaDegradoPrestazionale", ConfigurazionePolicy.model().APPLICABILITA_DEGRADO_PRESTAZIONALE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "rt_applicabilita_degrado", ConfigurazionePolicy.model().APPLICABILITA_DEGRADO_PRESTAZIONALE.getFieldType()));
				setParameter(object, "set_value_degradoAvgTimeModalitaControllo", String.class,
					jdbcParameterUtilities.readParameter(rs, "degrato_modalita_controllo", ConfigurazionePolicy.model().DEGRADO_AVG_TIME_MODALITA_CONTROLLO.getFieldType())+"");
				setParameter(object, "set_value_degradoAvgTimeTipoIntervalloOsservazioneRealtime", String.class,
					jdbcParameterUtilities.readParameter(rs, "degrado_avg_interval_type_real", ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_REALTIME.getFieldType())+"");
				setParameter(object, "set_value_degradoAvgTimeTipoIntervalloOsservazioneStatistico", String.class,
					jdbcParameterUtilities.readParameter(rs, "degrado_avg_interval_type_stat", ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO.getFieldType())+"");
				setParameter(object, "setDegradoAvgTimeIntervalloOsservazione", ConfigurazionePolicy.model().DEGRADO_AVG_TIME_INTERVALLO_OSSERVAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "degrado_avg_interval", ConfigurazionePolicy.model().DEGRADO_AVG_TIME_INTERVALLO_OSSERVAZIONE.getFieldType()));
				setParameter(object, "set_value_degradoAvgTimeFinestraOsservazione", String.class,
					jdbcParameterUtilities.readParameter(rs, "degrado_avg_finestra", ConfigurazionePolicy.model().DEGRADO_AVG_TIME_FINESTRA_OSSERVAZIONE.getFieldType())+"");
				setParameter(object, "set_value_degradoAvgTimeTipoLatenza", String.class,
					jdbcParameterUtilities.readParameter(rs, "degrado_avg_latency_type", ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_LATENZA.getFieldType())+"");
				setParameter(object, "setApplicabilitaStatoAllarme", ConfigurazionePolicy.model().APPLICABILITA_STATO_ALLARME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "rt_applicabilita_allarme", ConfigurazionePolicy.model().APPLICABILITA_STATO_ALLARME.getFieldType()));
				setParameter(object, "setAllarmeNome", ConfigurazionePolicy.model().ALLARME_NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "allarme_nome", ConfigurazionePolicy.model().ALLARME_NOME.getFieldType()));
				setParameter(object, "setAllarmeStato", ConfigurazionePolicy.model().ALLARME_STATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "allarme_stato", ConfigurazionePolicy.model().ALLARME_STATO.getFieldType()));
				setParameter(object, "setAllarmeNotStato", ConfigurazionePolicy.model().ALLARME_NOT_STATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "allarme_not_stato", ConfigurazionePolicy.model().ALLARME_NOT_STATO.getFieldType()));
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

			if(model.equals(ConfigurazionePolicy.model())){
				ConfigurazionePolicy object = new ConfigurazionePolicy();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setIdPolicy", ConfigurazionePolicy.model().ID_POLICY.getFieldType(),
					this.getObjectFromMap(map,"id-policy"));
				setParameter(object, "setDescrizione", ConfigurazionePolicy.model().DESCRIZIONE.getFieldType(),
					this.getObjectFromMap(map,"descrizione"));
				setParameter(object, "setRisorsa", ConfigurazionePolicy.model().RISORSA.getFieldType(),
					this.getObjectFromMap(map,"risorsa"));
				setParameter(object, "setSimultanee", ConfigurazionePolicy.model().SIMULTANEE.getFieldType(),
					this.getObjectFromMap(map,"simultanee"));
				setParameter(object, "setValore", ConfigurazionePolicy.model().VALORE.getFieldType(),
					this.getObjectFromMap(map,"valore"));
				setParameter(object, "set_value_valoreTipoBanda", String.class,
					this.getObjectFromMap(map,"valore-tipo-banda"));
				setParameter(object, "set_value_valoreTipoLatenza", String.class,
					this.getObjectFromMap(map,"valore-tipo-latenza"));
				setParameter(object, "set_value_modalitaControllo", String.class,
					this.getObjectFromMap(map,"modalita-controllo"));
				setParameter(object, "set_value_tipoIntervalloOsservazioneRealtime", String.class,
					this.getObjectFromMap(map,"tipo-intervallo-osservazione-realtime"));
				setParameter(object, "set_value_tipoIntervalloOsservazioneStatistico", String.class,
					this.getObjectFromMap(map,"tipo-intervallo-osservazione-statistico"));
				setParameter(object, "setIntervalloOsservazione", ConfigurazionePolicy.model().INTERVALLO_OSSERVAZIONE.getFieldType(),
					this.getObjectFromMap(map,"intervallo-osservazione"));
				setParameter(object, "set_value_finestraOsservazione", String.class,
					this.getObjectFromMap(map,"finestra-osservazione"));
				setParameter(object, "set_value_tipoApplicabilita", String.class,
					this.getObjectFromMap(map,"tipo-applicabilita"));
				setParameter(object, "setApplicabilitaConCongestione", ConfigurazionePolicy.model().APPLICABILITA_CON_CONGESTIONE.getFieldType(),
					this.getObjectFromMap(map,"applicabilita-con-congestione"));
				setParameter(object, "setApplicabilitaDegradoPrestazionale", ConfigurazionePolicy.model().APPLICABILITA_DEGRADO_PRESTAZIONALE.getFieldType(),
					this.getObjectFromMap(map,"applicabilita-degrado-prestazionale"));
				setParameter(object, "set_value_degradoAvgTimeModalitaControllo", String.class,
					this.getObjectFromMap(map,"degrado-avg-time-modalita-controllo"));
				setParameter(object, "set_value_degradoAvgTimeTipoIntervalloOsservazioneRealtime", String.class,
					this.getObjectFromMap(map,"degrado-avg-time-tipo-intervallo-osservazione-realtime"));
				setParameter(object, "set_value_degradoAvgTimeTipoIntervalloOsservazioneStatistico", String.class,
					this.getObjectFromMap(map,"degrado-avg-time-tipo-intervallo-osservazione-statistico"));
				setParameter(object, "setDegradoAvgTimeIntervalloOsservazione", ConfigurazionePolicy.model().DEGRADO_AVG_TIME_INTERVALLO_OSSERVAZIONE.getFieldType(),
					this.getObjectFromMap(map,"degrado-avg-time-intervallo-osservazione"));
				setParameter(object, "set_value_degradoAvgTimeFinestraOsservazione", String.class,
					this.getObjectFromMap(map,"degrado-avg-time-finestra-osservazione"));
				setParameter(object, "set_value_degradoAvgTimeTipoLatenza", String.class,
					this.getObjectFromMap(map,"degrado-avg-time-tipo-latenza"));
				setParameter(object, "setApplicabilitaStatoAllarme", ConfigurazionePolicy.model().APPLICABILITA_STATO_ALLARME.getFieldType(),
					this.getObjectFromMap(map,"applicabilita-stato-allarme"));
				setParameter(object, "setAllarmeNome", ConfigurazionePolicy.model().ALLARME_NOME.getFieldType(),
					this.getObjectFromMap(map,"allarme-nome"));
				setParameter(object, "setAllarmeStato", ConfigurazionePolicy.model().ALLARME_STATO.getFieldType(),
					this.getObjectFromMap(map,"allarme-stato"));
				setParameter(object, "setAllarmeNotStato", ConfigurazionePolicy.model().ALLARME_NOT_STATO.getFieldType(),
					this.getObjectFromMap(map,"allarme-not-stato"));
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

			if(model.equals(ConfigurazionePolicy.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("congestion_config_policy","id","seq_congestion_config_policy","congestion_config_policy_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
