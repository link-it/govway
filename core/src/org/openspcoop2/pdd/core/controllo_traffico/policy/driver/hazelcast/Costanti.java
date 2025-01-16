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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast;

/**
 * Costanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {
	
	private Costanti() {}
	
	public static final String SECURITY_RECOMMENDATIONS = "hazelcast.security.recommendations";
	public static final String SECURITY_RECOMMENDATIONS_ENABLED = "true";
	public static final String SECURITY_RECOMMENDATIONS_DISABLED = "false";
	
	public static final String DIAGNOSTICS = "hazelcast.diagnostics.enabled";
	public static final String DIAGNOSTICS_ENABLED = "true";
	public static final String DIAGNOSTICS_DISABLED = "false";
	
	public static final String DIAGNOSTICS_DIRECTORY = "hazelcast.diagnostics.directory";
	public static final String DIAGNOSTICS_DIRECTORY_MAX_ROLLED_FILE_COUNT = "hazelcast.diagnostics.max.rolled.file.count";
	public static final String DIAGNOSTICS_DIRECTORY_MAX_FILE_SIZE_MB = "hazelcast.diagnostics.max.file.size.mb";
	

}
