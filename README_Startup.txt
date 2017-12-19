Alle klassen nodig om de componenten op te starten bevinden zich in het main package.

Om alles op te starten gaat men als volgt te werk:

1:start de databasedispatcher (Main_S_D_Com_Controller.java) op. 

2:start de databases (MainDatabase.java) op. Zorg dat er voor elke database die men opstart een kopie van de correcte database aanwezig is. 
Deze kopieën moeten de naam "database"+poortnummer+".db" hebben.(De eerst opgestarte database is "database1500.db", de tweede "database1501.db", de derde "database1502.db"...)
Deze kopieën moeten handmatig gemaakt worden, ze worden niet automatisch gegenereerd.

3: Als men alle databases heeft opgestart druk op enter in de commandline van elke database.(dit zodat ze hun data propageren naar alle databases)

4:start de dispatcher(Main_C_S_Com_Controller.java) op.

5:start het gewenste aantal applicatieservers(MainServer.java) op.

6:nu kan men een gewenst aantal clients (MainClient.java) opstarten en beginnen spelen.