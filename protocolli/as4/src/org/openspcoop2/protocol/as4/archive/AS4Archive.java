/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
 * @author $Author: apoli $
 * @version $Rev: 12566 $, $Date: 2017-01-11 15:21:56 +0100 (Wed, 11 Jan 2017) $
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
