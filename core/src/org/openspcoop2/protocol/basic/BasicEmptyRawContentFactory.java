package org.openspcoop2.protocol.basic;

import org.openspcoop2.protocol.basic.builder.BustaBuilder;
import org.openspcoop2.protocol.basic.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.sdk.ProtocolException;

public abstract class BasicEmptyRawContentFactory extends BasicFactory<BasicEmptyRawContent> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/* ** PROTOCOL BUILDER ** */
	
	@Override
	public org.openspcoop2.protocol.sdk.builder.IBustaBuilder<BasicEmptyRawContent> createBustaBuilder() throws ProtocolException {
		return new BustaBuilder<BasicEmptyRawContent>(this);
	}
	
	/* ** PROTOCOL VALIDATOR ** */
	
	@Override
	public org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica<BasicEmptyRawContent> createValidazioneSintattica() throws ProtocolException {
		return new ValidazioneSintattica<BasicEmptyRawContent>(this);
	}
}
