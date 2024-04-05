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
package org.openspcoop2.core.statistiche;

import jakarta.xml.bind.annotation.XmlRegistry;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.statistiche package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.statistiche
     * 
     */
    public ObjectFactory() {
        // Create a new ObjectFactory
    }

    /**
     * Create an instance of {@link StatisticaOraria }
     */
    public StatisticaOraria createStatisticaOraria() {
        return new StatisticaOraria();
    }

    /**
     * Create an instance of {@link StatisticaContenuti }
     */
    public StatisticaContenuti createStatisticaContenuti() {
        return new StatisticaContenuti();
    }

    /**
     * Create an instance of {@link StatisticaMensile }
     */
    public StatisticaMensile createStatisticaMensile() {
        return new StatisticaMensile();
    }

    /**
     * Create an instance of {@link StatisticaGiornaliera }
     */
    public StatisticaGiornaliera createStatisticaGiornaliera() {
        return new StatisticaGiornaliera();
    }

    /**
     * Create an instance of {@link StatisticaSettimanale }
     */
    public StatisticaSettimanale createStatisticaSettimanale() {
        return new StatisticaSettimanale();
    }

    /**
     * Create an instance of {@link StatisticaInfo }
     */
    public StatisticaInfo createStatisticaInfo() {
        return new StatisticaInfo();
    }

    /**
     * Create an instance of {@link Statistica }
     */
    public Statistica createStatistica() {
        return new Statistica();
    }

    private static final QName _StatisticaOrariaContenuti = new QName("http://www.openspcoop2.org/core/statistiche", "statistica-oraria-contenuti");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatisticaContenuti }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/statistiche", name="statistica-oraria-contenuti")
    public JAXBElement<StatisticaContenuti> createStatisticaOrariaContenuti() {
        return new JAXBElement<StatisticaContenuti>(_StatisticaOrariaContenuti, StatisticaContenuti.class, null, this.createStatisticaContenuti());
    }
    public JAXBElement<StatisticaContenuti> createStatisticaOrariaContenuti(StatisticaContenuti statisticaOrariaContenuti) {
        return new JAXBElement<StatisticaContenuti>(_StatisticaOrariaContenuti, StatisticaContenuti.class, null, statisticaOrariaContenuti);
    }

    private static final QName _StatisticaMensileContenuti = new QName("http://www.openspcoop2.org/core/statistiche", "statistica-mensile-contenuti");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatisticaContenuti }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/statistiche", name="statistica-mensile-contenuti")
    public JAXBElement<StatisticaContenuti> createStatisticaMensileContenuti() {
        return new JAXBElement<StatisticaContenuti>(_StatisticaMensileContenuti, StatisticaContenuti.class, null, this.createStatisticaContenuti());
    }
    public JAXBElement<StatisticaContenuti> createStatisticaMensileContenuti(StatisticaContenuti statisticaMensileContenuti) {
        return new JAXBElement<StatisticaContenuti>(_StatisticaMensileContenuti, StatisticaContenuti.class, null, statisticaMensileContenuti);
    }

    private static final QName _StatisticaGiornalieraContenuti = new QName("http://www.openspcoop2.org/core/statistiche", "statistica-giornaliera-contenuti");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatisticaContenuti }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/statistiche", name="statistica-giornaliera-contenuti")
    public JAXBElement<StatisticaContenuti> createStatisticaGiornalieraContenuti() {
        return new JAXBElement<StatisticaContenuti>(_StatisticaGiornalieraContenuti, StatisticaContenuti.class, null, this.createStatisticaContenuti());
    }
    public JAXBElement<StatisticaContenuti> createStatisticaGiornalieraContenuti(StatisticaContenuti statisticaGiornalieraContenuti) {
        return new JAXBElement<StatisticaContenuti>(_StatisticaGiornalieraContenuti, StatisticaContenuti.class, null, statisticaGiornalieraContenuti);
    }

    private static final QName _StatisticaSettimanaleContenuti = new QName("http://www.openspcoop2.org/core/statistiche", "statistica-settimanale-contenuti");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatisticaContenuti }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.openspcoop2.org/core/statistiche", name="statistica-settimanale-contenuti")
    public JAXBElement<StatisticaContenuti> createStatisticaSettimanaleContenuti() {
        return new JAXBElement<StatisticaContenuti>(_StatisticaSettimanaleContenuti, StatisticaContenuti.class, null, this.createStatisticaContenuti());
    }
    public JAXBElement<StatisticaContenuti> createStatisticaSettimanaleContenuti(StatisticaContenuti statisticaSettimanaleContenuti) {
        return new JAXBElement<StatisticaContenuti>(_StatisticaSettimanaleContenuti, StatisticaContenuti.class, null, statisticaSettimanaleContenuti);
    }


 }
