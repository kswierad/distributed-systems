#include <stdio.h>
#include <stdlib.h> // exit()
#include <string.h> // strlen()
#include <stdbool.h>

#include <sys/socket.h> // socket()
#include <netinet/in.h> // struct sockaddr_in
#include <arpa/inet.h> // inet_pton()
#include <netdb.h> // gethostbyname()


#define err(s) {\
        perror((s));\
        exit(EXIT_FAILURE); }

char * textID;
short recvPort;
char * ip;
short sendPort
int hasToken;
char * protocol;

const char * getIpByName(const char * hostName);

int sendSetup();
int recvSetup();


int main(int argc, char *argv[]){
    if(argc != 6){
        err("wrong nbm of args \n");
    }
    
    textID = argv[1];
    recvPort = atoi(argv[2]);
    char *ipAndPort = argv[3];
    char *tokens[] = strtok(ipAndPort, ":");
    if(tokens){
        ip = tokens[0];
        sendPort = atoi(tokens[1]);
    }else {
        err("parsing error");
    }
    hasToken = atoi(argv[4]);
    protocol = argv[5];
    
    int sendHandle = sendSetup();
    int recvHandle = recvSetup();

    char buffer[ 10000 ] = "GET / HTTP/1.1\nHOST: cpp0x.pl\n\n";
   
    send( socketHandle, buffer, strlen( buffer ), 0 );
   
    while( recv( socketHandle, buffer, sizeof( buffer ), 0 ) > 0 ){
        puts( buffer );
        if( entireWebsidedLoaded( buffer ) ){
            strcpy( buffer, "^]" ); /// exit character
            send( socketHandle, buffer, strlen( buffer ), 0 );
            break;
        }
    }
   
    shutdown(recvHandle, SHUT_RDWR);
    shutdown(sendHandle, SHUT_RDWR);
   
    return 0;
}

const char * getIpByName( const char * hostName ){
    struct hostent * he = NULL;
   
    if(( he = gethostbyname( hostName ) ) == NULL ){
        herror( "gethostbyname" );
        exit( - 1 );
    }
   
    const char * ipAddress = inet_ntoa( **( struct in_addr ** ) he->h_addr_list );
    puts( ipAddress );
    return ipAddress;
}

int sendSetup(){
    struct sockaddr_in sender =
    {
        .sin_family = AF_INET,
        .sin_port = htons(sendPort)
    };
   
    const char * ipAddress = getIpByName(ip);
    inet_pton( sender.sin_family, ipAddress, & sender.sin_addr );
    const int socketHandle = socket(sender.sin_family, SOCK_STREAM, 0);
    if(socketHandle == -1){
       err("socket create error");
    }
    if(connect(socketHandle, (struct sockaddr *) &sender, sizeof(sender) == -1){
        err("socket connect error");
    }

    return socketHandle;
}

int recvSetup(){

    struct sockaddr_in reciever=
    {
        .sin_family = AF_INET,
        .sin_port = htons(recvPort)
    };
    if(inet_pton(AF_INET, "127.0.0.1", &reciever.sin_addr) <= 0 ){
        err("inet_pton_error");
    }
   
    const int socketHandle = socket(AF_INET, SOCK_STREAM, 0);
    if(socketHandle < 0){
        err("socket() ERROR");
    }
   
    socklen_t len = sizeof( reciever );
    if(bind(socketHandle, (struct sockaddr *) &reciever, len) < 0){
        err("bind() ERROR");
    }
   
    if(listen(socketHandle, MAX_CONNECTION) < 0){
        err("listen() ERROR");
    }
    return socketHandle;
}