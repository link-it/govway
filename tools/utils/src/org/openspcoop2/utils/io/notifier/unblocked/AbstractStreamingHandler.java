/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
package org.openspcoop2.utils.io.notifier.unblocked;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.openspcoop2.utils.UtilsException;

/**
 * AbstractStreamingHandler
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractStreamingHandler extends PipedUnblockedStream implements Callable<ResultStreamingHandler> {
	
	
	public AbstractStreamingHandler(Logger log, long sizeBuffer) throws Exception {
		super(log,sizeBuffer);
	}

	public abstract boolean isPrematureEnd() throws UtilsException;

	public abstract String getError();
	
	public abstract Throwable getException();
	
	@Override
	public abstract ResultStreamingHandler call() throws UtilsException;
	
}
