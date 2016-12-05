package org.openspcoop2.protocol.basic;

import org.openspcoop2.protocol.registry.CachedRegistryReader;
import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.slf4j.Logger;

public class BasicComponentFactory implements IComponentFactory {

	protected Logger log;
	protected IProtocolFactory<?> protocolFactory;
	protected IRegistryReader cachedRegistryReader;
	
	public BasicComponentFactory(IProtocolFactory<?> protocolFactory) throws ProtocolException{
		try{
			this.protocolFactory = protocolFactory;
			this.log = this.protocolFactory.getLogger();
			this.cachedRegistryReader = new CachedRegistryReader(this.log, protocolFactory);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public Logger getLog() {
		return this.log;
	}
	
	@Override
	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}
	
}
