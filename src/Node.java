import java.util.*;
public interface Node {
List<BrokerImp> brokers = new ArrayList<BrokerImp>();
public void init (int a);
public static List<BrokerImp> getBrokers(){
    return brokers;
}
List<BrokerImp> list = new ArrayList<BrokerImp>();
public void connect();
public void disconnect();
public void updateNodes();
void insert(BrokerImp b);
void print();
}