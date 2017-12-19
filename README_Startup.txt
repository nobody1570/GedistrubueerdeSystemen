Alle klassen nodig om de componenten op te starten bevinden zich in het main package.

Om alles op te starten gaat men als volgt te werk:


1: Start de databasedispatcher (Main_S_D_Com_Controller.java) op. 


2: Start de databases (MainDataBase.java) op. Zorg dat er voor elke database die men opstart een kopie van de correcte database aanwezig is. 
Deze kopieën moeten de naam "database"+poortnummer+".db" hebben.(De eerst opgestarte database is "database1500.db", de tweede "database1501.db", de derde "database1502.db"...)
Deze kopieën moeten handmatig gemaakt worden, ze worden niet automatisch gegenereerd.


3: Als men alle databases heeft opgestart druk op enter in de commandline van elke database.(dit zodat ze hun data propageren naar alle databases)


4: Start de serverdispatcher (Main_C_S_Com_Controller.java) op


5: Start het gewenste aantal applicatieservers(MainServer.java) op.


6: Nu kan men een gewenst aantal clients (MainClient.java) opstarten en beginnen spelen.

