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

package org.openspcoop2.core.transazioni.utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;

/**     
 * CredenzialiMittenteUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialiMittenteUtils {

	public static IPaginatedExpression createCredenzialeMittentePaginatedExpression(ICredenzialeMittenteService credenzialeMittentiService,
    		TipoCredenzialeMittente tipoCredenziale, String tipoAutenticazione, String credential,
    		boolean ricercaEsatta, boolean caseSensitive) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException, UtilsException {
    	IPaginatedExpression pagEpression = credenzialeMittentiService.newPaginatedExpression();
		pagEpression.and();
		if(TipoCredenzialeMittente.trasporto.equals(tipoCredenziale)) {
			pagEpression.equals(CredenzialeMittente.model().TIPO, tipoCredenziale.name()+"_"+tipoAutenticazione);
		}
		else {
			pagEpression.equals(CredenzialeMittente.model().TIPO, tipoCredenziale.name());
		}
		if(TipoCredenzialeMittente.trasporto.equals(tipoCredenziale) && TipoAutenticazione.SSL.getValue().equalsIgnoreCase(tipoAutenticazione) && ricercaEsatta) {
			Hashtable<String,  List<String>> hashSubject = CertificateUtils.getPrincipalIntoHashtable(credential, PrincipalType.subject);			
			Enumeration<String> keys = hashSubject.keys();			
			while(keys.hasMoreElements()){				
				String key = keys.nextElement();
				
				List<String> listValues = hashSubject.get(key);
				for (String value : listValues) {
					if(caseSensitive) {
						pagEpression.like(CredenzialeMittente.model().CREDENZIALE, "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", LikeMode.ANYWHERE);
					}
					else {
						pagEpression.ilike(CredenzialeMittente.model().CREDENZIALE, "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", LikeMode.ANYWHERE);
					}
				}
			}				
		}else {
			if(ricercaEsatta && caseSensitive) {
				pagEpression.equals(CredenzialeMittente.model().CREDENZIALE, credential);
			}
			else if(ricercaEsatta && !caseSensitive) {
				pagEpression.ilike(CredenzialeMittente.model().CREDENZIALE, credential, LikeMode.EXACT);
			}
			else if(!ricercaEsatta && !caseSensitive) {
				pagEpression.ilike(CredenzialeMittente.model().CREDENZIALE, credential, LikeMode.ANYWHERE);
			}
			else { // !ricercaEsatta && caseSensitive
				pagEpression.like(CredenzialeMittente.model().CREDENZIALE, credential, LikeMode.ANYWHERE);
			}
		}
		return pagEpression;
    }
    
	
}
