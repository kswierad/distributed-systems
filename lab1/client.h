#define TCP 1
#define UDP 0

#define FREE 1
#define TAKEN 0

#define CONNECT 10
#define TOKEN 11

#define LOGGER_PORT 7675

typedef struct message {
    char msg[100];
} message;

typedef struct access_record {
    int idx;
    int arr[100];
} access_record;

typedef struct token {
    int type; // 10 - connect, 11 - token
    int usage; // 0 - free, 1 - taken
    //char recipient_name[100]; // client ID
    message msg;
    access_record ac_rec;
    int port;
} token;