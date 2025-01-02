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
package org.openspcoop2.core.controllo_traffico.model;

/**     
 * Factory
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModelFactory {

	public static ConfigurazioneGeneraleModel CONFIGURAZIONE_GENERALE = new ConfigurazioneGeneraleModel();
	
	public static ConfigurazioneRateLimitingProprietaModel CONFIGURAZIONE_RATE_LIMITING_PROPRIETA = new ConfigurazioneRateLimitingProprietaModel();
	
	public static ConfigurazionePolicyModel CONFIGURAZIONE_POLICY = new ConfigurazionePolicyModel();
	
	public static AttivazionePolicyModel ATTIVAZIONE_POLICY = new AttivazionePolicyModel();
	

}