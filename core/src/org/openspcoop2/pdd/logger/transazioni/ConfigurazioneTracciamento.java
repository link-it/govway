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

package org.openspcoop2.pdd.logger.transazioni;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.ConfigurazioneTracciamentoPorta;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaTracciamento;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.TracciamentoConfigurazioneFiletrace;
import org.openspcoop2.core.config.Transazioni;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.utils.TracciamentoCompatibilitaFiltroEsiti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.EsitiConfigUtils;
import org.slf4j.Logger;

/**
 * ConfigurazioneTracciamento
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConfigurazioneTracciamento {

	private Logger log;
	
	private TracciamentoCompatibilitaFiltroEsiti regole;

	private boolean dbEnabled = false;
		
	private TracciamentoConfigurazioneFiletrace configurazioneFileTrace;
	private boolean fileTraceEnabled = false;
	private File fileTraceConfig = null;
	private boolean fileTraceConfigGlobal = true;
	
	private String esitiConfig;
	private List<String> esitiDaRegistrare;
	
	private Transazioni informazioniSalvareTransazioni;
	public Transazioni getInformazioniSalvareTransazioni() {
		return this.informazioniSalvareTransazioni;
	}
	
	private OpenSPCoop2Properties op2Properties;
	
	private ConfigurazionePdDManager configPdDManager;
	private boolean isConfig=false;
	private boolean isPD=false;
	private PortaDelegata pd;
	private boolean isPA=false;
	private PortaApplicativa pa;
	
	public ConfigurazioneTracciamento(Logger log, ConfigurazionePdDManager configPdDManager, PortaDelegata pd) throws DriverConfigurazioneException, CoreException {
		this.pd = pd;
		this.isPD = true;
		this.init(
				log, configPdDManager,
				(pd!=null ? pd.getTracciamento() : null));
	}
	public ConfigurazioneTracciamento(Logger log, ConfigurazionePdDManager configPdDManager, PortaApplicativa pa) throws DriverConfigurazioneException, CoreException {
		this.pa = pa;
		this.isPA = true;
		this.init(
				log, configPdDManager,
				(pa!=null ? pa.getTracciamento() : null));
	}
	public ConfigurazioneTracciamento(Logger log, ConfigurazionePdDManager configPdDManager, TipoPdD tipoPdD) throws DriverConfigurazioneException, CoreException {
		this.isConfig = true;
		if(tipoPdD==null) {
			throw new CoreException("TipoPdD non indicato");
		}
		switch (tipoPdD) {
		case DELEGATA:
			this.isPD = true;
			break;
		case APPLICATIVA:
			this.isPA = true;
			break;
		default:
			break;
		}
		this.init(
				log, configPdDManager,
				null);
	}
	private void init(Logger log, ConfigurazionePdDManager configPdDManager, PortaTracciamento porta) throws DriverConfigurazioneException, CoreException {
		
		this.log = log;
		this.configPdDManager = configPdDManager;
		this.op2Properties = OpenSPCoop2Properties.getInstance();
		
		Tracciamento configTracciamento = this.configPdDManager.getOpenSPCoopAppenderTracciamento();
		ConfigurazioneTracciamentoPorta config = null;
		if(this.isPA) {
			config = configTracciamento.getPortaApplicativa();
		}
		if(this.isPD) {
			config = configTracciamento.getPortaDelegata();
		} 
		
		if(porta!=null && StatoFunzionalita.ABILITATO.equals(porta.getStato())) {
			// ridefinito
			this.regole = new TracciamentoCompatibilitaFiltroEsiti(porta.getDatabase(), porta.getFiletrace());
			this.configurazioneFileTrace = porta.getFiletraceConfig();
			this.esitiConfig = porta.getEsiti();
			this.informazioniSalvareTransazioni = porta.getTransazioni();
		}
		else {
			this.regole = new TracciamentoCompatibilitaFiltroEsiti(config!=null ? config.getDatabase() : null, 
					config!=null ? config.getFiletrace() : null);
			if(config!=null) {
				this.configurazioneFileTrace = config.getFiletraceConfig();
				this.esitiConfig = config.getEsiti();
				this.informazioniSalvareTransazioni = config.getTransazioni();
			}
		}
		
		this.initDB();
		
		this.initFileTrace();
	}
	
	
	public TracciamentoCompatibilitaFiltroEsiti getRegole() {
		return this.regole;
	}
	

	private void initDB() {
		this.dbEnabled = this.regole.isTracciamentoDBEnabled();
	}
	public boolean isDbEnabled() {
		return this.dbEnabled;
	}
	
	
	
	private void initFileTrace() throws DriverConfigurazioneException, CoreException {
		if(this.regole.isTracciamentoFileTraceEnabled()) {
			if(this.regole.isTracciamentoFileTraceEnabledByExternalProperties()){
				this.initFileTraceExternalConfig();
			}
			else {
				this.fileTraceEnabled = true;
				initFileTraceInternalConfig();
			}
		}
		else {
			this.fileTraceEnabled = false;
			this.fileTraceConfig = null;
		}
	}
	private void initFileTraceExternalConfig() throws DriverConfigurazioneException, CoreException {
		this.fileTraceEnabled = this.op2Properties.isTransazioniFileTraceEnabled();
		if(this.fileTraceEnabled) {
			this.fileTraceConfig = this.op2Properties.getTransazioniFileTraceConfig();
		}
		
		if(!this.isConfig) {
			initFileTraceExternalConfigPorta();
		}
	}
	private void initFileTraceExternalConfigPorta() throws DriverConfigurazioneException, CoreException {
		if(this.isPA && this.pa!=null) {
			this.fileTraceEnabled = this.configPdDManager.isTransazioniFileTraceEnabled(this.pa);
			if(this.fileTraceEnabled) {
				this.fileTraceConfig = this.configPdDManager.getFileTraceConfig(this.pa);
				this.fileTraceConfigGlobal = this.op2Properties.isTransazioniFileTraceEnabled() && 
						this.op2Properties.getTransazioniFileTraceConfig().getAbsolutePath().equals(this.fileTraceConfig.getAbsolutePath());
			}
			else {
				this.fileTraceConfig = null;
			}
		}
		else if(this.isPD && this.pd!=null) {
			this.fileTraceEnabled = this.configPdDManager.isTransazioniFileTraceEnabled(this.pd);
			if(this.fileTraceEnabled) {
				this.fileTraceConfig = this.configPdDManager.getFileTraceConfig(this.pd);
				this.fileTraceConfigGlobal = this.op2Properties.isTransazioniFileTraceEnabled() && 
						this.op2Properties.getTransazioniFileTraceConfig().getAbsolutePath().equals(this.fileTraceConfig.getAbsolutePath());
			}
			else {
				this.fileTraceConfig = null;
			}
		}
	}
	private void initFileTraceInternalConfig() throws DriverConfigurazioneException, CoreException {
		if(this.configurazioneFileTrace!=null && this.configurazioneFileTrace.getConfig()!=null && StringUtils.isNotEmpty(this.configurazioneFileTrace.getConfig())) {
			this.fileTraceConfig = ConfigurazioneTracciamento.toFileTraceConfig(this.configurazioneFileTrace.getConfig(), this.op2Properties.getRootDirectory());
			this.fileTraceConfigGlobal = this.op2Properties.isTransazioniFileTraceEnabled() && 
					this.op2Properties.getTransazioniFileTraceConfig().getAbsolutePath().equals(this.fileTraceConfig.getAbsolutePath());
		}
		else {
			// utilizzo la configurazione di default
			this.fileTraceConfig = this.op2Properties.getTransazioniFileTraceConfig();
			if(!this.isConfig) {
				if(this.isPA && this.pa!=null) {
					this.fileTraceConfig = this.configPdDManager.getFileTraceConfig(this.pa);
					this.fileTraceConfigGlobal = this.op2Properties.isTransazioniFileTraceEnabled() && 
							this.op2Properties.getTransazioniFileTraceConfig().getAbsolutePath().equals(this.fileTraceConfig.getAbsolutePath());
				}
				else if(this.isPD && this.pd!=null) {
					this.fileTraceConfig = this.configPdDManager.getFileTraceConfig(this.pd);
					this.fileTraceConfigGlobal = this.op2Properties.isTransazioniFileTraceEnabled() && 
							this.op2Properties.getTransazioniFileTraceConfig().getAbsolutePath().equals(this.fileTraceConfig.getAbsolutePath());
				}
			}
		}
	}
	public boolean isFileTraceEnabled() {
		return this.fileTraceEnabled;
	}
	public File getFileTraceConfig() {
		return this.fileTraceConfig;
	}
	public boolean isFileTraceConfigGlobal() {
		return this.fileTraceConfigGlobal;
	}
	public static File toFileTraceConfig(String name, String rootDir) throws CoreException {
		File getTransazioniFileTraceConfig = new File(name);
		if(!getTransazioniFileTraceConfig.exists() &&
			rootDir!=null && !"".equals(rootDir)) {
			getTransazioniFileTraceConfig = new File(rootDir, name);
		}
		
		if(!getTransazioniFileTraceConfig.exists()) {
			throw ConfigurazioneTracciamentoUtils.newCoreExceptionNotExists(getTransazioniFileTraceConfig, false);
		}
		if(getTransazioniFileTraceConfig.isDirectory()) {
			throw ConfigurazioneTracciamentoUtils.newCoreExceptionNotFile(getTransazioniFileTraceConfig, false);
		}
		if(!getTransazioniFileTraceConfig.canRead()) {
			throw ConfigurazioneTracciamentoUtils.newCoreExceptionCannotRead(getTransazioniFileTraceConfig, false);
		}
		return getTransazioniFileTraceConfig;
	}
	
	public List<String> getEsitiDaRegistrare(StringBuilder bf) throws ProtocolException{
		if(this.regole.isFilterEnabled()) {
			if(this.esitiDaRegistrare==null) {
				this.esitiDaRegistrare = EsitiConfigUtils.getRegistrazioneEsiti(this.esitiConfig, this.log, bf);
			}
		}
		else {
			this.esitiDaRegistrare = null;
		}
		return this.esitiDaRegistrare;
	}
	public String getTipoFiltroEsiti() {
		StringBuilder sb = new StringBuilder();
		if(this.regole.isFilterDBEnabled()) {
			sb.append("db");
		}
		if(this.regole.isFilterFileTraceEnabled()) {
			if(sb.length()>0) {
				sb.append(",");
			}
			sb.append("db");
		}
		return sb.toString();
	}
	public boolean isFiltroEsitiDB() {
		return this.regole.isFilterDBEnabled();
	}
	public boolean isFiltroEsitiFileTrace() {
		return this.regole.isFilterFileTraceEnabled();
	}
	
}
