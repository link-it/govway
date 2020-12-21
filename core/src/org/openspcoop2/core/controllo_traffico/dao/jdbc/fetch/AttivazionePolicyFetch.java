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

import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;


/**     
 * AttivazionePolicyFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttivazionePolicyFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(AttivazionePolicy.model())){
				AttivazionePolicy object = new AttivazionePolicy();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setIdActivePolicy", AttivazionePolicy.model().ID_ACTIVE_POLICY.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "active_policy_id", AttivazionePolicy.model().ID_ACTIVE_POLICY.getFieldType()));
				setParameter(object, "setAlias", AttivazionePolicy.model().ALIAS.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "policy_alias", AttivazionePolicy.model().ALIAS.getFieldType()));
				setParameter(object, "setUpdateTime", AttivazionePolicy.model().UPDATE_TIME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "policy_update_time", AttivazionePolicy.model().UPDATE_TIME.getFieldType()));
				setParameter(object, "setPosizione", AttivazionePolicy.model().POSIZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "policy_posizione", AttivazionePolicy.model().POSIZIONE.getFieldType()));
				setParameter(object, "setContinuaValutazione", AttivazionePolicy.model().CONTINUA_VALUTAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "policy_continue", AttivazionePolicy.model().CONTINUA_VALUTAZIONE.getFieldType()));
				setParameter(object, "setIdPolicy", AttivazionePolicy.model().ID_POLICY.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "policy_id", AttivazionePolicy.model().ID_POLICY.getFieldType()));
				setParameter(object, "setEnabled", AttivazionePolicy.model().ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "policy_enabled", AttivazionePolicy.model().ENABLED.getFieldType()));
				setParameter(object, "setWarningOnly", AttivazionePolicy.model().WARNING_ONLY.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "policy_warning", AttivazionePolicy.model().WARNING_ONLY.getFieldType()));
				setParameter(object, "setRidefinisci", AttivazionePolicy.model().RIDEFINISCI.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "policy_redefined", AttivazionePolicy.model().RIDEFINISCI.getFieldType()));
				setParameter(object, "setValore", AttivazionePolicy.model().VALORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "policy_valore", AttivazionePolicy.model().VALORE.getFieldType()));
				object.setFiltro(new AttivazionePolicyFiltro());
				setParameter(object.getFiltro(), "setEnabled", AttivazionePolicy.model().FILTRO.ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_enabled", AttivazionePolicy.model().FILTRO.ENABLED.getFieldType()));
				setParameter(object.getFiltro(), "setProtocollo", AttivazionePolicy.model().FILTRO.PROTOCOLLO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_protocollo", AttivazionePolicy.model().FILTRO.PROTOCOLLO.getFieldType()));
				setParameter(object.getFiltro(), "set_value_ruoloPorta", String.class,
					jdbcParameterUtilities.readParameter(rs, "filtro_ruolo", AttivazionePolicy.model().FILTRO.RUOLO_PORTA.getFieldType())+"");
				setParameter(object.getFiltro(), "setNomePorta", AttivazionePolicy.model().FILTRO.NOME_PORTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_porta", AttivazionePolicy.model().FILTRO.NOME_PORTA.getFieldType()));
				setParameter(object.getFiltro(), "setTipoFruitore", AttivazionePolicy.model().FILTRO.TIPO_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_tipo_fruitore", AttivazionePolicy.model().FILTRO.TIPO_FRUITORE.getFieldType()));
				setParameter(object.getFiltro(), "setNomeFruitore", AttivazionePolicy.model().FILTRO.NOME_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_fruitore", AttivazionePolicy.model().FILTRO.NOME_FRUITORE.getFieldType()));
				setParameter(object.getFiltro(), "setRuoloFruitore", AttivazionePolicy.model().FILTRO.RUOLO_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_ruolo_fruitore", AttivazionePolicy.model().FILTRO.RUOLO_FRUITORE.getFieldType()));
				setParameter(object.getFiltro(), "setServizioApplicativoFruitore", AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_sa_fruitore", AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()));
				setParameter(object.getFiltro(), "setTipoErogatore", AttivazionePolicy.model().FILTRO.TIPO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_tipo_erogatore", AttivazionePolicy.model().FILTRO.TIPO_EROGATORE.getFieldType()));
				setParameter(object.getFiltro(), "setNomeErogatore", AttivazionePolicy.model().FILTRO.NOME_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_erogatore", AttivazionePolicy.model().FILTRO.NOME_EROGATORE.getFieldType()));
				setParameter(object.getFiltro(), "setRuoloErogatore", AttivazionePolicy.model().FILTRO.RUOLO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_ruolo_erogatore", AttivazionePolicy.model().FILTRO.RUOLO_EROGATORE.getFieldType()));
				setParameter(object.getFiltro(), "setServizioApplicativoErogatore", AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_sa_erogatore", AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()));
				setParameter(object.getFiltro(), "setTag", AttivazionePolicy.model().FILTRO.TAG.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_tag", AttivazionePolicy.model().FILTRO.TAG.getFieldType()));
				setParameter(object.getFiltro(), "setTipoServizio", AttivazionePolicy.model().FILTRO.TIPO_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_tipo_servizio", AttivazionePolicy.model().FILTRO.TIPO_SERVIZIO.getFieldType()));
				setParameter(object.getFiltro(), "setNomeServizio", AttivazionePolicy.model().FILTRO.NOME_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_servizio", AttivazionePolicy.model().FILTRO.NOME_SERVIZIO.getFieldType()));
				setParameter(object.getFiltro(), "setVersioneServizio", AttivazionePolicy.model().FILTRO.VERSIONE_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_versione_servizio", AttivazionePolicy.model().FILTRO.VERSIONE_SERVIZIO.getFieldType()));
				setParameter(object.getFiltro(), "setAzione", AttivazionePolicy.model().FILTRO.AZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_azione", AttivazionePolicy.model().FILTRO.AZIONE.getFieldType()));
				setParameter(object.getFiltro(), "setInformazioneApplicativaEnabled", AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_key_enabled", AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_ENABLED.getFieldType()));
				setParameter(object.getFiltro(), "setInformazioneApplicativaTipo", AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_TIPO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_key_type", AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_TIPO.getFieldType()));
				setParameter(object.getFiltro(), "setInformazioneApplicativaNome", AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_key_name", AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_NOME.getFieldType()));
				setParameter(object.getFiltro(), "setInformazioneApplicativaValore", AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_VALORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_key_value", AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_VALORE.getFieldType()));
				object.setGroupBy(new AttivazionePolicyRaggruppamento());
				setParameter(object.getGroupBy(), "setEnabled", AttivazionePolicy.model().GROUP_BY.ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_enabled", AttivazionePolicy.model().GROUP_BY.ENABLED.getFieldType()));
				setParameter(object.getGroupBy(), "setRuoloPorta", AttivazionePolicy.model().GROUP_BY.RUOLO_PORTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_ruolo", AttivazionePolicy.model().GROUP_BY.RUOLO_PORTA.getFieldType()));
				setParameter(object.getGroupBy(), "setProtocollo", AttivazionePolicy.model().GROUP_BY.PROTOCOLLO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_protocollo", AttivazionePolicy.model().GROUP_BY.PROTOCOLLO.getFieldType()));
				setParameter(object.getGroupBy(), "setFruitore", AttivazionePolicy.model().GROUP_BY.FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_fruitore", AttivazionePolicy.model().GROUP_BY.FRUITORE.getFieldType()));
				setParameter(object.getGroupBy(), "setServizioApplicativoFruitore", AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_sa_fruitore", AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()));
				setParameter(object.getGroupBy(), "setIdentificativoAutenticato", AttivazionePolicy.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_id_autenticato", AttivazionePolicy.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO.getFieldType()));
				setParameter(object.getGroupBy(), "setToken", AttivazionePolicy.model().GROUP_BY.TOKEN.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_token", AttivazionePolicy.model().GROUP_BY.TOKEN.getFieldType()));
				setParameter(object.getGroupBy(), "setErogatore", AttivazionePolicy.model().GROUP_BY.EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_erogatore", AttivazionePolicy.model().GROUP_BY.EROGATORE.getFieldType()));
				setParameter(object.getGroupBy(), "setServizioApplicativoErogatore", AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_sa_erogatore", AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()));
				setParameter(object.getGroupBy(), "setServizio", AttivazionePolicy.model().GROUP_BY.SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_servizio", AttivazionePolicy.model().GROUP_BY.SERVIZIO.getFieldType()));
				setParameter(object.getGroupBy(), "setAzione", AttivazionePolicy.model().GROUP_BY.AZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_azione", AttivazionePolicy.model().GROUP_BY.AZIONE.getFieldType()));
				setParameter(object.getGroupBy(), "setInformazioneApplicativaEnabled", AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_key_enabled", AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_ENABLED.getFieldType()));
				setParameter(object.getGroupBy(), "setInformazioneApplicativaTipo", AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_TIPO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_key_type", AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_TIPO.getFieldType()));
				setParameter(object.getGroupBy(), "setInformazioneApplicativaNome", AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_key_name", AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_NOME.getFieldType()));
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

			if(model.equals(AttivazionePolicy.model())){
				AttivazionePolicy object = new AttivazionePolicy();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setIdActivePolicy", AttivazionePolicy.model().ID_ACTIVE_POLICY.getFieldType(),
					this.getObjectFromMap(map,"id-active-policy"));
				setParameter(object, "setAlias", AttivazionePolicy.model().ALIAS.getFieldType(),
					this.getObjectFromMap(map,"alias"));
				setParameter(object, "setUpdateTime", AttivazionePolicy.model().UPDATE_TIME.getFieldType(),
					this.getObjectFromMap(map,"update-time"));
				setParameter(object, "setPosizione", AttivazionePolicy.model().POSIZIONE.getFieldType(),
					this.getObjectFromMap(map,"posizione"));
				setParameter(object, "setContinuaValutazione", AttivazionePolicy.model().CONTINUA_VALUTAZIONE.getFieldType(),
					this.getObjectFromMap(map,"continua-valutazione"));
				setParameter(object, "setIdPolicy", AttivazionePolicy.model().ID_POLICY.getFieldType(),
					this.getObjectFromMap(map,"id-policy"));
				setParameter(object, "setEnabled", AttivazionePolicy.model().ENABLED.getFieldType(),
					this.getObjectFromMap(map,"enabled"));
				setParameter(object, "setWarningOnly", AttivazionePolicy.model().WARNING_ONLY.getFieldType(),
					this.getObjectFromMap(map,"warning-only"));
				setParameter(object, "setRidefinisci", AttivazionePolicy.model().RIDEFINISCI.getFieldType(),
					this.getObjectFromMap(map,"ridefinisci"));
				setParameter(object, "setValore", AttivazionePolicy.model().VALORE.getFieldType(),
					this.getObjectFromMap(map,"valore"));
				object.setFiltro(new AttivazionePolicyFiltro());
				setParameter(object.getFiltro(), "setEnabled", AttivazionePolicy.model().FILTRO.ENABLED.getFieldType(),
					this.getObjectFromMap(map,"filtro.enabled"));
				setParameter(object.getFiltro(), "setProtocollo", AttivazionePolicy.model().FILTRO.PROTOCOLLO.getFieldType(),
					this.getObjectFromMap(map,"filtro.protocollo"));
				setParameter(object.getFiltro(), "set_value_ruoloPorta", String.class,
					this.getObjectFromMap(map,"filtro.ruolo-porta"));
				setParameter(object.getFiltro(), "setNomePorta", AttivazionePolicy.model().FILTRO.NOME_PORTA.getFieldType(),
					this.getObjectFromMap(map,"filtro.nome-porta"));
				setParameter(object.getFiltro(), "setTipoFruitore", AttivazionePolicy.model().FILTRO.TIPO_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.tipo-fruitore"));
				setParameter(object.getFiltro(), "setNomeFruitore", AttivazionePolicy.model().FILTRO.NOME_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.nome-fruitore"));
				setParameter(object.getFiltro(), "setRuoloFruitore", AttivazionePolicy.model().FILTRO.RUOLO_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.ruolo-fruitore"));
				setParameter(object.getFiltro(), "setServizioApplicativoFruitore", AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.servizio-applicativo-fruitore"));
				setParameter(object.getFiltro(), "setTipoErogatore", AttivazionePolicy.model().FILTRO.TIPO_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.tipo-erogatore"));
				setParameter(object.getFiltro(), "setNomeErogatore", AttivazionePolicy.model().FILTRO.NOME_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.nome-erogatore"));
				setParameter(object.getFiltro(), "setRuoloErogatore", AttivazionePolicy.model().FILTRO.RUOLO_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.ruolo-erogatore"));
				setParameter(object.getFiltro(), "setServizioApplicativoErogatore", AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.servizio-applicativo-erogatore"));
				setParameter(object.getFiltro(), "setTag", AttivazionePolicy.model().FILTRO.TAG.getFieldType(),
					this.getObjectFromMap(map,"filtro.tag"));
				setParameter(object.getFiltro(), "setTipoServizio", AttivazionePolicy.model().FILTRO.TIPO_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"filtro.tipo-servizio"));
				setParameter(object.getFiltro(), "setNomeServizio", AttivazionePolicy.model().FILTRO.NOME_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"filtro.nome-servizio"));
				setParameter(object.getFiltro(), "setVersioneServizio", AttivazionePolicy.model().FILTRO.VERSIONE_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"filtro.versione-servizio"));
				setParameter(object.getFiltro(), "setAzione", AttivazionePolicy.model().FILTRO.AZIONE.getFieldType(),
					this.getObjectFromMap(map,"filtro.azione"));
				setParameter(object.getFiltro(), "setInformazioneApplicativaEnabled", AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_ENABLED.getFieldType(),
					this.getObjectFromMap(map,"filtro.informazione-applicativa-enabled"));
				setParameter(object.getFiltro(), "setInformazioneApplicativaTipo", AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_TIPO.getFieldType(),
					this.getObjectFromMap(map,"filtro.informazione-applicativa-tipo"));
				setParameter(object.getFiltro(), "setInformazioneApplicativaNome", AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_NOME.getFieldType(),
					this.getObjectFromMap(map,"filtro.informazione-applicativa-nome"));
				setParameter(object.getFiltro(), "setInformazioneApplicativaValore", AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_VALORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.informazione-applicativa-valore"));
				object.setGroupBy(new AttivazionePolicyRaggruppamento());
				setParameter(object.getGroupBy(), "setEnabled", AttivazionePolicy.model().GROUP_BY.ENABLED.getFieldType(),
					this.getObjectFromMap(map,"group-by.enabled"));
				setParameter(object.getGroupBy(), "setRuoloPorta", AttivazionePolicy.model().GROUP_BY.RUOLO_PORTA.getFieldType(),
					this.getObjectFromMap(map,"group-by.ruolo-porta"));
				setParameter(object.getGroupBy(), "setProtocollo", AttivazionePolicy.model().GROUP_BY.PROTOCOLLO.getFieldType(),
					this.getObjectFromMap(map,"group-by.protocollo"));
				setParameter(object.getGroupBy(), "setFruitore", AttivazionePolicy.model().GROUP_BY.FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"group-by.fruitore"));
				setParameter(object.getGroupBy(), "setServizioApplicativoFruitore", AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"group-by.servizio-applicativo-fruitore"));
				setParameter(object.getGroupBy(), "setIdentificativoAutenticato", AttivazionePolicy.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO.getFieldType(),
					this.getObjectFromMap(map,"group-by.identificativo-autenticato"));
				setParameter(object.getGroupBy(), "setToken", AttivazionePolicy.model().GROUP_BY.TOKEN.getFieldType(),
					this.getObjectFromMap(map,"group-by.token"));
				setParameter(object.getGroupBy(), "setErogatore", AttivazionePolicy.model().GROUP_BY.EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"group-by.erogatore"));
				setParameter(object.getGroupBy(), "setServizioApplicativoErogatore", AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"group-by.servizio-applicativo-erogatore"));
				setParameter(object.getGroupBy(), "setServizio", AttivazionePolicy.model().GROUP_BY.SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"group-by.servizio"));
				setParameter(object.getGroupBy(), "setAzione", AttivazionePolicy.model().GROUP_BY.AZIONE.getFieldType(),
					this.getObjectFromMap(map,"group-by.azione"));
				setParameter(object.getGroupBy(), "setInformazioneApplicativaEnabled", AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_ENABLED.getFieldType(),
					this.getObjectFromMap(map,"group-by.informazione-applicativa-enabled"));
				setParameter(object.getGroupBy(), "setInformazioneApplicativaTipo", AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_TIPO.getFieldType(),
					this.getObjectFromMap(map,"group-by.informazione-applicativa-tipo"));
				setParameter(object.getGroupBy(), "setInformazioneApplicativaNome", AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_NOME.getFieldType(),
					this.getObjectFromMap(map,"group-by.informazione-applicativa-nome"));
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

			if(model.equals(AttivazionePolicy.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("ct_active_policy","id","seq_ct_active_policy","ct_active_policy_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
