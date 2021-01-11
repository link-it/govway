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
package org.openspcoop2.protocol.sdi.constants;

import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.utils.ProjectInfo;

/**
 * SDICostantiServizioRicezioneFatture
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDICostantiServizioRicezioneFatture {

    public final static String RICEZIONE_SERVIZIO_RICEZIONE_FATTURE = "RicezioneFatture";
    public final static String RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_AZIONE_RICEVI_FATTURE = "RiceviFatture";
    public final static String RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_AZIONE_NOTIFICA_DECORRENZA_TERMINI = "NotificaDecorrenzaTermini";
	
	public final static String RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_NAMESPACE = ProjectInfo.getInstance().getProjectNamespace();
	
	
	// AZIONE: RiceviFatture
    
	public final static String RICEVI_FATTURE_RICHIESTA_ROOT_ELEMENT = "fileSdIConMetadati";
    
    public final static String RICEVI_FATTURE_RICHIESTA_ELEMENT_IDENTIFICATIVO_SDI = "IdentificativoSdI";
    public final static String RICEVI_FATTURE_RICHIESTA_ELEMENT_NOME_FILE = "NomeFile";
    public final static String RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE = "File";
    public final static String RICEVI_FATTURE_RICHIESTA_ELEMENT_NOME_FILE_METADATI = "NomeFileMetadati";
    public final static String RICEVI_FATTURE_RICHIESTA_ELEMENT_METADATI = "Metadati";
    
    
    // AZIONE: NotificaDecorrenzaTermini
    
 	public final static String NOTIFICA_DECORRENZA_TERMINI_RICHIESTA_ROOT_ELEMENT = "fileSdI";
 	
 	
 	// COMMON fileSdI_Type
 	
     public final static String FILE_SDI_TYPE_RICHIESTA_ELEMENT_IDENTIFICATIVO_SDI = "IdentificativoSdI";
     public final static String FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_NOME_FILE = "NomeFile";
     public final static String FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE = "File";
	
}
