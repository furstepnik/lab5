package bmstu;

public class ActorApp {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main (String[] args) {
        ActorSystem system = ActorSystem.create("routes");
        ActorRef cache = system.actorOf(Props.create(ActorCache.class));
    }
}
