/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

	public final static String SDI_SERVIZIO_RICEVI_NOTIFICA = "RiceviNotifica";
    public final static String SDI_SERVIZIO_NOTIFICA_ESITO_AZIONE_NOTIFICA_ESITO = "NotificaEsito";
	 
	public final static String SDI_SERVIZIO_RICEVI_NOTIFICA_NAMESPACE = ProjectInfo.getInstance().getProjectNamespace();
	
	//public final static String SDI_SOAP_ACTION_SERVIZIO_NOTIFICA_ESITO_AZIONE_NOTIFICA_ESITO = "\"\"";
	public final static String SDI_SOAP_ACTION_SERVIZIO_NOTIFICA_ESITO_AZIONE_NOTIFICA_ESITO = "\"http://www.fatturapa.it/SdIRicezioneNotifiche/NotificaEsito\"";
	
	
	// AZIONE: NotificaEsito
	
	public final static String NOTIFICA_ESITO_RICHIESTA_ROOT_ELEMENT = "fileSdI";
	public final static String NOTIFICA_ESITO_RICHIESTA_ELEMENT_IDENTIFICATIVO_SDI = "IdentificativoSdI";
	public final static String NOTIFICA_ESITO_RICHIESTA_ELEMENT_NOME_FILE = "NomeFile";
	public final static String NOTIFICA_ESITO_RICHIESTA_ELEMENT_FILE = "File";
	
	public final static String NOTIFICA_ESITO_RISPOSTA_ROOT_ELEMENT = "rispostaSdINotificaEsito";
	public final static String NOTIFICA_ESITO_RISPOSTA_ELEMENT_ESITO = "Esito";
    public final static String NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO = "ScartoEsito";
    public final static String NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_NOME_FILE = "NomeFile";
    public final static String NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_FILE = "File";
	
	public final static String NOTIFICA_ESITO_INTEGRAZIONE_URLBASED_NOME_FILE  = "NomeFile";
	
	public final static String NOTIFICA_ESITO_INTEGRAZIONE_TRASPORTO_NOME_FILE_1  = "SDI-NomeFile";
	public final static String NOTIFICA_ESITO_INTEGRAZIONE_TRASPORTO_NOME_FILE_2  = "X-SDI-NomeFile";

}
