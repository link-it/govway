package org.openspcoop2.pdd.logger;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DumpUtility {

	public static String toString(Element element,Logger log,OpenSPCoop2Message msg){
		// Faccio una pretty-print
		String xml = null;
		try{
			xml = PrettyPrintXMLUtils.prettyPrintWithTrAX(element);
		}catch(Throwable e){
			log.error("(DumpUtility) PrettyPrintWithTrAX non riuscita",e);
			try{
				xml = msg.getAsString(element, false);
			}catch(Throwable e2){
				log.error("(DumpUtility) msg.getAsString(soap,false) non riuscita",e2);
				try{
					xml = XMLUtils.getInstance().toString(element, false);
				}catch(Throwable e3){
					log.error("(DumpUtility) XMLUtils.toString non riuscita",e3);
				}
			}
		}
		return xml;
	}
	
	public static String toString(Document d,Logger log,OpenSPCoop2Message msg){
		// Faccio una pretty-print
		String xml = null;
		try {
			xml = PrettyPrintXMLUtils.prettyPrintWithTrAX(d);
		}catch(Throwable e){
			log.error("(DumpUtility) PrettyPrintWithTrAX non riuscita",e);
			try{
				xml = msg.getAsString(d, false);
			}catch(Throwable e2){
				if(log!=null)
					log.error("(DumpUtility) msg.getAsString(soap,false) non riuscita",e2);
				try{
					xml = XMLUtils.getInstance().toString(d, false);
				}catch(Throwable e3){
					if(log!=null)
						log.error("(DumpUtility) XMLUtils.toString non riuscita",e3);
				}
			}
		}
		return xml;
	}
	
}
