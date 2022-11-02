/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.integrazione;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.dynamic.Template;
import org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.slf4j.Logger;

/**
 * Classe contenenti utilities per le integrazioni gestite tramite template di trasformazione.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UtilitiesTemplate {

	private String tipoHeaderIntegrazione;
	private OpenSPCoop2Message msg;
	private Context context;
	private Busta busta;
	private Logger log;
	private TipoTrasformazione tipoTrasformazione;
	private Template template;
	
	public UtilitiesTemplate(String tipoHeaderIntegrazione, HeaderIntegrazione integrazione,
			OutRequestPDMessage inRequestPDMessage, Context context, Logger log) throws HeaderIntegrazioneException {
		try {
			init(tipoHeaderIntegrazione, inRequestPDMessage.getMessage(), context, inRequestPDMessage.getBustaRichiesta(), log);
			
			IDPortaDelegata idPD = null;
			if(inRequestPDMessage.getPortaDelegata()!=null) {
				idPD = new IDPortaDelegata();
				idPD.setNome(inRequestPDMessage.getPortaDelegata().getNome());
			}
			
			List<Proprieta> proprieta = null;
			if(inRequestPDMessage.getPortaDelegata()!=null && inRequestPDMessage.getPortaDelegata().getProprietaList()!=null) {
				proprieta = inRequestPDMessage.getPortaDelegata().getProprietaList();
			}
			init(proprieta, true, true,
					null, idPD);
			
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(),e);
		}
	}
	public UtilitiesTemplate(String tipoHeaderIntegrazione, HeaderIntegrazione integrazione,
			OutResponsePDMessage inResponsePDMessage, Context context, Logger log) throws HeaderIntegrazioneException {
		try {
			init(tipoHeaderIntegrazione, inResponsePDMessage.getMessage(), context, inResponsePDMessage.getBustaRichiesta(), log);
			
			IDPortaDelegata idPD = null;
			if(inResponsePDMessage.getPortaDelegata()!=null) {
				idPD = new IDPortaDelegata();
				idPD.setNome(inResponsePDMessage.getPortaDelegata().getNome());
			}
			
			List<Proprieta> proprieta = null;
			if(inResponsePDMessage.getPortaDelegata()!=null && inResponsePDMessage.getPortaDelegata().getProprietaList()!=null) {
				proprieta = inResponsePDMessage.getPortaDelegata().getProprietaList();
			}
			init(proprieta, true, false,
					null, idPD);
			
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(),e);
		}
	}
	public UtilitiesTemplate(String tipoHeaderIntegrazione, HeaderIntegrazione integrazione,
			OutRequestPAMessage inRequestPAMessage, Context context, Logger log) throws HeaderIntegrazioneException {
		try {
			init(tipoHeaderIntegrazione, inRequestPAMessage.getMessage(), context, inRequestPAMessage.getBustaRichiesta(), log);
			
			IDPortaApplicativa idPA = null;
			if(inRequestPAMessage.getPortaApplicativa()!=null) {
				idPA = new IDPortaApplicativa();
				idPA.setNome(inRequestPAMessage.getPortaApplicativa().getNome());
			}
			
			List<Proprieta> proprieta = null;
			if(inRequestPAMessage.getPortaApplicativa()!=null && inRequestPAMessage.getPortaApplicativa().getProprietaList()!=null) {
				proprieta = inRequestPAMessage.getPortaApplicativa().getProprietaList();
			}
			init(proprieta, false, true,
					idPA, null);
			
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(),e);
		}
	}
	public UtilitiesTemplate(String tipoHeaderIntegrazione, HeaderIntegrazione integrazione,
			OutResponsePAMessage inResponsePAMessage, Context context, Logger log) throws HeaderIntegrazioneException {
		try {
			init(tipoHeaderIntegrazione, inResponsePAMessage.getMessage(), context, inResponsePAMessage.getBustaRichiesta(), log);
			
			IDPortaApplicativa idPA = null;
			if(inResponsePAMessage.getPortaApplicativa()!=null) {
				idPA = new IDPortaApplicativa();
				idPA.setNome(inResponsePAMessage.getPortaApplicativa().getNome());
			}
			
			List<Proprieta> proprieta = null;
			if(inResponsePAMessage.getPortaApplicativa()!=null && inResponsePAMessage.getPortaApplicativa().getProprietaList()!=null) {
				proprieta = inResponsePAMessage.getPortaApplicativa().getProprietaList();
			}
			init(proprieta, false, false,
					idPA, null);
			
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(),e);
		}
	}
	
	private void init(String tipoHeaderIntegrazione, OpenSPCoop2Message msg, Context context, Busta busta, Logger log){
		this.tipoHeaderIntegrazione = tipoHeaderIntegrazione;
		this.msg = msg;
		this.context = context;
		this.busta = busta;
		this.log = log;
	}
	private void init(List<Proprieta> proprieta, boolean portaDelegata, boolean request,
			IDPortaApplicativa idPA, IDPortaDelegata idPD) throws HeaderIntegrazioneException {
		try {
			OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
			TipoTrasformazione tipoTrasformazione = null;
			String file = null;
			if(portaDelegata) {
				if(request) {
					tipoTrasformazione = properties.getIntegrazioneTemplatePortaDelegataRequestTipo();
					file = properties.getIntegrazioneTemplatePortaDelegataRequestFile();
				}
				else {
					tipoTrasformazione = properties.getIntegrazioneTemplatePortaDelegataResponseTipo();
					file = properties.getIntegrazioneTemplatePortaDelegataResponseFile();
				}
			}
			else {
				if(request) {
					tipoTrasformazione = properties.getIntegrazioneTemplatePortaApplicativaRequestTipo();
					file = properties.getIntegrazioneTemplatePortaApplicativaRequestFile();
				}
				else {
					tipoTrasformazione = properties.getIntegrazioneTemplatePortaApplicativaResponseTipo();
					file = properties.getIntegrazioneTemplatePortaApplicativaResponseFile();
				}
			}
			
			boolean fileDefinedInPorta = false;
			
			if(proprieta!=null && !proprieta.isEmpty()) {
				
				String tipoTrasformazionePropertyName = request ? properties.getIntegrazioneTemplateRequestPropertyTipo() : properties.getIntegrazioneTemplateResponsePropertyTipo();
				String fileTrasformazionePropertyName = request ? properties.getIntegrazioneTemplateRequestPropertyFile() : properties.getIntegrazioneTemplateResponsePropertyFile();
				
				for (Proprieta p : proprieta) {
					if(tipoTrasformazionePropertyName.equalsIgnoreCase(p.getNome())) {
						tipoTrasformazione = convert(p.getValore());
					}
					if(fileTrasformazionePropertyName.equalsIgnoreCase(p.getNome())) {
						file = p.getValore();
						fileDefinedInPorta = true;
					}
				}
			}

			this.tipoTrasformazione = tipoTrasformazione;
			if(this.tipoTrasformazione==null) {
				throw new Exception("Tipo del template non definito");
			}
			
			if(file==null) {
				throw new Exception("Template file non definito");
			}
			File fFile = new File(file);
			/*
			ContentFile cFile = ConfigurazionePdDManager.getInstance().getContentFile(fFile);
			if(!cFile.isExists()) {
				throw new Exception("Template file '"+fFile.getAbsolutePath()+"' not exists");
			}
			byte [] template = cFile.getContent();
			if(template==null || template.length<=0) {
				throw new Exception("Template '"+fFile.getAbsolutePath()+"' is empty");
			}
			*/
			
			RequestInfo requestInfo = null;
			if(this.context!=null && this.context.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
				requestInfo = (RequestInfo) this.context.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
			}
			
			ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance();
			if(fileDefinedInPorta) {
				if(portaDelegata) {
					this.template = configurazionePdDManager.getTemplateIntegrazione(idPD, fFile, requestInfo);
				}
				else {
					this.template = configurazionePdDManager.getTemplateIntegrazione(idPA, fFile, requestInfo);
				}
			}
			else {
				this.template = configurazionePdDManager.getTemplateIntegrazione(fFile, requestInfo);
			}
			
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(),e);
		}
	}

	public static TipoTrasformazione convert(String tipo) throws HeaderIntegrazioneException {
		try {
			TipoTrasformazione tipoT = TipoTrasformazione.toEnumConstant(tipo, true);
			if(!tipoT.isTemplateFreemarker() &&
					!tipoT.isTemplateVelocity()) {
				throw new Exception("Tipo '"+tipo+"' non supportato");
			}
			return tipoT;
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(),e);
		}
	}

		
	public void process(boolean request) throws HeaderIntegrazioneException {
		try {
			if(this.msg==null) {
				return;
			}
			
			boolean bufferMessage_readOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
			Map<String, Object> dynamicMapRequest = DynamicUtils.buildDynamicMap(this.msg, this.context, this.busta, this.log,
					bufferMessage_readOnly);
			Map<String, Object> dynamicMapResponse = null;
			if(!request) {
				dynamicMapResponse = DynamicUtils.buildDynamicMapResponse(this.msg, this.context, this.busta, this.log,
						bufferMessage_readOnly,
						dynamicMapRequest);
			}
			
			Map<String, Object> dynamicMap = request ? dynamicMapRequest : dynamicMapResponse;
			
			if(this.tipoTrasformazione==null) {
				throw new Exception("Tipo di trasformazione non definita");
			}
			if(this.template==null) {
				throw new Exception("Template non definito");
			}
			
			switch (this.tipoTrasformazione) {
			case FREEMARKER_TEMPLATE:
			case CONTEXT_FREEMARKER_TEMPLATE:
		    case FREEMARKER_TEMPLATE_ZIP:
			
				this.log.debug("trasformazione '"+this.tipoHeaderIntegrazione+"' ["+this.tipoTrasformazione+"], risoluzione template ...");
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				if(TipoTrasformazione.FREEMARKER_TEMPLATE.equals(this.tipoTrasformazione) || TipoTrasformazione.CONTEXT_FREEMARKER_TEMPLATE.equals(this.tipoTrasformazione)) {
					DynamicUtils.convertFreeMarkerTemplate(this.template, dynamicMap, bout);
				}
				else {
					DynamicUtils.convertZipFreeMarkerTemplate(this.template.getZipTemplate(), dynamicMap, bout);
				}
				bout.flush();
				bout.close();
				this.log.debug("trasformazione '"+this.tipoHeaderIntegrazione+"' ["+this.tipoTrasformazione+"], risoluzione template completata");
				// ignoro il contenuto della trasformazione. Questo handler serve solamente a modificare il messaggio in transito
				
				break;
				
			case VELOCITY_TEMPLATE:
			case CONTEXT_VELOCITY_TEMPLATE:
		    case VELOCITY_TEMPLATE_ZIP:

							
		    	this.log.debug("trasformazione '"+this.tipoHeaderIntegrazione+"' ["+this.tipoTrasformazione+"], risoluzione template ...");
				bout = new ByteArrayOutputStream();
				if(TipoTrasformazione.VELOCITY_TEMPLATE.equals(this.tipoTrasformazione) || TipoTrasformazione.CONTEXT_VELOCITY_TEMPLATE.equals(this.tipoTrasformazione)) {
					DynamicUtils.convertVelocityTemplate(this.template, dynamicMap, bout);
				}
				else {
					DynamicUtils.convertZipVelocityTemplate(this.template.getZipTemplate(), dynamicMap, bout);	
				}
				bout.flush();
				bout.close();
				this.log.debug("trasformazione '"+this.tipoHeaderIntegrazione+"' ["+this.tipoTrasformazione+"], risoluzione template completata");
				// ignoro il contenuto della trasformazione. Questo handler serve solamente a modificare il messaggio in transito
				
				break;

			default:
				throw new Exception("Trasformazione '"+this.tipoHeaderIntegrazione+"' definita con un tipo '"+this.tipoTrasformazione+"' non supportato");
			}
			
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(),e);
		}
	}
}
