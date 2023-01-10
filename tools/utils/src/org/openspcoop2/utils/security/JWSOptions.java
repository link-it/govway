/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.utils.security;

/**
 * JWSOptions
 * 
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class JWSOptions {

	private JOSESerialization serialization;
	private boolean detached = false;   // https://tools.ietf.org/html/rfc7515#page-57 (utilizzabile sia per JSON che per COMPACT, rende inutile l'opzione payloadEncoding)
	private boolean payloadEncoding = true;    // https://tools.ietf.org/html/rfc7797 (utilizzabile solamente per JSON. Per COMPACT è sconsigliato poichè limitato nei caratteri, non utilizzabile quindi per un json https://tools.ietf.org/html/rfc7797#page-8)
	
	public JWSOptions(JOSESerialization serialization) {
		this.serialization = serialization;
	}
	
	public JOSESerialization getSerialization() {
		return this.serialization;
	}
	public void setSerialization(JOSESerialization serialization) {
		this.serialization = serialization;
	}
	public boolean isDetached() {
		return this.detached;
	}
	public void setDetached(boolean detached) {
		this.detached = detached;
	}
	public boolean isPayloadEncoding() {
		return this.payloadEncoding;
	}
	public void setPayloadEncoding(boolean payloadEncoding) {
		this.payloadEncoding = payloadEncoding;
	}
}
