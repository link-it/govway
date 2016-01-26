/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.beans;

import org.openspcoop2.generic_project.exception.ServiceException;

/**
 * UpdateField
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UpdateField {

	private IField field;
	private Object value;

	// Un aggiornamento potrebbe rendere null un campo, per questo il valore può essere null
	
	public UpdateField(IField field) throws ServiceException{
		this(field, null);
	}
	public UpdateField(IField field, Object value) throws ServiceException{
		this.setField(field);
		this.setValue(value);
	}
	
	public IField getField() {
		return this.field;
	}
	public void setField(IField field) throws ServiceException {
		if(field==null){
			throw new ServiceException("Param field not defined");
		}
		this.field = field;
	}
	public Object getValue() {
		return this.value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
