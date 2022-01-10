import java.io.IOException;
import java.util.*;
public interface Broker extends Node {
List<ConsumerImp> registeredUsers = new ArrayList<ConsumerImp>();
List<PublisherImp> registeredPublishers = new ArrayList<PublisherImp>();
void calculatedKeys();
PublisherImp acceptConnection(PublisherImp a);
void notifyPublisher(String a);
void pull(ArtistName a) throws IOException;
}