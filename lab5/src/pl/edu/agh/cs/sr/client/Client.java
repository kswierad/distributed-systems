package pl.edu.agh.cs.sr.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import pl.edu.agh.cs.sr.commands.Order;
import pl.edu.agh.cs.sr.commands.Search;
import pl.edu.agh.cs.sr.commands.Stream;

import java.io.File;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {

        // config
        File configFile = new File("config/client.conf");
        Config config = ConfigFactory.parseFile(configFile);
        // create actor system & actors
        final ActorSystem system = ActorSystem.create("client_system", config);
        final ActorRef clientActor = system.actorOf(Props.create(ClientActor.class, "akka.tcp://server_system@127.0.0.1:3552/user/serverActor"), "clientActor");

        final String clientActorPath = system.provider().getDefaultAddress().toString() + "/user/clientActor";
        System.out.println(clientActorPath);
         //       "akka.tcp://client_system@127.0.0.1:2552/user/clientActor";


        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!input.equals("exit")) {
            String name = input.split(" ")[1];
            if(input.startsWith("stream")) {

                Stream request = new Stream(name, clientActorPath);
                clientActor.tell(request, ActorRef.noSender());
            } else if (input.startsWith("search")) {

                Search request = new Search(name, clientActorPath);
                clientActor.tell(request, ActorRef.noSender());
            } else if (input.startsWith("order")) {

                Order request = new Order(name, clientActorPath);
                clientActor.tell(request, ActorRef.noSender());
            } else {
                System.out.println("Unknown operation!");
            }
            input = scanner.nextLine();
        }
    }
}
