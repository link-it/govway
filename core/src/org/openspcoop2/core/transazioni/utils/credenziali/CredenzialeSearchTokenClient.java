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

package org.openspcoop2.core.transazioni.utils.credenziali;

import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.utils.UtilsException;

/**     
 * CredenzialeSearchTokenClient
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialeSearchTokenClient extends AbstractSearchCredenziale {

	private boolean clientId;
	private boolean application;
	private boolean andOperator;
	
	public CredenzialeSearchTokenClient(boolean clientId, boolean application, boolean and) { 
		super(TipoCredenzialeMittente.TOKEN_CLIENT_ID);
		this.clientId = clientId;
		this.application = application;
		this.andOperator = and;
	}

	@Override
	protected String getExactValueDatabase(String credentialParam, boolean ricercaEsatta) throws UtilsException {
		throw new UtilsException("Not Implemented"); // il metodo non viene usato poich' createExpression è ridefinito per questo tipo di classe
	}
	
	@Override
	public IPaginatedExpression createExpression(ICredenzialeMittenteService credenzialeMittentiService, String credentialParam, boolean ricercaEsatta, boolean caseSensitive) throws UtilsException {
		try {
			
			IPaginatedExpression pagExpression = credenzialeMittentiService.newPaginatedExpression();
			pagExpression.and();
			
			String credentialClientId = this.convertToDBValue ? CredenzialeTokenClient.getClientIdDBValue(credentialParam, ricercaEsatta) : credentialParam;
			String credentialApplication = this.convertToDBValue ? CredenzialeTokenClient.getApplicationDBValue(credentialParam, ricercaEsatta) : credentialParam;
			
			pagExpression.equals(CredenzialeMittente.model().TIPO, this.tipo.getRawValue());
			
			if(ricercaEsatta) {
				if(caseSensitive) {
					setLikeConditionRicercaEsattaCaseSensitive(credenzialeMittentiService,
							pagExpression,
							credentialClientId, credentialApplication, credentialParam);
				}
				else {
					setLikeConditionRicercaEsattaCaseInsensitive(credenzialeMittentiService,
							pagExpression,
							credentialClientId, credentialApplication);
				}
			}
			else if(!caseSensitive) {
				pagExpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialParam, LikeMode.ANYWHERE); // non si differenzia tra socket e transport
			}
			else { // !ricercaEsatta && caseSensitive
				pagExpression.like(CredenzialeMittente.model().CREDENZIALE, credentialParam, LikeMode.ANYWHERE); // non si differenzia tra socket e transport
			}
		
			return pagExpression;
			
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(), e);
		}
		
	}
	
	private void setLikeConditionRicercaEsattaCaseSensitive(ICredenzialeMittenteService credenzialeMittentiService,
			IPaginatedExpression pagExpression,
			String credentialClientId, String credentialApplication, String credentialParam) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		if(this.clientId && this.application) {
			setLikeConditionRicercaEsattaCaseSensitiveBoth(credenzialeMittentiService,
					pagExpression,
					credentialClientId, credentialApplication);
		}
		else if(this.clientId) {
			if(this.convertToDBValue) {
				IPaginatedExpression pagExpressionOr = credenzialeMittentiService.newPaginatedExpression();
				pagExpressionOr.like(CredenzialeMittente.model().CREDENZIALE, credentialClientId, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il socket
				pagExpressionOr.or();
				pagExpressionOr.equals(CredenzialeMittente.model().CREDENZIALE, credentialParam); // per backward compatibility sulle transazioni create prima della gestione dell'applicativo
				pagExpression.and(pagExpressionOr);
			}
			else {
				pagExpression.equals(CredenzialeMittente.model().CREDENZIALE, credentialClientId);
			}
		}
		else if(this.application) {
			if(this.convertToDBValue) {
				pagExpression.like(CredenzialeMittente.model().CREDENZIALE, credentialApplication, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il trasporto
			}
			else {
				pagExpression.equals(CredenzialeMittente.model().CREDENZIALE, credentialApplication);
			}
		}
	}
	private void setLikeConditionRicercaEsattaCaseSensitiveBoth(ICredenzialeMittenteService credenzialeMittentiService,
			IPaginatedExpression pagExpression,
			String credentialClientId, String credentialApplication) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		IPaginatedExpression pagExpressionClientId = credenzialeMittentiService.newPaginatedExpression();
		if(this.convertToDBValue) {
			pagExpressionClientId.like(CredenzialeMittente.model().CREDENZIALE, credentialClientId, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il socket
		}
		else {
			pagExpressionClientId.equals(CredenzialeMittente.model().CREDENZIALE, credentialClientId); // utilizzato da govway dove credentialSocket=credentialTransport
		}
		
		IPaginatedExpression pagExpressionApplication = credenzialeMittentiService.newPaginatedExpression();
		if(this.convertToDBValue) {
			pagExpressionApplication.like(CredenzialeMittente.model().CREDENZIALE, credentialApplication, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il trasporto
		}
		else {
			pagExpressionApplication.equals(CredenzialeMittente.model().CREDENZIALE, credentialApplication); // utilizzato da govway dove credentialSocket=credentialTransport
		}
		
		if(this.andOperator) {
			pagExpression.and(pagExpressionClientId, pagExpressionApplication);
		}
		else {
			pagExpression.or(pagExpressionClientId, pagExpressionApplication);
		}
	}
	
	private void setLikeConditionRicercaEsattaCaseInsensitive(ICredenzialeMittenteService credenzialeMittentiService,
			IPaginatedExpression pagExpression,
			String credentialClientId, String credentialApplication) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		if(this.clientId && this.application) {
			setLikeConditionRicercaEsattaCaseInsensitiveBoth(credenzialeMittentiService,
					pagExpression,
					credentialClientId, credentialApplication);
		}
		else if(this.clientId) {
			if(this.convertToDBValue) {
				pagExpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialClientId, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il socket
			}
			else {
				pagExpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialClientId, LikeMode.EXACT);
			}
		}
		else if(this.application) {
			if(this.convertToDBValue) {
				pagExpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialApplication, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il trasporto
			}
			else {
				pagExpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialApplication, LikeMode.EXACT);
			}
		}
	}
	private void setLikeConditionRicercaEsattaCaseInsensitiveBoth(ICredenzialeMittenteService credenzialeMittentiService,
			IPaginatedExpression pagExpression,
			String credentialClientId, String credentialApplication) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		IPaginatedExpression pagExpressionClientId = credenzialeMittentiService.newPaginatedExpression();
		if(this.convertToDBValue) {
			pagExpressionClientId.ilike(CredenzialeMittente.model().CREDENZIALE, credentialClientId, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il socket
		}
		else {
			pagExpressionClientId.ilike(CredenzialeMittente.model().CREDENZIALE, credentialClientId, LikeMode.EXACT); // utilizzato da govway dove credentialSocket=credentialTransport
		}
		
		IPaginatedExpression pagExpressionApplication = credenzialeMittentiService.newPaginatedExpression();
		if(this.convertToDBValue) {
			pagExpressionApplication.ilike(CredenzialeMittente.model().CREDENZIALE, credentialApplication, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il trasporto
		}
		else {
			pagExpressionApplication.ilike(CredenzialeMittente.model().CREDENZIALE, credentialApplication, LikeMode.EXACT); // utilizzato da govway dove credentialSocket=credentialTransport
		}
			
		if(this.andOperator) {
			pagExpression.and(pagExpressionClientId, pagExpressionApplication);
		}
		else {
			pagExpression.or(pagExpressionClientId, pagExpressionApplication);
		}
	}
	
}
