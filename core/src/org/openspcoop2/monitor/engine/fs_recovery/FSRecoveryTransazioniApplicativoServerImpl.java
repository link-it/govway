/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.monitor.engine.fs_recovery;

import java.io.File;
import java.sql.Connection;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.utils.TransactionServerUtils;
import org.openspcoop2.core.transazioni.utils.serializer.JaxbDeserializer;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsMultiException;
import org.slf4j.Logger;

/**
 * FSRecoveryTransazioniImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FSRecoveryTransazioniApplicativoServerImpl extends AbstractFSRecovery {
	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM;
	private DAOFactory daoFactory;
	private Logger daoFactoryLogger;
	private ServiceManagerProperties daoFactoryServiceManagerProperties;
	private long gestioneSerializableDBAttesaAttiva;
	private int gestioneSerializableDBCheckInterval;
	
	public FSRecoveryTransazioniApplicativoServerImpl( 
			Logger log,
			boolean debug,
			DAOFactory daoFactory, Logger daoFactoryLogger, ServiceManagerProperties daoFactoryServiceManagerProperties,
			long gestioneSerializableDBAttesaAttiva, int gestioneSerializableDBCheckInterval,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM, 
			File directory, File directoryDLQ,
			int tentativi,
			long msAttesaProcessingFile) {
		super(log, debug, directory, directoryDLQ, tentativi, msAttesaProcessingFile);
		
		this.transazioniSM = transazioniSM;
		this.daoFactory = daoFactory;
		this.daoFactoryLogger = daoFactoryLogger;
		this.daoFactoryServiceManagerProperties = daoFactoryServiceManagerProperties;
		this.gestioneSerializableDBAttesaAttiva = gestioneSerializableDBAttesaAttiva;
		this.gestioneSerializableDBCheckInterval = gestioneSerializableDBCheckInterval;
	}

	@Override
	public void process(Connection connection) {
		this.log.info("Recovery TransazioneServerApplicativo ...");
		super.process(connection);
		this.log.info("Recovery TransazioneServerApplicativo completato");
	}
	
	@Override
	public void insertObject(File file, Connection connection) throws UtilsException, UtilsMultiException {
		JaxbDeserializer deserializer = new JaxbDeserializer();
		boolean ripristinato = false;
		TransazioneApplicativoServer transazioneApplicativoServer = null;
		try {
			transazioneApplicativoServer = deserializer.readTransazioneApplicativoServer(file);
			ripristinato = TransactionServerUtils.recover(this.transazioniSM.getTransazioneApplicativoServerService(), transazioneApplicativoServer);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		if(ripristinato) {
			
			int esitoConsegnaMultipla = -1;
			int esitoConsegnaMultiplaInCorso = -1;
			int esitoConsegnaMultiplaFallita = -1;
			int esitoConsegnaMultiplaCompletata = -1;
			int ok = -1;
			int esitoIntegrationManagerSingolo = -1;
			boolean esitiLetti = false;
			try {
				EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(this.log, transazioneApplicativoServer.getProtocollo());
				esitoConsegnaMultipla = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA);
				esitoConsegnaMultiplaInCorso = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA_IN_CORSO);
				esitoConsegnaMultiplaFallita = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA_FALLITA);
				esitoConsegnaMultiplaCompletata = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA_COMPLETATA);
				ok = esitiProperties.convertoToCode(EsitoTransazioneName.OK);
				esitoIntegrationManagerSingolo = esitiProperties.convertoToCode(EsitoTransazioneName.MESSAGE_BOX);
				esitiLetti = true;			
			}catch(Exception er) {
				// errore che non dovrebbe succedere
				String msg = "Errore durante l'aggiornamento delle transazione relativamente all'informazione del server '"+transazioneApplicativoServer.getServizioApplicativoErogatore()+"': (readEsiti) " + er.getMessage();
				this.log.error("["+transazioneApplicativoServer.getIdTransazione()+"] "+msg,er);
			}
			
			if(esitiLetti) {
				
				try{
				
					boolean autoCommit = false;
					try {
						connection.setAutoCommit(autoCommit);
					}catch(Exception e) {
						throw new UtilsException(e.getMessage(),e);
					}
					
					@SuppressWarnings("unused")
					boolean isMessaggioConsegnato = false;
					boolean possibileTerminazioneSingleIntegrationManagerMessage = false;
					boolean consegnaInErrore = false;
					// consegna terminata
					if(transazioneApplicativoServer.isConsegnaTerminata()) {
						isMessaggioConsegnato = true;
					}
					else if(transazioneApplicativoServer.getDataEliminazioneIm()!=null ||
							transazioneApplicativoServer.getDataMessaggioScaduto()!=null) {
						isMessaggioConsegnato = true;
						possibileTerminazioneSingleIntegrationManagerMessage = true;
					}
					else if(transazioneApplicativoServer.isConsegnaTrasparente() && transazioneApplicativoServer.getNumeroTentativi()>0) {
						// !transazioneApplicativoServer.isConsegnaTerminata() altrimenti entrava nel primo if
						consegnaInErrore = true;
					}
					
					// Grazie all'istruzione sopra 'boolean ripristinato = TransactionServerUtils.recover' entro nell'if solo se la consegna e' quella terminata
					/**if(isMessaggioConsegnato) {*/ 
					TransactionServerUtils.safeAggiornaInformazioneConsegnaTerminata(transazioneApplicativoServer, connection, 
							this.daoFactoryServiceManagerProperties.getDatabaseType(), this.log,
							this.daoFactory,this.daoFactoryLogger,this.daoFactoryServiceManagerProperties,
							this.debug,
							esitoConsegnaMultipla, esitoConsegnaMultiplaInCorso, esitoConsegnaMultiplaFallita, esitoConsegnaMultiplaCompletata, ok,
							esitoIntegrationManagerSingolo, possibileTerminazioneSingleIntegrationManagerMessage, consegnaInErrore,
							this.gestioneSerializableDBAttesaAttiva,this.gestioneSerializableDBCheckInterval,
							null);
					/**}*/
					
				}finally{
					
					try{
						connection.setAutoCommit(true);
					}catch(Exception eRollback){
						// ignore
					}
				}
			}

		}
	}


}
