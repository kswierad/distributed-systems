#include "tcp.h"
#include "client.h"
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <netinet/in.h>

struct sockaddr_in setup_tcp_rcv(int *socket_fd, int port) {

    *socket_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (*socket_fd == -1) {
        printf("ERROR: can't create socket\n");
        exit(1);
    }

    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    addr.sin_port = htons((uint16_t) port);
    addr.sin_addr.s_addr = htonl(INADDR_ANY);

    if ((bind(*socket_fd, (struct sockaddr *) &addr, sizeof(addr))) != 0) {
        printf("ERROR: socket bind failed\n");
        exit(1);
    }

    if ((listen(*socket_fd, 10)) != 0) {
        printf("ERROR: listen failed\n");
        exit(1);
    }

    return addr;
}

void setup_tcp_send(int *socket_ds, int port, struct sockaddr_in *addr) {

    *socket_ds = socket(AF_INET, SOCK_STREAM, 0);
    if (*socket_ds == -1) {
        printf("can't create socket\n");
        exit(1);
    }

    addr->sin_family = AF_INET;
    addr->sin_port = htons((uint16_t) port);
    addr->sin_addr.s_addr = htonl(INADDR_ANY);

}
