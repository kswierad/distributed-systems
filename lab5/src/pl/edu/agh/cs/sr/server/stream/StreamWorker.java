package pl.edu.agh.cs.sr.server.stream;

import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.stream.ActorMaterializer;
import akka.stream.OverflowStrategy;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.Timeout;
import pl.edu.agh.cs.sr.commands.Error;
import pl.edu.agh.cs.sr.commands.Stream;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class StreamWorker extends AbstractActor {
    private String books = "data/books";
    private ActorSelection client;
    private File[] fileList;
    private File file = null;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Stream.class, request -> {
                    client = context().actorSelection(request.getClientPath());
                    fileList = getFileList(books);

                    // search for file
                    for(File f : fileList){
                        if(f.toString().contains(request.getName())){
                            System.out.println("Found file to stream: " + f.toString());
                            file = f;
                            break;
                        }
                    }

                    //stream file
                    if(file != null) {
                        System.out.println("Streaming " + file.toString());
                        ActorMaterializer mat = ActorMaterializer.create(getContext());
                        Future<ActorRef> futureClient = client.resolveOne(new Timeout(5, TimeUnit.SECONDS));
                        ActorRef clientRef = Await.result(futureClient, Duration.create(5, "seconds"));
                        ActorRef run = Source.actorRef(1000, OverflowStrategy.dropNew())
                                .throttle(1, FiniteDuration.create(1, TimeUnit.SECONDS), 1, ThrottleMode.shaping())
                                .to(Sink.actorRef(clientRef, NotUsed.getInstance()))
                                .run(mat);

                        java.util.stream.Stream<String> lines = Files.lines(file.toPath());
                        lines.forEachOrdered(
                                line -> run.tell(line, getSelf()));

                    }
                    else {
                        System.out.println("File " + request.getName() + " not found.");
                        Error err = new Error("File was not found");
                        client.tell(err, getSelf());
                    }
                })
                .matchAny(o -> System.out.println("Stream worker received unknown message"))
                .build();
    }

    private File[] getFileList(String directory_name){
        File directory = new File(directory_name);
        return directory.listFiles();
    }
}
