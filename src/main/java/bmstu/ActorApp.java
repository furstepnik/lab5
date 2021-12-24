package bmstu;

import com.sun.deploy.net.HttpRequest;
import com.sun.deploy.net.HttpResponse;

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
            
        }
    }
}
