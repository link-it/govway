package org.openspcoop2.web.monitor.core.core;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
  * Single SignOn Utilities
 * @author Stefano Corallo <corallo@link.it>
 * @version 1.0, 15/gen/2009
 */
public class SSOUtils {
	
	private static String sharedSessionName = "sharedSession";
	private static String SSOCONTEXTNAME = null;
	private static Logger log = LoggerWrapperFactory.getLogger(SSOUtils.class);
	private static String SSO_COOKIE_NAME= "linkSSO";
	
	/**
	 * Utiliti per settare il login
	 * @param context
	 * @param sessionId
	 * @param login
	 */
	@SuppressWarnings("unchecked")
	public synchronized static void setSSOLogin(ServletContext context, String sessionId, String login){
				
		String ssocontextname = SSOUtils.readSSOContext();
		ServletContext ctx = context.getContext(ssocontextname);
		
		Hashtable<String, String> sharedSession = (Hashtable<String, String>) ctx.getAttribute(SSOUtils.sharedSessionName);
		
						
		if(sharedSession==null) sharedSession = new Hashtable<String, String>();
		SSOUtils.log.debug("adding association (sessionid,login) <--> ("+sessionId+","+login+")");
		sharedSession.put(sessionId, login);
		
		ctx.setAttribute(SSOUtils.sharedSessionName, sharedSession);
		
	}
			
	private static String readSSOContext(){
		InputStream ins = null;
		try{
			//ritorno valore gia' letto
			if(SSOUtils.SSOCONTEXTNAME!=null) return SSOUtils.SSOCONTEXTNAME;
			
			//leggo
			
			Properties prop = Utils.readProperties("/META-INF/integration.properties");
						
			SSOUtils.SSOCONTEXTNAME = prop.getProperty("ssoContext");
			
		}catch (Exception e) {
			SSOUtils.log.error(e.getMessage(), e);
		}finally{
			try{
				if(ins!=null)ins.close();
			}catch (Exception e) {}
		}
		
		if(SSOUtils.SSOCONTEXTNAME==null){
			SSOUtils.log.warn("ssoContext non impostato nel file di configurazione META-INF/integration.properties utilizzo valore di default ssoContext=/pddConsole");
			SSOUtils.SSOCONTEXTNAME = "/pddConsole";
		}
		
		return SSOUtils.SSOCONTEXTNAME;
	}
	
	
	/**
	 * Recupera la login associata al sessionId fornito
	 * @param context
	 * @param sessionId il session id che identifica la sessione
	 * @param ssoContextName Lo shared context
	 * @return
	 */
	public synchronized static String getSSOLogin(ServletContext context, String sessionId){
		
		String ssocontextname = SSOUtils.readSSOContext();
		return SSOUtils.getSSOLogin(context, sessionId, ssocontextname);
		
	}
		
	
	@SuppressWarnings("unchecked")
	private synchronized static String getSSOLogin(ServletContext context, String sessionId,String ssoContextName){
		
		//prelevo il context sso
		SSOUtils.log.debug("ssocontextname="+ssoContextName);
		ServletContext ssocontext = context.getContext(ssoContextName);
		
		if(ssocontext==null) return null;
		
		String login = null;
		
		SSOUtils.log.debug("searching login information for sessionId "+sessionId);
		
		Hashtable<String, String> sharedSession = (Hashtable<String, String>) ssocontext.getAttribute(SSOUtils.sharedSessionName);
		if(sharedSession!=null){
			login = sharedSession.get(sessionId);
		}
		
		if(login==null) SSOUtils.log.debug("No login found for sessionId: "+sessionId);
		else SSOUtils.log.debug("login ("+login+") found for sessionId: "+sessionId);
		
		return login;
		
	}

	/**
	 * Rimuove l'associazione (sessionId, login) se presente
	 * aggiorna le informazioni nello shared context
	 * @param context
	 * @param sessionId
	 */
	public synchronized static void removeSSOLogin(ServletContext context, String sessionId){
		String ssocontextname = SSOUtils.readSSOContext();
		SSOUtils.removeSSOLogin(context, sessionId, ssocontextname);
	}
	
	
	@SuppressWarnings("unchecked")
	private synchronized static void removeSSOLogin(ServletContext context, String sessionId, String ssoContextName){
		
		SSOUtils.log.debug("ssocontextname="+ssoContextName);
		//prelevo il context sso
		ServletContext ssocontext = context.getContext(ssoContextName);
		
		String login = null;
		
		SSOUtils.log.debug("searching login information for sessionId "+sessionId);
		
		Hashtable<String, String> sharedSession = (Hashtable<String, String>) ssocontext.getAttribute(SSOUtils.sharedSessionName);
		if(sharedSession!=null){
			login = sharedSession.remove(sessionId);
		}
		
		if(login==null){
			SSOUtils.log.debug("No login to remove for sessionId "+sessionId);
		}else{
			SSOUtils.log.debug("login ("+login+") removed for sessionId "+sessionId);
			SSOUtils.log.debug("share session name = "+SSOUtils.sharedSessionName);
			ssocontext.setAttribute(SSOUtils.sharedSessionName, sharedSession);
		}
	}
	
	public static String getSessionIdFromSSOTicket(HttpServletRequest request){
		Cookie[] cookies = request.getCookies();
		String sessionId="";
		if(cookies!=null){
			for (Cookie cookie : cookies) {
				if(SSOUtils.SSO_COOKIE_NAME.equals(cookie.getName())){
					SSOUtils.log.debug("found sso cookie: "+cookie.toString());
					sessionId=cookie.getValue();
				}
			}
		}
		return sessionId;
	}
	
	public static Cookie createSSOCookie(String sessionId){
		
		Cookie c = new Cookie(SSOUtils.SSO_COOKIE_NAME, sessionId);
		c.setPath("/");
		return c;
	}
}


