/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.jakarta.JakartaServletDiskFileUpload;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.web.monitor.core.bean.FileUploadBean;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.BrowserInfo;
import org.openspcoop2.web.monitor.core.utils.BrowserInfo.BrowserFamily;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * UploadServlet
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class UploadServlet extends HttpServlet {

	private static final String RESPONSE_LOG = "Response [{}].";
	private static final long serialVersionUID = 1L;
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	
	public static final String ID_TO_DELETE_PARAM_NAME= "id";
	
	@Override
	public void init() throws ServletException {
		super.init();
		UploadServlet.log.debug("Init Servlet UploadServlet completato.");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		throw new ServletException("Operazione non consentita"); 
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UploadServlet.log.debug("DoPost!");

		try {
			ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			
			if(context==null) {
				throw new CoreException("Context is null");
			}
			
			FileUploadBean fileUploadBean = (FileUploadBean)context.getBean("fileUploadBean");
			Map<String, UploadItem> mapElementiRicevuti = fileUploadBean.getMapElementiRicevuti();
			Map<String, String> mapChiaviElementi = fileUploadBean.getMapChiaviElementi();
	
			String baseDeleteURL = req.getContextPath() + req.getServletPath();
		
			String userAgent = req.getHeader(Costanti.USER_AGENT_HEADER_NAME);

			DiskFileItemFactory diskFileItemFactory = DiskFileItemFactory.builder().get();
			JakartaServletDiskFileUpload jakartaServletFileUpload = new JakartaServletDiskFileUpload(diskFileItemFactory);
			List<DiskFileItem> multiparts = jakartaServletFileUpload.parseRequest(req);
			String fileName = "";
			Iterator<DiskFileItem> iter = multiparts.iterator();
			List<ObjectNode> itemResp = new ArrayList<>();
			while (iter.hasNext()) {
				DiskFileItem item = iter.next();
				if (!item.isFormField()) {
					if(item.getName().contains("\\")) {
						fileName = item.getName().substring(item.getName().lastIndexOf("\\")+1);
					} else if(item.getName().contains("/")) {
						fileName = item.getName().substring(item.getName().lastIndexOf("/")+1);
					} else {
						fileName = item.getName();
					}
					
					ObjectNode respBodyItem = leggiContenutoFileRicevuto(mapElementiRicevuti, mapChiaviElementi, baseDeleteURL, fileName, item);

					itemResp.add(respBodyItem);
				}
			}

			ObjectNode responseBody = getResponse(itemResp);
			UploadServlet.log.debug(RESPONSE_LOG, responseBody);
			
			JSONUtils jsonUtils = JSONUtils.getInstance(true);
			jsonUtils.writeTo(responseBody, resp.getOutputStream());
			resp.setContentType(MediaType.APPLICATION_JSON_VALUE);

			BrowserInfo browserInfo = BrowserInfo.getBrowserInfo(userAgent);
			if(browserInfo.getBrowserFamily().equals(BrowserFamily.IE)){
				double versione = browserInfo.getVersion() != null ? browserInfo.getVersion().doubleValue() : -1d;
				
				// fix per IE8
				if((int) versione == 8) {
					resp.setContentType(MediaType.TEXT_HTML_VALUE);
				}
			}

			resp.setStatus(200);	
		}catch(UtilsException | IOException | CoreException e) {
			UploadServlet.log.error("Errore: " + e.getMessage(), e);
			resp.setStatus(500);
		} finally {
			resp.flushBuffer();	
		}
	}

	private ObjectNode leggiContenutoFileRicevuto(Map<String, UploadItem> mapElementiRicevuti, Map<String, String> mapChiaviElementi,
			String baseDeleteURL, String fileName, DiskFileItem item) throws UtilsException {
		ObjectNode respBodyItem;
		int dimensione = 0;
		try {
			byte[] contenuto = item.get();
			dimensione = contenuto != null ? contenuto.length : 0;
			String contentType = item.getContentType();
			UploadServlet.log.debug("Ricevuto File [{}], content-type [{}], dimensione [{}]", fileName, contentType, dimensione);
			UploadItem uploadItem = new UploadItem(fileName, dimensione, contentType, contenuto);


			String idFileRicevuto = UUID.randomUUID().toString().replace("-", "");
			// se ricevo un file gia' presente lo sostituisco
			UploadItem remove = mapElementiRicevuti.remove(fileName);
			String deleteUrl = null;
			if(remove == null) {
				mapChiaviElementi.put(idFileRicevuto, fileName); 
				UploadServlet.log.debug("File [{}] non presente, aggiunto alla lista con id [{}].", fileName, idFileRicevuto);
			} else {
				String oldId = null;
				
				for (Entry<String, String> entry : mapChiaviElementi.entrySet()) {
					if(entry.getValue().equals(fileName)) {
						oldId = entry.getKey();
						break;
					}
				}
				UploadServlet.log.debug("File [{}] gia' presente, assegno vecchio id [{}].", fileName, oldId);
				idFileRicevuto = oldId;
			}

			// salvo contenuto
			mapElementiRicevuti.put(fileName, uploadItem);
			// creo url delete
			deleteUrl = baseDeleteURL + "?"+ID_TO_DELETE_PARAM_NAME+"="+idFileRicevuto;
			// creo risposta
			respBodyItem =  getUploadOkResponseItem(fileName,dimensione,idFileRicevuto,deleteUrl);
		}catch(Exception e) {
			UploadServlet.log.error("[{}] Errore durante l'elaborazione del file ["+fileName+"]: " +e.getMessage(), e);
			respBodyItem = getUploadKoResponseItem(fileName, dimensione, "ERRORE");
		}
		return respBodyItem;
	}


	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UploadServlet.log.debug("DoDelete!");
		List<ObjectNode> itemResp = new ArrayList<>();
		try {
			ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			if(context==null) {
				throw new CoreException("Context is null");
			}
			
			FileUploadBean fileUploadBean = (FileUploadBean)context.getBean("fileUploadBean");
			Map<String, UploadItem> mapElementiRicevuti = fileUploadBean.getMapElementiRicevuti();
			Map<String, String> mapChiaviElementi = fileUploadBean.getMapChiaviElementi();

			String idToDelete = req.getParameter(ID_TO_DELETE_PARAM_NAME);
			UploadServlet.log.debug("Richiesta cancellazione del file id [{}].", idToDelete);

			boolean statoDelete = true;
			String fileName = mapChiaviElementi.remove(idToDelete);
			mapElementiRicevuti.remove(fileName); 

			if(fileName != null) {
				ObjectNode respBodyItem = getDeleteOkResponseItem(fileName, idToDelete, statoDelete);
				itemResp.add(respBodyItem);
	
				ObjectNode responseBody = getResponse(itemResp);
				UploadServlet.log.debug(RESPONSE_LOG, responseBody);
				
				JSONUtils jsonUtils = JSONUtils.getInstance(true);
				jsonUtils.writeTo(responseBody, resp.getOutputStream());
			} else {
				ObjectNode respBodyItem = getDeleteKoResponseItem("File non trovato");
				itemResp.add(respBodyItem);
				
				ObjectNode responseBody = getResponse(itemResp);
				UploadServlet.log.debug(RESPONSE_LOG, responseBody);
				
				JSONUtils jsonUtils = JSONUtils.getInstance(true);
				jsonUtils.writeTo(responseBody, resp.getOutputStream());
			}
			resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
			resp.setStatus(200);
		}catch(UtilsException | IOException | CoreException e) {
			UploadServlet.log.error("Errore: " + e.getMessage(), e);
			resp.setStatus(500);
		} finally {
			resp.flushBuffer();	
		}
	}

	/**
	Response Ok
		{"files": [
	  {
	    "name": "picture1.jpg",
	    "size": 902604,
	    "url": "http:\/\/example.org\/files\/picture1.jpg",
	    "thumbnailUrl": "http:\/\/example.org\/files\/thumbnail\/picture1.jpg",
	    "deleteUrl": "http:\/\/example.org\/files\/picture1.jpg",
	    "deleteType": "DELETE"
	  },
	  {
	    "name": "picture2.jpg",
	    "size": 841946,
	    "url": "http:\/\/example.org\/files\/picture2.jpg",
	    "thumbnailUrl": "http:\/\/example.org\/files\/thumbnail\/picture2.jpg",
	    "deleteUrl": "http:\/\/example.org\/files\/picture2.jpg",
	    "deleteType": "DELETE"
	  }
	]}
	 */
	public static ObjectNode getUploadOkResponseItem(String itemName, int dimensione, String idFileRicevuto, String deleteURL) throws UtilsException {
		JSONUtils jsonUtils = JSONUtils.getInstance();
		ObjectNode j = jsonUtils.newObjectNode();

		j.put("name", itemName);
		j.put("size", dimensione);
		j.put("url", "");
		j.put("thumbnailUrl", "");
		j.put("id", idFileRicevuto);
		j.put("deleteUrl", deleteURL);
		j.put("deleteType", "DELETE");
		

		return j;
	}
	
	/**
Response Error
{"files": [
  {
    "name": "picture1.jpg",
    "size": 902604,
    "error": "Filetype not allowed"
  },
  {
    "name": "picture2.jpg",
    "size": 841946,
    "error": "Filetype not allowed"
  }
]}
	 */
	public static ObjectNode getUploadKoResponseItem(String itemName, int dimensione, String errorString) throws UtilsException {
		
		JSONUtils jsonUtils = JSONUtils.getInstance();
		ObjectNode j = jsonUtils.newObjectNode();
		
		j.put("name", itemName);
		j.put("size", dimensione);
		j.put("error", errorString);
		

		return j;

	}

	/** risposta delete
	 * {"files": [
  {
    "picture1.jpg": true
  },
  {
    "picture2.jpg": true
  }
]}
	 * 
	 */
	public static ObjectNode getDeleteOkResponseItem(String itemName, String idFileRicevuto, boolean stato) throws UtilsException {
		
		JSONUtils jsonUtils = JSONUtils.getInstance();
		ObjectNode j = jsonUtils.newObjectNode();
		
		j.put("name", itemName);
		j.put("stato", stato);
		j.put("id", idFileRicevuto);
		

		return j;

	}
	
	/** risposta delete
	 * {"files": [
  {
   "error": "Filetype not allowed"
  },
  {
    "error": "Filetype not allowed"
  }
]}
	 * 
	 */
	public static ObjectNode getDeleteKoResponseItem(String errorString) throws UtilsException {
		
		JSONUtils jsonUtils = JSONUtils.getInstance();
		ObjectNode j = jsonUtils.newObjectNode();
		
		j.put("error", errorString);

		return j;

	}

	public static ObjectNode getResponse(List<ObjectNode> itemResp) throws UtilsException {
		
		JSONUtils jsonUtils = JSONUtils.getInstance();
		ObjectNode j = jsonUtils.newObjectNode();
		
		ArrayNode jsonArray = jsonUtils.newArrayNode();
		
		for (ObjectNode item : itemResp) {
			jsonArray.add(item);
		}
		
		j.set("files", jsonArray);

		return j;

	}
}
