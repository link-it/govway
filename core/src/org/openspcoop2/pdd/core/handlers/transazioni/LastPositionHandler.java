package org.openspcoop2.pdd.core.handlers.transazioni;

public class LastPositionHandler implements org.openspcoop2.pdd.core.handlers.PositionHandler {

	@Override
	public String getIdPosition() {
		return "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ"; 
	}

	@Override
	public boolean isHeadHandler() {
		return false;
	}

}
