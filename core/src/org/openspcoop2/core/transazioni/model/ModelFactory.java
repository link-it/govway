/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.core.transazioni.model;

/**     
 * Factory
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModelFactory {

	public static CredenzialeMittenteModel CREDENZIALE_MITTENTE = new CredenzialeMittenteModel();
	
	public static TransazioneModel TRANSAZIONE = new TransazioneModel();
	
	public static TransazioneInfoModel TRANSAZIONE_INFO = new TransazioneInfoModel();
	
	public static TransazioneExportModel TRANSAZIONE_EXPORT = new TransazioneExportModel();
	
	public static DumpMessaggioModel DUMP_MESSAGGIO = new DumpMessaggioModel();
	

}