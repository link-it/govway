package org.openspcoop2.pdd.core.handlers.transazioni;

public class FirstPositionHandler implements org.openspcoop2.pdd.core.handlers.PositionHandler {

	@Override
	public String getIdPosition() {
		return "0"; // i numeri vengono ordinati prima delle lettere
	}

	@Override
	public boolean isHeadHandler() {
		return true;
	}

}
