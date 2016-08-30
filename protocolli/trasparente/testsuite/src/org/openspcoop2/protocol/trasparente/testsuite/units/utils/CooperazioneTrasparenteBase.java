/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import java.util.Date;

import org.slf4j.Logger;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.testsuite.units.UnitsDatabaseProperties;
import org.openspcoop2.testsuite.units.UnitsTestSuiteProperties;
import org.testng.Assert;
import org.testng.Reporter;

/**
 * CooperazioneTrasparenteBase
 * 
 * @author Andreea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CooperazioneTrasparenteBase extends CooperazioneBase {

	public CooperazioneTrasparenteBase(boolean soapWithAttachments, boolean portaDelegata,
			SOAPVersion soapVersion, CooperazioneBaseInformazioni info,
			UnitsTestSuiteProperties unitsTestsuiteProperties,
			UnitsDatabaseProperties unitsDatabaseProperties, Logger log) {
		super(soapWithAttachments, soapVersion, info, unitsTestsuiteProperties,
				unitsDatabaseProperties, log, portaDelegata);
	}

	public void testOneWayAutenticato(DatabaseComponent data,
			DatabaseMsgDiagnosticiComponent msgDiagData, String id,
			String tipoServizio, String nomeServizio,boolean checkServizioApplicativo,
			Date date) {
		this.testOneWay(data, msgDiagData, id, tipoServizio, nomeServizio, checkServizioApplicativo);
		
		if(this.portaDelegata) {
			try{
				if(!msgDiagData.isTracedMessaggioWithLike(date, "Ricevuta richiesta di servizio dal Servizio Applicativo ( Basic Username:")) {
					Reporter.log("Diagnostico attestante l'autenticazione non trovato per il messaggio con id: " +id);
					Assert.fail("Diagnostico attestante l'autenticazione non trovato per il messaggio con id: " +id);
				}
			} catch(Exception e) {
				Reporter.log("Errore durante la ricerca del diagnostico attestante l'autenticazione non trovato per il messaggio con id: " +id + ". "+e.getMessage());
				Assert.fail("Errore durante la ricerca del diagnostico attestante l'autenticazione non trovato per il messaggio con id: " +id + ". "+e.getMessage());
			}
		}
	}
	

	public void testOneWay(DatabaseComponent data,
			DatabaseMsgDiagnosticiComponent msgDiagData, String id,
			String tipoServizio, String nomeServizio,boolean checkServizioApplicativo) {

		this.testOneWay(data, id, tipoServizio, nomeServizio,null, checkServizioApplicativo,null);
		
		verifyOk(data, msgDiagData, id);

		
	}
	
	public void testOneWayLocalForward(DatabaseComponent data,
			DatabaseMsgDiagnosticiComponent msgDiagData, String id,
			String tipoServizio, String nomeServizio,boolean checkServizioApplicativo) {

		verifyOk(data, msgDiagData, id);
		verifyOkLocalForward(msgDiagData, id);
	}
	
	public void testSincronoAutenticato(DatabaseComponent data,
			DatabaseMsgDiagnosticiComponent msgDiagData, String id,
			String tipoServizio, String nomeServizio,boolean checkServizioApplicativo, Date date) {
		this.testSincrono(data, msgDiagData, id, tipoServizio, nomeServizio, checkServizioApplicativo);
		if(this.portaDelegata) {
			try{
				if(!msgDiagData.isTracedMessaggioWithLike(date, "Ricevuta richiesta di servizio dal Servizio Applicativo ( Basic Username:")) {
					Reporter.log("Diagnostico attestante l'autenticazione non trovato per il messaggio con id: " +id);
					Assert.fail("Diagnostico attestante l'autenticazione non trovato per il messaggio con id: " +id);
				}
			} catch(Exception e) {
				Reporter.log("Errore durante la ricerca del diagnostico attestante l'autenticazione per il messaggio con id: " +id + ". "+e.getMessage());
				Assert.fail("Errore durante la ricerca del diagnostico attestante l'autenticazione per il messaggio con id: " +id + ". "+e.getMessage());
			}
		}
	}
	
	public void testSincrono(DatabaseComponent data,
			DatabaseMsgDiagnosticiComponent msgDiagData, String id,
			String tipoServizio, String nomeServizio, boolean checkServizioApplicativo) {

		this.testSincrono(data, id, tipoServizio, nomeServizio,null, checkServizioApplicativo,null);
		
		verifyOk(data, msgDiagData, id);
	}
	
	public void testSincronoLocalForward(DatabaseComponent data,
			DatabaseMsgDiagnosticiComponent msgDiagData, String id,
			String tipoServizio, String nomeServizio, boolean checkServizioApplicativo) {

		verifyOk(data, msgDiagData, id);
		verifyOkLocalForward(msgDiagData, id);
	}

	private void verifyOkLocalForward(DatabaseMsgDiagnosticiComponent msgDiagData, String id) {

		try{
			if(!msgDiagData.isTracedMessaggioWithLike(id, "Modalità 'local-forward' attiva, messaggio ruotato direttamente verso la porta applicativa ")) {
				Reporter.log("Diagnostico attestante la modalita' local-forward non trovato per il messaggio con id: " +id);
				Assert.fail("Diagnostico attestante la modalita' local-forward non trovato per il messaggio con id: " +id);
			}
		} catch(Exception e) {
			Reporter.log("Errore durante la ricerca del diagnostico attestante la modalita' local-forward per il messaggio con id: " +id + ". "+e.getMessage());
			Assert.fail("Errore durante la ricerca del diagnostico attestante la modalita' local-forward per il messaggio con id: " +id + ". "+e.getMessage());
		}

		try{
			if(!msgDiagData.isTracedMessaggioWithLike(id, "consegnato al servizio applicativo")) {
				Reporter.log("Diagnostico attestante la consegna al servizio applicativo non trovato per il messaggio con id: " +id);
				Assert.fail("Diagnostico attestante la consegna al servizio applicativo non trovato per il messaggio con id: " +id);
			}
		} catch(Exception e) {
			Reporter.log("Errore durante la ricerca del diagnostico attestante la consegna al servizio applicativo per il messaggio con id: " +id + ". "+e.getMessage());
			Assert.fail("Errore durante la ricerca del diagnostico attestante la consegna al servizio applicativo per il messaggio con id: " +id + ". "+e.getMessage());
		}

	}

	
	private String getTipoMessaggio() {
		return this.portaDelegata ? Costanti.OUTBOX : Costanti.INBOX;
	} 
	public void verifyOk(DatabaseComponent data,
			DatabaseMsgDiagnosticiComponent msgDiagData, String id) {
		if(data.getVerificatoreMessaggi().existsMessaggioInProcessamento(id, this.getTipoMessaggio())) {
			Reporter.log("Trovati messaggi in processamento per il messaggio con id: " +id);
			Assert.fail("Trovati messaggi in processamento per il messaggio con id: " +id);
		}
		
		long count = msgDiagData.countSeveritaLessEquals(id, LogLevels.SEVERITA_ERROR_INTEGRATION);
		if(count > 0) {
			Reporter.log("Trovati ["+count+"] messaggi diagnostici di errore per il messaggio con id: " +id);
			Assert.fail("Trovati ["+count+"] messaggi diagnostici di errore per il messaggio con id: " +id);
		}
	}

	public void testFaultOneWay(DatabaseComponent data,
			DatabaseMsgDiagnosticiComponent msgDiagData, String id,
			String tipoServizio, String nomeServizio,
			boolean is500, boolean stateful,
			boolean checkServizioApplicativo) throws Exception {
	
		this.testOneWay(data, id, tipoServizio, nomeServizio,null, checkServizioApplicativo,null);
		boolean checkMsgInProcessamento = !(!this.portaDelegata && is500 && stateful);
		this.testFaultDiag(data, msgDiagData, id, tipoServizio, nomeServizio, is500, stateful, checkMsgInProcessamento);
	}

	public void testFaultSincrono(DatabaseComponent data,
			DatabaseMsgDiagnosticiComponent msgDiagData, String id,
			String tipoServizio, String nomeServizio,
			boolean is500, boolean stateful) throws Exception {
	
		this.testSincrono(data, id, tipoServizio, nomeServizio,null, true,null);
		this.testFaultDiag(data, msgDiagData, id, tipoServizio, nomeServizio, is500, stateful, false);
	}

	public void testFaultDiag(DatabaseComponent data,
			DatabaseMsgDiagnosticiComponent msgDiagData, String id,
			String proxyTipoServizio, String proxyNomeServizioOneway,
			boolean is500, boolean stateful, boolean checkMsgInProcessamento) throws Exception {
		
		
		
		long count = msgDiagData.countSeveritaLessEquals(id, LogLevels.SEVERITA_ERROR_INTEGRATION);
		
		
		if(count <= 0) {
			Reporter.log("Attesi messaggi diagnostici di errore per il messaggio con id: " +id + ". Trovati ["+count+"].");
			Assert.fail("Attesi messaggi diagnostici di errore per il messaggio con id: " +id + ". Trovati ["+count+"].");
		}

		String codiceMsg = is500 ?  "500" : "200";

		if(this.portaDelegata) {
			if(!msgDiagData.isTracedMessaggio(id, true, "Messaggio di cooperazione con identificativo", "inviato alla parte destinataria", codiceMsg)) {
				Reporter.log("Attesi messaggi diagnostici che rilevino l'invio.");
				Assert.fail("Attesi messaggi diagnostici che rilevino l'invio.");
			}
			
			if(!msgDiagData.isTracedCodice(id, "003013")) {
				Reporter.log("Attesi messaggi diagnostici con codice 003013.");
				Assert.fail("Attesi messaggi diagnostici con codice 003013.");
			}
		} else {
			if(!msgDiagData.isTracedMessaggio(id, true, "Messaggio applicativo con ID", "generato dal mittente", "consegnato al servizio applicativo", codiceMsg)) {
				Reporter.log("Attesi messaggi diagnostici che rilevino l'invio.");
				Assert.fail("Attesi messaggi diagnostici che rilevino l'invio.");
			}
			
			if(!msgDiagData.isTracedCodice(id, "007014")) {
				Reporter.log("Attesi messaggi diagnostici con codice 007014.");
				Assert.fail("Attesi messaggi diagnostici con codice 007014.");
			}
		}
		
		if(checkMsgInProcessamento && stateful && is500) {
			if(data.getVerificatoreMessaggi().existsMessaggioInProcessamento(id, this.getTipoMessaggio())) {
				data.getVerificatoreMessaggi().deleteMessage(id, this.getTipoMessaggio(),Utilities.testSuiteProperties.isUseTransazioni());
			} else {
				Reporter.log("Atteso messaggio in processamento per il messaggio con id: " +id);
				Assert.fail("Atteso messaggio in processamento per il messaggio con id: " +id);
			}
		}

	}

}
