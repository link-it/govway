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

package org.openspcoop2.security.message.constants;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**     
 * SignatureC14NAlgorithm
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum SignatureC14NAlgorithm {
	
	INCLUSIVE_C14N_10_OMITS_COMMENTS(CostantiDB.INCLUSIVE_C14N_10_OMITS_COMMENTS_URI,"Inclusive XML Canonicalization 1.0 (omits comments)"),
	INCLUSIVE_C14N_10_WITH_COMMENTS(CostantiDB.INCLUSIVE_C14N_10_WITH_COMMENTS_URI,"Inclusive XML Canonicalization 1.0 (with comments)"),
	INCLUSIVE_C14N_11_OMITS_COMMENTS(CostantiDB.INCLUSIVE_C14N_11_OMITS_COMMENTS_URI,"Inclusive XML Canonicalization 1.1 (omits comments)"),
	INCLUSIVE_C14N_11_WITH_COMMENTS(CostantiDB.INCLUSIVE_C14N_11_WITH_COMMENTS_URI,"Inclusive XML Canonicalization 1.1 (with comments)"),
	EXCLUSIVE_C14N_10_OMITS_COMMENTS(CostantiDB.EXCLUSIVE_C14N_10_OMITS_COMMENTS_URI,"Exclusive XML Canonicalization 1.0 (omits comments)"),
	EXCLUSIVE_C14N_10_WITH_COMMENTS(CostantiDB.EXCLUSIVE_C14N_10_WITH_COMMENTS_URI,"Exclusive XML Canonicalization 1.0 (with comments)");
	
	private String uri;
	private String label;
	SignatureC14NAlgorithm(String uri,String label) {
		this.uri = uri;
		this.label = label;
	}
	
	public String getUri() {
		return this.uri;
	}
	public String getLabel() {
		return this.label;
	}
	
	public static SignatureC14NAlgorithm toEnumConstant(String uri){
		try{
			return toEnumConstant(uri,false);
		}catch(NotFoundException notFound){
			return null;
		}
	}
	public static SignatureC14NAlgorithm toEnumConstant(String uri, boolean throwNotFoundException) throws NotFoundException{
		SignatureC14NAlgorithm res = null;
		for (SignatureC14NAlgorithm tmp : values()) {
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
