package org.openspcoop2.pdd.core.eventi;

import java.sql.Connection;

import org.slf4j.Logger;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.date.DateManager;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.dao.jdbc.JDBCServiceManager;

public class GestoreEventi {

	private static GestoreEventi staticInstance = null;
	private static synchronized void initialize() throws Exception{
		if(staticInstance==null){
			staticInstance = new GestoreEventi();
		}
	}
	public static GestoreEventi getInstance() throws Exception{
		if(staticInstance==null){
			initialize();
		}
		return staticInstance;
	}
	
	
    private DAOFactory daoFactory = null;
	private ServiceManagerProperties daoFactoryServiceManagerProperties = null;
    private Logger daoFactoryLogger = null;
    
    private OpenSPCoop2Properties properties;
    
    private boolean debug;
	
    private static final String ID_MODULO = "GestoreEventi";
    
    public GestoreEventi() throws Exception{
    	
    	// ** config **
		
    	this.properties = OpenSPCoop2Properties.getInstance();
    	
		// Inizializzazione DAOFactory
		try{
	    	
	    	this.debug = this.properties.isEventiDebug();
	    	
	    	DAOFactoryProperties daoFactoryProperties = null;
	    	this.daoFactoryLogger = OpenSPCoop2Logger.getLoggerOpenSPCoopEventi(this.debug);
	    	this.daoFactory = DAOFactory.getInstance(this.daoFactoryLogger);
			daoFactoryProperties = DAOFactoryProperties.getInstance(this.daoFactoryLogger);
			this.daoFactoryServiceManagerProperties = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.eventi.utils.ProjectInfo.getInstance());
			this.daoFactoryServiceManagerProperties.setShowSql(this.debug);	
			
    	}catch(Exception e){
    		throw new DriverConfigurazioneException(e.getMessage(),e);
    	}
		
    }
        
    public void log(Evento evento) throws Exception{
    	
    	if(evento.getOraRegistrazione()==null){
    		evento.setOraRegistrazione(DateManager.getDate());
    	}
    	
    	DBManager dbManager = null;
    	Resource r = null;
    	String modulo = ID_MODULO+"."+evento.getTipo()+"."+evento.getCodice();
		try{
			dbManager = DBManager.getInstance();
			r = dbManager.getResource(this.properties.getIdentitaPortaDefault(null), modulo, evento.getIdTransazione());
			if(r==null){
				throw new Exception("Risorsa al database non disponibile");
			}
			Connection con = (Connection) r.getResource();
			if(con == null)
				throw new Exception("Connessione non disponibile");	
			this.log(evento, con);
			
		}finally{
			try{
				if(r!=null)
					dbManager.releaseResource(this.properties.getIdentitaPortaDefault(null), modulo, r);
			}catch(Exception e){}
		}
    }
    public void log(Evento evento, Connection connection) throws Exception{
    	
    	if(evento.getOraRegistrazione()==null){
    		evento.setOraRegistrazione(DateManager.getDate());
    	}
    	
    	if(connection == null)
			throw new Exception("Connessione non fornita");	
    	
    	JDBCServiceManager jdbcServiceManager = 
    			(JDBCServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.eventi.utils.ProjectInfo.getInstance(), connection, 
    					this.daoFactoryServiceManagerProperties, this.daoFactoryLogger);
    	jdbcServiceManager.getEventoService().create(evento);  	
    	
    	this.daoFactoryLogger.info("CREATO EVENTO: "+EventiUtilities.toString(evento));

    	//System.out.println("CREATO EVENTO: "+EventiUtilities.toString(evento));
    }

}
