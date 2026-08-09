/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.web.lib.mvc.security;

import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.jsoup.safety.Safelist;
import org.slf4j.Logger;

/**
 * SecurityProperties
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InputSanitizerProperties {

	private static Logger log;
	private static InputSanitizerProperties instance;
	private Properties properties;
	private Safelist safelist;
	private Set<String> cssProperties;

	public static synchronized void init(Properties p, Logger log) {
		if(InputSanitizerProperties.log == null) {
			InputSanitizerProperties.log = log;
		}
		if(InputSanitizerProperties.instance == null) {
			InputSanitizerProperties.instance = new InputSanitizerProperties(p);
		}
	}

	public static synchronized InputSanitizerProperties getInstance() {
		return instance;
	}

	private InputSanitizerProperties(Properties p) {
		this.properties = p;
		this.safelist = createSafelist();
		this.cssProperties = createCssProperties();
	}
	
	private Safelist createSafelist() {
        Safelist customSafelist = new Safelist();

        // Leggi i tag consentiti dal file properties
        String tags = getProperty("tag.whitelist");
        for (String tag : tags.split(",")) {
            customSafelist.addTags(tag.trim());
        }

        // Leggi i tag speciali
        String specialTags = getProperty("tag.contentSpecial");
        for (String tag : specialTags.split(",")) {
            customSafelist.addTags(tag.trim());
        }

        // Leggi gli attributi consentiti
        for (String tag : this.properties.stringPropertyNames()) {
            if (tag.startsWith("attributes.")) {
                String tagName = tag.substring("attributes.".length());
                String attributes = getProperty(tag);
                for (String attribute : attributes.split(",")) {
                    customSafelist.addAttributes(tagName, attribute.trim());
                }
            }
        }

        /** Attributi CSS
         *
         * Il blocco seguente registrava le proprietà CSS consentite sulla Safelist:
         *
         * String cssAttributes = getProperty("css.attributes");
         * for (String attribute : cssAttributes.split(",")) {
         *     customSafelist.addAttributes("style", attribute.trim());
         * }
         *
         * Il metodo 'Safelist.addAttributes(tag, attributi)' interpreta però il primo argomento come nome di un
         * elemento HTML e non di un attributo: la chiamata non filtrava quindi alcuna proprietà CSS (jsoup non
         * effettua alcun parsing del CSS) e, come effetto collaterale, registrava l'elemento raw-text '<style>'
         * tra i tag consentiti, facendo ricadere il prodotto nella casistica vulnerabile descritta da CVE-2026-71497.
         *
         * Il filtro sulle proprietà CSS è adesso realizzato dalla classe CssSanitizer, applicata dal Validatore
         * sul documento HTML già sanificato; le proprietà consentite continuano ad essere definite dalla medesima
         * property 'css.attributes', letta dal metodo createCssProperties() ed esposta da getCssProperties().
         */

        // Leggi i protocolli consentiti
        String protocols = getProperty("protocols.A.href");
        if (protocols != null) {
            for (String protocol : protocols.split(",")) {
                customSafelist.addProtocols("A", "href", protocol.trim());
            }
        }

        return customSafelist;
    }

	/**
	 * Legge dalla property 'css.attributes' i nomi delle proprietà CSS ammesse all'interno degli attributi
	 * 'style' consentiti dalla Safelist. Il filtro è applicato dalla classe CssSanitizer.
	 */
	private Set<String> createCssProperties() {
		Set<String> properties = new HashSet<>();

		String cssAttributes = getProperty("css.attributes");
		if(cssAttributes != null) {
			for (String attribute : cssAttributes.split(",")) {
				String name = attribute.trim().toLowerCase(Locale.ROOT);
				if(!name.isEmpty()) {
					properties.add(name);
				}
			}
		}

		return properties;
	}

	public String getProperty(String property) {
		return this.properties.getProperty( property );
	}

	public Safelist getSafelist() {
		return this.safelist;
	}

	public Set<String> getCssProperties() {
		return this.cssProperties;
	}
}
