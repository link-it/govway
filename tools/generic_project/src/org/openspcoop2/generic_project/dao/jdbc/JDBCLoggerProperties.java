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
package org.openspcoop2.generic_project.dao.jdbc;

import java.io.File;

import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.utils.Log4JLoaderProperties;
import org.openspcoop2.generic_project.utils.Utilities;

/**
 * JDBCLoggerProperties
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCLoggerProperties extends Log4JLoaderProperties {

	public JDBCLoggerProperties(IProjectInfo project,File log4jProperties) throws ServiceException {
		super(log4jProperties.getAbsolutePath());
	}
	public JDBCLoggerProperties(IProjectInfo project) throws ServiceException {
		super(Utilities.normalizedProjectName(project.getProjectName())+".dao.jdbc.log4j2.properties");
	}

	public static JDBCLoggerProperties getInstance(IProjectInfo project) throws ServiceException {
		return new JDBCLoggerProperties(project);
	}
	
} 
