package org.openspcoop2.protocol.as4.pmode.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;

import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.as4.pmode.Translator;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 07 nov 2017 $
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
		
		Class.forName(driver).newInstance();
		Connection con = null;
		try{
			con = DriverManager.getConnection(url, userName, password);
		
			ConfigurazionePdD configPdD = new ConfigurazionePdD();
			configPdD.setLog(log);
			configPdD.setLoader(new Loader());
			ProtocolFactoryManager.initializeSingleProtocol(log, configPdD, AS4Costanti.PROTOCOL_NAME);
			IProtocolFactory<?> p = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory();
	
			Translator t = new Translator();
			File f  = File.createTempFile("configPMODE", ".xml");
			FileOutputStream fout = new FileOutputStream(f);
			Writer out = new OutputStreamWriter(fout);
			
			IDriverRegistroServiziGet driverOp2 = 
					new DriverRegistroServiziDB(con, log, tipoDatabase.getNome());
			IRegistryReader rr = p.getRegistryReader(driverOp2);
			
			t.translate(rr, p, out);
			
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
