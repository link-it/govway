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

package org.openspcoop2.protocol.spcoop.testsuite.core;

import java.util.Date;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.utils.SPCoopUtils;
import org.openspcoop2.testsuite.db.DatiServizio;


/**
 * Utiliti per caricare buste E-Gov  da file
 *  
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BusteEGovDaFile {


	private final String ATTRIBUTO_TIPO = "tipo";
	private final String ATTRIBUTO_INDIRIZZO_TELEMATICO="indirizzoTelematico";
	private final String ATTRIBUTO_SERVIZIO_CORRELATO = "servizioCorrelato";
	private final String ATTRIBUTO_INOLTRO = "inoltro";
	private final String ATTRIBUTO_CONFERMA_RICEZIONE = "confermaRicezione";
	private final String ATTRIBUTO_NUMERO_PROGRESSIVO = "numeroProgressivo";

	private java.io.File[] file;
	private String[] nomiBuste;
	private int nfile;
	private char[] buffChar;

	private int startID[];
	private int stopID[];

	private String[] mittenti;
	private String[] tipoMittenti;
	private String[] indirizzoTelematicoMittenti;

	private String[] destinatari;
	private String[] tipoDestinatari;
	private String[] indirizzoTelematicoDestinatari;

	private String[] servizi;
	private String[] tipoServizio;
	private String[] azioni;

	private String[] profiliCollaborazione;
	private String[] tipoServizioCorrelatoProfiloCollaborazione;
	private String[] servizioCorrelatoProfiloCollaborazione;

	private String[] collaborazioni;

	private String[] idEGov; 

	private String[] inoltroProfiliTrasmissioni;
	private boolean[] confermaRicezioneProfiliTrasmissioni;

	private int[] numeroProgressivoSequenze;

	private String[] busteDaInviare;

	private Date[] min_gdo;

	public BusteEGovDaFile(java.io.File[] dir){

		this.file=dir;
		this.nfile = this.file.length;
		this.nomiBuste = new String[this.nfile];

		this.startID = new int[this.nfile];
		this.stopID = new int[this.nfile];

		this.mittenti  = new String[this.nfile];
		this.tipoMittenti = new String[this.nfile];
		this.indirizzoTelematicoMittenti = new String[this.nfile];

		this.destinatari = new String[this.nfile];
		this.tipoDestinatari = new String[this.nfile];
		this.indirizzoTelematicoDestinatari = new String[this.nfile];

		this.servizi = new String[this.nfile];
		this.tipoServizio = new String[this.nfile];
		this.azioni = new String[this.nfile];

		this.profiliCollaborazione = new String[this.nfile];
		this.servizioCorrelatoProfiloCollaborazione = new String[this.nfile];
		this.tipoServizioCorrelatoProfiloCollaborazione = new String[this.nfile];

		this.collaborazioni = new String[this.nfile];

		this.idEGov = new String[this.nfile];

		this.inoltroProfiliTrasmissioni = new String[this.nfile];
		this.confermaRicezioneProfiliTrasmissioni = new boolean[this.nfile];
		this.numeroProgressivoSequenze = new int[this.nfile];

		this.busteDaInviare = new String[this.nfile];

		this.min_gdo = new Date[this.nfile];


		this.inizializza();
	}

	public int getNumeroFileCaricati(){return this.nfile;}

	public int getIndexFromNomeFile(String nomefile){
		for(int i =0;i<this.nomiBuste.length;i++){
			if(this.nomiBuste[i].equals(nomefile))
				return i;
		}
		return -1;
	}
	public int getIndexFromId(String id){
		for(int i =0;i<this.idEGov.length;i++){
			if(this.idEGov[i].equals(id))
				return i;
		}
		return -1;
	}
	public String getID(int index){return this.idEGov[index];}
	public String getNomeBusta(int index){return this.nomiBuste[index];}
	private String getNomeMittente(int index){return this.mittenti[index];}
	private String getTipoMittente(int index){return this.tipoMittenti[index];}
	public String getIndirizzoTelematicoMittente(int index){return this.indirizzoTelematicoMittenti[index];}
	public IDSoggetto getMittente(int index){return new IDSoggetto(this.getTipoMittente(index), this.getNomeMittente(index), this.getNomeMittente(index)+"SPCoopIT");}
	private String getNomeDestinatario(int index){return this.destinatari[index];}
	private String getTipoDestinatario(int index){return this.tipoDestinatari[index];}
	public String getIndirizzoTelematicoDestinatario(int index){return this.indirizzoTelematicoDestinatari[index];} 
	public IDSoggetto getDestinatario(int index){return new IDSoggetto(this.getTipoDestinatario(index), this.getNomeDestinatario(index), this.getNomeDestinatario(index)+"SPCoopIT");}
	private String getServizio(int index){return this.servizi[index];}
	private String getTipoServizio(int index){return this.tipoServizio[index];}
	public DatiServizio getDatiServizio(int index){
		return new DatiServizio(getTipoServizio(index), getServizio(index), CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
	}
	public String getAzione(int index){return this.azioni[index];}
	public String getProfiloCollaborazione(int index){return this.profiliCollaborazione[index];}
	public ProfiloDiCollaborazione getProfiloCollaborazioneSdk(int index){
		String profilo = this.getProfiloCollaborazione(index);
		if(profilo==null){
			return null;
		}
		if(SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY.equals(profilo)){
			return ProfiloDiCollaborazione.ONEWAY;
		}
		else if(SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO.equals(profilo)){
			return ProfiloDiCollaborazione.SINCRONO;
		}
		else if(SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO.equals(profilo)){
			return ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO;
		}
		else if(SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO.equals(profilo)){
			return ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO;
		}
		else{
			return ProfiloDiCollaborazione.UNKNOWN;
		}
	}
	private String getTipoServizioCorrelatoProfiloCollaborazione(int index){return this.tipoServizioCorrelatoProfiloCollaborazione[index];}
	private String getServizioCorrelatoProfiloCollaborazione(int index){return this.servizioCorrelatoProfiloCollaborazione[index];}
	public DatiServizio getDatiServizioCorrelatoProfiloCollaborazione(int index){
		return new DatiServizio(getTipoServizioCorrelatoProfiloCollaborazione(index), getServizioCorrelatoProfiloCollaborazione(index), CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
	}
	public String getCollaborazione(int index){return this.collaborazioni[index];}
	public String getProfiloTrasmissioneInoltro(int index){return this.inoltroProfiliTrasmissioni[index];}
	public Inoltro getProfiloTrasmissioneInoltroSdk(int index){
		String profilo = this.getProfiloTrasmissioneInoltro(index);
		if(profilo==null){
			return null;
		}
		if(SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI.equals(profilo)){
			return Inoltro.SENZA_DUPLICATI;
		}
		else if(SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI.equals(profilo)){
			return Inoltro.CON_DUPLICATI;
		}
		else{
			return Inoltro.UNKNOWN;
		}
	}
	public boolean getProfiloTrasmissioneConfermaRicezione(int index){return this.confermaRicezioneProfiliTrasmissioni[index];}
	public int getSequenzaNumeroProgressivo(int index){return this.numeroProgressivoSequenze[index];}
	
	public String getBusta(int index){this.setMinGDO(index);return this.busteDaInviare[index];}

	public Date getMinGDO(int index){return this.min_gdo[index];}
	private void setMinGDO(int index){
		this.min_gdo[index] = new Date();
	}

	private void inizializza(){
		for(int i = 0;i<this.nfile;i++){

			this.startID[i] = -1;
			this.stopID[i] = -1;

			/*
			 * I VALORI SE NON PRESENTI !DEVONO! ESSERE NULL PER POTER EFFETTUARE IL CONTROLLO SUL TRACCIAMENTO
			 */
			 /*
    		this.idEGov[i]="";
    		this.mittenti[i]="";
    		this.tipoMittenti[i]="";
    		this.indirizzoTelematicoMittenti[i]="";
    		this.destinatari[i]="";
    		this.tipoDestinatari[i]="";
    		this.indirizzoTelematicoDestinatari[i]="";
    		this.servizi[i]="";
    		this.tipoServizio[i]="";
    		this.azioni[i]="";
    		this.profiliCollaborazione[i]="";
    		this.servizioCorrelatoProfiliCollaborazione[i]="";
    		this.tipoProfiliCollaborazione[i]="";
    		this.collaborazioni[i] = ""; 
            this.inoltroProfiliTrasmissioni[i] = ""; 
            this.confermaRicezioneProfiliTrasmissioni[i] = ""; 
            this.numeroProgressivoSequenze[i] = ""; 
			  */

			this.nomiBuste[i] = this.file[i].getName();
			this.busteDaInviare[i]="";


			this.min_gdo[i] = null;

			this.buffChar = (new String(BusteEGovDaFile.leggi(this.file[i]))).trim().toCharArray();
			this.estraiCampi(i);
			if(this.nomiBuste[i].equals("bustaIdentificatoreNonValido.xml"))
			{
				this.idEGov[i]=UtilitiesEGov.getIDEGov("Errato",this.mittenti[i]+"SPCoopIT");
			}else if(this.nomiBuste[i].equals("bustaMittenteSenzaValoreIdentificativoParte.xml")){
				this.idEGov[i]=UtilitiesEGov.getIDEGov("MinisteroFruitore","MinisteroFruitoreSPCoopIT");
			}else{
				this.idEGov[i]=UtilitiesEGov.getIDEGov(this.mittenti[i],this.mittenti[i]+"SPCoopIT");
			}
			this.scriviBusta(i);
			
			if(!this.nomiBuste[i].equals("bustaOraRegistrazioneNonValida.xml") &&
					!this.nomiBuste[i].equals("bustaSenzaOraRegistrazione.xml")){
				//System.out.println("BUSTA: "+this.nomiBuste[i]);
				//System.out.println("CONTENUTO BUSTA: "+this.busteDaInviare[i]);
				int indexOfStartOraRegistrazione = this.busteDaInviare[i].indexOf("<eGov_IT:OraRegistrazione");
				if(indexOfStartOraRegistrazione>0){
					int indexOfEndOraRegistrazione = this.busteDaInviare[i].indexOf(">",indexOfStartOraRegistrazione);
					String first = this.busteDaInviare[i].substring(0,indexOfEndOraRegistrazione+1);
					//System.out.println("FIRST: "+first);
					int indexOfFine = this.busteDaInviare[i].indexOf("<",indexOfEndOraRegistrazione);
					String after = this.busteDaInviare[i].substring(indexOfFine,this.busteDaInviare[i].length());
					//System.out.println("AFTER; "+after);
					String tmp = first + SPCoopUtils.getDate_eGovFormat()+ after;
					this.busteDaInviare[i] = tmp;
				}
			}
		}
	}
	private void estraiCampi(int i){
		String tag = "";
		String tagCampo = "";
		String campo = "";
		boolean inTag = false;
		boolean inCampo = false;
		boolean termineTag = false;
		boolean termineCampo = false;
		int contatore = 0;
		for(int j = 0;j<this.buffChar.length;j++){
			if(this.buffChar[j]=='<'){
				inTag = true;
				inCampo= false;
				termineCampo = true;
			}
			if(inTag&&!termineCampo){
				tag = (tag+this.buffChar[j]);
			}
			if(this.buffChar[j]=='>'){
				inTag = false;
				inCampo= true;
				termineTag = true;
			}
			if(inCampo&&!termineCampo){
				if(this.buffChar[j]!='>'){campo= (campo+this.buffChar[j]);}
			}
			if(termineTag){
				termineTag=false;
				tag = tag.trim();
				tagCampo = tag;
				tag = "";
			}
			if(termineCampo){
				termineCampo=false;
				campo = campo.trim();
				contatore=contatore+this.esamina(i, j, tagCampo,campo);
				campo = "";
			}
		}
	}
	private int esamina(int i, int j, String tagCampo, String campo){

		if((tagCampo.contains("eGov_IT:Mittente"))&&!(tagCampo.contains("/eGov_IT:Mittente"))){
			return this.esaminaMittente(i,j);
		}

		else if((tagCampo.contains("eGov_IT:Destinatario"))&&!(tagCampo.contains("/eGov_IT:Destinatario"))){
			return this.esaminaDestinatario(i,j);
		}

		else if(tagCampo.contains("eGov_IT:Servizio")&&!tagCampo.contains("/eGov_IT:Servizio")){
			if(this.servizi[i]==null){
				this.servizi[i]=campo;
				this.tipoServizio[i]=this.estraiTipo(tagCampo);
			}
			return 1;
		}

		else if(tagCampo.contains("eGov_IT:Azione")&&!tagCampo.contains("/eGov_IT:Azione")){
			if(this.azioni[i]==null){
				this.azioni[i]=campo;
			}
			return 1;
		}

		else if(tagCampo.contains("eGov_IT:ProfiloCollaborazione")&&!tagCampo.contains("/eGov_IT:ProfiloCollaborazione")){
			if(this.profiliCollaborazione[i]==null){
				this.profiliCollaborazione[i]=campo;
				this.servizioCorrelatoProfiloCollaborazione[i]=this.estraiAttributo(tagCampo,this.ATTRIBUTO_SERVIZIO_CORRELATO);
				this.tipoServizioCorrelatoProfiloCollaborazione[i]=this.estraiAttributo(tagCampo,this.ATTRIBUTO_TIPO);
			}
			return 1;
		}

		else if(tagCampo.contains("eGov_IT:Collaborazione")&&!tagCampo.contains("/eGov_IT:Collaborazione")){
			if(this.collaborazioni[i]==null){
				this.collaborazioni[i] = campo;
			}
			return 1;
		}

		else if(tagCampo.contains("eGov_IT:Messaggio")&&!(tagCampo.contains("/eGov_IT:Messaggio"))){
			return this.esaminaMessaggio(i,j);
		}

		else if(tagCampo.contains("eGov_IT:ProfiloTrasmissione")&&!tagCampo.contains("/eGov_IT:ProfiloTrasmissione")){
			
			if(this.inoltroProfiliTrasmissioni[i]==null){
				String tmp1 = tagCampo;
				String con = this.estraiAttributo(tmp1,this.ATTRIBUTO_CONFERMA_RICEZIONE);
				if(con!=null && "true".equals(con))
					this.confermaRicezioneProfiliTrasmissioni[i]= true;
				
				String tmp2 = tagCampo;
				this.inoltroProfiliTrasmissioni[i]=this.estraiAttributo(tmp2,this.ATTRIBUTO_INOLTRO);
			}
			
			return 1;
		}

		else if(tagCampo.contains("eGov_IT:Sequenza")&&!tagCampo.contains("/eGov_IT:Sequenza")){
			String tmp = this.estraiAttributo(tagCampo,this.ATTRIBUTO_NUMERO_PROGRESSIVO);
			if(tmp!=null){
				try{
					this.numeroProgressivoSequenze[i]=Integer.parseInt(tmp);
				}catch(Exception e){}
			}
			return 1;
		}

		return 0;
	}

	private String estraiTipo(String tag){
		if(tag.contains("tipo"))
		{
			String[] a = tag.split(" ");
			int z;
			char[] c;
			String tipo;
			boolean flag = false;
			for(int h = 0;h<a.length;h++){
				c = a[h].toCharArray();
				z = 0;
				tipo = "";
				while(z+3<c.length){
					if((c[z]=='t')||(c[z]=='T')){if((c[z+1]=='i')||(c[z+1]=='I')){if((c[z+2]=='p')||(c[z+2]=='P')){if((c[z+3]=='o')||(c[z+3]=='O')){
						if(z+4<c.length){
							if(c[z+4]=='='){
								if(z+5<c.length){
									if(c[z+5]=='"'){
										int j = z+6;
										while((j<c.length)&&(c[j]!='"')){
											tipo = tipo+c[j];
											j++;
										}
										return tipo;
									}
									int j = z+5;
									while((j<c.length)&&(c[j]!='"')){
										tipo = tipo+c[j];
										j++;
									}
									return tipo;
								}else{flag = true;}
							}
						}else{flag = true;}
					}}}}
					z++;
				}
				if(flag){
					if(a[h].length()==1){
						c= a[h+2].toCharArray();
					}else{
						c= a[h+1].toCharArray();
					}
					z=0; tipo = "";
					while(z<c.length&&(c[z]!='"')){
						tipo = tipo+c[z];
						z++;
					}
					return tipo;
				}
			}
		}
		return null;
	}

	/**/
	private String estraiAttributo(String tag,String attrib){
		try{
			if(tag.toLowerCase().contains(attrib.toLowerCase())){
				String[] a = (tag).split(" ");
				//char[] att = (attrib).toCharArray();
				char[] c;
				int z;
				//int p;
				//boolean flag = false;
				for(int h = 0;h<a.length;h++){
					c = a[h].toCharArray();
					z = 0;
					/*while(z+att.length<c.length&&!flag){
						
						if(ceckNomeAtt(c,z,att)){
							p = z+att.length;
							if(p<c.length)flag=true;
							if(!flag&&c[p]=='=')p++;
							if(p<c.length)flag=true;
							if(!flag&&c[p]=='"')p++;
							if(!flag){
								
								if(attrib.equals("confermaRicezione"))
									System.out.println("ENTRO 1 ... ["+new String(c)+"].indexOf("+attrib+")="+((new String(c)).indexOf(attrib))+"");
								
								if((new String(c)).indexOf(attrib)!=-1){
									String tmp = scriviAttValue(c,p,attrib);
									if(attrib.equals("confermaRicezione"))
										System.out.println("RETURN1 ["+tmp+"]");
									if(attrib.equals("inoltro"))
										System.out.println("INOLTRO RETURN1 ["+tmp+"]");
									return tmp;
								}
							}
						}
						z++;
					}
					if(flag){
					*/
						if(a[h+1].length()==1)c=a[h+2].toCharArray();
						else c= a[h+1].toCharArray();
						z=0;
						if(c[z]=='='){z++;}
						if(c[z]=='"'){z++;}
						
						if((new String(c)).indexOf(attrib)!=-1){
							String tmp = scriviAttValue(c,z,attrib);
							return tmp;
						}
					//}
				}
			}
			return null;
		}catch(java.lang.NullPointerException e ){
			return null;
		}
		catch(java.lang.ArrayIndexOutOfBoundsException e){
			return null;
		}

	}/**/
	protected boolean ceckNomeAtt(char[] buff, int p, char[] att){
		int s = p;
		try{
			while(p<att.length){
				if(buff[s]!=att[p-s]){
					break;
				}else{
					p++;
				}
			}
		}catch(java.lang.NullPointerException e){return false;}
		catch(java.lang.ArrayIndexOutOfBoundsException e){return false;}
		if(p==att.length)return true;
		else return false;
	}
	private String scriviAttValue(char[] buff,int s,String attrNome){
		String attributo = "";
		try{
			// mi porto a dove inizia l'attributo
			String tmp = new String(buff);
				
			s = s+ tmp.indexOf(attrNome) + attrNome.length();
			
			// Elimino '='
			while((s<buff.length)&&(buff[s]!='=')){
				s++;
			}
			s++;
			
			// Elimino '"'
			while((s<buff.length)&&(buff[s]!='"')){
				s++;
			}
			s++;
			
			while((s<buff.length)&&(buff[s]!='"')){
				attributo = attributo+buff[s];
				s++;
			}
		}catch(java.lang.NullPointerException e){return "";}
		catch(java.lang.ArrayIndexOutOfBoundsException e){return "";}
		return attributo.trim();
	}	

	private int esaminaMittente(int i,int j){
		String tag = "";
		String tagCampo = "";
		String campo = "";
		boolean inTag = false;
		boolean inCampo = false;
		boolean termineTag = false;
		boolean termineCampo = false;
		boolean flag = false;
		while(j<this.buffChar.length){		
			if(this.buffChar[j]=='<'){
				inTag = true;
				inCampo= false;
				termineCampo = true;
			}
			if(inTag&&!termineCampo){
				tag = (tag+this.buffChar[j]);//.trim();
			}
			if(this.buffChar[j]=='>'){
				inTag = false;
				inCampo= true;
				termineTag = true;
			}
			if(inCampo&&!termineCampo){
				if(this.buffChar[j]!='>'){campo= (campo+this.buffChar[j]);}//.trim();}
			}
			if(termineTag){
				termineTag=false;
				tag =tag.trim();
				tagCampo = tag;
				if(tag.contains("/eGov_IT:Mittente")){return 0;}
				if(tag.contains("eGov_IT:IdentificativoParte")){flag= true;}
				tag="";
			}
			if(termineCampo){
				termineCampo=false;	
				if(flag){
					termineCampo=false;
					if(this.mittenti[i]==null){
						this.mittenti[i]=campo;
						this.tipoMittenti[i]=this.estraiTipo(tagCampo);
						this.indirizzoTelematicoMittenti[i]=this.estraiAttributo(tagCampo,this.ATTRIBUTO_INDIRIZZO_TELEMATICO);
					}
					return 1;
				}
				campo="";
			}	
			j++;
		}
		return 0;
	}
	private int esaminaDestinatario(int i, int j){
		String tag = "";
		String tagCampo = "";
		String campo = "";
		boolean inTag = false;
		boolean inCampo = false;
		boolean termineTag = false;
		boolean termineCampo = false;
		boolean flag = false;
		while(j<this.buffChar.length){
			if(this.buffChar[j]=='<'){
				inTag = true;
				inCampo= false;
				termineCampo = true;
			}
			if(inTag&&!termineCampo){
				tag = (tag+this.buffChar[j]);//.trim();
			}
			if(this.buffChar[j]=='>'){
				inTag = false;
				inCampo= true;
				termineTag = true;
			}
			if(inCampo&&!termineCampo){
				if(this.buffChar[j]!='>'){campo= (campo+this.buffChar[j]);}//.trim();}
			}
			if(termineTag){
				termineTag=false;
				tag= tag.trim();
				tagCampo = tag;
				if(tag.contains("/eGov_IT:Destinatario")){return 0;}
				if(tag.contains("eGov_IT:IdentificativoParte")){flag= true;}
				tag="";
			}
			if(termineCampo){
				termineCampo=false;
				if(flag){
					termineCampo=false;
					if(this.destinatari[i]==null){
						this.destinatari[i]=campo;
						this.tipoDestinatari[i]=this.estraiTipo(tagCampo);
						this.indirizzoTelematicoDestinatari[i]=this.estraiAttributo(tagCampo,this.ATTRIBUTO_INDIRIZZO_TELEMATICO);
					}
					return 1;
				}
				campo="";
			}
			j++;
		}
		return 0;
	}
	private int esaminaMessaggio(int i, int j){
		String tag = "";
		String campo = "";
		boolean inTag = false;
		boolean inCampo = false;
		boolean termineTag = false;
		boolean termineCampo = false;
		boolean flag = false;
		while(j<this.buffChar.length){
			if(this.buffChar[j]=='<'){
				inTag = true;
				inCampo= false;
				termineCampo = true;
			}
			if(inTag&&!termineCampo){
				tag = (tag+this.buffChar[j]);
			}
			if(this.buffChar[j]=='>'){
				inTag = false;
				inCampo= true;
				termineTag = true;
			}
			if(inCampo&&!termineCampo){
				if(this.buffChar[j]!='>'){campo= (campo+this.buffChar[j]);}
			}
			if(termineTag){
				termineTag=false;
				if(tag.contains("/eGov_IT:Messaggio")){return 0;}
				if(tag.contains("eGov_IT:Identificatore")){flag= true;this.startID[i] = j+1;}
				tag="";
			}
			if(termineCampo){
				termineCampo=false;
				if(flag){
					termineCampo=false;
					this.stopID[i] = j;
					return 1;
				}
				campo="";
			}
			j++;
		}
		return 0;
	}

	private void scriviBusta(int i){
		if((this.startID[i]!=-1)&&(this.stopID[i]!=-1)){
			for(int j = 0;j<this.buffChar.length;j++){
				if((j<this.startID[i])||(this.stopID[i]<=j))
					this.busteDaInviare[i]=this.busteDaInviare[i]+this.buffChar[j];
				if(j==this.startID[i])
					this.busteDaInviare[i]=this.busteDaInviare[i]+this.idEGov[i];
			}
		}else{
			this.busteDaInviare[i] = new String(this.buffChar);
			this.idEGov[i]="BustaSenzaID_"+i;
		}	
	}	

	private static byte[] leggi(java.io.File file){
		if (file == null){return null;}
		byte[] buff;
		try{
			int fileSize = (int) file.length();
			buff = new byte[fileSize];
			java.io.FileInputStream inFileFromHD = null;
			try{
				inFileFromHD = new java.io.FileInputStream(file);
				inFileFromHD.read(buff);
				return buff;
			}finally{
				try{
					if(inFileFromHD!=null){
						inFileFromHD.close();
					}
				}catch(Exception eClose){}
			}
		}catch(java.io.FileNotFoundException e){System.err.println(e);}
		catch(java.io.IOException e){System.err.println(e);}
		return (buff = null);
	}
}
