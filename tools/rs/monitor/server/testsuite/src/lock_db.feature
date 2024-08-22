@ignore
Feature:
	
Scenario:

# Rimuovo la lock e do il tempo all'engine di preparare le statistiche.
* print 'Rimuovo La lock sul db..'

#mflag: eliminata la callonce dalle chiamate di funzione, altrimenti di ottiene errore javascript
#
* setup.delete_lock(setup.db)
# * callonce setup.delete_lock(setup.db)

# TODO: Ora che dedichiamo un applicative_id a ciascun intervallo  orario in op2_semaphore, potremmo invece che attendere indefinitamente,
# rilevare che questa lock sia stata presa e rilasciata almeno una volta.

* print 'Attendo che vengano generate le statistiche'
* pause(statsInterval)
# * callonce pause(statsInterval)
* print 'Attendo di ottenere la lock'
* setup.wait_for_lock(setup.db);  
# * callonce setup.wait_for_lock(setup.db);  
