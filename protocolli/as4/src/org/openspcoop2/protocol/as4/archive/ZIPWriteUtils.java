/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.basic.archive.ZIPUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.slf4j.Logger;

/**
 * XMLWriteUtils 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ZIPWriteUtils {

	protected Logger log = null;
	protected IProtocolFactory<?> protocolFactory;	
	protected IRegistryReader registryReader;
	protected IConfigIntegrationReader configIntegrationReader;
	protected XMLWriteUtils xmlWriteUtils;
	
	public ZIPWriteUtils(Logger log,IProtocolFactory<?> protocolFactory,IRegistryReader registryReader,IConfigIntegrationReader configIntegrationReader) throws ProtocolException {
		this.log = log;
		this.protocolFactory = protocolFactory;
		
		this.registryReader = registryReader;
		this.configIntegrationReader = configIntegrationReader;
		
		this.xmlWriteUtils = new XMLWriteUtils(log, protocolFactory, registryReader, configIntegrationReader);
	}

	
	public byte[] generate(List<ArchiveSoggetto> soggetti) throws ProtocolException {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.generate(bout, soggetti);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public void generate(OutputStream out,List<ArchiveSoggetto> soggetti) throws ProtocolException {
		ZipOutputStream zipOut = null;
		try {
			zipOut = new ZipOutputStream(out);

			String rootPackageDir = "";
			// Il codice dopo fissa il problema di inserire una directory nel package.
			// Commentare la riga sotto per ripristinare il vecchio comportamento.
			rootPackageDir = AS4Costanti.PMODE_ARCHIVE_ROOT_DIR+File.separatorChar;
			
			for (ArchiveSoggetto archiveSoggetto : soggetti) {
				String nomeSoggetto = archiveSoggetto.getIdSoggetto().getNome();
				String nomeFile = ZIPUtils.convertNameToSistemaOperativoCompatible(nomeSoggetto)+"."+AS4Costanti.PMODE_ARCHIVE_EXT_XML;
				zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
				byte[]pmode = this.xmlWriteUtils.generate(nomeSoggetto);
				zipOut.write(pmode);
			}
			
			zipOut.flush();

		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}finally{
			try{
				if(zipOut!=null)
					zipOut.close();
			}catch(Exception eClose){}
		}
	}
	
	

	
}
