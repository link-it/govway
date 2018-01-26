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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for businessProcesses complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="businessProcesses">
 * 		&lt;sequence>
 * 			&lt;element name="roles" type="{http://www.domibus.eu/configuration}roles" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="parties" type="{http://www.domibus.eu/configuration}parties" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="meps" type="{http://www.domibus.eu/configuration}meps" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="properties" type="{http://www.domibus.eu/configuration}properties" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="payloadProfiles" type="{http://www.domibus.eu/configuration}payloadProfiles" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="securities" type="{http://www.domibus.eu/configuration}securities" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="errorHandlings" type="{http://www.domibus.eu/configuration}errorHandlings" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="agreements" type="{http://www.domibus.eu/configuration}agreements" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="services" type="{http://www.domibus.eu/configuration}services" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="actions" type="{http://www.domibus.eu/configuration}actions" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="as4" type="{http://www.domibus.eu/configuration}as4" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="legConfigurations" type="{http://www.domibus.eu/configuration}legConfigurations" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="process" type="{http://www.domibus.eu/configuration}process" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "businessProcesses", 
  propOrder = {
  	"roles",
  	"parties",
  	"meps",
  	"properties",
  	"payloadProfiles",
  	"securities",
  	"errorHandlings",
  	"agreements",
  	"services",
  	"actions",
  	"as4",
  	"legConfigurations",
  	"process"
  }
)

@XmlRootElement(name = "businessProcesses")

public class BusinessProcesses extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public BusinessProcesses() {
  }

  public Roles getRoles() {
    return this.roles;
  }

  public void setRoles(Roles roles) {
    this.roles = roles;
  }

  public Parties getParties() {
    return this.parties;
  }

  public void setParties(Parties parties) {
    this.parties = parties;
  }

  public Meps getMeps() {
    return this.meps;
  }

  public void setMeps(Meps meps) {
    this.meps = meps;
  }

  public Properties getProperties() {
    return this.properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public PayloadProfiles getPayloadProfiles() {
    return this.payloadProfiles;
  }

  public void setPayloadProfiles(PayloadProfiles payloadProfiles) {
    this.payloadProfiles = payloadProfiles;
  }

  public Securities getSecurities() {
    return this.securities;
  }

  public void setSecurities(Securities securities) {
    this.securities = securities;
  }

  public ErrorHandlings getErrorHandlings() {
    return this.errorHandlings;
  }

  public void setErrorHandlings(ErrorHandlings errorHandlings) {
    this.errorHandlings = errorHandlings;
  }

  public Agreements getAgreements() {
    return this.agreements;
  }

  public void setAgreements(Agreements agreements) {
    this.agreements = agreements;
  }

  public Services getServices() {
    return this.services;
  }

  public void setServices(Services services) {
    this.services = services;
  }

  public Actions getActions() {
    return this.actions;
  }

  public void setActions(Actions actions) {
    this.actions = actions;
  }

  public As4 getAs4() {
    return this.as4;
  }

  public void setAs4(As4 as4) {
    this.as4 = as4;
  }

  public LegConfigurations getLegConfigurations() {
    return this.legConfigurations;
  }

  public void setLegConfigurations(LegConfigurations legConfigurations) {
    this.legConfigurations = legConfigurations;
  }

  public void addProcess(Process process) {
    this.process.add(process);
  }

  public Process getProcess(int index) {
    return this.process.get( index );
  }

  public Process removeProcess(int index) {
    return this.process.remove( index );
  }

  public List<Process> getProcessList() {
    return this.process;
  }

  public void setProcessList(List<Process> process) {
    this.process=process;
  }

  public int sizeProcessList() {
    return this.process.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="roles",required=true,nillable=false)
  protected Roles roles;

  @XmlElement(name="parties",required=true,nillable=false)
  protected Parties parties;

  @XmlElement(name="meps",required=true,nillable=false)
  protected Meps meps;

  @XmlElement(name="properties",required=true,nillable=false)
  protected Properties properties;

  @XmlElement(name="payloadProfiles",required=true,nillable=false)
  protected PayloadProfiles payloadProfiles;

  @XmlElement(name="securities",required=true,nillable=false)
  protected Securities securities;

  @XmlElement(name="errorHandlings",required=true,nillable=false)
  protected ErrorHandlings errorHandlings;

  @XmlElement(name="agreements",required=true,nillable=false)
  protected Agreements agreements;

  @XmlElement(name="services",required=true,nillable=false)
  protected Services services;

  @XmlElement(name="actions",required=true,nillable=false)
  protected Actions actions;

  @XmlElement(name="as4",required=true,nillable=false)
  protected As4 as4;

  @XmlElement(name="legConfigurations",required=true,nillable=false)
  protected LegConfigurations legConfigurations;

  @XmlElement(name="process",required=true,nillable=false)
  protected List<Process> process = new ArrayList<Process>();

  /**
   * @deprecated Use method getProcessList
   * @return List<Process>
  */
  @Deprecated
  public List<Process> getProcess() {
  	return this.process;
  }

  /**
   * @deprecated Use method setProcessList
   * @param process List<Process>
  */
  @Deprecated
  public void setProcess(List<Process> process) {
  	this.process=process;
  }

  /**
   * @deprecated Use method sizeProcessList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProcess() {
  	return this.process.size();
  }

}
