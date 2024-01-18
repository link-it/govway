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
package org.openspcoop2.protocol.sdi.constants;

import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.utils.ProjectInfo;

/**
 * SDICostantiServizioRiceviNotifica
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDICostantiServizioRiceviNotifica {

	public static final String SDI_SERVIZIO_RICEVI_NOTIFICA = "SdIRiceviNotifica";
    public static final String SDI_SERVIZIO_NOTIFICA_ESITO_AZIONE_NOTIFICA_ESITO = "NotificaEsito";
	 
	public static final String SDI_SERVIZIO_RICEVI_NOTIFICA_NAMESPACE = ProjectInfo.getInstance().getProjectNamespace();
	
	//public static final String SDI_SOAP_ACTION_SERVIZIO_NOTIFICA_ESITO_AZIONE_NOTIFICA_ESITO = "\"\"";
	public static final String SDI_SOAP_ACTION_SERVIZIO_NOTIFICA_ESITO_AZIONE_NOTIFICA_ESITO = "\"http://www.fatturapa.it/SdIRicezioneNotifiche/NotificaEsito\"";
	
	
	// AZIONE: NotificaEsito
	
	public static final String NOTIFICA_ESITO_RICHIESTA_ROOT_ELEMENT = "fileSdI";
	public static final String NOTIFICA_ESITO_RICHIESTA_ELEMENT_IDENTIFICATIVO_SDI = "IdentificativoSdI";
	public static final String NOTIFICA_ESITO_RICHIESTA_ELEMENT_NOME_FILE = "NomeFile";
	public static final String NOTIFICA_ESITO_RICHIESTA_ELEMENT_FILE = "File";
	
	public static final String NOTIFICA_ESITO_RISPOSTA_ROOT_ELEMENT = "rispostaSdINotificaEsito";
	public static final String NOTIFICA_ESITO_RISPOSTA_ELEMENT_ESITO = "Esito";
    public static final String NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO = "ScartoEsito";
    public static final String NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_NOME_FILE = "NomeFile";
    public static final String NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_FILE = "File";
	
	public static final String NOTIFICA_ESITO_INTEGRAZIONE_URLBASED_NOME_FILE  = "NomeFile";	
	public static final String NOTIFICA_ESITO_INTEGRAZIONE_TRASPORTO_NOME_FILE_1  = "SDI-NomeFile";
	public static final String NOTIFICA_ESITO_INTEGRAZIONE_TRASPORTO_NOME_FILE_2  = "GovWay-SDI-NomeFile";
	
	public static final String NOTIFICA_ESITO_INTEGRAZIONE_URLBASED_IDENTIFICATIVO_SDI  = "IdentificativoSdI";	
	public static final String NOTIFICA_ESITO_INTEGRAZIONE_TRASPORTO_IDENTIFICATIVO_SDI_1  = "SDI-IdentificativoSdI";
	public static final String NOTIFICA_ESITO_INTEGRAZIONE_TRASPORTO_IDENTIFICATIVO_SDI_2  = "GovWay-SDI-IdentificativoSdI";

}
