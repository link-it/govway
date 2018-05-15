package org.openspcoop2.web.monitor.transazioni.mbean;

import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.BlackListElement;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.ITraduttore;

/****
 * 
 * Bean per che incapsula un oggetto di tipo Eccezione, per la visualizzazione
 * nella pagina web.
 * 
 * 
 * @author pintori
 * 
 */
public class EccezioneBean extends Eccezione {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient IProtocolFactory<?> protocolFactory;
	private transient ITraduttore traduttore;

	public EccezioneBean(Eccezione eccezione, IProtocolFactory<?> protocolFactory) {
		this.protocolFactory = protocolFactory;

		try {
			this.traduttore = this.protocolFactory.createTraduttore();
		} catch (ProtocolException e) {
		}

		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
		metodiEsclusi.add(new BlackListElement("setProtocolFactory",
				IProtocolFactory.class));
		metodiEsclusi.add(new BlackListElement("setCodiceEccezioneValue",
				String.class));
		metodiEsclusi.add(new BlackListElement("setContestoCodificaValue",
				String.class));
		metodiEsclusi.add(new BlackListElement("setRilevanzaValue",
				String.class));
		metodiEsclusi.add(new BlackListElement("setDescrizione", String.class));

		BeanUtils.copy(this, eccezione, metodiEsclusi);
		
		this.setCodiceEccezione(eccezione.getCodiceEccezione());
		this.setContestoCodifica(eccezione.getContestoCodifica());
		this.setRilevanza(eccezione.getRilevanza());

		
		try {
			if(this.getCodiceEccezione() != null)
				this.setCodiceEccezioneValue(eccezione.getCodiceEccezioneValue(this.protocolFactory));
			
			if(this.getContestoCodifica() != null)
				this.setContestoCodificaValue(eccezione.getContestoCodificaValue(this.protocolFactory));
			
			if(this.getRilevanza() != null)
				this.setRilevanzaValue(eccezione.getRilevanzaValue(this.protocolFactory));

			this.setDescrizione(eccezione.getDescrizione(this.protocolFactory));
			
		} catch (ProtocolException e) {
			//Log?
		}
		
	}

	public String getCodiceEccezioneRicavato() {
		if(this.getCodiceEccezioneValue()!=null){
			return this.getCodiceEccezioneValue();
		} else if(this.getCodiceEccezione()!=null){
			return this.getCodiceEccezione().name();
		} else 
			return null;
	}

	public String getContestoCodificaRicavato() {
		if(this.getContestoCodificaValue()!=null){
			return this.getContestoCodificaValue();
		} else if(this.getContestoCodifica()!=null){
			return this.getContestoCodifica().getEngineValue();
		} else 
			return null;
	}

	public String getRilevanzaRicavato() {
		if(this.getRilevanzaValue()!=null){
			return this.getRilevanzaValue();
		} else if(this.getRilevanza()!=null){
			return this.getRilevanza().getEngineValue();
		} else 
			return null;
	}

	public String getCodiceEccezioneValue() {
		if (this.traduttore != null) {
			try{
				return super.getCodiceEccezioneValue(this.protocolFactory) == null ? this.traduttore
						.toString(this.getCodiceEccezione())
						: super.getCodiceEccezioneValue(this.protocolFactory);
			}catch(Exception e){
				if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null){
					this.protocolFactory.getLogger().error(e.getMessage(),e);
				}else{
					e.printStackTrace(System.err);
				}
				return null;
			}
		}
		return null;
	}

	public String getContestoCodificaValue() {
		if (this.traduttore != null) {
			try{
				return super.getContestoCodificaValue(this.protocolFactory) == null ? this.traduttore
						.toString(this.getContestoCodifica())
						: super.getContestoCodificaValue(this.protocolFactory);
			}catch(Exception e){
				if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null){
					this.protocolFactory.getLogger().error(e.getMessage(),e);
				}else{
					e.printStackTrace(System.err);
				}
				return null;
			}
		}
		return null;
	}

	public String getRilevanzaValue() {
		if (this.traduttore != null) {
			try{
				return super.getRilevanzaValue(this.protocolFactory) == null ? this.traduttore.toString(this
						.getRilevanza()) : super.getRilevanzaValue(this.protocolFactory);
			}catch(Exception e){
				if(this.protocolFactory!=null && this.protocolFactory.getLogger()!=null){
					this.protocolFactory.getLogger().error(e.getMessage(),e);
				}else{
					e.printStackTrace(System.err);
				}
				return null;
			}
		}
		return null;
	}

	public String getDescrizioneValue() {
		if (this.protocolFactory != null) {
			try {
				return this.getDescrizione(this.protocolFactory);
			} catch (ProtocolException e) {
				return null;
			} catch (NullPointerException e) {
				return null;
			}
		}

		return null;
	}

}
