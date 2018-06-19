package org.openspcoop2.core.transazioni.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.transazioni.CredenzialeMittente;


/**     
 * CredenzialeMittenteFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialeMittenteFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(CredenzialeMittente.model())){
				CredenzialeMittente object = new CredenzialeMittente();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setTipo", CredenzialeMittente.model().TIPO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo", CredenzialeMittente.model().TIPO.getFieldType()));
				setParameter(object, "setCredenziale", CredenzialeMittente.model().CREDENZIALE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "credenziale", CredenzialeMittente.model().CREDENZIALE.getFieldType()));
				setParameter(object, "setOraRegistrazione", CredenzialeMittente.model().ORA_REGISTRAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "ora_registrazione", CredenzialeMittente.model().ORA_REGISTRAZIONE.getFieldType()));
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

			if(model.equals(CredenzialeMittente.model())){
				CredenzialeMittente object = new CredenzialeMittente();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setTipo", CredenzialeMittente.model().TIPO.getFieldType(),
					this.getObjectFromMap(map,"tipo"));
				setParameter(object, "setCredenziale", CredenzialeMittente.model().CREDENZIALE.getFieldType(),
					this.getObjectFromMap(map,"credenziale"));
				setParameter(object, "setOraRegistrazione", CredenzialeMittente.model().ORA_REGISTRAZIONE.getFieldType(),
					this.getObjectFromMap(map,"ora-registrazione"));
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

			if(model.equals(CredenzialeMittente.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("credenziale_mittente","id","seq_credenziale_mittente","credenziale_mittente_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
