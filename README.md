## DESCRIPTION

Grizzly server that's connected to a MySQL database (included in dump.sql).
Needs username/pass (which is hashed in database) to be used (default is: username:anap / pass:hardpass) and has a GUI (Swing).
It caches results so as to avoid excessive database requests.


## WHAT IT CAN DO

- It allows a NmapClient to connect to it (by showing a prompt), send it Nmap jobs and retrieve the results.
- It has multiple GUI options (sending a custom request to client, stopping client, showing results, showing client information etc).
- It also accepts Android clients with correct credentials and by showing a prompt the server client has to accept. 
- An android client is allowed to do multiple things (eg. view results the clients have sent the server, send new Nmap jobs to clients through the server, view jobs the server has sent to clients etc).
