#include "client.h"
#include <stdlib.h>
#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <string.h>
#include <netdb.h>
#include <sys/types.h>
#include <signal.h>
#include "tcp.h"
#include "udp.h"

#define err(s) {\
        perror((s));\
        exit(EXIT_FAILURE); }

int sock_in;
int sock_out;


void before_exit(int signum) {
    printf("\nexiting\n");
    shutdown(sock_in, SHUT_RDWR);
    shutdown(sock_out, SHUT_RDWR);
}

int main(int argc, char **argv) {

    char *id;
    int protocol;
    int in_port;
    char *neigh_address;
    int out_port;
    int has_token;
    int logger_fd;
    token ring_token;

    signal(SIGINT, exit);
    atexit(before_exit);

    if(argc != 7){
        err("wrong nmb of args \n");
    }
    
    id = argv[1];
    in_port = atoi(argv[2]);
    neigh_address = argv[3];
    out_port = atoi(argv[4]);
    has_token = atoi(argv[5]);
    if(strcmp(argv[5], "UDP")) protocol = 1;
    else if(strcmp(argv[5], "TCP")) protocol = 0;
    else err("Wrong protocol \n");

    init_udp_socket_client(&logger_fd);

    if (has_token) {
        int socket_cli;
        token read_token;

        if (protocol) {
            setup_tcp_rcv(&sock_in, in_port);

            struct sockaddr_in client;
            socklen_t len = sizeof(client);
            socket_cli = accept(sock_in, (struct sockaddr *) &client, &len);
            if (socket_cli < 0) {
                err("server accept failed\n");
            }

            read(socket_cli, &read_token, sizeof(token));
            out_port = read_token.port;
            write(socket_cli, &in_port, sizeof(in_port));

        } else {
            init_udp_socket_server(&sock_in, in_port);
            struct sockaddr_in cli;
            socklen_t len;
            recvfrom(sock_in, &read_token, sizeof(token), MSG_WAITALL,
                     (struct sockaddr *) &cli, &len);
            out_port = read_token.port;

            sendto(sock_in, &in_port, sizeof(in_port), MSG_CONFIRM,
                   (struct sockaddr *) &cli, len);
        }

        sleep(1);

    } else {

        struct sockaddr_in addr;
        token token1;
        token1.type = CONNECT;
        token1.port = in_port;
        int response_port;

        if (protocol) {
            setup_tcp_send(&sock_out, out_port, &addr);
            while (connect(sock_out, (const struct sockaddr *) &addr, sizeof(addr)) == -1) {
                printf("waiting to connect\n");
                usleep(500000);
            }
            if (write(sock_out, &token1, sizeof(token)) < 0) {
                printf("can't log on server\n");
            }

            read(sock_out, &response_port, sizeof(response_port));
            out_port = response_port;

            setup_tcp_rcv(&sock_in, in_port);

        } else {

            init_udp_socket_client(&sock_out);
            struct sockaddr_in cli = init_udp_send(sock_out,
                    out_port, htonl(INADDR_ANY), &token1, MSG_CONFIRM, sizeof(token));
            socklen_t len = sizeof(cli);
            recvfrom(sock_out, &response_port, sizeof(response_port), MSG_WAITALL,
                     (struct sockaddr *) &cli, &len);

            out_port = response_port;
            init_udp_socket_server(&sock_in, in_port);
        } 

        printf("connection established\n");
    }

    // network loop

    while (1) {

        if (has_token) {
            // MULTICAST
            init_udp_send(logger_fd, LOGGER_PORT, inet_addr("224.0.0.1"),
                          id, MSG_DONTWAIT, sizeof(id));
            ring_token.type = TOKEN;
            printf("trying to connect to %d\n", out_port);
            struct sockaddr_in addr;

            if (protocol == TCP) {
                setup_tcp_send(&sock_out, out_port, &addr);
                while (connect(sock_out, (const struct sockaddr *) &addr, sizeof(addr)) == -1) {
                    printf("waiting to connect\n");
                    usleep(500000);
                }
                write(sock_out, &ring_token, sizeof(ring_token));

                close(sock_out);

            } else {
                init_udp_socket_client(&sock_out);
                init_udp_send(sock_out, out_port, htonl(INADDR_ANY),
                              &ring_token, MSG_CONFIRM, sizeof(token));
            }

            has_token = 0;
            printf("TOKEN PASSED!\n");

        } else {
             if (protocol == TCP) {
                int socket_cli;
                struct sockaddr_in client;
                socklen_t len = sizeof(client);
                socket_cli = accept(sock_in, (struct sockaddr *) &client, &len);
                if (socket_cli < 0) {
                    err("server accept failed\n");
                }

                read(socket_cli, &ring_token, sizeof(ring_token));

                // ---handling new requests------
                if (ring_token.type == CONNECT) {
                    write(socket_cli, &out_port, sizeof(out_port));
                    out_port = ring_token.port;
                    continue;
                }

                close(socket_cli);

            } else {
                struct sockaddr_in cli;
                socklen_t len;
                recvfrom(sock_in, &ring_token, sizeof(ring_token), MSG_WAITALL,
                         (struct sockaddr *) &cli, &len);

                if (ring_token.type == CONNECT) {
                    sendto(sock_in, &out_port, sizeof(out_port), MSG_CONFIRM,
                           (struct sockaddr *) &cli, len);
                    out_port = ring_token.port;
                    continue;
                }
            }

            has_token = 1;
        }

        sleep(1);
    }

}