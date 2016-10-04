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
package org.openspcoop2.core.tracciamento.ws.server.wrapped;

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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.tracciamento
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetTracciaResponse }
     */
    public GetTracciaResponse createGetTracciaResponse() {
        return new GetTracciaResponse();
    }
    
    /**
     * Create an instance of {@link GetTraccia }
     */
    public GetTraccia createGetTraccia() {
        return new GetTraccia();
    }
    
    /**
     * Create an instance of {@link ExistsTracciaResponse }
     */
    public ExistsTracciaResponse createExistsTracciaResponse() {
        return new ExistsTracciaResponse();
    }
    
    /**
     * Create an instance of {@link FindTraccia }
     */
    public FindTraccia createFindTraccia() {
        return new FindTraccia();
    }
    
    /**
     * Create an instance of {@link FindTracciaResponse }
     */
    public FindTracciaResponse createFindTracciaResponse() {
        return new FindTracciaResponse();
    }
    
    /**
     * Create an instance of {@link FindAllIdsTracciaResponse }
     */
    public FindAllIdsTracciaResponse createFindAllIdsTracciaResponse() {
        return new FindAllIdsTracciaResponse();
    }
    
    /**
     * Create an instance of {@link CountTraccia }
     */
    public CountTraccia createCountTraccia() {
        return new CountTraccia();
    }
    
    /**
     * Create an instance of {@link ExistsTraccia }
     */
    public ExistsTraccia createExistsTraccia() {
        return new ExistsTraccia();
    }
    
    /**
     * Create an instance of {@link FindAllTraccia }
     */
    public FindAllTraccia createFindAllTraccia() {
        return new FindAllTraccia();
    }
    
    /**
     * Create an instance of {@link FindAllTracciaResponse }
     */
    public FindAllTracciaResponse createFindAllTracciaResponse() {
        return new FindAllTracciaResponse();
    }
    
    /**
     * Create an instance of {@link CountTracciaResponse }
     */
    public CountTracciaResponse createCountTracciaResponse() {
        return new CountTracciaResponse();
    }
    
    /**
     * Create an instance of {@link FindAllIdsTraccia }
     */
    public FindAllIdsTraccia createFindAllIdsTraccia() {
        return new FindAllIdsTraccia();
    }
    

}