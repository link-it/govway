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
package org.openspcoop2.core.allarmi.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.allarmi.AllarmeFiltro;
import org.openspcoop2.core.allarmi.AllarmeMail;
import org.openspcoop2.core.allarmi.AllarmeParametro;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeScript;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.allarmi.AllarmeRaggruppamento;


/**     
 * AllarmeFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmeFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(Allarme.model())){
				Allarme object = new Allarme();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", Allarme.model().NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", Allarme.model().NOME.getFieldType()));
				setParameter(object, "setTipo", Allarme.model().TIPO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo", Allarme.model().TIPO.getFieldType()));
				setParameter(object, "set_value_tipoAllarme", String.class,
					jdbcParameterUtilities.readParameter(rs, "tipo_allarme", Allarme.model().TIPO_ALLARME.getFieldType())+"");
				object.setMail(new AllarmeMail());
				setParameter(object.getMail(), "setAckMode", Allarme.model().MAIL.ACK_MODE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "mail_ack_mode", Allarme.model().MAIL.ACK_MODE.getFieldType()));
				setParameter(object.getMail(), "setInviaWarning", Allarme.model().MAIL.INVIA_WARNING.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "mail_invia_warning", Allarme.model().MAIL.INVIA_WARNING.getFieldType()));
				setParameter(object.getMail(), "setInviaAlert", Allarme.model().MAIL.INVIA_ALERT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "mail_invia_alert", Allarme.model().MAIL.INVIA_ALERT.getFieldType()));
				setParameter(object.getMail(), "setDestinatari", Allarme.model().MAIL.DESTINATARI.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "mail_destinatari", Allarme.model().MAIL.DESTINATARI.getFieldType()));
				setParameter(object.getMail(), "setSubject", Allarme.model().MAIL.SUBJECT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "mail_subject", Allarme.model().MAIL.SUBJECT.getFieldType()));
				setParameter(object.getMail(), "setBody", Allarme.model().MAIL.BODY.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "mail_body", Allarme.model().MAIL.BODY.getFieldType()));
				object.setScript(new AllarmeScript());
				setParameter(object.getScript(), "setAckMode", Allarme.model().SCRIPT.ACK_MODE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "script_ack_mode", Allarme.model().SCRIPT.ACK_MODE.getFieldType()));
				setParameter(object.getScript(), "setInvocaWarning", Allarme.model().SCRIPT.INVOCA_WARNING.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "script_invoke_warning", Allarme.model().SCRIPT.INVOCA_WARNING.getFieldType()));
				setParameter(object.getScript(), "setInvocaAlert", Allarme.model().SCRIPT.INVOCA_ALERT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "script_invoke_alert", Allarme.model().SCRIPT.INVOCA_ALERT.getFieldType()));
				setParameter(object.getScript(), "setCommand", Allarme.model().SCRIPT.COMMAND.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "script_command", Allarme.model().SCRIPT.COMMAND.getFieldType()));
				setParameter(object.getScript(), "setArgs", Allarme.model().SCRIPT.ARGS.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "script_args", Allarme.model().SCRIPT.ARGS.getFieldType()));
				setParameter(object, "setStatoPrecedente", Allarme.model().STATO_PRECEDENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "stato_precedente", Allarme.model().STATO_PRECEDENTE.getFieldType()));
				setParameter(object, "setStato", Allarme.model().STATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "stato", Allarme.model().STATO.getFieldType()));
				setParameter(object, "setDettaglioStato", Allarme.model().DETTAGLIO_STATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "stato_dettaglio", Allarme.model().DETTAGLIO_STATO.getFieldType()));
				setParameter(object, "setLasttimestampCreate", Allarme.model().LASTTIMESTAMP_CREATE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "lasttimestamp_create", Allarme.model().LASTTIMESTAMP_CREATE.getFieldType()));
				setParameter(object, "setLasttimestampUpdate", Allarme.model().LASTTIMESTAMP_UPDATE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "lasttimestamp_update", Allarme.model().LASTTIMESTAMP_UPDATE.getFieldType()));
				setParameter(object, "setEnabled", Allarme.model().ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "enabled", Allarme.model().ENABLED.getFieldType()));
				setParameter(object, "setAcknowledged", Allarme.model().ACKNOWLEDGED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "acknowledged", Allarme.model().ACKNOWLEDGED.getFieldType()));
				setParameter(object, "setTipoPeriodo", Allarme.model().TIPO_PERIODO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "periodo_tipo", Allarme.model().TIPO_PERIODO.getFieldType()));
				setParameter(object, "setPeriodo", Allarme.model().PERIODO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "periodo", Allarme.model().PERIODO.getFieldType()));
				object.setFiltro(new AllarmeFiltro());
				setParameter(object.getFiltro(), "setEnabled", Allarme.model().FILTRO.ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_enabled", Allarme.model().FILTRO.ENABLED.getFieldType()));
				setParameter(object.getFiltro(), "setProtocollo", Allarme.model().FILTRO.PROTOCOLLO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_protocollo", Allarme.model().FILTRO.PROTOCOLLO.getFieldType()));
				setParameter(object.getFiltro(), "set_value_ruoloPorta", String.class,
					jdbcParameterUtilities.readParameter(rs, "filtro_ruolo", Allarme.model().FILTRO.RUOLO_PORTA.getFieldType())+"");
				setParameter(object.getFiltro(), "setNomePorta", Allarme.model().FILTRO.NOME_PORTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_porta", Allarme.model().FILTRO.NOME_PORTA.getFieldType()));
				setParameter(object.getFiltro(), "setTipoFruitore", Allarme.model().FILTRO.TIPO_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_tipo_fruitore", Allarme.model().FILTRO.TIPO_FRUITORE.getFieldType()));
				setParameter(object.getFiltro(), "setNomeFruitore", Allarme.model().FILTRO.NOME_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_fruitore", Allarme.model().FILTRO.NOME_FRUITORE.getFieldType()));
				setParameter(object.getFiltro(), "setRuoloFruitore", Allarme.model().FILTRO.RUOLO_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_ruolo_fruitore", Allarme.model().FILTRO.RUOLO_FRUITORE.getFieldType()));
				setParameter(object.getFiltro(), "setServizioApplicativoFruitore", Allarme.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_sa_fruitore", Allarme.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()));
				setParameter(object.getFiltro(), "setTipoErogatore", Allarme.model().FILTRO.TIPO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_tipo_erogatore", Allarme.model().FILTRO.TIPO_EROGATORE.getFieldType()));
				setParameter(object.getFiltro(), "setNomeErogatore", Allarme.model().FILTRO.NOME_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_erogatore", Allarme.model().FILTRO.NOME_EROGATORE.getFieldType()));
				setParameter(object.getFiltro(), "setRuoloErogatore", Allarme.model().FILTRO.RUOLO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_ruolo_erogatore", Allarme.model().FILTRO.RUOLO_EROGATORE.getFieldType()));
				setParameter(object.getFiltro(), "setTag", Allarme.model().FILTRO.TAG.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_tag", Allarme.model().FILTRO.TAG.getFieldType()));
				setParameter(object.getFiltro(), "setTipoServizio", Allarme.model().FILTRO.TIPO_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_tipo_servizio", Allarme.model().FILTRO.TIPO_SERVIZIO.getFieldType()));
				setParameter(object.getFiltro(), "setNomeServizio", Allarme.model().FILTRO.NOME_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_servizio", Allarme.model().FILTRO.NOME_SERVIZIO.getFieldType()));
				setParameter(object.getFiltro(), "setVersioneServizio", Allarme.model().FILTRO.VERSIONE_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_versione_servizio", Allarme.model().FILTRO.VERSIONE_SERVIZIO.getFieldType()));
				setParameter(object.getFiltro(), "setAzione", Allarme.model().FILTRO.AZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_azione", Allarme.model().FILTRO.AZIONE.getFieldType()));
				object.setGroupBy(new AllarmeRaggruppamento());
				setParameter(object.getGroupBy(), "setEnabled", Allarme.model().GROUP_BY.ENABLED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_enabled", Allarme.model().GROUP_BY.ENABLED.getFieldType()));
				setParameter(object.getGroupBy(), "setRuoloPorta", Allarme.model().GROUP_BY.RUOLO_PORTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_ruolo", Allarme.model().GROUP_BY.RUOLO_PORTA.getFieldType()));
				setParameter(object.getGroupBy(), "setProtocollo", Allarme.model().GROUP_BY.PROTOCOLLO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_protocollo", Allarme.model().GROUP_BY.PROTOCOLLO.getFieldType()));
				setParameter(object.getGroupBy(), "setFruitore", Allarme.model().GROUP_BY.FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_fruitore", Allarme.model().GROUP_BY.FRUITORE.getFieldType()));
				setParameter(object.getGroupBy(), "setServizioApplicativoFruitore", Allarme.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_sa_fruitore", Allarme.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()));
				setParameter(object.getGroupBy(), "setIdentificativoAutenticato", Allarme.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_id_autenticato", Allarme.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO.getFieldType()));
				setParameter(object.getGroupBy(), "setToken", Allarme.model().GROUP_BY.TOKEN.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_token", Allarme.model().GROUP_BY.TOKEN.getFieldType()));
				setParameter(object.getGroupBy(), "setErogatore", Allarme.model().GROUP_BY.EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_erogatore", Allarme.model().GROUP_BY.EROGATORE.getFieldType()));
				setParameter(object.getGroupBy(), "setServizio", Allarme.model().GROUP_BY.SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_servizio", Allarme.model().GROUP_BY.SERVIZIO.getFieldType()));
				setParameter(object.getGroupBy(), "setAzione", Allarme.model().GROUP_BY.AZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "group_azione", Allarme.model().GROUP_BY.AZIONE.getFieldType()));
				return object;
			}
			if(model.equals(Allarme.model().ALLARME_PARAMETRO)){
				AllarmeParametro object = new AllarmeParametro();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "chk_param_id", Long.class));
				setParameter(object, "setIdParametro", Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "param_id", Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO.getFieldType()));
				setParameter(object, "setValore", Allarme.model().ALLARME_PARAMETRO.VALORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "param_value", Allarme.model().ALLARME_PARAMETRO.VALORE.getFieldType()));
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

			if(model.equals(Allarme.model())){
				Allarme object = new Allarme();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setNome", Allarme.model().NOME.getFieldType(),
					this.getObjectFromMap(map,"nome"));
				setParameter(object, "setTipo", Allarme.model().TIPO.getFieldType(),
					this.getObjectFromMap(map,"tipo"));
				setParameter(object, "set_value_tipoAllarme", String.class,
					this.getObjectFromMap(map,"tipo-allarme"));
				object.setMail(new AllarmeMail());
				setParameter(object.getMail(), "setAckMode", Allarme.model().MAIL.ACK_MODE.getFieldType(),
					this.getObjectFromMap(map,"mail.ack-mode"));
				setParameter(object.getMail(), "setInviaWarning", Allarme.model().MAIL.INVIA_WARNING.getFieldType(),
					this.getObjectFromMap(map,"mail.invia-warning"));
				setParameter(object.getMail(), "setInviaAlert", Allarme.model().MAIL.INVIA_ALERT.getFieldType(),
					this.getObjectFromMap(map,"mail.invia-alert"));
				setParameter(object.getMail(), "setDestinatari", Allarme.model().MAIL.DESTINATARI.getFieldType(),
					this.getObjectFromMap(map,"mail.destinatari"));
				setParameter(object.getMail(), "setSubject", Allarme.model().MAIL.SUBJECT.getFieldType(),
					this.getObjectFromMap(map,"mail.subject"));
				setParameter(object.getMail(), "setBody", Allarme.model().MAIL.BODY.getFieldType(),
					this.getObjectFromMap(map,"mail.body"));
				object.setScript(new AllarmeScript());
				setParameter(object.getScript(), "setAckMode", Allarme.model().SCRIPT.ACK_MODE.getFieldType(),
					this.getObjectFromMap(map,"script.ack-mode"));
				setParameter(object.getScript(), "setInvocaWarning", Allarme.model().SCRIPT.INVOCA_WARNING.getFieldType(),
					this.getObjectFromMap(map,"script.invoca-warning"));
				setParameter(object.getScript(), "setInvocaAlert", Allarme.model().SCRIPT.INVOCA_ALERT.getFieldType(),
					this.getObjectFromMap(map,"script.invoca-alert"));
				setParameter(object.getScript(), "setCommand", Allarme.model().SCRIPT.COMMAND.getFieldType(),
					this.getObjectFromMap(map,"script.command"));
				setParameter(object.getScript(), "setArgs", Allarme.model().SCRIPT.ARGS.getFieldType(),
					this.getObjectFromMap(map,"script.args"));
				setParameter(object, "setStatoPrecedente", Allarme.model().STATO_PRECEDENTE.getFieldType(),
					this.getObjectFromMap(map,"stato-precedente"));
				setParameter(object, "setStato", Allarme.model().STATO.getFieldType(),
					this.getObjectFromMap(map,"stato"));
				setParameter(object, "setDettaglioStato", Allarme.model().DETTAGLIO_STATO.getFieldType(),
					this.getObjectFromMap(map,"dettaglio-stato"));
				setParameter(object, "setLasttimestampCreate", Allarme.model().LASTTIMESTAMP_CREATE.getFieldType(),
					this.getObjectFromMap(map,"lasttimestamp-create"));
				setParameter(object, "setLasttimestampUpdate", Allarme.model().LASTTIMESTAMP_UPDATE.getFieldType(),
					this.getObjectFromMap(map,"lasttimestamp-update"));
				setParameter(object, "setEnabled", Allarme.model().ENABLED.getFieldType(),
					this.getObjectFromMap(map,"enabled"));
				setParameter(object, "setAcknowledged", Allarme.model().ACKNOWLEDGED.getFieldType(),
					this.getObjectFromMap(map,"acknowledged"));
				setParameter(object, "setTipoPeriodo", Allarme.model().TIPO_PERIODO.getFieldType(),
					this.getObjectFromMap(map,"tipo-periodo"));
				setParameter(object, "setPeriodo", Allarme.model().PERIODO.getFieldType(),
					this.getObjectFromMap(map,"periodo"));
				object.setFiltro(new AllarmeFiltro());
				setParameter(object.getFiltro(), "setEnabled", Allarme.model().FILTRO.ENABLED.getFieldType(),
					this.getObjectFromMap(map,"filtro.enabled"));
				setParameter(object.getFiltro(), "setProtocollo", Allarme.model().FILTRO.PROTOCOLLO.getFieldType(),
					this.getObjectFromMap(map,"filtro.protocollo"));
				setParameter(object.getFiltro(), "set_value_ruoloPorta", String.class,
					this.getObjectFromMap(map,"filtro.ruolo-porta"));
				setParameter(object.getFiltro(), "setNomePorta", Allarme.model().FILTRO.NOME_PORTA.getFieldType(),
					this.getObjectFromMap(map,"filtro.nome-porta"));
				setParameter(object.getFiltro(), "setTipoFruitore", Allarme.model().FILTRO.TIPO_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.tipo-fruitore"));
				setParameter(object.getFiltro(), "setNomeFruitore", Allarme.model().FILTRO.NOME_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.nome-fruitore"));
				setParameter(object.getFiltro(), "setRuoloFruitore", Allarme.model().FILTRO.RUOLO_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.ruolo-fruitore"));
				setParameter(object.getFiltro(), "setServizioApplicativoFruitore", Allarme.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.servizio-applicativo-fruitore"));
				setParameter(object.getFiltro(), "setTipoErogatore", Allarme.model().FILTRO.TIPO_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.tipo-erogatore"));
				setParameter(object.getFiltro(), "setNomeErogatore", Allarme.model().FILTRO.NOME_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.nome-erogatore"));
				setParameter(object.getFiltro(), "setRuoloErogatore", Allarme.model().FILTRO.RUOLO_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"filtro.ruolo-erogatore"));
				setParameter(object.getFiltro(), "setTag", Allarme.model().FILTRO.TAG.getFieldType(),
					this.getObjectFromMap(map,"filtro.tag"));
				setParameter(object.getFiltro(), "setTipoServizio", Allarme.model().FILTRO.TIPO_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"filtro.tipo-servizio"));
				setParameter(object.getFiltro(), "setNomeServizio", Allarme.model().FILTRO.NOME_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"filtro.nome-servizio"));
				setParameter(object.getFiltro(), "setVersioneServizio", Allarme.model().FILTRO.VERSIONE_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"filtro.versione-servizio"));
				setParameter(object.getFiltro(), "setAzione", Allarme.model().FILTRO.AZIONE.getFieldType(),
					this.getObjectFromMap(map,"filtro.azione"));
				object.setGroupBy(new AllarmeRaggruppamento());
				setParameter(object.getGroupBy(), "setEnabled", Allarme.model().GROUP_BY.ENABLED.getFieldType(),
					this.getObjectFromMap(map,"group-by.enabled"));
				setParameter(object.getGroupBy(), "setRuoloPorta", Allarme.model().GROUP_BY.RUOLO_PORTA.getFieldType(),
					this.getObjectFromMap(map,"group-by.ruolo-porta"));
				setParameter(object.getGroupBy(), "setProtocollo", Allarme.model().GROUP_BY.PROTOCOLLO.getFieldType(),
					this.getObjectFromMap(map,"group-by.protocollo"));
				setParameter(object.getGroupBy(), "setFruitore", Allarme.model().GROUP_BY.FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"group-by.fruitore"));
				setParameter(object.getGroupBy(), "setServizioApplicativoFruitore", Allarme.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"group-by.servizio-applicativo-fruitore"));
				setParameter(object.getGroupBy(), "setIdentificativoAutenticato", Allarme.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO.getFieldType(),
					this.getObjectFromMap(map,"group-by.identificativo-autenticato"));
				setParameter(object.getGroupBy(), "setToken", Allarme.model().GROUP_BY.TOKEN.getFieldType(),
					this.getObjectFromMap(map,"group-by.token"));
				setParameter(object.getGroupBy(), "setErogatore", Allarme.model().GROUP_BY.EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"group-by.erogatore"));
				setParameter(object.getGroupBy(), "setServizio", Allarme.model().GROUP_BY.SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"group-by.servizio"));
				setParameter(object.getGroupBy(), "setAzione", Allarme.model().GROUP_BY.AZIONE.getFieldType(),
					this.getObjectFromMap(map,"group-by.azione"));
				return object;
			}
			if(model.equals(Allarme.model().ALLARME_PARAMETRO)){
				AllarmeParametro object = new AllarmeParametro();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"allarme-parametro.id"));
				setParameter(object, "setIdParametro", Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO.getFieldType(),
					this.getObjectFromMap(map,"allarme-parametro.id-parametro"));
				setParameter(object, "setValore", Allarme.model().ALLARME_PARAMETRO.VALORE.getFieldType(),
					this.getObjectFromMap(map,"allarme-parametro.valore"));
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

			if(model.equals(Allarme.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject(CostantiDB.ALLARMI,"id","seq_allarmi","allarmi_init_seq");
			}
			if(model.equals(Allarme.model().ALLARME_PARAMETRO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject(CostantiDB.ALLARMI_PARAMETRI,"chk_param_id","seq_allarmi_parametri","allarmi_parametri_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
