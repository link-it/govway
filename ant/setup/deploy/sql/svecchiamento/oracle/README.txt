# Esecuzione dello script per oracle
sqlplus govwayuser/govwaypassword@DB_GOVWAY < svecchiamento.sql" >> "svecchiamento-$(date  +'%d').stdout.log" 2> "svecchiamento-$(date  +'%d').stderr.log" 

