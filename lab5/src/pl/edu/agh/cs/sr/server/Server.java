package pl.edu.agh.cs.sr.server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class Server {
    public static void main(String[] args) throws Exception{
        // config
        File configFile = new File("config/server.conf");
        Config config = ConfigFactory.parseFile(configFile);

        // create actor system & actors
        final ActorSystem system = ActorSystem.create("server_system", config);
        final ActorRef serverActor = system.actorOf(Props.create(ServerActor.class), "serverActor");

        if( System.in.read() == 0) system.terminate();

    }
}
