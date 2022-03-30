/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
 * CredenzialeTrasporto
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
		super(TipoCredenzialeMittente.client_address);
		this.socketAddress = socketAddress;
		this.trasportAddress = trasportAddress;
		this.andOperator = and;
	}

	@Override
	protected String getExactValueDatabase(String credentialParam) throws UtilsException {
		throw new UtilsException("Not Implemented"); // il metodo non viene usato poich' createExpression è ridefinito per questo tipo di classe
	}
	
	@Override
	public IPaginatedExpression createExpression(ICredenzialeMittenteService credenzialeMittentiService, String credentialParam, boolean ricercaEsatta, boolean caseSensitive) throws UtilsException {
		
		try {
		
			IPaginatedExpression pagEpression = credenzialeMittentiService.newPaginatedExpression();
			pagEpression.and();
			
			String credentialSocket = this.convertToDBValue ? CredenzialeClientAddress.getSocketAddressDBValue(credentialParam) : credentialParam;
			String credentialTransport = this.convertToDBValue ? CredenzialeClientAddress.getTransportAddressDBValue(credentialParam) : credentialParam;
			
			pagEpression.equals(CredenzialeMittente.model().TIPO, this.tipo.name());
			
			if(ricercaEsatta && caseSensitive) {
				if(this.socketAddress && this.trasportAddress) {
					
					IPaginatedExpression pagEpressionSocket = credenzialeMittentiService.newPaginatedExpression();
					if(this.convertToDBValue) {
						pagEpressionSocket.like(CredenzialeMittente.model().CREDENZIALE, credentialSocket, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il socket
					}
					else {
						pagEpressionSocket.equals(CredenzialeMittente.model().CREDENZIALE, credentialSocket); // utilizzato da govway dove credentialSocket=credentialTransport
					}
					
					IPaginatedExpression pagEpressionTransport = credenzialeMittentiService.newPaginatedExpression();
					if(this.convertToDBValue) {
						pagEpressionTransport.like(CredenzialeMittente.model().CREDENZIALE, credentialTransport, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il trasporto
					}
					else {
						pagEpressionTransport.equals(CredenzialeMittente.model().CREDENZIALE, credentialTransport); // utilizzato da govway dove credentialSocket=credentialTransport
					}
					
					if(this.andOperator) {
						pagEpression.and(pagEpressionSocket, pagEpressionTransport);
					}
					else {
						pagEpression.or(pagEpressionSocket, pagEpressionTransport);
					}
				}
				else if(this.socketAddress) {
					if(this.convertToDBValue) {
						pagEpression.like(CredenzialeMittente.model().CREDENZIALE, credentialSocket, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il socket
					}
					else {
						pagEpression.equals(CredenzialeMittente.model().CREDENZIALE, credentialSocket);
					}
				}
				else if(this.trasportAddress) {
					if(this.convertToDBValue) {
						pagEpression.like(CredenzialeMittente.model().CREDENZIALE, credentialTransport, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il trasporto
					}
					else {
						pagEpression.equals(CredenzialeMittente.model().CREDENZIALE, credentialTransport);
					}
				}
			}
			else if(ricercaEsatta && !caseSensitive) {
				if(this.socketAddress && this.trasportAddress) {
					
					IPaginatedExpression pagEpressionSocket = credenzialeMittentiService.newPaginatedExpression();
					if(this.convertToDBValue) {
						pagEpressionSocket.ilike(CredenzialeMittente.model().CREDENZIALE, credentialSocket, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il socket
					}
					else {
						pagEpressionSocket.ilike(CredenzialeMittente.model().CREDENZIALE, credentialSocket, LikeMode.EXACT); // utilizzato da govway dove credentialSocket=credentialTransport
					}
					
					IPaginatedExpression pagEpressionTransport = credenzialeMittentiService.newPaginatedExpression();
					if(this.convertToDBValue) {
						pagEpressionTransport.ilike(CredenzialeMittente.model().CREDENZIALE, credentialTransport, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il trasporto
					}
					else {
						pagEpressionTransport.ilike(CredenzialeMittente.model().CREDENZIALE, credentialTransport, LikeMode.EXACT); // utilizzato da govway dove credentialSocket=credentialTransport
					}
						
					if(this.andOperator) {
						pagEpression.and(pagEpressionSocket, pagEpressionTransport);
					}
					else {
						pagEpression.or(pagEpressionSocket, pagEpressionTransport);
					}
				}
				else if(this.socketAddress) {
					if(this.convertToDBValue) {
						pagEpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialSocket, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il socket
					}
					else {
						pagEpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialSocket, LikeMode.EXACT);
					}
				}
				else if(this.trasportAddress) {
					if(this.convertToDBValue) {
						pagEpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialTransport, LikeMode.ANYWHERE); // cmq devo usare anywhere, il valore è gia codificato per cercare il trasporto
					}
					else {
						pagEpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialTransport, LikeMode.EXACT);
					}
				}
			}
			else if(!ricercaEsatta && !caseSensitive) {
				pagEpression.ilike(CredenzialeMittente.model().CREDENZIALE, credentialParam, LikeMode.ANYWHERE); // non si differenzia tra socket e transport
			}
			else { // !ricercaEsatta && caseSensitive
				pagEpression.like(CredenzialeMittente.model().CREDENZIALE, credentialParam, LikeMode.ANYWHERE); // non si differenzia tra socket e transport
			}
		
			return pagEpression;
			
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(), e);
		}
		
	}
	
}
