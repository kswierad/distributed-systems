package pl.edu.agh.cs.sr.server.search;

import akka.actor.AbstractActor;
import pl.edu.agh.cs.sr.commands.SearchExecute;
import pl.edu.agh.cs.sr.commands.SearchResponse;

import java.io.File;
import java.util.Scanner;

public class SearchWorker extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchExecute.class, request -> {
                    Scanner scanner = new Scanner(new File(request.getDatabasePath()));

                    String line = null;
                    while (scanner.hasNext()) {
                        line = scanner.nextLine();
                        if(line.startsWith(request.getSearch().getName())) {
                            System.out.println("Found: " + line);
                            break;
                        }
                    }
                    SearchResponse response;
                    if(line != null){
                        String[] lineSplit = line.split(" ");
                        response = new SearchResponse(request.getSearch().getName(), true,
                                request.getSearch().getClientPath(), Double.parseDouble(lineSplit[1]));

                    } else {
                        response = new SearchResponse(request.getSearch().getName(), false,
                                request.getSearch().getClientPath(), 0.0);

                    }
                    getSender().tell(response, getSelf());

                    getContext().stop(getSelf());
                })
                .matchAny(o -> System.out.println("Worker received unknown message"))
                .build();
    }
}
