/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package it.cnipa.schemas._2003.egovit.exception1_0;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.cnipa.schemas._2003.egovit.exception1_0 package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.cnipa.schemas._2003.egovit.exception1_0
     * 
     */
    public ObjectFactory() {
        // Create a new ObjectFactory
    }

    /**
     * Create an instance of {@link Eccezione }
     */
    public Eccezione createEccezione() {
        return new Eccezione();
    }

    /**
     * Create an instance of {@link MessaggioDiErroreApplicativo }
     */
    public MessaggioDiErroreApplicativo createMessaggioDiErroreApplicativo() {
        return new MessaggioDiErroreApplicativo();
    }

    /**
     * Create an instance of {@link EccezioneBusta }
     */
    public EccezioneBusta createEccezioneBusta() {
        return new EccezioneBusta();
    }

    /**
     * Create an instance of {@link EccezioneProcessamento }
     */
    public EccezioneProcessamento createEccezioneProcessamento() {
        return new EccezioneProcessamento();
    }


 }
