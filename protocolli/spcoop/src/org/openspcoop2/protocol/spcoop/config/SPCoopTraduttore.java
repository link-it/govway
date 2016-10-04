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

package org.openspcoop2.protocol.spcoop.config;

import java.util.Date;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.basic.config.BasicTraduttore;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.SubCodiceErrore;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostantiPosizioneEccezione;
import org.openspcoop2.protocol.spcoop.utils.SPCoopUtils;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.ITraduttore} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopTraduttore extends BasicTraduttore {


	public SPCoopTraduttore(IProtocolFactory protocolFactory){
		super(protocolFactory);
	}

	@Override
	public String toString(CodiceErroreCooperazione cod) {
		return toString(cod, null);
	}
	@Override
	public String toString(CodiceErroreCooperazione cod,SubCodiceErrore subCode) {

		String prefix = "EGOV_IT_";

		switch (cod) {

		// Macro-struttura
		case FORMATO_NON_CORRETTO:
			return prefix+"001";
		case FORMATO_INTESTAZIONE_NON_CORRETTO:
			return prefix+"002";
		case FORMATO_CORPO_NON_CORRETTO:
		case ALLEGATI:
			return prefix+"003";

			// Header
		case INTESTAZIONE_NON_CORRETTA:
			return prefix+"100";

			// Mittente
		case MITTENTE:
		case MITTENTE_NON_PRESENTE:
		case TIPO_MITTENTE_NON_PRESENTE:
		case IDPORTA_MITTENTE_NON_PRESENTE:
		case INDIRIZZO_MITTENTE_NON_PRESENTE:
		case MITTENTE_PRESENTE_PIU_VOLTE:
		case TIPO_MITTENTE_PRESENTE_PIU_VOLTE:
		case IDPORTA_MITTENTE_PRESENTE_PIU_VOLTE:
		case INDIRIZZO_MITTENTE_PRESENTE_PIU_VOLTE:
		case MITTENTE_NON_VALORIZZATO:
		case TIPO_MITTENTE_NON_VALORIZZATO:
		case IDPORTA_MITTENTE_NON_VALORIZZATO:
		case INDIRIZZO_MITTENTE_NON_VALORIZZATO:
		case MITTENTE_NON_VALIDO:
		case TIPO_MITTENTE_NON_VALIDO:
		case IDPORTA_MITTENTE_NON_VALIDO:
		case INDIRIZZO_MITTENTE_NON_VALIDO:
		case MITTENTE_SCONOSCIUTO:
		case TIPO_MITTENTE_SCONOSCIUTO:
		case IDPORTA_MITTENTE_SCONOSCIUTO:
		case INDIRIZZO_MITTENTE_SCONOSCIUTO:
			return prefix+"101";

			// Destinatario
		case DESTINATARIO:
		case DESTINATARIO_NON_PRESENTE:
		case TIPO_DESTINATARIO_NON_PRESENTE:
		case IDPORTA_DESTINATARIO_NON_PRESENTE:
		case INDIRIZZO_DESTINATARIO_NON_PRESENTE:
		case DESTINATARIO_PRESENTE_PIU_VOLTE:
		case TIPO_DESTINATARIO_PRESENTE_PIU_VOLTE:
		case IDPORTA_DESTINATARIO_PRESENTE_PIU_VOLTE:
		case INDIRIZZO_DESTINATARIO_PRESENTE_PIU_VOLTE:
		case DESTINATARIO_NON_VALORIZZATO:
		case TIPO_DESTINATARIO_NON_VALORIZZATO:
		case IDPORTA_DESTINATARIO_NON_VALORIZZATO:
		case INDIRIZZO_DESTINATARIO_NON_VALORIZZATO:
		case DESTINATARIO_NON_VALIDO:
		case TIPO_DESTINATARIO_NON_VALIDO:
		case IDPORTA_DESTINATARIO_NON_VALIDO:
		case INDIRIZZO_DESTINATARIO_NON_VALIDO:
		case DESTINATARIO_SCONOSCIUTO:
		case TIPO_DESTINATARIO_SCONOSCIUTO:
		case IDPORTA_DESTINATARIO_SCONOSCIUTO:
		case INDIRIZZO_DESTINATARIO_SCONOSCIUTO:
			return prefix+"102";

			// Profilo di Collaboraizone
		case PROFILO_COLLABORAZIONE:
		case PROFILO_COLLABORAZIONE_NON_PRESENTE:
		case PROFILO_COLLABORAZIONE_PRESENTE_PIU_VOLTE:
		case PROFILO_COLLABORAZIONE_NON_VALORIZZATO:
		case PROFILO_COLLABORAZIONE_NON_VALIDO:
		case PROFILO_COLLABORAZIONE_SCONOSCIUTO:
		case SERVIZIO_CORRELATO_NON_PRESENTE:
		case TIPO_SERVIZIO_CORRELATO_NON_PRESENTE:
		case VERSIONE_SERVIZIO_CORRELATO_NON_PRESENTE:
		case SERVIZIO_CORRELATO_PRESENTE_PIU_VOLTE:
		case TIPO_SERVIZIO_CORRELATO_PRESENTE_PIU_VOLTE:
		case VERSIONE_SERVIZIO_CORRELATO_PRESENTE_PIU_VOLTE:
		case SERVIZIO_CORRELATO_NON_VALORIZZATO:
		case TIPO_SERVIZIO_CORRELATO_NON_VALORIZZATO:
		case VERSIONE_SERVIZIO_CORRELATO_NON_VALORIZZATO:
		case SERVIZIO_CORRELATO_NON_VALIDO:
		case TIPO_SERVIZIO_CORRELATO_NON_VALIDO:
		case VERSIONE_SERVIZIO_CORRELATO_NON_VALIDO:
		case SERVIZIO_CORRELATO_SCONOSCIUTO:
		case TIPO_SERVIZIO_CORRELATO_SCONOSCIUTO:
		case VERSIONE_SERVIZIO_CORRELATO_SCONOSCIUTO:
			return prefix+"103";

			// Collaborazione
		case COLLABORAZIONE:
		case COLLABORAZIONE_NON_PRESENTE:
		case COLLABORAZIONE_PRESENTE_PIU_VOLTE:
		case COLLABORAZIONE_NON_VALORIZZATA:
		case COLLABORAZIONE_NON_VALIDA:
		case COLLABORAZIONE_SCONOSCIUTA:
			return prefix+"104";

			// Servizio
		case SERVIZIO:
		case SERVIZIO_NON_PRESENTE:
		case TIPO_SERVIZIO_NON_PRESENTE:
		case VERSIONE_SERVIZIO_NON_PRESENTE:
		case SERVIZIO_PRESENTE_PIU_VOLTE:
		case TIPO_SERVIZIO_PRESENTE_PIU_VOLTE:
		case VERSIONE_SERVIZIO_PRESENTE_PIU_VOLTE:
		case SERVIZIO_NON_VALORIZZATO:
		case TIPO_SERVIZIO_NON_VALORIZZATO:
		case VERSIONE_SERVIZIO_NON_VALORIZZATO:
		case SERVIZIO_NON_VALIDO:
		case TIPO_SERVIZIO_NON_VALIDO:
		case VERSIONE_SERVIZIO_NON_VALIDO:
		case SERVIZIO_SCONOSCIUTO:
		case TIPO_SERVIZIO_SCONOSCIUTO:
		case VERSIONE_SERVIZIO_SCONOSCIUTO:
			return prefix+"105";

			// Azione
		case AZIONE:
		case AZIONE_NON_PRESENTE:
		case AZIONE_PRESENTE_PIU_VOLTE:
		case AZIONE_NON_VALORIZZATA:
		case AZIONE_NON_VALIDA:
		case AZIONE_SCONOSCIUTA:
		case INVOCAZIONE_SENZA_AZIONE_NON_PERMESSA:
			return prefix+"106";

			// IdentificativoMessaggio non definito
		case IDENTIFICATIVO_MESSAGGIO:
		case IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE:
		case IDENTIFICATIVO_MESSAGGIO_PRESENTE_PIU_VOLTE:
		case IDENTIFICATIVO_MESSAGGIO_NON_VALORIZZATO:
			return prefix+"107";

			// OraRegistrazione
		case ORA_REGISTRAZIONE:
		case ORA_REGISTRAZIONE_NON_PRESENTE:
		case TIPO_ORA_REGISTRAZIONE_NON_PRESENTE:
		case ORA_REGISTRAZIONE_PRESENTE_PIU_VOLTE:
		case TIPO_ORA_REGISTRAZIONE_PRESENTE_PIU_VOLTE:
		case ORA_REGISTRAZIONE_NON_VALORIZZATA:
		case TIPO_ORA_REGISTRAZIONE_NON_VALORIZZATA:
		case ORA_REGISTRAZIONE_NON_VALIDA:
		case TIPO_ORA_REGISTRAZIONE_NON_VALIDA:
		case ORA_REGISTRAZIONE_SCONOSCIUTA:
		case TIPO_ORA_REGISTRAZIONE_SCONOSCIUTA:
			return prefix+"108";

			// RiferimentoMessaggio non definito
		case RIFERIMENTO_MESSAGGIO:
		case RIFERIMENTO_MESSAGGIO_NON_PRESENTE:
		case RIFERIMENTO_MESSAGGIO_PRESENTE_PIU_VOLTE:
		case RIFERIMENTO_MESSAGGIO_NON_VALORIZZATO:
			return prefix+"109";

			// IdentificativoMessaggio non valido
		case IDENTIFICATIVO_MESSAGGIO_NON_VALIDO:
		case IDENTIFICATIVO_MESSAGGIO_SCONOSCIUTO:
		case IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO:
			return prefix+"110";

			// RiferimentoMessaggio non valido
		case RIFERIMENTO_MESSAGGIO_NON_VALIDO:
		case RIFERIMENTO_MESSAGGIO_SCONOSCIUTO:
		case RIFERIMENTO_MESSAGGIO_GIA_PROCESSATO:
			return prefix+"111";

			// OraRegistrazione
		case SCADENZA:
		case SCADENZA_NON_PRESENTE:
		case TIPO_SCADENZA_NON_PRESENTE:
		case SCADENZA_PRESENTE_PIU_VOLTE:
		case TIPO_SCADENZA_PRESENTE_PIU_VOLTE:
		case SCADENZA_NON_VALORIZZATA:
		case TIPO_SCADENZA_NON_VALORIZZATA:
		case SCADENZA_NON_VALIDA:
		case TIPO_SCADENZA_NON_VALIDA:
		case SCADENZA_SCONOSCIUTA:
		case TIPO_SCADENZA_SCONOSCIUTA:
			return prefix+"112";

			// ProfiloTrasmissione
		case PROFILO_TRASMISSIONE:
		case PROFILO_TRASMISSIONE_FILTRO_DUPLICATI:
		case PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE:
		case PROFILO_TRASMISSIONE_NON_PRESENTE:
		case PROFILO_TRASMISSIONE_FILTRO_DUPLICATI_NON_PRESENTE:
		case PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE_NON_PRESENTE:
		case PROFILO_TRASMISSIONE_PRESENTE_PIU_VOLTE:
		case PROFILO_TRASMISSIONE_FILTRO_DUPLICATI_PRESENTE_PIU_VOLTE:
		case PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE_PRESENTE_PIU_VOLTE:
		case PROFILO_TRASMISSIONE_NON_VALORIZZATO:
		case PROFILO_TRASMISSIONE_FILTRO_DUPLICATI_NON_VALORIZZATO:
		case PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE_NON_VALORIZZATO:
		case PROFILO_TRASMISSIONE_NON_VALIDO:
		case PROFILO_TRASMISSIONE_FILTRO_DUPLICATI_NON_VALIDO:
		case PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE_NON_VALIDO:
		case PROFILO_TRASMISSIONE_SCONOSCIUTO:
		case PROFILO_TRASMISSIONE_FILTRO_DUPLICATI_SCONOSCIUTO:
		case PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE_SCONOSCIUTO:
			return prefix+"113";

			// Sequenza
		case CONSEGNA_IN_ORDINE_NON_PRESENTE:
		case CONSEGNA_IN_ORDINE_PRESENTE_PIU_VOLTE:
		case CONSEGNA_IN_ORDINE_NON_VALORIZZATA:
		case CONSEGNA_IN_ORDINE_NON_VALIDA:
		case CONSEGNA_IN_ORDINE_SCONOSCIUTA:
		case CONSEGNA_IN_ORDINE_FUORI_SEQUENZA:
		case CONSEGNA_IN_ORDINE_TIPO_MITTENTE_NON_VALIDO:
		case CONSEGNA_IN_ORDINE_MITTENTE_NON_VALIDO:
		case CONSEGNA_IN_ORDINE_TIPO_DESTINATARIO_NON_VALIDO:
		case CONSEGNA_IN_ORDINE_DESTINATARIO_NON_VALIDO:
		case CONSEGNA_IN_ORDINE_TIPO_SERVIZIO_NON_VALIDO:
		case CONSEGNA_IN_ORDINE_SERVIZIO_NON_VALIDO:
		case CONSEGNA_IN_ORDINE_AZIONE_NON_VALIDA:
		case CONSEGNA_IN_ORDINE_COLLABORAZIONE_IN_BUSTA_NON_CAPOSTIPITE_SCONOSCIUTA:
			return prefix+"114";

			// ListaRiscontri
		case LISTA_RISCONTRI:
		case LISTA_RISCONTRI_NON_PRESENTE:
		case LISTA_RISCONTRI_PRESENTE_PIU_VOLTE:
		case LISTA_RISCONTRI_NON_VALORIZZATA:
		case LISTA_RISCONTRI_NON_VALIDA:
		case LISTA_RISCONTRI_SCONOSCIUTA:
		case RISCONTRO:
		case RISCONTRO_IDENTIFICATIVO_MESSAGGIO:
		case RISCONTRO_ORA_REGISTRAZIONE:
		case RISCONTRO_TIPO_ORA_REGISTRAZIONE:
		case RISCONTRO_NON_PRESENTE:
		case RISCONTRO_IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE:
		case RISCONTRO_ORA_REGISTRAZIONE_NON_PRESENTE:
		case RISCONTRO_TIPO_ORA_REGISTRAZIONE_NON_PRESENTE:
		case RISCONTRO_PRESENTE_PIU_VOLTE:
		case RISCONTRO_IDENTIFICATIVO_MESSAGGIO_PRESENTE_PIU_VOLTE:
		case RISCONTRO_ORA_REGISTRAZIONE_PRESENTE_PIU_VOLTE:
		case RISCONTRO_TIPO_ORA_REGISTRAZIONE_PRESENTE_PIU_VOLTE:
		case RISCONTRO_NON_VALORIZZATO:
		case RISCONTRO_IDENTIFICATIVO_MESSAGGIO_NON_VALORIZZATO:
		case RISCONTRO_ORA_REGISTRAZIONE_NON_VALORIZZATA:
		case RISCONTRO_TIPO_ORA_REGISTRAZIONE_NON_VALORIZZATO:
		case RISCONTRO_NON_VALIDO:
		case RISCONTRO_IDENTIFICATIVO_MESSAGGIO_NON_VALIDO:
		case RISCONTRO_ORA_REGISTRAZIONE_NON_VALIDA:
		case RISCONTRO_TIPO_ORA_REGISTRAZIONE_NON_VALIDO:
		case RISCONTRO_SCONOSCIUTO:
		case RISCONTRO_IDENTIFICATIVO_MESSAGGIO_SCONOSCIUTO:
		case RISCONTRO_ORA_REGISTRAZIONE_SCONOSCIUTA:
		case RISCONTRO_TIPO_ORA_REGISTRAZIONE_SCONOSCIUTO:
			return prefix+"115";

			// ListaTrasmissioni
		case LISTA_TRASMISSIONI:
		case LISTA_TRASMISSIONI_NON_PRESENTE:
		case LISTA_TRASMISSIONI_PRESENTE_PIU_VOLTE:
		case LISTA_TRASMISSIONI_NON_VALORIZZATA:
		case LISTA_TRASMISSIONI_NON_VALIDA:
		case LISTA_TRASMISSIONI_SCONOSCIUTA:
		case TRASMISSIONE:
		case TRASMISSIONE_TIPO_ORIGINE:
		case TRASMISSIONE_ORIGINE:
		case TRASMISSIONE_INDIRIZZO_ORIGINE:
		case TRASMISSIONE_IDPORTA_ORIGINE:
		case TRASMISSIONE_TIPO_DESTINAZIONE:
		case TRASMISSIONE_DESTINAZIONE:
		case TRASMISSIONE_INDIRIZZO_DESTINAZIONE:
		case TRASMISSIONE_IDPORTA_DESTINAZIONE:
		case TRASMISSIONE_TIPO_ORA_REGISTRAZIONE:
		case TRASMISSIONE_ORA_REGISTRAZIONE:
		case TRASMISSIONE_NON_PRESENTE:
		case TRASMISSIONE_TIPO_ORIGINE_NON_PRESENTE:
		case TRASMISSIONE_ORIGINE_NON_PRESENTE:
		case TRASMISSIONE_INDIRIZZO_ORIGINE_NON_PRESENTE:
		case TRASMISSIONE_IDPORTA_ORIGINE_NON_PRESENTE:
		case TRASMISSIONE_TIPO_DESTINAZIONE_NON_PRESENTE:
		case TRASMISSIONE_DESTINAZIONE_NON_PRESENTE:
		case TRASMISSIONE_INDIRIZZO_DESTINAZIONE_NON_PRESENTE:
		case TRASMISSIONE_IDPORTA_DESTINAZIONE_NON_PRESENTE:
		case TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_NON_PRESENTE:
		case TRASMISSIONE_ORA_REGISTRAZIONE_NON_PRESENTE:
		case TRASMISSIONE_PRESENTE_PIU_VOLTE:
		case TRASMISSIONE_TIPO_ORIGINE_PRESENTE_PIU_VOLTE:
		case TRASMISSIONE_ORIGINE_PRESENTE_PIU_VOLTE:
		case TRASMISSIONE_INDIRIZZO_ORIGINE_PRESENTE_PIU_VOLTE:
		case TRASMISSIONE_IDPORTA_ORIGINE_PRESENTE_PIU_VOLTE:
		case TRASMISSIONE_TIPO_DESTINAZIONE_PRESENTE_PIU_VOLTE:
		case TRASMISSIONE_DESTINAZIONE_PRESENTE_PIU_VOLTE:
		case TRASMISSIONE_INDIRIZZO_DESTINAZIONE_PRESENTE_PIU_VOLTE:
		case TRASMISSIONE_IDPORTA_DESTINAZIONE_PRESENTE_PIU_VOLTE:
		case TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_PRESENTE_PIU_VOLTE:
		case TRASMISSIONE_ORA_REGISTRAZIONE_PRESENTE_PIU_VOLTE:
		case TRASMISSIONE_NON_VALORIZZATA:
		case TRASMISSIONE_TIPO_ORIGINE_NON_VALORIZZATA:
		case TRASMISSIONE_ORIGINE_NON_VALORIZZATA:
		case TRASMISSIONE_INDIRIZZO_ORIGINE_NON_VALORIZZATA:
		case TRASMISSIONE_IDPORTA_ORIGINE_NON_VALORIZZATA:
		case TRASMISSIONE_TIPO_DESTINAZIONE_NON_VALORIZZATA:
		case TRASMISSIONE_DESTINAZIONE_NON_VALORIZZATA:
		case TRASMISSIONE_INDIRIZZO_DESTINAZIONE_NON_VALORIZZATA:
		case TRASMISSIONE_IDPORTA_DESTINAZIONE_NON_VALORIZZATA:
		case TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_NON_VALORIZZATA:
		case TRASMISSIONE_ORA_REGISTRAZIONE_NON_VALORIZZATA:
		case TRASMISSIONE_NON_VALIDA:
		case TRASMISSIONE_TIPO_ORIGINE_NON_VALIDA:
		case TRASMISSIONE_ORIGINE_NON_VALIDA:
		case TRASMISSIONE_INDIRIZZO_ORIGINE_NON_VALIDA:
		case TRASMISSIONE_IDPORTA_ORIGINE_NON_VALIDA:
		case TRASMISSIONE_TIPO_DESTINAZIONE_NON_VALIDA:
		case TRASMISSIONE_DESTINAZIONE_NON_VALIDA:
		case TRASMISSIONE_INDIRIZZO_DESTINAZIONE_NON_VALIDA:
		case TRASMISSIONE_IDPORTA_DESTINAZIONE_NON_VALIDA:
		case TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_NON_VALIDA:
		case TRASMISSIONE_ORA_REGISTRAZIONE_NON_VALIDA:
		case TRASMISSIONE_SCONOSCIUTA:
		case TRASMISSIONE_TIPO_ORIGINE_SCONOSCIUTA:
		case TRASMISSIONE_ORIGINE_SCONOSCIUTA:
		case TRASMISSIONE_INDIRIZZO_ORIGINE_SCONOSCIUTA:
		case TRASMISSIONE_IDPORTA_ORIGINE_SCONOSCIUTA:
		case TRASMISSIONE_TIPO_DESTINAZIONE_SCONOSCIUTA:
		case TRASMISSIONE_DESTINAZIONE_SCONOSCIUTA:
		case TRASMISSIONE_INDIRIZZO_DESTINAZIONE_SCONOSCIUTA:
		case TRASMISSIONE_IDPORTA_DESTINAZIONE_SCONOSCIUTA:
		case TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_SCONOSCIUTA:
		case TRASMISSIONE_ORA_REGISTRAZIONE_SCONOSCIUTA:
			return prefix+"116";

			// Firma non valida
		case SICUREZZA_FIRMA_INTESTAZIONE_NON_VALIDA:
		case SICUREZZA_FIRMA_INTESTAZIONE_NON_PRESENTE:
			return prefix+"117";	

			// Allegato riferimento non valido
		case ALLEGATI_RIFERIMENTO_NON_PRESENTE:
		case ALLEGATI_RIFERIMENTO_NON_VALIDO:
			return prefix+"118";	

			// Allegato non presente
		case ALLEGATO_NON_PRESENTE:
			return prefix+"119";	

			// Allegato non definito
		case ALLEGATO_NON_DEFINITO_MANIFEST:
			return prefix+"120";	

			// Politiche di sicurezza
		case SICUREZZA:
		case SICUREZZA_CIFRATURA_NON_PRESENTE:
		case SICUREZZA_CIFRATURA_NON_VALIDA:
		case SICUREZZA_CIFRATURA_ALLEGATO_NON_PRESENTE:
		case SICUREZZA_CIFRATURA_ALLEGATO_NON_VALIDA:
		case SICUREZZA_NON_PRESENTE:
			return prefix+"200";	

			// Mittente non autorizzato
		case SICUREZZA_AUTORIZZAZIONE_FALLITA:
		case SICUREZZA_FALSIFICAZIONE_MITTENTE:	
			return prefix+"201";	

			// Firma XML non valida
		case SICUREZZA_FIRMA_NON_VALIDA:
		case SICUREZZA_FIRMA_NON_PRESENTE:
		case SICUREZZA_FIRMA_ALLEGATO_NON_VALIDA:
		case SICUREZZA_FIRMA_ALLEGATO_NON_PRESENTE:
			return prefix+"202";	

			// Firma PKCS#7 non valida
		case SICUREZZA_FIRMA_PKCS7_ALLEGATO_NON_VALIDA:
		case SICUREZZA_FIRMA_PKCS7_ALLEGATO_NON_PRESENTE:
			return prefix+"203";	

			// Eccezione processamento
		case ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO:
			return prefix+"300";	

			// Messaggio scaduto
		case MESSAGGIO_SCADUTO:
			return prefix+"301";	

			// ECCEZIONE_GESTIONE_TRASPARENZA_TEMPORALE
		case CONSEGNA_IN_ORDINE:
			return prefix+"400";	

			// ECCEZIONE_TRASPARENZA_TEMPORALE_NON_SUPPORTATA
		case CONSEGNA_IN_ORDINE_NON_SUPPORTATA:
			return prefix+"401";	

			// ECCEZIONE_TRASPARENZA_TEMPORALE_NON_GESTIBILE
		case CONSEGNA_IN_ORDINE_NON_GESTIBILE:
			return prefix+"402";	

		default:
			return null;
		} 

	}

	@Override
	public CodiceErroreCooperazione toCodiceErroreCooperazione(String codiceCooperazione) {

		String prefix = "EGOV_IT_";

		// Macro-struttura
		if( (prefix+"001").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.FORMATO_NON_CORRETTO;
		}
		else if( (prefix+"002").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO;
		}
		else if( (prefix+"003").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO;
		}

		// Header
		else if( (prefix+"100").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.INTESTAZIONE_NON_CORRETTA;
		}

		// Mittente
		else if( (prefix+"101").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.MITTENTE;
		}

		// Destinatario
		else if( (prefix+"102").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.DESTINATARIO;
		}

		// Profilo di Collaborazione
		else if( (prefix+"103").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.PROFILO_COLLABORAZIONE;
		}

		// Collaborazione
		else if( (prefix+"104").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.COLLABORAZIONE;
		}

		// Servizio
		else if( (prefix+"105").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.SERVIZIO;
		}

		// Azione
		else if( (prefix+"106").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.AZIONE;
		}

		// IdentificativoMessaggio non definito
		else if( (prefix+"107").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO;
		}

		// OraRegistrazione
		else if( (prefix+"108").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.ORA_REGISTRAZIONE;
		}

		// RiferimentoMessaggio non definito
		else if( (prefix+"109").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.RIFERIMENTO_MESSAGGIO;
		}

		// IdentificativoMessaggio non valido
		else if( (prefix+"110").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO;
		}

		// RiferimentoMessaggio non valido
		else if( (prefix+"111").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALIDO;
		}

		// Scadenza
		else if( (prefix+"112").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.SCADENZA;
		}

		// ProfiloTrasmissione
		else if( (prefix+"113").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.PROFILO_TRASMISSIONE;
		}

		// Sequenza
		else if( (prefix+"114").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_VALIDA;
		}

		// ListaRiscontri
		else if( (prefix+"115").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.LISTA_RISCONTRI;
		}

		// ListaTrasmissioni
		else if( (prefix+"116").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.LISTA_TRASMISSIONI;
		}

		// FirmaNonValida
		else if( (prefix+"117").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.SICUREZZA_FIRMA_INTESTAZIONE_NON_VALIDA;
		}

		// Allegato riferimento non valido
		else if( (prefix+"118").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.ALLEGATI_RIFERIMENTO_NON_VALIDO;
		}

		// Allegato non presente
		else if( (prefix+"119").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.ALLEGATO_NON_PRESENTE;
		}

		// Allegato non definito
		else if( (prefix+"120").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.ALLEGATO_NON_DEFINITO_MANIFEST;
		}

		// Politiche di sicurezza
		else if( (prefix+"200").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.SICUREZZA;
		}

		// Mittente non autorizzato
		else if( (prefix+"201").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA;
		}

		// Firma non valida
		else if( (prefix+"202").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA;
		}	

		// Firma non valida
		else if( (prefix+"203").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.SICUREZZA_FIRMA_PKCS7_ALLEGATO_NON_VALIDA;
		}		

		// Eccezione processamento
		else if( (prefix+"300").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO;
		}	

		// MessaggioScaduto
		else if( (prefix+"301").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.MESSAGGIO_SCADUTO;
		}	

		// ECCEZIONE_GESTIONE_TRASPARENZA_TEMPORALE
		else if( (prefix+"400").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.CONSEGNA_IN_ORDINE;
		}	

		// ECCEZIONE_TRASPARENZA_TEMPORALE_NON_SUPPORTATA
		else if( (prefix+"401").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_SUPPORTATA;
		}	

		// ECCEZIONE_TRASPARENZA_TEMPORALE_NON_GESTIBILE
		else if( (prefix+"402").equals(codiceCooperazione)){
			return CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_GESTIBILE;
		}	

		else{
			return CodiceErroreCooperazione.UNKNOWN;
		}

	}

	@Override
	public String toString(MessaggiFaultErroreCooperazione msg){
		switch (msg) {
		case FAULT_STRING_PROCESSAMENTO:
			return "EGOV_IT_300 - Errore nel processamento del messaggio SPCoop";
		case FAULT_STRING_PROCESSAMENTO_SENZA_CODICE:
			return "Errore nel processamento del messaggio SPCoop";
		case FAULT_STRING_VALIDAZIONE:
			return "EGOV_IT_001 - Formato Busta non corretto";
		default:
			return msg.toString();
		}
	}

	@Override
	public String toString(ErroreCooperazione msg){

		try{

			if(msg.getCodiceErrore()!=null){
				switch (msg.getCodiceErrore()) {

				case MITTENTE_SCONOSCIUTO:
				case MITTENTE_NON_VALIDO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString();
					
				case TIPO_MITTENTE_NON_VALIDO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString();

				case DESTINATARIO_SCONOSCIUTO:
				case DESTINATARIO_NON_VALIDO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString();
					
				case TIPO_DESTINATARIO_NON_VALIDO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString();

				case SERVIZIO_SCONOSCIUTO:
				case SERVIZIO_NON_VALIDO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE.toString();
					
				case TIPO_SERVIZIO_NON_VALIDO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE_TIPO.toString();

				case AZIONE_NON_VALIDA:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE.toString();
					
				case IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_VALIDO_POSIZIONE.toString();

				case RIFERIMENTO_MESSAGGIO_NON_PRESENTE:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_RIFERIMENTO_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString();
				case RIFERIMENTO_MESSAGGIO_NON_VALIDO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_RIFERIMENTO_MESSAGGIO_NON_VALIDO_POSIZIONE.toString();

				case MESSAGGIO_SCADUTO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_MESSAGGIO_SCADUTO_POSIZIONE.toString();

				case PROFILO_COLLABORAZIONE_SCONOSCIUTO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE.toString();
				case PROFILO_COLLABORAZIONE_NON_VALIDO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE.toString();

				case COLLABORAZIONE_NON_VALIDA:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString();
				case COLLABORAZIONE_SCONOSCIUTA:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString();

				case PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE_NON_PRESENTE:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_CONFERMA_RICEZIONE.toString();

				case CONSEGNA_IN_ORDINE_NON_GESTIBILE:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE.toString();
				case CONSEGNA_IN_ORDINE_FUORI_SEQUENZA:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_NUMERO_PROGRESSIVO_BUSTA_CAPOSTIPITE.toString();
				case CONSEGNA_IN_ORDINE_TIPO_MITTENTE_NON_VALIDO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_TIPO_MITTENTE.toString();
				case CONSEGNA_IN_ORDINE_MITTENTE_NON_VALIDO:		
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_MITTENTE.toString();	
				case CONSEGNA_IN_ORDINE_TIPO_DESTINATARIO_NON_VALIDO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_TIPO_DESTINATARIO.toString();
				case CONSEGNA_IN_ORDINE_DESTINATARIO_NON_VALIDO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_DESTINATARIO.toString();
				case CONSEGNA_IN_ORDINE_TIPO_SERVIZIO_NON_VALIDO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_TIPO_SERVIZIO.toString();
				case CONSEGNA_IN_ORDINE_SERVIZIO_NON_VALIDO:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_SERVIZIO.toString();
				case CONSEGNA_IN_ORDINE_AZIONE_NON_VALIDA:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_AZIONE.toString();
				case CONSEGNA_IN_ORDINE_COLLABORAZIONE_IN_BUSTA_NON_CAPOSTIPITE_SCONOSCIUTA:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_COLLABORAZIONE.toString();
				case CONSEGNA_IN_ORDINE_NON_SUPPORTATA:
					return SPCoopCostantiPosizioneEccezione.ECCEZIONE_TRASPARENZA_TEMPORALE_NON_SUPPORTATA_POSIZIONE.toString();

				case SICUREZZA:
				case SICUREZZA_CIFRATURA_ALLEGATO_NON_PRESENTE:
				case SICUREZZA_CIFRATURA_NON_PRESENTE:
				case SICUREZZA_CIFRATURA_ALLEGATO_NON_VALIDA:
				case SICUREZZA_CIFRATURA_NON_VALIDA:
				case SICUREZZA_FIRMA_ALLEGATO_NON_PRESENTE:
				case SICUREZZA_FIRMA_ALLEGATO_NON_VALIDA:
				case SICUREZZA_FIRMA_INTESTAZIONE_NON_PRESENTE:
				case SICUREZZA_FIRMA_INTESTAZIONE_NON_VALIDA:
				case SICUREZZA_FIRMA_NON_PRESENTE:
				case SICUREZZA_FIRMA_NON_VALIDA:
				case SICUREZZA_FIRMA_PKCS7_ALLEGATO_NON_PRESENTE:
				case SICUREZZA_FIRMA_PKCS7_ALLEGATO_NON_VALIDA:
				case SICUREZZA_NON_PRESENTE:
				case SICUREZZA_FALSIFICAZIONE_MITTENTE:
				case SICUREZZA_AUTORIZZAZIONE_FALLITA:
					return msg.getDescrizioneRawValue();

				case ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO:
					return msg.getDescrizioneRawValue();

				case FORMATO_NON_CORRETTO:
					return msg.getDescrizioneRawValue();
					
				default:
					return msg.getDescrizioneRawValue();
				}
			}
			else{
				return  msg.getDescrizioneRawValue();
			}

		}catch(Exception e){
			this.log.error("Errore durante la trasformazione del messaggio di cooperazione: "+e.getMessage(),e);
			return  msg.getDescrizioneRawValue();
		}
	}

	@Override
	public String getDate_protocolFormat() {
		return getDate_protocolFormat(null);
	}

	@Override
	public String getDate_protocolFormat(Date date) {
		return SPCoopUtils.getDate_eGovFormat(date);
	}

	/**
	 * Restituisce il valore dell'identificativo porta di default per il soggetto.
	 * @param soggetto
	 * @return identificativoPortaDefault
	 */
	@Override
	public String getIdentificativoPortaDefault(IDSoggetto soggetto){
		return soggetto.getNome()+"SPCoopIT";
	}

	@Override
	public String toString(Inoltro inoltro) {
		switch (inoltro) {
		case CON_DUPLICATI: return SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI;
		case SENZA_DUPLICATI: return SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI;
		default: return SPCoopCostanti.PROFILO_TRASMISSIONE_SCONOSCIUTO;
		}
	}

	@Override
	public String toString(ProfiloDiCollaborazione profilo) {
		switch (profilo) {
		case ONEWAY: return SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY;
		case SINCRONO: return SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO;
		case ASINCRONO_SIMMETRICO: return SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO;
		case ASINCRONO_ASIMMETRICO: return SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO;
		default: return SPCoopCostanti.PROFILO_COLLABORAZIONE_SCONOSCIUTO;
		}
	}

	@Override
	public String toString(TipoOraRegistrazione tipo) {
		switch (tipo) {
		case LOCALE: return SPCoopCostanti.TIPO_TEMPO_LOCALE;
		case SINCRONIZZATO: return SPCoopCostanti.TIPO_TEMPO_SPC;
		default: return SPCoopCostanti.TIPO_TEMPO_SCONOSCIUTO;
		}
	}

	@Override
	public String toString(LivelloRilevanza rilevanza) {
		switch (rilevanza) {
		case DEBUG: return SPCoopCostanti.ECCEZIONE_RILEVANZA_LIEVE;
		case INFO: return SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO;
		case WARN: return SPCoopCostanti.ECCEZIONE_RILEVANZA_LIEVE;
		case ERROR: return SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE;
		case FATAL: return SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE;
		default: return SPCoopCostanti.ECCEZIONE_RILEVANZA_SCONOSCIUTO;
		}
	}

	@Override
	public LivelloRilevanza toLivelloRilevanza(String livelloRilevanza) {
		if(livelloRilevanza==null) return LivelloRilevanza.UNKNOWN;
		if(livelloRilevanza.equals(SPCoopCostanti.ECCEZIONE_RILEVANZA_LIEVE)) return LivelloRilevanza.DEBUG;
		if(livelloRilevanza.equals(SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO)) return LivelloRilevanza.INFO;
		if(livelloRilevanza.equals(SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE)) return LivelloRilevanza.ERROR;
		return LivelloRilevanza.UNKNOWN;
	}

	@Override
	public Inoltro toInoltro(String inoltro) {
		if(inoltro == null) return Inoltro.UNKNOWN;
		if(inoltro.equals(SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI)) return Inoltro.SENZA_DUPLICATI;
		if(inoltro.equals(SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI)) return Inoltro.CON_DUPLICATI;
		return Inoltro.UNKNOWN;
	}

	@Override
	public TipoOraRegistrazione toTipoOraRegistrazione(String tipoOraRegistrazione) {
		if(tipoOraRegistrazione == null) return TipoOraRegistrazione.UNKNOWN;
		if(tipoOraRegistrazione.equals(SPCoopCostanti.TIPO_TEMPO_LOCALE)) return TipoOraRegistrazione.LOCALE;
		if(tipoOraRegistrazione.equals(SPCoopCostanti.TIPO_TEMPO_SPC)) return TipoOraRegistrazione.SINCRONIZZATO;
		return TipoOraRegistrazione.UNKNOWN;
	}

	@Override
	public ProfiloDiCollaborazione toProfiloDiCollaborazione(
			String profiloDiCollaborazione) {
		if(profiloDiCollaborazione == null) return ProfiloDiCollaborazione.UNKNOWN;
		if(profiloDiCollaborazione.equals(SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY)) return ProfiloDiCollaborazione.ONEWAY;
		if(profiloDiCollaborazione.equals(SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO)) return ProfiloDiCollaborazione.SINCRONO;
		if(profiloDiCollaborazione.equals(SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO)) return ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO;
		if(profiloDiCollaborazione.equals(SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO)) return ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO;
		return ProfiloDiCollaborazione.UNKNOWN;
	}

	@Override
	public String toString(ContestoCodificaEccezione contesto) {
		switch (contesto) {
		case INTESTAZIONE:
			return SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE;
		case PROCESSAMENTO:
			return SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_PROCESSAMENTO;
		default:
			return SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE;	
		}
	}

	@Override
	public ContestoCodificaEccezione toContestoCodificaEccezione(
			String contestoCodificaEccezione) {
		if(SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE.equals(contestoCodificaEccezione)){
			return ContestoCodificaEccezione.INTESTAZIONE;
		}
		else{
			return ContestoCodificaEccezione.PROCESSAMENTO;
		}
	}

	@Override
	protected String getIdentificativoCodiceIPA_Nome(IDSoggetto idSoggetto){
		String soggetto = null;
		if("SPC".equals(idSoggetto.getTipo())){
			// standard, utilizzo solo il nome
			soggetto = idSoggetto.getNome();
		}else{
			// non standard, utilizzo tipo/nome per avere l'univocita'
			soggetto = idSoggetto.toString();
		}
		return soggetto;
	}
}
