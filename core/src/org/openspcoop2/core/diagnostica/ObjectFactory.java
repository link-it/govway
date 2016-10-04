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
package org.openspcoop2.core.diagnostica;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.diagnostica package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/

@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.diagnostica
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
     * Create an instance of {@link MessaggioDiagnostico }
     */
    public MessaggioDiagnostico createMessaggioDiagnostico() {
        return new MessaggioDiagnostico();
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
     * Create an instance of {@link IdInformazioniProtocolloTransazione }
     */
    public IdInformazioniProtocolloTransazione createIdInformazioniProtocolloTransazione() {
        return new IdInformazioniProtocolloTransazione();
    }

    /**
     * Create an instance of {@link Soggetto }
     */
    public Soggetto createSoggetto() {
        return new Soggetto();
    }

    /**
     * Create an instance of {@link Proprieta }
     */
    public Proprieta createProprieta() {
        return new Proprieta();
    }

    /**
     * Create an instance of {@link InformazioniProtocolloTransazione }
     */
    public InformazioniProtocolloTransazione createInformazioniProtocolloTransazione() {
        return new InformazioniProtocolloTransazione();
    }


 }
