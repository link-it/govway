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
package org.openspcoop2.protocol.sdi.constants;

import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.utils.ProjectInfo;

/**
 * SDICostantiServizioRiceviFile
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDICostantiServizioRiceviFile {

    public static final String SDI_SERVIZIO_RICEVI_FILE = "SdIRiceviFile";
    public static final String SDI_SERVIZIO_RICEVI_FILE_AZIONE_RICEVI_FILE = "RiceviFile";
	
	public static final String SDI_SERVIZIO_RICEVI_FILE_NAMESPACE = ProjectInfo.getInstance().getProjectNamespace();
	
	//public static final String SDI_SOAP_ACTION_SERVIZIO_RICEVI_FILE_AZIONE_RICEVI_FILE = "\"\"";
	public static final String SDI_SOAP_ACTION_SERVIZIO_RICEVI_FILE_AZIONE_RICEVI_FILE = "\"http://www.fatturapa.it/SdIRiceviFile/RiceviFile\"";
	
	
	// AZIONE: RiceviFile
	
	public static final String RICEVI_FILE_RICHIESTA_ROOT_ELEMENT = "fileSdIAccoglienza";
	
    public static final String FILE_SDI_TYPE_ELEMENT_FILE = "File";
	
	public static final String RICEVI_FILE_RISPOSTA_ROOT_ELEMENT = "rispostaSdIRiceviFile";
    public static final String RICEVI_FILE_RISPOSTA_ELEMENT_IDENTIFICATIVO_SDI = "IdentificativoSdI";
    public static final String RICEVI_FILE_RISPOSTA_ELEMENT_DATA_ORA_RICEZIONE = "DataOraRicezione";
    public static final String RICEVI_FILE_RISPOSTA_ELEMENT_ERRORE = "Errore";
	
	public static final String RICEVI_FILE_INTEGRAZIONE_URLBASED_VERSIONE_FATTURA  = "Versione";
	public static final String RICEVI_FILE_INTEGRAZIONE_TRASPORTO_VERSIONE_FATTURA_1  = "SDI-Versione";
	public static final String RICEVI_FILE_INTEGRAZIONE_TRASPORTO_VERSIONE_FATTURA_2  = "GovWay-SDI-Versione";
	
	public static final String RICEVI_FILE_INTEGRAZIONE_URLBASED_TIPO_FILE  = "TipoFile";
	public static final String RICEVI_FILE_INTEGRAZIONE_TRASPORTO_TIPO_FILE_1  = "SDI-TipoFile";
	public static final String RICEVI_FILE_INTEGRAZIONE_TRASPORTO_TIPO_FILE_2  = "GovWay-SDI-TipoFile";
	
	public static final String RICEVI_FILE_INTEGRAZIONE_URLBASED_ID_PAESE  = "IdPaese";
	public static final String RICEVI_FILE_INTEGRAZIONE_TRASPORTO_ID_PAESE_1  = "SDI-IdPaese";
	public static final String RICEVI_FILE_INTEGRAZIONE_TRASPORTO_ID_PAESE_2  = "GovWay-SDI-IdPaese";
	
	public static final String RICEVI_FILE_INTEGRAZIONE_URLBASED_ID_CODICE  = "IdCodice";
	public static final String RICEVI_FILE_INTEGRAZIONE_TRASPORTO_ID_CODICE_1  = "SDI-IdCodice";
	public static final String RICEVI_FILE_INTEGRAZIONE_TRASPORTO_ID_CODICE_2  = "GovWay-SDI-IdCodice";
	
	public static final String RICEVI_FILE_INTEGRAZIONE_URLBASED_NOME_FILE  = "NomeFile";
	public static final String RICEVI_FILE_INTEGRAZIONE_TRASPORTO_NOME_FILE_1  = "SDI-NomeFile";
	public static final String RICEVI_FILE_INTEGRAZIONE_TRASPORTO_NOME_FILE_2  = "GovWay-SDI-NomeFile";

	
}
