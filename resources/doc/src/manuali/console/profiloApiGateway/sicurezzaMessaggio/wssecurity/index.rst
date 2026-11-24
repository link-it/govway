.. _sicurezzaLivelloMessaggioWSSecurity:

WS-Security (API SOAP)
^^^^^^^^^^^^^^^^^^^^^^

Gli schemi WS-Security sono disponibili per le API SOAP e sono basati sulla libreria Apache WSS4J. Per ciascuno schema vengono descritti i campi della maschera di configurazione, distinguendo tra il ruolo *Receiver* (verifica del messaggio ricevuto) e *Sender* (protezione del messaggio inviato); si ricorda che in un'erogazione il gateway è Receiver sulla richiesta e Sender sulla risposta, mentre in una fruizione i ruoli si invertono.

.. _sicurezzaLivelloMessaggioWSSecurityComuni:

Campi comuni
""""""""""""

Tutte le maschere WS-Security presentano nella sezione *Informazioni Generali* i seguenti campi:

- *Actor*: valore dell'attributo 'actor' (SOAP 1.1) o 'role' (SOAP 1.2) dell'header WS-Security; in ricezione viene elaborato solamente l'header destinato all'actor indicato; se non valorizzato viene utilizzato l'header privo di actor;
- *MustUnderstand*: negli schemi di spedizione l'header WS-Security viene generato con l'attributo 'mustUnderstand' abilitato; negli schemi di ricezione indica il valore con cui è atteso l'attributo nell'header ricevuto e ha solo scopo descrittivo, poiché l'header da elaborare viene individuato tramite l'*Actor*.

Nelle maschere di ricezione sono inoltre presenti:

- *Compliance BSP 1.1* (default: abilitato): il messaggio ricevuto deve rispettare i vincoli del profilo WS-I Basic Security Profile 1.1;
- *Ripulisci Messaggio dalle Informazioni Sicurezza* (default: abilitato): dopo la verifica l'header WS-Security e i riferimenti utilizzati dalla sicurezza vengono eliminati dal messaggio prima del suo inoltro.

Gli schemi di firma, cifratura e SAML consentono di aggiungere l'azione *Timestamp* tramite l'omonimo campo; in tal caso compare la sezione *Timestamp*, con i campi descritti nella sezione :ref:`sicurezzaLivelloMessaggioWSSecurityTimestamp`.

I campi relativi a keystore e truststore sono descritti nella sezione :ref:`sicurezzaLivelloMessaggioKeystore`.

.. _sicurezzaLivelloMessaggioWSSecurityParti:

Indicazione degli elementi del messaggio
""""""""""""""""""""""""""""""""""""""""

Gli elementi da firmare o cifrare in spedizione, e quelli attesi in ricezione, si selezionano tra *Body*, *Timestamp* (solo per la firma con l'azione Timestamp abilitata) e *Attachments*, o loro combinazioni. Il valore *Attachments* indica tutti gli allegati di un messaggio SOAP With Attachments.

Selezionando *Configurazione Manuale* gli elementi vengono indicati come lista separata da ';' nel formato '{modalità}{namespace}nomeLocale', dove:

- la modalità è 'Element' (l'intero elemento) o 'Content' (solo il contenuto dell'elemento, utilizzabile per la cifratura);
- il namespace può essere indicato con la parola chiave 'SOAP_TEMPLATE_NS', sostituita con il namespace della versione SOAP del messaggio;
- gli allegati si indicano come '{}cid:Attachments' (tutti gli allegati) o '{}cid:<Content-ID>' (un allegato specifico).

Ad esempio: '{Element}{SOAP_TEMPLATE_NS}Body;{}cid:Attachments'.

.. _sicurezzaLivelloMessaggioWSSecurityPartiAttese:

Elementi firmati e cifrati attesi
"""""""""""""""""""""""""""""""""

Negli schemi di ricezione *WS-Security Signature* e *WS-Security Decrypt* i campi *Elementi firmati attesi* ed *Elementi cifrati attesi* consentono di indicare quali parti del messaggio devono risultare protette:

- *Qualsiasi* (default): la firma o la cifratura degli elementi presenti viene verificata, senza richiedere che ad essere protetti siano elementi particolari;
- *Body*, *Timestamp*, *Attachments* e loro combinazioni: gli elementi firmati o cifrati devono corrispondere esattamente a quelli indicati;
- *Configurazione Manuale*: gli elementi attesi vengono indicati nel campo *Elementi Attesi* con il formato descritto nella sezione :ref:`sicurezzaLivelloMessaggioWSSecurityParti`.

Se manca un elemento atteso, oppure risulta protetto un elemento non indicato, il messaggio viene rifiutato con un errore di sicurezza messaggio e il diagnostico riporta le differenze rilevate (es. *Expected signature part(...) not found*, *Found encryption part(...) not expected*).

Indipendentemente da tali campi, quando la firma o la cifratura riguardano il *Body*, GovWay verifica che l'elemento protetto sia effettivamente il *Body* del messaggio e non una sua copia collocata altrove, come avviene negli attacchi di tipo *XML Signature Wrapping* (XSW) (vedi :ref:`configAvanzataStrutturaEnvelopeSoap`).

Schemi di sicurezza
"""""""""""""""""""

.. toctree::
        :maxdepth: 2

        timestamp
        signature
        encrypt
        saml
        usernameToken

È inoltre disponibile lo schema *Configurazione Manuale*, descritto nella sezione :ref:`sicurezzaLivelloMessaggioManuale`.
