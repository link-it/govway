/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.transport.ldap.test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.LdapComparator;
import org.apache.directory.api.ldap.model.schema.comparators.NormalizingComparator;
import org.apache.directory.api.ldap.model.schema.registries.ComparatorRegistry;
import org.apache.directory.api.ldap.schema.extractor.impl.DefaultSchemaLdifExtractor;
import org.apache.directory.api.ldap.schema.loader.LdifSchemaLoader;
import org.apache.directory.api.ldap.schema.manager.impl.DefaultSchemaManager;
import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.core.api.schema.SchemaPartition;
import org.apache.directory.server.core.factory.JdbmPartitionFactory;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.springframework.core.io.Resource;

/**
 * Classe LdapServerTest
 * 
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$  
 * @version $Rev$, $Date$
 *
 */
public class LdapServerTest {
	private LdapServer server;
	private String host = "127.0.0.1";
	private int port = 9321;
	private Resource ldif;
	private File workingDirector;
	private String rootPartition = "dc=example,dc=com";
	
	public LdapServerTest(Resource ldif) {
		this.ldif = ldif;
	}
	public LdapServerTest(Resource ldif, org.apache.logging.log4j.Level level) throws IOException, UtilsException {
		this.ldif = ldif;

		String propertiesLog = "logger.apacheds.name = org.apache.directory.server\n"+ "logger.apacheds.level="+level.name()+"\n"+"logger.apacheds.additivity = false";
		Properties p = new Properties();
		p.load(new StringReader(propertiesLog));
		LoggerWrapperFactory.setLogConfiguration(p);
	}

	public void setHost(String host) {
		this.host = host;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setRootPartition(String rootPartition) {
		this.rootPartition = rootPartition;
	}
	
	public void start(String path) throws Exception {
		start(path, false);
	}
	public void start(String path, boolean allowAnonymousAccess) throws Exception {
		DefaultDirectoryService directoryService = new DefaultDirectoryService();
		
        this.workingDirector = new File(path);
        FileSystemUtilities.mkdir(this.workingDirector);
        InstanceLayout instanceLayout = new InstanceLayout(this.workingDirector);
        directoryService.setInstanceLayout(instanceLayout);
 
        File schemaRepository = new File(this.workingDirector, "schema");
        
        DefaultSchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor(this.workingDirector);
        try {
            extractor.extractOrCopy();
        } catch (IOException ioe) {
        	// se lo schema esiste gia non devo crearlo
        }
        
        LdifSchemaLoader loader = new LdifSchemaLoader(schemaRepository);
        DefaultSchemaManager schemaManager = new DefaultSchemaManager(loader);
        schemaManager.loadAllEnabled();
        ComparatorRegistry comparatorRegistry = schemaManager.getComparatorRegistry();
 
        for (LdapComparator<?> comparator : comparatorRegistry) {
            if (comparator instanceof NormalizingComparator) {
                ((NormalizingComparator) comparator).setOnServer();
            }
        }
        directoryService.setSchemaManager(schemaManager);
 
        // Init the schema partation
        LdifPartition ldifPartition = new LdifPartition(schemaManager, directoryService.getDnFactory());
        ldifPartition.setPartitionPath(new File(this.workingDirector, "schema").toURI());
        SchemaPartition schemaPartition = new SchemaPartition(schemaManager);
        schemaPartition.setWrappedPartition(ldifPartition);
        directoryService.setSchemaPartition(schemaPartition);
        /**List<Throwable> errors = schemaManager.getErrors();*/
        
        // Init the user  partation
        LdifPartition userPartition = new LdifPartition(schemaManager, directoryService.getDnFactory());
        userPartition.setPartitionPath(new File(this.workingDirector, "user").toURI());
        userPartition.setSuffixDn(new Dn(this.rootPartition));
        directoryService.addPartition(userPartition); 
        
        // Inject the System Partition
        JdbmPartitionFactory partitionFactory = new JdbmPartitionFactory();
        Partition systemPartition = partitionFactory.createPartition(directoryService.getSchemaManager(), directoryService.getDnFactory(), "system", ServerDNConstants.SYSTEM_DN, 500, new File(directoryService.getInstanceLayout().getPartitionsDirectory(), "system"));
        systemPartition.setSchemaManager(directoryService.getSchemaManager());
        partitionFactory.addIndex(systemPartition, SchemaConstants.OBJECT_CLASS_AT, 100);
        directoryService.setSystemPartition(systemPartition);
 
        this.server = new LdapServer();
        this.server.setDirectoryService(directoryService);
        this.server.setServiceName("DefaultLDAP");
        
        TcpTransport tcp = new TcpTransport(this.host, this.port, 1, 5);
        this.server.addTransports(tcp);
 
        if(allowAnonymousAccess) {
        	directoryService.setAllowAnonymousAccess(true);
        }
        
        directoryService.startup();
        
        LdifFileLoader ldifLoader = new LdifFileLoader(directoryService.getAdminSession(), this.ldif.getFile().getPath());
        ldifLoader.execute();
        
        this.server.start();
	}
	
	public void shutdown(boolean dropWorkingDirectory) {
		if(this.server.isStarted()) {
			this.server.stop();
		}
		
		int index = 0;
		while (this.server.isStarted() && index < 1000) {
			Utilities.sleep(100);
			index++;
		}
		
		if(dropWorkingDirectory) {
			FileSystemUtilities.deleteDirNotEmpty(this.workingDirector, 10);
			FileSystemUtilities.deleteDir(this.workingDirector);
		}
	}
	
	public String getURL() {
		return "ldap://" + this.host + ":" + this.port;
	}
}
