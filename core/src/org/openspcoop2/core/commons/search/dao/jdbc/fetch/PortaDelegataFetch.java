/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.commons.search.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.commons.search.PortaDelegataAzione;


/**     
 * PortaDelegataFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDelegataFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(PortaDelegata.model())){
				PortaDelegata object = new PortaDelegata();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", PortaDelegata.model().NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_porta", PortaDelegata.model().NOME.getFieldType()));
				setParameter(object, "setStato", PortaDelegata.model().STATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "stato", PortaDelegata.model().STATO.getFieldType()));
				setParameter(object, "setTipoSoggettoErogatore", PortaDelegata.model().TIPO_SOGGETTO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_soggetto_erogatore", PortaDelegata.model().TIPO_SOGGETTO_EROGATORE.getFieldType()));
				setParameter(object, "setNomeSoggettoErogatore", PortaDelegata.model().NOME_SOGGETTO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_soggetto_erogatore", PortaDelegata.model().NOME_SOGGETTO_EROGATORE.getFieldType()));
				setParameter(object, "setTipoServizio", PortaDelegata.model().TIPO_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_servizio", PortaDelegata.model().TIPO_SERVIZIO.getFieldType()));
				setParameter(object, "setNomeServizio", PortaDelegata.model().NOME_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_servizio", PortaDelegata.model().NOME_SERVIZIO.getFieldType()));
				setParameter(object, "setVersioneServizio", PortaDelegata.model().VERSIONE_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "versione_servizio", PortaDelegata.model().VERSIONE_SERVIZIO.getFieldType()));
				setParameter(object, "setModeAzione", PortaDelegata.model().MODE_AZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "mode_azione", PortaDelegata.model().MODE_AZIONE.getFieldType()));
				setParameter(object, "setNomeAzione", PortaDelegata.model().NOME_AZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_azione", PortaDelegata.model().NOME_AZIONE.getFieldType()));
				setParameter(object, "setNomePortaDeleganteAzione", PortaDelegata.model().NOME_PORTA_DELEGANTE_AZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_porta_delegante_azione", PortaDelegata.model().NOME_PORTA_DELEGANTE_AZIONE.getFieldType()));
				return object;
			}
			if(model.equals(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO)){
				PortaDelegataServizioApplicativo object = new PortaDelegataServizioApplicativo();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				return object;
			}
			if(model.equals(PortaDelegata.model().PORTA_DELEGATA_AZIONE)){
				PortaDelegataAzione object = new PortaDelegataAzione();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", PortaDelegata.model().PORTA_DELEGATA_AZIONE.NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "azione", PortaDelegata.model().PORTA_DELEGATA_AZIONE.NOME.getFieldType()));
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

			if(model.equals(PortaDelegata.model())){
				PortaDelegata object = new PortaDelegata();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setNome", PortaDelegata.model().NOME.getFieldType(),
					this.getObjectFromMap(map,"nome"));
				setParameter(object, "setStato", PortaDelegata.model().STATO.getFieldType(),
					this.getObjectFromMap(map,"stato"));
				setParameter(object, "setTipoSoggettoErogatore", PortaDelegata.model().TIPO_SOGGETTO_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"tipo_soggetto_erogatore"));
				setParameter(object, "setNomeSoggettoErogatore", PortaDelegata.model().NOME_SOGGETTO_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"nome_soggetto_erogatore"));
				setParameter(object, "setTipoServizio", PortaDelegata.model().TIPO_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"tipo_servizio"));
				setParameter(object, "setNomeServizio", PortaDelegata.model().NOME_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"nome_servizio"));
				setParameter(object, "setVersioneServizio", PortaDelegata.model().VERSIONE_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"versione_servizio"));
				setParameter(object, "setModeAzione", PortaDelegata.model().MODE_AZIONE.getFieldType(),
					this.getObjectFromMap(map,"mode_azione"));
				setParameter(object, "setNomeAzione", PortaDelegata.model().NOME_AZIONE.getFieldType(),
					this.getObjectFromMap(map,"nome_azione"));
				setParameter(object, "setNomePortaDeleganteAzione", PortaDelegata.model().NOME_PORTA_DELEGANTE_AZIONE.getFieldType(),
					this.getObjectFromMap(map,"nome_porta_delegante_azione"));
				return object;
			}
			if(model.equals(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO)){
				PortaDelegataServizioApplicativo object = new PortaDelegataServizioApplicativo();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"porta-delegata-servizio-applicativo.id"));
				return object;
			}
			if(model.equals(PortaDelegata.model().PORTA_DELEGATA_AZIONE)){
				PortaDelegataAzione object = new PortaDelegataAzione();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"porta-delegata-azione.id"));
				setParameter(object, "setNome", PortaDelegata.model().PORTA_DELEGATA_AZIONE.NOME.getFieldType(),
					this.getObjectFromMap(map,"porta-delegata-azione.nome"));
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

			if(model.equals(PortaDelegata.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("porte_delegate","id","seq_porte_delegate","porte_delegate_init_seq");
			}
			if(model.equals(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("porte_delegate_sa","id","seq_porte_delegate_sa","porte_delegate_sa_init_seq");
			}
			if(model.equals(PortaDelegata.model().PORTA_DELEGATA_AZIONE)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("pd_azioni","id","seq_pd_azioni","pd_azioni_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
