package org.openspcoop2.web.monitor.transazioni.mbean;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.BlackListElement;
import org.openspcoop2.web.monitor.core.utils.MimeTypeUtils;


/****
 * 
 * Bean per che incapsula un oggetto di tipo DumpAllegato, per la visualizzazione nella pagina web.
 * 
 * @author pintori
 *
 */
public class DumpAllegatoBean extends DumpAllegato {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	public DumpAllegatoBean(DumpAllegato dumpAllegato) {

		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
//		metodiEsclusi.add(new BlackListElement("setProtocolFactory",
//				IProtocolFactory.class));
//		metodiEsclusi.add(new BlackListElement("setTempoValue", String.class));

		BeanUtils.copy(this, dumpAllegato, metodiEsclusi);
		
	}
	
	public String getMimeTypeImageClass() {
		
		return MimeTypeUtils.getMimeTypeImageClass(this.getContentType());
		
	}
	
	public byte[] decodeBase64(){
		Logger log = LoggerManager.getPddMonitorCoreLogger();
		try{
			PddMonitorProperties prop = PddMonitorProperties.getInstance(log);
			boolean isTransazioniAllegatiDecodeBase64 = prop.isTransazioniAllegatiDecodeBase64();
			List<String> isTransazioniAllegatiDecodeBase64_noDecodeList = prop.getTransazioniAllegatiDecodeBase64_noDecodeList();
			
			String mimeTypeBase = MimeTypeUtils.getBaseType(this.getContentType());
			boolean checkBase64 = isTransazioniAllegatiDecodeBase64 &&
					mimeTypeBase!=null &&
					!isTransazioniAllegatiDecodeBase64_noDecodeList.contains(mimeTypeBase);
			byte[] contenutoAllegato = this.getAllegato();
			if(checkBase64){
				if(MimeTypeUtils.isBase64(contenutoAllegato)){
					log.debug("Decode Base64 Content ["+this.getContentId()+"] ...");
					return org.apache.commons.codec.binary.Base64.decodeBase64(contenutoAllegato);
				}
			}
		}catch(Exception e){
			log.error("IsBase64 error: "+e.getMessage(),e);
		}
		return this.getAllegato();
	}
	
	public boolean isBase64(){
		Logger log = LoggerManager.getPddMonitorCoreLogger();
		try{
			PddMonitorProperties prop = PddMonitorProperties.getInstance(log);
			boolean isTransazioniAllegatiDecodeBase64 = prop.isTransazioniAllegatiDecodeBase64();
			List<String> isTransazioniAllegatiDecodeBase64_noDecodeList = prop.getTransazioniAllegatiDecodeBase64_noDecodeList();
			
			String mimeTypeBase = MimeTypeUtils.getBaseType(this.getContentType());
			boolean checkBase64 = isTransazioniAllegatiDecodeBase64 &&
					mimeTypeBase!=null &&
					!isTransazioniAllegatiDecodeBase64_noDecodeList.contains(mimeTypeBase);
			byte[] contenutoAllegato = this.getAllegato();
			if(checkBase64){
				return MimeTypeUtils.isBase64(contenutoAllegato);
			}
		}catch(Exception e){
			log.error("IsBase64 error: "+e.getMessage(),e);
		}
		return false;
	}
}
