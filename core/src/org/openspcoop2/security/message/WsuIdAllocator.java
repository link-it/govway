/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.security.message;

import org.apache.xml.security.stax.impl.util.IDGenerator;


/**
 * WsuIdAllocator 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WsuIdAllocator implements org.apache.wss4j.dom.WsuIdAllocator {
	
	private String prefixComponent;
	public WsuIdAllocator(String prefix){
		this.prefixComponent=prefix;
	}
	    
    @Override
	public String createId(String prefix, Object o) {
        if (prefix == null) {
            return IDGenerator.generateID(this.prefixComponent);
        }

        return IDGenerator.generateID(this.prefixComponent+prefix);
    }

    @Override
	public String createSecureId(String prefix, Object o) {
    	if (prefix == null) {
            return IDGenerator.generateID(this.prefixComponent);
        }

        return IDGenerator.generateID(this.prefixComponent+prefix);
    }

}
