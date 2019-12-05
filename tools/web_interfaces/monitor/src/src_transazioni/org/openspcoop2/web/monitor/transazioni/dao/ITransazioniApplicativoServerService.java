package org.openspcoop2.web.monitor.transazioni.dao;

import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneApplicativoServerBean;

public interface ITransazioniApplicativoServerService extends IService<TransazioneApplicativoServerBean, Long> { 

	public void setIdTransazione(String idTransazione);
	
	public void setProtocollo(String protocollo); 
	
	public TransazioneApplicativoServerBean findByServizioApplicativoErogatore(String nomeServizioApplicativoErogatore) throws Exception;
}
