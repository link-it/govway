package org.openspcoop2.core.transazioni.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.transazioni.TransazioneLlm;


/**     
 * TransazioneLlmFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneLlmFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			GenericJDBCParameterUtilities jdbcParameterUtilities =  
					new GenericJDBCParameterUtilities(tipoDatabase);

			if(model.equals(TransazioneLlm.model())){
				TransazioneLlm object = new TransazioneLlm();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setIdTransazione", TransazioneLlm.model().ID_TRANSAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_transazione", TransazioneLlm.model().ID_TRANSAZIONE.getFieldType()));
				setParameter(object, "setDataIngressoRichiesta", TransazioneLlm.model().DATA_INGRESSO_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_ingresso_richiesta", TransazioneLlm.model().DATA_INGRESSO_RICHIESTA.getFieldType()));
				setParameter(object, "setLlmProvider", TransazioneLlm.model().LLM_PROVIDER.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "llm_provider", TransazioneLlm.model().LLM_PROVIDER.getFieldType()));
				setParameter(object, "setLlmModel", TransazioneLlm.model().LLM_MODEL.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "llm_model", TransazioneLlm.model().LLM_MODEL.getFieldType()));
				setParameter(object, "setLlmProviderBinding", TransazioneLlm.model().LLM_PROVIDER_BINDING.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "llm_provider_binding", TransazioneLlm.model().LLM_PROVIDER_BINDING.getFieldType()));
				setParameter(object, "setTokenInput", TransazioneLlm.model().TOKEN_INPUT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_input", TransazioneLlm.model().TOKEN_INPUT.getFieldType()));
				setParameter(object, "setTokenOutput", TransazioneLlm.model().TOKEN_OUTPUT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_output", TransazioneLlm.model().TOKEN_OUTPUT.getFieldType()));
				setParameter(object, "setCostEstimated", TransazioneLlm.model().COST_ESTIMATED.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cost_estimated", TransazioneLlm.model().COST_ESTIMATED.getFieldType()));
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

			if(model.equals(TransazioneLlm.model())){
				TransazioneLlm object = new TransazioneLlm();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setIdTransazione", TransazioneLlm.model().ID_TRANSAZIONE.getFieldType(),
					this.getObjectFromMap(map,"id-transazione"));
				setParameter(object, "setDataIngressoRichiesta", TransazioneLlm.model().DATA_INGRESSO_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"data-ingresso-richiesta"));
				setParameter(object, "setLlmProvider", TransazioneLlm.model().LLM_PROVIDER.getFieldType(),
					this.getObjectFromMap(map,"llm-provider"));
				setParameter(object, "setLlmModel", TransazioneLlm.model().LLM_MODEL.getFieldType(),
					this.getObjectFromMap(map,"llm-model"));
				setParameter(object, "setLlmProviderBinding", TransazioneLlm.model().LLM_PROVIDER_BINDING.getFieldType(),
					this.getObjectFromMap(map,"llm-provider-binding"));
				setParameter(object, "setTokenInput", TransazioneLlm.model().TOKEN_INPUT.getFieldType(),
					this.getObjectFromMap(map,"token-input"));
				setParameter(object, "setTokenOutput", TransazioneLlm.model().TOKEN_OUTPUT.getFieldType(),
					this.getObjectFromMap(map,"token-output"));
				setParameter(object, "setCostEstimated", TransazioneLlm.model().COST_ESTIMATED.getFieldType(),
					this.getObjectFromMap(map,"cost-estimated"));
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

			if(model.equals(TransazioneLlm.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("transazioni_llm","id","seq_transazioni_llm","transazioni_llm_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
