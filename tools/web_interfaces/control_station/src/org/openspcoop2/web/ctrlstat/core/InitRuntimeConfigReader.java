/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openspcoop2.web.ctrlstat.core;

import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntimeInit;
import org.openspcoop2.pdd.config.InvokerNodiRuntime;
import org.openspcoop2.pdd.logger.filetrace.FileTraceGovWayState;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;

/**
 * InitRuntimeConfigReader
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class InitRuntimeConfigReader extends ConfigurazioneNodiRuntimeInit {

	private ConsoleProperties consoleProperties;
		
	public InitRuntimeConfigReader(ConsoleProperties consoleProperties, boolean reInitSecretMaps) throws UtilsException {
		super(InitListener.getLog(), consoleProperties.getConfigurazioneNodiRuntime(), 
				reInitSecretMaps, 
				consoleProperties.getBYOKEnvSecretsConfig(), consoleProperties.isBYOKEnvSecretsConfigRequired(),
				BYOKManager.getSecurityEngineGovWayInstance(), BYOKManager.getSecurityRemoteEngineGovWayInstance());
		this.consoleProperties = consoleProperties;
	}
	
	@Override
	protected boolean isCompleted(String alias) {
		
		boolean finished = false;
		
		InvokerNodiRuntime invoker = new InvokerNodiRuntime(InitListener.log, this.configurazioneNodiRuntime);
		analizeFileTraceGovWayState(invoker, alias, this.consoleProperties);
		if(InitListener.getFileTraceGovWayState()!=null) {
			finished = true;
		}
		
		return finished;
	}

	private void analizeFileTraceGovWayState(InvokerNodiRuntime invoker, String alias, ConsoleProperties consoleProperties) {
		try{
			String tmp = invoker.invokeJMXMethod(alias, consoleProperties.getJmxPdDConfigurazioneSistemaType(alias),
					consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoGetFileTrace(alias));
			InitListener.setFileTraceGovWayState(FileTraceGovWayState.toConfig(tmp,true));	
		} catch (Exception e) {
			InitListener.logDebug(e.getMessage(),e);
			// provo su tutti i nodi, non voglio mettere un vincolo che serve per forza un nodo acceso
		}
	}
	
	@Override
	protected String getDescrizione() {
		return "secrets,filetrace";
	}
}
