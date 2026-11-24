.. _sicurezzaLivelloMessaggioWSSecurityUsernameToken:

WS-Security Username Token
~~~~~~~~~~~~~~~~~~~~~~~~~~

Lo schema gestisce uno UsernameToken nell'header WS-Security, contenente le credenziali del mittente. Per i campi presenti nella sezione *Informazioni Generali* si rimanda alla sezione :ref:`sicurezzaLivelloMessaggioWSSecurityComuni`.

**Ricezione (Receiver)**

Viene verificato lo UsernameToken presente nell'header WS-Security (:numref:`wssReceiverUsernameTokenFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSReceiverUsernameToken.png
    :scale: 70%
    :align: center
    :name: wssReceiverUsernameTokenFig

    Ricezione: WS-Security Username Token

- *Token senza Password*: accetta uno UsernameToken privo di password; in tal caso non viene verificata alcuna credenziale;
- *Modalità* (default: *Default*): con *Default* le credenziali attese sono indicate nei campi *User* e *Password*; con *Mappa* vengono elencate più credenziali valide, una per riga nel formato 'username=password'.

**Spedizione (Sender)**

Viene aggiunto all'header WS-Security uno UsernameToken (:numref:`wssSenderUsernameTokenFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSSenderUsernameToken.png
    :scale: 70%
    :align: center
    :name: wssSenderUsernameTokenFig

    Spedizione: WS-Security Username Token

- *User* e *Password*: credenziali inserite nel token;
- *Tipo Password*: *Password Digest* (viene inserito il digest della password), *Password Text* (password in chiaro) o *Password None* (token senza password);
- *Nonce Element* e *Created Element*: aggiungono al token rispettivamente un valore casuale e la data di creazione.
