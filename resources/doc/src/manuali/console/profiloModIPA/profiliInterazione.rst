.. _modipa_profiliInterazione:

Profili di Interazione
----------------------

Le specifiche del Modello di Interoperabilità definiscono i Profili di Interazione come le modalità secondo le quali un erogatore e un fruitore possono interagire. La distinzione operata a livello della specifica è quella tra il profilo "Bloccante" e quello "Non Bloccante". Per le differenze di dettaglio tra i due profili si rimanda al testo della specifica.

Il profilo di interazione viene definito nell'interfaccia del servizio e conseguentemente GovWay recepisce tale informazioni nell'ambito della configurazione di una API nel contesto del profilo ModI PA.

La configurazione di API con il profilo ModI PA produce per default servizi con profilo di interazione "Bloccante". Se si desidera, è possibile modificare questa impostazione intervenendo puntualmente sulle singole operation/risorse della API.

La maschera di editing della singola operation/risorsa possiede la sezione ModI PA per consentire di specificare le seguenti informazioni (:numref:`api_bloccante_fig`):

- *Profilo di Interazione*: specifica il profilo di interazione che si vuole associare alla specifica operation/risorsa

    + *Profilo*: indica il nome del profilo di interazione, a scelta tra Bloccante e Non Bloccante
    + *Interazione*: (solo per il profilo non bloccante) indica se l'interazione prevista è di tipo PUSH (iniziativa del mittente) o PULL (iniziativa del destinatario)
    + *Funzione*: (solo per il profilo non bloccante) indica se l'operation/risorsa ha la funzione di inviare una richiesta, chiedere lo stato di avanzamento dell'elaborazione della risposta o inviare una risposta.
    + *Richiesta Correlata*: (solo per la funzione Richiesta Stato e Risposta) indica l'operation/risorsa correlata che corrisponde all'invio della richiesta.

   .. figure:: ../_figure_console/modipa_api_bloccante.png
    :scale: 50%
    :align: center
    :name: api_bloccante_fig

    Profili di interazione ModI PA per operation/risorse dell'API

