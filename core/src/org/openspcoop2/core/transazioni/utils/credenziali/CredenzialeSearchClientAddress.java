/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
 * CredenzialeSearchClientAddress
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialeSearchClientAddress extends AbstractSearchCredenziale {

	private boolean socketAddress;
	private boolean trasportAddress;
	private boolean andOperator;
	
	public CredenzialeSearchClientAddress(boolean socketAddress, boolean trasportAddress, boolean and) { 
		super(TipoCredenzialeMittente.CLIENT_ADDRESS);
		this.socketAddress = socketAddress;
		this.trasportAddress = trasportAddress;
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
			
			String credentialSocket = this.convertToDBValue ? CredenzialeClientAddress.getSocketAddressDBValue(credentialParam, ricercaEsatta) : credentialParam;
			String credentialTransport = this.convertToDBValue ? CredenzialeClientAddress.getTransportAddressDBValue(credentialParam, ricercaEsatta) : credentialParam;
			
			pagExpression.equals(CredenzialeMittente.model().TIPO, this.tipo.getRawValue());
			
			setLikeCondition(credenzialeMittentiService, 
					pagExpression, ricercaEsatta, caseSensitive,
					credentialSocket, credentialTransport, credentialParam);
		
			return pagExpression;
			
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(), e);
		}
		
	}
	
	private void setLikeCondition(ICredenzialeMittenteService credenzialeMittentiService, 
			IPaginatedExpression pagExpression, boolean ricercaEsatta, boolean caseSensitive,
			String credentialSocket, String credentialTransport, String credentialParam) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		if(ricercaEsatta) {
			if(caseSensitive) {
				setLikeConditionRicercaEsattaCaseSensitive(credenzialeMittentiService, 
						pagExpression, 
						credentialSocket, credentialTransport);
			}
			else {
				setLikeConditionRicercaEsattaCaseInsensitive(credenzialeMittentiService, 
						pagExpression, 
						credentialSocket, credentialTransport);
			}
		}
		else if(!caseSensitive) {
			pagExpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialParam, LikeMode.ANYWHERE); // non si differenzia tra socket e transport
		}
		else { // !ricercaEsatta && caseSensitive
			pagExpression.like(CredenzialeMittente.model().CREDENZIALE, credentialParam, LikeMode.ANYWHERE); // non si differenzia tra socket e transport
		}
	}
	
	private void setLikeConditionRicercaEsattaCaseSensitive(ICredenzialeMittenteService credenzialeMittentiService, 
			IPaginatedExpression pagExpression, 
			String credentialSocket, String credentialTransport) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		if(this.socketAddress && this.trasportAddress) {
			setLikeConditionRicercaEsattaCaseSensitiveBoth(credenzialeMittentiService, 
					pagExpression, 
					credentialSocket, credentialTransport);	
		}
		else if(this.socketAddress) {
			if(this.convertToDBValue) {
				pagExpression.like(CredenzialeMittente.model().CREDENZIALE, credentialSocket, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il socket
			}
			else {
				pagExpression.equals(CredenzialeMittente.model().CREDENZIALE, credentialSocket);
			}
		}
		else if(this.trasportAddress) {
			if(this.convertToDBValue) {
				pagExpression.like(CredenzialeMittente.model().CREDENZIALE, credentialTransport, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il trasporto
			}
			else {
				pagExpression.equals(CredenzialeMittente.model().CREDENZIALE, credentialTransport);
			}
		}
	}
	private void setLikeConditionRicercaEsattaCaseSensitiveBoth(ICredenzialeMittenteService credenzialeMittentiService, 
			IPaginatedExpression pagExpression, 
			String credentialSocket, String credentialTransport) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		IPaginatedExpression pagExpressionSocket = credenzialeMittentiService.newPaginatedExpression();
		if(this.convertToDBValue) {
			pagExpressionSocket.like(CredenzialeMittente.model().CREDENZIALE, credentialSocket, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il socket
		}
		else {
			pagExpressionSocket.equals(CredenzialeMittente.model().CREDENZIALE, credentialSocket); // utilizzato da govway dove credentialSocket=credentialTransport
		}
		
		IPaginatedExpression pagExpressionTransport = credenzialeMittentiService.newPaginatedExpression();
		if(this.convertToDBValue) {
			pagExpressionTransport.like(CredenzialeMittente.model().CREDENZIALE, credentialTransport, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il trasporto
		}
		else {
			pagExpressionTransport.equals(CredenzialeMittente.model().CREDENZIALE, credentialTransport); // utilizzato da govway dove credentialSocket=credentialTransport
		}
		
		if(this.andOperator) {
			pagExpression.and(pagExpressionSocket, pagExpressionTransport);
		}
		else {
			pagExpression.or(pagExpressionSocket, pagExpressionTransport);
		}
	}
	
	private void setLikeConditionRicercaEsattaCaseInsensitive(ICredenzialeMittenteService credenzialeMittentiService, 
			IPaginatedExpression pagExpression, 
			String credentialSocket, String credentialTransport) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		if(this.socketAddress && this.trasportAddress) {
			setLikeConditionRicercaEsattaCaseInsensitiveBoth(credenzialeMittentiService, 
					pagExpression, 
					credentialSocket, credentialTransport);
		}
		else if(this.socketAddress) {
			if(this.convertToDBValue) {
				pagExpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialSocket, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il socket
			}
			else {
				pagExpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialSocket, LikeMode.EXACT);
			}
		}
		else if(this.trasportAddress) {
			if(this.convertToDBValue) {
				pagExpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialTransport, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il trasporto
			}
			else {
				pagExpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialTransport, LikeMode.EXACT);
			}
		}
	}
	private void setLikeConditionRicercaEsattaCaseInsensitiveBoth(ICredenzialeMittenteService credenzialeMittentiService, 
			IPaginatedExpression pagExpression, 
			String credentialSocket, String credentialTransport) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		IPaginatedExpression pagExpressionSocket = credenzialeMittentiService.newPaginatedExpression();
		if(this.convertToDBValue) {
			pagExpressionSocket.ilike(CredenzialeMittente.model().CREDENZIALE, credentialSocket, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il socket
		}
		else {
			pagExpressionSocket.ilike(CredenzialeMittente.model().CREDENZIALE, credentialSocket, LikeMode.EXACT); // utilizzato da govway dove credentialSocket=credentialTransport
		}
		
		IPaginatedExpression pagExpressionTransport = credenzialeMittentiService.newPaginatedExpression();
		if(this.convertToDBValue) {
			pagExpressionTransport.ilike(CredenzialeMittente.model().CREDENZIALE, credentialTransport, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il trasporto
		}
		else {
			pagExpressionTransport.ilike(CredenzialeMittente.model().CREDENZIALE, credentialTransport, LikeMode.EXACT); // utilizzato da govway dove credentialSocket=credentialTransport
		}
			
		if(this.andOperator) {
			pagExpression.and(pagExpressionSocket, pagExpressionTransport);
		}
		else {
			pagExpression.or(pagExpressionSocket, pagExpressionTransport);
		}
	}
}
