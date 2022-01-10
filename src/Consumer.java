import java.io.IOException;
import java.util.*;
public interface Consumer extends Node {
void register(BrokerImp a, ArtistName b);
void disconnect(BrokerImp a, ArtistName b);
void playData(ArtistName a, Value b);
}