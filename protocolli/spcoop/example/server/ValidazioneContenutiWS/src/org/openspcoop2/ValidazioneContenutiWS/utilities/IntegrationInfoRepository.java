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


package org.openspcoop2.ValidazioneContenutiWS.utilities;

import java.util.Vector;

/**
*
* @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class IntegrationInfoRepository {

	private static Vector<IntegrationInfo> informazioniIntegrazione = new Vector<IntegrationInfo>();
	
	public static synchronized void put(IntegrationInfo info){
		IntegrationInfoRepository.informazioniIntegrazione.add(info);
	}
	
	public static synchronized IntegrationInfo removeNext(){
		return IntegrationInfoRepository.informazioniIntegrazione.remove(0);
	}

	public static synchronized void clear(){
		IntegrationInfoRepository.informazioniIntegrazione.clear();
	}
}
