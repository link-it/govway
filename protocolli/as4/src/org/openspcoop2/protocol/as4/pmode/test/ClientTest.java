/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

package org.openspcoop2.protocol.as4.pmode.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;

import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.as4.pmode.PModeRegistryReader;
import org.openspcoop2.protocol.as4.pmode.Translator;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ClientTest {

	public static void main(String[] args) throws Exception {

		Logger log = LoggerWrapperFactory.getLogger(ClientTest.class);

		String url = "jdbc:postgresql://localhost/openspcoop30";
		String driver = "org.postgresql.Driver";
		String userName = "openspcoop2";
		String password = "openspcoop2";

		TipiDatabase tipoDatabase = TipiDatabase.toEnumConstant("postgresql");
		
		String nomeSoggetto = "Red";
		//String nomeSoggetto = "Blue";
		
		ClassLoaderUtilities.newInstance(driver);
		Connection con = null;
		try{
			con = DriverManager.getConnection(url, userName, password);
		
			ConfigurazionePdD configPdD = new ConfigurazionePdD();
			configPdD.setLog(log);
			configPdD.setLoader(new Loader());
			ProtocolFactoryManager.initializeSingleProtocol(log, configPdD, AS4Costanti.PROTOCOL_NAME);
			IProtocolFactory<?> p = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory();
	
			IDriverRegistroServiziGet driverOp2 = 
					new DriverRegistroServiziDB(con, log, tipoDatabase.getNome());
			IRegistryReader rr = p.getRegistryReader(driverOp2);
			IDriverConfigurazioneGet dirverConfigOp2 = 
					new DriverConfigurazioneDB(con, log, tipoDatabase.getNome());
			IConfigIntegrationReader cf = p.getConfigIntegrationReader(dirverConfigOp2);
			
			PModeRegistryReader pModeRegistryReader = new PModeRegistryReader(rr, cf, p); 
			Translator t = new Translator(pModeRegistryReader);
			File f  = File.createTempFile("configPMODE", ".xml");
			FileOutputStream fout = new FileOutputStream(f);
			Writer out = new OutputStreamWriter(fout);
				
			t.translate(out,nomeSoggetto);
			
			out.flush();
			out.close();
			
			System.out.println("File generato in ["+f.getAbsolutePath()+"]");
		
		}finally{
			try{
				con.close();
			}catch(Exception eClose){}
		}
		
	}

}
