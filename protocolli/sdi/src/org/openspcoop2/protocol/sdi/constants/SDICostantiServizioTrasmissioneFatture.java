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

import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.utils.ProjectInfo;

/**
 * SDICostantiServizioTrasmissioneFatture
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDICostantiServizioTrasmissioneFatture {

    public final static String TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE = "TrasmissioneFatture";
    public final static String TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_RICEVUTA_CONSEGNA = "RicevutaConsegna";
    public final static String TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_MANCATA_CONSEGNA = "NotificaMancataConsegna";
    public final static String TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_SCARTO = "NotificaScarto";
    public final static String TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_ESITO = "NotificaEsito";
    public final static String TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_DECORRENZA_TERMINI = "NotificaDecorrenzaTermini";
    public final static String TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_ATTESTAZIONE_TRASMISSIONE_FATTURA = "AttestazioneTrasmissioneFattura";
	
	public final static String TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_NAMESPACE = ProjectInfo.getInstance().getProjectNamespace();
	
	
	// AZIONE: RicevutaConsegna
    
	public final static String RICEVUTA_CONSEGNA_RICHIESTA_ROOT_ELEMENT = "ricevutaConsegna";

	// AZIONE: NotificaMancataConsegna
    
	public final static String NOTIFICA_MANCATA_CONSEGNA_RICHIESTA_ROOT_ELEMENT = "notificaMancataConsegna";

	// AZIONE: NotificaScarto
    
	public final static String NOTIFICA_SCARTO_RICHIESTA_ROOT_ELEMENT = "notificaScarto";
	
	// AZIONE: NotificaEsito
    
	public final static String NOTIFICA_ESITO_RICHIESTA_ROOT_ELEMENT = "notificaEsito";
	
	// AZIONE: NotificaDecorrenzaTermini
    
	public final static String NOTIFICA_DECORRENZA_TERMINI_RICHIESTA_ROOT_ELEMENT = "notificaDecorrenzaTermini";
	
	// AZIONE: AttestazioneTrasmissioneFattura
    
	public final static String ATTESTAZIONE_TRASMISSIONE_FATTURA_RICHIESTA_ROOT_ELEMENT = "attestazioneTrasmissioneFattura";
	
	
	// COMMON fileSdI_Type
	
    public final static String FILE_SDI_TYPE_RICHIESTA_ELEMENT_IDENTIFICATIVO_SDI = "IdentificativoSdI";
    public final static String FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_NOME_FILE = "NomeFile";
    public final static String FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE = "File";

	
	
}
