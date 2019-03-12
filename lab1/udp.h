#include <netinet/in.h>

void init_udp_socket_server(int *socket_fd, int port);

void init_udp_socket_client(int *socket_fd);

struct sockaddr_in init_udp_send(int socket_fd, int port, in_addr_t addr, void *buff, int flag, int size);