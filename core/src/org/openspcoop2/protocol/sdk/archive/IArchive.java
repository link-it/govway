/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.sdk.archive;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;


/**
 * Interfaccia di importazione degli archivi
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IArchive {

	public IProtocolFactory getProtocolFactory();
	
	
	/* ----- Utilita' generali ----- */
	
	/**
	 * Tipi di package gestiti per il protocollo, il valore della tabella contiene l'estensione associata al tipo al momento della generazione del package
	 * 
	 * @return Tipi dei package gestiti per il protocollo
	 * @throws ProtocolException
	 */
	public MappingModeTypesExtensions getMappingTypesExtensions(ArchiveMode mode) throws ProtocolException;
	
	/**
	 * Imposta per ogni portType e operation presente nell'accordo fornito come parametro 
	 * le informazioni di protocollo analizzando i documenti interni agli archivi
	 */
	public void setProtocolInfo(AccordoServizioParteComune accordoServizioParteComune) throws ProtocolException;
	
	
	
	
	/* ----- Import ----- */
	
	/**
	 * Modalita' di importazione degli archivi nella configurazione della Porta di Dominio
	 * 
	 * @return lista di modalita' di importazione
	 * @throws ProtocolException
	 */
	public List<ImportMode> getImportModes() throws ProtocolException;
	
	/**
	 * Converte l'archivio fornito come parametro in un oggetto org.openspcoop2.protocol.sdk.archive.Archive 
	 * 
	 * @param archive bytes dell'archivio
	 * @return org.openspcoop2.protocol.sdk.archive.Archive
	 * @throws ProtocolException
	 */
	public Archive importArchive(byte[]archive,ArchiveMode mode,ArchiveModeType type,
			IRegistryReader registryReader,boolean validationDocuments,
			MapPlaceholder placeholder) throws ProtocolException;
	
	/**
	 * Converte l'archivio fornito come parametro in un oggetto org.openspcoop2.protocol.sdk.archive.Archive 
	 * 
	 * @param archive bytes dell'archivio
	 * @return org.openspcoop2.protocol.sdk.archive.Archive
	 * @throws ProtocolException
	 */
	public Archive importArchive(InputStream archive,ArchiveMode mode,ArchiveModeType type,
			IRegistryReader registryReader,boolean validationDocuments,
			MapPlaceholder placeholder) throws ProtocolException;
	
	/**
	 * Converte l'esito dell'operazione di import di un archivio in una stringa
	 * 
	 * @param esito esito dell'operazione di import
	 * @param archiveMode archiveMode
	 * @return Rappresentazione a Stringa dell'esito dell'importazione
	 */
	public String toString(ArchiveEsitoImport esito, ArchiveMode archiveMode) throws ProtocolException;
	
	/**
	 * Converte l'esito dell'operazione di eliminazione di un archivio in una stringa
	 * 
	 * @param esito esito dell'operazione di eliminazione
	 * @param archiveMode archiveMode
	 * @return Rappresentazione a Stringa dell'esito dell'eliminazione
	 */
	public String toString(ArchiveEsitoDelete esito, ArchiveMode archiveMode) throws ProtocolException;
	
	
	
	
	/* ----- Export ----- */
	
	/**
	 * Modalita' di esportazione degli archivi nella configurazione della Porta di Dominio
	 * 
	 * @param archiveType tipo di archivio da esportare
	 * @return lista di ulteriori opzioni di esportazione
	 * @throws ProtocolException
	 */
	public List<ExportMode> getExportModes(ArchiveType archiveType) throws ProtocolException;
	
	/**
	 * Converte l'archivio org.openspcoop2.protocol.sdk.archive.Archive in un formato binario serializzato nell'output stream 
	 * 
	 * @param archive archivio da esportare
	 * @param out stream su cui deve essere serializzato l'archivio
	 * @throws ProtocolException
	 */
	public void exportArchive(Archive archive, OutputStream out, ArchiveMode mode,
			IRegistryReader registroReader) throws ProtocolException;
		
	/**
	 * Converte l'archivio org.openspcoop2.protocol.sdk.archive.Archive in un formato binario serializzato nell'output stream 
	 * 
	 * @param archive archivio da esportare
	 * @throws ProtocolException
	 */
	public byte[] exportArchive(Archive archive, ArchiveMode mode,
			IRegistryReader registroReader) throws ProtocolException;
	
	
}





