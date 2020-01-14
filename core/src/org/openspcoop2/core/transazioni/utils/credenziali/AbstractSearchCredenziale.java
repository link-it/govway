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

package org.openspcoop2.core.transazioni.utils.credenziali;

import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.utils.UtilsException;

/**     
 * AbstractCredenziale
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractSearchCredenziale {

	protected TipoCredenzialeMittente tipo;
	protected boolean convertToDBValue = true; // usato dalla console
	
	public AbstractSearchCredenziale(TipoCredenzialeMittente tipo) { 
		this.tipo = tipo;
	}
	
	public void disableConvertToDBValue() {
		this.convertToDBValue = false; // usato dal gateway
	}
	
	public String getTipo() {
		return this.tipo.name();
	}
	
	protected abstract String getExactValueDatabase(String credentialParam) throws UtilsException;
	
	public IPaginatedExpression createExpression(ICredenzialeMittenteService credenzialeMittentiService, String credentialParam, boolean ricercaEsatta, boolean caseSensitive) throws UtilsException {
		
		try {
		
			IPaginatedExpression pagEpression = credenzialeMittentiService.newPaginatedExpression();
			pagEpression.and();
			
			String credential = this.convertToDBValue ? this.getExactValueDatabase(credentialParam) : credentialParam;
			
			pagEpression.equals(CredenzialeMittente.model().TIPO, this.tipo.name());
			
			if(ricercaEsatta && caseSensitive) {
				if(this.convertToDBValue) {
					pagEpression.like(CredenzialeMittente.model().CREDENZIALE, credential, LikeMode.ANYWHERE); // il valore credential è già convertito sul valore del database.
				}
				else {
					pagEpression.equals(CredenzialeMittente.model().CREDENZIALE, credential); 
				}
			}
			else if(ricercaEsatta && !caseSensitive) {
				if(this.convertToDBValue) {
					pagEpression.ilike(CredenzialeMittente.model().CREDENZIALE, credential, LikeMode.ANYWHERE); // il valore credential è già convertito sul valore del database.
				}
				else {
					pagEpression.ilike(CredenzialeMittente.model().CREDENZIALE, credential, LikeMode.EXACT); 
				}
			}
			else if(!ricercaEsatta && !caseSensitive) {
				pagEpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialParam, LikeMode.ANYWHERE); // credentialParam: non bisogna cercare il valore esatto, basta quanto fornito
			}
			else { // !ricercaEsatta && caseSensitive
				pagEpression.like(CredenzialeMittente.model().CREDENZIALE, credentialParam, LikeMode.ANYWHERE); // credentialParam: non bisogna cercare il valore esatto, basta quanto fornito 
			}
		
			return pagEpression;
			
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(), e);
		}
		
	}
	
}
