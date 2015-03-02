package org.openspcoop2.testsuite.units;

import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;

public interface UnitsDatabaseProperties {

	public DatabaseComponent newInstanceDatabaseComponentErogatore();
	
	public DatabaseComponent newInstanceDatabaseComponentFruitore();
	
	
	public DatabaseMsgDiagnosticiComponent newInstanceDatabaseComponentDiagnosticaErogatore();
	
	public DatabaseMsgDiagnosticiComponent newInstanceDatabaseComponentDiagnosticaFruitore();
	
}
