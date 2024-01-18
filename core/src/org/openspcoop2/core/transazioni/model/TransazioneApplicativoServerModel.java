/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TransazioneApplicativoServer 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneApplicativoServerModel extends AbstractModel<TransazioneApplicativoServer> {

	public TransazioneApplicativoServerModel(){
	
		super();
	
		this.ID_TRANSAZIONE = new Field("id-transazione",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.SERVIZIO_APPLICATIVO_EROGATORE = new Field("servizio-applicativo-erogatore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CONNETTORE_NOME = new Field("connettore-nome",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_REGISTRAZIONE = new Field("data-registrazione",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.PROTOCOLLO = new Field("protocollo",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CONSEGNA_TERMINATA = new Field("consegna-terminata",boolean.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_MESSAGGIO_SCADUTO = new Field("data-messaggio-scaduto",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DETTAGLIO_ESITO = new Field("dettaglio-esito",int.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CONSEGNA_TRASPARENTE = new Field("consegna-trasparente",boolean.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CONSEGNA_INTEGRATION_MANAGER = new Field("consegna-integration-manager",boolean.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.IDENTIFICATIVO_MESSAGGIO = new Field("identificativo-messaggio",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_ACCETTAZIONE_RICHIESTA = new Field("data-accettazione-richiesta",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_USCITA_RICHIESTA = new Field("data-uscita-richiesta",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_USCITA_RICHIESTA_STREAM = new Field("data-uscita-richiesta-stream",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_ACCETTAZIONE_RISPOSTA = new Field("data-accettazione-risposta",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_INGRESSO_RISPOSTA = new Field("data-ingresso-risposta",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_INGRESSO_RISPOSTA_STREAM = new Field("data-ingresso-risposta-stream",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.RICHIESTA_USCITA_BYTES = new Field("richiesta-uscita-bytes",java.lang.Long.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.RISPOSTA_INGRESSO_BYTES = new Field("risposta-ingresso-bytes",java.lang.Long.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.LOCATION_CONNETTORE = new Field("location-connettore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CODICE_RISPOSTA = new Field("codice-risposta",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.FAULT = new Field("fault",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.FORMATO_FAULT = new Field("formato-fault",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_PRIMO_TENTATIVO = new Field("data-primo-tentativo",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.NUMERO_TENTATIVI = new Field("numero-tentativi",int.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CLUSTER_ID_PRESA_IN_CARICO = new Field("cluster-id-presa-in-carico",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CLUSTER_ID_CONSEGNA = new Field("cluster-id-consegna",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_ULTIMO_ERRORE = new Field("data-ultimo-errore",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DETTAGLIO_ESITO_ULTIMO_ERRORE = new Field("dettaglio-esito-ultimo-errore",int.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CODICE_RISPOSTA_ULTIMO_ERRORE = new Field("codice-risposta-ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.ULTIMO_ERRORE = new Field("ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.LOCATION_ULTIMO_ERRORE = new Field("location-ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CLUSTER_ID_ULTIMO_ERRORE = new Field("cluster-id-ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.FAULT_ULTIMO_ERRORE = new Field("fault-ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.FORMATO_FAULT_ULTIMO_ERRORE = new Field("formato-fault-ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_PRIMO_PRELIEVO_IM = new Field("data-primo-prelievo-im",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_PRELIEVO_IM = new Field("data-prelievo-im",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.NUMERO_PRELIEVI_IM = new Field("numero-prelievi-im",int.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_ELIMINAZIONE_IM = new Field("data-eliminazione-im",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CLUSTER_ID_PRELIEVO_IM = new Field("cluster-id-prelievo-im",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CLUSTER_ID_ELIMINAZIONE_IM = new Field("cluster-id-eliminazione-im",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
	
	}
	
	public TransazioneApplicativoServerModel(IField father){
	
		super(father);
	
		this.ID_TRANSAZIONE = new ComplexField(father,"id-transazione",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.SERVIZIO_APPLICATIVO_EROGATORE = new ComplexField(father,"servizio-applicativo-erogatore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CONNETTORE_NOME = new ComplexField(father,"connettore-nome",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_REGISTRAZIONE = new ComplexField(father,"data-registrazione",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.PROTOCOLLO = new ComplexField(father,"protocollo",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CONSEGNA_TERMINATA = new ComplexField(father,"consegna-terminata",boolean.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_MESSAGGIO_SCADUTO = new ComplexField(father,"data-messaggio-scaduto",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DETTAGLIO_ESITO = new ComplexField(father,"dettaglio-esito",int.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CONSEGNA_TRASPARENTE = new ComplexField(father,"consegna-trasparente",boolean.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CONSEGNA_INTEGRATION_MANAGER = new ComplexField(father,"consegna-integration-manager",boolean.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.IDENTIFICATIVO_MESSAGGIO = new ComplexField(father,"identificativo-messaggio",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_ACCETTAZIONE_RICHIESTA = new ComplexField(father,"data-accettazione-richiesta",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_USCITA_RICHIESTA = new ComplexField(father,"data-uscita-richiesta",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_USCITA_RICHIESTA_STREAM = new ComplexField(father,"data-uscita-richiesta-stream",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_ACCETTAZIONE_RISPOSTA = new ComplexField(father,"data-accettazione-risposta",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_INGRESSO_RISPOSTA = new ComplexField(father,"data-ingresso-risposta",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_INGRESSO_RISPOSTA_STREAM = new ComplexField(father,"data-ingresso-risposta-stream",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.RICHIESTA_USCITA_BYTES = new ComplexField(father,"richiesta-uscita-bytes",java.lang.Long.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.RISPOSTA_INGRESSO_BYTES = new ComplexField(father,"risposta-ingresso-bytes",java.lang.Long.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.LOCATION_CONNETTORE = new ComplexField(father,"location-connettore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CODICE_RISPOSTA = new ComplexField(father,"codice-risposta",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.FAULT = new ComplexField(father,"fault",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.FORMATO_FAULT = new ComplexField(father,"formato-fault",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_PRIMO_TENTATIVO = new ComplexField(father,"data-primo-tentativo",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.NUMERO_TENTATIVI = new ComplexField(father,"numero-tentativi",int.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CLUSTER_ID_PRESA_IN_CARICO = new ComplexField(father,"cluster-id-presa-in-carico",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CLUSTER_ID_CONSEGNA = new ComplexField(father,"cluster-id-consegna",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_ULTIMO_ERRORE = new ComplexField(father,"data-ultimo-errore",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DETTAGLIO_ESITO_ULTIMO_ERRORE = new ComplexField(father,"dettaglio-esito-ultimo-errore",int.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CODICE_RISPOSTA_ULTIMO_ERRORE = new ComplexField(father,"codice-risposta-ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.ULTIMO_ERRORE = new ComplexField(father,"ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.LOCATION_ULTIMO_ERRORE = new ComplexField(father,"location-ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CLUSTER_ID_ULTIMO_ERRORE = new ComplexField(father,"cluster-id-ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.FAULT_ULTIMO_ERRORE = new ComplexField(father,"fault-ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.FORMATO_FAULT_ULTIMO_ERRORE = new ComplexField(father,"formato-fault-ultimo-errore",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_PRIMO_PRELIEVO_IM = new ComplexField(father,"data-primo-prelievo-im",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_PRELIEVO_IM = new ComplexField(father,"data-prelievo-im",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.NUMERO_PRELIEVI_IM = new ComplexField(father,"numero-prelievi-im",int.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.DATA_ELIMINAZIONE_IM = new ComplexField(father,"data-eliminazione-im",java.util.Date.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CLUSTER_ID_PRELIEVO_IM = new ComplexField(father,"cluster-id-prelievo-im",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
		this.CLUSTER_ID_ELIMINAZIONE_IM = new ComplexField(father,"cluster-id-eliminazione-im",java.lang.String.class,"transazione-applicativo-server",TransazioneApplicativoServer.class);
	
	}
	
	

	public IField ID_TRANSAZIONE = null;
	 
	public IField SERVIZIO_APPLICATIVO_EROGATORE = null;
	 
	public IField CONNETTORE_NOME = null;
	 
	public IField DATA_REGISTRAZIONE = null;
	 
	public IField PROTOCOLLO = null;
	 
	public IField CONSEGNA_TERMINATA = null;
	 
	public IField DATA_MESSAGGIO_SCADUTO = null;
	 
	public IField DETTAGLIO_ESITO = null;
	 
	public IField CONSEGNA_TRASPARENTE = null;
	 
	public IField CONSEGNA_INTEGRATION_MANAGER = null;
	 
	public IField IDENTIFICATIVO_MESSAGGIO = null;
	 
	public IField DATA_ACCETTAZIONE_RICHIESTA = null;
	 
	public IField DATA_USCITA_RICHIESTA = null;
	 
	public IField DATA_USCITA_RICHIESTA_STREAM = null;
	 
	public IField DATA_ACCETTAZIONE_RISPOSTA = null;
	 
	public IField DATA_INGRESSO_RISPOSTA = null;
	 
	public IField DATA_INGRESSO_RISPOSTA_STREAM = null;
	 
	public IField RICHIESTA_USCITA_BYTES = null;
	 
	public IField RISPOSTA_INGRESSO_BYTES = null;
	 
	public IField LOCATION_CONNETTORE = null;
	 
	public IField CODICE_RISPOSTA = null;
	 
	public IField FAULT = null;
	 
	public IField FORMATO_FAULT = null;
	 
	public IField DATA_PRIMO_TENTATIVO = null;
	 
	public IField NUMERO_TENTATIVI = null;
	 
	public IField CLUSTER_ID_PRESA_IN_CARICO = null;
	 
	public IField CLUSTER_ID_CONSEGNA = null;
	 
	public IField DATA_ULTIMO_ERRORE = null;
	 
	public IField DETTAGLIO_ESITO_ULTIMO_ERRORE = null;
	 
	public IField CODICE_RISPOSTA_ULTIMO_ERRORE = null;
	 
	public IField ULTIMO_ERRORE = null;
	 
	public IField LOCATION_ULTIMO_ERRORE = null;
	 
	public IField CLUSTER_ID_ULTIMO_ERRORE = null;
	 
	public IField FAULT_ULTIMO_ERRORE = null;
	 
	public IField FORMATO_FAULT_ULTIMO_ERRORE = null;
	 
	public IField DATA_PRIMO_PRELIEVO_IM = null;
	 
	public IField DATA_PRELIEVO_IM = null;
	 
	public IField NUMERO_PRELIEVI_IM = null;
	 
	public IField DATA_ELIMINAZIONE_IM = null;
	 
	public IField CLUSTER_ID_PRELIEVO_IM = null;
	 
	public IField CLUSTER_ID_ELIMINAZIONE_IM = null;
	 

	@Override
	public Class<TransazioneApplicativoServer> getModeledClass(){
		return TransazioneApplicativoServer.class;
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