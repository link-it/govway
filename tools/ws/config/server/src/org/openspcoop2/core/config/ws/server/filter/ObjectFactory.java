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
package org.openspcoop2.core.config.ws.server.filter;

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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.config
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SearchFilterPortaApplicativa }
     */
    public SearchFilterPortaApplicativa createSearchFilterPortaApplicativa() {
        return new SearchFilterPortaApplicativa();
    }
    
    /**
     * Create an instance of {@link SearchFilterPortaDelegata }
     */
    public SearchFilterPortaDelegata createSearchFilterPortaDelegata() {
        return new SearchFilterPortaDelegata();
    }
    
    /**
     * Create an instance of {@link SearchFilterServizioApplicativo }
     */
    public SearchFilterServizioApplicativo createSearchFilterServizioApplicativo() {
        return new SearchFilterServizioApplicativo();
    }
    
    /**
     * Create an instance of {@link SearchFilterSoggetto }
     */
    public SearchFilterSoggetto createSearchFilterSoggetto() {
        return new SearchFilterSoggetto();
    }
    

}