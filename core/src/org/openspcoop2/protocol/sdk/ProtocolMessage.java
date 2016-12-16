package org.openspcoop2.protocol.sdk;

import org.openspcoop2.message.OpenSPCoop2Message;

public class ProtocolMessage {

	private BustaRawContent<?> bustaRawContent;
	private OpenSPCoop2Message message;
	
	public BustaRawContent<?> getBustaRawContent() {
		return this.bustaRawContent;
	}
	public void setBustaRawContent(BustaRawContent<?> bustaRawContent) {
		this.bustaRawContent = bustaRawContent;
	}
	public OpenSPCoop2Message getMessage() {
		return this.message;
	}
	public void setMessage(OpenSPCoop2Message message) {
		this.message = message;
	}
}
