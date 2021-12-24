package bmstu;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

public class ActorApp {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main (String[] args) {
        ActorSystem system = ActorSystem.create("routes");
        ActorRef cache = system.actorOf(Props.create(ActorCache.class));
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow;
        try {
            routeFlow = FlowCreator.createFlow(materializer, cache);
            final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost(HOST, PORT), materializer);
            System.out.println("Serer online at http://localhost:8080/\nPress RETURN to stop...");
            System.in.read();
            binding.thenCompose(
                    ServerBinding::unbind
            ).thenAccept(
                    unbound -> system.terminate()
            );
        }
    }
}
