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
package org.openspcoop2.pdd.monitor.ws.server.wrapped;

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
     * Create an instance of {@link FindStatoPddResponse }
     */
    public FindStatoPddResponse createFindStatoPddResponse() {
        return new FindStatoPddResponse();
    }
    
    /**
     * Create an instance of {@link FindStatoPdd }
     */
    public FindStatoPdd createFindStatoPdd() {
        return new FindStatoPdd();
    }
    
    /**
     * Create an instance of {@link FindAllMessaggio }
     */
    public FindAllMessaggio createFindAllMessaggio() {
        return new FindAllMessaggio();
    }
    
    /**
     * Create an instance of {@link DeleteAllByFilterMessaggio }
     */
    public DeleteAllByFilterMessaggio createDeleteAllByFilterMessaggio() {
        return new DeleteAllByFilterMessaggio();
    }
    
    /**
     * Create an instance of {@link DeleteAllByFilterMessaggioResponse }
     */
    public DeleteAllByFilterMessaggioResponse createDeleteAllByFilterMessaggioResponse() {
        return new DeleteAllByFilterMessaggioResponse();
    }
    
    /**
     * Create an instance of {@link CountMessaggio }
     */
    public CountMessaggio createCountMessaggio() {
        return new CountMessaggio();
    }
    
    /**
     * Create an instance of {@link CountMessaggioResponse }
     */
    public CountMessaggioResponse createCountMessaggioResponse() {
        return new CountMessaggioResponse();
    }
    
    /**
     * Create an instance of {@link FindAllMessaggioResponse }
     */
    public FindAllMessaggioResponse createFindAllMessaggioResponse() {
        return new FindAllMessaggioResponse();
    }
    

}