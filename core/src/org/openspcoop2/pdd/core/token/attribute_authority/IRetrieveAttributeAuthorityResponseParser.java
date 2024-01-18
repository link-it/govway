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
package org.openspcoop2.pdd.core.token.attribute_authority;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**     
 * IRetrieveAttributeAuthorityResponseParser
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IRetrieveAttributeAuthorityResponseParser {

	public void init(String raw, Map<String,Object> claims);
	
	public void init(byte[] content);
	public String getContentAsString(); // per avere un 'raw'
	
	// Controllo se il token e' un token atteso su cui leggere le informazioni
	public void checkHttpTransaction(Integer httpResponseCode) throws Exception;
	
	// Indicazione se il token e' valido
	public boolean isValid();
	
	// Attributi
	public Map<String, Object> getAttributes();
	
	// String representing the issuer for this attribute response
	public String getIssuer();
	
	// String representing the Subject of this attribute response
	public String getSubject();
	
	// Service-specific string identifier or list of string identifiers representing the intended audience for this attribute response
	public List<String> getAudience();
	
	// Indicate when this attribute response will expire
	public Date getExpired();
	
	// Indicate when this attribute response was originally issued
	public Date getIssuedAt();
	
	// Indicate when this attribute response is not to be used before.
	public Date getNotToBeUsedBefore();
	
	// String representing the unique identifier for the attribute response
	public String getIdentifier();
	
}
	
