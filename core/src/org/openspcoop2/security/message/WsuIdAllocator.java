/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
