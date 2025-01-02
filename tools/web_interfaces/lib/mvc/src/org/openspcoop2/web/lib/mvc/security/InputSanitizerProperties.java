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

package org.openspcoop2.web.lib.mvc.security;

import java.util.Properties;

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

	public static synchronized void init(Properties p, Logger log) {
		if(InputSanitizerProperties.log == null) {
			InputSanitizerProperties.log = log;
		}
		if(InputSanitizerProperties.instance == null) {
			InputSanitizerProperties.instance = new InputSanitizerProperties(p);
		}
	}

	public static InputSanitizerProperties getInstance() {
		return instance;
	}

	private InputSanitizerProperties(Properties p) {
		this.properties = p;
		this.safelist = createSafelist();
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

        // Leggi gli attributi CSS
        String cssAttributes = getProperty("css.attributes");
        for (String attribute : cssAttributes.split(",")) {
            customSafelist.addAttributes("style", attribute.trim());
        }

        // Leggi i protocolli consentiti
        String protocols = getProperty("protocols.A.href");
        if (protocols != null) {
            for (String protocol : protocols.split(",")) {
                customSafelist.addProtocols("A", "href", protocol.trim());
            }
        }

        return customSafelist;
    }

	public String getProperty(String property) {
		return this.properties.getProperty( property );
	}

	public Safelist getSafelist() {
		return this.safelist;
	}
}
