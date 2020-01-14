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

package org.openspcoop2.security.message.constants;

import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * EncryptionSymmetricKeyWrapAlgorithm
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum EncryptionSymmetricKeyWrapAlgorithm {

	TRIPLEDES("http://www.w3.org/2001/04/xmlenc#kw-tripledes"),
	AES_128("http://www.w3.org/2001/04/xmlenc#kw-aes128"),
	AES_256("http://www.w3.org/2001/04/xmlenc#kw-aes256"),
	AES_192("http://www.w3.org/2001/04/xmlenc#kw-aes192"),
	CAMELLIA_128("http://www.w3.org/2001/04/xmldsig-more#kw-camellia128"),
	CAMELLIA_192("http://www.w3.org/2001/04/xmldsig-more#kw-camellia192"),
	CAMELLIA_256("http://www.w3.org/2001/04/xmldsig-more#kw-camellia256"),
	SEED_128("http://www.w3.org/2007/05/xmldsig-more#kw-seed128");
	 
	
	private String uri;
	EncryptionSymmetricKeyWrapAlgorithm(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return this.uri;
	}
	
	public static EncryptionSymmetricKeyWrapAlgorithm toEnumConstant(String uri){
		try{
			return toEnumConstant(uri,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static EncryptionSymmetricKeyWrapAlgorithm toEnumConstant(String uri, boolean throwNotFoundException) throws NotFoundException{
		EncryptionSymmetricKeyWrapAlgorithm res = null;
		for (EncryptionSymmetricKeyWrapAlgorithm tmp : values()) {
			if(tmp.getUri().equals(uri)){
				res = tmp;
				break;
			}
		}
		if(res==null && throwNotFoundException){
			throw new NotFoundException("Enum with uri ["+uri+"] not found");
		}
		return res;
	}
}
