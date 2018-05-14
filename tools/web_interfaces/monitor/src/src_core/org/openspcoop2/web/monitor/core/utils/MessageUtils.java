package org.openspcoop2.web.monitor.core.utils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.openspcoop2.generic_project.beans.InUse;

/**
 * Classe di utilita' per i @{link {@link FacesMessage}
 * @author corallo
 *
 */
public class MessageUtils {

	public static void addInfoMsg(String message){
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,message,null);
		FacesContext.getCurrentInstance().addMessage(null, m);
	}
	
	public static void addErrorMsg(String message){
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,message,null);
		FacesContext.getCurrentInstance().addMessage(null, m);
	}
	
	public static void addWarnMsg(String message){
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_WARN,message,null);
		FacesContext.getCurrentInstance().addMessage(null, m);
	}

	public static void addInfoMsg(String message, String details){
		MessageUtils.addMsg(FacesMessage.SEVERITY_INFO, message, details);
	}
	
	public static void addErrorMsg(String message, String details){
		MessageUtils.addMsg(FacesMessage.SEVERITY_ERROR, message, details);
	}
	
	public static void addWarnMsg(String message, String details){
		MessageUtils.addMsg(FacesMessage.SEVERITY_WARN, message, details);
	}
	
	private static void addMsg(FacesMessage.Severity severity, String message, String details){
		FacesMessage m = new FacesMessage(severity,message,details);
		FacesContext.getCurrentInstance().addMessage(null, m);
	}
	
	public static String toString(InUse inUse){
		if(inUse!=null && inUse.isInUse()){
	 		StringBuffer bf = new StringBuffer();
	 		for (int i = 0; i < inUse.sizeInUseConditionList(); i++) {
	 			if(bf.length()>0){
	 				bf.append("; ");
	 			}
		 		bf.append(inUse.getInUseCondition(i).getCause());
		 		if(inUse.getInUseCondition(i).getIds()!=null && inUse.getInUseCondition(i).getIds().size()>0){
			 		bf.append(": ");
			 		for (int j = 0; j < inUse.getInUseCondition(i).getIds().size(); j++) {
			 			if(j>0){
			 				bf.append(",");
			 			}
			 			bf.append(inUse.getInUseCondition(i).getIds().get(j));
					}
		 		}
	 		}
	 		return bf.toString();
	 	}
		return null;
	}
}
