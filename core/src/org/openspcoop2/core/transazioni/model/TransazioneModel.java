/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.transazioni.model;

import org.openspcoop2.core.transazioni.Transazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Transazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneModel extends AbstractModel<Transazione> {

	public TransazioneModel(){
	
		super();
	
		this.ID_TRANSAZIONE = new Field("id-transazione",java.lang.String.class,"transazione",Transazione.class);
		this.STATO = new Field("stato",java.lang.String.class,"transazione",Transazione.class);
		this.RUOLO_TRANSAZIONE = new Field("ruolo-transazione",int.class,"transazione",Transazione.class);
		this.ESITO = new Field("esito",int.class,"transazione",Transazione.class);
		this.ESITO_SINCRONO = new Field("esito-sincrono",int.class,"transazione",Transazione.class);
		this.CONSEGNE_MULTIPLE_IN_CORSO = new Field("consegne-multiple-in-corso",int.class,"transazione",Transazione.class);
		this.ESITO_CONTESTO = new Field("esito-contesto",java.lang.String.class,"transazione",Transazione.class);
		this.PROTOCOLLO = new Field("protocollo",java.lang.String.class,"transazione",Transazione.class);
		this.TIPO_RICHIESTA = new Field("tipo-richiesta",java.lang.String.class,"transazione",Transazione.class);
		this.CODICE_RISPOSTA_INGRESSO = new Field("codice-risposta-ingresso",java.lang.String.class,"transazione",Transazione.class);
		this.CODICE_RISPOSTA_USCITA = new Field("codice-risposta-uscita",java.lang.String.class,"transazione",Transazione.class);
		this.DATA_ACCETTAZIONE_RICHIESTA = new Field("data-accettazione-richiesta",java.util.Date.class,"transazione",Transazione.class);
		this.DATA_INGRESSO_RICHIESTA = new Field("data-ingresso-richiesta",java.util.Date.class,"transazione",Transazione.class);
		this.DATA_USCITA_RICHIESTA = new Field("data-uscita-richiesta",java.util.Date.class,"transazione",Transazione.class);
		this.DATA_ACCETTAZIONE_RISPOSTA = new Field("data-accettazione-risposta",java.util.Date.class,"transazione",Transazione.class);
		this.DATA_INGRESSO_RISPOSTA = new Field("data-ingresso-risposta",java.util.Date.class,"transazione",Transazione.class);
		this.DATA_USCITA_RISPOSTA = new Field("data-uscita-risposta",java.util.Date.class,"transazione",Transazione.class);
		this.RICHIESTA_INGRESSO_BYTES = new Field("richiesta-ingresso-bytes",java.lang.Long.class,"transazione",Transazione.class);
		this.RICHIESTA_USCITA_BYTES = new Field("richiesta-uscita-bytes",java.lang.Long.class,"transazione",Transazione.class);
		this.RISPOSTA_INGRESSO_BYTES = new Field("risposta-ingresso-bytes",java.lang.Long.class,"transazione",Transazione.class);
		this.RISPOSTA_USCITA_BYTES = new Field("risposta-uscita-bytes",java.lang.Long.class,"transazione",Transazione.class);
		this.PDD_CODICE = new Field("pdd-codice",java.lang.String.class,"transazione",Transazione.class);
		this.PDD_TIPO_SOGGETTO = new Field("pdd-tipo-soggetto",java.lang.String.class,"transazione",Transazione.class);
		this.PDD_NOME_SOGGETTO = new Field("pdd-nome-soggetto",java.lang.String.class,"transazione",Transazione.class);
		this.PDD_RUOLO = new Field("pdd-ruolo",java.lang.String.class,"transazione",Transazione.class);
		this.FAULT_INTEGRAZIONE = new Field("fault-integrazione",java.lang.String.class,"transazione",Transazione.class);
		this.FORMATO_FAULT_INTEGRAZIONE = new Field("formato-fault-integrazione",java.lang.String.class,"transazione",Transazione.class);
		this.FAULT_COOPERAZIONE = new Field("fault-cooperazione",java.lang.String.class,"transazione",Transazione.class);
		this.FORMATO_FAULT_COOPERAZIONE = new Field("formato-fault-cooperazione",java.lang.String.class,"transazione",Transazione.class);
		this.TIPO_SOGGETTO_FRUITORE = new Field("tipo-soggetto-fruitore",java.lang.String.class,"transazione",Transazione.class);
		this.NOME_SOGGETTO_FRUITORE = new Field("nome-soggetto-fruitore",java.lang.String.class,"transazione",Transazione.class);
		this.IDPORTA_SOGGETTO_FRUITORE = new Field("idporta-soggetto-fruitore",java.lang.String.class,"transazione",Transazione.class);
		this.INDIRIZZO_SOGGETTO_FRUITORE = new Field("indirizzo-soggetto-fruitore",java.lang.String.class,"transazione",Transazione.class);
		this.TIPO_SOGGETTO_EROGATORE = new Field("tipo-soggetto-erogatore",java.lang.String.class,"transazione",Transazione.class);
		this.NOME_SOGGETTO_EROGATORE = new Field("nome-soggetto-erogatore",java.lang.String.class,"transazione",Transazione.class);
		this.IDPORTA_SOGGETTO_EROGATORE = new Field("idporta-soggetto-erogatore",java.lang.String.class,"transazione",Transazione.class);
		this.INDIRIZZO_SOGGETTO_EROGATORE = new Field("indirizzo-soggetto-erogatore",java.lang.String.class,"transazione",Transazione.class);
		this.ID_MESSAGGIO_RICHIESTA = new Field("id-messaggio-richiesta",java.lang.String.class,"transazione",Transazione.class);
		this.ID_MESSAGGIO_RISPOSTA = new Field("id-messaggio-risposta",java.lang.String.class,"transazione",Transazione.class);
		this.DATA_ID_MSG_RICHIESTA = new Field("data-id-msg-richiesta",java.util.Date.class,"transazione",Transazione.class);
		this.DATA_ID_MSG_RISPOSTA = new Field("data-id-msg-risposta",java.util.Date.class,"transazione",Transazione.class);
		this.PROFILO_COLLABORAZIONE_OP_2 = new Field("profilo-collaborazione-op2",java.lang.String.class,"transazione",Transazione.class);
		this.PROFILO_COLLABORAZIONE_PROT = new Field("profilo-collaborazione-prot",java.lang.String.class,"transazione",Transazione.class);
		this.ID_COLLABORAZIONE = new Field("id-collaborazione",java.lang.String.class,"transazione",Transazione.class);
		this.URI_ACCORDO_SERVIZIO = new Field("uri-accordo-servizio",java.lang.String.class,"transazione",Transazione.class);
		this.TIPO_SERVIZIO = new Field("tipo-servizio",java.lang.String.class,"transazione",Transazione.class);
		this.NOME_SERVIZIO = new Field("nome-servizio",java.lang.String.class,"transazione",Transazione.class);
		this.VERSIONE_SERVIZIO = new Field("versione-servizio",int.class,"transazione",Transazione.class);
		this.AZIONE = new Field("azione",java.lang.String.class,"transazione",Transazione.class);
		this.ID_ASINCRONO = new Field("id-asincrono",java.lang.String.class,"transazione",Transazione.class);
		this.TIPO_SERVIZIO_CORRELATO = new Field("tipo-servizio-correlato",java.lang.String.class,"transazione",Transazione.class);
		this.NOME_SERVIZIO_CORRELATO = new Field("nome-servizio-correlato",java.lang.String.class,"transazione",Transazione.class);
		this.HEADER_PROTOCOLLO_RICHIESTA = new Field("header-protocollo-richiesta",java.lang.String.class,"transazione",Transazione.class);
		this.DIGEST_RICHIESTA = new Field("digest-richiesta",java.lang.String.class,"transazione",Transazione.class);
		this.PROTOCOLLO_EXT_INFO_RICHIESTA = new Field("protocollo-ext-info-richiesta",java.lang.String.class,"transazione",Transazione.class);
		this.HEADER_PROTOCOLLO_RISPOSTA = new Field("header-protocollo-risposta",java.lang.String.class,"transazione",Transazione.class);
		this.DIGEST_RISPOSTA = new Field("digest-risposta",java.lang.String.class,"transazione",Transazione.class);
		this.PROTOCOLLO_EXT_INFO_RISPOSTA = new Field("protocollo-ext-info-risposta",java.lang.String.class,"transazione",Transazione.class);
		this.TRACCIA_RICHIESTA = new Field("traccia-richiesta",java.lang.String.class,"transazione",Transazione.class);
		this.TRACCIA_RISPOSTA = new Field("traccia-risposta",java.lang.String.class,"transazione",Transazione.class);
		this.DIAGNOSTICI = new Field("diagnostici",java.lang.String.class,"transazione",Transazione.class);
		this.DIAGNOSTICI_LIST_1 = new Field("diagnostici-list1",java.lang.String.class,"transazione",Transazione.class);
		this.DIAGNOSTICI_LIST_2 = new Field("diagnostici-list2",java.lang.String.class,"transazione",Transazione.class);
		this.DIAGNOSTICI_LIST_EXT = new Field("diagnostici-list-ext",java.lang.String.class,"transazione",Transazione.class);
		this.DIAGNOSTICI_EXT = new Field("diagnostici-ext",java.lang.String.class,"transazione",Transazione.class);
		this.ERROR_LOG = new Field("error-log",java.lang.String.class,"transazione",Transazione.class);
		this.WARNING_LOG = new Field("warning-log",java.lang.String.class,"transazione",Transazione.class);
		this.ID_CORRELAZIONE_APPLICATIVA = new Field("id-correlazione-applicativa",java.lang.String.class,"transazione",Transazione.class);
		this.ID_CORRELAZIONE_APPLICATIVA_RISPOSTA = new Field("id-correlazione-applicativa-risposta",java.lang.String.class,"transazione",Transazione.class);
		this.SERVIZIO_APPLICATIVO_FRUITORE = new Field("servizio-applicativo-fruitore",java.lang.String.class,"transazione",Transazione.class);
		this.SERVIZIO_APPLICATIVO_EROGATORE = new Field("servizio-applicativo-erogatore",java.lang.String.class,"transazione",Transazione.class);
		this.OPERAZIONE_IM = new Field("operazione-im",java.lang.String.class,"transazione",Transazione.class);
		this.LOCATION_RICHIESTA = new Field("location-richiesta",java.lang.String.class,"transazione",Transazione.class);
		this.LOCATION_RISPOSTA = new Field("location-risposta",java.lang.String.class,"transazione",Transazione.class);
		this.NOME_PORTA = new Field("nome-porta",java.lang.String.class,"transazione",Transazione.class);
		this.CREDENZIALI = new Field("credenziali",java.lang.String.class,"transazione",Transazione.class);
		this.LOCATION_CONNETTORE = new Field("location-connettore",java.lang.String.class,"transazione",Transazione.class);
		this.URL_INVOCAZIONE = new Field("url-invocazione",java.lang.String.class,"transazione",Transazione.class);
		this.TRASPORTO_MITTENTE = new Field("trasporto-mittente",java.lang.String.class,"transazione",Transazione.class);
		this.TOKEN_ISSUER = new Field("token-issuer",java.lang.String.class,"transazione",Transazione.class);
		this.TOKEN_CLIENT_ID = new Field("token-client-id",java.lang.String.class,"transazione",Transazione.class);
		this.TOKEN_SUBJECT = new Field("token-subject",java.lang.String.class,"transazione",Transazione.class);
		this.TOKEN_USERNAME = new Field("token-username",java.lang.String.class,"transazione",Transazione.class);
		this.TOKEN_MAIL = new Field("token-mail",java.lang.String.class,"transazione",Transazione.class);
		this.TOKEN_INFO = new Field("token-info",java.lang.String.class,"transazione",Transazione.class);
		this.TEMPI_ELABORAZIONE = new Field("tempi-elaborazione",java.lang.String.class,"transazione",Transazione.class);
		this.DUPLICATI_RICHIESTA = new Field("duplicati-richiesta",int.class,"transazione",Transazione.class);
		this.DUPLICATI_RISPOSTA = new Field("duplicati-risposta",int.class,"transazione",Transazione.class);
		this.CLUSTER_ID = new Field("cluster-id",java.lang.String.class,"transazione",Transazione.class);
		this.SOCKET_CLIENT_ADDRESS = new Field("socket-client-address",java.lang.String.class,"transazione",Transazione.class);
		this.TRANSPORT_CLIENT_ADDRESS = new Field("transport-client-address",java.lang.String.class,"transazione",Transazione.class);
		this.CLIENT_ADDRESS = new Field("client-address",java.lang.String.class,"transazione",Transazione.class);
		this.EVENTI_GESTIONE = new Field("eventi-gestione",java.lang.String.class,"transazione",Transazione.class);
		this.TIPO_API = new Field("tipo-api",int.class,"transazione",Transazione.class);
		this.URI_API = new Field("uri-api",java.lang.String.class,"transazione",Transazione.class);
		this.GRUPPI = new Field("gruppi",java.lang.String.class,"transazione",Transazione.class);
		this.DUMP_MESSAGGIO = new org.openspcoop2.core.transazioni.model.DumpMessaggioModel(new Field("dump-messaggio",org.openspcoop2.core.transazioni.DumpMessaggio.class,"transazione",Transazione.class));
		this.TRANSAZIONE_APPLICATIVO_SERVER = new org.openspcoop2.core.transazioni.model.TransazioneApplicativoServerModel(new Field("transazione-applicativo-server",org.openspcoop2.core.transazioni.TransazioneApplicativoServer.class,"transazione",Transazione.class));
		this.TRANSAZIONE_EXTENDED_INFO = new org.openspcoop2.core.transazioni.model.TransazioneExtendedInfoModel(new Field("transazione-extended-info",org.openspcoop2.core.transazioni.TransazioneExtendedInfo.class,"transazione",Transazione.class));
	
	}
	
	public TransazioneModel(IField father){
	
		super(father);
	
		this.ID_TRANSAZIONE = new ComplexField(father,"id-transazione",java.lang.String.class,"transazione",Transazione.class);
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"transazione",Transazione.class);
		this.RUOLO_TRANSAZIONE = new ComplexField(father,"ruolo-transazione",int.class,"transazione",Transazione.class);
		this.ESITO = new ComplexField(father,"esito",int.class,"transazione",Transazione.class);
		this.ESITO_SINCRONO = new ComplexField(father,"esito-sincrono",int.class,"transazione",Transazione.class);
		this.CONSEGNE_MULTIPLE_IN_CORSO = new ComplexField(father,"consegne-multiple-in-corso",int.class,"transazione",Transazione.class);
		this.ESITO_CONTESTO = new ComplexField(father,"esito-contesto",java.lang.String.class,"transazione",Transazione.class);
		this.PROTOCOLLO = new ComplexField(father,"protocollo",java.lang.String.class,"transazione",Transazione.class);
		this.TIPO_RICHIESTA = new ComplexField(father,"tipo-richiesta",java.lang.String.class,"transazione",Transazione.class);
		this.CODICE_RISPOSTA_INGRESSO = new ComplexField(father,"codice-risposta-ingresso",java.lang.String.class,"transazione",Transazione.class);
		this.CODICE_RISPOSTA_USCITA = new ComplexField(father,"codice-risposta-uscita",java.lang.String.class,"transazione",Transazione.class);
		this.DATA_ACCETTAZIONE_RICHIESTA = new ComplexField(father,"data-accettazione-richiesta",java.util.Date.class,"transazione",Transazione.class);
		this.DATA_INGRESSO_RICHIESTA = new ComplexField(father,"data-ingresso-richiesta",java.util.Date.class,"transazione",Transazione.class);
		this.DATA_USCITA_RICHIESTA = new ComplexField(father,"data-uscita-richiesta",java.util.Date.class,"transazione",Transazione.class);
		this.DATA_ACCETTAZIONE_RISPOSTA = new ComplexField(father,"data-accettazione-risposta",java.util.Date.class,"transazione",Transazione.class);
		this.DATA_INGRESSO_RISPOSTA = new ComplexField(father,"data-ingresso-risposta",java.util.Date.class,"transazione",Transazione.class);
		this.DATA_USCITA_RISPOSTA = new ComplexField(father,"data-uscita-risposta",java.util.Date.class,"transazione",Transazione.class);
		this.RICHIESTA_INGRESSO_BYTES = new ComplexField(father,"richiesta-ingresso-bytes",java.lang.Long.class,"transazione",Transazione.class);
		this.RICHIESTA_USCITA_BYTES = new ComplexField(father,"richiesta-uscita-bytes",java.lang.Long.class,"transazione",Transazione.class);
		this.RISPOSTA_INGRESSO_BYTES = new ComplexField(father,"risposta-ingresso-bytes",java.lang.Long.class,"transazione",Transazione.class);
		this.RISPOSTA_USCITA_BYTES = new ComplexField(father,"risposta-uscita-bytes",java.lang.Long.class,"transazione",Transazione.class);
		this.PDD_CODICE = new ComplexField(father,"pdd-codice",java.lang.String.class,"transazione",Transazione.class);
		this.PDD_TIPO_SOGGETTO = new ComplexField(father,"pdd-tipo-soggetto",java.lang.String.class,"transazione",Transazione.class);
		this.PDD_NOME_SOGGETTO = new ComplexField(father,"pdd-nome-soggetto",java.lang.String.class,"transazione",Transazione.class);
		this.PDD_RUOLO = new ComplexField(father,"pdd-ruolo",java.lang.String.class,"transazione",Transazione.class);
		this.FAULT_INTEGRAZIONE = new ComplexField(father,"fault-integrazione",java.lang.String.class,"transazione",Transazione.class);
		this.FORMATO_FAULT_INTEGRAZIONE = new ComplexField(father,"formato-fault-integrazione",java.lang.String.class,"transazione",Transazione.class);
		this.FAULT_COOPERAZIONE = new ComplexField(father,"fault-cooperazione",java.lang.String.class,"transazione",Transazione.class);
		this.FORMATO_FAULT_COOPERAZIONE = new ComplexField(father,"formato-fault-cooperazione",java.lang.String.class,"transazione",Transazione.class);
		this.TIPO_SOGGETTO_FRUITORE = new ComplexField(father,"tipo-soggetto-fruitore",java.lang.String.class,"transazione",Transazione.class);
		this.NOME_SOGGETTO_FRUITORE = new ComplexField(father,"nome-soggetto-fruitore",java.lang.String.class,"transazione",Transazione.class);
		this.IDPORTA_SOGGETTO_FRUITORE = new ComplexField(father,"idporta-soggetto-fruitore",java.lang.String.class,"transazione",Transazione.class);
		this.INDIRIZZO_SOGGETTO_FRUITORE = new ComplexField(father,"indirizzo-soggetto-fruitore",java.lang.String.class,"transazione",Transazione.class);
		this.TIPO_SOGGETTO_EROGATORE = new ComplexField(father,"tipo-soggetto-erogatore",java.lang.String.class,"transazione",Transazione.class);
		this.NOME_SOGGETTO_EROGATORE = new ComplexField(father,"nome-soggetto-erogatore",java.lang.String.class,"transazione",Transazione.class);
		this.IDPORTA_SOGGETTO_EROGATORE = new ComplexField(father,"idporta-soggetto-erogatore",java.lang.String.class,"transazione",Transazione.class);
		this.INDIRIZZO_SOGGETTO_EROGATORE = new ComplexField(father,"indirizzo-soggetto-erogatore",java.lang.String.class,"transazione",Transazione.class);
		this.ID_MESSAGGIO_RICHIESTA = new ComplexField(father,"id-messaggio-richiesta",java.lang.String.class,"transazione",Transazione.class);
		this.ID_MESSAGGIO_RISPOSTA = new ComplexField(father,"id-messaggio-risposta",java.lang.String.class,"transazione",Transazione.class);
		this.DATA_ID_MSG_RICHIESTA = new ComplexField(father,"data-id-msg-richiesta",java.util.Date.class,"transazione",Transazione.class);
		this.DATA_ID_MSG_RISPOSTA = new ComplexField(father,"data-id-msg-risposta",java.util.Date.class,"transazione",Transazione.class);
		this.PROFILO_COLLABORAZIONE_OP_2 = new ComplexField(father,"profilo-collaborazione-op2",java.lang.String.class,"transazione",Transazione.class);
		this.PROFILO_COLLABORAZIONE_PROT = new ComplexField(father,"profilo-collaborazione-prot",java.lang.String.class,"transazione",Transazione.class);
		this.ID_COLLABORAZIONE = new ComplexField(father,"id-collaborazione",java.lang.String.class,"transazione",Transazione.class);
		this.URI_ACCORDO_SERVIZIO = new ComplexField(father,"uri-accordo-servizio",java.lang.String.class,"transazione",Transazione.class);
		this.TIPO_SERVIZIO = new ComplexField(father,"tipo-servizio",java.lang.String.class,"transazione",Transazione.class);
		this.NOME_SERVIZIO = new ComplexField(father,"nome-servizio",java.lang.String.class,"transazione",Transazione.class);
		this.VERSIONE_SERVIZIO = new ComplexField(father,"versione-servizio",int.class,"transazione",Transazione.class);
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"transazione",Transazione.class);
		this.ID_ASINCRONO = new ComplexField(father,"id-asincrono",java.lang.String.class,"transazione",Transazione.class);
		this.TIPO_SERVIZIO_CORRELATO = new ComplexField(father,"tipo-servizio-correlato",java.lang.String.class,"transazione",Transazione.class);
		this.NOME_SERVIZIO_CORRELATO = new ComplexField(father,"nome-servizio-correlato",java.lang.String.class,"transazione",Transazione.class);
		this.HEADER_PROTOCOLLO_RICHIESTA = new ComplexField(father,"header-protocollo-richiesta",java.lang.String.class,"transazione",Transazione.class);
		this.DIGEST_RICHIESTA = new ComplexField(father,"digest-richiesta",java.lang.String.class,"transazione",Transazione.class);
		this.PROTOCOLLO_EXT_INFO_RICHIESTA = new ComplexField(father,"protocollo-ext-info-richiesta",java.lang.String.class,"transazione",Transazione.class);
		this.HEADER_PROTOCOLLO_RISPOSTA = new ComplexField(father,"header-protocollo-risposta",java.lang.String.class,"transazione",Transazione.class);
		this.DIGEST_RISPOSTA = new ComplexField(father,"digest-risposta",java.lang.String.class,"transazione",Transazione.class);
		this.PROTOCOLLO_EXT_INFO_RISPOSTA = new ComplexField(father,"protocollo-ext-info-risposta",java.lang.String.class,"transazione",Transazione.class);
		this.TRACCIA_RICHIESTA = new ComplexField(father,"traccia-richiesta",java.lang.String.class,"transazione",Transazione.class);
		this.TRACCIA_RISPOSTA = new ComplexField(father,"traccia-risposta",java.lang.String.class,"transazione",Transazione.class);
		this.DIAGNOSTICI = new ComplexField(father,"diagnostici",java.lang.String.class,"transazione",Transazione.class);
		this.DIAGNOSTICI_LIST_1 = new ComplexField(father,"diagnostici-list1",java.lang.String.class,"transazione",Transazione.class);
		this.DIAGNOSTICI_LIST_2 = new ComplexField(father,"diagnostici-list2",java.lang.String.class,"transazione",Transazione.class);
		this.DIAGNOSTICI_LIST_EXT = new ComplexField(father,"diagnostici-list-ext",java.lang.String.class,"transazione",Transazione.class);
		this.DIAGNOSTICI_EXT = new ComplexField(father,"diagnostici-ext",java.lang.String.class,"transazione",Transazione.class);
		this.ERROR_LOG = new ComplexField(father,"error-log",java.lang.String.class,"transazione",Transazione.class);
		this.WARNING_LOG = new ComplexField(father,"warning-log",java.lang.String.class,"transazione",Transazione.class);
		this.ID_CORRELAZIONE_APPLICATIVA = new ComplexField(father,"id-correlazione-applicativa",java.lang.String.class,"transazione",Transazione.class);
		this.ID_CORRELAZIONE_APPLICATIVA_RISPOSTA = new ComplexField(father,"id-correlazione-applicativa-risposta",java.lang.String.class,"transazione",Transazione.class);
		this.SERVIZIO_APPLICATIVO_FRUITORE = new ComplexField(father,"servizio-applicativo-fruitore",java.lang.String.class,"transazione",Transazione.class);
		this.SERVIZIO_APPLICATIVO_EROGATORE = new ComplexField(father,"servizio-applicativo-erogatore",java.lang.String.class,"transazione",Transazione.class);
		this.OPERAZIONE_IM = new ComplexField(father,"operazione-im",java.lang.String.class,"transazione",Transazione.class);
		this.LOCATION_RICHIESTA = new ComplexField(father,"location-richiesta",java.lang.String.class,"transazione",Transazione.class);
		this.LOCATION_RISPOSTA = new ComplexField(father,"location-risposta",java.lang.String.class,"transazione",Transazione.class);
		this.NOME_PORTA = new ComplexField(father,"nome-porta",java.lang.String.class,"transazione",Transazione.class);
		this.CREDENZIALI = new ComplexField(father,"credenziali",java.lang.String.class,"transazione",Transazione.class);
		this.LOCATION_CONNETTORE = new ComplexField(father,"location-connettore",java.lang.String.class,"transazione",Transazione.class);
		this.URL_INVOCAZIONE = new ComplexField(father,"url-invocazione",java.lang.String.class,"transazione",Transazione.class);
		this.TRASPORTO_MITTENTE = new ComplexField(father,"trasporto-mittente",java.lang.String.class,"transazione",Transazione.class);
		this.TOKEN_ISSUER = new ComplexField(father,"token-issuer",java.lang.String.class,"transazione",Transazione.class);
		this.TOKEN_CLIENT_ID = new ComplexField(father,"token-client-id",java.lang.String.class,"transazione",Transazione.class);
		this.TOKEN_SUBJECT = new ComplexField(father,"token-subject",java.lang.String.class,"transazione",Transazione.class);
		this.TOKEN_USERNAME = new ComplexField(father,"token-username",java.lang.String.class,"transazione",Transazione.class);
		this.TOKEN_MAIL = new ComplexField(father,"token-mail",java.lang.String.class,"transazione",Transazione.class);
		this.TOKEN_INFO = new ComplexField(father,"token-info",java.lang.String.class,"transazione",Transazione.class);
		this.TEMPI_ELABORAZIONE = new ComplexField(father,"tempi-elaborazione",java.lang.String.class,"transazione",Transazione.class);
		this.DUPLICATI_RICHIESTA = new ComplexField(father,"duplicati-richiesta",int.class,"transazione",Transazione.class);
		this.DUPLICATI_RISPOSTA = new ComplexField(father,"duplicati-risposta",int.class,"transazione",Transazione.class);
		this.CLUSTER_ID = new ComplexField(father,"cluster-id",java.lang.String.class,"transazione",Transazione.class);
		this.SOCKET_CLIENT_ADDRESS = new ComplexField(father,"socket-client-address",java.lang.String.class,"transazione",Transazione.class);
		this.TRANSPORT_CLIENT_ADDRESS = new ComplexField(father,"transport-client-address",java.lang.String.class,"transazione",Transazione.class);
		this.CLIENT_ADDRESS = new ComplexField(father,"client-address",java.lang.String.class,"transazione",Transazione.class);
		this.EVENTI_GESTIONE = new ComplexField(father,"eventi-gestione",java.lang.String.class,"transazione",Transazione.class);
		this.TIPO_API = new ComplexField(father,"tipo-api",int.class,"transazione",Transazione.class);
		this.URI_API = new ComplexField(father,"uri-api",java.lang.String.class,"transazione",Transazione.class);
		this.GRUPPI = new ComplexField(father,"gruppi",java.lang.String.class,"transazione",Transazione.class);
		this.DUMP_MESSAGGIO = new org.openspcoop2.core.transazioni.model.DumpMessaggioModel(new ComplexField(father,"dump-messaggio",org.openspcoop2.core.transazioni.DumpMessaggio.class,"transazione",Transazione.class));
		this.TRANSAZIONE_APPLICATIVO_SERVER = new org.openspcoop2.core.transazioni.model.TransazioneApplicativoServerModel(new ComplexField(father,"transazione-applicativo-server",org.openspcoop2.core.transazioni.TransazioneApplicativoServer.class,"transazione",Transazione.class));
		this.TRANSAZIONE_EXTENDED_INFO = new org.openspcoop2.core.transazioni.model.TransazioneExtendedInfoModel(new ComplexField(father,"transazione-extended-info",org.openspcoop2.core.transazioni.TransazioneExtendedInfo.class,"transazione",Transazione.class));
	
	}
	
	

	public IField ID_TRANSAZIONE = null;
	 
	public IField STATO = null;
	 
	public IField RUOLO_TRANSAZIONE = null;
	 
	public IField ESITO = null;
	 
	public IField ESITO_SINCRONO = null;
	 
	public IField CONSEGNE_MULTIPLE_IN_CORSO = null;
	 
	public IField ESITO_CONTESTO = null;
	 
	public IField PROTOCOLLO = null;
	 
	public IField TIPO_RICHIESTA = null;
	 
	public IField CODICE_RISPOSTA_INGRESSO = null;
	 
	public IField CODICE_RISPOSTA_USCITA = null;
	 
	public IField DATA_ACCETTAZIONE_RICHIESTA = null;
	 
	public IField DATA_INGRESSO_RICHIESTA = null;
	 
	public IField DATA_USCITA_RICHIESTA = null;
	 
	public IField DATA_ACCETTAZIONE_RISPOSTA = null;
	 
	public IField DATA_INGRESSO_RISPOSTA = null;
	 
	public IField DATA_USCITA_RISPOSTA = null;
	 
	public IField RICHIESTA_INGRESSO_BYTES = null;
	 
	public IField RICHIESTA_USCITA_BYTES = null;
	 
	public IField RISPOSTA_INGRESSO_BYTES = null;
	 
	public IField RISPOSTA_USCITA_BYTES = null;
	 
	public IField PDD_CODICE = null;
	 
	public IField PDD_TIPO_SOGGETTO = null;
	 
	public IField PDD_NOME_SOGGETTO = null;
	 
	public IField PDD_RUOLO = null;
	 
	public IField FAULT_INTEGRAZIONE = null;
	 
	public IField FORMATO_FAULT_INTEGRAZIONE = null;
	 
	public IField FAULT_COOPERAZIONE = null;
	 
	public IField FORMATO_FAULT_COOPERAZIONE = null;
	 
	public IField TIPO_SOGGETTO_FRUITORE = null;
	 
	public IField NOME_SOGGETTO_FRUITORE = null;
	 
	public IField IDPORTA_SOGGETTO_FRUITORE = null;
	 
	public IField INDIRIZZO_SOGGETTO_FRUITORE = null;
	 
	public IField TIPO_SOGGETTO_EROGATORE = null;
	 
	public IField NOME_SOGGETTO_EROGATORE = null;
	 
	public IField IDPORTA_SOGGETTO_EROGATORE = null;
	 
	public IField INDIRIZZO_SOGGETTO_EROGATORE = null;
	 
	public IField ID_MESSAGGIO_RICHIESTA = null;
	 
	public IField ID_MESSAGGIO_RISPOSTA = null;
	 
	public IField DATA_ID_MSG_RICHIESTA = null;
	 
	public IField DATA_ID_MSG_RISPOSTA = null;
	 
	public IField PROFILO_COLLABORAZIONE_OP_2 = null;
	 
	public IField PROFILO_COLLABORAZIONE_PROT = null;
	 
	public IField ID_COLLABORAZIONE = null;
	 
	public IField URI_ACCORDO_SERVIZIO = null;
	 
	public IField TIPO_SERVIZIO = null;
	 
	public IField NOME_SERVIZIO = null;
	 
	public IField VERSIONE_SERVIZIO = null;
	 
	public IField AZIONE = null;
	 
	public IField ID_ASINCRONO = null;
	 
	public IField TIPO_SERVIZIO_CORRELATO = null;
	 
	public IField NOME_SERVIZIO_CORRELATO = null;
	 
	public IField HEADER_PROTOCOLLO_RICHIESTA = null;
	 
	public IField DIGEST_RICHIESTA = null;
	 
	public IField PROTOCOLLO_EXT_INFO_RICHIESTA = null;
	 
	public IField HEADER_PROTOCOLLO_RISPOSTA = null;
	 
	public IField DIGEST_RISPOSTA = null;
	 
	public IField PROTOCOLLO_EXT_INFO_RISPOSTA = null;
	 
	public IField TRACCIA_RICHIESTA = null;
	 
	public IField TRACCIA_RISPOSTA = null;
	 
	public IField DIAGNOSTICI = null;
	 
	public IField DIAGNOSTICI_LIST_1 = null;
	 
	public IField DIAGNOSTICI_LIST_2 = null;
	 
	public IField DIAGNOSTICI_LIST_EXT = null;
	 
	public IField DIAGNOSTICI_EXT = null;
	 
	public IField ERROR_LOG = null;
	 
	public IField WARNING_LOG = null;
	 
	public IField ID_CORRELAZIONE_APPLICATIVA = null;
	 
	public IField ID_CORRELAZIONE_APPLICATIVA_RISPOSTA = null;
	 
	public IField SERVIZIO_APPLICATIVO_FRUITORE = null;
	 
	public IField SERVIZIO_APPLICATIVO_EROGATORE = null;
	 
	public IField OPERAZIONE_IM = null;
	 
	public IField LOCATION_RICHIESTA = null;
	 
	public IField LOCATION_RISPOSTA = null;
	 
	public IField NOME_PORTA = null;
	 
	public IField CREDENZIALI = null;
	 
	public IField LOCATION_CONNETTORE = null;
	 
	public IField URL_INVOCAZIONE = null;
	 
	public IField TRASPORTO_MITTENTE = null;
	 
	public IField TOKEN_ISSUER = null;
	 
	public IField TOKEN_CLIENT_ID = null;
	 
	public IField TOKEN_SUBJECT = null;
	 
	public IField TOKEN_USERNAME = null;
	 
	public IField TOKEN_MAIL = null;
	 
	public IField TOKEN_INFO = null;
	 
	public IField TEMPI_ELABORAZIONE = null;
	 
	public IField DUPLICATI_RICHIESTA = null;
	 
	public IField DUPLICATI_RISPOSTA = null;
	 
	public IField CLUSTER_ID = null;
	 
	public IField SOCKET_CLIENT_ADDRESS = null;
	 
	public IField TRANSPORT_CLIENT_ADDRESS = null;
	 
	public IField CLIENT_ADDRESS = null;
	 
	public IField EVENTI_GESTIONE = null;
	 
	public IField TIPO_API = null;
	 
	public IField URI_API = null;
	 
	public IField GRUPPI = null;
	 
	public org.openspcoop2.core.transazioni.model.DumpMessaggioModel DUMP_MESSAGGIO = null;
	 
	public org.openspcoop2.core.transazioni.model.TransazioneApplicativoServerModel TRANSAZIONE_APPLICATIVO_SERVER = null;
	 
	public org.openspcoop2.core.transazioni.model.TransazioneExtendedInfoModel TRANSAZIONE_EXTENDED_INFO = null;
	 

	@Override
	public Class<Transazione> getModeledClass(){
		return Transazione.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}