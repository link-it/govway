/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.lib.mvc.login;

import java.time.Instant;

/**
 * Informazioni sul tentativo di login
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class LoginAttempt {

	private final Instant expiring;
	private final int retries;
	
	public LoginAttempt(Instant expiring, int retries) {
		this.expiring = expiring;
		this.retries = retries;
	}

	public Instant getExpiring() {
		return this.expiring;
	}
	
	public int getRetries() {
		return this.retries;
	}
}
