package org.openspcoop2.core.transazioni.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
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
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(TransazioneApplicativoServer.model())){
				TransazioneApplicativoServer object = new TransazioneApplicativoServer();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setIdTransazione", TransazioneApplicativoServer.model().ID_TRANSAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_transazione", TransazioneApplicativoServer.model().ID_TRANSAZIONE.getFieldType()));
				setParameter(object, "setServizioApplicativoErogatore", TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "servizio_applicativo_erogatore", TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()));
				setParameter(object, "setDataUscitaRichiesta", TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_uscita_richiesta", TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA.getFieldType()));
				setParameter(object, "setDataAccettazioneRisposta", TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_accettazione_risposta", TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA.getFieldType()));
				setParameter(object, "setDataIngressoRisposta", TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_ingresso_risposta", TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA.getFieldType()));
				setParameter(object, "setRichiestaUscitaBytes", TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "richiesta_uscita_bytes", TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES.getFieldType()));
				setParameter(object, "setRispostaIngressoBytes", TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "risposta_ingresso_bytes", TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES.getFieldType()));
				setParameter(object, "setCodiceRisposta", TransazioneApplicativoServer.model().CODICE_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "codice_risposta", TransazioneApplicativoServer.model().CODICE_RISPOSTA.getFieldType()));
				setParameter(object, "setDataPrimoTentativo", TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_primo_tentativo", TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO.getFieldType()));
				setParameter(object, "setDataUltimoErrore", TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_ultimo_errore", TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE.getFieldType()));
				setParameter(object, "setCodiceRispostaUltimoErrore", TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "codice_risposta_ultimo_errore", TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE.getFieldType()));
				setParameter(object, "setUltimoErrore", TransazioneApplicativoServer.model().ULTIMO_ERRORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "ultimo_errore", TransazioneApplicativoServer.model().ULTIMO_ERRORE.getFieldType()));
				setParameter(object, "setNumeroTentativi", TransazioneApplicativoServer.model().NUMERO_TENTATIVI.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "numero_tentativi", TransazioneApplicativoServer.model().NUMERO_TENTATIVI.getFieldType()));
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
				setParameter(object, "setDataUscitaRichiesta", TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"data-uscita-richiesta"));
				setParameter(object, "setDataAccettazioneRisposta", TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"data-accettazione-risposta"));
				setParameter(object, "setDataIngressoRisposta", TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"data-ingresso-risposta"));
				setParameter(object, "setRichiestaUscitaBytes", TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES.getFieldType(),
					this.getObjectFromMap(map,"richiesta-uscita-bytes"));
				setParameter(object, "setRispostaIngressoBytes", TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES.getFieldType(),
					this.getObjectFromMap(map,"risposta-ingresso-bytes"));
				setParameter(object, "setCodiceRisposta", TransazioneApplicativoServer.model().CODICE_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"codice-risposta"));
				setParameter(object, "setDataPrimoTentativo", TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO.getFieldType(),
					this.getObjectFromMap(map,"data-primo-tentativo"));
				setParameter(object, "setDataUltimoErrore", TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE.getFieldType(),
					this.getObjectFromMap(map,"data-ultimo-errore"));
				setParameter(object, "setCodiceRispostaUltimoErrore", TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE.getFieldType(),
					this.getObjectFromMap(map,"codice-risposta-ultimo-errore"));
				setParameter(object, "setUltimoErrore", TransazioneApplicativoServer.model().ULTIMO_ERRORE.getFieldType(),
					this.getObjectFromMap(map,"ultimo-errore"));
				setParameter(object, "setNumeroTentativi", TransazioneApplicativoServer.model().NUMERO_TENTATIVI.getFieldType(),
					this.getObjectFromMap(map,"numero-tentativi"));
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
