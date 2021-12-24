package bmstu;

public class FlowCreator {
    private static final String URL = "testUrl";
    private static final String COUNT = "count";
    private static final int NUMBER = 10;
    private static final int DURATION = 5;
    public FlowCreator() {}
    public static Flow<HttpRequest, HttpResponse, NotUsed>
                    createFlow(ActorMaterializer materializer, ActorRef cache) throws ParseArgException {
        return Flow.of(HttpRequest.class).map(
                req -> {
                    Query query = req.getUri().query();
                    String url;
    }
}
