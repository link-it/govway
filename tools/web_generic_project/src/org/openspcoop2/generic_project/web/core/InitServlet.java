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
package org.openspcoop2.generic_project.web.core;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * InitServlet Classe che fornisce il supporto alle operazioni da eseguire allo startup dell'applicazione.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InitServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String LOGGER_FILE_PATH = "loggerPath";
	
	private String loggerPath= null;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			String initParameter = getInitParameter(LOGGER_FILE_PATH);

			if(initParameter != null)
				this.setLoggerPath(initParameter);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	public String getLoggerPath() {
		return this.loggerPath;
	}

	public void setLoggerPath(String loggerPath) {
		this.loggerPath = loggerPath;
	}
}
