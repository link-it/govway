package org.openspcoop2.web.monitor.core.taglib;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

public class Utils {

	private static Logger log = LoggerWrapperFactory.getLogger(Utils.class);
	/**
	 * Concatena la stringa a con la stringa b
	 * @param a
	 * @param b
	 * @return concatenazione delle stringhe passate
	 */
	public static String concat(String a, String b) {
		Utils.log.debug("concateno a:"+a+" b:"+b);
	      return StringUtils.join(new String[]{a,b},null); 
	}

	/**
	 * Concatena le stringhe presenti nell'array in input utilizzando come separatore
	 * il carattere indicato, se il separatore e' null allora le stringhe saranno concatenate senza separatore
	 * @param strings
	 * @param separator
	 * @return concatenazione delle stringhe passate
	 */
	public static String concat(String[] strings, String separator){
		return StringUtils.join(strings,separator);
	}
}
