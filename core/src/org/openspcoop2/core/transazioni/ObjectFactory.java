/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.core.transazioni;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.transazioni package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.transazioni
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DumpContenuto }
     */
    public DumpContenuto createDumpContenuto() {
        return new DumpContenuto();
    }

    /**
     * Create an instance of {@link Transazione }
     */
    public Transazione createTransazione() {
        return new Transazione();
    }

    /**
     * Create an instance of {@link DumpMultipartHeader }
     */
    public DumpMultipartHeader createDumpMultipartHeader() {
        return new DumpMultipartHeader();
    }

    /**
     * Create an instance of {@link DumpMessaggio }
     */
    public DumpMessaggio createDumpMessaggio() {
        return new DumpMessaggio();
    }

    /**
     * Create an instance of {@link DumpAllegato }
     */
    public DumpAllegato createDumpAllegato() {
        return new DumpAllegato();
    }

    /**
     * Create an instance of {@link DumpHeaderTrasporto }
     */
    public DumpHeaderTrasporto createDumpHeaderTrasporto() {
        return new DumpHeaderTrasporto();
    }

    /**
     * Create an instance of {@link IdDumpMessaggio }
     */
    public IdDumpMessaggio createIdDumpMessaggio() {
        return new IdDumpMessaggio();
    }

    /**
     * Create an instance of {@link TransazioneExtendedInfo }
     */
    public TransazioneExtendedInfo createTransazioneExtendedInfo() {
        return new TransazioneExtendedInfo();
    }

    /**
     * Create an instance of {@link DumpHeaderAllegato }
     */
    public DumpHeaderAllegato createDumpHeaderAllegato() {
        return new DumpHeaderAllegato();
    }


 }
