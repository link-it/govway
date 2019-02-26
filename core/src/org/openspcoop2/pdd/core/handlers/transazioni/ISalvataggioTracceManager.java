/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.pdd.core.handlers.transazioni;

import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.slf4j.Logger;

/**     
 * ISalvataggioTracceManager
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ISalvataggioTracceManager {

	public StatoSalvataggioTracce getInformazioniSalvataggioTracciaRichiesta(Logger log,
			PostOutResponseContext context,
			Transaction transaction,
			Transazione transazioneDTO,
			boolean pddStateless) throws Exception;
	
	public StatoSalvataggioTracce getInformazioniSalvataggioTracciaRisposta(Logger log,
			PostOutResponseContext context,
			Transaction transaction,
			Transazione transazioneDTO,
			boolean pddStateless) throws Exception;
	
}
