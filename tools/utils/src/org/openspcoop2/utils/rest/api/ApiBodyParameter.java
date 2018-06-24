/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.rest.api;

/**
 * ApiRequestBodyParameter
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiBodyParameter extends AbstractApiParameter {

	private static String MEDIA_TYPE_ALL_1 = "*/*";
	private static String MEDIA_TYPE_ALL_2 = "*";
	
	private String mediaType;
	private Object element;

	public ApiBodyParameter(String name) {
		super(name);
	}
	
	public Object getElement() {
		return this.element;
	}
	public void setElement(Object element) {
		this.element = element;
	}
	public String getMediaType() {
		return this.mediaType;
	}
	public boolean isAllMediaType() {
		return MEDIA_TYPE_ALL_1.equals(this.mediaType) || MEDIA_TYPE_ALL_2.equals(this.mediaType);
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
}
