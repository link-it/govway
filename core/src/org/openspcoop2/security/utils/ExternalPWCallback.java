/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.openspcoop2.security.SecurityException;

/**
 * Gestore delle password dei certificati scambiati con wssecurity. Le password vengono mantenute in un file di proprietà
 * 	
 * @author Andrea Poli <apoli@link.it>
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
                
                Properties p = null;
                try{
                	p = getProperties();
                }catch(Exception e){
                	throw new UnsupportedCallbackException(callbacks[i], "Identifier ["+pc.getIdentifier()+"], occurs error (read password from properties file): "+e.getMessage());
                }
                String key = "user."+pc.getIdentifier();
                if(p.containsKey(key)==false){
                	throw new UnsupportedCallbackException(callbacks[i], "Identifier ["+pc.getIdentifier()+"] unknown");
                }
                String password = p.getProperty(key);
                pc.setPassword(password);
                
            } else
            {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }

    }
    
    
    private static String propertiesFilePath = "/etc/openspcoop2/wssPassword.properties";
    private static Properties wssProperties = null;
    
    public static synchronized void initialize() throws SecurityException{
    	initialize(propertiesFilePath);
    }
    public static synchronized void initialize(String path) throws SecurityException{
    	if(wssProperties==null){
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
    	if(wssProperties==null){
    		initialize();
    	}
    	return wssProperties;
    }

    
}
