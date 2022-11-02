/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.statistiche.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.statistiche.StatisticaContenuti;
import org.openspcoop2.core.statistiche.StatisticaOraria;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.statistiche.Statistica;


/**     
 * StatisticaOrariaFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticaOrariaFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			GenericJDBCParameterUtilities jdbcParameterUtilities =  
					new GenericJDBCParameterUtilities(tipoDatabase);

			if(model.equals(StatisticaOraria.model())){
				StatisticaOraria object = new StatisticaOraria();
				object.setStatisticaBase(new Statistica());
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object.getStatisticaBase(), "setData", StatisticaOraria.model().STATISTICA_BASE.DATA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data", StatisticaOraria.model().STATISTICA_BASE.DATA.getFieldType()));
				setParameter(object.getStatisticaBase(), "setStatoRecord", StatisticaOraria.model().STATISTICA_BASE.STATO_RECORD.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "stato_record", StatisticaOraria.model().STATISTICA_BASE.STATO_RECORD.getFieldType()));
				setParameter(object.getStatisticaBase(), "setIdPorta", StatisticaOraria.model().STATISTICA_BASE.ID_PORTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_porta", StatisticaOraria.model().STATISTICA_BASE.ID_PORTA.getFieldType()));
				setParameter(object.getStatisticaBase(), "set_value_tipoPorta", String.class,
					jdbcParameterUtilities.readParameter(rs, "tipo_porta", StatisticaOraria.model().STATISTICA_BASE.TIPO_PORTA.getFieldType())+"");
				setParameter(object.getStatisticaBase(), "setTipoMittente", StatisticaOraria.model().STATISTICA_BASE.TIPO_MITTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_mittente", StatisticaOraria.model().STATISTICA_BASE.TIPO_MITTENTE.getFieldType()));
				setParameter(object.getStatisticaBase(), "setMittente", StatisticaOraria.model().STATISTICA_BASE.MITTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "mittente", StatisticaOraria.model().STATISTICA_BASE.MITTENTE.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTipoDestinatario", StatisticaOraria.model().STATISTICA_BASE.TIPO_DESTINATARIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_destinatario", StatisticaOraria.model().STATISTICA_BASE.TIPO_DESTINATARIO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setDestinatario", StatisticaOraria.model().STATISTICA_BASE.DESTINATARIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "destinatario", StatisticaOraria.model().STATISTICA_BASE.DESTINATARIO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTipoServizio", StatisticaOraria.model().STATISTICA_BASE.TIPO_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_servizio", StatisticaOraria.model().STATISTICA_BASE.TIPO_SERVIZIO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setServizio", StatisticaOraria.model().STATISTICA_BASE.SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "servizio", StatisticaOraria.model().STATISTICA_BASE.SERVIZIO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setVersioneServizio", StatisticaOraria.model().STATISTICA_BASE.VERSIONE_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "versione_servizio", StatisticaOraria.model().STATISTICA_BASE.VERSIONE_SERVIZIO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setAzione", StatisticaOraria.model().STATISTICA_BASE.AZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "azione", StatisticaOraria.model().STATISTICA_BASE.AZIONE.getFieldType()));
				setParameter(object.getStatisticaBase(), "setServizioApplicativo", StatisticaOraria.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "servizio_applicativo", StatisticaOraria.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTrasportoMittente", StatisticaOraria.model().STATISTICA_BASE.TRASPORTO_MITTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "trasporto_mittente", StatisticaOraria.model().STATISTICA_BASE.TRASPORTO_MITTENTE.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTokenIssuer", StatisticaOraria.model().STATISTICA_BASE.TOKEN_ISSUER.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_issuer", StatisticaOraria.model().STATISTICA_BASE.TOKEN_ISSUER.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTokenClientId", StatisticaOraria.model().STATISTICA_BASE.TOKEN_CLIENT_ID.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_client_id", StatisticaOraria.model().STATISTICA_BASE.TOKEN_CLIENT_ID.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTokenSubject", StatisticaOraria.model().STATISTICA_BASE.TOKEN_SUBJECT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_subject", StatisticaOraria.model().STATISTICA_BASE.TOKEN_SUBJECT.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTokenUsername", StatisticaOraria.model().STATISTICA_BASE.TOKEN_USERNAME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_username", StatisticaOraria.model().STATISTICA_BASE.TOKEN_USERNAME.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTokenMail", StatisticaOraria.model().STATISTICA_BASE.TOKEN_MAIL.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_mail", StatisticaOraria.model().STATISTICA_BASE.TOKEN_MAIL.getFieldType()));
				setParameter(object.getStatisticaBase(), "setEsito", StatisticaOraria.model().STATISTICA_BASE.ESITO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "esito", StatisticaOraria.model().STATISTICA_BASE.ESITO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setEsitoContesto", StatisticaOraria.model().STATISTICA_BASE.ESITO_CONTESTO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "esito_contesto", StatisticaOraria.model().STATISTICA_BASE.ESITO_CONTESTO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setClientAddress", StatisticaOraria.model().STATISTICA_BASE.CLIENT_ADDRESS.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "client_address", StatisticaOraria.model().STATISTICA_BASE.CLIENT_ADDRESS.getFieldType()));
				setParameter(object.getStatisticaBase(), "setGruppi", StatisticaOraria.model().STATISTICA_BASE.GRUPPI.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "gruppi", StatisticaOraria.model().STATISTICA_BASE.GRUPPI.getFieldType()));
				setParameter(object.getStatisticaBase(), "setUriApi", StatisticaOraria.model().STATISTICA_BASE.URI_API.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "uri_api", StatisticaOraria.model().STATISTICA_BASE.URI_API.getFieldType()));
				setParameter(object.getStatisticaBase(), "setClusterId", StatisticaOraria.model().STATISTICA_BASE.CLUSTER_ID.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cluster_id", StatisticaOraria.model().STATISTICA_BASE.CLUSTER_ID.getFieldType()));
				setParameter(object.getStatisticaBase(), "setNumeroTransazioni", StatisticaOraria.model().STATISTICA_BASE.NUMERO_TRANSAZIONI.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "richieste", StatisticaOraria.model().STATISTICA_BASE.NUMERO_TRANSAZIONI.getFieldType()));
				setParameter(object.getStatisticaBase(), "setDimensioniBytesBandaComplessiva", StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "bytes_banda_complessiva", StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()));
				setParameter(object.getStatisticaBase(), "setDimensioniBytesBandaInterna", StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "bytes_banda_interna", StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()));
				setParameter(object.getStatisticaBase(), "setDimensioniBytesBandaEsterna", StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "bytes_banda_esterna", StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()));
				setParameter(object.getStatisticaBase(), "setLatenzaTotale", StatisticaOraria.model().STATISTICA_BASE.LATENZA_TOTALE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "latenza_totale", StatisticaOraria.model().STATISTICA_BASE.LATENZA_TOTALE.getFieldType()));
				setParameter(object.getStatisticaBase(), "setLatenzaPorta", StatisticaOraria.model().STATISTICA_BASE.LATENZA_PORTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "latenza_porta", StatisticaOraria.model().STATISTICA_BASE.LATENZA_PORTA.getFieldType()));
				setParameter(object.getStatisticaBase(), "setLatenzaServizio", StatisticaOraria.model().STATISTICA_BASE.LATENZA_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "latenza_servizio", StatisticaOraria.model().STATISTICA_BASE.LATENZA_SERVIZIO.getFieldType()));
				return object;
			}
			if(model.equals(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI)){
				StatisticaContenuti object = new StatisticaContenuti();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setData", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DATA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DATA.getFieldType()));
				setParameter(object, "setRisorsaNome", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "risorsa_nome", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_NOME.getFieldType()));
				setParameter(object, "setRisorsaValore", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_VALORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "risorsa_valore", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_VALORE.getFieldType()));
				setParameter(object, "setFiltroNome1", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_1.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_1", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_1.getFieldType()));
				setParameter(object, "setFiltroValore1", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_1.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_1", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_1.getFieldType()));
				setParameter(object, "setFiltroNome2", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_2.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_2", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_2.getFieldType()));
				setParameter(object, "setFiltroValore2", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_2.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_2", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_2.getFieldType()));
				setParameter(object, "setFiltroNome3", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_3.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_3", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_3.getFieldType()));
				setParameter(object, "setFiltroValore3", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_3.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_3", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_3.getFieldType()));
				setParameter(object, "setFiltroNome4", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_4.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_4", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_4.getFieldType()));
				setParameter(object, "setFiltroValore4", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_4.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_4", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_4.getFieldType()));
				setParameter(object, "setFiltroNome5", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_5.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_5", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_5.getFieldType()));
				setParameter(object, "setFiltroValore5", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_5.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_5", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_5.getFieldType()));
				setParameter(object, "setFiltroNome6", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_6.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_6", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_6.getFieldType()));
				setParameter(object, "setFiltroValore6", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_6.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_6", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_6.getFieldType()));
				setParameter(object, "setFiltroNome7", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_7.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_7", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_7.getFieldType()));
				setParameter(object, "setFiltroValore7", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_7.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_7", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_7.getFieldType()));
				setParameter(object, "setFiltroNome8", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_8.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_8", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_8.getFieldType()));
				setParameter(object, "setFiltroValore8", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_8.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_8", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_8.getFieldType()));
				setParameter(object, "setFiltroNome9", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_9.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_9", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_9.getFieldType()));
				setParameter(object, "setFiltroValore9", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_9.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_9", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_9.getFieldType()));
				setParameter(object, "setFiltroNome10", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_10.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_10", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_10.getFieldType()));
				setParameter(object, "setFiltroValore10", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_10.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_10", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_10.getFieldType()));
				setParameter(object, "setNumeroTransazioni", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "richieste", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType()));
				setParameter(object, "setDimensioniBytesBandaComplessiva", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "bytes_banda_complessiva", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()));
				setParameter(object, "setDimensioniBytesBandaInterna", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "bytes_banda_interna", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()));
				setParameter(object, "setDimensioniBytesBandaEsterna", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "bytes_banda_esterna", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()));
				setParameter(object, "setLatenzaTotale", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_TOTALE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "latenza_totale", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_TOTALE.getFieldType()));
				setParameter(object, "setLatenzaPorta", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_PORTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "latenza_porta", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_PORTA.getFieldType()));
				setParameter(object, "setLatenzaServizio", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "latenza_servizio", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_SERVIZIO.getFieldType()));
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

			if(model.equals(StatisticaOraria.model())){
				StatisticaOraria object = new StatisticaOraria();
				object.setStatisticaBase(new Statistica());
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"statistica-base.id"));
				setParameter(object.getStatisticaBase(), "setData", StatisticaOraria.model().STATISTICA_BASE.DATA.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.data"));
				setParameter(object.getStatisticaBase(), "setStatoRecord", StatisticaOraria.model().STATISTICA_BASE.STATO_RECORD.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.stato-record"));
				setParameter(object.getStatisticaBase(), "setIdPorta", StatisticaOraria.model().STATISTICA_BASE.ID_PORTA.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.id-porta"));
				setParameter(object.getStatisticaBase(), "set_value_tipoPorta", String.class,
					this.getObjectFromMap(map,"statistica-base.tipo-porta"));
				setParameter(object.getStatisticaBase(), "setTipoMittente", StatisticaOraria.model().STATISTICA_BASE.TIPO_MITTENTE.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.tipo-mittente"));
				setParameter(object.getStatisticaBase(), "setMittente", StatisticaOraria.model().STATISTICA_BASE.MITTENTE.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.mittente"));
				setParameter(object.getStatisticaBase(), "setTipoDestinatario", StatisticaOraria.model().STATISTICA_BASE.TIPO_DESTINATARIO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.tipo-destinatario"));
				setParameter(object.getStatisticaBase(), "setDestinatario", StatisticaOraria.model().STATISTICA_BASE.DESTINATARIO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.destinatario"));
				setParameter(object.getStatisticaBase(), "setTipoServizio", StatisticaOraria.model().STATISTICA_BASE.TIPO_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.tipo-servizio"));
				setParameter(object.getStatisticaBase(), "setServizio", StatisticaOraria.model().STATISTICA_BASE.SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.servizio"));
				setParameter(object.getStatisticaBase(), "setVersioneServizio", StatisticaOraria.model().STATISTICA_BASE.VERSIONE_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.versione-servizio"));
				setParameter(object.getStatisticaBase(), "setAzione", StatisticaOraria.model().STATISTICA_BASE.AZIONE.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.azione"));
				setParameter(object.getStatisticaBase(), "setServizioApplicativo", StatisticaOraria.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.servizio-applicativo"));
				setParameter(object.getStatisticaBase(), "setTrasportoMittente", StatisticaOraria.model().STATISTICA_BASE.TRASPORTO_MITTENTE.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.trasporto-mittente"));
				setParameter(object.getStatisticaBase(), "setTokenIssuer", StatisticaOraria.model().STATISTICA_BASE.TOKEN_ISSUER.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.token-issuer"));
				setParameter(object.getStatisticaBase(), "setTokenClientId", StatisticaOraria.model().STATISTICA_BASE.TOKEN_CLIENT_ID.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.token-client-id"));
				setParameter(object.getStatisticaBase(), "setTokenSubject", StatisticaOraria.model().STATISTICA_BASE.TOKEN_SUBJECT.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.token-subject"));
				setParameter(object.getStatisticaBase(), "setTokenUsername", StatisticaOraria.model().STATISTICA_BASE.TOKEN_USERNAME.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.token-username"));
				setParameter(object.getStatisticaBase(), "setTokenMail", StatisticaOraria.model().STATISTICA_BASE.TOKEN_MAIL.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.token-mail"));
				setParameter(object.getStatisticaBase(), "setEsito", StatisticaOraria.model().STATISTICA_BASE.ESITO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.esito"));
				setParameter(object.getStatisticaBase(), "setEsitoContesto", StatisticaOraria.model().STATISTICA_BASE.ESITO_CONTESTO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.esito-contesto"));
				setParameter(object.getStatisticaBase(), "setClientAddress", StatisticaOraria.model().STATISTICA_BASE.CLIENT_ADDRESS.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.client-address"));
				setParameter(object.getStatisticaBase(), "setGruppi", StatisticaOraria.model().STATISTICA_BASE.GRUPPI.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.gruppi"));
				setParameter(object.getStatisticaBase(), "setUriApi", StatisticaOraria.model().STATISTICA_BASE.URI_API.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.uri-api"));
				setParameter(object.getStatisticaBase(), "setClusterId", StatisticaOraria.model().STATISTICA_BASE.CLUSTER_ID.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.cluster-id"));
				setParameter(object.getStatisticaBase(), "setNumeroTransazioni", StatisticaOraria.model().STATISTICA_BASE.NUMERO_TRANSAZIONI.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.numero-transazioni"));
				setParameter(object.getStatisticaBase(), "setDimensioniBytesBandaComplessiva", StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.dimensioni-bytes-banda-complessiva"));
				setParameter(object.getStatisticaBase(), "setDimensioniBytesBandaInterna", StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.dimensioni-bytes-banda-interna"));
				setParameter(object.getStatisticaBase(), "setDimensioniBytesBandaEsterna", StatisticaOraria.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.dimensioni-bytes-banda-esterna"));
				setParameter(object.getStatisticaBase(), "setLatenzaTotale", StatisticaOraria.model().STATISTICA_BASE.LATENZA_TOTALE.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.latenza-totale"));
				setParameter(object.getStatisticaBase(), "setLatenzaPorta", StatisticaOraria.model().STATISTICA_BASE.LATENZA_PORTA.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.latenza-porta"));
				setParameter(object.getStatisticaBase(), "setLatenzaServizio", StatisticaOraria.model().STATISTICA_BASE.LATENZA_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.latenza-servizio"));
				return object;
			}
			if(model.equals(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI)){
				StatisticaContenuti object = new StatisticaContenuti();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"statistica-oraria-contenuti.id"));
				setParameter(object, "setData", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DATA.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.data"));
				setParameter(object, "setRisorsaNome", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_NOME.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.risorsa-nome"));
				setParameter(object, "setRisorsaValore", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.RISORSA_VALORE.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.risorsa-valore"));
				setParameter(object, "setFiltroNome1", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_1.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-nome-1"));
				setParameter(object, "setFiltroValore1", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_1.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-valore-1"));
				setParameter(object, "setFiltroNome2", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_2.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-nome-2"));
				setParameter(object, "setFiltroValore2", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_2.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-valore-2"));
				setParameter(object, "setFiltroNome3", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_3.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-nome-3"));
				setParameter(object, "setFiltroValore3", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_3.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-valore-3"));
				setParameter(object, "setFiltroNome4", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_4.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-nome-4"));
				setParameter(object, "setFiltroValore4", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_4.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-valore-4"));
				setParameter(object, "setFiltroNome5", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_5.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-nome-5"));
				setParameter(object, "setFiltroValore5", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_5.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-valore-5"));
				setParameter(object, "setFiltroNome6", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_6.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-nome-6"));
				setParameter(object, "setFiltroValore6", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_6.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-valore-6"));
				setParameter(object, "setFiltroNome7", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_7.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-nome-7"));
				setParameter(object, "setFiltroValore7", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_7.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-valore-7"));
				setParameter(object, "setFiltroNome8", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_8.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-nome-8"));
				setParameter(object, "setFiltroValore8", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_8.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-valore-8"));
				setParameter(object, "setFiltroNome9", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_9.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-nome-9"));
				setParameter(object, "setFiltroValore9", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_9.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-valore-9"));
				setParameter(object, "setFiltroNome10", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_NOME_10.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-nome-10"));
				setParameter(object, "setFiltroValore10", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.FILTRO_VALORE_10.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.filtro-valore-10"));
				setParameter(object, "setNumeroTransazioni", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.numero-transazioni"));
				setParameter(object, "setDimensioniBytesBandaComplessiva", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.dimensioni-bytes-banda-complessiva"));
				setParameter(object, "setDimensioniBytesBandaInterna", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.dimensioni-bytes-banda-interna"));
				setParameter(object, "setDimensioniBytesBandaEsterna", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.dimensioni-bytes-banda-esterna"));
				setParameter(object, "setLatenzaTotale", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_TOTALE.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.latenza-totale"));
				setParameter(object, "setLatenzaPorta", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_PORTA.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.latenza-porta"));
				setParameter(object, "setLatenzaServizio", StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.LATENZA_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"statistica-oraria-contenuti.latenza-servizio"));
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

			if(model.equals(StatisticaOraria.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject(CostantiDB.STATISTICHE_ORARIE,"id","seq_"+CostantiDB.STATISTICHE_ORARIE,CostantiDB.STATISTICHE_ORARIE+"_init_seq");
			}
			if(model.equals(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject(CostantiDB.STATISTICHE_ORARIE_CONTENUTI,"id","seq_"+CostantiDB.STATISTICHE_ORARIE_CONTENUTI,CostantiDB.STATISTICHE_ORARIE_CONTENUTI+"_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
