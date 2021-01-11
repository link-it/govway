/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.handlers.notifier.engine;

/**     
 * NotifierConstants
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotifierConstants {

	public final static String NOTIFIER_PREFIX = "NOTIFIER_";
	
	public final static String REQUEST_CONTENT_TYPE = NOTIFIER_PREFIX + "REQUEST_CONTENT_TYPE";
	public final static String RESPONSE_CONTENT_TYPE = NOTIFIER_PREFIX + "RESPONSE_CONTENT_TYPE";
	
	public final static String REQUEST_CONTENT_LENGHT = NOTIFIER_PREFIX + "REQUEST_CONTENT_LENGHT";
	public final static String RESPONSE_CONTENT_LENGHT = NOTIFIER_PREFIX + "RESPONSE_CONTENT_LENGHT";
	
	public final static String MANAGEMENT_MODE = NOTIFIER_PREFIX + "MANAGEMENT_MODE";
	
	public final static String DUMP_POST_PROCESS_ID_CONFIG = NOTIFIER_PREFIX + "DUMP_POST_PROCESS_ID_CONFIG";
	public final static String REQUEST_DUMP_POST_PROCESS_ENABLED = NOTIFIER_PREFIX + "REQUEST_DUMP_POST_PROCESS";
	public final static String RESPONSE_DUMP_POST_PROCESS_ENABLED = NOTIFIER_PREFIX + "RESPONSE_DUMP_POST_PROCESS";
	
	public final static String REQUEST_DUMP_POST_PROCESS_HEADER_TRASPORTO = NOTIFIER_PREFIX + "REQUEST_DUMP_POST_PROCESS_HEADER_TRASPORTO";
	public final static String RESPONSE_DUMP_POST_PROCESS_HEADER_TRASPORTO = NOTIFIER_PREFIX + "RESPONSE_DUMP_POST_PROCESS_HEADER_TRASPORTO";
	
	public final static String ID_HANDLER = "PddMonitorStreamingHandler";
	
}
