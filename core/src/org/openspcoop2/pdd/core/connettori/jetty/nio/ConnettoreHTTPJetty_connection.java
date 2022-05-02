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

package org.openspcoop2.pdd.core.connettori.jetty.nio;

import org.eclipse.jetty.client.HttpClient;

import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.nio.AbstractConnettoreHTTPCORE_connection;

/**
 * ConnettoreHTTPJava_connection
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPJetty_connection extends AbstractConnettoreHTTPCORE_connection<HttpClient>{

	public ConnettoreHTTPJetty_connection(String key, HttpClient client) {
		super(key, client);
	}

	@Override
	public String getStatus() {
		return "Undefined";
	}

	@Override
	public void close() throws ConnettoreException {
		// TBK da controllare se necessita prevedere una close
		// Unimplemented
	}

}
