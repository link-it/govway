package org.openspcoop2.web.monitor.core.mbean;

import org.openspcoop2.core.commons.search.GeneralInfo;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import it.link.pdd.tools.license.LicenseVerifier;
import it.link.pdd.tools.license.LicenseVerifierException;
import it.link.pdd.tools.license.LicenseVerifierExpiredException;
import it.link.pddoe.license.Modulo;
import it.link.pddoe.license.PddOE;
import it.link.pddoe.license.constants.CodiceLicenza;
import it.link.pddoe.license.constants.CodiceModulo;
import it.link.pddoe.license.constants.CodiceProdotto;
import it.link.pddoe.license.constants.TipoLicenza;
import it.link.pddoe.license.constants.TipoModulo;
import it.link.pddoe.license.constants.TipoProdotto;
import it.link.pddoe.license.utils.ConverterUtilities;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.VersionUtilities;
import org.openspcoop2.utils.date.DateManager;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

public class LicenseBean extends PdDBaseBean<PddOE, String, IService<GeneralInfo, String>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private byte[] licenseByte = null;
	private List<UploadItem> files = new ArrayList<UploadItem>();
	private boolean caricato = false;
	private String fileName = null;

	private String informazioneLicenzaModificata = null;
	public String getInformazioneLicenzaModificata() {
		return this.informazioneLicenzaModificata;
	}

	// Contiene le informazioni relative alla licenza da aggiornare
	private GeneralInfo licenzaForm = null;


	@Override
	public PddOE getSelectedElement() {
		return ConsoleStartupListener.license;
	}

	public String getTitle() throws Exception{
		
		LoginBean loggedUser = Utility.getLoginBean();
		
		if(ConsoleStartupListener.titoloPddMonitor!=null){
			//return PddMonitorProperties.getInstance(log).getPddMonitorTitle() + ConsoleStartupListener.titoloPddMonitor;
			return PddMonitorProperties.getInstance(log).getPddMonitorTitle(); // L'informazione sulla versione e' stata spostata nella pagina delle informazioni
		}
		else{
			if(loggedUser != null && loggedUser.isLoggedIn())
				return PddMonitorProperties.getInstance(log).getPddMonitorTitle() + " [Unlicensed Version]";
			else 
				return PddMonitorProperties.getInstance(log).getPddMonitorTitle() ;
		}
		
	}
	
	public String about(){
		this.files = new ArrayList<UploadItem>();
		this.licenseByte = null;
		this.fileName = null;
		this.caricato = false;
		this.licenzaForm = null;
		this.informazioneLicenzaModificata = null; // anche questa informativa deve essere azzerata, poiche pdd e pddMonitor possono risiedere su A.S. differenti
		return "about";
	}
	
	public void fileUploadListener(UploadEvent event) {
		this.fileUploadErrorMessage = null;
		UploadItem item = event.getUploadItem();

		if(item!= null && item.getData() != null){
			this.licenseByte = item.getData();
			this.fileName = item.getFileName();

			// se il messaggio di test e' diverso da null il caricamento e' andato a buon fine
			if(this.licenseByte != null){
				this.caricato = true;
			}

			// verifica
			try {
				
				this.validaLicenza();				
								
			} catch (Exception e) {
				this.files = new ArrayList<UploadItem>();
				this.licenseByte = null;
				this.fileName = null;
				this.caricato = false;

				LicenseBean.log.error(e.getMessage(), e);
				
				String message = "Il formato del file non e' valido. Riprovare con un altro file.";
				if(Utilities.existsInnerException(e, LicenseVerifierException.class)){
					try{
						Throwable eInner = Utilities.getInnerException(e, LicenseVerifierException.class);
						if(eInner!=null){
							message = eInner.getMessage();
						}
					}catch(Throwable eClose){}
				}
				else if(Utilities.existsInnerException(e, LicenseVerifierExpiredException.class)){
					try{
						Throwable eInner = Utilities.getInnerException(e, LicenseVerifierExpiredException.class);
						if(eInner!=null){
							message = eInner.getMessage();
						}
					}catch(Throwable eClose){}
				}
				
				MessageUtils.addErrorMsg(message);
				this.fileUploadErrorMessage = message;
				return;
			} 

						
			//salvataggio
			try {

				if(this.licenzaForm==null){
					this.licenzaForm = new GeneralInfo();
				}
				this.licenzaForm.setLicense(this.licenseByte);
				this.service.store(this.licenzaForm);
				
				MessageUtils.addInfoMsg("Licenza aggiornata con successo.");

				this.informazioneLicenzaModificata = "La nuova licenza sarà attivata al prossimo riavvio della Porta di Dominio";
				
			} catch (Exception e) {
				this.files = new ArrayList<UploadItem>();
				this.licenseByte = null;
				this.fileName = null;
				this.caricato = false;
				this.licenzaForm=null;
				
				ConsoleStartupListener.resetPddOE_License();
				
				MessageUtils.addErrorMsg("Si e' verificato un errore durante il salvataggio della licenza.");
				LicenseBean.log.error(e.getMessage(), e);
				this.fileUploadErrorMessage = "Si e' verificato un errore durante il salvataggio della licenza.";
				return;
			}
			
		}
		else {
			MessageUtils
			.addErrorMsg("Si e' verificato un errore durante il caricamento del file. Riprovare.");
			this.fileUploadErrorMessage = "Si e' verificato un errore durante il caricamento del file. Riprovare.";
		}
	}

	public void validaLicenza() throws Exception{
		
		ConsoleStartupListener.updatePddOE_License(this.licenseByte);
		
	}

	public String getTipoLicenza() throws Exception{
		if(ConsoleStartupListener.license!=null && 
				ConsoleStartupListener.license.getLicenza()!=null && 
				ConsoleStartupListener.license.getLicenza().getCodice()!=null){
			TipoLicenza tipo = ConverterUtilities.toTipoLicenza(CodiceLicenza.toEnumConstant(ConsoleStartupListener.license.getLicenza().getCodice()));
			if(tipo!=null){
				return tipo.getValue();
			}
		}
		return null;
	}
	
	public String getTipoProdotto() throws Exception{
		if(ConsoleStartupListener.license!=null && 
				ConsoleStartupListener.license.getProdotto()!=null && 
				ConsoleStartupListener.license.getProdotto().getCodice()!=null){
			TipoProdotto tipo = ConverterUtilities.toTipoProdotto(CodiceProdotto.toEnumConstant(ConsoleStartupListener.license.getProdotto().getCodice()));
			if(tipo!=null){
				return tipo.getValue();
			}
		}
		return null;
	}
	
	public String getIntestatario(){
		if(ConsoleStartupListener.license!=null && 
				ConsoleStartupListener.license.getLicenza()!=null && 
				ConsoleStartupListener.license.getLicenza().getIntestatario()!=null){
			if("Unlicensed Version".equals(ConsoleStartupListener.license.getLicenza().getIntestatario())){
				return "Licenza non presente";
			}
			else{
				return ConsoleStartupListener.license.getLicenza().getIntestatario();	
			}
		}
		return  null;
	}
	
	public String getDataScadenza() throws Exception{
		if(ConsoleStartupListener.license!=null && 
				ConsoleStartupListener.license.getLicenza()!=null && 
				ConsoleStartupListener.license.getLicenza().getScadenza()!=null){
			return LicenseVerifier.DATE_FORMAT.format(ConsoleStartupListener.license.getLicenza().getScadenza());
		}
		return null;
	}

	public boolean isLicenzaScaduta() throws Exception{
		if(ConsoleStartupListener.license!=null && 
				ConsoleStartupListener.license.getLicenza()!=null && 
				ConsoleStartupListener.license.getLicenza().getScadenza()!=null){
			return DateManager.getDate().after(ConsoleStartupListener.license.getLicenza().getScadenza());
		}
		return false;
	}

	public String getModuliAbilitati(){
		if(ConsoleStartupListener.license!=null && 
				ConsoleStartupListener.license.getProdotto()!=null && 
				ConsoleStartupListener.license.getProdotto().getModuli()!=null && 
						ConsoleStartupListener.license.getProdotto().getModuli().sizeModuloList()>0){
			StringBuffer bf = new StringBuffer();
			for (Modulo modulo : ConsoleStartupListener.license.getProdotto().getModuli().getModuloList()) {
				if(bf.length()>0){
					bf.append("<br/>");
				}
				try{
					TipoModulo tipoModulo = ConverterUtilities.toTipoModulo(CodiceModulo.toEnumConstant(modulo.getCodice(), true));
					bf.append(tipoModulo.getValue());
				}catch(Exception e){
					log.error("Codice sconosciuto ["+modulo.getCodice()+"], error: "+e.getMessage(),e);
					bf.append("Codice sconosciuto ["+modulo.getCodice()+"]");
				}
			}
			return bf.toString();
		}
		return  null;
	}
	
	public String getIdProdotto() throws Exception{
		StringBuffer bf = new StringBuffer();
		if(ConsoleStartupListener.license!=null && 
				ConsoleStartupListener.license.getProdotto()!=null && 
				ConsoleStartupListener.license.getProdotto().getNome()!=null){
			bf.append(ConsoleStartupListener.license.getProdotto().getNome());
		}
		if(ConsoleStartupListener.license!=null && 
				ConsoleStartupListener.license.getProdotto()!=null && 
				ConsoleStartupListener.license.getProdotto().getVersione()!=null){
			if(bf.length()>0){
				bf.append(" ");
				bf.append(ConsoleStartupListener.license.getProdotto().getVersione().getProduct());
				bf.append(".");
				bf.append(ConsoleStartupListener.license.getProdotto().getVersione().getMajor());
				bf.append(".");
				bf.append(ConsoleStartupListener.license.getProdotto().getVersione().getMinor());
			}
		}
		if(bf.length()>0){
			String pVersion = bf.toString();
			String buildVersion = null;
			try {
				buildVersion = VersionUtilities.readBuildVersion();
			}catch(Exception e) {}
			if(buildVersion!=null) {
				pVersion = pVersion + " (build "+buildVersion+")";
			}
			return pVersion;
		}
		return null;
	}
	
	public String getCopyright(){
		return CostantiPdD.OPENSPCOOP2_COPYRIGHT;
	}
	
	public final List<UploadItem> getFiles() {
		return this.files;
	}

	public final void clear(final ActionEvent e) {
		this.files = new ArrayList<UploadItem>();
		this.licenseByte = null;
		this.fileName = null;
		this.caricato = false;
		this.fileUploadErrorMessage = null;
	}

	public int getNumeroFile(){
		return 1 - this.files.size();
	}

	public String getMessaggioCaricato(){
		if(this.caricato){
			if(this.licenseByte != null && this.licenseByte.length > 0 && this.getNumeroFile() != 1){
				return "";
			}
		}
		else
			if(this.licenseByte != null && this.licenseByte.length > 0){
				return "";
			}

		return null;
	}

	public String getNomeFile() {
		return this.fileName;
	}

	public long getDimensioneFile(){
		if(this.licenseByte!= null)
			return this.licenseByte.length;

		return 0;
	}
	
	private String fileUploadErrorMessage;
	public String getFileUploadErrorMessage() {
		return this.fileUploadErrorMessage;
	}

}
