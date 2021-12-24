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
package org.openspcoop2.web.monitor.core.utils;

import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.config.InvokerNodiRuntime;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.slf4j.Logger;

/**
 * JmxUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class JmxUtils {

	private InvokerNodiRuntime invoker = null;

	private transient Logger log;
	public JmxUtils(Logger log) throws Exception{
		this.log = log;
		PddMonitorProperties monitorProperties = PddMonitorProperties.getInstance(this.log);
		ConfigurazioneNodiRuntime config = monitorProperties.getConfigurazioneNodiRuntime();
		this.invoker = new InvokerNodiRuntime(log, config);
	}

	public InvokerNodiRuntime getInvoker() {
		return this.invoker;
	}
	
}
