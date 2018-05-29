package org.openspcoop2.pdd.core.credenziali.engine;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiConfigurationException;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiException;
import org.openspcoop2.pdd.core.credenziali.IGestoreCredenziali;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

public class GestoreCredenzialiPA implements IGestoreCredenziali {

	private GestoreCredenzialiEngine gestore = null;
	public GestoreCredenzialiPA(){
		this.gestore = new GestoreCredenzialiEngine(true);
	}
	
	@Override
	public void init(PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			Object... args) {
	}
	
	@Override
	public Credenziali elaborazioneCredenziali(
			InfoConnettoreIngresso infoConnettoreIngresso, OpenSPCoop2Message messaggio)
			throws GestoreCredenzialiException,
			GestoreCredenzialiConfigurationException {
		return this.gestore.elaborazioneCredenziali(infoConnettoreIngresso, messaggio);
	}

	@Override
	public String getIdentitaGestoreCredenziali() {
		return this.gestore.getIdentitaGestoreCredenziali();
	}


}
