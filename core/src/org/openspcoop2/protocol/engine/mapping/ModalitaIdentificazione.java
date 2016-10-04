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

package org.openspcoop2.protocol.engine.mapping;


/**
 * Enumerazione sulle modalita' di identificazione dei servizi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum ModalitaIdentificazione {
	
	/*
	# Modalita' possibili per i dati su mittente, destinatario, servizio e azione:
	# - static: il valore viene indicato nell'omonima proprieta' con suffisso '.value'
	# - pluginBased: l'identificazione del valore viene delegato al plugin di protocollo
	# - urlBased: il valore viene recuperato tramite l'espressione regolare indicata  nell'omonima proprieta' con suffisso '.pattern'
	# - contentBased: il valore viene recuperato tramite l'espressione xpath indicata  nell'omonima proprieta' con suffisso '.pattern'
	# - soapActionBased: (opzione valida solo per l'azione) il valore viene recuperato dalla SOAPAction
	# - wsdlBased: (opzione valida solo per l'azione) la struttura del messaggio viene confrontata con i messaggi 
	#			   definiti nell'interfaccia WSDL associata al servizio al fine di identificare l'operation
	# - inputBased: il valore viene recuperato tramite gli header di integrazione di default per il protocollo
	# - headerBased: il valore viene recuperato dall'header indicato nell'omonima proprieta' con suffisso '.name'
	# - paBased: (opzione non valida per i dati sul mittente) 
	#			 il valore viene recuperato accedendo alla porta applicativa. 
	#            La Porta Applicativa deve essere indicata nella url invocata subito dopo 'PA' (es. http://127.0.0.1:8080/openspcoop2/PA/<NOME_PA>)
	#            Siccome potrebbero esistere piu' porte applicative con lo stesso nome, associati a soggetti diversi, 
	#            opzionalmente e' possibile indicare il tipo ed il nome del soggetto prima del nome della porta applicativa utilizzando la seguente sintassi:
	#            	http://<server>/openspcoop2/<protocol>/PA/<NOME_PA>[/<NOME_AZIONE>][[?,&]tipo_soggetto=<TIPO_SOGGETTO>][[?,&]soggetto=<NOME_SOGGETTO>]
	# 			 Se non vengono indicati i dati sul soggetto ed esiste piu' di una PA viene generato un errore.
	#			 Infine se la PA non possiede una azione all'interno della propria configurazione, e' possibile indicarla nella url.
	# - identityBased: (opzione valida solo per il mittente) 
	#					il valore viene recuperato dalle credenziali ottenute in uno dei seguenti metodi alternativi (il primo che matcha viene utilizzato):
	#                   - getUserPrincipal (servlet API)
	#                   - subject certificato client
	#                   - username fornito tramite un header http basic
	#                   E' possibile fornire una espressione regolare in cui indicare come recuperare il nome a partire dalla credenziale recuperata 
	#					L'espressione regolare deve essere indicata nell'omonima proprieta' con suffisso '.pattern'
	#                   Se l'espressione regolare non ritorna un valore, viene utilizzato interamente la credenziale recuperata
	# 
	# NOTA-AZIONE: se viene abilitata l'opzione 'forceWsdlBased', anche se la modalità indicata di riconoscimento fallisce,
	#			   viene comunque provato a recuperare il valore dell'azione interpretando il wsdl
	# 
	# NOTA-Mittente: le proprieta' relative al tipo-mittente ed al mittente possono contenere anche una ulteriore proprieta' con suffisso '.anonymous'
	#                Il valore indicato viene utilizzato come nome associato al soggetto che non e' stato identificato.
	#                In questa maniera e' possibile far rappresentare ad un soggetto unico gli accessi anonimi.
	#                Basta associare questo soggetto come fruitore di un servizio per rendere il servizio accessibile in forma anonima.
	#
	# NOTA: La modalita' 'principalBased' dell'identificazione presente sulla porta delegata e della porta applicativa 
	#       e' differente dalla modalita' 'identityTransportBased' indicata sopra.
	#       La modalita' utilizzata sulla PD e PA si basa solo sul metodo getUserPrincipal, e inoltre non usa il principal recuperato 
	#       direttamente come nome del soggetto, ma invece lo utilizza come chiave di ricerca per identificare un un soggetto registrato in base dati che lo possiede.
	#
	# NOTA2: La Modalita' 'protocolBased' dell'identificazione presente sulla porta applicativa invece si basa proprio sul valore recuperato per il mittente 
	#        tramite le regole indicate in questa sezione 
	#
	# Per quanto riguarda invece l'identificaizone delle informazioni di protocollo (Profilo di Collaborazione,FiltroDuplicati) le modalita' possibili sono:
	# - pluginBased: l'identificazione del valore viene delegato al plugin di protocollo
	# - static: l'identificazione viene letta dai valori presenti sul registro associati al servizio e operazione identificata
	#
	# Per quanto riguarda infine l'identificaizone dell'identificativo unico le modalita' possibili sono:
	# - pluginBased: l'identificazione del valore viene delegato al plugin di protocollo
	# - static: un identificatore viene generato ex-nove utilizzando la factory fornita con il protocollo
	*/
	
	STATIC("static"), 
	PLUGIN_BASED("pluginBased"), 
	URL_BASED("urlBased"), 
	CONTENT_BASED("contentBased"), 
	SOAP_ACTION_BASED("soapActionBased"), 
	WSDL_BASED("wsdlBased"), 
	//INPUT_BASED("inputBased"), // TODO 
	HEADER_BASED("+headerBased+"), 
	PA_BASED("paBased"), 
	IDENTITY_BASED("identityBased");
	
	
	
	private final String valore;

	ModalitaIdentificazione(String valore)
	{
		this.valore = valore;
	}

	public String getValore()
	{
		return this.valore;
	}

	public static String[] toStringArray(){
		String[] res = new String[ModalitaIdentificazione.values().length];
		int i=0;
		for (ModalitaIdentificazione tmp : ModalitaIdentificazione.values()) {
			res[i]=tmp.getValore();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[ModalitaIdentificazione.values().length];
		int i=0;
		for (ModalitaIdentificazione tmp : ModalitaIdentificazione.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}


	public static ModalitaIdentificazione toEnumConstant(String val){

		ModalitaIdentificazione res = null;

		if(ModalitaIdentificazione.STATIC.toString().equals(val)){
			res = ModalitaIdentificazione.STATIC;
		}
		else if(ModalitaIdentificazione.PLUGIN_BASED.toString().equals(val)){
			res = ModalitaIdentificazione.PLUGIN_BASED;
		} 
		else if(ModalitaIdentificazione.URL_BASED.toString().equals(val)){
			res = ModalitaIdentificazione.URL_BASED;
		}  
		else if(ModalitaIdentificazione.CONTENT_BASED.toString().equals(val)){
			res = ModalitaIdentificazione.CONTENT_BASED;
		}  
		else if(ModalitaIdentificazione.SOAP_ACTION_BASED.toString().equals(val)){
			res = ModalitaIdentificazione.SOAP_ACTION_BASED;
		} 
		else if(ModalitaIdentificazione.WSDL_BASED.toString().equals(val)){
			res = ModalitaIdentificazione.WSDL_BASED;
		} 
//		else if(ModalitaIdentificazione.INPUT_BASED.toString().equals(val)){
//			res = ModalitaIdentificazione.INPUT_BASED;
//		} 
		else if(ModalitaIdentificazione.HEADER_BASED.toString().equals(val)){
			res = ModalitaIdentificazione.HEADER_BASED;
		} 
		else if(ModalitaIdentificazione.PA_BASED.toString().equals(val)){
			res = ModalitaIdentificazione.PA_BASED;
		} 
		else if(ModalitaIdentificazione.IDENTITY_BASED.toString().equals(val)){
			res = ModalitaIdentificazione.IDENTITY_BASED;
		} 
		return res;
	}

	
	@Override
	public String toString(){
		return this.valore;
	}
	public boolean equals(ModalitaIdentificazione esito){
		return this.toString().equals(esito.toString());
	}
}
