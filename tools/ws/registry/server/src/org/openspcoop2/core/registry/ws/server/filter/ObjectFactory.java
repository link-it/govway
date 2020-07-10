/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.registry.ws.server.filter;

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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.registry
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SearchFilterAccordoCooperazione }
     */
    public SearchFilterAccordoCooperazione createSearchFilterAccordoCooperazione() {
        return new SearchFilterAccordoCooperazione();
    }
    
    /**
     * Create an instance of {@link SearchFilterPortaDominio }
     */
    public SearchFilterPortaDominio createSearchFilterPortaDominio() {
        return new SearchFilterPortaDominio();
    }
    
    /**
     * Create an instance of {@link SearchFilterRuolo }
     */
    public SearchFilterRuolo createSearchFilterRuolo() {
        return new SearchFilterRuolo();
    }
    
    /**
     * Create an instance of {@link SearchFilterScope }
     */
    public SearchFilterScope createSearchFilterScope() {
        return new SearchFilterScope();
    }
    
    /**
     * Create an instance of {@link SearchFilterAccordoServizioParteComune }
     */
    public SearchFilterAccordoServizioParteComune createSearchFilterAccordoServizioParteComune() {
        return new SearchFilterAccordoServizioParteComune();
    }
    
    /**
     * Create an instance of {@link SearchFilterAccordoServizioParteSpecifica }
     */
    public SearchFilterAccordoServizioParteSpecifica createSearchFilterAccordoServizioParteSpecifica() {
        return new SearchFilterAccordoServizioParteSpecifica();
    }
    
    /**
     * Create an instance of {@link SearchFilterGruppo }
     */
    public SearchFilterGruppo createSearchFilterGruppo() {
        return new SearchFilterGruppo();
    }
    
    /**
     * Create an instance of {@link SearchFilterSoggetto }
     */
    public SearchFilterSoggetto createSearchFilterSoggetto() {
        return new SearchFilterSoggetto();
    }
    

}