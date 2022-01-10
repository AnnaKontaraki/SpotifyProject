import java.util.*;
public class NodeImp implements Node{
    static List<BrokerImp> brokers = new ArrayList<BrokerImp>();
    public void init (int a){

    }
    public static List<BrokerImp> getBrokers(){
return brokers;
}

    public void connect() {
    }

    public void disconnect(){
    }
    public void updateNodes(){
    }

    public void insert(BrokerImp b) {
        //Broker obj = new BrokerImp(p,ip);
        brokers.add(b);
    }

    public void print(){
        for(BrokerImp broker: brokers)
            System.out.println("value: " + broker);
    }
}