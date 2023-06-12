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
package org.openspcoop2.utils.beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

/**
 * BaseBeanWithId
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlTransient
public abstract class BaseBeanWithId extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	protected BaseBeanWithId(){
		super();
	}
	
	@XmlTransient
	protected Long id;
	  
	public Long getId() {
		if(this.id!=null)
			return this.id;
		else
			return Long.valueOf(-1);
	}

	public void setId(Long id) {
		if(id!=null)
			this.id=id;
		else
			this.id=Long.valueOf(-1);
	}
	
}

