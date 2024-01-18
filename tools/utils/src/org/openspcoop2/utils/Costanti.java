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
package org.openspcoop2.utils;

/**
 * Costanti
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {
	
	private Costanti() {}

	public static final String OPENSPCOOP2 = "GovWay";
	
	public static final String OPENSPCOOP2_LOCAL_HOME = "GOVWAY_HOME";
	public static final String OPENSPCOOP2_FORCE_CONFIG_FILE = "GOVWAY_FORCE_CONFIG_FILE";
	
	public static final String OPENSPCOOP2_LOOKUP = "GOVWAY_LOOKUP";
	 
    /** Versione beta, es: "b1" */
    public static final String OPENSPCOOP2_BETA = ".0.b1"; /**".0.rc1";*/
    /** Versione di OpenSPCoop */
    public static final String OPENSPCOOP2_VERSION = "3.4"+Costanti.OPENSPCOOP2_BETA;
    /** Versione di OpenSPCoop */
    public static final String OPENSPCOOP2_PRODUCT = "GovWay";
    /** Versione di OpenSPCoop (User-Agent) */
    public static final String OPENSPCOOP2_PRODUCT_VERSION = Costanti.OPENSPCOOP2_PRODUCT+"/"+Costanti.OPENSPCOOP2_VERSION;
    /** Details */
    public static final String OPENSPCOOP2_DETAILS = "www.govway.org";
    /** Copyright */
	public static final String OPENSPCOOP2_COPYRIGHT = "2005-2024 Link.it srl";
	 /** License */
	public static final String OPENSPCOOP2_LICENSE = "This program is free software: you can redistribute it and/or modify\n"+
	"it under the terms of the GNU General Public License version 3, as published by\n"+
	"the Free Software Foundation.\n"+
	"\n"+
	"This program is distributed in the hope that it will be useful,\n"+
	"but WITHOUT ANY WARRANTY; without even the implied warranty of\n"+
	"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"+
	"GNU General Public License for more details.\n"+
	"\n"+
	"You should have received a copy of the GNU General Public License\n"+
	"along with this program.  If not, see <http://www.gnu.org/licenses/>.";

}
