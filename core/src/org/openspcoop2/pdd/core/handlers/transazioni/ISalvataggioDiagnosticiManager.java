/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
import org.openspcoop2.pdd.core.handlers.IntegrationManagerResponseContext;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.slf4j.Logger;

/**     
 * ISalvataggioDiagnosticiManager
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ISalvataggioDiagnosticiManager {

	public StatoSalvataggioDiagnostici getInformazioniSalvataggioDiagnostici(Logger log,
			PostOutResponseContext context,
			Transaction transaction,
			Transazione transazioneDTO,
			boolean pddStateless) throws Exception;
	
	public StatoSalvataggioDiagnostici getInformazioniSalvataggioDiagnostici(Logger log,
			IntegrationManagerResponseContext context,
			Transaction transaction,
			Transazione transazioneDTO,
			boolean pddStateless) throws Exception;

}
