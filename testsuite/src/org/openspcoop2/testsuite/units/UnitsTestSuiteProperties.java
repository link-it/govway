/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.testsuite.units;

/**
 * UnitsTestSuiteProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface UnitsTestSuiteProperties {

	
	/**
	 * Indicazione sulla versione dell'Application Server
	 * 
	 * @return application server version
	 */
	public String getApplicationServerVersion();
	
	
	
	/**
	 * Ritorna Servizio di ricezione contenuti applicativi della Porta di Dominio
	 * della porta di dominio fruitore
	 *
	 */
	public String getServizioRicezioneContenutiApplicativiFruitore();
	/**
	 * Ritorna Servizio di ricezione contenuti applicativi della Porta di Dominio
	 * della porta di dominio erogatore
	 *
	 */
	public String getServizioRicezioneContenutiApplicativiErogatore();
	/**
	 * Ritorna Servizio di ricezione contenuti applicativi della Porta di Dominio frutore (https con autenticazione client)
	 *
	 */
	public String getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient();
	/**
	 * Ritorna Servizio di ricezione contenuti applicativi della Porta di Dominio fruitore (govwaySec)
	 *
	 */
	public String getServizioRicezioneContenutiApplicativiFruitore_openspcoop2Sec();
	
	/**
	 * Ritorna Servizio di ricezione buste della Porta di Dominio
	 * della porta di dominio fruitore
	 *
	 */
	public String getServizioRicezioneBusteFruitore();
	/**
	 * Ritorna Servizio di ricezione buste della Porta di Dominio
	 * della porta di dominio erogatore
	 *
	 */
	public String getServizioRicezioneBusteErogatore();
	/**
	 * Ritorna Servizio di ricezione buste della Porta di Dominio erogatore (https con autenticazione client)
	 *
	 */
	public String getServizioRicezioneBusteErogatore_httpsConAutenticazioneClient();
	/**
	 * Ritorna Servizio di ricezione buste della Porta di Dominio erogatore (govwaySec)
	 *
	 */
	public String getServizioRicezioneBusteErogatore_openspcoop2Sec();
	
	
	
	public int getConnectionTimeout();
	public int getReadConnectionTimeout();
	
	
	
	public String getLogDirectoryOpenSPCoop();
	
	
	
	/**
	 * Ritorna la location del file da utilizzare per la spedizione di messaggi Soap 1.1
	 *
	 */
	public String getSoap11FileName();
	
	/**
	 * Ritorna la location del file da utilizzare per la spedizione di messaggi Soap 1.2
	 *
	 */
	public String getSoap12FileName();
	
	/**
	 * Ritorna la location del file da utilizzare per la spedizione di messaggi Soap With Attachments 1.1
	 *
	 */
	public String getSoap11WithAttachmentsFileName();
	
	/**
	 * Ritorna la location del file da utilizzare per la spedizione di messaggi Soap With Attachments 1.2
	 *
	 */
	public String getSoap12WithAttachmentsFileName();
	
	
	
	/**
	 * Ritorna true se devono essere verificati la terminazione dei msg sul database, prima della verifica sul database
	 *
	 */
	public boolean attendiTerminazioneMessaggi_verificaDatabase();
	
	/**
	 * Ritorna il  Tempo di attesa in millisecondi, prima della verifica sul database
	 *
	 */
	public long timeToSleep_verificaDatabase();
	
	
	
	/**
	 * Ritorna il Numero di Worker per Test
	 *
	 */
	public int getWorkerNumber();
	
	/**
	 * Ritorna la dimensione del Pool dei thread
	 *
	 */
	public int getPoolSize();
	
	/**
	 * Ritorna l'indicazione se sequenzializzare i test
	 *
	 */
	public boolean sequentialTests();
	
	
	
	public String getJMXFactory();
	
	public String getJMXServer();
	
	public String getJMXServiceURL();
	public String getJMXServiceURL(String applicationServer);
	
	public String getJMXUsername();
	public String getJMXPassword();
	
	
	public boolean isSoapEngineAxis14();
	
	public boolean isSoapEngineCxf();
	
	public boolean isUseTransazioni();
	
}
