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

package org.openspcoop2.web.monitor.transazioni.dao;

import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneApplicativoServerBean;


/**
 * ITransazioniApplicativoServerService
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ITransazioniApplicativoServerService extends IService<TransazioneApplicativoServerBean, Long> { 

	public void setIdTransazione(String idTransazione);
	
	public void setProtocollo(String protocollo); 
	
	public TransazioneApplicativoServerBean findByServizioApplicativoErogatore(String nomeServizioApplicativoErogatore) throws Exception;
}
