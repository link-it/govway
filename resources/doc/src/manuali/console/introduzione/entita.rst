.. _entitaConfigurazione:

Le entità di configurazione dei servizi
---------------------------------------

Prima di descrivere le entità di configurazione presenti nel registro è
importante chiarire il concetto di *Dominio* cui alcuni elementi di
configurazione fanno riferimento. Il dominio rappresenta il confine
logico (tipicamente un ente amministrativo) entro il quale sono
racchiuse le risorse applicative da condividere con l'esterno. Nel
seguito si fa distinzione tra i seguenti:

-  *Dominio Gestito*: l'insieme delle risorse applicative i cui flussi
   di comunicazione sono sotto il controllo del GovWay di propria
   gestione.

-  *Dominio Esterno*: Insieme di risorse applicative esterne al dominio
   gestito.

Le principali entità di configurazione del Registro sono:

-  *API*

   Descrizione formale dei flussi di comunicazione previsti da un dato
   servizio, erogato o fruito nel proprio dominio. Ad ogni API è
   assegnata una singola modalità operativa e, in base ad essa, sarà
   fornita una descrizione formale delle interfacce di dialogo
   supportate. Ad esempio saranno forniti WSDL/XSD per le interfacce
   Soap o un file YAML in formato Swagger per quelle Rest.

-  *Erogazione*

   Registrazione di una specifica istanza di servizio che un soggetto
   del dominio interno eroga in accordo alle interfacce applicative
   descritte da un set di API censito nel registro.

-  *Fruizione*

   Registrazione di una specifica istanza di servizio che un soggetto
   del dominio interno fruisce in accordo alle interfacce applicative
   descritte da un set di API censito nel registro.

-  *Soggetto*

   Entità che rappresenta la singola organizzazione, o ente
   amministrativo, coinvolto nei flussi di comunicazione. Ciascun
   soggetto censito nel registro può appartenere al dominio interno o
   esterno e può avere associata un'unica modalità operativa.

-  *Applicativo*

   Entità per censire i client, riferiti ad uno specifico soggetto (e
   quindi modalità), che fruiscono di servizi. Censire un applicativo è
   indispensabile nei casi in cui l'identificazione è necessaria per
   poter superare i criteri di autenticazione autorizzazione specificati
   nella configurazione del *Controllo degli Accessi* per ciascun
   servizio fruito.

-  *Ruolo*

   Entità per censire i ruoli che possono essere utilizzati nell'ambito
   del controllo degli accessi per costruire specifici criteri di
   autorizzazione. I ruoli possono avere origine interna al registro
   oppure essere passati da un sistema esterno, sia in contesti
   fruizione che di erogazione.

-  *Scope*

   Entità per censire gli scope che possono essere utilizzati nell'ambito
   del controllo degli accessi per costruire specifici criteri di
   autorizzazione basato sui token.
