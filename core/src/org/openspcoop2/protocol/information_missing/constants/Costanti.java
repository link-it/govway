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
package org.openspcoop2.protocol.information_missing.constants;

import org.openspcoop2.protocol.information_missing.utils.ProjectInfo;

/**
 * Costanti
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {

	public static final String ROOT_LOCAL_NAME_INFORMATION_MISSING = "openspcoop2";
	public static final String LOCAL_NAME_SOGGETTO = "soggetto";
	public static final String LOCAL_NAME_SERVIZIO_APPLICATIVO = "servizio-applicativo";
	public static final String TARGET_NAMESPACE = (new ProjectInfo()).getProjectNamespace();
	
	// IL TIPO DEVE ESSERE DEFINITO PER POTER POI COMPRENDERE IL PROTOCOLLO public static final String TIPO_SOGGETTO_DEFAULT = "@OPENSPCOOP2_TIPO_SOGGETTO_DEFAULT@";
	public static final String NOME_SOGGETTO_DEFAULT = "@OPENSPCOOP2_NOME_SOGGETTO_DEFAULT@";
	
	// sinonimi
	public static final String TIPO_SOGGETTO = "@OPENSPCOOP2_TIPO_SOGGETTO@";
	public static final String NOME_SOGGETTO = "@OPENSPCOOP2_NOME_SOGGETTO@";
	public static final String TIPO_SOGGETTO_PROPRIETARIO = "@OPENSPCOOP2_TIPO_SOGGETTO_PROPRIETARIO@";
	public static final String NOME_SOGGETTO_PROPRIETARIO = "@OPENSPCOOP2_NOME_SOGGETTO_PROPRIETARIO@";
	
	public static final String TIPO_SOGGETTO_EROGATORE = "@OPENSPCOOP2_TIPO_SOGGETTO_EROGATORE@";
	public static final String NOME_SOGGETTO_EROGATORE = "@OPENSPCOOP2_NOME_SOGGETTO_EROGATORE@";
	
	public static final String TIPO_SERVIZIO = "@OPENSPCOOP2_TIPO_SERVIZIO@";
	public static final String NOME_SERVIZIO = "@OPENSPCOOP2_NOME_SERVIZIO@";
	public static final String AZIONE = "@OPENSPCOOP2_AZIONE@";
	
	public static final String TIPO_FRUITORE = "@OPENSPCOOP2_TIPO_FRUITORE@";
	public static final String NOME_FRUITORE = "@OPENSPCOOP2_NOME_FRUITORE@";
	
}
