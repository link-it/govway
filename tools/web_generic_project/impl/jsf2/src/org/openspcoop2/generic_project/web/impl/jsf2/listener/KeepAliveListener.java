/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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

package org.openspcoop2.generic_project.web.impl.jsf2.listener;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.log4j.Logger;
import org.openspcoop2.generic_project.web.logging.LoggerManager;

/***
 * 
 * KeepAliveListener
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$ 
 */
public class KeepAliveListener  implements PhaseListener {

    private static final long serialVersionUID = -3689434283608539768L;
    
    @SuppressWarnings("unchecked")
	@Override
	public void afterPhase(PhaseEvent event) {
        if (PhaseId.RESTORE_VIEW.equals(event.getPhaseId())) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            UIViewRoot viewRoot = facesContext.getViewRoot(); 
			Map<String, Object> map = (Map<String, Object>) viewRoot.getAttributes().get("keepAliveMap");

            if (map != null) {
                Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();
                requestMap.putAll(map);
            }
        }
    }

    @Override
	public void beforePhase(PhaseEvent event) {
    }

    @Override
	public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    
    public static void keepAlive(String name, Object object) {
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot(); 
		Map<String, Object> viewRootAttributes = viewRoot.getAttributes();
		@SuppressWarnings("unchecked")
		Map<String, Object> keepAliveMap = (Map<String, Object>) viewRootAttributes.get("keepAliveMap");
		if (keepAliveMap == null) {
			keepAliveMap = new HashMap<String, Object>();
			viewRootAttributes.put("keepAliveMap", keepAliveMap);
		}
		keepAliveMap.put(name, object);
    }
    
    public static void logMsg(String msg){
    	 try {
			Logger log = LoggerManager.getWebGenericProjectLogger();
			log.debug(msg);
			
		} catch (Exception e) {
			return ;
		}
    }
}
