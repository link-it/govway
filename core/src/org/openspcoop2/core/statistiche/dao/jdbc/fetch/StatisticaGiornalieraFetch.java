/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.statistiche.StatisticaContenuti;
import org.openspcoop2.core.statistiche.StatisticaGiornaliera;
import org.openspcoop2.core.statistiche.Statistica;


/**     
 * StatisticaGiornalieraFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticaGiornalieraFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(StatisticaGiornaliera.model())){
				StatisticaGiornaliera object = new StatisticaGiornaliera();
				object.setStatisticaBase(new Statistica());
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object.getStatisticaBase(), "setData", StatisticaGiornaliera.model().STATISTICA_BASE.DATA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data", StatisticaGiornaliera.model().STATISTICA_BASE.DATA.getFieldType()));
				setParameter(object.getStatisticaBase(), "setIdPorta", StatisticaGiornaliera.model().STATISTICA_BASE.ID_PORTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_porta", StatisticaGiornaliera.model().STATISTICA_BASE.ID_PORTA.getFieldType()));
				setParameter(object.getStatisticaBase(), "set_value_tipoPorta", String.class,
					jdbcParameterUtilities.readParameter(rs, "tipo_porta", StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_PORTA.getFieldType())+"");
				setParameter(object.getStatisticaBase(), "setTipoMittente", StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_MITTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_mittente", StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_MITTENTE.getFieldType()));
				setParameter(object.getStatisticaBase(), "setMittente", StatisticaGiornaliera.model().STATISTICA_BASE.MITTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "mittente", StatisticaGiornaliera.model().STATISTICA_BASE.MITTENTE.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTipoDestinatario", StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_DESTINATARIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_destinatario", StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_DESTINATARIO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setDestinatario", StatisticaGiornaliera.model().STATISTICA_BASE.DESTINATARIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "destinatario", StatisticaGiornaliera.model().STATISTICA_BASE.DESTINATARIO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTipoServizio", StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_servizio", StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_SERVIZIO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setServizio", StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "servizio", StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setVersioneServizio", StatisticaGiornaliera.model().STATISTICA_BASE.VERSIONE_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "versione_servizio", StatisticaGiornaliera.model().STATISTICA_BASE.VERSIONE_SERVIZIO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setAzione", StatisticaGiornaliera.model().STATISTICA_BASE.AZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "azione", StatisticaGiornaliera.model().STATISTICA_BASE.AZIONE.getFieldType()));
				setParameter(object.getStatisticaBase(), "setServizioApplicativo", StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "servizio_applicativo", StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTrasportoMittente", StatisticaGiornaliera.model().STATISTICA_BASE.TRASPORTO_MITTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "trasporto_mittente", StatisticaGiornaliera.model().STATISTICA_BASE.TRASPORTO_MITTENTE.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTokenIssuer", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_ISSUER.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_issuer", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_ISSUER.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTokenClientId", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_CLIENT_ID.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_client_id", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_CLIENT_ID.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTokenSubject", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_SUBJECT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_subject", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_SUBJECT.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTokenUsername", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_USERNAME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_username", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_USERNAME.getFieldType()));
				setParameter(object.getStatisticaBase(), "setTokenMail", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_MAIL.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_mail", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_MAIL.getFieldType()));
				setParameter(object.getStatisticaBase(), "setEsito", StatisticaGiornaliera.model().STATISTICA_BASE.ESITO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "esito", StatisticaGiornaliera.model().STATISTICA_BASE.ESITO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setEsitoContesto", StatisticaGiornaliera.model().STATISTICA_BASE.ESITO_CONTESTO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "esito_contesto", StatisticaGiornaliera.model().STATISTICA_BASE.ESITO_CONTESTO.getFieldType()));
				setParameter(object.getStatisticaBase(), "setClientAddress", StatisticaGiornaliera.model().STATISTICA_BASE.CLIENT_ADDRESS.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "client_address", StatisticaGiornaliera.model().STATISTICA_BASE.CLIENT_ADDRESS.getFieldType()));
				setParameter(object.getStatisticaBase(), "setGruppi", StatisticaGiornaliera.model().STATISTICA_BASE.GRUPPI.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "gruppi", StatisticaGiornaliera.model().STATISTICA_BASE.GRUPPI.getFieldType()));
				setParameter(object.getStatisticaBase(), "setUriApi", StatisticaGiornaliera.model().STATISTICA_BASE.URI_API.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "uri_api", StatisticaGiornaliera.model().STATISTICA_BASE.URI_API.getFieldType()));
				setParameter(object.getStatisticaBase(), "setNumeroTransazioni", StatisticaGiornaliera.model().STATISTICA_BASE.NUMERO_TRANSAZIONI.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "richieste", StatisticaGiornaliera.model().STATISTICA_BASE.NUMERO_TRANSAZIONI.getFieldType()));
				setParameter(object.getStatisticaBase(), "setDimensioniBytesBandaComplessiva", StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "bytes_banda_complessiva", StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()));
				setParameter(object.getStatisticaBase(), "setDimensioniBytesBandaInterna", StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "bytes_banda_interna", StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()));
				setParameter(object.getStatisticaBase(), "setDimensioniBytesBandaEsterna", StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "bytes_banda_esterna", StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()));
				setParameter(object.getStatisticaBase(), "setLatenzaTotale", StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_TOTALE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "latenza_totale", StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_TOTALE.getFieldType()));
				setParameter(object.getStatisticaBase(), "setLatenzaPorta", StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_PORTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "latenza_porta", StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_PORTA.getFieldType()));
				setParameter(object.getStatisticaBase(), "setLatenzaServizio", StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "latenza_servizio", StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_SERVIZIO.getFieldType()));
				return object;
			}
			if(model.equals(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI)){
				StatisticaContenuti object = new StatisticaContenuti();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setData", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DATA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DATA.getFieldType()));
				setParameter(object, "setRisorsaNome", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "risorsa_nome", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_NOME.getFieldType()));
				setParameter(object, "setRisorsaValore", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_VALORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "risorsa_valore", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_VALORE.getFieldType()));
				setParameter(object, "setFiltroNome1", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_1.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_1", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_1.getFieldType()));
				setParameter(object, "setFiltroValore1", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_1.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_1", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_1.getFieldType()));
				setParameter(object, "setFiltroNome2", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_2.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_2", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_2.getFieldType()));
				setParameter(object, "setFiltroValore2", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_2.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_2", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_2.getFieldType()));
				setParameter(object, "setFiltroNome3", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_3.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_3", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_3.getFieldType()));
				setParameter(object, "setFiltroValore3", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_3.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_3", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_3.getFieldType()));
				setParameter(object, "setFiltroNome4", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_4.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_4", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_4.getFieldType()));
				setParameter(object, "setFiltroValore4", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_4.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_4", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_4.getFieldType()));
				setParameter(object, "setFiltroNome5", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_5.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_5", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_5.getFieldType()));
				setParameter(object, "setFiltroValore5", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_5.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_5", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_5.getFieldType()));
				setParameter(object, "setFiltroNome6", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_6.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_6", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_6.getFieldType()));
				setParameter(object, "setFiltroValore6", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_6.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_6", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_6.getFieldType()));
				setParameter(object, "setFiltroNome7", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_7.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_7", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_7.getFieldType()));
				setParameter(object, "setFiltroValore7", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_7.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_7", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_7.getFieldType()));
				setParameter(object, "setFiltroNome8", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_8.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_8", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_8.getFieldType()));
				setParameter(object, "setFiltroValore8", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_8.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_8", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_8.getFieldType()));
				setParameter(object, "setFiltroNome9", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_9.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_9", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_9.getFieldType()));
				setParameter(object, "setFiltroValore9", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_9.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_9", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_9.getFieldType()));
				setParameter(object, "setFiltroNome10", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_10.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_nome_10", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_10.getFieldType()));
				setParameter(object, "setFiltroValore10", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_10.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "filtro_valore_10", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_10.getFieldType()));
				setParameter(object, "setNumeroTransazioni", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "richieste", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType()));
				setParameter(object, "setDimensioniBytesBandaComplessiva", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "bytes_banda_complessiva", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType()));
				setParameter(object, "setDimensioniBytesBandaInterna", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "bytes_banda_interna", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType()));
				setParameter(object, "setDimensioniBytesBandaEsterna", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "bytes_banda_esterna", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType()));
				setParameter(object, "setLatenzaTotale", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_TOTALE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "latenza_totale", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_TOTALE.getFieldType()));
				setParameter(object, "setLatenzaPorta", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_PORTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "latenza_porta", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_PORTA.getFieldType()));
				setParameter(object, "setLatenzaServizio", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "latenza_servizio", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_SERVIZIO.getFieldType()));
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

			if(model.equals(StatisticaGiornaliera.model())){
				StatisticaGiornaliera object = new StatisticaGiornaliera();
				object.setStatisticaBase(new Statistica());
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"statistica-base.id"));
				setParameter(object.getStatisticaBase(), "setData", StatisticaGiornaliera.model().STATISTICA_BASE.DATA.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.data"));
				setParameter(object.getStatisticaBase(), "setIdPorta", StatisticaGiornaliera.model().STATISTICA_BASE.ID_PORTA.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.id-porta"));
				setParameter(object.getStatisticaBase(), "set_value_tipoPorta", String.class,
					this.getObjectFromMap(map,"statistica-base.tipo-porta"));
				setParameter(object.getStatisticaBase(), "setTipoMittente", StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_MITTENTE.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.tipo-mittente"));
				setParameter(object.getStatisticaBase(), "setMittente", StatisticaGiornaliera.model().STATISTICA_BASE.MITTENTE.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.mittente"));
				setParameter(object.getStatisticaBase(), "setTipoDestinatario", StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_DESTINATARIO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.tipo-destinatario"));
				setParameter(object.getStatisticaBase(), "setDestinatario", StatisticaGiornaliera.model().STATISTICA_BASE.DESTINATARIO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.destinatario"));
				setParameter(object.getStatisticaBase(), "setTipoServizio", StatisticaGiornaliera.model().STATISTICA_BASE.TIPO_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.tipo-servizio"));
				setParameter(object.getStatisticaBase(), "setServizio", StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.servizio"));
				setParameter(object.getStatisticaBase(), "setVersioneServizio", StatisticaGiornaliera.model().STATISTICA_BASE.VERSIONE_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.versione-servizio"));
				setParameter(object.getStatisticaBase(), "setAzione", StatisticaGiornaliera.model().STATISTICA_BASE.AZIONE.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.azione"));
				setParameter(object.getStatisticaBase(), "setServizioApplicativo", StatisticaGiornaliera.model().STATISTICA_BASE.SERVIZIO_APPLICATIVO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.servizio-applicativo"));
				setParameter(object.getStatisticaBase(), "setTrasportoMittente", StatisticaGiornaliera.model().STATISTICA_BASE.TRASPORTO_MITTENTE.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.trasporto-mittente"));
				setParameter(object.getStatisticaBase(), "setTokenIssuer", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_ISSUER.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.token-issuer"));
				setParameter(object.getStatisticaBase(), "setTokenClientId", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_CLIENT_ID.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.token-client-id"));
				setParameter(object.getStatisticaBase(), "setTokenSubject", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_SUBJECT.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.token-subject"));
				setParameter(object.getStatisticaBase(), "setTokenUsername", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_USERNAME.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.token-username"));
				setParameter(object.getStatisticaBase(), "setTokenMail", StatisticaGiornaliera.model().STATISTICA_BASE.TOKEN_MAIL.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.token-mail"));
				setParameter(object.getStatisticaBase(), "setEsito", StatisticaGiornaliera.model().STATISTICA_BASE.ESITO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.esito"));
				setParameter(object.getStatisticaBase(), "setEsitoContesto", StatisticaGiornaliera.model().STATISTICA_BASE.ESITO_CONTESTO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.esito-contesto"));
				setParameter(object.getStatisticaBase(), "setClientAddress", StatisticaGiornaliera.model().STATISTICA_BASE.CLIENT_ADDRESS.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.client-address"));
				setParameter(object.getStatisticaBase(), "setGruppi", StatisticaGiornaliera.model().STATISTICA_BASE.GRUPPI.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.gruppi"));
				setParameter(object.getStatisticaBase(), "setUriApi", StatisticaGiornaliera.model().STATISTICA_BASE.URI_API.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.uri-api"));
				setParameter(object.getStatisticaBase(), "setNumeroTransazioni", StatisticaGiornaliera.model().STATISTICA_BASE.NUMERO_TRANSAZIONI.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.numero-transazioni"));
				setParameter(object.getStatisticaBase(), "setDimensioniBytesBandaComplessiva", StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.dimensioni-bytes-banda-complessiva"));
				setParameter(object.getStatisticaBase(), "setDimensioniBytesBandaInterna", StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.dimensioni-bytes-banda-interna"));
				setParameter(object.getStatisticaBase(), "setDimensioniBytesBandaEsterna", StatisticaGiornaliera.model().STATISTICA_BASE.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.dimensioni-bytes-banda-esterna"));
				setParameter(object.getStatisticaBase(), "setLatenzaTotale", StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_TOTALE.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.latenza-totale"));
				setParameter(object.getStatisticaBase(), "setLatenzaPorta", StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_PORTA.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.latenza-porta"));
				setParameter(object.getStatisticaBase(), "setLatenzaServizio", StatisticaGiornaliera.model().STATISTICA_BASE.LATENZA_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"statistica-base.latenza-servizio"));
				return object;
			}
			if(model.equals(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI)){
				StatisticaContenuti object = new StatisticaContenuti();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.id"));
				setParameter(object, "setData", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DATA.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.data"));
				setParameter(object, "setRisorsaNome", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_NOME.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.risorsa-nome"));
				setParameter(object, "setRisorsaValore", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.RISORSA_VALORE.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.risorsa-valore"));
				setParameter(object, "setFiltroNome1", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_1.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-nome-1"));
				setParameter(object, "setFiltroValore1", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_1.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-valore-1"));
				setParameter(object, "setFiltroNome2", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_2.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-nome-2"));
				setParameter(object, "setFiltroValore2", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_2.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-valore-2"));
				setParameter(object, "setFiltroNome3", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_3.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-nome-3"));
				setParameter(object, "setFiltroValore3", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_3.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-valore-3"));
				setParameter(object, "setFiltroNome4", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_4.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-nome-4"));
				setParameter(object, "setFiltroValore4", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_4.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-valore-4"));
				setParameter(object, "setFiltroNome5", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_5.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-nome-5"));
				setParameter(object, "setFiltroValore5", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_5.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-valore-5"));
				setParameter(object, "setFiltroNome6", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_6.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-nome-6"));
				setParameter(object, "setFiltroValore6", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_6.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-valore-6"));
				setParameter(object, "setFiltroNome7", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_7.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-nome-7"));
				setParameter(object, "setFiltroValore7", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_7.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-valore-7"));
				setParameter(object, "setFiltroNome8", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_8.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-nome-8"));
				setParameter(object, "setFiltroValore8", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_8.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-valore-8"));
				setParameter(object, "setFiltroNome9", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_9.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-nome-9"));
				setParameter(object, "setFiltroValore9", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_9.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-valore-9"));
				setParameter(object, "setFiltroNome10", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_NOME_10.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-nome-10"));
				setParameter(object, "setFiltroValore10", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.FILTRO_VALORE_10.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.filtro-valore-10"));
				setParameter(object, "setNumeroTransazioni", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.NUMERO_TRANSAZIONI.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.numero-transazioni"));
				setParameter(object, "setDimensioniBytesBandaComplessiva", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_COMPLESSIVA.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.dimensioni-bytes-banda-complessiva"));
				setParameter(object, "setDimensioniBytesBandaInterna", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_INTERNA.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.dimensioni-bytes-banda-interna"));
				setParameter(object, "setDimensioniBytesBandaEsterna", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.DIMENSIONI_BYTES_BANDA_ESTERNA.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.dimensioni-bytes-banda-esterna"));
				setParameter(object, "setLatenzaTotale", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_TOTALE.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.latenza-totale"));
				setParameter(object, "setLatenzaPorta", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_PORTA.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.latenza-porta"));
				setParameter(object, "setLatenzaServizio", StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.LATENZA_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"statistica-giornaliera-contenuti.latenza-servizio"));
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

			if(model.equals(StatisticaGiornaliera.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("statistiche_giornaliere","id","seq_statistiche_giornaliere","statistiche_giornaliere_init_seq");
			}
			if(model.equals(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("stat_giorni_contenuti","id","seq_stat_giorni_contenuti","stat_giorni_contenuti_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
