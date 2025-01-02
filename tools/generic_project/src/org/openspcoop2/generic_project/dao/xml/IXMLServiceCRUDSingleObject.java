/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.dao.xml;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.beans.IDMappingBehaviour;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;

/**
 * IXMLServiceCRUDSingleObject
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IXMLServiceCRUDSingleObject<XML,T,SM>  extends IXMLServiceManager<SM> {

	public void create(Logger log,XML xmlRoot,T obj, IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception;
	
	public void update(Logger log,XML xmlRoot,T obj, IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException,NotImplementedException,ServiceException,Exception;
	
	public void updateFields(Logger log,XML xmlRoot,UpdateField ... updateFields) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public void updateFields(Logger log,XML xmlRoot,IExpression condition, UpdateField ... updateFields) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public void updateFields(Logger log,XML xmlRoot,UpdateModel ... updateModels) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public void updateOrCreate(Logger log,XML xmlRoot,T obj, IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception;
	
	public void delete(Logger log,XML xmlRoot,T obj) throws NotImplementedException,ServiceException,Exception;

}
