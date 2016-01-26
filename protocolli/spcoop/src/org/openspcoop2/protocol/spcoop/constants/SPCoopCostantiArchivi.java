/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.protocol.spcoop.constants;

import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchiveModeType;

import it.gov.spcoop.sica.dao.Costanti;


/**
 * SPCoopCostantiArchivi 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopCostantiArchivi {

	public static final ArchiveMode CNIPA_MODE = new ArchiveMode("cnipa");
	
	public static final ArchiveMode EXPORT_MODE_COMPATIBILITA_CLIENT_SICA = new ArchiveMode("cnipa (compatibile con Client SICA)");
	public static final ArchiveMode EXPORT_MODE_INFORMAZIONI_COMPLETE = new ArchiveMode("cnipa (informazioni complete)");
	
	public static final ArchiveModeType TYPE_ALL = new ArchiveModeType("*");
	public static final ArchiveModeType TYPE_APC = new ArchiveModeType("Accordo Servizio Parte Comune ("+Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_COMUNE+")");
	public static final ArchiveModeType TYPE_APS = new ArchiveModeType("Accordo Servizio Parte Specifica ("+Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA+")");
	public static final ArchiveModeType TYPE_ADC = new ArchiveModeType("Accordo Cooperazione ("+Costanti.ESTENSIONE_ACCORDO_COOPERAZIONE+")");
	public static final ArchiveModeType TYPE_ASC = new ArchiveModeType("Accordo Servizio Composto ("+Costanti.ESTENSIONE_ACCORDO_SERVIZIO_COMPOSTO+")");
	
	
}
