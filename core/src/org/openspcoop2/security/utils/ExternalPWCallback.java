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

package org.openspcoop2.security.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.properties.PropertiesReader;

/**
 * Gestore delle password dei certificati scambiati con wssecurity. Le password vengono mantenute in un file di proprietà
 * 	
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExternalPWCallback
    implements CallbackHandler
{

    public ExternalPWCallback()
    {
    }

    @Override
   	public void handle(Callback[] callbacks) throws IOException,
               UnsupportedCallbackException {
        for(int i = 0; i < callbacks.length; i++)
            if(callbacks[i] instanceof WSPasswordCallback)
            {
                WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];
                
                Properties pSource = null;
                try{
                	pSource = getProperties();
                }catch(Exception e){
                	throw new UnsupportedCallbackException(callbacks[i], "Identifier ["+pc.getIdentifier()+"], occurs error (read password from properties file): "+e.getMessage());
                }
                PropertiesReader pr = new PropertiesReader(pSource, true);
                Properties p = null;
                try{
	                p = pr.readProperties_convertEnvProperties("user.",true);
                }catch(Exception e){
                	throw new UnsupportedCallbackException(callbacks[i], "Identifier ["+pc.getIdentifier()+"], occurs error (read password from properties file): "+e.getMessage());
                }
                String key = pc.getIdentifier();
                if(p.containsKey(key)==false){
                	
                	// check se fosse con maiuscolo minuscolo
                	if(p.size()>0){
                		Iterator<?> it = p.keySet().iterator();
                		while (it.hasNext()) {
							Object keyCheck = (Object) it.next();
							if(keyCheck instanceof String){
								String tmp = (String) keyCheck;
								if(tmp.toLowerCase().equals(key)){
									key = tmp; // aggiorno la chiave
								}
							}
						}
                	}
                	if(key==null){
                		throw new UnsupportedCallbackException(callbacks[i], "Identifier ["+pc.getIdentifier()+"] unknown");
                	}
                }
                String password = p.getProperty(key);
                pc.setPassword(password);
                
            } else
            {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }

    }
    
    
    private static String propertiesFilePath = "/etc/govway/wssPassword.properties";
    private static Properties wssProperties = null;
    
    private static String wssRefreshProps = "refresh";
    private static boolean wssRefresh = true; // default
    private static Date wssRead = null;
    private static int wssTime = 1*1000*60; // ogni minuti refresh
    
    public static synchronized void initialize() throws SecurityException{
    	initialize(propertiesFilePath);
    }
    public static synchronized void initialize(String path) throws SecurityException{
    	
    	propertiesFilePath = path; // aggiorno path nel caso mi venga inizializzato un path differente. Il path verrà utilizzato poi in presenza del file scaduto
    	
    	if(wssProperties==null || isScaduto()){
    		FileInputStream fin = null;
    		try{
    			File f = new File(path);
    			if(f.exists()==false){
    				throw new SecurityException("File properties ["+path+"] doesn't exists");
    			}
    			if(f.canRead()==false){
    				throw new SecurityException("File properties ["+path+"] cannot read");
    			}
    			fin = new FileInputStream(f);
    			
    			wssProperties = new Properties();
    			wssProperties.load(fin);
    			
    			if(wssProperties.containsKey(wssRefreshProps)){
    				String tmp = wssProperties.getProperty(wssRefreshProps);
    				wssRefresh = "true".equalsIgnoreCase(tmp.trim());
    			}
    			
    			if(wssRefresh){
    				wssRead = DateManager.getDate(); // update date
    			}
    		}
    		catch(SecurityException e){
    			throw e;
    		}
    		catch(Exception e){
    			throw new SecurityException("Errore durante la lettura del file properties ["+path+"]: "+e.getMessage(),e);
    		}
    		finally{
    			try{
    				if(fin!=null)
    					fin.close();
    			}catch(Exception eClose){}
    		}
    	}
    } 
    public static Properties getProperties() throws SecurityException{
    	if(wssProperties==null || isScaduto()){
    		initialize();
    	}
    	return wssProperties;
    }
    private static boolean isScaduto(){
    	boolean scaduto = false;
    	if(wssRefresh && wssRead!=null){
    		long read = wssRead.getTime();
    		Date now = DateManager.getDate();
    		long diff = now.getTime() - read;
    		if(diff > wssTime){
    			scaduto = true;
    		}
    	}
    	return scaduto;
    }

    
}
