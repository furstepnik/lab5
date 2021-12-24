package bmstu;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.japi.function.Function2;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import akka.japi.Pair;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;


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
                    if (query.get(URL).isPresent()) {
                        url = query.get(URL).get();
                    } else {
                        throw new ParseArgException("No URL parameters");
                    }
                    int count;
                    if (query.get(Count).isPresent()) {
                        try {
                            count = Integer.parseInt(query.get(COUNT).get());
                            return new Pair<>(url, count);
                        } catch (NumberFormatException e) {
                            throw new ParseArgException("Can not parse value of count");
                        }
                    } else {
                        throw new ParseArgException("No count parameter");
                    }
                }
        ).mapAsync(Number, req ->
                Patterns.ask(cache, new Message(req.first()), Duration.ofSeconds(DURATION)).thenCompose(
                        res -> {
                            if ((long) res >= 0) {
                                return CompletableFuture.completedFuture(new Pair<>(req.first(), (long)res));
                            } else {
                                Sink<Pair<String, Integer>, CompletionStage<Long>> sink = Flow.<Pair<String, Integer>>create()
                                        .mapConcat(pair ->
                                                new ArrayList<>(Collections.nCopies(pair.second(), pair.first())))
                                        .mapAsync(req.second(), url -> {
                                            long initTime = System.currentTimeMillis();
                                            Request request = Dsl.get(url).build();
                                            CompletableFuture<Response> resp = Dsl.asyncHttpClient().executeRequest(request).toCompletableFuture();
                                            return resp.thenCompose(response -> CompletableFuture.completedFuture((int) (System.currentTimeMillis() - initTime)));
                                        }).toMat(Sink.fold(0L, (Function2<Long, Integer, Long>) Long::sum), Keep.right());
                                return Source.from(Collections.singletonList(req))
                                        .toMat(sink, Keep.right())
                                        .run(materializer)
                                        .thenApply(s ->
                                                new Pair<>(req.first(), s / req.second()));
                            }
                        }
                )).map(resultPair -> {
                    

    }
}
