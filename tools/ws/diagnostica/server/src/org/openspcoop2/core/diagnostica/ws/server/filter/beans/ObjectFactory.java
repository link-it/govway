/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.core.diagnostica.ws.server.filter.beans;

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
     * Create an instance of {@link SoggettoIdentificativo }
     */
    public SoggettoIdentificativo createSoggettoIdentificativo() {
        return new SoggettoIdentificativo();
    }
    
    /**
     * Create an instance of {@link FiltroInformazioniDiagnostici }
     */
    public FiltroInformazioniDiagnostici createFiltroInformazioniDiagnostici() {
        return new FiltroInformazioniDiagnostici();
    }
    
    /**
     * Create an instance of {@link DominioSoggetto }
     */
    public DominioSoggetto createDominioSoggetto() {
        return new DominioSoggetto();
    }
    
    /**
     * Create an instance of {@link Protocollo }
     */
    public Protocollo createProtocollo() {
        return new Protocollo();
    }
    
    /**
     * Create an instance of {@link FiltroInformazioneProtocollo }
     */
    public FiltroInformazioneProtocollo createFiltroInformazioneProtocollo() {
        return new FiltroInformazioneProtocollo();
    }
    
    /**
     * Create an instance of {@link Servizio }
     */
    public Servizio createServizio() {
        return new Servizio();
    }
    
    /**
     * Create an instance of {@link DominioTransazione }
     */
    public DominioTransazione createDominioTransazione() {
        return new DominioTransazione();
    }
    
    /**
     * Create an instance of {@link DominioDiagnostico }
     */
    public DominioDiagnostico createDominioDiagnostico() {
        return new DominioDiagnostico();
    }
    
    /**
     * Create an instance of {@link Soggetto }
     */
    public Soggetto createSoggetto() {
        return new Soggetto();
    }
    

}