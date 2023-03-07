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
package org.openspcoop2.core.transazioni.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;


/**     
 * TransazioneApplicativoServerFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneApplicativoServerFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			GenericJDBCParameterUtilities jdbcParameterUtilities =  
					new GenericJDBCParameterUtilities(tipoDatabase);

			if(model.equals(TransazioneApplicativoServer.model())){
				TransazioneApplicativoServer object = new TransazioneApplicativoServer();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setIdTransazione", TransazioneApplicativoServer.model().ID_TRANSAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_transazione", TransazioneApplicativoServer.model().ID_TRANSAZIONE.getFieldType()));
				setParameter(object, "setServizioApplicativoErogatore", TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "servizio_applicativo_erogatore", TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()));
				setParameter(object, "setConnettoreNome", TransazioneApplicativoServer.model().CONNETTORE_NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "connettore_nome", TransazioneApplicativoServer.model().CONNETTORE_NOME.getFieldType()));
				setParameter(object, "setDataRegistrazione", TransazioneApplicativoServer.model().DATA_REGISTRAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_registrazione", TransazioneApplicativoServer.model().DATA_REGISTRAZIONE.getFieldType()));
				setParameter(object, "setConsegnaTerminata", TransazioneApplicativoServer.model().CONSEGNA_TERMINATA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "consegna_terminata", TransazioneApplicativoServer.model().CONSEGNA_TERMINATA.getFieldType()));
				setParameter(object, "setDataMessaggioScaduto", TransazioneApplicativoServer.model().DATA_MESSAGGIO_SCADUTO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_messaggio_scaduto", TransazioneApplicativoServer.model().DATA_MESSAGGIO_SCADUTO.getFieldType()));
				setParameter(object, "setDettaglioEsito", TransazioneApplicativoServer.model().DETTAGLIO_ESITO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "dettaglio_esito", TransazioneApplicativoServer.model().DETTAGLIO_ESITO.getFieldType()));
				setParameter(object, "setConsegnaTrasparente", TransazioneApplicativoServer.model().CONSEGNA_TRASPARENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "consegna_trasparente", TransazioneApplicativoServer.model().CONSEGNA_TRASPARENTE.getFieldType()));
				setParameter(object, "setConsegnaIntegrationManager", TransazioneApplicativoServer.model().CONSEGNA_INTEGRATION_MANAGER.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "consegna_im", TransazioneApplicativoServer.model().CONSEGNA_INTEGRATION_MANAGER.getFieldType()));
				setParameter(object, "setIdentificativoMessaggio", TransazioneApplicativoServer.model().IDENTIFICATIVO_MESSAGGIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "identificativo_messaggio", TransazioneApplicativoServer.model().IDENTIFICATIVO_MESSAGGIO.getFieldType()));
				setParameter(object, "setDataAccettazioneRichiesta", TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_accettazione_richiesta", TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RICHIESTA.getFieldType()));
				setParameter(object, "setDataUscitaRichiesta", TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_uscita_richiesta", TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA.getFieldType()));
				setParameter(object, "setDataUscitaRichiestaStream", TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA_STREAM.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_uscita_richiesta_stream", TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA_STREAM.getFieldType()));
				setParameter(object, "setDataAccettazioneRisposta", TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_accettazione_risposta", TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA.getFieldType()));
				setParameter(object, "setDataIngressoRisposta", TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_ingresso_risposta", TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA.getFieldType()));
				setParameter(object, "setDataIngressoRispostaStream", TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA_STREAM.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_ingresso_risposta_stream", TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA_STREAM.getFieldType()));
				setParameter(object, "setRichiestaUscitaBytes", TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "richiesta_uscita_bytes", TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES.getFieldType()));
				setParameter(object, "setRispostaIngressoBytes", TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "risposta_ingresso_bytes", TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES.getFieldType()));
				setParameter(object, "setLocationConnettore", TransazioneApplicativoServer.model().LOCATION_CONNETTORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "location_connettore", TransazioneApplicativoServer.model().LOCATION_CONNETTORE.getFieldType()));
				setParameter(object, "setCodiceRisposta", TransazioneApplicativoServer.model().CODICE_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "codice_risposta", TransazioneApplicativoServer.model().CODICE_RISPOSTA.getFieldType()));
				setParameter(object, "setFault", TransazioneApplicativoServer.model().FAULT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "fault", TransazioneApplicativoServer.model().FAULT.getFieldType()));
				setParameter(object, "setFormatoFault", TransazioneApplicativoServer.model().FORMATO_FAULT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "formato_fault", TransazioneApplicativoServer.model().FORMATO_FAULT.getFieldType()));
				setParameter(object, "setDataPrimoTentativo", TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_primo_tentativo", TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO.getFieldType()));
				setParameter(object, "setNumeroTentativi", TransazioneApplicativoServer.model().NUMERO_TENTATIVI.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "numero_tentativi", TransazioneApplicativoServer.model().NUMERO_TENTATIVI.getFieldType()));
				setParameter(object, "setClusterIdPresaInCarico", TransazioneApplicativoServer.model().CLUSTER_ID_PRESA_IN_CARICO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cluster_id_in_coda", TransazioneApplicativoServer.model().CLUSTER_ID_PRESA_IN_CARICO.getFieldType()));
				setParameter(object, "setClusterIdConsegna", TransazioneApplicativoServer.model().CLUSTER_ID_CONSEGNA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cluster_id_consegna", TransazioneApplicativoServer.model().CLUSTER_ID_CONSEGNA.getFieldType()));
				setParameter(object, "setDataUltimoErrore", TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_ultimo_errore", TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE.getFieldType()));
				setParameter(object, "setDettaglioEsitoUltimoErrore", TransazioneApplicativoServer.model().DETTAGLIO_ESITO_ULTIMO_ERRORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "dettaglio_esito_ultimo_errore", TransazioneApplicativoServer.model().DETTAGLIO_ESITO_ULTIMO_ERRORE.getFieldType()));
				setParameter(object, "setCodiceRispostaUltimoErrore", TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "codice_risposta_ultimo_errore", TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE.getFieldType()));
				setParameter(object, "setUltimoErrore", TransazioneApplicativoServer.model().ULTIMO_ERRORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "ultimo_errore", TransazioneApplicativoServer.model().ULTIMO_ERRORE.getFieldType()));
				setParameter(object, "setLocationUltimoErrore", TransazioneApplicativoServer.model().LOCATION_ULTIMO_ERRORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "location_ultimo_errore", TransazioneApplicativoServer.model().LOCATION_ULTIMO_ERRORE.getFieldType()));
				setParameter(object, "setClusterIdUltimoErrore", TransazioneApplicativoServer.model().CLUSTER_ID_ULTIMO_ERRORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cluster_id_ultimo_errore", TransazioneApplicativoServer.model().CLUSTER_ID_ULTIMO_ERRORE.getFieldType()));
				setParameter(object, "setFaultUltimoErrore", TransazioneApplicativoServer.model().FAULT_ULTIMO_ERRORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "fault_ultimo_errore", TransazioneApplicativoServer.model().FAULT_ULTIMO_ERRORE.getFieldType()));
				setParameter(object, "setFormatoFaultUltimoErrore", TransazioneApplicativoServer.model().FORMATO_FAULT_ULTIMO_ERRORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "formato_fault_ultimo_errore", TransazioneApplicativoServer.model().FORMATO_FAULT_ULTIMO_ERRORE.getFieldType()));
				setParameter(object, "setDataPrimoPrelievoIm", TransazioneApplicativoServer.model().DATA_PRIMO_PRELIEVO_IM.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_primo_prelievo_im", TransazioneApplicativoServer.model().DATA_PRIMO_PRELIEVO_IM.getFieldType()));
				setParameter(object, "setDataPrelievoIm", TransazioneApplicativoServer.model().DATA_PRELIEVO_IM.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_prelievo_im", TransazioneApplicativoServer.model().DATA_PRELIEVO_IM.getFieldType()));
				setParameter(object, "setNumeroPrelieviIm", TransazioneApplicativoServer.model().NUMERO_PRELIEVI_IM.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "numero_prelievi_im", TransazioneApplicativoServer.model().NUMERO_PRELIEVI_IM.getFieldType()));
				setParameter(object, "setDataEliminazioneIm", TransazioneApplicativoServer.model().DATA_ELIMINAZIONE_IM.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_eliminazione_im", TransazioneApplicativoServer.model().DATA_ELIMINAZIONE_IM.getFieldType()));
				setParameter(object, "setClusterIdPrelievoIm", TransazioneApplicativoServer.model().CLUSTER_ID_PRELIEVO_IM.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cluster_id_prelievo_im", TransazioneApplicativoServer.model().CLUSTER_ID_PRELIEVO_IM.getFieldType()));
				setParameter(object, "setClusterIdEliminazioneIm", TransazioneApplicativoServer.model().CLUSTER_ID_ELIMINAZIONE_IM.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cluster_id_eliminazione_im", TransazioneApplicativoServer.model().CLUSTER_ID_ELIMINAZIONE_IM.getFieldType()));
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

			if(model.equals(TransazioneApplicativoServer.model())){
				TransazioneApplicativoServer object = new TransazioneApplicativoServer();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setIdTransazione", TransazioneApplicativoServer.model().ID_TRANSAZIONE.getFieldType(),
					this.getObjectFromMap(map,"id-transazione"));
				setParameter(object, "setServizioApplicativoErogatore", TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"servizio-applicativo-erogatore"));
				setParameter(object, "setConnettoreNome", TransazioneApplicativoServer.model().CONNETTORE_NOME.getFieldType(),
					this.getObjectFromMap(map,"connettore-nome"));
				setParameter(object, "setDataRegistrazione", TransazioneApplicativoServer.model().DATA_REGISTRAZIONE.getFieldType(),
					this.getObjectFromMap(map,"data-registrazione"));
				setParameter(object, "setConsegnaTerminata", TransazioneApplicativoServer.model().CONSEGNA_TERMINATA.getFieldType(),
					this.getObjectFromMap(map,"consegna-terminata"));
				setParameter(object, "setDataMessaggioScaduto", TransazioneApplicativoServer.model().DATA_MESSAGGIO_SCADUTO.getFieldType(),
					this.getObjectFromMap(map,"data-messaggio-scaduto"));
				setParameter(object, "setDettaglioEsito", TransazioneApplicativoServer.model().DETTAGLIO_ESITO.getFieldType(),
					this.getObjectFromMap(map,"dettaglio-esito"));
				setParameter(object, "setConsegnaTrasparente", TransazioneApplicativoServer.model().CONSEGNA_TRASPARENTE.getFieldType(),
					this.getObjectFromMap(map,"consegna-trasparente"));
				setParameter(object, "setConsegnaIntegrationManager", TransazioneApplicativoServer.model().CONSEGNA_INTEGRATION_MANAGER.getFieldType(),
					this.getObjectFromMap(map,"consegna-integration-manager"));
				setParameter(object, "setIdentificativoMessaggio", TransazioneApplicativoServer.model().IDENTIFICATIVO_MESSAGGIO.getFieldType(),
					this.getObjectFromMap(map,"identificativo-messaggio"));
				setParameter(object, "setDataAccettazioneRichiesta", TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"data-accettazione-richiesta"));
				setParameter(object, "setDataUscitaRichiesta", TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"data-uscita-richiesta"));
				setParameter(object, "setDataUscitaRichiestaStream", TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA_STREAM.getFieldType(),
					this.getObjectFromMap(map,"data-uscita-richiesta-stream"));
				setParameter(object, "setDataAccettazioneRisposta", TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"data-accettazione-risposta"));
				setParameter(object, "setDataIngressoRisposta", TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"data-ingresso-risposta"));
				setParameter(object, "setDataIngressoRispostaStream", TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA_STREAM.getFieldType(),
					this.getObjectFromMap(map,"data-ingresso-risposta-stream"));
				setParameter(object, "setRichiestaUscitaBytes", TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES.getFieldType(),
					this.getObjectFromMap(map,"richiesta-uscita-bytes"));
				setParameter(object, "setRispostaIngressoBytes", TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES.getFieldType(),
					this.getObjectFromMap(map,"risposta-ingresso-bytes"));
				setParameter(object, "setLocationConnettore", TransazioneApplicativoServer.model().LOCATION_CONNETTORE.getFieldType(),
					this.getObjectFromMap(map,"location-connettore"));
				setParameter(object, "setCodiceRisposta", TransazioneApplicativoServer.model().CODICE_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"codice-risposta"));
				setParameter(object, "setFault", TransazioneApplicativoServer.model().FAULT.getFieldType(),
					this.getObjectFromMap(map,"fault"));
				setParameter(object, "setFormatoFault", TransazioneApplicativoServer.model().FORMATO_FAULT.getFieldType(),
					this.getObjectFromMap(map,"formato-fault"));
				setParameter(object, "setDataPrimoTentativo", TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO.getFieldType(),
					this.getObjectFromMap(map,"data-primo-tentativo"));
				setParameter(object, "setNumeroTentativi", TransazioneApplicativoServer.model().NUMERO_TENTATIVI.getFieldType(),
					this.getObjectFromMap(map,"numero-tentativi"));
				setParameter(object, "setClusterIdPresaInCarico", TransazioneApplicativoServer.model().CLUSTER_ID_PRESA_IN_CARICO.getFieldType(),
					this.getObjectFromMap(map,"cluster-id-presa-in-carico"));
				setParameter(object, "setClusterIdConsegna", TransazioneApplicativoServer.model().CLUSTER_ID_CONSEGNA.getFieldType(),
					this.getObjectFromMap(map,"cluster-id-consegna"));
				setParameter(object, "setDataUltimoErrore", TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE.getFieldType(),
					this.getObjectFromMap(map,"data-ultimo-errore"));
				setParameter(object, "setDettaglioEsitoUltimoErrore", TransazioneApplicativoServer.model().DETTAGLIO_ESITO_ULTIMO_ERRORE.getFieldType(),
					this.getObjectFromMap(map,"dettaglio-esito-ultimo-errore"));
				setParameter(object, "setCodiceRispostaUltimoErrore", TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE.getFieldType(),
					this.getObjectFromMap(map,"codice-risposta-ultimo-errore"));
				setParameter(object, "setUltimoErrore", TransazioneApplicativoServer.model().ULTIMO_ERRORE.getFieldType(),
					this.getObjectFromMap(map,"ultimo-errore"));
				setParameter(object, "setLocationUltimoErrore", TransazioneApplicativoServer.model().LOCATION_ULTIMO_ERRORE.getFieldType(),
					this.getObjectFromMap(map,"location-ultimo-errore"));
				setParameter(object, "setClusterIdUltimoErrore", TransazioneApplicativoServer.model().CLUSTER_ID_ULTIMO_ERRORE.getFieldType(),
					this.getObjectFromMap(map,"cluster-id-ultimo-errore"));
				setParameter(object, "setFaultUltimoErrore", TransazioneApplicativoServer.model().FAULT_ULTIMO_ERRORE.getFieldType(),
					this.getObjectFromMap(map,"fault-ultimo-errore"));
				setParameter(object, "setFormatoFaultUltimoErrore", TransazioneApplicativoServer.model().FORMATO_FAULT_ULTIMO_ERRORE.getFieldType(),
					this.getObjectFromMap(map,"formato-fault-ultimo-errore"));
				setParameter(object, "setDataPrimoPrelievoIm", TransazioneApplicativoServer.model().DATA_PRIMO_PRELIEVO_IM.getFieldType(),
					this.getObjectFromMap(map,"data-primo-prelievo-im"));
				setParameter(object, "setDataPrelievoIm", TransazioneApplicativoServer.model().DATA_PRELIEVO_IM.getFieldType(),
					this.getObjectFromMap(map,"data-prelievo-im"));
				setParameter(object, "setNumeroPrelieviIm", TransazioneApplicativoServer.model().NUMERO_PRELIEVI_IM.getFieldType(),
					this.getObjectFromMap(map,"numero-prelievi-im"));
				setParameter(object, "setDataEliminazioneIm", TransazioneApplicativoServer.model().DATA_ELIMINAZIONE_IM.getFieldType(),
					this.getObjectFromMap(map,"data-eliminazione-im"));
				setParameter(object, "setClusterIdPrelievoIm", TransazioneApplicativoServer.model().CLUSTER_ID_PRELIEVO_IM.getFieldType(),
					this.getObjectFromMap(map,"cluster-id-prelievo-im"));
				setParameter(object, "setClusterIdEliminazioneIm", TransazioneApplicativoServer.model().CLUSTER_ID_ELIMINAZIONE_IM.getFieldType(),
					this.getObjectFromMap(map,"cluster-id-eliminazione-im"));
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

			if(model.equals(TransazioneApplicativoServer.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("transazioni_sa","id","seq_transazioni_sa","transazioni_sa_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
