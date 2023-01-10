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
 * JWEOptions
 * 
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class JWEOptions {

	private JOSESerialization serialization;
	private boolean deflate = false; // https://tools.ietf.org/html/rfc7516#section-4.1.3 The "zip" (compression algorithm) applied to the plaintext before encryption, compression with the DEFLATE [RFC1951] algorithm
	
	public JWEOptions(JOSESerialization serialization) {
		this.serialization = serialization;
	}
	
	public JOSESerialization getSerialization() {
		return this.serialization;
	}
	public void setSerialization(JOSESerialization serialization) {
		this.serialization = serialization;
	}
	public boolean isDeflate() {
		return this.deflate;
	}
	public void setDeflate(boolean deflate) {
		this.deflate = deflate;
	}
	
}
