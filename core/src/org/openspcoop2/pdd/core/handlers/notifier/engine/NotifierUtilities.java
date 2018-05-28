package org.openspcoop2.pdd.core.handlers.notifier.engine;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.BaseContext;
import org.openspcoop2.pdd.core.handlers.PreInRequestContext;
import org.openspcoop2.pdd.core.handlers.notifier.NotifierType;

public class NotifierUtilities {

	public static String getIdTransazione(NotifierType notifierType, Object context){
		return (String) getPddContext(notifierType, context).getObject(Costanti.ID_TRANSAZIONE);
	}
	
	public static PdDContext getPddContext(NotifierType notifierType, Object context){
		switch (notifierType) {
		case PRE_IN_REQUEST:
			PreInRequestContext preInRequestContext = (PreInRequestContext) context;
			return preInRequestContext.getPddContext();
		case IN_REQUEST:
		case IN_REQUEST_PROTOCOL_INFO:
		case OUT_REQUEST:
		case POST_OUT_REQUEST:
		case PRE_IN_RESPONSE:
		case IN_RESPONSE:
		case OUT_RESPONSE:
		case POST_OUT_RESPONSE:
			BaseContext baseContext = (BaseContext) context;
			return baseContext.getPddContext();

		default:
			return null;
		}
	}
	
	public static TipoPdD getTipoPorta(NotifierType notifierType, Object context){
		switch (notifierType) {
		case PRE_IN_REQUEST:
			PreInRequestContext preInRequestContext = (PreInRequestContext) context;
			return preInRequestContext.getTipoPorta();
		case IN_REQUEST:
		case IN_REQUEST_PROTOCOL_INFO:
		case OUT_REQUEST:
		case POST_OUT_REQUEST:
		case PRE_IN_RESPONSE:
		case IN_RESPONSE:
		case OUT_RESPONSE:
		case POST_OUT_RESPONSE:
			BaseContext baseContext = (BaseContext) context;
			return baseContext.getTipoPorta();

		default:
			return null;
		}
	}
	
	public static Logger getLogger(NotifierType notifierType, Object context){
		switch (notifierType) {
		case PRE_IN_REQUEST:
			PreInRequestContext preInRequestContext = (PreInRequestContext) context;
			return preInRequestContext.getLogCore();
		case IN_REQUEST:
		case IN_REQUEST_PROTOCOL_INFO:
		case OUT_REQUEST:
		case POST_OUT_REQUEST:
		case PRE_IN_RESPONSE:
		case IN_RESPONSE:
		case OUT_RESPONSE:
		case POST_OUT_RESPONSE:
			BaseContext baseContext = (BaseContext) context;
			return baseContext.getLogCore();

		default:
			return null;
		}
	}
	
	public static OpenSPCoop2Message getOpenSPCoopMessage(NotifierType notifierType, Object context){
		switch (notifierType) {
		case PRE_IN_REQUEST:
			return null;
		case IN_REQUEST:
		case IN_REQUEST_PROTOCOL_INFO:
		case OUT_REQUEST:
		case POST_OUT_REQUEST:
		case PRE_IN_RESPONSE:
		case IN_RESPONSE:
		case OUT_RESPONSE:
		case POST_OUT_RESPONSE:
			BaseContext baseContext = (BaseContext) context;
			return baseContext.getMessaggio();

		default:
			return null;
		}
	}
	
}
