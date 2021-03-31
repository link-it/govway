/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.logger.filetrace;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;

/**
 * Classe che raccoglie le proprieta
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class CostantiProprieta {

	public static final String FILE_TRACE_VALUE_ENABLED = "true";
	public static final String FILE_TRACE_VALUE_DISABLED = "false";
	
	private static final String FILE_TRACE_ENABLED = "fileTrace.enabled";
	private static final String FILE_TRACE_DUMP_BINARIO_ENABLED = "fileTrace.dumpBinario.enabled";
	private static final String FILE_TRACE_DUMP_BINARIO_CONNETTORE_ENABLED = "fileTrace.dumpBinario.connettore.enabled";
	
	private static final String FILE_TRACE_CONFIG = "fileTrace.config";
	
	public static boolean isFileTraceEnabled(List<Proprieta> proprieta, boolean defaultValue) throws Exception {
		return readBooleanValueWithDefault(proprieta, FILE_TRACE_ENABLED, defaultValue);
	}
	public static boolean isFileTraceDumpBinarioEnabled(List<Proprieta> proprieta, boolean defaultValue) throws Exception {
		return readBooleanValueWithDefault(proprieta, FILE_TRACE_DUMP_BINARIO_ENABLED, defaultValue);
	}
	public static boolean isFileTraceDumpBinarioConnettoreEnabled(List<Proprieta> proprieta, boolean defaultValue) throws Exception {
		return readBooleanValueWithDefault(proprieta, FILE_TRACE_DUMP_BINARIO_CONNETTORE_ENABLED, defaultValue);
	}
	
	public static File getFileTraceConfig(List<Proprieta> proprieta, File defaultValue) throws Exception {
		String v = readValue(proprieta, FILE_TRACE_CONFIG);
		if(v==null || StringUtils.isEmpty(v)) {
			return defaultValue;
		}
		
		File getTransazioniFileTraceConfig = new File(v);
		if(!getTransazioniFileTraceConfig.exists()) {
			String rootDir = OpenSPCoop2Properties.getInstance().getRootDirectory();
			if(rootDir!=null && !"".equals(rootDir)) {
				getTransazioniFileTraceConfig = new File(rootDir, v);
			}
		}
		
		if(!getTransazioniFileTraceConfig.exists()) {
			throw new Exception("Config file ["+getTransazioniFileTraceConfig.getAbsolutePath()+"] not exists");
		}
		if(getTransazioniFileTraceConfig.isDirectory()) {
			throw new Exception("Config file ["+getTransazioniFileTraceConfig.getAbsolutePath()+"] is directory");
		}
		if(getTransazioniFileTraceConfig.canRead()==false) {
			throw new Exception("Config file ["+getTransazioniFileTraceConfig.getAbsolutePath()+"] cannot read");
		}
		
		return getTransazioniFileTraceConfig;
	}
	
	private static String readValue(List<Proprieta> proprieta, String nome) {
		if(proprieta==null || proprieta.isEmpty()) {
			return null;
		}
		for (Proprieta proprietaCheck : proprieta) {
			if(nome.equalsIgnoreCase(proprietaCheck.getNome())) {
				return proprietaCheck.getValore()!=null ? proprietaCheck.getValore().trim() : null;
			}
		}
		return null;
	}
	private static boolean readBooleanValueWithDefault(List<Proprieta> proprieta, String nome, boolean defaultValue) {
		String valueS = readValue(proprieta, nome);
		if(valueS!=null && !StringUtils.isEmpty(valueS)) {
			if(CostantiProprieta.FILE_TRACE_VALUE_ENABLED.equals(valueS.trim())) {
				return true;
			}
			else if(CostantiProprieta.FILE_TRACE_VALUE_DISABLED.equals(valueS.trim())) {
				return false;
			}
		}
		return defaultValue;
	}
}
