/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.testsuite.zap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;
import org.zaproxy.clientapi.core.ClientApi;


/**
 * ZAPContext
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ZAPReport {

	public final static String NONE = "none";
	
	public static String suffix = " report-title report-description report-includedConfidences report-includedRisks report-reportFileNamePattern report-dir report-display [report-template1 report-theme1 report-sections1] ... [report-templateN report-themeN report-sectionsN]";
	
	public ZAPReport(String[] args, String mainClass, String usage, int index, ClientApi clientApi) throws Exception {
		String usageMsg = "\nUsage: mainClass "+usage+suffix;
		if(args==null || args.length<=0) {
			throw new Exception("ERROR: arguments undefined"+usageMsg);
		}

		if(args.length>(index)) {
			this.title = args[index+0];
		}
		if(this.title==null || StringUtils.isEmpty(this.title)) {
			throw new Exception("ERROR: argument 'report-title' undefined"+usageMsg);
		}
		
		
		if(args.length>(index+1)) {
			this.description = args[index+1];
		}
		if(this.description==null || StringUtils.isEmpty(this.description)) {
			throw new Exception("ERROR: argument 'report-description' undefined"+usageMsg);
		}
		if(NONE.equalsIgnoreCase(this.description)) {
			this.description = null;
		}
		


		if(args.length>(index+2)) {
			this.includedConfidences = args[index+2];
		}
		if(this.includedConfidences==null || StringUtils.isEmpty(this.includedConfidences)) {
			throw new Exception("ERROR: argument 'report-includedConfidences' undefined"+usageMsg);
		}
		if(NONE.equalsIgnoreCase(this.includedConfidences)) {
			this.includedConfidences = null;
		}
		else {
			List<String> includedConfidences = getAllIncludedConfidences();
			String [] split = this.includedConfidences.split("\\|");
			if(split!=null && split.length>1) {
				for (String s : split) {
					if(!includedConfidences.contains(s)) {
						throw new Exception("ERROR: argument 'report-includedConfidences'='"+this.includedConfidences+"' unknown confidence '"+s+"' ; usable: "+includedConfidences+usageMsg);
					}	
				}
			}
		}
		
		if(args.length>(index+3)) {
			this.includedRisks = args[index+3];
		}
		if(this.includedRisks==null || StringUtils.isEmpty(this.includedRisks)) {
			throw new Exception("ERROR: argument 'report-includedRisks' undefined"+usageMsg);
		}
		if(NONE.equalsIgnoreCase(this.includedRisks)) {
			this.includedRisks = null;
		}
		else {
			List<String> includedRisks = getAllIncludedRisks();
			String [] split = this.includedRisks.split("\\|");
			if(split!=null && split.length>1) {
				for (String s : split) {
					if(!includedRisks.contains(s)) {
						throw new Exception("ERROR: argument 'report-includedRisks'='"+this.includedRisks+"' unknown confidence '"+s+"' ; usable: "+includedRisks+usageMsg);
					}	
				}
			}
		}
		
		if(args.length>(index+4)) {
			this.fileNamePattern = args[index+4];
		}
		if(this.fileNamePattern==null || StringUtils.isEmpty(this.fileNamePattern)) {
			throw new Exception("ERROR: argument 'report-fileNamePattern' undefined"+usageMsg);
		}
		
		if(args.length>(index+5)) {
			this.dir = args[index+5];
		}
		if(this.dir==null || StringUtils.isEmpty(this.dir)) {
			throw new Exception("ERROR: argument 'report-dir' undefined"+usageMsg);
		}
		File fDir = new File(this.dir);
		if(fDir.exists()) {
			if(!fDir.isDirectory()) {
				throw new Exception("ERROR: argument 'report-dir' '"+this.dir+"' isn't a directory"+usageMsg);
			}
			if(!fDir.canWrite()) {
				throw new Exception("ERROR: argument 'report-dir' '"+this.dir+"' cannot write"+usageMsg);
			}
			if(!fDir.canRead()) {
				throw new Exception("ERROR: argument 'report-dir' '"+this.dir+"' cannot read"+usageMsg);
			}
		}
		else {
			FileSystemUtilities.mkdir(fDir);
		}

		String sDisplay = null;
		if(args.length>(index+6)) {
			sDisplay = args[index+6];
		}
		if(sDisplay==null || StringUtils.isEmpty(sDisplay)) {
			throw new Exception("ERROR: argument 'report-display' undefined"+usageMsg);
		}
		try {
			this.display = Boolean.valueOf(sDisplay);
		}catch(Throwable t) {
			throw new Exception("ERROR: argument 'report-display' uncorrect ("+sDisplay+")"+usageMsg);
		}
		
		
		String sTemplates = null;
		if(args.length>(index+7)) {
			sTemplates = args[index+7];
		}
		if(sTemplates==null || StringUtils.isEmpty(sTemplates)) {
			throw new Exception("ERROR: argument 'report-templates' undefined"+usageMsg);
		}
		
		this.templates = new ArrayList<>();
		
		String [] tmp = sTemplates.split(" ");
		if(tmp==null || tmp.length<=0) {
			throw new Exception("ERROR: argument 'report-templates' undefined"+usageMsg);
		}
		
		for (int i = 0; i < tmp.length; ) {
						
			String template = tmp[i];
			if(template==null || StringUtils.isEmpty(template)) {
				throw new Exception("ERROR: argument 'report-template' undefined"+usageMsg);
			}
			List<String> templates = getTemplates(clientApi);
			if(!templates.contains(template)) {
				throw new Exception("ERROR: argument 'report-template'='"+template+"' unknown; usable: "+templates+usageMsg);
			}
			i++;
			
			String theme = null;
			if(tmp.length>i) {
				theme = tmp[i];
			}
			if(theme==null || StringUtils.isEmpty(theme)) {
				throw new Exception("ERROR: argument 'report-theme' undefined"+usageMsg);
			}
			if(NONE.equalsIgnoreCase(theme)) {
				theme = null;
			}
			else {
				List<String> themes = getThemes(clientApi, template);
				if(themes==null || themes.isEmpty()) {
					throw new Exception("ERROR: argument 'report-theme'='"+theme+"' unknown; template '"+template+"' without theme"+usageMsg);
				}
				if(!themes.contains(theme)) {
					throw new Exception("ERROR: argument 'report-theme'='"+theme+"' unknown; usable: "+themes+usageMsg);
				}
			}
			i++;
			
			String sections = null;
			if(tmp.length>i) {
				sections = tmp[i];
			}
			if(sections==null || StringUtils.isEmpty(sections)) {
				throw new Exception("ERROR: argument 'report-sections' undefined"+usageMsg);
			}
			if(NONE.equalsIgnoreCase(sections)) {
				sections = null;
			}
			else {
				List<String> sectionsCheck = getSections(clientApi, template);
				if(sectionsCheck==null || sectionsCheck.isEmpty()) {
					throw new Exception("ERROR: argument 'report-sections'='"+sections+"' unknown; template '"+template+"' without sections"+usageMsg);
				}
				String [] split = sections.split("\\|");
				if(split!=null && split.length>1) {
					for (String s : split) {
						if(!sectionsCheck.contains(s)) {
							throw new Exception("ERROR: argument 'report-sections'='"+sections+"' unknown section '"+s+"' ; usable: "+sectionsCheck+usageMsg);
						}	
					}
				}
			}
			i++;
			
			ZAPReportTemplate t = new ZAPReportTemplate();
			t.template = template;
			t.theme = theme;
			t.sections = sections;
			t.fileName = this.fileNamePattern.replace(".ext", getExt(t.template));
			this.templates.add(t);
		}

		if(this.templates.isEmpty()) {
			throw new Exception("ERROR: argument 'report-template' undefined"+usageMsg);
		}
	}
	
	private String title;
	private String description;
	private String includedConfidences;
	private String includedRisks;
	private String fileNamePattern;
	private String dir;
	private boolean display;
	private List<ZAPReportTemplate> templates;
		

	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileNamePattern() {
		return this.fileNamePattern;
	}
	public void setFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}

	public String getDir() {
		return this.dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}

	public boolean isDisplay() {
		return this.display;
	}
	public void setDisplay(boolean display) {
		this.display = display;
	}

	public void setIncludedConfidences(String includedConfidences) {
		this.includedConfidences = includedConfidences;
	}
	public String getIncludedConfidences() {
		return this.includedConfidences;
	}
	public String getIncludedRisks() {
		return this.includedRisks;
	}
	public void setIncludedRisks(String includedRisks) {
		this.includedRisks = includedRisks;
	}
	
	public List<ZAPReportTemplate> getTemplates() {
		return this.templates;
	}
	public void setTemplates(List<ZAPReportTemplate> templates) {
		this.templates = templates;
	}
	
	
	public static List<String> getTemplates(ClientApi clientApi) throws Exception {
		
		List<String> l = new ArrayList<>();
		
		ApiResponse response = clientApi.reports.templates();
        ApiResponseList list = ((ApiResponseList) response);
        for (ApiResponse res : list.getItems()) {
			 ApiResponseElement re = (ApiResponseElement) res;
			 //System.out.println("Template '"+re.getName()+"'='"+re.getValue()+"'");
			 l.add(re.getValue());
			 // Template 'template'='traditional-json'
			 // Template 'template'='traditional-pdf'
			 // Template 'template'='traditional-md'
			 // Template 'template'='traditional-xml'
			 // Template 'template'='sarif-json'
			 // Template 'template'='traditional-html'
			 // Template 'template'='modern'
			 // Template 'template'='high-level-report'
			 // Template 'template'='traditional-xml-plus'
			 // Template 'template'='risk-confidence-html'
			 // Template 'template'='traditional-json-plus'
			 // Template 'template'='traditional-html-plus'
		}
        
        return l;
	}
	
	public static List<String> getThemes(ClientApi clientApi, String template) throws Exception {

		List<String> l = new ArrayList<>();
		
		ApiResponse response =  clientApi.reports.templateDetails(template);
		ApiResponseSet responseSet = (ApiResponseSet) response;
		/*for (String key : responseSet.getKeys()) {
			System.out.println("Key["+key+"] ["+responseSet.getValue(key)+"]");
		}*/
		//System.out.println("NAME: ["+responseSet.getValue("name")+"]");
		//System.out.println("FORMAT: ["+responseSet.getValue("format")+"]");
		ApiResponseList list = ((ApiResponseList) responseSet.getValue("themes"));
		// System.out.println("themes: "+list.getItems().size());
		for (ApiResponse res : list.getItems()) {
			 ApiResponseElement re = (ApiResponseElement) res;
			 //System.out.println("Theme '"+re.getName()+"'='"+re.getValue()+"'");
			 l.add(re.getValue());
		}
	
		return l;
	}
	public static List<String> getSections(ClientApi clientApi, String template) throws Exception {

		List<String> l = new ArrayList<>();
		
		ApiResponse response =  clientApi.reports.templateDetails(template);
		ApiResponseSet responseSet = (ApiResponseSet) response;
		/*for (String key : responseSet.getKeys()) {
			System.out.println("Key["+key+"] ["+responseSet.getValue(key)+"]");
		}*/
		//System.out.println("NAME: ["+responseSet.getValue("name")+"]");
		//System.out.println("FORMAT: ["+responseSet.getValue("format")+"]");
		ApiResponseList list = ((ApiResponseList) responseSet.getValue("sections"));
		//System.out.println("sections: "+list.getItems().size());
		for (ApiResponse res : list.getItems()) {
			 ApiResponseElement re = (ApiResponseElement) res;
			 //System.out.println("Section '"+re.getName()+"'='"+re.getValue()+"'");
			 l.add(re.getValue());
		}
		
		return l;
	}
	public static List<String> getAllIncludedConfidences(){
		List<String> l = new ArrayList<>();
		l.add("False Positive");
		l.add("Low");
		l.add("Medium");
		l.add("High");
		l.add("Confirmed");
		return l;
	}
	public static List<String> getAllIncludedRisks(){
		List<String> l = new ArrayList<>();
		l.add("Informational");
		l.add("Low");
		l.add("Medium");
		l.add("High");
		return l;
	}
	public static String getExt(String template){
	
		 // Template 'template'='traditional-json'
		 // Template 'template'='traditional-pdf'
		 // Template 'template'='traditional-md'
		 // Template 'template'='traditional-xml'
		 // Template 'template'='sarif-json'
		 // Template 'template'='traditional-html'
		 // Template 'template'='modern'
		 // Template 'template'='high-level-report'
		 // Template 'template'='traditional-xml-plus'
		 // Template 'template'='risk-confidence-html'
		 // Template 'template'='traditional-json-plus'
		 // Template 'template'='traditional-html-plus'
		
		if(template.contains("traditional-json")) {
			return ".json";
		}
		else if(template.contains("traditional-pdf")) {
			return ".pdf";
		}
		else if(template.contains("traditional-md")) {
			return ".md";
		}
		else if(template.contains("traditional-xml")) {
			return ".xml";
		}
		else if(template.contains("traditional-html")) {
			return ".html";
		}
		else if(template.contains("sarif-json")) {
			return "_sarif.json";
		}
		else if(template.contains("modern")) {
			return "_modern.html";
		}
		else if(template.contains("high-level-report")) {
			return "_high-level-report.html";
		}
		else if(template.contains("risk-confidence-html")) {
			return "_risk-confidence.html";
		}
		else if(template.contains("traditional-xml-plus")) {
			return "_plus.xml";
		}
		else if(template.contains("traditional-json-plus")) {
			return "_plus.json";
		}
		else if(template.contains("traditional-html-plus")) {
			return "_plus.html";
		}
		else if(template.contains("-json")) {
			return ".json";
		}
		else if(template.contains("-pdf")) {
			return ".pdf";
		}
		else if(template.contains("-md")) {
			return ".md";
		}
		else if(template.contains("-xml")) {
			return ".xml";
		}
		else if(template.contains("-html")) {
			return ".html";
		}
		return "bin";
		
	}
}

class ZAPReportTemplate {
	
	String template;
	String theme;
	String sections;
	String fileName;
	
}
