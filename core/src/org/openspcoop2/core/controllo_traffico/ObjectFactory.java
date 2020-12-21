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
package org.openspcoop2.core.controllo_traffico;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openspcoop2.core.controllo_traffico package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openspcoop2.core.controllo_traffico
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConfigurazioneGenerale }
     */
    public ConfigurazioneGenerale createConfigurazioneGenerale() {
        return new ConfigurazioneGenerale();
    }

    /**
     * Create an instance of {@link ElencoIdPolicyAttive }
     */
    public ElencoIdPolicyAttive createElencoIdPolicyAttive() {
        return new ElencoIdPolicyAttive();
    }

    /**
     * Create an instance of {@link TempiRispostaErogazione }
     */
    public TempiRispostaErogazione createTempiRispostaErogazione() {
        return new TempiRispostaErogazione();
    }

    /**
     * Create an instance of {@link Cache }
     */
    public Cache createCache() {
        return new Cache();
    }

    /**
     * Create an instance of {@link ConfigurazioneControlloTraffico }
     */
    public ConfigurazioneControlloTraffico createConfigurazioneControlloTraffico() {
        return new ConfigurazioneControlloTraffico();
    }

    /**
     * Create an instance of {@link ConfigurazioneRateLimiting }
     */
    public ConfigurazioneRateLimiting createConfigurazioneRateLimiting() {
        return new ConfigurazioneRateLimiting();
    }

    /**
     * Create an instance of {@link IdActivePolicy }
     */
    public IdActivePolicy createIdActivePolicy() {
        return new IdActivePolicy();
    }

    /**
     * Create an instance of {@link ConfigurazionePolicy }
     */
    public ConfigurazionePolicy createConfigurazionePolicy() {
        return new ConfigurazionePolicy();
    }

    /**
     * Create an instance of {@link AttivazionePolicy }
     */
    public AttivazionePolicy createAttivazionePolicy() {
        return new AttivazionePolicy();
    }

    /**
     * Create an instance of {@link IdPolicy }
     */
    public IdPolicy createIdPolicy() {
        return new IdPolicy();
    }

    /**
     * Create an instance of {@link AttivazionePolicyRaggruppamento }
     */
    public AttivazionePolicyRaggruppamento createAttivazionePolicyRaggruppamento() {
        return new AttivazionePolicyRaggruppamento();
    }

    /**
     * Create an instance of {@link ElencoPolicy }
     */
    public ElencoPolicy createElencoPolicy() {
        return new ElencoPolicy();
    }

    /**
     * Create an instance of {@link TempiRispostaFruizione }
     */
    public TempiRispostaFruizione createTempiRispostaFruizione() {
        return new TempiRispostaFruizione();
    }

    /**
     * Create an instance of {@link ElencoIdPolicy }
     */
    public ElencoIdPolicy createElencoIdPolicy() {
        return new ElencoIdPolicy();
    }

    /**
     * Create an instance of {@link AttivazionePolicyFiltro }
     */
    public AttivazionePolicyFiltro createAttivazionePolicyFiltro() {
        return new AttivazionePolicyFiltro();
    }

    /**
     * Create an instance of {@link ElencoPolicyAttive }
     */
    public ElencoPolicyAttive createElencoPolicyAttive() {
        return new ElencoPolicyAttive();
    }


 }
