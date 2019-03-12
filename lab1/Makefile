.PHONY: all clean run udp

CC=gcc -Wall

all: tcp.o tcp.a udp.o udp.a client.o client

tcp.o: tcp.c
	$(CC) -c $^

tcp.a: tcp.o
	ar rcs $@ $^

udp.o: udp.c
	$(CC) -c $^

udp.a: udp.o
	ar rcs $@ $^

client.o: client.c
	$(CC) -c $^

client: client.o tcp.a udp.a
	$(CC) $^ -o $@

clean:
	rm client client.o tcp.a tcp.o udp.a udp.o

run-client-1:
	./client CLIENT1 18080 127.0.0.1 18081 1 TCP

run-client-2:
	./client CLIENT2 18081 127.0.0.1 18080 0 TCP

run-client-3:
	./client CLIENT3 18082 127.0.0.1 18080 0 TCP

udp-run-client-1:
	./client CLIENT1 8080 127.0.0.1 -1 1 UDP

udp-run-client-2:
	./client CLIENT2 8081 127.0.0.1 8080 0 UDP

udp-run-client-3:
	./client CLIENT3 8082 127.0.0.1 8080 0 UDP

run-logger:
	python logger.py