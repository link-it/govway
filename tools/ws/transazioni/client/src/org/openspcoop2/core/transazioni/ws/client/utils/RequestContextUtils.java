package org.openspcoop2.core.transazioni.ws.client.utils;

import java.io.InputStream;
import java.util.Properties;

import javax.xml.ws.BindingProvider;

/**     
 * RequestContextUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RequestContextUtils {

	private static final String PROPERTIES = "/client.properties";
	private String url;
	private String username;
	private String password;
	
	public RequestContextUtils(String service) throws Exception {
		try{
			InputStream is = RequestContextUtils.class.getResourceAsStream(PROPERTIES);
			if(is==null){
				throw new Exception("File ["+PROPERTIES+"] not found in classpath");
			}
			Properties props = new Properties();
			props.load(is);
			this.url = props.getProperty(service+".url");
			this.username = props.getProperty(service+".username");
			this.password = props.getProperty(service+".password");
		} catch (Exception e) {
			java.util.logging.Logger.getLogger(RequestContextUtils.class.getName())
            .log(java.util.logging.Level.INFO,
            		"Errore durante l'init del RequestContextUtils", e);
			throw e;
		}
	}
	
	public void addRequestContextParameters(BindingProvider provider) {
		if(this.url != null)
			provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, this.url);
		if(this.username !=null && this.password!=null){
			// to use Basic HTTP Authentication: 
			provider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, this.username);
			provider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, this.password);
		}
		provider.getRequestContext().put("schema-validation-enabled", true);
	}
}
