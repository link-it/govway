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

package org.openspcoop2.security.message.constants;

import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * EncryptionC14NAlgorithm
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum EncryptionC14NAlgorithm {
	
	INCLUSIVE_C14N_10_OMITS_COMMENTS("http://www.w3.org/TR/2001/REC-xml-c14n-20010315","Inclusive XML Canonicalization 1.0 (omits comments)"),
	INCLUSIVE_C14N_10_WITH_COMMENTS("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments","Inclusive XML Canonicalization 1.0 (with comments)"),
	INCLUSIVE_C14N_11_OMITS_COMMENTS("http://www.w3.org/2006/12/xml-c14n11","Inclusive XML Canonicalization 1.1 (omits comments)"),
	INCLUSIVE_C14N_11_WITH_COMMENTS("http://www.w3.org/2006/12/xml-c14n11#WithComments","Inclusive XML Canonicalization 1.1 (with comments)"),
	EXCLUSIVE_C14N_10_OMITS_COMMENTS("http://www.w3.org/2001/10/xml-exc-c14n#","Exclusive XML Canonicalization 1.0 (omits comments)"),
	EXCLUSIVE_C14N_10_WITH_COMMENTS("http://www.w3.org/2001/10/xml-exc-c14n#WithComments","Exclusive XML Canonicalization 1.0 (with comments)");
	
	private String uri;
	private String label;
	EncryptionC14NAlgorithm(String uri,String label) {
		this.uri = uri;
		this.label = label;
	}
	
	public String getUri() {
		return this.uri;
	}
	public String getLabel() {
		return this.label;
	}
	
	public static EncryptionC14NAlgorithm toEnumConstant(String uri){
		try{
			return toEnumConstant(uri,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static EncryptionC14NAlgorithm toEnumConstant(String uri, boolean throwNotFoundException) throws NotFoundException{
		EncryptionC14NAlgorithm res = null;
		for (EncryptionC14NAlgorithm tmp : values()) {
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
