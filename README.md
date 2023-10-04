# Single server key-value store (TCP & UDP)
> A server program that serves as a key-value store.
### It's set up to allow a single client to communicate with it and perform three basic operations:
- PUT(key, value)
- GET(key)
- DELETE(key)
- - -
#### Usage (locally):
1) Open up two terminal windows and navigate to `/Project1/src`
2) In one window, type `javac servers/*.java` (hit <kbd>↩</kbd>), followed by `java servers.Main <Port#> <TCP/UDP>`, where `<Port#>` is the port number the server advertises its service with, and `<TCP/UDP>` instantiates the type of server that communicates with the TCP or UDP protocol (hit <kbd>↩</kbd>)
3) The server is now running
4) In the other window, type `javac clients/*.java` (hit <kbd>↩</kbd>), followed by `java clients.Main <Hostname> <Port#> <TCP/UDP>`, where `<Hostname>` can be either the server's hostname or IP address, `Port#` is the port number the server advertises its service with, and `<TCPC/UDP>` instantiates the type of client that communicates with the TCP or UDP protocol (***it has to match the server's***) (hit <kbd>↩</kbd>)
5) The client is now running
6) The predefined protocol is:
   * `PUT:key:value`(hit <kbd>↩</kbd>)
   * `GET:key`(hit <kbd>↩</kbd>)
   * `DELETE:key`(hit <kbd>↩</kbd>)
7) To shut down the server, type `server stop`(hit <kbd>↩</kbd>) or `server shutdown`(hit <kbd>↩</kbd>)
8) To shut down the client, type `client stop`(hit <kbd>↩</kbd>) or `client shutdown`(hit <kbd>↩</kbd>)
