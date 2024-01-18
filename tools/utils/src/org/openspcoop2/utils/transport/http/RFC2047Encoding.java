/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.utils.transport.http;


/**
* RFC2047Encoding
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/

public enum RFC2047Encoding {

	// You can specify either “B” or “Q” for the EncodingStyle parameter, indicating either Base64 or “Quoted-Printable” content-transfer-encoding, respectively.
	// Oracle recommends using the “Q” encoding when most of the characters to be encoded are in the ASCII character set; 
	// otherwise, you should use the “B” encoding. 	
	Q,B;
	
}


