package org.openspcoop2.web.ctrlstat.servlet.config;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.RegistroPlugins;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.monitor.engine.dynamic.IRegistroPluginsReader;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;

public class ConfigurazioneRegistroPluginsReader implements IRegistroPluginsReader{
	
	private ControlStationCore core;

	public ConfigurazioneRegistroPluginsReader(ControlStationCore core) {
		this.core = core;
	}
	
	@Override
	public RegistroPlugins getRegistroPlugins() throws NotFoundException, CoreException {
		try {
			ConfigurazioneCore confCore = new ConfigurazioneCore(this.core);
			
			return confCore.getRegistroPlugins();
		} catch (Exception e) {
			throw new CoreException(e);
		}
	}

}
