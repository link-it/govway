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
package org.openspcoop2.web.monitor.transazioni.bean;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.utils.TransactionContentUtils;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;

/**
 * DumpContenutoBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DumpContenutoBean extends DumpContenuto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DumpContenutoBean() {
		super();
	}
	
	public DumpContenutoBean(DumpContenuto dumpContenuto){
		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
		
		BeanUtils.copy(this, dumpContenuto, metodiEsclusi);
	}

	public java.lang.String getValoreAsString() {
		return TransactionContentUtils.getDumpContenutoValue(this);
	}
}
