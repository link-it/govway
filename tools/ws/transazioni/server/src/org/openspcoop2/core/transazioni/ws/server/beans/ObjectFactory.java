/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.transazioni.ws.server.beans;

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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.transazioni
     * 
     */
    public ObjectFactory() {
    	// Create a new ObjectFactory
    }

    /**
     * Create an instance of {@link WrapperIdTransazione }
     */
    public WrapperIdTransazione createWrapperIdTransazione() {
        return new WrapperIdTransazione();
    }
    
    /**
     * Create an instance of {@link WrapperIdTransazioneApplicativoServer }
     */
    public WrapperIdTransazioneApplicativoServer createWrapperIdTransazioneApplicativoServer() {
        return new WrapperIdTransazioneApplicativoServer();
    }
    
    /**
     * Create an instance of {@link IdEntity }
     */
    public IdEntity createIdEntity() {
        return new IdEntity();
    }
    
    /**
     * Create an instance of {@link WrapperIdDumpMessaggio }
     */
    public WrapperIdDumpMessaggio createWrapperIdDumpMessaggio() {
        return new WrapperIdDumpMessaggio();
    }
    
    /**
     * Create an instance of {@link UseInfo }
     */
    public UseInfo createUseInfo() {
        return new UseInfo();
    }
    
    /**
     * Create an instance of {@link InUseCondition }
     */
    public InUseCondition createInUseCondition() {
        return new InUseCondition();
    }
    

}