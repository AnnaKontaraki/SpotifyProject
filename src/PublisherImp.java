import MusicFile.MusicFile;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PublisherImp extends NodeImp implements Publisher,Serializable{

	public static ArrayList<Integer> ports = new ArrayList<Integer>( Arrays.asList(9090,9080,9070));
	public static String IP = "127.0.0.1";
	public static ArrayList<ArtistName> Artists = new ArrayList<ArtistName>();
	public static ArrayList<MusicFile> songs = new ArrayList<MusicFile>();
	public static ArrayList<BrokerImp> brokers = new ArrayList<BrokerImp>();

	public static final String path = "C:\\Users\\User\\Desktop\\dataset3\\";
	public static Hashtable<String, Integer> PubhashTable = new Hashtable<String, Integer>();

	public static void main(String[] args) throws IOException {

		ReadData a = new ReadData();
		a.listFilesForFolder(new File(path));
		for(int bp : ports){
			BrokerImp br = new BrokerImp(IP,bp);
			br.setHash(calculatedKeys(IP,bp));
			brokers.add(br);
			System.out.println(br.getHash());
		}

		ArrayList<BrokerImp> right = new ArrayList<BrokerImp>();
		for (BrokerImp br : brokers) {
			if (right.isEmpty()) {
				right.add(br);
			} else {
				for (int i = 0; i < right.size(); i++) {
					if (br.getHash().compareTo(right.get(i).getHash()) <= 0 && i == right.size() - 1) {
						right.add(i, br);
						break;
					} else {
						right.add(i, br);
						break;
					}
				}
			}
		}
		Artists = a.getArtistsList();
		songs = a.getSongsList();
		System.out.println(songs);
		for (ArtistName artist : Artists) {
			int portOfBroker = BrokerPort(artist.getArtistName(), right);
			PubhashTable.put(artist.getArtistName(), portOfBroker);
		}
		System.out.println(PubhashTable);
		Scanner console = new Scanner(System.in);
		System.out.print("Enter port number: ");
		int portNumber = console.nextInt();



		for (int p = 0; p < ports.size(); p++) {
			Socket publisherclient = new Socket(IP, ports.get(p));
			ObjectOutputStream out2 = new ObjectOutputStream(publisherclient.getOutputStream());
			String portpub = Integer.toString(portNumber);
			out2.writeObject(IP + "/" + portpub);
			try {
				ObjectInputStream input = new ObjectInputStream(publisherclient.getInputStream());

				out2.writeObject(PubhashTable);


			} catch (IOException e) {
				System.out.println("The socket for reading the object has problem");
				e.printStackTrace();
			}

			publisherclient.close();
		}

		ServerSocket publisherserv = new ServerSocket(portNumber);
		while (true) {
			System.out.println("[PUBLISHER] Waiting for BROKER connection. . .");
			Socket client = publisherserv.accept();
			System.out.println("[PUBLISHER] Connects to BROKER!");
			MyThread2 c = new MyThread2(client);
			c.run(Artists,songs,path);
		}
	}

	public static BigInteger calculatedKeys(String ip, int port) {
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");

			String str1 = Integer.toString(port);
			String str2 =ip;
			String str = str1 + str2 ;
			byte[] messageDigest = md.digest(str.getBytes());

			BigInteger i = new BigInteger(1,messageDigest);
			return i;

		}catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static int port() throws IOException {
		File file = new File("C:\\Users\\User\\Desktop\\DS\\src\\ports.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String st;
		int a=0;
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("PUBLISHERS:");
		boolean flag = true;
		while((st = br.readLine()) != null){
			if(!st.contains("PUBLISHERS:")){
				if(!st.contains("IN USE")&& flag==true){
					lines.add(st + " IN USE");
					a = Integer.parseInt(st);
					flag = false;
				} else {
					lines.add(st);
				}
			}
		}
		if(flag==true){
			System.out.println("No available port for PUBLISHER node right now!");
		}
		br.close();
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file));
		for(String l : lines){
			bufWriter.write(l+"\n");
		}
		bufWriter.close();
		return a;
	}
	public static int BrokerPort(String str, List<BrokerImp> node) {

		try{

			MessageDigest md = MessageDigest.getInstance("MD5");
			//String str = artist.getArtistName();
			byte[] messageDigest = md.digest(str.getBytes());

			BigInteger i = new BigInteger(1,messageDigest);
			while(i.compareTo(node.get(node.size()-1).getHash())>0){
				i = i.mod(node.get(0).getHash());
			}
			for( int j =0;j<node.size(); j++) {
				if(j == node.size()-1){
					return node.get(j).SERVER_PORT;
				} else {
					if(node.get(j).getHash().compareTo(i)>=0){
						return node.get(j).SERVER_PORT;
					}
				}
			}
			return 0;
		}catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public List<BrokerImp> getBrokerList() {
		return null;
	}

	@Override
	public BrokerImp hashTopic(ArtistName a) {
		return null;
	}

	@Override
	public void push(ArtistName a, Value b) throws IOException { }

	@Override
	public void notifyFailure(BrokerImp a) { }

}

class MyThread2 extends Thread implements Runnable{
	private Socket client;
	private BufferedReader in;
	private ObjectOutputStream out;

	public MyThread2 (Socket clientSocket) throws IOException {
		this.client = clientSocket;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new ObjectOutputStream(client.getOutputStream());
	}

	public void run(ArrayList<ArtistName> a, ArrayList<MusicFile> b, String path) throws IOException {
		System.out.println("MyThread running");
		String request = null;
		try {
			request = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (request == null) {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(request);
		for (int i = 0; i < a.size(); i++) {
			if (a.get(i).getArtistName().equals(request)) {
				ArrayList<String> specsongs = new ArrayList<String>();
				for(MusicFile m : b){
					if(m.getArtist().equals(request)){
						specsongs.add(m.getTrack());
					}
				}
				out.writeObject(specsongs);
				//στελνει την λιστα με τα τραγουδια του καλλιτεχνη αυτου
				break;
			}
		}
		request = in.readLine();
		out.writeObject(path + request);
	}
}