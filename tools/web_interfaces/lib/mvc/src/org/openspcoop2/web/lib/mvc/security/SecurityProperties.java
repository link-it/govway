/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * SecurityProperties
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityProperties {

	private static final Map<String, Pattern> patternCache = new HashMap<String, Pattern>();

	private static Logger log;
	private static SecurityProperties instance;
	private Properties properties;

	public static synchronized void init(Properties p, Logger log) {
		if(SecurityProperties.log == null) {
			SecurityProperties.log = log;
		}
		if(SecurityProperties.instance == null) {
			SecurityProperties.instance = new SecurityProperties(p);
		}
	}

	public static SecurityProperties getInstance() {
		return instance;
	}

	private SecurityProperties(Properties p) {
		this.properties = p;
	}

	public Pattern getValidationPattern(String key) {
		String value = getProperty( Costanti.PATTERN_VALIDAZIONE_PREFIX + key);

		if ( value == null || value.equals( "" ) ) return null;

		// controllo presenza in cache
		Pattern p = patternCache.get( value );
		if ( p != null ) return p;

		// compilazione del nuovo pattern
		try {
			Pattern q = Pattern.compile(value);
			patternCache.put( value, q );
			return q;
		} catch ( PatternSyntaxException e ) {
			SecurityProperties.log.error("Pattern di validazione non valido per il tipo " + key + ".");
			return null;
		}
	}

	public String getProperty(String property) {
		return this.properties.getProperty( property );
	}

	public Integer getIntProp(String property) throws UtilsException {
		String tmp = this.getProperty(property);
		try {
			return tmp != null ? Integer.parseInt(tmp) : null;
		} catch (NumberFormatException e) {
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (int value expected)");
		}
	}

	public Boolean getBooleanProp(String property) throws UtilsException {
		String tmp = this.getProperty( property );
		if ( tmp == null ) {
			return null;
		}
		if("true".equalsIgnoreCase(tmp)==false && "false".equalsIgnoreCase(tmp)==false){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (true/value expected)");
		}
		return Boolean.parseBoolean(tmp) ;
	}


}
