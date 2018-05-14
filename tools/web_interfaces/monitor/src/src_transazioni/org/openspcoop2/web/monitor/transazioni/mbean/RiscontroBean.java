package org.openspcoop2.web.monitor.transazioni.mbean;

import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.BlackListElement;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.config.ITraduttore;

/****
 * 
 * Bean per che incapsula un oggetto di tipo Riscontro, per la visualizzazione nella pagina web.
 * 
 * @author pintori
 *
 */
public class RiscontroBean extends Riscontro {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient IProtocolFactory protocolFactory;
	private transient ITraduttore traduttore;

	public RiscontroBean(Riscontro riscontro, IProtocolFactory protocolFactory) {

		this.protocolFactory = protocolFactory;

		try {
			this.traduttore = this.protocolFactory.createTraduttore();
		} catch (ProtocolException e) {
		}

		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
		metodiEsclusi.add(new BlackListElement("setProtocolFactory",
				IProtocolFactory.class));
		metodiEsclusi.add(new BlackListElement("setTipoOraRegistrazioneValue",
				String.class));

		BeanUtils.copy(this, riscontro, metodiEsclusi);
		try {
			this.setTipoOraRegistrazioneValue(riscontro.getTipoOraRegistrazioneValue(protocolFactory));
		} catch (ProtocolException e) {
		}

	}

	public String getTipoOraRegistrazioneValue() {
		try{
			return super.getTipoOraRegistrazioneValue(this.protocolFactory) == null ? this.traduttore
					.toString(this.getTipoOraRegistrazione())
					: super.getTipoOraRegistrazioneValue(this.protocolFactory);
		}catch(Exception e){
			if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null){
				this.protocolFactory.getLogger().error(e.getMessage(),e);
			}else{
				e.printStackTrace(System.err);
			}
			return null;
		}
	}
	
	public String getTipoOraRegistrazioneRicavato() {
		if(this.getTipoOraRegistrazioneValue()!=null){
			return this.getTipoOraRegistrazioneValue();
		} else if(this.getTipoOraRegistrazione()!=null){
			return this.getTipoOraRegistrazione().getEngineValue();
		} else 
			return null;
	}



}
