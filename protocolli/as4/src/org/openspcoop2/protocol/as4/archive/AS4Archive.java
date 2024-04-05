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

package org.openspcoop2.protocol.as4.archive;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.protocol.as4.constants.AS4ConsoleCostanti;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.basic.archive.BasicArchive;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.archive.ExportMode;
import org.openspcoop2.protocol.sdk.archive.MappingModeTypesExtensions;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;

/**
 * AS4Archive 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4Archive extends BasicArchive {


	public AS4Archive(IProtocolFactory<?> protocolFactory) throws ProtocolException {
		super(protocolFactory);
	}

	
	

	/* ----- Utilita' generali ----- */
		
	@Override
	public MappingModeTypesExtensions getMappingTypesExtensions(ArchiveMode mode)
			throws ProtocolException {
		
		return this._getMappingTypesExtensions(mode, null, null);
	}
	
	
	/**
	 * Imposta per ogni portType e operation presente nell'accordo fornito come parametro 
	 * le informazioni di protocollo analizzando i documenti interni agli archivi
	 */
	@Override
	public void setProtocolInfo(AccordoServizioParteComune accordoServizioParteComune) throws ProtocolException{
		
		super.setProtocolInfo(accordoServizioParteComune);
		
		try{
			if(ServiceBinding.SOAP.equals(accordoServizioParteComune.getServiceBinding())) {
				
				for (Azione azione : accordoServizioParteComune.getAzioneList()) {
					
					// forzo utilizzo oneway (unico supportato)
					azione.setProfiloCollaborazione(ProfiloCollaborazione.ONEWAY);
					
					boolean found = false;
					boolean foundBinding = false;
					for (ProtocolProperty pp : azione.getProtocolPropertyList()) {
						if(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID.equals(pp.getName())) {
							if(pp.getValue()==null || "".equals(pp.getValue())) {
								pp.setValue(azione.getNome());
							}
							found = true;
							break;
						}
						if(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_ID.equals(pp.getName())) {
							if(pp.getValue()==null || "".equals(pp.getValue())) {
								pp.setValue(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_DEFAULT_VALUE);
							}
							foundBinding = true;
							break;
						}
					}
					if(!found) {
						ProtocolProperty pp = new ProtocolProperty();
						pp.setName(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID);
						pp.setValue(azione.getNome());
						azione.addProtocolProperty(pp);
					}
					if(!foundBinding) {
						ProtocolProperty pp = new ProtocolProperty();
						pp.setName(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_ID);
						pp.setValue(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_DEFAULT_VALUE);
						azione.addProtocolProperty(pp);
					}
				}
				for (PortType pt : accordoServizioParteComune.getPortTypeList()) {
					
					// forzo utilizzo oneway (unico supportato)
					pt.setProfiloCollaborazione(ProfiloCollaborazione.ONEWAY);
					
					for (Operation azione : pt.getAzioneList()) {
						
						// forzo utilizzo oneway (unico supportato)
						azione.setProfiloCollaborazione(ProfiloCollaborazione.ONEWAY);
						
						boolean found = false;
						boolean foundBinding = false;
						for (ProtocolProperty pp : azione.getProtocolPropertyList()) {
							if(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID.equals(pp.getName())) {
								if(pp.getValue()==null || "".equals(pp.getValue())) {
									pp.setValue(azione.getNome());
								}
								found = true;
								break;
							}
							if(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_ID.equals(pp.getName())) {
								if(pp.getValue()==null || "".equals(pp.getValue())) {
									pp.setValue(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_DEFAULT_VALUE);
								}
								foundBinding = true;
								break;
							}
						}
						if(!found) {
							ProtocolProperty pp = new ProtocolProperty();
							pp.setName(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID);
							pp.setValue(azione.getNome());
							azione.addProtocolProperty(pp);
						}
						if(!foundBinding) {
							ProtocolProperty pp = new ProtocolProperty();
							pp.setName(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_ID);
							pp.setValue(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_DEFAULT_VALUE);
							azione.addProtocolProperty(pp);
						}
					}
				}
				
			}
			else {
			
				for (Resource resource : accordoServizioParteComune.getResourceList()) {
					boolean found = false;
					boolean foundBinding = false;
					for (ProtocolProperty pp : resource.getProtocolPropertyList()) {
						if(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID.equals(pp.getName())) {
							if(pp.getValue()==null || "".equals(pp.getValue())) {
								pp.setValue(resource.getNome());
							}
							found = true;
							break;
						}
						if(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_ID.equals(pp.getName())) {
							if(pp.getValue()==null || "".equals(pp.getValue())) {
								pp.setValue(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_DEFAULT_VALUE);
							}
							foundBinding = true;
							break;
						}
					}
					if(!found) {
						ProtocolProperty pp = new ProtocolProperty();
						pp.setName(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID);
						pp.setValue(resource.getNome());
						resource.addProtocolProperty(pp);
					}
					if(!foundBinding) {
						ProtocolProperty pp = new ProtocolProperty();
						pp.setName(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_ID);
						pp.setValue(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_BINDING_DEFAULT_VALUE);
						resource.addProtocolProperty(pp);
					}
				}
				
			}
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
		
	
	
	/* ----- Export ----- */
	
	@Override
	public List<ExportMode> getExportModes(ArchiveType archiveType) throws ProtocolException {
		List<ExportMode> list = super.getExportModes(archiveType);
		switch (archiveType) {
		case SOGGETTO:
			ExportMode exportPMode = new ExportMode(AS4Costanti.EXPORT_MODE_DOMIBUS_FROM_SOGGETTI);
			list.add(exportPMode);
			break;
		case CONFIGURAZIONE:
		case ALL:
		case ALL_WITHOUT_CONFIGURAZIONE:
			ExportMode exportPModeConfig = new ExportMode(AS4Costanti.EXPORT_MODE_DOMIBUS_FROM_CONFIG);
			list.add(exportPModeConfig);
			break;
		default:
			break;
		}
		return list;
	}
	
	@Override
	public MappingModeTypesExtensions getExportMappingTypesExtensions(Archive archive, ArchiveMode mode,
			IRegistryReader registroReader, IConfigIntegrationReader configIntegrationReader) throws ProtocolException{
		return this._getMappingTypesExtensions(mode, archive, registroReader);
	}
	
	@Override
	public byte[] exportArchive(Archive archive, ArchiveMode mode,
			IRegistryReader registroReader, IConfigIntegrationReader configIntegrationReader)
			throws ProtocolException {
		
		if(org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_EXPORT_ARCHIVE_MODE.equals(mode)){
			return super.exportArchive(archive, mode, registroReader, configIntegrationReader);
		}
		else if(AS4Costanti.EXPORT_MODE_DOMIBUS_FROM_SOGGETTI.equals(mode) || 
				AS4Costanti.EXPORT_MODE_DOMIBUS_FROM_CONFIG.equals(mode) ){
			try {
				ByteArrayOutputStream bot = new ByteArrayOutputStream();
				this.exportArchive(archive, bot, mode, registroReader, configIntegrationReader);
				bot.flush();
				bot.close();
				return bot.toByteArray();
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		else{
			throw new ProtocolException("Mode ["+mode+"] unknown");
		}	
		
	}

	@Override
	public void exportArchive(Archive archive, OutputStream out, ArchiveMode mode,
			IRegistryReader registroReader, IConfigIntegrationReader configIntegrationReader)
			throws ProtocolException {
		if(org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_EXPORT_ARCHIVE_MODE.equals(mode)){
			super.exportArchive(archive, out, mode, registroReader, configIntegrationReader);
		}
		else if(AS4Costanti.EXPORT_MODE_DOMIBUS_FROM_SOGGETTI.equals(mode)){
			if(archive.getSoggetti().size()<=0) {
				throw new ProtocolException("Non risulta selezionato alcun soggetto");
			}
			
			List<ArchiveSoggetto> listArchive = new ArrayList<>();
			for (int i = 0; i < archive.getSoggetti().size(); i++) {
				ArchiveSoggetto soggetto = archive.getSoggetti().get(i);
				this.checkSoggetto(soggetto, registroReader,mode);
				listArchive.add(soggetto);
			}
			
			if(listArchive.size()>1) {
				ZIPWriteUtils zipUtils = new ZIPWriteUtils(this.log, this.protocolFactory, registroReader, configIntegrationReader);
				zipUtils.generate(out, listArchive);
			}
			else {
				XMLWriteUtils writeUtils = new XMLWriteUtils(this.log, this.protocolFactory, registroReader, configIntegrationReader);
				writeUtils.generate(out, listArchive.get(0).getIdSoggetto().getNome());
			}

		}
		else if(AS4Costanti.EXPORT_MODE_DOMIBUS_FROM_CONFIG.equals(mode)){
			if(archive.getSoggetti().size()<=0) {
				throw new ProtocolException("Non risultano configurati soggetti");
			}
			
			List<ArchiveSoggetto> listArchive = this._listSoggettiOperativi(archive, registroReader);
			
			if(listArchive.size()>1) {
				ZIPWriteUtils zipUtils = new ZIPWriteUtils(this.log, this.protocolFactory, registroReader, configIntegrationReader);
				zipUtils.generate(out, listArchive);
			}
			else {
				XMLWriteUtils writeUtils = new XMLWriteUtils(this.log, this.protocolFactory, registroReader, configIntegrationReader);
				writeUtils.generate(out, listArchive.get(0).getIdSoggetto().getNome());
			}
			
		}
		else{
			throw new ProtocolException("Mode ["+mode+"] unknown");
		}	
	}

	
	
	
	
	/* ----- Utilita' interne ----- */
	
	private List<ArchiveSoggetto> _listSoggettiOperativi(Archive archive, IRegistryReader registryReader) throws ProtocolException{
		List<ArchiveSoggetto> listArchive = new ArrayList<>();
		if(archive!=null && archive.getSoggetti()!=null && archive.getSoggetti().size()>0) {
			
			List<String> listPddOperative = null;
			try {
				listPddOperative = registryReader.findIdPorteDominio(true);
			}catch(Exception notFound) {
				throw new ProtocolException("Non risultano configurate porte di dominio di tipo 'operativo'"); // non dovrebbe succedere
			}
			
			for (int i = 0; i < archive.getSoggetti().size(); i++) {
				ArchiveSoggetto archiveSoggetto = archive.getSoggetti().get(i);
				boolean soggettoOperativo = false;
				if(archiveSoggetto.getSoggettoRegistro().getPortaDominio()!=null) {
					soggettoOperativo = listPddOperative.contains(archiveSoggetto.getSoggettoRegistro().getPortaDominio());
				}
				if(soggettoOperativo) {
					listArchive.add(archiveSoggetto);
				}
			}
		}
		return listArchive;
	}
	
	private MappingModeTypesExtensions _getMappingTypesExtensions(ArchiveMode mode, Archive archive, IRegistryReader registryReader) throws ProtocolException{
		if(AS4Costanti.DOMIBUS_MODE.equals(mode) || 
				AS4Costanti.EXPORT_MODE_DOMIBUS_FROM_SOGGETTI.equals(mode) || 
				AS4Costanti.EXPORT_MODE_DOMIBUS_FROM_CONFIG.equals(mode) ){

			MappingModeTypesExtensions m = new MappingModeTypesExtensions();
							
			List<ArchiveSoggetto> listSoggetti = null;
			if(AS4Costanti.EXPORT_MODE_DOMIBUS_FROM_CONFIG.equals(mode)) {
				listSoggetti = this._listSoggettiOperativi(archive, registryReader);
			}
			
			// xml
			boolean preferExtXmlSingleObject = false;
			if(AS4Costanti.EXPORT_MODE_DOMIBUS_FROM_SOGGETTI.equals(mode)) {
				preferExtXmlSingleObject = true;
			}
			else if(AS4Costanti.EXPORT_MODE_DOMIBUS_FROM_CONFIG.equals(mode) && listSoggetti!=null && listSoggetti.size()==1) {
				preferExtXmlSingleObject = true;
			}
			m.add(AS4Costanti.PMODE_ARCHIVE_EXT_XML, 
					preferExtXmlSingleObject,
					AS4Costanti.PMODE_ARCHIVE_MODE_TYPE_XML);
			
			// zip
			boolean preferExtZipSingleObject = false;
			 if(AS4Costanti.EXPORT_MODE_DOMIBUS_FROM_CONFIG.equals(mode) && listSoggetti!=null && listSoggetti.size()>1) {
				preferExtZipSingleObject = true;
			}
			m.add(AS4Costanti.PMODE_ARCHIVE_EXT_ZIP, 
					preferExtZipSingleObject,
					AS4Costanti.PMODE_ARCHIVE_MODE_TYPE_ZIP);
			
			return m;
		}
		else{
			return super.getMappingTypesExtensions(mode);
		}
	}
	
	private void checkSoggetto(ArchiveSoggetto soggetto, IRegistryReader registroReader, ArchiveMode mode) throws ProtocolException {
		if(soggetto.getSoggettoRegistro().getPortaDominio()==null) {
			throw new ProtocolException("Con l'esportazione '"+mode+"' deve essere selezionato un soggetto di dominio 'interno'; il soggetto "+
					soggetto.getSoggettoRegistro().getNome()+" risulta di dominio 'esterno'");
		}
		else {
			List<String> pddOperative = null;
			try {
				pddOperative = registroReader.findIdPorteDominio(true);
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			if(pddOperative.contains(soggetto.getSoggettoRegistro().getPortaDominio())==false) {
				throw new ProtocolException("Con l'esportazione '"+mode+"' deve essere selezionato un soggetto di dominio 'interno'; il soggetto "+
					soggetto.getSoggettoRegistro().getNome()+" risulta di dominio 'esterno'");
			}
		}
	}
	

	
}
