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

import java.util.List;

import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.protocol.sdk.archive.MapPlaceholder;

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

	protected String filePath;
	protected String protocollo;
	protected String importMode;
	protected String importType;
	
	protected String importInformationMissingObjectId = null;
	protected String importInformationMissingCollectionFilePath = null;
	
	// Campi per lo specifico missingObjectId in corso
	protected Class<?> importInformationMissing_classObject = null;
	protected Object importInformationMissing_object = null;
	protected String importInformationMissing_soggettoInput = null;
	protected String importInformationMissing_versioneInput = null;
	protected List<PortType> importInformationMissing_portTypes = null;
	protected String importInformationMissing_modalitaAcquisizioneInformazioniProtocollo = null;
	protected String importInformationMissing_portTypeImplementedInput = null;
	protected String importInformationMissing_accordoServizioParteComuneInput = null;
	protected String importInformationMissing_accordoCooperazioneInput = null;
	protected InvocazioneServizio importInformationMissing_invocazioneServizio = null;
	protected Connettore importInformationMissing_connettore = null;
	protected Credenziali importInformationMissing_credenziali = null;
	
	protected MapPlaceholder importInformationMissing_globalPlaceholder = new MapPlaceholder();
	
	protected boolean validazioneDocumenti = true;
	protected boolean updateEnabled = false;
	protected boolean importDeletePolicyConfig = false;
	protected boolean importDeletePluginConfig = false;
	protected boolean importConfig = false;

	protected int step = 0;
	
}
