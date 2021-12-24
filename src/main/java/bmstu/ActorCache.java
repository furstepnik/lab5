package bmstu;

import akka.actor.AbstractActor;

public class ActorCache extends AbstractActor {
    private final Map<String, Long> store = new HashMap<>();
    @Override
    public Receive createReceive() {
        
    }
}
