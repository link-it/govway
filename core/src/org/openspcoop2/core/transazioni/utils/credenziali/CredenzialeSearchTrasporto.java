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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;

/**     
 * CredenzialeTrasporto
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialeSearchTrasporto extends AbstractSearchCredenziale {

	private String tipoAutenticazione;
	
	public CredenzialeSearchTrasporto(String tipoAutenticazione) {
		super(TipoCredenzialeMittente.trasporto);
		this.tipoAutenticazione = tipoAutenticazione;
	}

	@Override
	public String getTipo() {
		return CredenzialeTrasporto.getTipoTrasporto(this.tipo, this.tipoAutenticazione);
	}
	
	public boolean isSsl() {
		return TipoAutenticazione.SSL.getValue().equalsIgnoreCase(this.tipoAutenticazione);
	}
	
	@Override
	protected String getExactValueDatabase(String credentialParam) throws UtilsException {
		throw new UtilsException("Not Implemented"); // il metodo non viene usato poich' createExpression è ridefinito per questo tipo di classe
	}
	
	@Override
	public IPaginatedExpression createExpression(ICredenzialeMittenteService credenzialeMittentiService, String credential, boolean ricercaEsatta, boolean caseSensitive) throws UtilsException {
		
		try {
		
			IPaginatedExpression pagEpression = credenzialeMittentiService.newPaginatedExpression();
			pagEpression.and();
			
			pagEpression.equals(CredenzialeMittente.model().TIPO, CredenzialeTrasporto.getTipoTrasporto(this.tipo, this.tipoAutenticazione));
			
			if(CredenzialeTrasporto.isSsl(this.tipoAutenticazione) && ricercaEsatta) {
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
			}
			else {
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
			
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(), e);
		}
		
	}
	

	
}
