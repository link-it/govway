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

package org.openspcoop2.utils.rest.problem;

/**
 * Costanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProblemConstants {

	public static final String CLAIM_TYPE = "type";
	public static final String CLAIM_TITLE = "title";
	public static final String CLAIM_STATUS = "status";
	public static final String CLAIM_DETAIL = "detail";
	public static final String CLAIM_INSTANCE = "instance";
	
	public static final String CLAIM_TYPE_BLANK_VALUE = "about:blank";
	
	/** RFC 7807 Problem Details for HTTP APIs */
	public static final String XML_PROBLEM_DETAILS_RFC_7807_NAMESPACE = "urn:ietf:rfc:7807";
	public static final String XML_PROBLEM_DETAILS_RFC_7807_LOCAL_NAME = "problem";
	
}
