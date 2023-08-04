/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.web.ctrlstat.servlet.archivi;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.archive.MapPlaceholder;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ImporterStrutsBean
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ImporterStrutsBean {

	private static final String IMPORT_MAP_PLACE_HOLDER = Costanti.CONSOLE_ATTRIBUTO_TAB_SESSION_KEY_PREFIX + "importMap";
	private static final String IMPORT_STEP = Costanti.CONSOLE_ATTRIBUTO_TAB_SESSION_KEY_PREFIX + "step";
	
	public ImporterStrutsBean(HttpServletRequest request, HttpSession session, boolean newInstance) {
		if(!newInstance && session!=null) {
			this.importInformationMissingGlobalPlaceholder = ServletUtils.getObjectFromSession(request, session, MapPlaceholder.class, IMPORT_MAP_PLACE_HOLDER);
			this.stepCounter = ServletUtils.getObjectFromSession(request, session, StepCounter.class, IMPORT_STEP);
		}
		else {
			this.importInformationMissingGlobalPlaceholder = new MapPlaceholder();
			ServletUtils.setObjectIntoSession(request, session,
					this.importInformationMissingGlobalPlaceholder,
					IMPORT_MAP_PLACE_HOLDER);
			
			this.stepCounter = new StepCounter(); 
			ServletUtils.setObjectIntoSession(request, session,
					this.stepCounter,
					IMPORT_STEP);
		}
	}
	
	protected String filePath;
	protected String protocollo;
	protected String importMode;
	protected String importType;
	
	protected String importInformationMissingObjectId = null;
	protected String importInformationMissingCollectionFilePath = null;
	
	// Campi per lo specifico missingObjectId in corso
	protected Class<?> importInformationMissingClassObject = null;
	protected Object importInformationMissingObject = null;
	protected String importInformationMissingSoggettoInput = null;
	protected String importInformationMissingVersioneInput = null;
	protected List<PortType> importInformationMissingPortTypes = null;
	protected String importInformationMissingModalitaAcquisizioneInformazioniProtocollo = null;
	protected String importInformationMissingPortTypeImplementedInput = null;
	protected String importInformationMissingAccordoServizioParteComuneInput = null;
	protected String importInformationMissingAccordoCooperazioneInput = null;
	protected InvocazioneServizio importInformationMissingInvocazioneServizio = null;
	protected Connettore importInformationMissingConnettore = null;
	protected Credenziali importInformationMissingCredenziali = null;
	
	protected boolean validazioneDocumenti = true;
	protected boolean updateEnabled = false;
	protected boolean importDeletePolicyConfig = false;
	protected boolean importDeletePluginConfig = false;
	protected boolean importConfig = false;
	
	// Variabili in sessione
	protected MapPlaceholder importInformationMissingGlobalPlaceholder = new MapPlaceholder();
	protected StepCounter stepCounter = null;

}

class StepCounter implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int step = 0;

	public int getStep() {
		return this.step;
	}

	public void increment() {
		this.step++;
	}
}
