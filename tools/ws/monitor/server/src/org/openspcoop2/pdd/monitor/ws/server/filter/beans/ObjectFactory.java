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
package org.openspcoop2.pdd.monitor.ws.server.filter.beans;

import javax.xml.bind.annotation.XmlRegistry;




/**     
 * ObjectFactory
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
 @XmlRegistry
public class ObjectFactory {

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.pdd.monitor
     * 
     */
    public ObjectFactory() {
    	// Create a new ObjectFactory
    }

    /**
     * Create an instance of {@link Filtro }
     */
    public Filtro createFiltro() {
        return new Filtro();
    }
    
    /**
     * Create an instance of {@link BustaServizio }
     */
    public BustaServizio createBustaServizio() {
        return new BustaServizio();
    }
    
    /**
     * Create an instance of {@link BustaSoggetto }
     */
    public BustaSoggetto createBustaSoggetto() {
        return new BustaSoggetto();
    }
    
    /**
     * Create an instance of {@link Busta }
     */
    public Busta createBusta() {
        return new Busta();
    }
    

}