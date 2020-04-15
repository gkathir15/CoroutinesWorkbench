# Coroutine codelab Basics

## Intent of the talk
#### Since [AsyncTask](https://developer.android.com/reference/android/os/AsyncTask) is depricated Kotlin corutines is another way of going up for background execution on Android.

This App has covered the the Basic Scopes available in coroutine and launching task to differnet threads.
 [Slides](https://docs.google.com/presentation/d/e/2PACX-1vSnT8m64E6-i7MZscw8j5g35R_aiAYYf4D4sWx7HoIAYBECA98NnVVsUfmU4tC6pC95a7pQlkzwJ5PN/pub?start=false&loop=false&delayms=3000)
```
dependencies {
  ...
  implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:x.x.x"
  implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:x.x.x"
}
```
The app has samples to 
* Download image and show in ImageView
* Connecting to a socket and updating UI
* Fetching a http Api and parsing using Gson and displaying on UI & also downloading and catching bitmaps in memory.




With regards to socket, the code samples of socket server and a sample client to test is pased below.

### Installation

 requires [Node.js](https://nodejs.org/)

*server.js
```
const net = require('net');
const port = 80;
const host = '192.168.0.108';

const server = net.createServer();
server.listen(port, host, () => {
    console.log('TCP Server is running on port ' + port + '.');
});

let sockets = [];

server.on('connection', function(sock) {
    console.log('CONNECTED: ' + sock.remoteAddress + ':' + sock.remotePort);
    sockets.push(sock);
        let check = true;

    sock.on('data', function(data) {
        console.log('DATA ' + sock.remoteAddress + ': ' + data);
        // Write the data back to all the connected, the client will receive it as data from the server
        setInterval(function(){    
           
            sockets.forEach(function(sock, index, array) {
                
                sock.write(new Date().toLocaleTimeString());
            });},3000)
        
    });

    // Add a 'close' event handler to this instance of socket
    sock.on('close', function(data) {
        let index = sockets.findIndex(function(o) {
            return o.remoteAddress === sock.remoteAddress && o.remotePort === sock.remotePort;
        })
        if (index !== -1) sockets.splice(index, 1);
        console.log('CLOSED: ' + sock.remoteAddress + ' ' + sock.remotePort);
    });
});
```
>to run Server
``
node server.js
``

*client.js
```
var net = require('net');

var client = new net.Socket();
client.connect(80, '192.168.0.108', function() {
	console.log('Connected');
	client.write('Hello, server! Love, Client.');
});

client.on('data', function(data) {
	console.log('Received: ' + data);
	//client.destroy(); // kill client after server's response
});

client.on('close', function() {
	console.log('Connection closed');
});
```
>to run Client
``
node client.js
``

*To Do
> open your ports both inbound and outbounf in your windows firewall to connect to Mobile device ie port 80

``
ipconfig
``
> Use the ipv4 ip and port to connect to socket.





