package org.openspcoop2.web.monitor.transazioni.mbean;

import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.BlackListElement;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;


/*****
 * 
 * Bean per che incapsula un oggetto di tipo Traccia, per la visualizzazione nella pagina web.
 * 
 * @author pintori
 *
 */
public class TracciaBean extends Traccia {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private transient IProtocolFactory<?> protocolFactory;
	
	private BustaBean bustaBean;

	public TracciaBean(Traccia traccia) {
		// creo la protocol factory
		try {
			this.protocolFactory = ProtocolFactoryManager.getInstance()
					.getProtocolFactoryByName(traccia.getProtocollo());
		} catch (ProtocolException e) {
			TracciaBean.log.error("Errore durante la creazione della Factory", e);
		}

		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
		metodiEsclusi.add(new BlackListElement("setBusta", Busta.class));
		metodiEsclusi.add(new BlackListElement("setListaAllegati", List.class));
		metodiEsclusi.add(new BlackListElement("setProtocolFactory",
				IProtocolFactory.class));

		BeanUtils.copy(this, traccia, metodiEsclusi);

		this.setListaAllegati(traccia.getListaAllegati());
		// copia della busta
		this.bustaBean = new BustaBean(traccia.getBusta(), this.protocolFactory);

	}

	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}

	public void setProtocolFactory(IProtocolFactory<?> protocolFactory) {
		this.protocolFactory = protocolFactory;
	}
	
	public BustaBean getBustaBean(){
		return this.bustaBean;
	}
	
	public String getLocationLabel(){
		String in = "IN:";
		String out = "OUT:";
		if(this.getLocation() != null) {
			if(this.getLocation().startsWith(in)) {
				return "Indirizzo di provenienza della busta";
			} else if(this.getLocation().startsWith(out)) {
				return "Indirizzo a cui è stata spedita la busta";
			} else {
				return "Indirizzo provenienza/spedizione della busta";
			}
		}else {
			return "";
		}
	}

	public String getLocationValue(){
		String in = "IN:";
		String out = "OUT:";
		if(this.getLocation() != null) {
			if(this.getLocation().startsWith(in)) {
				return this.getLocation().substring(in.length());
			} else if(this.getLocation().startsWith(out)) {
				return this.getLocation().substring(out.length());
			} else {
				return this.getLocation();
			}
		}else {
			return "";
		}
	}
	
	
	
}
