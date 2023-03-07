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



package org.openspcoop2.pdd.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.date.DateManager;

/**
 * GestoreCorrelazioneApplicativaPSUtilities
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class GestoreCorrelazioneApplicativaPSUtilities {

	// In questa classe vengono registrati i metodi per cui vengono collezionate le prepared statement
	
	public static void applicaCorrelazione(GestoreCorrelazioneApplicativa gestore,
			CorrelazioneApplicativa correlazioneApplicativa,String idApplicativo,String idBustaRequest) throws GestoreMessaggiException, ProtocolException{

		if(correlazioneApplicativa==null){
			gestore.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
					getErrore416_CorrelazioneApplicativaRichiesta("dati per l'identificazione dell'id di correlazione non presenti");
			throw new GestoreMessaggiException(gestore.errore.getDescrizione(gestore.protocolFactory));
		}
		if(idBustaRequest==null){
			gestore.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
					getErrore416_CorrelazioneApplicativaRichiesta("identificativo non presente tra i parametri di invocazione");
			throw new GestoreMessaggiException(gestore.errore.getDescrizione(gestore.protocolFactory));
		}
		if(idApplicativo==null){
			gestore.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
					getErrore416_CorrelazioneApplicativaRichiesta("identificativo applicativo non presente tra i parametri di invocazione");
			throw new GestoreMessaggiException(gestore.errore.getDescrizione(gestore.protocolFactory));
		}

		Timestamp scadenzaCorrelazioneT = null;
		if(correlazioneApplicativa.getScadenza()!=null){
			try{
				long scadenza = Long.parseLong(correlazioneApplicativa.getScadenza());
				scadenzaCorrelazioneT = new Timestamp(DateManager.getTimeMillis()+(scadenza*60*1000));
			}catch(Exception e){
				gestore.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA);
				throw new GestoreMessaggiException("Scadenza impostata per la correlazione applicativa non corretta: "+e.getMessage(),e);
			}
		}
		// E' stata introdotta l'ora di registrazione per gestire le scadenze null
//		else{
//			scadenzaCorrelazioneT = new Timestamp(DateManager.getTimeMillis()+(this.scadenzaDefault*60*1000));
//		}

		/** Fase di verifica dell'id di correlazione con l'id */
		PreparedStatement pstmtInsert = null;
		try{
			StateMessage stateMSG = (StateMessage)gestore.state;
			Connection connectionDB = stateMSG.getConnectionDB();

			// nuova correlazione
			StringBuilder queryInsert = new StringBuilder();
			queryInsert.append("INSERT INTO "+GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA);
			queryInsert.append(" (ID_MESSAGGIO,ID_APPLICATIVO,SERVIZIO_APPLICATIVO,TIPO_MITTENTE,MITTENTE,TIPO_DESTINATARIO,DESTINATARIO,TIPO_SERVIZIO,SERVIZIO,VERSIONE_SERVIZIO,AZIONE ");
			if(scadenzaCorrelazioneT!=null){
				queryInsert.append(",SCADENZA");
			}
			queryInsert.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?");
			if(scadenzaCorrelazioneT!=null){
				queryInsert.append(",?");
			}
			queryInsert.append(")");
			pstmtInsert = connectionDB.prepareStatement(queryInsert.toString());
			int index = 1;
			pstmtInsert.setString(index++,idBustaRequest);
			pstmtInsert.setString(index++,idApplicativo);
			pstmtInsert.setString(index++,gestore.servizioApplicativo);
			pstmtInsert.setString(index++,gestore.soggettoFruitore.getTipo());
			pstmtInsert.setString(index++,gestore.soggettoFruitore.getNome());
			pstmtInsert.setString(index++,gestore.idServizio.getSoggettoErogatore().getTipo());
			pstmtInsert.setString(index++,gestore.idServizio.getSoggettoErogatore().getNome());
			pstmtInsert.setString(index++,gestore.idServizio.getTipo());
			pstmtInsert.setString(index++,gestore.idServizio.getNome());
			pstmtInsert.setInt(index++,gestore.idServizio.getVersione());
			pstmtInsert.setString(index++,gestore.idServizio.getAzione());
			if(scadenzaCorrelazioneT!=null){
				pstmtInsert.setTimestamp(index++,scadenzaCorrelazioneT);
			}

			//	Add PreparedStatement
			String valoreAzione = "N.D.";
			if( (gestore.idServizio.getAzione()!=null) && ("".equals(gestore.idServizio.getAzione())==false) ){
				valoreAzione = gestore.idServizio.getAzione();
			}
			stateMSG.getPreparedStatement().put("INSERT CorrelazioneApplicativa_"+idBustaRequest+"_"+idApplicativo+"_"+gestore.soggettoFruitore.getTipo()+gestore.soggettoFruitore.getNome()+
					"_"+gestore.idServizio.getSoggettoErogatore().getTipo()+gestore.idServizio.getSoggettoErogatore().getNome()+"_"+
					gestore.idServizio.getTipo()+gestore.idServizio.getNome()+":"+gestore.idServizio.getVersione()+"_"+valoreAzione,pstmtInsert);

		}catch(Exception er){
			gestore.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA);
			gestore.log.error("Correlazione IDApplicativo - ID non riuscita: "+er.getMessage());
			throw new GestoreMessaggiException("Correlazione IDApplicativo - ID non riuscita: "+er.getMessage(),er);
		}

	}

}

