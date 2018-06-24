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
package org.openspcoop2.generic_project.dao.xml;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.beans.IDMappingBehaviour;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;

/**
 * IXMLServiceCRUDWithId
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IXMLServiceCRUDWithId<XML,T,K,SM>  extends IXMLServiceManager<SM> {

	public void create(Logger log,XML xmlRoot,T obj, IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception;
	
	public void update(Logger log,XML xmlRoot,K oldId, T obj, IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException,NotImplementedException,ServiceException,Exception;
	
	public void updateFields(Logger log,XML xmlRoot,K oldId, UpdateField ... updateFields) throws ServiceException,NotFoundException,NotImplementedException, Exception;
	
	public void updateFields(Logger log,XML xmlRoot,K oldId, IExpression condition, UpdateField ... updateFields) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public void updateFields(Logger log,XML xmlRoot,K oldId, UpdateModel ... updateModels) throws ServiceException,NotFoundException,NotImplementedException,Exception;
	
	public void updateOrCreate(Logger log,XML xmlRoot,K oldId, T obj, IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception;
	
	public void deleteById(Logger log,XML xmlRoot,K id) throws NotImplementedException,ServiceException,Exception;
	
	public void delete(Logger log,XML xmlRoot,T obj) throws NotImplementedException,ServiceException,Exception;
	
	public NonNegativeNumber deleteAll(Logger log,XML xmlRoot) throws NotImplementedException,ServiceException,Exception;
	
	public NonNegativeNumber deleteAll(Logger log,XML xmlRoot,XMLExpression expression) throws NotImplementedException,ServiceException,Exception;

}
