import java.io.IOException;
import java.util.*;
public interface Publisher extends Node {
    List<BrokerImp> getBrokerList();
   BrokerImp hashTopic(ArtistName a);
    void push(ArtistName a, Value b) throws IOException;
    void notifyFailure(BrokerImp a);
}