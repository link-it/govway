/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.core.batch;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.SystemDate;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
* Generator
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Generator {

	public static void main(String[] args) throws Exception {

		StringBuilder bf = new StringBuilder();
		String [] tipi = TipoRuntimeRepository.toStringArray();
		if(tipi!=null) {
			for (String t : tipi) {
				if(bf.length()>0) {
					bf.append(",");
				}
				bf.append(t);
			}
		}
		String usage = "\n\nUse: generator.sh tipo\n\ttipo: "+bf.toString();
		
		if(args.length<=0) {
			throw new Exception("ERROR: tipo gestore non fornito"+usage);
		}
		
		String tipo = args[0].trim();
		TipoRuntimeRepository tipoGestoreArg = null;
		try {
			tipoGestoreArg = TipoRuntimeRepository.toEnumConstant(tipo, true);
		}catch(Exception e) {
			throw new Exception("ERROR: tipo gestore fornito ("+tipo+") sconosciuto"+usage);
		}

		List<TipoRuntimeRepository> listTipiDaProcessare = new ArrayList<TipoRuntimeRepository>();
		if(TipoRuntimeRepository.ALL.equals(tipoGestoreArg)) {
			listTipiDaProcessare.add(TipoRuntimeRepository.MESSAGGI);
			listTipiDaProcessare.add(TipoRuntimeRepository.BUSTE);
		}
		else {
			listTipiDaProcessare.add(tipoGestoreArg);
		}
		
		try{
			Properties props = new Properties();
			FileInputStream fis = new FileInputStream(Generator.class.getResource("/batch-runtime-repository.log4j2.properties").getFile());
			props.load(fis);
			LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
			LoggerWrapperFactory.setLogConfiguration(props);
		}catch(Exception e) {
			throw new Exception("Impostazione logging fallita: "+e.getMessage());
		}
		
		GeneratorProperties generatorProperties = GeneratorProperties.getInstance();
		
		int refreshConnection = generatorProperties.getRefreshConnessione();
		int scadenzaMessaggi = generatorProperties.getScadenzaMessaggiMinuti();
		
		String tipoRepositoryBuste = generatorProperties.getRepositoryBuste();
		boolean useDataRegistrazione = generatorProperties.isUseDataRegistrazione();
		
		boolean debug = generatorProperties.isMessaggiDebug();
		boolean logQuery = generatorProperties.isMessaggiLogQuery();
		
		int finestraSecondi = generatorProperties.getMessaggiFinestraSecondi();
		
		for (TipoRuntimeRepository tipoGestore : listTipiDaProcessare) {
					
			Logger logCore = null;
			Logger logSql = null;
			if(debug) {
				logCore = LoggerWrapperFactory.getLogger("govway.batch.runtime_repository."+tipoGestore.getValue());
			}
			else {
				logCore = LoggerWrapperFactory.getLogger("govway.batch.runtime_repository."+tipoGestore.getValue()+".error");
			}
			if(logQuery) {
				logSql = LoggerWrapperFactory.getLogger("govway.batch.runtime_repository."+tipoGestore.getValue()+".sql");
			}
			else {
				logSql = LoggerWrapperFactory.getLogger("govway.batch.runtime_repository."+tipoGestore.getValue()+".sql.error");
			}
			
			try {
				ConfigurazionePdD configPdD = new ConfigurazionePdD();
				configPdD.setAttesaAttivaJDBC(-1);
				configPdD.setCheckIntervalJDBC(-1);
				configPdD.setLoader(new Loader(Generator.class.getClassLoader()));
				configPdD.setLog(logCore);
				ProtocolFactoryManager.initialize(logCore, configPdD,
						generatorProperties.getProtocolloDefault());
				
				DateManager.initializeDataManager(SystemDate.class.getName(), new Properties(), LoggerWrapperFactory.getLogger(Generator.class));
				
			} catch (Exception e) {
				throw new Exception("Errore durante la gestione del repository '"+tipoGestore+"' (InitConfigurazione - ProtocolFactoryManager): "+e.getMessage(),e);
			}
			
			try{
				switch (tipoGestore) {
				case MESSAGGI:{
					GestoreMessaggi gestore = new GestoreMessaggi(debug, logQuery, logCore, logSql, finestraSecondi, refreshConnection, scadenzaMessaggi, tipoRepositoryBuste);
					gestore.process();
					break;
				}
				case BUSTE:{
					GestoreBuste gestore = new GestoreBuste(debug, logQuery, logCore, logSql, finestraSecondi, refreshConnection, tipoRepositoryBuste, useDataRegistrazione);
					gestore.process();
					break;
				}
				case ALL:{
					throw new Exception("Tipo '"+tipoGestore+"' non supportato");
				}
				}
			}catch(Exception e){
				throw new Exception("Errore durante la gestione del repository '"+tipoGestore+"': "+e.getMessage(),e);
			}
		
		}
		
	}

}
