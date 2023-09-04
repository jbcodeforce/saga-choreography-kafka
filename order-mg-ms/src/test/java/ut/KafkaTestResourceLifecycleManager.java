package ut;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;

public class KafkaTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager{

    @Override
    public Map<String, String> start() {
        Map<String, String> env = new HashMap<>();
        Map<String, String> props1 = InMemoryConnector.switchIncomingChannelsToInMemory("vessels");     
        Map<String, String> props2 = InMemoryConnector.switchOutgoingChannelsToInMemory("orders"); 
        Map<String, String> props3 = InMemoryConnector.switchIncomingChannelsToInMemory("reefers");    
        env.putAll(props1);
        env.putAll(props2);
        env.putAll(props3);
        return env;  
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();  
    }
    
}
