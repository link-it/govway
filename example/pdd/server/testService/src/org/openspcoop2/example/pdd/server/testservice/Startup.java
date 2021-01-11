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
package org.openspcoop2.example.pdd.server.testservice;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * Startup
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Startup implements ServletContextListener { 

	public static Logger logPing;
	public static Logger logEcho;
	public static Logger logStressTest;
	public static File repositoryResponseFiles;
	public static List<String> whitePropertiesList;
	public static boolean genericError = false;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try{
			
			LoggerWrapperFactory.setLogConfiguration(Startup.class.getResource("/testService.log4j2.properties"));			
			logPing = LoggerWrapperFactory.getLogger("govway.ping");
			logPing.info("avviato con successo");
			logEcho = LoggerWrapperFactory.getLogger("govway.echo");
			logEcho.info("avviato con successo");
			logStressTest = LoggerWrapperFactory.getLogger("govway.stress");
			logStressTest.info("avviato con successo");
			
			Properties p = new Properties();
			p.load(Startup.class.getResourceAsStream("/testService.properties"));
			String repo = p.getProperty("responseFiles.repository");
			if(repo!=null) {
				repo = repo.trim();
				File f = new File(repo);
				if(f.exists()==false) {
					throw new Exception("Directory ["+f.getAbsolutePath()+"] defined in property 'responseFiles.repository' not exists");
				}
				if(f.isDirectory()==false) {
					throw new Exception("Directory ["+f.getAbsolutePath()+"] defined in property 'responseFiles.repository' is not directory");
				}
				if(f.canRead()==false) {
					throw new Exception("Directory ["+f.getAbsolutePath()+"] defined in property 'responseFiles.repository' cannot read");
				}
				repositoryResponseFiles = f;
			}
			
			String wList = p.getProperty("parameters.whiteList");
			if(wList!=null) {
				wList = wList.trim();
				
				String [] tmp = wList.split(",");
				if(tmp!=null && tmp.length>0) {
					List<String> tmpL = new ArrayList<>();
					for (int i = 0; i < tmp.length; i++) {
						String pr = tmp[i];
						if(pr!=null) {
							pr = pr.trim();
							if(!"".equals(pr)) {
								tmpL.add(pr);
							}
						}
					}
					if(tmpL.size()>0) {
						whitePropertiesList = tmpL;
					}
				}
			}
			
			String genErr = p.getProperty("genericError");
			if(genErr!=null) {
				genErr = genErr.trim();
				Startup.genericError = "true".equalsIgnoreCase(genErr);
			}
			
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
	}

}