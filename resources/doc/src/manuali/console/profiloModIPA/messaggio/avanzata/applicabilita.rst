.. _modipa_sicurezza_avanzate_applicabilita:

Attivazione della sicurezza messaggio su richiesta/risposta
---------------------------------------------------------------

Insieme all'attivazione di un profilo di sicurezza messaggio è possibile configurarne l'attivazione solamente sulla richiesta o sulla risposta (:numref:`modipa_sicurezza_applicabilita`).

   .. figure:: ../../../_figure_console/modipa_sicurezza_applicabilita.png
    :scale: 50%
    :align: center
    :name: modipa_sicurezza_applicabilita

    Configurazione dell'applicabilità della sicurezza messaggio

Per API REST è possibile anche definire dei criteri di applicabilità della sicurezza messaggio in base a Content-Type o codici di risposta HTTP selezionando la voce 'Personalizza criteri di applicabilità'. La personalizzazione dei criteri consente di differenziare la configurazione tra richiesta e risposta come mostrato nella figura :numref:`modipa_sicurezza_applicabilita_personalizzata`:

- Richiesta: oltre ad abilitare o disabilitare, è consentito definire una lista di Content-Type solamente per i quali verrà attuata la sicurezza messaggio sulla richiesta.

- Risposta:  oltre ad abilitare o disabilitare, è consentito definire una lista di Content-Type e/o una lista di codice di risposta HTTP per i quali verrà attuata la sicurezza messaggio sulla risposta.

   .. figure:: ../../../_figure_console/modipa_sicurezza_applicabilita_personalizzata.png
    :scale: 50%
    :align: center
    :name: modipa_sicurezza_applicabilita_personalizzata

    Configurazione dell'applicabilità della sicurezza messaggio personalizzata per Content-Type e Codici di Risposta

La lista di Content-Type per i quali la sicurezza messaggio verrà utilizzata è definibile tramite i seguenti formati:

- type/subtype: indicazione puntuale di un Content-Type

- type/\*: hanno un match tutti i Content-Type appartenenti al tipo indicato

- \*/\*+xml: hanno un match tutti i Content-Type che terminano con '+xml'

- regexpType/regexpSubType: hanno un match tutti i Content-Type che soddisfano le espressioni regolari indicate

- empty: valore speciale che rappresenta una richiesta senza Content-Type

La lista dei codici di risposta HTTP per i quali la sicurezza messaggio verrà utilizzata può contenere un codice http puntuale (es. 200) o un intervallo fornendo due codici separati dal trattino (es. 200-299).


