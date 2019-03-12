struct sockaddr_in setup_tcp_rcv(int *socket_fd, int port);

void setup_tcp_send(int *socket_ds, int port, struct sockaddr_in *addr);