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
package org.openspcoop2.utils.logger.beans.context.batch;

import java.io.Serializable;

import org.openspcoop2.utils.logger.IContext;
import org.openspcoop2.utils.logger.beans.context.core.AbstractContext;
import org.openspcoop2.utils.logger.beans.context.core.AbstractTransaction;

/**
 * BatchContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BatchContext extends AbstractContext implements IContext,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
					
	public BatchContext(){}
	
	@Override
	public AbstractTransaction getTransaction() {
		if(this.transaction == null){
			this.transaction = new BatchTransaction();
		}
		return this.transaction;
	}

}
