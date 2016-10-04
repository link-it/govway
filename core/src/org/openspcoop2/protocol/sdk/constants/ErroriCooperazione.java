/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.protocol.sdk.constants;

/**
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public enum ErroriCooperazione {

	MITTENTE_SCONOSCIUTO("Il Mittente non risulta registrato nel Registro dei Servizi",
			CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO),
	MITTENTE_NON_VALIDO("Il Mittente presente nella busta non è valido",
			CodiceErroreCooperazione.MITTENTE_NON_VALIDO),
			
	TIPO_MITTENTE_NON_VALIDO("Il Tipo del mittente presente nella busta non è valido",
			CodiceErroreCooperazione.TIPO_MITTENTE_NON_VALIDO),
	
	DESTINATARIO_SCONOSCIUTO("Il Destinatario non risulta registrato nel Registro dei Servizi",
			CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO),
	DESTINATARIO_NON_VALIDO("Il Destinatario presente nella busta non è valido",
			CodiceErroreCooperazione.DESTINATARIO_NON_VALIDO),
			
	TIPO_DESTINATARIO_NON_VALIDO("Il Tipo del destinatario presente nella busta non è valido",
			CodiceErroreCooperazione.TIPO_DESTINATARIO_NON_VALIDO),
					
	SERVIZIO_SCONOSCIUTO("Il Servizio non risulta registrato nel Registro dei Servizi",
			CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO),
	SERVIZIO_NON_VALIDO("Il Servizio presente nella busta non è valido",
			CodiceErroreCooperazione.SERVIZIO_NON_VALIDO),
			
	TIPO_SERVIZIO_NON_VALIDO("Il Tipo del servizio presente nella busta non è valido",
			CodiceErroreCooperazione.TIPO_SERVIZIO_NON_VALIDO),
	
	AZIONE_NON_VALIDA("L'azione presente nella busta non è valida",
			CodiceErroreCooperazione.AZIONE_NON_VALIDA),		
			
	IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO("La busta presenta un identificativo già processato in precedenza",
			CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO),
			
	RIFERIMENTO_MESSAGGIO_NON_PRESENTE("La busta non contiene un RiferimentoMessaggio alla precedente busta a cui è correlata logicamente come richiede il profilo",
			CodiceErroreCooperazione.RIFERIMENTO_MESSAGGIO_NON_PRESENTE),
	RIFERIMENTO_MESSAGGIO_NON_VALIDO("IdentificativoBusta presente nel RiferimentoMessaggio non è valido",
			CodiceErroreCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALIDO),
	
	MESSAGGIO_SCADUTO("Messaggio scaduto",
			CodiceErroreCooperazione.MESSAGGIO_SCADUTO),
	
	PROFILO_COLLABORAZIONE_SCONOSCIUTO("Busta con profilo di collaborazione sconosciuto",
			CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO),
	PROFILO_COLLABORAZIONE_NON_VALIDO("Busta con profilo di collaborazione non valido rispetto al tipo di cooperazione in corso",
			CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_NON_VALIDO),
			
	COLLABORAZIONE_NON_VALIDA("La collaborazione presente nella busta non è valida",
			CodiceErroreCooperazione.COLLABORAZIONE_NON_VALIDA),
	
	COLLABORAZIONE_SCONOSCIUTA("La collaborazione presente nella busta non appartiene a nessuna sessione valida",
			CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA),
	
	PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE_NON_PRESENTE("La busta non contiene una richiesta di 'conferma ricezione', nonostante il servizio indicato richieda, tramite la definizione dell'accordo nel registro, una consegna affidabile",
			CodiceErroreCooperazione.PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE_NON_PRESENTE),
		
	CONSEGNA_IN_ORDINE_NON_GESTIBILE("La busta non contiene una richiesta di 'consegna in ordine', nonostante il servizio indicato richieda, tramite la definizione dell'accordo nel registro, un ordinamento",
			CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_GESTIBILE),
	
	CONSEGNA_IN_ORDINE_FUORI_SEQUENZA("Riscontrato numero di sequenza diverso da 1, in una busta capostipite di una sequenza",
			CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_FUORI_SEQUENZA),
	
	CONSEGNA_IN_ORDINE_TIPO_MITTENTE_NON_VALIDO("Il tipo di mittente non rispetta quello atteso nella gestione della collaborazione con consegna in ordine",
			CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_TIPO_MITTENTE_NON_VALIDO),
	
	CONSEGNA_IN_ORDINE_MITTENTE_NON_VALIDO("Il nome del mittente non rispetta quello atteso nella gestione della collaborazione con consegna in ordine",
			CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_MITTENTE_NON_VALIDO),
	
	CONSEGNA_IN_ORDINE_TIPO_DESTINATARIO_NON_VALIDO("Il tipo di destinatario non rispetta quello atteso nella gestione della collaborazione con consegna in ordine",
			CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_TIPO_DESTINATARIO_NON_VALIDO),
	
	CONSEGNA_IN_ORDINE_DESTINATARIO_NON_VALIDO("Il nome del destinatario non rispetta quello atteso nella gestione della collaborazione con consegna in ordine",
			CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_DESTINATARIO_NON_VALIDO),
	
	CONSEGNA_IN_ORDINE_TIPO_SERVIZIO_NON_VALIDO("Il tipo di servizio non rispetta quello atteso nella gestione della collaborazione con consegna in ordine",
			CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_TIPO_SERVIZIO_NON_VALIDO),
	
	CONSEGNA_IN_ORDINE_SERVIZIO_NON_VALIDO("Il nome del servizio non rispetta quello atteso nella gestione della collaborazione con consegna in ordine",
			CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_SERVIZIO_NON_VALIDO),
	
	CONSEGNA_IN_ORDINE_AZIONE_NON_VALIDA("Il nome dell'azione non rispetta quello atteso nella gestione della collaborazione con consegna in ordine",
			CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_AZIONE_NON_VALIDA),
	
	CONSEGNA_IN_ORDINE_COLLABORAZIONE_IN_BUSTA_NON_CAPOSTIPITE_SCONOSCIUTA("Busta non capostipite che richiede funzionalità di consegna in ordine presenta una collaborazione non registrata per le funzioni di consegna in ordine",
			CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_COLLABORAZIONE_IN_BUSTA_NON_CAPOSTIPITE_SCONOSCIUTA),
	
	CONSEGNA_IN_ORDINE_NON_SUPPORTATA("Funzionalità di consegna in ordine non supportata",
			CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_SUPPORTATA),
			
	MESSAGE_SECURITY("E' occorso un errore durante la gestione della Sicurezza sul Messaggio",
			null),
			
	AUTORIZZAZIONE_FALLITA("Il Mittente della busta non è autorizzato a fruire del servizio richiesto",
			null),
			
	ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO("E' occorso un errore durante il processamento del messsaggio",
			CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO),
			
	ERRORE_GENERICO_PROTOCOLLO_NON_CORRETTO("E' stato rilevata una violazione per quanto concerne le informazioni richieste del protocollo in gestione",
			CodiceErroreCooperazione.FORMATO_NON_CORRETTO);
	
	private final String descrizione;
	private final CodiceErroreCooperazione codiceErrore;

	ErroriCooperazione(String descrizione,CodiceErroreCooperazione codiceErrore){
		this.descrizione = descrizione;
		this.codiceErrore = codiceErrore;
	}
	
	@Override
	public String toString(){
		return this.newErroreCooperazione().toString();
	}
	
	private ErroreCooperazione newErroreCooperazione(){
		return newErroreCooperazione(this.descrizione, this.codiceErrore);
	}
	private ErroreCooperazione newErroreCooperazione(String descrizioneParam){
		return newErroreCooperazione(descrizioneParam, this.codiceErrore);
	}
	@SuppressWarnings("unused")
	private ErroreCooperazione newErroreCooperazione(CodiceErroreCooperazione codiceParam){
		return newErroreCooperazione(this.descrizione, codiceParam);
	}
	private ErroreCooperazione newErroreCooperazione(String descrizioneParam,CodiceErroreCooperazione codiceParam){
		String newDescrizione = new String(descrizioneParam);
		return new ErroreCooperazione(newDescrizione, codiceParam);
	}
	
	public ErroreCooperazione getErroreCooperazione() {
		return getErroreCooperazione(null);
	}
	public ErroreCooperazione getErroreCooperazione(String descrizione) {
		if(this.equals(MESSAGE_SECURITY) ||
			this.equals(AUTORIZZAZIONE_FALLITA)){
			throw new RuntimeException("Il metodo non può essere utilizzato con il messaggio "+this.name());
		}
		if(descrizione!=null){
			return newErroreCooperazione(descrizione);
		}
		else{
			return newErroreCooperazione();
		}
	}
	
	public ErroreCooperazione getErroreProfiloCollaborazioneSconosciuto(String profiloCollaborazione) {
		if(this.equals(PROFILO_COLLABORAZIONE_SCONOSCIUTO)){
			if(profiloCollaborazione==null){
				return newErroreCooperazione("Busta senza Profilo di Collaborazione");	
			}else{
				return newErroreCooperazione("Busta con profilo di collaborazione non gestito ["+profiloCollaborazione+"]");	
			}	
		}else{
			throw new RuntimeException("Il metodo può essere utilizzato solo con il messaggio "+PROFILO_COLLABORAZIONE_SCONOSCIUTO.name());
		}
	}
	
	public ErroreCooperazione getErroreProfiloCollaborazioneNonValido(String descrizione) {
		if(this.equals(PROFILO_COLLABORAZIONE_NON_VALIDO)){
			return newErroreCooperazione(descrizione);
		}else{
			throw new RuntimeException("Il metodo può essere utilizzato solo con il messaggio "+PROFILO_COLLABORAZIONE_NON_VALIDO.name());
		}
	}
		
	public ErroreCooperazione getErroreMessageSecurity(String descrizione,CodiceErroreCooperazione codiceErrore) {
		if(this.equals(MESSAGE_SECURITY)){
			if(CodiceErroreCooperazione.isEccezioneMessageSecurity(codiceErrore)==false &&
					CodiceErroreCooperazione.isEccezioneSicurezzaAutorizzazione(codiceErrore)==false){ //messageSecurity internamente possiede anche un engine di autorizzazione
				throw new RuntimeException("Il metodo può essere utilizzato solo con codici associati alla sicurezza, relativamente ad errori di messageSecurity o erroreAutorizzazione, codice fornito: "+codiceErrore);
			}
			return newErroreCooperazione(descrizione, codiceErrore);	
		}else{
			throw new RuntimeException("Il metodo può essere utilizzato solo con il messaggio "+MESSAGE_SECURITY.name());
		}
	}
	
	public ErroreCooperazione getErroreAutorizzazione(String descrizione,CodiceErroreCooperazione codiceErrore) {
		if(this.equals(AUTORIZZAZIONE_FALLITA)){
			if(CodiceErroreCooperazione.isEccezioneSicurezzaAutorizzazione(codiceErrore)==false){
				throw new RuntimeException("Il metodo può essere utilizzato solo con codici associati alla sicurezza, relativamente a fallimenti di autorizzazione, codice fornito: "+codiceErrore);
			}
			return newErroreCooperazione(descrizione, codiceErrore);	
		}else{
			throw new RuntimeException("Il metodo può essere utilizzato solo con il messaggio "+AUTORIZZAZIONE_FALLITA.name());
		}
	}
		
	public ErroreCooperazione getErroreProcessamento(String descrizione) {
		if(this.equals(ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO)){
			return newErroreCooperazione(descrizione);
		}else{
			throw new RuntimeException("Il metodo può essere utilizzato solo con il messaggio "+ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.name());
		}
	}
	
	public ErroreCooperazione getErroreGestioneProtocollo(String descrizione) {
		if(this.equals(ERRORE_GENERICO_PROTOCOLLO_NON_CORRETTO)){
			return newErroreCooperazione(descrizione);
		}else{
			throw new RuntimeException("Il metodo può essere utilizzato solo con il messaggio "+ERRORE_GENERICO_PROTOCOLLO_NON_CORRETTO.name());
		}
	}
}
