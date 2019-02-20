.. _ruolo:

Creazione di un ruolo
^^^^^^^^^^^^^^^^^^^^^

È possibile censire i ruoli che potranno essere utilizzati come criterio
di autorizzazione. Quelli contrassegnati come *fonte registro* potranno
essere associandoli ai soggetti. Quelli invece contrassegnati come
*fonte esterna* verranno assegnati dinamicamente ai soggetti che si
autenticano, sulla base di quanto comunicato dal container dopo che
l'utente ha effettuato l'autenticazione esternamente.

Per creare un nuovo ruolo ci si posiziona nella sezione *Registro >
Ruoli* e si preme il pulsante *Aggiungi*.

   .. figure:: ../../_figure_console/Ruoli-new.png
    :scale: 100%
    :align: center
    :name: ruoloNew

    Registrazione di un ruolo

Compilare il form (:numref:`ruoloNew`) nel seguente modo:

-  *Nome*: identifica univocamente il ruolo.

-  *Descrizione*: rappresenta una descrizione generica del ruolo.

-  *Fonte*: la gestione del ruolo può essere effettuata direttamente su
   GovWay (fonte: registro) dove può essere assegnato ad un soggetto o
   applicativo. In alternativa (fonte: esterna) la gestione può essere
   delegata all'Application Server o a qualunque altra modalità che
   permetta al gateway di accedere ai ruoli tramite la api
   *HttpServletRequest.isUserInRole()*. In questo caso il nome del ruolo
   deve corrispondere allo stesso identificativo utilizzato nella
   configurazione esterna.

   Se non viene specificata alcuna fonte il ruolo potrà essere
   utilizzato per entrambe le modalità.

-  *Contesto*: l'utilizzo del ruolo può essere limitato ad un contesto
   di erogazione o fruizione di servizio attraverso questa opzione.

-  *Identificativo Esterno*: Nei casi in cui il ruolo provenga da un
   sistema esterno, è possibile che il suo identificativo sia differente
   rispetto a quello indicato nel contesto del Registro. In tal caso
   inserire in questo campo tale identificativo esterno.
