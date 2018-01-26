/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

package org.openspcoop2.utils.wadl;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * SchemaCallback che riceve gli schemi, presenti nella grammatica, durante la lettura di un wadl
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class NullSchemaCallback implements org.jvnet.ws.wadl.ast.WadlAstBuilder.SchemaCallback {

	
	@Override
	public void processSchema(InputSource isSource) {
		
				
	}

	@Override
	public void processSchema(String systemId, Element element) {
		
	}

}
