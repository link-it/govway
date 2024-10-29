package org.openspcoop2.utils.transport.ldap.test;

import java.io.File;
import java.io.IOException;

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
import org.springframework.core.io.Resource;

public class LdapServerTest {
	private LdapServer server;
	private String host = "127.0.0.1";
	private int port = 9321;
	private Resource ldif;
	
	public LdapServerTest(Resource ldif) {
		this.ldif = ldif;
	}
	
	public void start(String path) throws Exception {
		DefaultDirectoryService directoryService = new DefaultDirectoryService();
		
        File workingDirector = new File(path);
        InstanceLayout instanceLayout = new InstanceLayout(workingDirector);
        directoryService.setInstanceLayout(instanceLayout);
 
        File schemaRepository = new File(workingDirector, "schema");
        
        DefaultSchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor(workingDirector);
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
        ldifPartition.setPartitionPath(new File(workingDirector, "schema").toURI());
        SchemaPartition schemaPartition = new SchemaPartition(schemaManager);
        schemaPartition.setWrappedPartition(ldifPartition);
        directoryService.setSchemaPartition(schemaPartition);
        //List<Throwable> errors = schemaManager.getErrors();
        
        // Init the user  partation
        LdifPartition userPartition = new LdifPartition(schemaManager, directoryService.getDnFactory());
        userPartition.setPartitionPath(new File(workingDirector, "user").toURI());
        userPartition.setSuffixDn(new Dn("dc=example,dc=com"));
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
 
        directoryService.startup();
        
        LdifFileLoader ldifLoader = new LdifFileLoader(directoryService.getAdminSession(), this.ldif.getFile().getPath());
        ldifLoader.execute();
        
        this.server.start();
	}
	
	public void shutdown() {
		this.server.stop();
	}
	
	public String getURL() {
		return "ldap://" + this.host + ":" + this.port;
	}
}
