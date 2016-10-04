/*
 * OpenSPCoop - Customizable API Gateway 
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



package org.openspcoop2.core.registry.driver.web;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiXMLRepository;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.utils.xml.JiBXUtils;

/**
 * Classe utilizzata per creare/modificare/eliminare entita XML di un registro dei servizi OpenSPCoop.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLLib{

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** --- Location del repository dove memorizzare i file XML associate alle entita create nel registro UDDI ---- */
	private String pathPrefix;
	/** --- URL Prefix utilizzato come prefisso da associare alle url memorizzate nelle entita create nell'UDDI
	e contenenti le definizioni XML ------*/
	private String urlPrefix;
	
	// Factory
	private IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();

	private org.openspcoop2.core.registry.utils.CleanerOpenSPCoop2Extensions cleanerOpenSPCoop2ExtensionsRegistry = null;


	/**
	 * Costruttore.
	 *
	 * @param path Contiene il path del repository necessario al registro dei servizi
	 * @param url URL Prefix utilizzato come prefisso da associare alle url memorizzate per i WSDL
	 */    
	public XMLLib(String path,String url) throws DriverRegistroServiziException{

		this.cleanerOpenSPCoop2ExtensionsRegistry = new org.openspcoop2.core.registry.utils.CleanerOpenSPCoop2Extensions();
		
		if(path!=null){
			if (!path.endsWith(File.separator))
				this.pathPrefix= path + File.separator;
			else
				this.pathPrefix = path;
		}
		
		if(url!=null){
			if (!url.endsWith(CostantiXMLRepository.URL_SEPARATOR))
				this.urlPrefix= url + CostantiXMLRepository.URL_SEPARATOR;
			else
				this.urlPrefix = url;
		}
		
		if(path!=null){
			try {

				// Creazione/Controllo Esistenza directory accordo
				File dir = new File(this.pathPrefix+CostantiXMLRepository.ACCORDI_DI_SERVIZIO);
				if(dir.exists() == false){
					if(dir.mkdir()==false){
						throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la creazione della directory per gli accordi di servizio ["+
								this.pathPrefix+CostantiXMLRepository.ACCORDI_DI_SERVIZIO+"]");
					}
				}

			}catch(DriverRegistroServiziException e){
				throw e;
			}catch(Exception io){
				throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la creazione della directory per gli accordi di servizio ["+
						this.pathPrefix+CostantiXMLRepository.ACCORDI_DI_SERVIZIO+"]: "+io.getMessage());
			}	
			
			
			try {

				// Creazione/Controllo Esistenza directory accordo cooperazione
				File dir = new File(this.pathPrefix+CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE);
				if(dir.exists() == false){
					if(dir.mkdir()==false){
						throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la creazione della directory per gli accordi di cooperazione ["+
								this.pathPrefix+CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE+"]");
					}
				}

			}catch(DriverRegistroServiziException e){
				throw e;
			}catch(Exception io){
				throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la creazione della directory per gli accordi di servizio ["+
						this.pathPrefix+CostantiXMLRepository.ACCORDI_DI_SERVIZIO+"]: "+io.getMessage());
			}	
			
			
			try {
				
				// Creazione/Controllo Esistenza directory porte di dominio
				File dirPDD = new File(this.pathPrefix+CostantiXMLRepository.PORTE_DI_DOMINIO);
				if(dirPDD.exists() == false){
					if(dirPDD.mkdir()==false){
						throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la creazione della directory per le porte di dominio ["+
								this.pathPrefix+CostantiXMLRepository.PORTE_DI_DOMINIO+"]");
					}
				}

			}catch(DriverRegistroServiziException e){
				throw e;
			}catch(Exception io){
				throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la creazione della directory per le porte di dominio ["+
						this.pathPrefix+CostantiXMLRepository.PORTE_DI_DOMINIO+"]: "+io.getMessage());
			}	
		}
	}














	/* ---------------- GENERAZIONE XML DA/A FILE ------------------------- */

	/**
	 * Metodo che si occupa della generazione di un file a partire da un array di byte 
	 * Sono richiesti interattivamente i parametri che identificano il file da generare e l'array di byte. 
	 * 
	 * @param date Array di Byte da trasformare in file.
	 * @param fileName Nome del File
	 */   
	public void generaFile(byte[]date, String fileName) throws DriverRegistroServiziException{

		FileOutputStream fos = null;
		try{
			File file = new File(fileName);
			if(file.exists()){
				file.delete();
			}  

			fos =new FileOutputStream(fileName);
			fos.write(date);
			fos.close();

		}catch(Exception e){
			try{
				if( fos != null )
					fos.close();
			} catch(Exception er) {}
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante il salvataggio del file ("+fileName+"): "+e.getMessage(),e);
		}
	} 


	/**
	 * Legge un indice dei servizi di un soggetto
	 * 
	 * @param directoryServiziSoggetto
	 * @return L'indice dei servizi di un soggetto
	 * @throws DriverRegistroServiziException
	 */
	public String getContenutoIndexServizi( String directoryServiziSoggetto ) throws DriverRegistroServiziException{
		String indexServizi = directoryServiziSoggetto;
		if(directoryServiziSoggetto.endsWith(File.separator)==false){
			indexServizi = directoryServiziSoggetto + File.separator;
		}
		indexServizi = indexServizi + CostantiXMLRepository.INDEX_SERVIZI;
		BufferedReader bf = null;
		FileReader fr = null;
		try{

			File f = new File(indexServizi);
			StringBuffer contenuto = new StringBuffer();
			if(f.exists()){
				if(f.isFile()==false){
					throw new Exception("Non e' un file regolare");
				}
				fr = new FileReader(f);
				bf = new BufferedReader(fr);
				char[]buffer = new char[1024];
				int byteLetti = -1;
				while( (byteLetti = fr.read(buffer))!=-1){
					contenuto.append(buffer,0,byteLetti);	
				}
			}

			if(contenuto.length()>0)
				return contenuto.toString();
			else
				return null;

		}catch(Exception e){
			try{
				if( fr != null )
					fr.close();
			} catch(Exception er) {}
			try{
				if( bf != null )
					bf.close();
			} catch(Exception er) {}
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la elaborazione del file di index ("+indexServizi+"): "+e.getMessage(),e);
		}
	}

	/**
	 * Genera l'indice dei servizi
	 * 
	 * @param directoryServiziSoggetto
	 * @param contenuto
	 * @throws DriverRegistroServiziException
	 */
	private void setContenutoIndexServizi( String directoryServiziSoggetto , String contenuto ) throws DriverRegistroServiziException{

		// index name
		String indexServizi = directoryServiziSoggetto;
		if(directoryServiziSoggetto.endsWith(File.separator)==false){
			indexServizi = directoryServiziSoggetto + File.separator;
		}
		indexServizi = indexServizi + CostantiXMLRepository.INDEX_SERVIZI;

		if(contenuto==null)
			throw new DriverRegistroServiziException("Contenuto da inserire nell'indice dei servizi is null");

		// intestazione
		String content = null;
		if( contenuto.startsWith(CostantiXMLRepository.INDEX_SERVIZI_MANIFEST)==false ){
			content = CostantiXMLRepository.INDEX_SERVIZI_MANIFEST + contenuto;
		}else{
			content = contenuto;
		}
		FileOutputStream fos = null;
		try{

			File f = new File(indexServizi);
			if(f.exists()){
				f.delete();
			}  

			fos = new FileOutputStream(f);
			fos.write(content.getBytes());
			fos.close();

		}catch(Exception e){
			try{
				if( fos != null )
					fos.close();
			} catch(Exception er) {}
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante l'impostazione del file di index ("+indexServizi+"): "+e.getMessage(),e);
		}
	}
	private void setContenutoIndexServizi(String directoryServiziSoggetto,IDServizio[]index)throws DriverRegistroServiziException {

		// genero stringa
		StringBuffer bf = new StringBuffer();
		for(int i=0; i<index.length; i++){
			bf.append(this.generaIndexServiziLine(index[i]));
		}
		this.setContenutoIndexServizi(directoryServiziSoggetto, bf.toString());
	}

	/**
	 * Genera la linea di un servizio nell'indice
	 * @return linea di un servizio nell'indice
	 */
	private String generaIndexServiziLine(IDServizio idS) throws DriverRegistroServiziException{
		if(idS.getUriAccordo()==null){
			throw new DriverRegistroServiziException("[generaIndexServiziLine] uri accordo is null");
		}
		if(idS.getTipoServizio()==null){
			throw new DriverRegistroServiziException("[generaIndexServiziLine] tipo Servizio is null");
		}
		if(idS.getServizio()==null){
			throw new DriverRegistroServiziException("[generaIndexServiziLine] nome Servizio is null");
		}
		if(idS.getSoggettoErogatore()==null){
			throw new DriverRegistroServiziException("[generaIndexServiziLine] soggetto Erogatore is null");
		}
		if(idS.getSoggettoErogatore().getTipo()==null){
			throw new DriverRegistroServiziException("[generaIndexServiziLine] tipo soggetto Erogatore is null");
		}
		if(idS.getSoggettoErogatore().getNome()==null){
			throw new DriverRegistroServiziException("[generaIndexServiziLine] nome soggetto Erogatore is null");
		}
		return idS.getUriAccordo() +CostantiXMLRepository.INDEX_SERVIZI_SEPARATORE+
			idS.getSoggettoErogatore().getTipo()+"/"+idS.getSoggettoErogatore().getNome()+CostantiXMLRepository.INDEX_SERVIZI_SEPARATORE+
			idS.getTipoServizio()+"/"+idS.getServizio()+CostantiXMLRepository.INDEX_SERVIZI_SEPARATORE+
			"tipologiaServizio("+idS.getTipologiaServizio()+")"+"\n";
	}

	private boolean existsIndexServizi(String directoryServiziSoggetto){

		String indexServizi = directoryServiziSoggetto;
		if(directoryServiziSoggetto.endsWith(File.separator)==false){
			indexServizi = directoryServiziSoggetto + File.separator;
		}
		indexServizi = indexServizi + CostantiXMLRepository.INDEX_SERVIZI;

		File f = new File(indexServizi);
		return f.exists();
	}
	
	private boolean isIndexServizi(File file)throws DriverRegistroServiziException{

		FileInputStream fin = null;
		ByteArrayOutputStream bout = null;
		try{
			fin = new FileInputStream(file);
			byte[] readB = new byte[1024];
			int letti =0;
			bout = new ByteArrayOutputStream();
			while( (letti=fin.read(readB))!=-1 ){
				bout.write(readB,0,letti);
			}
			fin.close();
			if(bout.size()>0 && bout.toString()!=null && bout.toString().startsWith(CostantiXMLRepository.INDEX_SERVIZI_MANIFEST))
				return true;
			else
				return false;
		}catch(Exception e){
			try{
				if( fin != null )
					fin.close();
			} catch(Exception er) {}
			try{
				if( bout != null )
					bout.close();
			} catch(Exception er) {}
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante il check del file di index ("+file.getName()+"): "+e.getMessage(),e);
		}
	}

	private void deleteIndexServizi(String directoryServiziSoggetto)throws DriverRegistroServiziException{

		String indexServizi = directoryServiziSoggetto;
		if(directoryServiziSoggetto.endsWith(File.separator)==false){
			indexServizi = directoryServiziSoggetto + File.separator;
		}
		indexServizi = indexServizi + CostantiXMLRepository.INDEX_SERVIZI;
		try{
			File f = new File(indexServizi);
			if(f.exists()){
				if (f.delete()==false){
					throw new DriverRegistroServiziException("Eliminazione file di index ["+indexServizi+"] non riuscita");
				}
			}
		}catch(DriverRegistroServiziException de){
			throw de;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante l'eliminazione del file di index ("+indexServizi+"): "+e.getMessage(),e);
		}
	}

	/**
	 * Legge un index dei servizi
	 * 
	 * @param contenuto
	 * @return La lettura di un indice dei servizi come array di IDServizio
	 */
	public static IDServizio[] mappingIndexServizi(String contenuto)throws DriverRegistroServiziException{
		XMLLib xmlLib = new XMLLib(null,null);
		return xmlLib.readIndexServizi(contenuto);
	}
	
	/**
	 * Legge un index dei servizi
	 * 
	 * @param directoryServiziSoggetto
	 * @return indice dei servizi
	 */
	private IDServizio[] readIndexServizi(String contenuto)throws DriverRegistroServiziException{

		try{

			if(contenuto.toString().startsWith(CostantiXMLRepository.INDEX_SERVIZI_MANIFEST)==false){
				throw new Exception("Non e' un file index servizi del registro dei servizi di OpenSPCoop");
			}

			
			String cont = contenuto.replace(CostantiXMLRepository.INDEX_SERVIZI_MANIFEST,"");
			StringTokenizer st = new StringTokenizer(cont,"\n");
			Vector<IDServizio> ids = new Vector<IDServizio>();
			while(st.hasMoreTokens()){
				String line = null;
				try{
					line = st.nextToken();
				}catch(Exception e){
					break;
				}

				
				byte condizioneUscita = 0;
				if( (line!=null) && (line.length()>0) && (line.getBytes()[0]==condizioneUscita) )
					break;

				String [] splitLine = line.split(CostantiXMLRepository.INDEX_SERVIZI_SEPARATORE);
				if(splitLine==null){
					throw new Exception("Non e' un file index servizi del registro dei servizi di OpenSPCoop (read line '"+CostantiXMLRepository.INDEX_SERVIZI_SEPARATORE+"') ["+line+"]");
				}else if(splitLine.length!=4){
					throw new Exception("Non e' un file index servizi del registro dei servizi di OpenSPCoop (read line '"+CostantiXMLRepository.INDEX_SERVIZI_SEPARATORE+"') ["+line+"][lenght:"+splitLine.length+"]");
				}else if(splitLine[0]==null){
					throw new Exception("Non e' un file index servizi del registro dei servizi di OpenSPCoop (read line '"+CostantiXMLRepository.INDEX_SERVIZI_SEPARATORE+"') ["+line+"][split[0]:"+splitLine[0]+"]");
				}else if(splitLine[1]==null){
					throw new Exception("Non e' un file index servizi del registro dei servizi di OpenSPCoop (read line '"+CostantiXMLRepository.INDEX_SERVIZI_SEPARATORE+"') ["+line+"][split[1]:"+splitLine[1]+"]");
				}else if(splitLine[2]==null){
					throw new Exception("Non e' un file index servizi del registro dei servizi di OpenSPCoop (read line '"+CostantiXMLRepository.INDEX_SERVIZI_SEPARATORE+"') ["+line+"][split[2]:"+splitLine[2]+"]");
				}else if(splitLine[3]==null){
					throw new Exception("Non e' un file index servizi del registro dei servizi di OpenSPCoop (read line '"+CostantiXMLRepository.INDEX_SERVIZI_SEPARATORE+"') ["+line+"][split[3]:"+splitLine[3]+"]");
				}else if(splitLine[1].indexOf("/")==-1){
					throw new Exception("Non e' un file index servizi del registro dei servizi di OpenSPCoop (read line '"+CostantiXMLRepository.INDEX_SERVIZI_SEPARATORE+"') '/' ["+line+"][split[1]:"+splitLine[1]+"]");
				}else if(splitLine[2].indexOf("/")==-1){
					throw new Exception("Non e' un file index servizi del registro dei servizi di OpenSPCoop (read line '"+CostantiXMLRepository.INDEX_SERVIZI_SEPARATORE+"') '/' ["+line+"][split[2]:"+splitLine[2]+"]");
				}else if( (splitLine[3].equals("tipologiaServizio(normale)")==false) && (splitLine[3].equals("tipologiaServizio(correlato)")==false) ){
					throw new Exception("Non e' un file index servizi del registro dei servizi di OpenSPCoop (read line '"+CostantiXMLRepository.INDEX_SERVIZI_SEPARATORE+"') 'tipologiaServizio' ["+line+"][split[3]:"+splitLine[3]+"]");
				}

				IDServizio idServ = new IDServizio();
				// uri accordo
				idServ.setUriAccordo(splitLine[0]);
				// soggetto Erogatore
				String [] splitSoggetto = splitLine[1].split("/");
				if(splitSoggetto==null || splitSoggetto.length!=2 || splitSoggetto[0]==null || splitSoggetto[1]==null){
					throw new Exception("Non e' un file index servizi del registro dei servizi di OpenSPCoop (read soggetto ':') ["+splitLine[1]+"]");
				}
				idServ.setSoggettoErogatore(splitSoggetto[0], splitSoggetto[1]);
				// servizio
				String [] splitServizio = splitLine[2].split("/");
				if(splitServizio==null || splitServizio.length!=2 || splitServizio[0]==null || splitServizio[1]==null){
					throw new Exception("Non e' un file index servizi del registro dei servizi di OpenSPCoop (read servizio ':') ["+splitLine[2]+"]");
				}
				idServ.setTipoServizio(splitServizio[0]);
				idServ.setServizio(splitServizio[1]);
				// servizio correlato
				if( splitLine[3].equals("tipologiaServizio(correlato)") )
					idServ.setTipologiaServizio(TipologiaServizio.CORRELATO.toString());
				else
					idServ.setTipologiaServizio(TipologiaServizio.NORMALE.toString());
				ids.add(idServ);
			}

			if(ids.size()==0){
				return null;
			}else{
				IDServizio [] rIDS = new IDServizio[1];
				return ids.toArray(rIDS);
			}

		}catch(DriverRegistroServiziException de){
			throw de;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la lettura del file di index: "+e.getMessage(),e);
		}
	}

	/**
	 * Legge un file di index dei servizi
	 * 
	 * @param directoryServiziSoggetto
	 * @return index dei servizi
	 */
	private IDServizio[] readIndexServiziFromFile(String directoryServiziSoggetto)throws DriverRegistroServiziException{

		String indexServizi = directoryServiziSoggetto;
		if(directoryServiziSoggetto.endsWith(File.separator)==false){
			indexServizi = directoryServiziSoggetto + File.separator;
		}
		indexServizi = indexServizi + CostantiXMLRepository.INDEX_SERVIZI;

		BufferedReader bf = null;
		FileReader fr = null;
		try{

			File f = new File(indexServizi);
			StringBuffer contenuto = new StringBuffer();
			if(f.exists()){
				if(f.isFile()==false){
					throw new Exception("Non e' un file regolare");
				}
				fr = new FileReader(f);
				bf = new BufferedReader(fr);
				char[]buffer = new char[1024];
				int letti=0;
				while( (letti=fr.read(buffer))!=-1){
					contenuto.append(buffer,0,letti);	
				}
			}else{
				throw new DriverRegistroServiziException("File di index non esistente");
			}

			return this.readIndexServizi(contenuto.toString());

		}catch(DriverRegistroServiziException de){
			throw de;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la lettura del file di index ("+indexServizi+"): "+e.getMessage(),e);
		}finally{
			try{
				if( fr != null )
					fr.close();
			} catch(Exception er) {}
			try{
				if( bf != null )
					bf.close();
			} catch(Exception er) {}
		}
	}









	/* ---------------- GENERAZIONE XML ACCORDO DI COOPERAZIONE ------------------------- */
	
	public String mappingIDAccordoCooperazioneToFileName(IDAccordoCooperazione idAccordo)throws DriverRegistroServiziException{
		return this.mappingUriToFileName_accordoCooperazione(this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordo));
	}
	public String mappingUriToFileName_accordoCooperazione(String uriAccordo)throws DriverRegistroServiziException{
		String tmp = uriAccordo.replaceAll("/", "_");
		tmp = tmp.replaceAll(":", "_");
		return tmp;
	}
	
	/**
	 * Metodo che controlla se l'accordo risulta gia' registrato
	 * 
	 * @param idAccordo ID dell'accordo di cooperazione da creare/modificare
	 * @return true se l'accordo risulta registrato, false altrimenti.
	 * 
	 */   
	public boolean existsAccordoCooperazione(IDAccordoCooperazione idAccordo)throws DriverRegistroServiziException{

		String fileNameAccordo = null;
		try {
			fileNameAccordo = mappingIDAccordoCooperazioneToFileName(idAccordo);
			File fileXML = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE + File.separator + fileNameAccordo + ".xml");
			File dirAllegati = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE + File.separator + CostantiXMLRepository.ALLEGATI_DIR +fileNameAccordo);
			File dirSpecificheSemiformali = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE + File.separator + CostantiXMLRepository.SPECIFICHE_SEMIFORMALI_DIR +fileNameAccordo);
			if( (fileXML.exists()==false) || (dirAllegati.exists()==false) || (dirSpecificheSemiformali.exists()==false) ){
				return false;
			}else
				return true;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la ricerca ("+CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE+")  ["+fileNameAccordo+"]: "+e.getMessage(),e);
		}
		
	}

	/**
	 * Metodo che si occupa della generazione/modifica di un file XML a partire da un accordo di cooperazione. 
	 * Sono richiesti interattivamente i parametri che identificano il file XML da generare. 
	 * 
	 * @param idAccordo ID dell'accordo di cooperazione da creare/modificare
	 * @param accordo Dati dell'accordo di cooperazione da trasformare in XML.
	 * 
	 */   
	public void createAccordoCooperazione(IDAccordoCooperazione idAccordo,AccordoCooperazione accordo) throws DriverRegistroServiziException{

		String fileNameAccordo = null;
		try {
			fileNameAccordo = mappingIDAccordoCooperazioneToFileName(idAccordo);
			String accordoDir = this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE + File.separator;
			String accordoOLD = fileNameAccordo;
			String accordoNEW = mappingUriToFileName_accordoCooperazione(this.idAccordoCooperazioneFactory.getUriFromAccordo(accordo));

			if(accordoOLD.equals(accordoNEW)==false){
				// Esiste gia' deve essere modificato il nome.
				// Controllo esistenza per modifica
				File fileXML = new File(accordoDir + accordoOLD + ".xml");
				File dirAllegati = new File(accordoDir + CostantiXMLRepository.ALLEGATI_DIR + accordoOLD);
				File dirSpecificheSemiformali = new File(accordoDir + CostantiXMLRepository.SPECIFICHE_SEMIFORMALI_DIR + accordoOLD);
				
				if( (fileXML.exists()==false) || (dirAllegati.exists()==false) || (dirSpecificheSemiformali.exists()==false) ){
					throw new DriverRegistroServiziException("Richiesta modifica di un accordo non registrato ["+accordoOLD + "]");
				}
				else{
					try{
						// elimino vecchia definizione Accordo Servizio
						if(fileXML.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della vecchia definizione per l'accordo ["+fileXML.getAbsolutePath()+"] non riuscita");
						}
						// Elimino vecchi allegati
						String[]listAllegati = dirAllegati.list();
						for(int j=0; j<listAllegati.length;j++){
							File fileAllegati = new File(dirAllegati.getAbsolutePath() + File.separator +listAllegati[j]);
							if(fileAllegati.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione dell'allegato ["+ fileAllegati.getAbsolutePath() +"] per l'accordo [" + accordoOLD + "] non riuscita");
							}
						}
						// Elimina vecchia directory degli allegati
						if(dirAllegati.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della directory per gli allegati dell'accordo ["+accordoOLD + "] non riuscita");
						}
						// Elimino vecche specificheSemiformali
						String[]listSpecificheSemiformali = dirSpecificheSemiformali.list();
						for(int j=0; j<listSpecificheSemiformali.length;j++){
							File fileSpecificheSemiformali = new File(dirSpecificheSemiformali.getAbsolutePath() + File.separator +listSpecificheSemiformali[j]);
							if(fileSpecificheSemiformali.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione della specifica semiformale ["+ fileSpecificheSemiformali.getAbsolutePath() +"] per l'accordo [" + accordoOLD + "] non riuscita");
							}
						}
						// Elimina vecchia directory delle specifiche semiformali
						if(dirSpecificheSemiformali.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della directory per le specifiche semiformali dell'accordo ["+accordoOLD + "] non riuscita");
						}

					}catch(Exception io){
						throw new DriverRegistroServiziException("Rinominazione della directory/file per l'accordo ["+accordoOLD+ "] non riuscita: "+io.toString());
					}
				}
			}


			// Crea directory 
			File dirAllegati = new File(accordoDir + CostantiXMLRepository.ALLEGATI_DIR + accordoNEW);
			File dirSpecificheSemiformali = new File(accordoDir + CostantiXMLRepository.SPECIFICHE_SEMIFORMALI_DIR + accordoNEW);
			String prefixUrlAllegato = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.ALLEGATI_DIR + accordoNEW +  CostantiXMLRepository.URL_SEPARATOR;
			String prefixUrlSpecificaSemiformale = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.SPECIFICHE_SEMIFORMALI_DIR + accordoNEW +  CostantiXMLRepository.URL_SEPARATOR;
			
			// Gestione Allegati
			if(dirAllegati.exists()==false){
				if(dirAllegati.mkdir()==false){
					throw new DriverRegistroServiziException("Creazione della directory per gli allegati dell'accordo ["+accordoNEW + "] non riuscita");
				}   
			} else { 
				// elimino vecchi allegati
				String [] files = dirAllegati.list();
				if(files!=null){
					for(int i=0; i<files.length; i++){
						File file = new File(files[i]);
						if(file.exists()){
							if(file.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione allegato ["+files[i]+ "] non riuscita");
							} 
						}
					}
				}
			}
			// Gestione SpecificheSemiformali
			if(dirSpecificheSemiformali.exists()==false){
				if(dirSpecificheSemiformali.mkdir()==false){
					throw new DriverRegistroServiziException("Creazione della directory per le specifiche semiformali dell'accordo ["+accordoNEW + "] non riuscita");
				}   
			} else { 
				// elimino vecche specifiche semiformali
				String [] files = dirSpecificheSemiformali.list();
				if(files!=null){
					for(int i=0; i<files.length; i++){
						File file = new File(files[i]);
						if(file.exists()){
							if(file.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione spacifica semiformale ["+files[i]+ "] non riuscita");
							} 
						}
					}
				}
			}

			// File che definisce l'accordo
			String fileXML = accordoDir + accordoNEW + ".xml";

			// Definizione accordo
			org.openspcoop2.core.registry.RegistroServizi registroXML = new org.openspcoop2.core.registry.RegistroServizi();
			
			// Allegati
			for(int i=0; i<accordo.sizeAllegatoList();i++){
				String path = null;
				if(accordo.getAllegato(i).getFile().startsWith(prefixUrlAllegato)){
					path = dirAllegati.getAbsolutePath() + File.separator + accordo.getAllegato(i).getFile().substring(prefixUrlAllegato.length());
				}else{
					path = dirAllegati.getAbsolutePath() + File.separator + accordo.getAllegato(i).getFile();
				}
				generaFile(accordo.getAllegato(i).getByteContenuto(),path);
				String url = prefixUrlAllegato + accordo.getAllegato(i).getFile();
				accordo.getAllegato(i).setFile(url);
			}
			
			// SpecificheSemiformali
			for(int i=0; i<accordo.sizeSpecificaSemiformaleList();i++){
				String path = null;
				if(accordo.getSpecificaSemiformale(i).getFile().startsWith(prefixUrlSpecificaSemiformale)){
					path = dirSpecificheSemiformali.getAbsolutePath() + File.separator + accordo.getSpecificaSemiformale(i).getFile().substring(prefixUrlSpecificaSemiformale.length());
				}else{
					path = dirSpecificheSemiformali.getAbsolutePath() + File.separator + accordo.getSpecificaSemiformale(i).getFile();
				}
				generaFile(accordo.getSpecificaSemiformale(i).getByteContenuto(),path);
				String url = prefixUrlSpecificaSemiformale + accordo.getSpecificaSemiformale(i).getFile();
				accordo.getSpecificaSemiformale(i).setFile(url);
			}

			// generazione XML
			this.cleanerOpenSPCoop2ExtensionsRegistry.clean(accordo); // NOTA: vengono eliminati anche tutti i campi contenenti bytes. Comunque li ho letti prima
			registroXML.addAccordoCooperazione(accordo);
			JiBXUtils.objToXml(fileXML,org.openspcoop2.core.registry.RegistroServizi.class,registroXML);

		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante l'elaborazione XML dell'accordo  ["+fileNameAccordo+"]: "+e.getMessage(),e);
		}
	}  

	/**
	 * Metodo che si occupa dell'eliminazione dell'accordo dal repository. 
	 * 
	 * @param idAccordo ID dell'accordo da eliminare
	 */   
	public void deleteAccordoCooperazione(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException {

		String fileNameAccordo = null;
		try {
			fileNameAccordo = mappingIDAccordoCooperazioneToFileName(idAccordo);
			String fileXML = this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE + File.separator + fileNameAccordo + ".xml";
			File dirAllegati = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE + File.separator + CostantiXMLRepository.ALLEGATI_DIR + fileNameAccordo);
			File dirSpecificheSemiformali = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE + File.separator + CostantiXMLRepository.SPECIFICHE_SEMIFORMALI_DIR + fileNameAccordo);
			
			// Elimino accordo
			File accordo = new File(fileXML);
			if(accordo.delete() == false){
				throw new DriverRegistroServiziException("Eliminazione XML dell'accordo ["+fileNameAccordo+"] non riuscta");
			}
			// Elimino vecchi allegati
			String[]listAllegati = dirAllegati.list();
			for(int j=0; j<listAllegati.length;j++){
				File fileAllegati = new File(dirAllegati.getAbsolutePath() + File.separator +listAllegati[j]);
				if(fileAllegati.delete()==false){
					throw new DriverRegistroServiziException("Eliminazione dell'allegato ["+ fileAllegati.getAbsolutePath() +"] per l'accordo [" + fileNameAccordo + "] non riuscita");
				}
			}
			// Elimina vecchia directory degli allegati
			if(dirAllegati.delete()==false){
				throw new DriverRegistroServiziException("Eliminazione della directory per gli allegati dell'accordo ["+fileNameAccordo + "] non riuscita");
			}
			// Elimino vecche specificheSemiformali
			String[]listSpecificheSemiformali = dirSpecificheSemiformali.list();
			for(int j=0; j<listSpecificheSemiformali.length;j++){
				File fileSpecificheSemiformali = new File(dirSpecificheSemiformali.getAbsolutePath() + File.separator +listSpecificheSemiformali[j]);
				if(fileSpecificheSemiformali.delete()==false){
					throw new DriverRegistroServiziException("Eliminazione della specifica semiformale ["+ fileSpecificheSemiformali.getAbsolutePath() +"] per l'accordo [" + fileNameAccordo + "] non riuscita");
				}
			}
			// Elimina vecchia directory delle specifiche semiformali
			if(dirSpecificheSemiformali.delete()==false){
				throw new DriverRegistroServiziException("Eliminazione della directory per le specifiche semiformali dell'accordo ["+fileNameAccordo + "] non riuscita");
			}

		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLlib] Riscontrato errore durante l'eliminazione XML dell'accordo ["+fileNameAccordo+"]: "+e.getMessage(),e);
		}
	}  

	/**
	 * Lista di accordi registrati
	 * 
	 * @return lista di accordi registrati.
	 * 
	 */   
	public AccordoCooperazione[] getAccordiCooperazione()throws DriverRegistroServiziException{

		AccordoCooperazione [] accordiRegistrati = null;
		try {
			File dir = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE);
			if(dir.exists()==false)
				return null;
			File[] accordi = dir.listFiles();
			int numAccordi = 0;
			if(accordi!=null){
				for(int i=0; i<accordi.length;i++){
					if(accordi[i].isFile()){
						numAccordi++;
					}
				}
			}
			if(numAccordi>0){
				accordiRegistrati = new AccordoCooperazione[numAccordi];
				for(int i=0,index=0; i<accordi.length;i++){
					if(accordi[i].isFile()){
						org.openspcoop2.core.registry.RegistroServizi r = 
							(org.openspcoop2.core.registry.RegistroServizi)  JiBXUtils.xmlToObj(accordi[i].getAbsolutePath(),
									org.openspcoop2.core.registry.RegistroServizi.class);
						accordiRegistrati[index] = r.getAccordoCooperazione(0);
						index++;
					}
				}
			}
			return accordiRegistrati;
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la ricerca degli accordi di cooperazione: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ---------------- GENERAZIONE XML ACCORDO DI SERVIZIO ------------------------- */
	
	public String mappingIDAccordoToFileName(IDAccordo idAccordo)throws DriverRegistroServiziException{
		return this.mappingUriToFileName(this.idAccordoFactory.getUriFromIDAccordo(idAccordo));
	}
	public String mappingUriToFileName(String uriAccordo)throws DriverRegistroServiziException{
		String tmp = uriAccordo.replaceAll("/", "_");
		tmp = tmp.replaceAll(":", "_");
		return tmp;
	}
	
	/**
	 * Metodo che controlla se l'accordo risulta gia' registrato
	 * 
	 * @param idAccordo ID dell'accordo di servizio da creare/modificare
	 * @return true se l'accordo risulta registrato, false altrimenti.
	 * 
	 */   
	public boolean existsAccordoServizioParteComune(IDAccordo idAccordo)throws DriverRegistroServiziException{

		String fileNameAccordo = null;
		try {
			fileNameAccordo = mappingIDAccordoToFileName(idAccordo);
			File fileXML = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + File.separator + fileNameAccordo + ".xml");
			File dirWSDL = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + File.separator + CostantiXMLRepository.WSDL_DIR +fileNameAccordo);
			File dirSpecificheConversazioni = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + File.separator + CostantiXMLRepository.SPECIFICHE_CONVERSAZIONI_DIR +fileNameAccordo);
			File dirAllegati = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + File.separator + CostantiXMLRepository.ALLEGATI_DIR +fileNameAccordo);
			File dirSpecificheSemiformali = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + File.separator + CostantiXMLRepository.SPECIFICHE_SEMIFORMALI_DIR +fileNameAccordo);
			if( (fileXML.exists()==false) || (dirWSDL.exists()==false) || (dirSpecificheConversazioni.exists()==false) || (dirAllegati.exists()==false) || (dirSpecificheSemiformali.exists()==false) ){
				return false;
			}else
				return true;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la ricerca ("+CostantiXMLRepository.ACCORDI_DI_SERVIZIO+")  ["+fileNameAccordo+"]: "+e.getMessage(),e);
		}
		
	}

	/**
	 * Metodo che si occupa della generazione/modifica di un file XML a partire da un accordo di servizio. 
	 * Sono richiesti interattivamente i parametri che identificano il file XML da generare. 
	 * 
	 * @param idAccordo ID dell'accordo di servizio da creare/modificare
	 * @param accordo Dati dell'accordo di servizio da trasformare in XML.
	 * 
	 */   
	public void createAccordoServizioParteComune(IDAccordo idAccordo,AccordoServizioParteComune accordo) throws DriverRegistroServiziException{

		String fileNameAccordo = null;
		try {
			fileNameAccordo = mappingIDAccordoToFileName(idAccordo);
			String accordoDir = this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + File.separator;
			String accordoOLD = fileNameAccordo;
			String accordoNEW = mappingUriToFileName(this.idAccordoFactory.getUriFromAccordo(accordo));

			if(accordoOLD.equals(accordoNEW)==false){
				// Esiste gia' deve essere modificato il nome.
				// Controllo esistenza per modifica
				File fileXML = new File(accordoDir + accordoOLD + ".xml");
				File dirWSDL = new File(accordoDir + CostantiXMLRepository.WSDL_DIR + accordoOLD);
				File dirSpecificheConversazioni = new File(accordoDir + CostantiXMLRepository.SPECIFICHE_CONVERSAZIONI_DIR + accordoOLD);
				File dirAllegati = new File(accordoDir + CostantiXMLRepository.ALLEGATI_DIR + accordoOLD);
				File dirSpecificheSemiformali = new File(accordoDir + CostantiXMLRepository.SPECIFICHE_SEMIFORMALI_DIR + accordoOLD);
				File dirSpecificheCoordinamento = new File(accordoDir + CostantiXMLRepository.SPECIFICHE_COORDINAMENTO_DIR + accordoOLD);
				
				if( (fileXML.exists()==false) || (dirWSDL.exists()==false) || (dirSpecificheConversazioni.exists()==false) || (dirAllegati.exists()==false) || (dirSpecificheSemiformali.exists()==false) ){
					throw new DriverRegistroServiziException("Richiesta modifica di un accordo non registrato ["+accordoOLD + "]");
				}
				else{
					try{
						// elimino vecchia definizione Accordo Servizio
						if(fileXML.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della vecchia definizione per l'accordo ["+fileXML.getAbsolutePath()+"] non riuscita");
						}
						// Elimino vecchi WSDL
						String[]listWSDL = dirWSDL.list();
						for(int j=0; j<listWSDL.length;j++){
							File fileWSDL = new File(dirWSDL.getAbsolutePath() + File.separator +listWSDL[j]);
							if(fileWSDL.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione del file wsdl ["+ fileWSDL.getAbsolutePath() +"] per l'accordo [" + accordoOLD + "] non riuscita");
							}
						} 
						// Elimina vecchia directory dei WSDL
						if(dirWSDL.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della directory per i WSDL dell'accordo ["+accordoOLD + "] non riuscita");
						}
						// Elimino vecche specofiche conversazione
						String[]listSpecificheConversazione = dirSpecificheConversazioni.list();
						for(int j=0; j<listSpecificheConversazione.length;j++){
							File fileSpecificaConversazione = new File(dirSpecificheConversazioni.getAbsolutePath() + File.separator +listSpecificheConversazione[j]);
							if(fileSpecificaConversazione.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione del file di specifica conversazione ["+ fileSpecificaConversazione.getAbsolutePath() +"] per l'accordo [" + accordoOLD + "] non riuscita");
							}
						}
						// Elimina vecchia directory delle specifiche di conversazione
						if(dirSpecificheConversazioni.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della directory per le specifiche di conversazione dell'accordo ["+accordoOLD + "] non riuscita");
						}
						// Elimino vecchi allegati
						String[]listAllegati = dirAllegati.list();
						for(int j=0; j<listAllegati.length;j++){
							File fileAllegati = new File(dirAllegati.getAbsolutePath() + File.separator +listAllegati[j]);
							if(fileAllegati.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione dell'allegato ["+ fileAllegati.getAbsolutePath() +"] per l'accordo [" + accordoOLD + "] non riuscita");
							}
						}
						// Elimina vecchia directory degli allegati
						if(dirAllegati.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della directory per gli allegati dell'accordo ["+accordoOLD + "] non riuscita");
						}
						// Elimino vecche specificheSemiformali
						String[]listSpecificheSemiformali = dirSpecificheSemiformali.list();
						for(int j=0; j<listSpecificheSemiformali.length;j++){
							File fileSpecificheSemiformali = new File(dirSpecificheSemiformali.getAbsolutePath() + File.separator +listSpecificheSemiformali[j]);
							if(fileSpecificheSemiformali.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione della specifica semiformale ["+ fileSpecificheSemiformali.getAbsolutePath() +"] per l'accordo [" + accordoOLD + "] non riuscita");
							}
						}
						// Elimina vecchia directory delle specifiche semiformali
						if(dirSpecificheSemiformali.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della directory per le specifiche semiformali dell'accordo ["+accordoOLD + "] non riuscita");
						}
						// Gestione specifiche di coordinamento se esistenti
						if(dirSpecificheCoordinamento.exists()){
							// Elimino vecche specificheCoordinamento
							String[]listSpecificheCoordinamento = dirSpecificheCoordinamento.list();
							for(int j=0; j<listSpecificheCoordinamento.length;j++){
								File fileSpecificheCoordinamento = new File(dirSpecificheCoordinamento.getAbsolutePath() + File.separator +listSpecificheCoordinamento[j]);
								if(fileSpecificheCoordinamento.delete()==false){
									throw new DriverRegistroServiziException("Eliminazione della specifica coordinamento ["+ fileSpecificheCoordinamento.getAbsolutePath() +"] per l'accordo [" + accordoOLD + "] non riuscita");
								}
							}
							// Elimina vecchia directory delle specifiche coordinamento
							if(dirSpecificheCoordinamento.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione della directory per le specifiche coordinamento dell'accordo ["+accordoOLD + "] non riuscita");
							}
						}

					}catch(Exception io){
						throw new DriverRegistroServiziException("Rinominazione della directory/file per l'accordo ["+accordoOLD+ "] non riuscita: "+io.toString());
					}
				}
			}


			// Crea directory dei WSDL
			File dirWSDL = new File(accordoDir +CostantiXMLRepository.WSDL_DIR +accordoNEW);
			File dirSpecificaConversazioni = new File(accordoDir + CostantiXMLRepository.SPECIFICHE_CONVERSAZIONI_DIR + accordoNEW);
			File dirAllegati = new File(accordoDir + CostantiXMLRepository.ALLEGATI_DIR + accordoNEW);
			File dirSpecificheSemiformali = new File(accordoDir + CostantiXMLRepository.SPECIFICHE_SEMIFORMALI_DIR + accordoNEW);
			File dirSpecificheCoordinamento = new File(accordoDir + CostantiXMLRepository.SPECIFICHE_COORDINAMENTO_DIR + accordoNEW);
			String prefixUrlWSDL = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.WSDL_DIR + accordoNEW +  CostantiXMLRepository.URL_SEPARATOR;
			String prefixUrlSpecificaConversazione = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.SPECIFICHE_CONVERSAZIONI_DIR + accordoNEW +  CostantiXMLRepository.URL_SEPARATOR;
			String prefixUrlAllegato = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.ALLEGATI_DIR + accordoNEW +  CostantiXMLRepository.URL_SEPARATOR;
			String prefixUrlSpecificaSemiformale = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.SPECIFICHE_SEMIFORMALI_DIR + accordoNEW +  CostantiXMLRepository.URL_SEPARATOR;
			String prefixUrlSpecificaCoordinamento = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.SPECIFICHE_COORDINAMENTO_DIR + accordoNEW +  CostantiXMLRepository.URL_SEPARATOR;
			
			// Gestione WSDL
			if(dirWSDL.exists()==false){
				if(dirWSDL.mkdir()==false){
					throw new DriverRegistroServiziException("Creazione della directory per i WSDL dell'accordo ["+accordoNEW + "] non riuscita");
				}   
			} else { 
				// elimino vecchi wsdl

				// definitorio
				String definitorioPath = dirWSDL.getAbsolutePath() + File.separator  + CostantiXMLRepository.WSDL_DEFINITORIO;
				File definitorioWSDL = new File(definitorioPath);
				if(definitorioWSDL.exists()){
					if(definitorioWSDL.delete()==false){
						throw new DriverRegistroServiziException("Eliminazione WSDL definitorio ["+definitorioPath+ "] non riuscita");
					} 
				}

				// concettuale
				String concettualePath = dirWSDL.getAbsolutePath() + File.separator  + CostantiXMLRepository.WSDL_CONCETTUALE;
				File concettualeWSDL = new File(concettualePath);
				if(concettualeWSDL.exists()){
					if(concettualeWSDL.delete()==false){
						throw new DriverRegistroServiziException("Eliminazione WSDL concettuale ["+concettualePath+ "] non riuscita");
					} 
				}

				// logicoErogatore
				String logicoErogatorePath = dirWSDL.getAbsolutePath() + File.separator  + CostantiXMLRepository.WSDL_LOGICO_EROGATORE;
				File logicoErogatoreWSDL = new File(logicoErogatorePath);
				if(logicoErogatoreWSDL.exists()){
					if(logicoErogatoreWSDL.delete()==false){
						throw new DriverRegistroServiziException("Eliminazione WSDL logico Erogatore ["+logicoErogatorePath+ "] non riuscita");
					} 
				}

				// logicoFruitore
				String logicoFruitorePath = dirWSDL.getAbsolutePath() + File.separator  + CostantiXMLRepository.WSDL_LOGICO_FRUITORE;
				File logicoFruitoreWSDL = new File(logicoFruitorePath);
				if(logicoFruitoreWSDL.exists()){
					if(logicoFruitoreWSDL.delete()==false){
						throw new DriverRegistroServiziException("Eliminazione WSDL logico Fruitore ["+logicoFruitorePath+ "] non riuscita");
					} 
				}
			} 	
			// Gestione Specifica Conversazioni
			if(dirSpecificaConversazioni.exists()==false){
				if(dirSpecificaConversazioni.mkdir()==false){
					throw new DriverRegistroServiziException("Creazione della directory per le specifiche delle conversazioni dell'accordo ["+accordoNEW + "] non riuscita");
				}   
			} else { 
				// elimino vecchie specifiche conversazioni

				// concettuale
				String concettualePath = dirSpecificaConversazioni.getAbsolutePath() + File.separator  + CostantiXMLRepository.SPECIFICA_CONVERSAZIONE_CONCETTUALE;
				File concettuale = new File(concettualePath);
				if(concettuale.exists()){
					if(concettuale.delete()==false){
						throw new DriverRegistroServiziException("Eliminazione specifica conversazione concettuale ["+concettualePath+ "] non riuscita");
					} 
				}

				// erogatore
				String erogatorePath = dirSpecificaConversazioni.getAbsolutePath() + File.separator  + CostantiXMLRepository.SPECIFICA_CONVERSAZIONE_EROGATORE;
				File erogatore = new File(erogatorePath);
				if(erogatore.exists()){
					if(erogatore.delete()==false){
						throw new DriverRegistroServiziException("Eliminazione specifica conversazione erogatore ["+erogatorePath+ "] non riuscita");
					} 
				}

				// fruitore
				String fruitorePath = dirSpecificaConversazioni.getAbsolutePath() + File.separator  + CostantiXMLRepository.SPECIFICA_CONVERSAZIONE_FRUITORE;
				File fruitore = new File(fruitorePath);
				if(fruitore.exists()){
					if(fruitore.delete()==false){
						throw new DriverRegistroServiziException("Eliminazione specifica conversazione fruitore ["+fruitorePath+ "] non riuscita");
					} 
				}
			} 	
			// Gestione Allegati
			if(dirAllegati.exists()==false){
				if(dirAllegati.mkdir()==false){
					throw new DriverRegistroServiziException("Creazione della directory per gli allegati dell'accordo ["+accordoNEW + "] non riuscita");
				}   
			} else { 
				// elimino vecchi allegati
				String [] files = dirAllegati.list();
				if(files!=null){
					for(int i=0; i<files.length; i++){
						File file = new File(files[i]);
						if(file.exists()){
							if(file.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione allegato ["+files[i]+ "] non riuscita");
							} 
						}
					}
				}
			}
			// Gestione SpecificheSemiformali
			if(dirSpecificheSemiformali.exists()==false){
				if(dirSpecificheSemiformali.mkdir()==false){
					throw new DriverRegistroServiziException("Creazione della directory per le specifiche semiformali dell'accordo ["+accordoNEW + "] non riuscita");
				}   
			} else { 
				// elimino vecche specifiche semiformali
				String [] files = dirSpecificheSemiformali.list();
				if(files!=null){
					for(int i=0; i<files.length; i++){
						File file = new File(files[i]);
						if(file.exists()){
							if(file.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione spacifica semiformale ["+files[i]+ "] non riuscita");
							} 
						}
					}
				}
			}
			// Gestione SpecificheCoordinamento
			if(dirSpecificheCoordinamento.exists()==false){
				if( accordo.getServizioComposto()!=null &&  dirSpecificheCoordinamento.mkdir()==false){
					throw new DriverRegistroServiziException("Creazione della directory per le specifiche di coordinamento dell'accordo ["+accordoNEW + "] non riuscita");
				}   
			} else { 
				// elimino vecche specifiche di coordinamento
				String [] files = dirSpecificheCoordinamento.list();
				if(files!=null){
					for(int i=0; i<files.length; i++){
						File file = new File(files[i]);
						if(file.exists()){
							if(file.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione spacifica di coordinamento ["+files[i]+ "] non riuscita");
							} 
						}
					}
				}
			}

			// File che definisce l'accordo
			String fileXML = accordoDir + accordoNEW + ".xml";

			// Definizione accordo
			org.openspcoop2.core.registry.RegistroServizi registroXML = new org.openspcoop2.core.registry.RegistroServizi();

			// Definizione WSDL
			// WSDLDefinitorio
			if( accordo.getByteWsdlDefinitorio() == null )
				accordo.setWsdlDefinitorio(CostantiXMLRepository.WSDL_UNDEFINED);
			else{
				String path = dirWSDL.getAbsolutePath() + File.separator  + CostantiXMLRepository.WSDL_DEFINITORIO;
				generaFile(accordo.getByteWsdlDefinitorio(),path);
				String url = prefixUrlWSDL+ CostantiXMLRepository.WSDL_DEFINITORIO;
				accordo.setWsdlDefinitorio(url);
			}
			// WSDLConcettuale
			if( accordo.getByteWsdlConcettuale() == null )
				accordo.setWsdlConcettuale(CostantiXMLRepository.WSDL_UNDEFINED);
			else{
				String path = dirWSDL.getAbsolutePath() + File.separator + CostantiXMLRepository.WSDL_CONCETTUALE;
				generaFile(accordo.getByteWsdlConcettuale(),path);
				String url = prefixUrlWSDL+ CostantiXMLRepository.WSDL_CONCETTUALE;
				accordo.setWsdlConcettuale(url);
			}
			// WSDL Logico Erogatore
			if( accordo.getByteWsdlLogicoErogatore() == null  )
				accordo.setWsdlLogicoErogatore(CostantiXMLRepository.WSDL_UNDEFINED);
			else{
				String path = dirWSDL.getAbsolutePath() + File.separator + CostantiXMLRepository.WSDL_LOGICO_EROGATORE;
				generaFile(accordo.getByteWsdlLogicoErogatore(),path);
				String url = prefixUrlWSDL + CostantiXMLRepository.WSDL_LOGICO_EROGATORE;
				accordo.setWsdlLogicoErogatore(url);
			}
			// WSDL Logico Fruitore
			if( (accordo.getByteWsdlLogicoFruitore() == null))
				accordo.setWsdlLogicoFruitore(CostantiXMLRepository.WSDL_UNDEFINED);
			else{
				String path = dirWSDL.getAbsolutePath() + File.separator + CostantiXMLRepository.WSDL_LOGICO_FRUITORE;
				generaFile(accordo.getByteWsdlLogicoFruitore(),path);
				String url = prefixUrlWSDL + CostantiXMLRepository.WSDL_LOGICO_FRUITORE;
				accordo.setWsdlLogicoFruitore(url);
			}
			// SpecificaConversazioneConcettuale
			if( accordo.getByteSpecificaConversazioneConcettuale() == null )
				accordo.setSpecificaConversazioneConcettuale(CostantiXMLRepository.SPECIFICA_CONVERSAZIONE_UNDEFINED);
			else{
				String path = dirSpecificaConversazioni.getAbsolutePath() + File.separator + CostantiXMLRepository.SPECIFICA_CONVERSAZIONE_CONCETTUALE;
				generaFile(accordo.getByteSpecificaConversazioneConcettuale(),path);
				String url = prefixUrlSpecificaConversazione+ CostantiXMLRepository.SPECIFICA_CONVERSAZIONE_CONCETTUALE;
				accordo.setSpecificaConversazioneConcettuale(url);
			}
			// SpecificaConversazioneErogatore
			if( accordo.getByteSpecificaConversazioneErogatore() == null  )
				accordo.setSpecificaConversazioneErogatore(CostantiXMLRepository.SPECIFICA_CONVERSAZIONE_UNDEFINED);
			else{
				String path = dirSpecificaConversazioni.getAbsolutePath() + File.separator + CostantiXMLRepository.SPECIFICA_CONVERSAZIONE_EROGATORE;
				generaFile(accordo.getByteSpecificaConversazioneErogatore(),path);
				String url = prefixUrlSpecificaConversazione + CostantiXMLRepository.SPECIFICA_CONVERSAZIONE_EROGATORE;
				accordo.setSpecificaConversazioneErogatore(url);
			}
			// SpecificaConversazioneFruitore
			if( (accordo.getByteSpecificaConversazioneFruitore() == null))
				accordo.setSpecificaConversazioneFruitore(CostantiXMLRepository.SPECIFICA_CONVERSAZIONE_UNDEFINED);
			else{
				String path = dirSpecificaConversazioni.getAbsolutePath() + File.separator + CostantiXMLRepository.SPECIFICA_CONVERSAZIONE_FRUITORE;
				generaFile(accordo.getByteSpecificaConversazioneFruitore(),path);
				String url = prefixUrlSpecificaConversazione + CostantiXMLRepository.SPECIFICA_CONVERSAZIONE_FRUITORE;
				accordo.setSpecificaConversazioneFruitore(url);
			}
			
			// Allegati
			for(int i=0; i<accordo.sizeAllegatoList();i++){
				String path = null;
				if(accordo.getAllegato(i).getFile().startsWith(prefixUrlAllegato)){
					path = dirAllegati.getAbsolutePath() + File.separator + accordo.getAllegato(i).getFile().substring(prefixUrlAllegato.length());
				}else{
					path = dirAllegati.getAbsolutePath() + File.separator + accordo.getAllegato(i).getFile();
				}
				generaFile(accordo.getAllegato(i).getByteContenuto(),path);
				String url = prefixUrlAllegato + accordo.getAllegato(i).getFile();
				accordo.getAllegato(i).setFile(url);
			}
			
			// SpecificheSemiformali
			for(int i=0; i<accordo.sizeSpecificaSemiformaleList();i++){
				String path = null;
				if(accordo.getSpecificaSemiformale(i).getFile().startsWith(prefixUrlSpecificaSemiformale)){
					path = dirSpecificheSemiformali.getAbsolutePath() + File.separator + accordo.getSpecificaSemiformale(i).getFile().substring(prefixUrlSpecificaSemiformale.length());
				}else{
					path = dirSpecificheSemiformali.getAbsolutePath() + File.separator + accordo.getSpecificaSemiformale(i).getFile();
				}
				generaFile(accordo.getSpecificaSemiformale(i).getByteContenuto(),path);
				String url = prefixUrlSpecificaSemiformale + accordo.getSpecificaSemiformale(i).getFile();
				accordo.getSpecificaSemiformale(i).setFile(url);
			}
			
			// Specifiche di coordinamento
			if(accordo.getServizioComposto()!=null){
				for(int i=0; i<accordo.getServizioComposto().sizeSpecificaCoordinamentoList();i++){
					String path = null;
					if(accordo.getServizioComposto().getSpecificaCoordinamento(i).getFile().startsWith(prefixUrlSpecificaCoordinamento)){
						path = dirSpecificheCoordinamento.getAbsolutePath() + File.separator + accordo.getServizioComposto().getSpecificaCoordinamento(i).getFile().substring(prefixUrlSpecificaCoordinamento.length());
					}else{
						path = dirSpecificheCoordinamento.getAbsolutePath() + File.separator + accordo.getServizioComposto().getSpecificaCoordinamento(i).getFile();
					}
					generaFile(accordo.getServizioComposto().getSpecificaCoordinamento(i).getByteContenuto(),path);
					String url = prefixUrlSpecificaCoordinamento + accordo.getServizioComposto().getSpecificaCoordinamento(i).getFile();
					accordo.getServizioComposto().getSpecificaCoordinamento(i).setFile(url);
				}
			}

			// generazione XML
			this.cleanerOpenSPCoop2ExtensionsRegistry.clean(accordo); // NOTA: vengono eliminati anche tutti i campi contenenti bytes. Comunque li ho letti prima
			registroXML.addAccordoServizioParteComune(accordo);
			JiBXUtils.objToXml(fileXML,org.openspcoop2.core.registry.RegistroServizi.class,registroXML);

		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante l'elaborazione XML dell'accordo  ["+fileNameAccordo+"]: "+e.getMessage(),e);
		}
	}  

	/**
	 * Metodo che si occupa dell'eliminazione dell'accordo dal repository. 
	 * 
	 * @param idAccordo ID dell'accordo da eliminare
	 */   
	public void deleteAccordoServizioParteComune(IDAccordo idAccordo) throws DriverRegistroServiziException {

		String fileNameAccordo = null;
		try {
			fileNameAccordo = mappingIDAccordoToFileName(idAccordo);
			String fileXML = this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + File.separator + fileNameAccordo + ".xml";
			File dirWSDL = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + File.separator + CostantiXMLRepository.WSDL_DIR + fileNameAccordo);
			File dirSpecificheConversazione = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + File.separator + CostantiXMLRepository.SPECIFICHE_CONVERSAZIONI_DIR + fileNameAccordo);
			File dirAllegati = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + File.separator + CostantiXMLRepository.ALLEGATI_DIR + fileNameAccordo);
			File dirSpecificheSemiformali = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + File.separator + CostantiXMLRepository.SPECIFICHE_SEMIFORMALI_DIR + fileNameAccordo);
			File dirSpecificheCoordinamento = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + File.separator + CostantiXMLRepository.SPECIFICHE_COORDINAMENTO_DIR + fileNameAccordo);
			
			// Elimino accordo
			File accordo = new File(fileXML);
			if(accordo.delete() == false){
				throw new DriverRegistroServiziException("Eliminazione XML dell'accordo ["+fileNameAccordo+"] non riuscta");
			}
			// Elimino vecchi WSDL
			String[]listWSDL = dirWSDL.list();
			for(int j=0; j<listWSDL.length;j++){
				File fileWSDL = new File(dirWSDL.getAbsolutePath() + File.separator +listWSDL[j]);
				if(fileWSDL.delete()==false){
					throw new DriverRegistroServiziException("Eliminazione del file wsdl ["+fileWSDL.getAbsolutePath()+"] per l'accordo [" + fileNameAccordo + "] non riuscita");
				}
			}  
			// Elimina vecchia directory dei WSDL
			if(dirWSDL.delete()==false){
				throw new DriverRegistroServiziException("Eliminazione della directory per i WSDL dell'accordo ["+fileNameAccordo + "] non riuscita");
			}
			// Elimino vecchie specifiche conversazione
			String[]listSpecificheConversazione = dirSpecificheConversazione.list();
			for(int j=0; j<listSpecificheConversazione.length;j++){
				File fileSpecificaConversazione = new File(dirSpecificheConversazione.getAbsolutePath() + File.separator +listSpecificheConversazione[j]);
				if(fileSpecificaConversazione.delete()==false){
					throw new DriverRegistroServiziException("Eliminazione del file specifica di conversazione ["+ fileSpecificaConversazione.getAbsolutePath() +"] per l'accordo [" + fileNameAccordo + "] non riuscita");
				}
			}
			// Elimina vecchia directory delle specifiche di conversazione
			if(dirSpecificheConversazione.delete()==false){
				throw new DriverRegistroServiziException("Eliminazione della directory per le specifiche di conversazione dell'accordo ["+fileNameAccordo + "] non riuscita");
			}
			// Elimino vecchi allegati
			String[]listAllegati = dirAllegati.list();
			for(int j=0; j<listAllegati.length;j++){
				File fileAllegati = new File(dirAllegati.getAbsolutePath() + File.separator +listAllegati[j]);
				if(fileAllegati.delete()==false){
					throw new DriverRegistroServiziException("Eliminazione dell'allegato ["+ fileAllegati.getAbsolutePath() +"] per l'accordo [" + fileNameAccordo + "] non riuscita");
				}
			}
			// Elimina vecchia directory degli allegati
			if(dirAllegati.delete()==false){
				throw new DriverRegistroServiziException("Eliminazione della directory per gli allegati dell'accordo ["+fileNameAccordo + "] non riuscita");
			}
			// Elimino vecche specificheSemiformali
			String[]listSpecificheSemiformali = dirSpecificheSemiformali.list();
			for(int j=0; j<listSpecificheSemiformali.length;j++){
				File fileSpecificheSemiformali = new File(dirSpecificheSemiformali.getAbsolutePath() + File.separator +listSpecificheSemiformali[j]);
				if(fileSpecificheSemiformali.delete()==false){
					throw new DriverRegistroServiziException("Eliminazione della specifica semiformale ["+ fileSpecificheSemiformali.getAbsolutePath() +"] per l'accordo [" + fileNameAccordo + "] non riuscita");
				}
			}
			// Elimina vecchia directory delle specifiche semiformali
			if(dirSpecificheSemiformali.delete()==false){
				throw new DriverRegistroServiziException("Eliminazione della directory per le specifiche semiformali dell'accordo ["+fileNameAccordo + "] non riuscita");
			}
			// Gestione specifiche di coordinamento se esistenti
			if(dirSpecificheCoordinamento.exists()){
				// Elimino vecche specificheCoordinamento
				String[]listSpecificheCoordinamento = dirSpecificheCoordinamento.list();
				for(int j=0; j<listSpecificheCoordinamento.length;j++){
					File fileSpecificheCoordinamento = new File(dirSpecificheCoordinamento.getAbsolutePath() + File.separator +listSpecificheCoordinamento[j]);
					if(fileSpecificheCoordinamento.delete()==false){
						throw new DriverRegistroServiziException("Eliminazione della specifica coordinamento ["+ fileSpecificheCoordinamento.getAbsolutePath() +"] per l'accordo [" + fileNameAccordo + "] non riuscita");
					}
				}
				// Elimina vecchia directory delle specifiche coordinamento
				if(dirSpecificheCoordinamento.delete()==false){
					throw new DriverRegistroServiziException("Eliminazione della directory per le specifiche coordinamento dell'accordo ["+fileNameAccordo + "] non riuscita");
				}
			}

		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLlib] Riscontrato errore durante l'eliminazione XML dell'accordo ["+fileNameAccordo+"]: "+e.getMessage(),e);
		}
	}  

	/**
	 * Lista di accordi registrati
	 * 
	 * @return lista di accordi registrati.
	 * 
	 */   
	public AccordoServizioParteComune[] getAccordiServizioParteComune()throws DriverRegistroServiziException{

		AccordoServizioParteComune [] accordiRegistrati = null;
		try {
			File dir = new File(this.pathPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO);
			if(dir.exists()==false)
				return null;
			File[] accordi = dir.listFiles();
			int numAccordi = 0;
			if(accordi!=null){
				for(int i=0; i<accordi.length;i++){
					if(accordi[i].isFile()){
						numAccordi++;
					}
				}
			}
			if(numAccordi>0){
				accordiRegistrati = new AccordoServizioParteComune[numAccordi];
				for(int i=0,index=0; i<accordi.length;i++){
					if(accordi[i].isFile()){
						org.openspcoop2.core.registry.RegistroServizi r = 
							(org.openspcoop2.core.registry.RegistroServizi)  JiBXUtils.xmlToObj(accordi[i].getAbsolutePath(),
									org.openspcoop2.core.registry.RegistroServizi.class);
						accordiRegistrati[index] = r.getAccordoServizioParteComune(0);
						index++;
					}
				}
			}
			return accordiRegistrati;
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la ricerca degli accordi di servizio: "+e.getMessage(),e);
		}
	}







	
	
	
	
	
	
	
	/* ---------------- GENERAZIONE XML PORTE DI DOMINIO ------------------------- */
	/**
	 * Metodo che controlla se la porta di dominio risulta gia' registrata
	 * 
	 * @param nome Nome della porta di dominio da creare/modificare
	 * @return true se la porta di dominio risulta registrata, false altrimenti.
	 * 
	 */   
	public boolean existsPortaDominio(String nome)throws DriverRegistroServiziException{

		try {
			File fileXML = new File(this.pathPrefix + CostantiXMLRepository.PORTE_DI_DOMINIO + File.separator + nome + ".xml");
			if( (fileXML.exists()==false) ){
				return false;
			}else
				return true;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la ricerca ("+CostantiXMLRepository.PORTE_DI_DOMINIO+")  ["+nome+"]: "+e.getMessage(),e);
		}

	}

	/**
	 * Metodo che si occupa della generazione/modifica di un file XML a partire da una porta di dominio.
	 * Sono richiesti interattivamente i parametri che identificano il file XML da generare. 
	 * 
	 * @param nome Nome della porta di dominio da creare/modificare
	 * @param pdd Dati della porta di dominio da trasformare in XML.
	 * 
	 */   
	public void createPortaDominio(String nome,PortaDominio pdd) throws DriverRegistroServiziException{


		try {
			String pddDir = this.pathPrefix + CostantiXMLRepository.PORTE_DI_DOMINIO + File.separator;
			
			// Controllo esistenza per modifica
			String fileXML = pddDir + nome + ".xml";
			File fileXMLF = new File(fileXML);
			if( fileXMLF.exists() ){
				// richiesta modifica
				// elimino vecchia definizione Porta di Dominio
				try{
					if(fileXMLF.delete()==false){
						throw new DriverRegistroServiziException("Eliminazione della vecchia definizione per la porta di dominio ["+fileXMLF.getAbsolutePath()+"] non riuscita");
					}
				}catch(Exception io){
					throw new DriverRegistroServiziException("Eliminazione della vecchia definizione per la porta di dominio ["+nome+ "] non riuscita: "+io.toString());
				}	
			}

			// Definizione porta di dominio
			org.openspcoop2.core.registry.RegistroServizi registroXML = new org.openspcoop2.core.registry.RegistroServizi();

			// generazione XML
			this.cleanerOpenSPCoop2ExtensionsRegistry.clean(pdd);
			registroXML.addPortaDominio(pdd);
			JiBXUtils.objToXml(fileXML,org.openspcoop2.core.registry.RegistroServizi.class,registroXML);

		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante l'elaborazione XML della porta di dominio  ["+nome+"]: "+e.getMessage(),e);
		}
	}  

	/**
	 * Metodo che si occupa dell'eliminazione della porta di dominio dal repository. 
	 * 
	 * @param nome Nome della porta di dominio da eliminare
	 */   
	public void deletePortaDominio(String nome) throws DriverRegistroServiziException {

		try {
			String fileXML = this.pathPrefix + CostantiXMLRepository.PORTE_DI_DOMINIO + File.separator + nome + ".xml";
			
			// Elimino porta di dominio
			File pdd = new File(fileXML);
			if(pdd.delete() == false){
				throw new DriverRegistroServiziException("Eliminazione XML della porta di dominio ["+nome+"] non riuscta");
			}

		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLlib] Riscontrato errore durante l'eliminazione XML della porta di dominio ["+nome+"]: "+e.getMessage(),e);
		}
	}  

	/**
	 * Lista delle porte di dominio registrate
	 * 
	 * @return lista delle porte di dominio registrate
	 * 
	 */   
	public PortaDominio[] getPorteDominio()throws DriverRegistroServiziException{

		PortaDominio [] pddRegistrate = null;
		try {
			File dir = new File(this.pathPrefix + CostantiXMLRepository.PORTE_DI_DOMINIO);
			if(dir.exists()==false)
				return null;
			File[] porteDiDominio = dir.listFiles();
			int numPorteDiDominio = 0;
			if(porteDiDominio!=null){
				for(int i=0; i<porteDiDominio.length;i++){
					if(porteDiDominio[i].isFile()){
						numPorteDiDominio++;
					}
				}
			}
			if(numPorteDiDominio>0){
				pddRegistrate = new PortaDominio[numPorteDiDominio];
				for(int i=0,index=0; i<porteDiDominio.length;i++){
					if(porteDiDominio[i].isFile()){
						org.openspcoop2.core.registry.RegistroServizi r = 
							(org.openspcoop2.core.registry.RegistroServizi)  JiBXUtils.xmlToObj(porteDiDominio[i].getAbsolutePath(),
									org.openspcoop2.core.registry.RegistroServizi.class);
						pddRegistrate[index] = r.getPortaDominio(0);
						index++;
					}
				}
			}
			return pddRegistrate;
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la ricerca delle porte di dominio: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	





	/* ---------------- GENERAZIONE XML SOGGETTO ------------------------- */

	/**
	 * Metodo che controlla se il soggetto risulta gia' registrato
	 * 
	 * @param idSogg id del soggetto
	 * @return true se il soggetto risulta registrato, false altrimenti.
	 */   
	public boolean existsSoggetto(IDSoggetto idSogg) throws DriverRegistroServiziException{

		if(idSogg==null)
			throw new DriverRegistroServiziException("Alcuni parametri sono null");

		String idSoggetto = idSogg.getTipo() + idSogg.getNome();
		try {
			File dirSoggetto = new File(this.pathPrefix+idSoggetto);
			return dirSoggetto.exists();
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la ricerca del soggetto  ["+idSoggetto+"]: "+e.getMessage(),e);
		}
	}

	/**
	 * Metodo che si occupa della generazione di un file XML a partire da un soggetto. 
	 * Sono richiesti interattivamente i parametri che identificano il file XML da generare. 
	 * 
	 * @param idSogg id del soggetto da creare/modificare
	 * @param soggetto Dati del Soggetto da trasformare in XML.
	 */   
	public void createSoggetto(IDSoggetto idSogg,Soggetto soggetto) throws DriverRegistroServiziException{

		if(idSogg==null || soggetto==null)
			throw new DriverRegistroServiziException("Alcuni parametri sono null");

		String idSoggettoOLD = idSogg.getTipo() + idSogg.getNome();
		String idSoggettoNEW = soggetto.getTipo() + soggetto.getNome();

		try {

			// Creazione/Controllo Esistenza directory soggetto
			if(idSoggettoOLD.equals(idSoggettoNEW)){
				// Controllo esistenza o creazione
				File dir = new File(this.pathPrefix+idSoggettoNEW);
				if(dir.exists() == false){
					try{
						if(dir.mkdir()==false){
							throw new DriverRegistroServiziException("Creazione della directory per il soggetto ["+dir.getAbsolutePath()+"] non riuscita");
						}
					}catch(Exception io){
						throw new DriverRegistroServiziException("Creazione della directory per il soggetto ["+dir.getAbsolutePath()+"] non riuscita: "+io.toString());
					}
				}
			}else{
				// esiste gia deve essere rinominato.
				// Controllo esistenza o creazione
				File dir = new File(this.pathPrefix+idSoggettoOLD);
				if(dir.exists() == false){
					throw new DriverRegistroServiziException("Modifica di un soggetto non registrato ["+dir.getAbsolutePath()+"] non riuscita");
				}
				else{
					File newdir = new File(this.pathPrefix+idSoggettoNEW);
					try{
						// Rinomina directory
						if(dir.renameTo(newdir)==false){
							throw new DriverRegistroServiziException("Rinominazione della directory per il soggetto ["+dir.getAbsolutePath()+"] in ["+newdir.getAbsolutePath()+"] non riuscita");
						}
						// elimino vecchia definizione
						File oldXML = new File(this.pathPrefix+idSoggettoNEW+File.separator+idSoggettoOLD+".xml");
						if(oldXML.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della vecchia definizione per il soggetto ["+oldXML.getAbsolutePath()+"] non riuscita");
						}
					}catch(Exception io){
						throw new DriverRegistroServiziException("Rinominazione della directory per il soggetto ["+dir.getAbsolutePath()+"] in ["+newdir.getAbsolutePath()+"] non riuscita: "+io.toString());
					}
				}
			}

			String dirSoggetto = this.pathPrefix + idSoggettoNEW + File.separator;

			// Creazione directory servizio
			String serviceDir = dirSoggetto + CostantiXMLRepository.SERVIZI;
			File dirS = new File(serviceDir);
			if(dirS.exists() == false){
				try{
					if(dirS.mkdir()==false){
						throw new DriverRegistroServiziException("Creazione della directory per i servizi del soggetto ["+dirS.getAbsolutePath()+"] non riuscita");
					}
				}catch(Exception io){
					throw new DriverRegistroServiziException("Creazione della directory per i servizi del soggetto ["+dirS.getAbsolutePath()+"] non riuscita: "+io.toString());
				}
			}

			String fileXML = dirSoggetto + idSoggettoNEW + ".xml";

			// Definizione soggetto
			org.openspcoop2.core.registry.RegistroServizi registroXML = new org.openspcoop2.core.registry.RegistroServizi();

			// generazione XML
			this.cleanerOpenSPCoop2ExtensionsRegistry.clean(soggetto);
			registroXML.addSoggetto(soggetto);
			JiBXUtils.objToXml(fileXML,org.openspcoop2.core.registry.RegistroServizi.class,registroXML);

		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante l'elaborazione XML del soggetto ["+idSoggettoOLD+"]: "+e.getMessage(),e);
		}
	}  



	/**
	 * Metodo che si occupa dell'eliminazione del soggetto dal repository. 
	 * 
	 * @param idSogg id del soggetto da eliminare
	 */   
	public void deleteSoggetto(IDSoggetto idSogg) throws DriverRegistroServiziException{

		if(idSogg==null)
			throw new DriverRegistroServiziException("Alcuni parametri sono null");

		String idSoggetto = idSogg.getTipo() + idSogg.getNome();

		try {

			String dirSoggetto = this.pathPrefix + idSoggetto + File.separator;

			// Elimino servizi del soggetto
			File dirS = new File(dirSoggetto + CostantiXMLRepository.SERVIZI);
			File[] dirSserv = dirS.listFiles();
			for(int i=0; i<dirSserv.length;i++){
				if(dirSserv[i].delete() == false){
					throw new DriverRegistroServiziException("Eliminazione XML del soggetto ["+idSoggetto+"] (eliminazione definizione servizio ["+dirSserv[i].getAbsolutePath()+"]) non riuscita");
				}
			}

			// Elimino directory servizio
			if(dirS.delete() == false){
				throw new DriverRegistroServiziException("Eliminazione XML del soggetto ["+idSoggetto+"] (eliminazione directory servizi ["+dirS.getAbsolutePath()+"]) non riuscita");
			}

			// Elimino definizione soggetto
			File defSoggetto = new File(dirSoggetto + idSoggetto + ".xml");
			if(defSoggetto.delete() == false){
				throw new DriverRegistroServiziException("Eliminazione XML del soggetto ["+idSoggetto+"] (eliminazione xml soggetto ["+defSoggetto.getAbsolutePath()+"]) non riuscita");
			}

			// Elimino directory soggetto
			File dir = new File(this.pathPrefix+idSoggetto);
			if(dir.delete() == false){
				throw new DriverRegistroServiziException("Eliminazione XML del soggetto ["+idSoggetto+"] (eliminazione directory soggetto ["+dir.getAbsolutePath()+"]) non riuscita");
			}

		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante l'eliminazione XML del soggetto ["+idSoggetto+"]: "+e.getMessage(),e);
		}
	}  

	/**
	 * Lista di Soggetti registrati
	 * 
	 * @return lista di soggetti registrati.
	 * 
	 */   
	public Soggetto[] getSoggetti()throws DriverRegistroServiziException{

		Soggetto [] soggettiRegistrati = null;
		try {
			File dir = new File(this.pathPrefix);
			if(dir.exists()==false)
				return null;
			File[] soggetti = dir.listFiles();
			int numSoggetti = 0;
			if(soggetti!=null){
				for(int i=0; i<soggetti.length;i++){
					if(soggetti[i].isDirectory() && 
							!CostantiXMLRepository.ACCORDI_DI_SERVIZIO.equalsIgnoreCase(soggetti[i].getName()) && 
							!CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE.equalsIgnoreCase(soggetti[i].getName()) && 
							!CostantiXMLRepository.PORTE_DI_DOMINIO.equalsIgnoreCase(soggetti[i].getName())){
						numSoggetti++;
					}
				}
			}
			if(numSoggetti>0){
				soggettiRegistrati = new Soggetto[numSoggetti];
				for(int i=0,index=0; i<soggetti.length;i++){
					if(soggetti[i].isDirectory() && 
							!CostantiXMLRepository.ACCORDI_DI_SERVIZIO.equalsIgnoreCase(soggetti[i].getName()) &&
							!CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE.equalsIgnoreCase(soggetti[i].getName()) && 
							!CostantiXMLRepository.PORTE_DI_DOMINIO.equalsIgnoreCase(soggetti[i].getName())){
						File[] fs = soggetti[i].listFiles();
						for(int j=0; j<fs.length;j++){
							if(fs[j].isFile()){
								org.openspcoop2.core.registry.RegistroServizi r = 
									(org.openspcoop2.core.registry.RegistroServizi)  JiBXUtils.xmlToObj(fs[j].getAbsolutePath(),
											org.openspcoop2.core.registry.RegistroServizi.class);
								soggettiRegistrati[index] = r.getSoggetto(0);
								index++;
								break;
							}
						}
					}
				}
			}
			return soggettiRegistrati;
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la ricerca dei soggetti: "+e.getMessage(),e);
		}
	}







	/* ---------------- GENERAZIONE XML SERVIZIO ------------------------- */

	/**
	 * Metodo che controlla se il servizio risulta gia' registrato
	 * 
	 * @param idServ Identificativo del Servizio
	 * @return true se il servizio risulta registrato, false altrimenti.
	 */   
	public boolean existsAccordoServizioParteSpecifica(IDServizio idServ) throws DriverRegistroServiziException{

		if(idServ==null || idServ.getSoggettoErogatore()==null)
			throw new DriverRegistroServiziException("Alcuni parametri sono null");

		String idSoggetto = idServ.getSoggettoErogatore().getTipo()+
		idServ.getSoggettoErogatore().getNome();
		String idServizio = idServ.getTipoServizio()+idServ.getServizio();
		try {
			File definizioneXML = new File(this.pathPrefix+idSoggetto+File.separator+CostantiXMLRepository.SERVIZI
					+File.separator+idServizio+ ".xml");
			File dirWSDL = new File(this.pathPrefix + idSoggetto+File.separator+CostantiXMLRepository.SERVIZI
					+File.separator+CostantiXMLRepository.WSDL_DIR +idServizio);
			if( (definizioneXML.exists()==false) || (dirWSDL.exists()==false)){
				return false;
			}else
				return true;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la ricerca del servizio ["+idServizio+"]: "+e.getMessage(),e);
		}
	}

	/**
	 * Metodo che si occupa della generazione di un file XML a partire da un servizio. 
	 * Sono richiesti interattivamente i parametri che identificano il file XML da generare. 
	 * 
	 * @param idServ Identificativo del Servizio
	 * @param asps Dati del Servizio da trasformare in XML.
	 */   
	public void createAccordoServizioParteSpecifica(IDServizio idServ,AccordoServizioParteSpecifica asps) throws DriverRegistroServiziException{

		if(idServ==null || idServ.getSoggettoErogatore()==null || asps==null)
			throw new DriverRegistroServiziException("Alcuni parametri sono null");

		String idSoggetto = idServ.getSoggettoErogatore().getTipo()+
		idServ.getSoggettoErogatore().getNome();
		String idServizioOLD = idServ.getTipoServizio()+idServ.getServizio();
		Servizio servizio = asps.getServizio();
		String idServizioNEW = servizio.getTipo() + servizio.getNome();	

		String dirSoggetto = this.pathPrefix+idSoggetto+File.separator;
		String dirServiziSoggetto = dirSoggetto + CostantiXMLRepository.SERVIZI + File.separator;


		try {

			// contenuto index dei servizi 
			Vector<IDServizio> nID = new Vector<IDServizio>();
			if(this.existsIndexServizi(dirServiziSoggetto)){
				// elimino vecchia definizione
				IDServizio [] oldImage = this.readIndexServiziFromFile(dirServiziSoggetto);
				for(int i=0; i< oldImage.length; i++){
					if( !(oldImage[i].getTipoServizio().equals(idServ.getTipoServizio()) && oldImage[i].getServizio().equals(idServ.getServizio())) ){
						nID.add(oldImage[i]);
					}
				}
			}
			
			
			// esiste gia deve essere rinominato, eliminando il vecchio.
			if(idServizioOLD.equals(idServizioNEW) == false){

				
				// Esiste gia' deve essere modificato il nome.
				// Controllo esistenza servizio per modifica
				File definizioneXML = new File(dirServiziSoggetto+idServizioOLD+ ".xml");
				File dirWSDL = new File(dirServiziSoggetto + CostantiXMLRepository.WSDL_DIR + idServizioOLD);
				File dirAllegati = new File(dirServiziSoggetto + CostantiXMLRepository.ALLEGATI_DIR + idServizioOLD);
				File dirSpecificheSemiformali = new File(dirServiziSoggetto + CostantiXMLRepository.SPECIFICHE_SEMIFORMALI_DIR + idServizioOLD);
				File dirSpecificheSicurezza = new File(dirServiziSoggetto + CostantiXMLRepository.SPECIFICHE_SICUREZZA_DIR + idServizioOLD);
				File dirSpecificheLivelliServizio = new File(dirServiziSoggetto + CostantiXMLRepository.SPECIFICHE_LIVELLI_SERVIZIO_DIR + idServizioOLD);
				
				if( (definizioneXML.exists()==false) || (dirWSDL.exists()==false) || (dirAllegati.exists()==false) || (dirSpecificheSemiformali.exists()==false) || (dirSpecificheSicurezza.exists()==false) || (dirSpecificheLivelliServizio.exists()==false)){
					throw new DriverRegistroServiziException("Modifica di un servizio non registrato ["+idServizioOLD + "] non riuscita");
				}
				else{

					try{
						//	Controllo esistenza o creazione
						if(definizioneXML.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della vecchia definizione per il servizio ["+definizioneXML.getAbsolutePath()+"] non riuscita");
						}

						// Elimino vecchi WSDL
						String[]listWSDL = dirWSDL.list();
						for(int j=0; j<listWSDL.length;j++){
							File fileInterno = new File(dirWSDL.getAbsolutePath() + File.separator +listWSDL[j]);
							if(fileInterno.isDirectory()){
								// wsdl-implementativo fruitore-servizio
								String[]listFruitoreWSDL = fileInterno.list();
								for(int k=0; k<listFruitoreWSDL.length;k++){
									File fileWsdlFruitore = new File(fileInterno.getAbsolutePath() + File.separator +listFruitoreWSDL[k]);
									if(fileWsdlFruitore.delete()==false){
										throw new DriverRegistroServiziException("Eliminazione del file wsdl ["+ fileWsdlFruitore.getAbsolutePath() +"] per il servizio [" + idServizioOLD + "] non riuscita");
									}	
								}
							}
							if(fileInterno.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione del file wsdl ["+ fileInterno.getAbsolutePath() +"] per il servizio [" + idServizioOLD + "] non riuscita");
							}		
						}  
						// Elimina vecchia directory dei WSDL
						if(dirWSDL.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della directory per i WSDL del servizio ["+idServizioOLD + "] non riuscita");
						}					
						// Elimino vecchi allegati
						String[]listAllegati = dirAllegati.list();
						for(int j=0; j<listAllegati.length;j++){
							File fileAllegati = new File(dirAllegati.getAbsolutePath() + File.separator +listAllegati[j]);
							if(fileAllegati.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione dell'allegato ["+ fileAllegati.getAbsolutePath() +"] per l'accordo [" + idServizioOLD + "] non riuscita");
							}
						}
						// Elimina vecchia directory degli allegati
						if(dirAllegati.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della directory per gli allegati dell'accordo ["+idServizioOLD + "] non riuscita");
						}
						// Elimino vecche specificheSemiformali
						String[]listSpecificheSemiformali = dirSpecificheSemiformali.list();
						for(int j=0; j<listSpecificheSemiformali.length;j++){
							File fileSpecificheSemiformali = new File(dirSpecificheSemiformali.getAbsolutePath() + File.separator +listSpecificheSemiformali[j]);
							if(fileSpecificheSemiformali.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione della specifica semiformale ["+ fileSpecificheSemiformali.getAbsolutePath() +"] per l'accordo [" + idServizioOLD + "] non riuscita");
							}
						}
						// Elimina vecchia directory delle specifiche semiformali
						if(dirSpecificheSemiformali.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della directory per le specifiche semiformali dell'accordo ["+idServizioOLD + "] non riuscita");
						}
						// Elimino vecchie specificheSicurezza
						String[]listSpecificheSicurezza = dirSpecificheSicurezza.list();
						for(int j=0; j<listSpecificheSicurezza.length;j++){
							File fileSpecificheSicurezza = new File(dirSpecificheSicurezza.getAbsolutePath() + File.separator +listSpecificheSicurezza[j]);
							if(fileSpecificheSicurezza.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione della specifica di sicurezza ["+ fileSpecificheSicurezza.getAbsolutePath() +"] per l'accordo [" + idServizioOLD + "] non riuscita");
							}
						}
						// Elimina vecchia directory delle specifiche di sicurezza
						if(dirSpecificheSicurezza.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della directory per le specifiche di sicurezza dell'accordo ["+idServizioOLD + "] non riuscita");
						}
						// Elimino vecchie specificheLivelliServizio
						String[]listSpecificheLivelliServizio = dirSpecificheLivelliServizio.list();
						for(int j=0; j<listSpecificheLivelliServizio.length;j++){
							File fileSpecificheLivelliServizio = new File(dirSpecificheLivelliServizio.getAbsolutePath() + File.separator +listSpecificheLivelliServizio[j]);
							if(fileSpecificheLivelliServizio.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione della specifica dei livelli di servizio ["+ fileSpecificheLivelliServizio.getAbsolutePath() +"] per l'accordo [" + idServizioOLD + "] non riuscita");
							}
						}
						// Elimina vecchia directory delle specifiche dei livelli di servizio
						if(dirSpecificheLivelliServizio.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione della directory per le specifiche dei livelli di servizio dell'accordo ["+idServizioOLD + "] non riuscita");
						}
						
					}catch(Exception io){
						throw new DriverRegistroServiziException("Eliminazione del servizio ["+idServizioOLD +"] non riuscita: "+io.toString());
					}
				}
			}

			//	Crea directory dei WSDL
			File dirWSDL = new File(dirServiziSoggetto + CostantiXMLRepository.WSDL_DIR +idServizioNEW);
			File dirAllegati = new File(dirServiziSoggetto + CostantiXMLRepository.ALLEGATI_DIR + idServizioNEW);
			File dirSpecificheSemiformali = new File(dirServiziSoggetto + CostantiXMLRepository.SPECIFICHE_SEMIFORMALI_DIR + idServizioNEW);
			File dirSpecificheSicurezza = new File(dirServiziSoggetto + CostantiXMLRepository.SPECIFICHE_SICUREZZA_DIR + idServizioNEW);
			File dirSpecificheLivelliServizio = new File(dirServiziSoggetto + CostantiXMLRepository.SPECIFICHE_LIVELLI_SERVIZIO_DIR + idServizioNEW);
			String prefixUrlWSDL = this.urlPrefix + idSoggetto + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.SERVIZI + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.WSDL_DIR +idServizioNEW+  CostantiXMLRepository.URL_SEPARATOR;
			String prefixUrlAllegato = this.urlPrefix + idSoggetto + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.SERVIZI + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.ALLEGATI_DIR + idServizioNEW +  CostantiXMLRepository.URL_SEPARATOR;
			String prefixUrlSpecificaSemiformale = this.urlPrefix + idSoggetto + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.SERVIZI + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.SPECIFICHE_SEMIFORMALI_DIR + idServizioNEW +  CostantiXMLRepository.URL_SEPARATOR;
			String prefixUrlSpecificaSicurezza = this.urlPrefix + idSoggetto + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.SERVIZI + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.SPECIFICHE_SICUREZZA_DIR + idServizioNEW +  CostantiXMLRepository.URL_SEPARATOR;
			String prefixUrlSpecificaLivelliServizio = this.urlPrefix + idSoggetto + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.SERVIZI + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.SPECIFICHE_LIVELLI_SERVIZIO_DIR + idServizioNEW +  CostantiXMLRepository.URL_SEPARATOR;
			if(dirWSDL.exists()==false){
				if(dirWSDL.mkdir()==false){
					throw new DriverRegistroServiziException("Creazione della directory per i WSDL del servizio ["+idServizioNEW + "]");
				}   
			} else { 
				// elimino vecchi wsdl

				// implementativoErogatore
				String implementativoErogatorePath = dirWSDL.getAbsolutePath() + File.separator + CostantiXMLRepository.WSDL_IMPLEMENTATIVO_EROGATORE;
				File implementativoErogatoreWSDL = new File(implementativoErogatorePath);
				if(implementativoErogatoreWSDL.exists()){
					if(implementativoErogatoreWSDL.delete()==false){
						throw new DriverRegistroServiziException("Eliminazione WSDL implementativo Erogatore del servizio ["+implementativoErogatorePath+ "] non riuscita");
					} 
				}

				// implementativoFruitore
				String implementativoFruitorePath = dirWSDL.getAbsolutePath() + File.separator  + CostantiXMLRepository.WSDL_IMPLEMENTATIVO_FRUITORE;
				File implementativoFruitoreWSDL = new File(implementativoFruitorePath);
				if(implementativoFruitoreWSDL.exists()){
					if(implementativoFruitoreWSDL.delete()==false){
						throw new DriverRegistroServiziException("Eliminazione WSDL implementativo Fruitore del servizio ["+implementativoFruitorePath+ "] non riuscita");
					} 
				}
			} 	 
			// Gestione Allegati
			if(dirAllegati.exists()==false){
				if(dirAllegati.mkdir()==false){
					throw new DriverRegistroServiziException("Creazione della directory per gli allegati dell'accordo ["+idServizioNEW + "] non riuscita");
				}   
			} else { 
				// elimino vecchi allegati
				String [] files = dirAllegati.list();
				if(files!=null){
					for(int i=0; i<files.length; i++){
						File file = new File(files[i]);
						if(file.exists()){
							if(file.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione allegato ["+files[i]+ "] non riuscita");
							} 
						}
					}
				}
			}
			// Gestione SpecificheSemiformali
			if(dirSpecificheSemiformali.exists()==false){
				if(dirSpecificheSemiformali.mkdir()==false){
					throw new DriverRegistroServiziException("Creazione della directory per le specifiche semiformali dell'accordo ["+idServizioNEW + "] non riuscita");
				}   
			} else { 
				// elimino vecche specifiche semiformali
				String [] files = dirSpecificheSemiformali.list();
				if(files!=null){
					for(int i=0; i<files.length; i++){
						File file = new File(files[i]);
						if(file.exists()){
							if(file.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione spacifica semiformale ["+files[i]+ "] non riuscita");
							} 
						}
					}
				}
			}
			// Gestione SpecificheSicurezza
			if(dirSpecificheSicurezza.exists()==false){
				if(dirSpecificheSicurezza.mkdir()==false){
					throw new DriverRegistroServiziException("Creazione della directory per le specifiche di sicurezza dell'accordo ["+idServizioNEW + "] non riuscita");
				}   
			} else { 
				// elimino vecche specifiche di sicurezza
				String [] files = dirSpecificheSicurezza.list();
				if(files!=null){
					for(int i=0; i<files.length; i++){
						File file = new File(files[i]);
						if(file.exists()){
							if(file.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione spacifica di sicurezza ["+files[i]+ "] non riuscita");
							} 
						}
					}
				}
			}
			// Gestione SpecificheLivelliServizio
			if(dirSpecificheLivelliServizio.exists()==false){
				if(dirSpecificheLivelliServizio.mkdir()==false){
					throw new DriverRegistroServiziException("Creazione della directory per le specifiche dei livelli di servizio dell'accordo ["+idServizioNEW + "] non riuscita");
				}   
			} else { 
				// elimino vecche specifiche dei livelli di servizio
				String [] files = dirSpecificheLivelliServizio.list();
				if(files!=null){
					for(int i=0; i<files.length; i++){
						File file = new File(files[i]);
						if(file.exists()){
							if(file.delete()==false){
								throw new DriverRegistroServiziException("Eliminazione spacifica del livello di servizio ["+files[i]+ "] non riuscita");
							} 
						}
					}
				}
			}

			// check directory dei WSDL dei fruitori 
			String[]listWSDLFruitori = dirWSDL.list();
			for(int j=0; j<listWSDLFruitori.length;j++){
				File fileInterno = new File(dirWSDL.getAbsolutePath() + File.separator +listWSDLFruitori[j]);

				// Se e' una directory potra' contenere i wsdl di un fruitore
				if(fileInterno.isDirectory()){
					boolean definizioneTrovata = false;
					for(int h=0; h<asps.sizeFruitoreList(); h++){
						String idFruitore = asps.getFruitore(h).getTipo()+asps.getFruitore(h).getNome();
						if(idFruitore.equals(fileInterno.getName())){
							definizioneTrovata = true;
							break;
						}
					}

					// elimino i wsdl dei fruitori.
					String[]listWSDLFruitoreEliminato = fileInterno.list();
					for(int n=0; n<listWSDLFruitoreEliminato.length;n++){
						File fileWsdlFruitoreEliminato = new File(fileInterno.getAbsolutePath() + File.separator +listWSDLFruitoreEliminato[n]);
						if(fileWsdlFruitoreEliminato.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione del file wsdl ["+ fileWsdlFruitoreEliminato.getAbsolutePath() +"] per il servizio [" + idServizioNEW + "] (Fruitore) non riuscita");
						}	
					}

					// Elimino le directory dei fruitori che non sono piu' contenuti nel servizio.
					if(definizioneTrovata == false){
						if(fileInterno.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione del file wsdl ["+ fileInterno.getAbsolutePath() +"] per il servizio [" + idServizioNEW + "] (Fruitore) non riuscita");
						}
					}
				}
			}

			// aggiungo nuova definizione all'indice dei servizi
			IDServizio [] newImage = new IDServizio[nID.size()+1];
			int imageIndex = 0;
			for(; imageIndex<nID.size(); imageIndex++)
				newImage[imageIndex] = nID.get(imageIndex);
			IDServizio newImageS = (IDServizio) idServ.clone();
			newImageS.setUriAccordo(asps.getAccordoServizioParteComune());
			newImageS.setTipologiaServizio(servizio.getTipologiaServizio().toString());
			newImageS.setTipoServizio(servizio.getTipo());
			newImageS.setServizio(servizio.getNome());
			newImage[imageIndex] = newImageS;
			this.setContenutoIndexServizi(dirServiziSoggetto,newImage);
			
			
			String fileXML = dirServiziSoggetto+idServizioNEW+ ".xml";

			org.openspcoop2.core.registry.RegistroServizi registroXML = new org.openspcoop2.core.registry.RegistroServizi();
			org.openspcoop2.core.registry.Soggetto  soggXML = new org.openspcoop2.core.registry.Soggetto();
			soggXML.setTipo(servizio.getTipoSoggettoErogatore());
			soggXML.setNome(servizio.getNomeSoggettoErogatore());

			//	WSDL implementativo Erogatore
			if( asps.getByteWsdlImplementativoErogatore() == null  )
				asps.setWsdlImplementativoErogatore(CostantiXMLRepository.WSDL_UNDEFINED);
			else{
				String path = dirWSDL.getAbsolutePath() + File.separator + CostantiXMLRepository.WSDL_IMPLEMENTATIVO_EROGATORE;
				generaFile(asps.getByteWsdlImplementativoErogatore(),path);
				String url = prefixUrlWSDL + CostantiXMLRepository.WSDL_IMPLEMENTATIVO_EROGATORE;
				asps.setWsdlImplementativoErogatore(url);
			}
			// WSDL implementativo Fruitore
			if( (asps.getByteWsdlImplementativoFruitore() == null))
				asps.setWsdlImplementativoFruitore(CostantiXMLRepository.WSDL_UNDEFINED);
			else{
				String path = dirWSDL.getAbsolutePath() + File.separator + CostantiXMLRepository.WSDL_IMPLEMENTATIVO_FRUITORE;
				generaFile(asps.getByteWsdlImplementativoFruitore(),path);
				String url = prefixUrlWSDL + CostantiXMLRepository.WSDL_IMPLEMENTATIVO_FRUITORE;
				asps.setWsdlImplementativoFruitore(url);
			}

			// Allegati
			for(int i=0; i<asps.sizeAllegatoList();i++){
				String path = null;
				if(asps.getAllegato(i).getFile().startsWith(prefixUrlAllegato)){
					path = dirAllegati.getAbsolutePath() + File.separator + asps.getAllegato(i).getFile().substring(prefixUrlAllegato.length());
				}else{
					path = dirAllegati.getAbsolutePath() + File.separator + asps.getAllegato(i).getFile();
				}
				generaFile(asps.getAllegato(i).getByteContenuto(),path);
				String url = prefixUrlAllegato + asps.getAllegato(i).getFile();
				asps.getAllegato(i).setFile(url);
			}
			
			// SpecificheSemiformali
			for(int i=0; i<asps.sizeSpecificaSemiformaleList();i++){
				String path = null;
				if(asps.getSpecificaSemiformale(i).getFile().startsWith(prefixUrlSpecificaSemiformale)){
					path = dirSpecificheSemiformali.getAbsolutePath() + File.separator + asps.getSpecificaSemiformale(i).getFile().substring(prefixUrlSpecificaSemiformale.length());
				}else{
					path = dirSpecificheSemiformali.getAbsolutePath() + File.separator + asps.getSpecificaSemiformale(i).getFile();
				}
				generaFile(asps.getSpecificaSemiformale(i).getByteContenuto(),path);
				String url = prefixUrlSpecificaSemiformale + asps.getSpecificaSemiformale(i).getFile();
				asps.getSpecificaSemiformale(i).setFile(url);
			}
			
			// SpecificheSicurezza
			for(int i=0; i<asps.sizeSpecificaSicurezzaList();i++){
				String path = null;
				if(asps.getSpecificaSicurezza(i).getFile().startsWith(prefixUrlSpecificaSicurezza)){
					path = dirSpecificheSicurezza.getAbsolutePath() + File.separator + asps.getSpecificaSicurezza(i).getFile().substring(prefixUrlSpecificaSicurezza.length());
				}else{
					path = dirSpecificheSicurezza.getAbsolutePath() + File.separator + asps.getSpecificaSicurezza(i).getFile();
				}
				generaFile(asps.getSpecificaSicurezza(i).getByteContenuto(),path);
				String url = prefixUrlSpecificaSicurezza + asps.getSpecificaSicurezza(i).getFile();
				asps.getSpecificaSicurezza(i).setFile(url);
			}
			
			// SpecificheLivelliServizio
			for(int i=0; i<asps.sizeSpecificaLivelloServizioList();i++){
				String path = null;
				if(asps.getSpecificaLivelloServizio(i).getFile().startsWith(prefixUrlSpecificaLivelliServizio)){
					path = dirSpecificheLivelliServizio.getAbsolutePath() + File.separator + asps.getSpecificaLivelloServizio(i).getFile().substring(prefixUrlSpecificaLivelliServizio.length());
				}else{
					path = dirSpecificheLivelliServizio.getAbsolutePath() + File.separator + asps.getSpecificaLivelloServizio(i).getFile();
				}
				generaFile(asps.getSpecificaLivelloServizio(i).getByteContenuto(),path);
				String url = prefixUrlSpecificaLivelliServizio + asps.getSpecificaLivelloServizio(i).getFile();
				asps.getSpecificaLivelloServizio(i).setFile(url);
			}
			
			// Fruitori
			for(int i=0;i<asps.sizeFruitoreList();i++){
				Fruitore fr = asps.getFruitore(i);
				String idFruitore = fr.getTipo() + fr.getNome();
				File dirWSDLFruitore = new File(dirServiziSoggetto + CostantiXMLRepository.WSDL_DIR +idServizioNEW + File.separator + idFruitore);
				String prefixUrlWSDLFruitore = this.urlPrefix + idSoggetto + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.SERVIZI + CostantiXMLRepository.URL_SEPARATOR + CostantiXMLRepository.WSDL_DIR +idServizioNEW+  CostantiXMLRepository.URL_SEPARATOR + idFruitore + CostantiXMLRepository.URL_SEPARATOR;

				// WSDL Fruitore
				if(fr.getByteWsdlImplementativoErogatore()!=null || fr.getByteWsdlImplementativoFruitore()!=null){

					//	Crea directory dei WSDL
					if(dirWSDLFruitore.exists()==false){
						if(dirWSDLFruitore.mkdir()==false){
							throw new DriverRegistroServiziException("Creazione della directory per i WSDL del servizio (Fruitore "+idFruitore+") ["+idServizioNEW + "] fallita");
						}   
					}
				}

				//	WSDL implementativo Erogatore 
				if( fr.getByteWsdlImplementativoErogatore() == null  )
					asps.getFruitore(i).setWsdlImplementativoErogatore(CostantiXMLRepository.WSDL_UNDEFINED);
				else{
					String path = dirWSDLFruitore.getAbsolutePath() + File.separator + CostantiXMLRepository.WSDL_IMPLEMENTATIVO_EROGATORE;
					generaFile(fr.getByteWsdlImplementativoErogatore(),path);
					String url = prefixUrlWSDLFruitore + CostantiXMLRepository.WSDL_IMPLEMENTATIVO_EROGATORE;
					asps.getFruitore(i).setWsdlImplementativoErogatore(url);
				}
				// WSDL implementativo Fruitore
				if( (fr.getByteWsdlImplementativoFruitore() == null))
					asps.getFruitore(i).setWsdlImplementativoFruitore(CostantiXMLRepository.WSDL_UNDEFINED);
				else{
					String path = dirWSDLFruitore.getAbsolutePath() + File.separator + CostantiXMLRepository.WSDL_IMPLEMENTATIVO_FRUITORE;
					generaFile(fr.getByteWsdlImplementativoFruitore(),path);
					String url = prefixUrlWSDLFruitore + CostantiXMLRepository.WSDL_IMPLEMENTATIVO_FRUITORE;
					asps.getFruitore(i).setWsdlImplementativoFruitore(url);
				}

			}

			// generazione XML
			this.cleanerOpenSPCoop2ExtensionsRegistry.clean(asps); // NOTA: vengono eliminati anche tutti i campi contenenti bytes. Comunque li ho letti prima
			soggXML.addAccordoServizioParteSpecifica(asps);
			registroXML.addSoggetto(soggXML);
			JiBXUtils.objToXml(fileXML,org.openspcoop2.core.registry.RegistroServizi.class,registroXML);

		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante l'elaborazione XML del servizio ["+idServizioOLD+"]: "+e.getMessage(),e);
		}
	}  

	/**
	 * Metodo che si occupa dell'eliminazione del servizio dal repository. 
	 * 
	 * @param idServ Identificativo del Servizio
	 */   
	public void deleteAccordoServizioParteSpecifica(IDServizio idServ) throws DriverRegistroServiziException{

		if(idServ==null || idServ.getSoggettoErogatore()==null)
			throw new DriverRegistroServiziException("Alcuni parametri sono null");

		String idSoggetto = idServ.getSoggettoErogatore().getTipo()+
		idServ.getSoggettoErogatore().getNome();
		String idServizio = idServ.getTipoServizio()+idServ.getServizio();

		try {

			String dirSoggetto = this.pathPrefix+idSoggetto+File.separator;
			String dirServiziSoggetto = dirSoggetto + CostantiXMLRepository.SERVIZI + File.separator;

			// Elimino eventuale file di index
			if(this.existsIndexServizi(dirServiziSoggetto)){
				IDServizio[]index = this.readIndexServiziFromFile(dirServiziSoggetto);
				if(index!=null && index.length==1){
					this.deleteIndexServizi(dirServiziSoggetto);
				}else{
					IDServizio [] newImage = new IDServizio[index.length-1];
					int j=0;
					for(int i=0; i< index.length; i++){
						if( !(index[i].getTipoServizio().equals(idServ.getTipoServizio()) && index[i].getServizio().equals(idServ.getServizio())) ){
							newImage[j]=index[i];
							j++;
						}
					}
					this.setContenutoIndexServizi(dirServiziSoggetto,newImage);
				}
			}

			// Elimino servizi del soggetto
			File definizioneXML = new File(dirServiziSoggetto+idServizio+".xml");
			if(definizioneXML.delete() == false){
				throw new DriverRegistroServiziException("Eliminazione XML del servizio ["+idServizio+"] erogato dal soggetto ["+
						idSoggetto+"] non riuscita");
			}

			File dirWSDL = new File(dirServiziSoggetto + CostantiXMLRepository.WSDL_DIR + idServizio);
			File dirAllegati = new File(dirServiziSoggetto + CostantiXMLRepository.ALLEGATI_DIR + idServizio);
			File dirSpecificheSemiformali = new File(dirServiziSoggetto + CostantiXMLRepository.SPECIFICHE_SEMIFORMALI_DIR + idServizio);
			File dirSpecificheSicurezza = new File(dirServiziSoggetto + CostantiXMLRepository.SPECIFICHE_SICUREZZA_DIR + idServizio);
			File dirSpecificheLivelliServizio = new File(dirServiziSoggetto + CostantiXMLRepository.SPECIFICHE_LIVELLI_SERVIZIO_DIR + idServizio);
			
			//	Elimino vecchi WSDL
			String[]listWSDL = dirWSDL.list();
			for(int j=0; j<listWSDL.length;j++){
				File fileInterno = new File(dirWSDL.getAbsolutePath() + File.separator +listWSDL[j]);
				if(fileInterno.isDirectory()){
					// wsdl-implementativo fruitore-servizio
					String[]listFruitoreWSDL = fileInterno.list();
					for(int k=0; k<listFruitoreWSDL.length;k++){
						File fileWsdlFruitore = new File(fileInterno.getAbsolutePath() + File.separator +listFruitoreWSDL[k]);
						if(fileWsdlFruitore.delete()==false){
							throw new DriverRegistroServiziException("Eliminazione del file wsdl ["+ fileWsdlFruitore.getAbsolutePath() +"] per il servizio [" + idServizio + "] non riuscita");
						}	
					}
				}
				if(fileInterno.delete()==false){
					throw new DriverRegistroServiziException("Eliminazione del file wsdl ["+ fileInterno.getAbsolutePath() +"] per il servizio [" + idServizio + "] non riuscita");
				}		
			}  
			// Elimina vecchia directory dei WSDL
			if(dirWSDL.delete()==false){
				throw new DriverRegistroServiziException("Eliminazione della directory per i WSDL del servizio ["+idServizio + "] non riuscita");
			}
			
			// Elimino vecchi allegati
			String[]listAllegati = dirAllegati.list();
			for(int j=0; j<listAllegati.length;j++){
				File fileAllegati = new File(dirAllegati.getAbsolutePath() + File.separator +listAllegati[j]);
				if(fileAllegati.delete()==false){
					throw new DriverRegistroServiziException("Eliminazione dell'allegato ["+ fileAllegati.getAbsolutePath() +"] per l'accordo [" + idServizio + "] non riuscita");
				}
			}
			// Elimina vecchia directory degli allegati
			if(dirAllegati.delete()==false){
				throw new DriverRegistroServiziException("Eliminazione della directory per gli allegati dell'accordo ["+idServizio + "] non riuscita");
			}
			// Elimino vecche specificheSemiformali
			String[]listSpecificheSemiformali = dirSpecificheSemiformali.list();
			for(int j=0; j<listSpecificheSemiformali.length;j++){
				File fileSpecificheSemiformali = new File(dirSpecificheSemiformali.getAbsolutePath() + File.separator +listSpecificheSemiformali[j]);
				if(fileSpecificheSemiformali.delete()==false){
					throw new DriverRegistroServiziException("Eliminazione della specifica semiformale ["+ fileSpecificheSemiformali.getAbsolutePath() +"] per l'accordo [" + idServizio + "] non riuscita");
				}
			}
			// Elimina vecchia directory delle specifiche semiformali
			if(dirSpecificheSemiformali.delete()==false){
				throw new DriverRegistroServiziException("Eliminazione della directory per le specifiche semiformali dell'accordo ["+idServizio + "] non riuscita");
			}
			// Elimino vecchie specificheSicurezza
			String[]listSpecificheSicurezza = dirSpecificheSicurezza.list();
			for(int j=0; j<listSpecificheSicurezza.length;j++){
				File fileSpecificheSicurezza = new File(dirSpecificheSicurezza.getAbsolutePath() + File.separator +listSpecificheSicurezza[j]);
				if(fileSpecificheSicurezza.delete()==false){
					throw new DriverRegistroServiziException("Eliminazione della specifica di sicurezza ["+ fileSpecificheSicurezza.getAbsolutePath() +"] per l'accordo [" + idServizio + "] non riuscita");
				}
			}
			// Elimina vecchia directory delle specifiche di sicurezza
			if(dirSpecificheSicurezza.delete()==false){
				throw new DriverRegistroServiziException("Eliminazione della directory per le specifiche di sicurezza dell'accordo ["+idServizio + "] non riuscita");
			}
			// Elimino vecchie specificheLivelliServizio
			String[]listSpecificheLivelliServizio = dirSpecificheLivelliServizio.list();
			for(int j=0; j<listSpecificheLivelliServizio.length;j++){
				File fileSpecificheLivelliServizio = new File(dirSpecificheLivelliServizio.getAbsolutePath() + File.separator +listSpecificheLivelliServizio[j]);
				if(fileSpecificheLivelliServizio.delete()==false){
					throw new DriverRegistroServiziException("Eliminazione della specifica dei livelli di servizio ["+ fileSpecificheLivelliServizio.getAbsolutePath() +"] per l'accordo [" + idServizio + "] non riuscita");
				}
			}
			// Elimina vecchia directory delle specifiche dei livelli di servizio
			if(dirSpecificheLivelliServizio.delete()==false){
				throw new DriverRegistroServiziException("Eliminazione della directory per le specifiche dei livelli di servizio dell'accordo ["+idServizio + "] non riuscita");
			}

		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante l'eliminazione XML del servizio ["+idServizio+"] erogato dal soggetto ["+
					idSoggetto+"]: "+e.getMessage(),e);
		}
	}  

	/**
	 * Lista di Servizi registrati
	 * 
	 * @return lista di servizi registrati.
	 * 
	 */   
	public AccordoServizioParteSpecifica[] getAccordiServiziParteSpecifica()throws DriverRegistroServiziException{

		AccordoServizioParteSpecifica [] serviziRegistrati = null;
		try {
			// Prendo prima i soggetti
			Soggetto [] soggettiRegistrati = this.getSoggetti();
			// Prendo dopodiche i servizi
			File dir = new File(this.pathPrefix);
			File[] soggetti = dir.listFiles();
			Vector<AccordoServizioParteSpecifica> servizi = new Vector<AccordoServizioParteSpecifica>();
			int indexSoggetto = 0;
			if(soggetti!=null){
				for(int i=0; i<soggetti.length;i++){
					if(soggetti[i].isDirectory() && 
							!CostantiXMLRepository.ACCORDI_DI_SERVIZIO.equalsIgnoreCase(soggetti[i].getName()) &&
							!CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE.equalsIgnoreCase(soggetti[i].getName()) && 
							!CostantiXMLRepository.PORTE_DI_DOMINIO.equalsIgnoreCase(soggetti[i].getName())){
						File dirServiziSoggetto = new File(soggetti[i].getAbsoluteFile() + File.separator +CostantiXMLRepository.SERVIZI);
						File[] serviziSoggetto = dirServiziSoggetto.listFiles();
						if(serviziSoggetto!=null){
							for(int k=0; k<serviziSoggetto.length;k++){
								if(serviziSoggetto[k].isFile() && (this.isIndexServizi(serviziSoggetto[k])==false) ){
									org.openspcoop2.core.registry.RegistroServizi r = 
										(org.openspcoop2.core.registry.RegistroServizi)  JiBXUtils.xmlToObj(serviziSoggetto[k].getAbsolutePath(),
												org.openspcoop2.core.registry.RegistroServizi.class);
									if(r.getSoggetto(0).sizeAccordoServizioParteSpecificaList()>0){
										AccordoServizioParteSpecifica s = r.getSoggetto(0).getAccordoServizioParteSpecifica(0);
										s.getServizio().setTipoSoggettoErogatore(soggettiRegistrati[indexSoggetto].getTipo());
										s.getServizio().setNomeSoggettoErogatore(soggettiRegistrati[indexSoggetto].getNome());
										servizi.add(s);
									}
								}
							}
						}
						indexSoggetto++;
					}
				}
			}
			if(servizi.size()>0){
				serviziRegistrati = new AccordoServizioParteSpecifica[1];
				serviziRegistrati =  servizi.toArray(serviziRegistrati);
			}
			return serviziRegistrati;
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("[XMLLib] Riscontrato errore durante la ricerca dei soggetti: "+e.getMessage(),e);
		}
	}
}
