/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
 * SPCoopArchive 
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
		if( AS4Costanti.PMODE_ARCHIVE_MODE_SINGLE_XML.equals(mode) ){
			MappingModeTypesExtensions m = new MappingModeTypesExtensions();
			m.add(AS4Costanti.PMODE_ARCHIVE_EXT_XML, ArchiveType.SOGGETTO, AS4Costanti.PMODE_ARCHIVE_MODE_TYPE_XML);			
			return m;
		}
		else if( AS4Costanti.PMODE_ARCHIVE_MODE_MULTIPLE_ZIP.equals(mode) ){
			MappingModeTypesExtensions m = new MappingModeTypesExtensions();			
			m.add(AS4Costanti.PMODE_ARCHIVE_EXT_ZIP, ArchiveType.SOGGETTO, AS4Costanti.PMODE_ARCHIVE_MODE_TYPE_ZIP);			
			return m;
		}
		else{
			return super.getMappingTypesExtensions(mode);
		}
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
					boolean found = false;
					for (ProtocolProperty pp : azione.getProtocolPropertyList()) {
						if(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID.equals(pp.getName())) {
							if(pp.getValue()==null || "".equals(pp.getValue())) {
								pp.setValue(azione.getNome());
							}
							found = true;
							break;
						}
					}
					if(!found) {
						ProtocolProperty pp = new ProtocolProperty();
						pp.setName(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID);
						pp.setValue(azione.getNome());
						azione.addProtocolProperty(pp);
					}
				}
				for (PortType pt : accordoServizioParteComune.getPortTypeList()) {
					for (Operation azione : pt.getAzioneList()) {
						boolean found = false;
						for (ProtocolProperty pp : azione.getProtocolPropertyList()) {
							if(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID.equals(pp.getName())) {
								if(pp.getValue()==null || "".equals(pp.getValue())) {
									pp.setValue(azione.getNome());
								}
								found = true;
								break;
							}
						}
						if(!found) {
							ProtocolProperty pp = new ProtocolProperty();
							pp.setName(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID);
							pp.setValue(azione.getNome());
							azione.addProtocolProperty(pp);
						}
					}
				}
				
			}
			else {
			
				for (Resource resource : accordoServizioParteComune.getResourceList()) {
					boolean found = false;
					for (ProtocolProperty pp : resource.getProtocolPropertyList()) {
						if(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID.equals(pp.getName())) {
							if(pp.getValue()==null || "".equals(pp.getValue())) {
								pp.setValue(resource.getNome());
							}
							found = true;
							break;
						}
					}
					if(!found) {
						ProtocolProperty pp = new ProtocolProperty();
						pp.setName(AS4ConsoleCostanti.AS4_AZIONE_USER_MESSAGE_COLLABORATION_INFO_ACTION_ID);
						pp.setValue(resource.getNome());
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
			list.add(AS4Costanti.PMODE_EXPORT_ARCHIVE_MODE_MULTIPLE_ZIP);
			list.add(AS4Costanti.PMODE_EXPORT_ARCHIVE_MODE_SINGLE_XML);
			break;
		default:
			break;
		}
		return list;
	}
	
	@Override
	public byte[] exportArchive(Archive archive, ArchiveMode mode,
			IRegistryReader registroReader, IConfigIntegrationReader configIntegrationReader)
			throws ProtocolException {
		
		if(org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_EXPORT_ARCHIVE_MODE.equals(mode)){
			return super.exportArchive(archive, mode, registroReader, configIntegrationReader);
		}
		else if(AS4Costanti.PMODE_EXPORT_ARCHIVE_MODE_SINGLE_XML.equals(mode) ||
				AS4Costanti.PMODE_EXPORT_ARCHIVE_MODE_MULTIPLE_ZIP.equals(mode)){
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
		else if(AS4Costanti.PMODE_EXPORT_ARCHIVE_MODE_SINGLE_XML.equals(mode)){
			if(archive.getSoggetti().size()<=0) {
				throw new ProtocolException("Non risulta selezionato alcun soggetto");
			}
			else if(archive.getSoggetti().size()>1) {
				throw new ProtocolException("Con l'esportazione '"+AS4Costanti.PMODE_EXPORT_ARCHIVE_MODE_SINGLE_XML.getName()+"' deve essere selezionato solamente un soggetto");
			}
			else {
				ArchiveSoggetto soggetto = archive.getSoggetti().get(0);
				this.checkSoggetto(soggetto, registroReader,mode);
				XMLWriteUtils writeUtils = new XMLWriteUtils(this.log, this.protocolFactory, registroReader, configIntegrationReader);
				writeUtils.generate(out, soggetto.getIdSoggetto().getNome());
			}
		}
		else if(AS4Costanti.PMODE_EXPORT_ARCHIVE_MODE_MULTIPLE_ZIP.equals(mode)){
			if(archive.getSoggetti().size()<=0) {
				throw new ProtocolException("Non risulta selezionato alcun soggetto");
			}
			else {
				List<ArchiveSoggetto> listArchive = new ArrayList<>();
				for (int i = 0; i < archive.getSoggetti().size(); i++) {
					ArchiveSoggetto soggetto = archive.getSoggetti().get(i);
					this.checkSoggetto(soggetto, registroReader,mode);
					listArchive.add(soggetto);
				}
				ZIPWriteUtils zipUtils = new ZIPWriteUtils(this.log, this.protocolFactory, registroReader, configIntegrationReader);
				zipUtils.generate(out, listArchive);
			}
		}
		else{
			throw new ProtocolException("Mode ["+mode+"] unknown");
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
