import MusicFile.MusicFile;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;

import java.io.*;
import java.util.*;
import java.net.*;
import java.math.BigInteger; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 
import java.util.ArrayList;

public class BrokerImp extends NodeImp implements Broker,Serializable{
    public static ArrayList<Integer> ports = new ArrayList<Integer>( Arrays.asList(9090,9080,9070,9060,9050,9040));
    public static String IP = "127.0.0.1";
    public static List<BrokerImp> BrokersHashes = new ArrayList<BrokerImp>();
    public static BigInteger hash;
    public static int SERVER_PORT;
    public static ArrayList<String> pubip = new ArrayList<String>();
    public static ArrayList<Integer> pubport = new ArrayList<Integer>( Arrays.asList(9060,9050));
    public static ArrayList<ArtistName> availableartists = new ArrayList<ArtistName>();
    public static ArrayList<String> range = new ArrayList<String>();
    public static Hashtable<String,Integer> BrhashTable = new Hashtable<String,Integer>();



    public static void setRange(ArrayList<String> range) {
        BrokerImp.range = range;
    }

    public static void setPort(int x){ SERVER_PORT=x; }

    public static int getPort() {
        return SERVER_PORT;
    }

    public static String getIP() {
        return IP;
    }

    public BrokerImp(String ip, int port) {
        this.IP=ip;
        this.SERVER_PORT = port;
    }
    public BigInteger getHash() {
        return hash;
    }
    public void setHash(BigInteger hash) {
        this.hash = hash;
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, TagException {
        Scanner console = new Scanner(System.in);
        System.out.print("Enter port number: ");
        int portNumber = console.nextInt();
        BrokerImp broker = new BrokerImp(IP,portNumber);
        setPort(portNumber);
        broker.calculatedKeys();
        int count = 0;
        Node.brokers.add(broker);
        ServerSocket brokerserver = new ServerSocket(portNumber);
       while(count < 2){
            System.out.println("[BROKER] Waiting for PUBLISHER connection. . .");
            Socket clientserv = brokerserver.accept();
            System.out.println("[BROKER] Connects to PUBLISHER!");
            MyThread1 thread = new MyThread1(clientserv);
            thread.run(broker,pubip,pubport,BrhashTable);
            count++;
        }
        System.out.println("hash table is " + BrhashTable);

           BrhashTable.forEach((artist,port) -> {
               if(BrhashTable.contains(portNumber)){
                   if(port == portNumber){
                       availableartists.add(new ArtistName(artist));
                   }
               }
           });

        while(true) {
            System.out.println("[BROKER] Waiting for CONSUMER connection. . .");
            Socket consumer = brokerserver.accept();
            System.out.println("[BROKER] Connects to CONSUMER!");
            MyThread a = new MyThread(brokerserver,consumer);
            a.start(broker);
        }
    }
    @Override
    public void calculatedKeys() {
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");

            String str1 = Integer.toString(SERVER_PORT);
            String str2 =IP;
            String str = str1 + str2 ;
            byte[] messageDigest = md.digest(str.getBytes());

            BigInteger i = new BigInteger(1,messageDigest);
            this.setHash(i);

        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PublisherImp acceptConnection(PublisherImp a) {
        return null;
    }

    @Override
    public void notifyPublisher(String a) {

    }

    @Override
    public void pull(ArtistName a) throws IOException {

    }

    public static int PORT_NUMBER() throws IOException {
        File file = new File("port.txt");
        BufferedWriter output = new BufferedWriter(new FileWriter(file, true));
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while((st = br.readLine()) != null){
                if(!st.contains("BROKERS")){
                    if(!st.contains("IN USE")){
                        int a;
                        a = Integer.parseInt(st);
                        output.write(st+"IN USE");
                    }
                }
            }
        return 0;
    }


    public static ArrayList<MusicFile> splitFile(File f) throws IOException, TagException, TagException {//method to split an mp3 file
        int partCounter = 1;//name parts from 001, 002, 003, ...
        ArrayList<MusicFile> musicFiles = new ArrayList<MusicFile>();

        int sizeOfFiles = 1024 * 1024;// 1MB
        byte[] buffer = new byte[sizeOfFiles];
        MP3File mp3file = new MP3File(f);
        AbstractID3v2 tag = mp3file.getID3v2Tag();
        String fileName = f.getName();

        //try-with-resources to ensure closing stream
        try (FileInputStream fis = new FileInputStream(f);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            int bytesAmount = 0;
            while ((bytesAmount = bis.read(buffer)) > 0) {
                //write each chunk of data into separate file with different number in name
                String filePartName = String.format("%s.%03d", fileName, partCounter++);
                filePartName = filePartName.concat(".mp3"); //add .mp3 at the end so every part is an mp3 file
              //  File newFile = new File(f.getParent(), filePartName);
              //  try (FileOutputStream out = new FileOutputStream(newFile)) {
             //       out.write(buffer, 0, bytesAmount);
           //     }

                FileInputStream fin = null;
                try {
                    fin = new FileInputStream(f);
                    byte[] musicFileExtract = new byte[sizeOfFiles];
                    fin.read(musicFileExtract);
                    MusicFile musicfile = new MusicFile(tag.getSongTitle(), tag.getLeadArtist(), tag.getAlbumTitle(), tag.getSongGenre(), musicFileExtract);
                    musicFiles.add(musicfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return musicFiles;
    }
}
class MyThread extends Thread implements Runnable {
    private ServerSocket brokerImp;
    private Socket consumer;

    public MyThread(ServerSocket broker,Socket consumer){
        this.consumer = consumer;
        this.brokerImp = broker;
    }
    public void start(BrokerImp broker) throws IOException, ClassNotFoundException {
        MyThread3 thread3 = null;
        try {
            thread3 = new MyThread3(consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String asked = thread3.run1(broker.availableartists, broker.BrhashTable);
        if (asked != null) {
            int rightone;
            if (asked.charAt(0) > 'M') {
                rightone = broker.pubport.get(1);
            } else {
                rightone = broker.pubport.get(0);
            }
            System.out.println(rightone);
            Socket brokerclient = new Socket(broker.IP, rightone);
            ObjectInputStream in = new ObjectInputStream(brokerclient.getInputStream());
            PrintWriter out = new PrintWriter(brokerclient.getOutputStream(), true);
            out.println(asked);
            ArrayList<String> songs = new ArrayList<String>();
            Object object = in.readObject();
            songs = (ArrayList<String>) object;
            System.out.println(songs);
            String songname = thread3.run2(songs);

            out.println(songname + ".mp3");
            String pathResponse = (String) in.readObject();
            System.out.println(pathResponse);
            brokerclient.close();

            ArrayList<MusicFile> chunks = new ArrayList<MusicFile>();
            try {
                chunks = broker.splitFile(new File(pathResponse));
            } catch (TagException e) {
                e.printStackTrace();
            }
            thread3.run3(chunks);
        }
    }
}
class MyThread1 extends Thread implements Runnable {
    private Socket client;
    private ObjectOutputStream out;
    private ObjectInputStream in2;

    public MyThread1(Socket clientSocket) throws IOException {
        this.client = clientSocket;
        out = new ObjectOutputStream(client.getOutputStream());
        in2 = new ObjectInputStream(client.getInputStream());
    }

    public void run(BrokerImp b, List<String> pubip, List<Integer> pubport, Hashtable<String, Integer> hashtable) throws IOException, ClassNotFoundException {
        System.out.println("MyThread running");
        String request = null;


            Object ob = in2.readObject();

            request = (String)ob;
            System.out.println(request);
            int find = request.indexOf("/");
            if (find != 0) {
                String ip = request.substring(0, find);
                String f = request.substring(find + 1, request.length());
                int port = Integer.parseInt(f);
                pubip.add(ip);
                pubport.add(port);
            }
        Object hash = in2.readObject();
        b.BrhashTable = (Hashtable<String, Integer>) hash;


    }
}
class MyThread3 extends Thread implements Runnable{
    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public MyThread3 (Socket clientSocket) throws IOException {
        this.client = clientSocket;
        out = new ObjectOutputStream(client.getOutputStream());
        in = new ObjectInputStream(client.getInputStream());
    }

    public String run1(ArrayList<ArtistName> avail,Hashtable<String,Integer> br) throws IOException, ClassNotFoundException {
        System.out.println("MyThread running");
        Object a = in.readObject();
        String request = (String) a;
        System.out.println(request);
        if (request == null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (request.equals("@")){
            out.writeObject(br);
            client.close();
            return null;
        }
        for (ArtistName artistName : avail) {
            if ((artistName.getArtistName()).equals(request)) {
                return request;
            }
        }
        out.writeObject("There is no such Artist available");
        client.close();
        return null;
    }
    public String run2(ArrayList<String> songs) throws IOException, ClassNotFoundException {
        String request = null;
        out.writeObject(songs);
        Object temp = in.readObject();
        request = (String) temp;
        if (request == null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return request;
    }

    public void run3(ArrayList<MusicFile> chunks) throws IOException {
        if(chunks==null){
            out.writeObject(null);
            return;
        }
        for(int i = 0; i<chunks.size();i++){
            out.writeObject(chunks.get(i));
        }
        out.writeObject(null);
    }
}