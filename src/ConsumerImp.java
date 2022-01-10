import MusicFile.MusicFile;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ConsumerImp extends NodeImp implements Consumer,Serializable {
    private static final String SERVER_IP = "127.0.0.1";
    private static ArrayList<Integer> SERVER_PORT = new ArrayList<Integer>(Arrays.asList(9090,9080,9070));
    public static String name;
    public static Hashtable<String,Integer> table = new Hashtable<String, Integer>();
    public static ArrayList<String> artists = new ArrayList<String>();
    public ConsumerImp() throws IOException {
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        register(9070, "@");
        if(!table.isEmpty()) {
            while (true) {
                    Scanner console = new Scanner(System.in);
                    System.out.print("> Enter an Artist: ");
                    name = console.nextLine();
                    if (name.equals("quit")) break;
                    connection(table.get(name), name);
            }
        }
        System.exit(0);
    }

    public static void mergeFiles(ArrayList<MusicFile> musicfiles, File into) throws IOException {
        ArrayList<byte[]> mp3files = new ArrayList<>();
        for(int i = 0; i < musicfiles.size(); i++){
            MusicFile f = musicfiles.get(i);
            mp3files.add(f.getMusicFileEctract());
        }

        try (FileOutputStream fos = new FileOutputStream(into)) {
            for (int i = 0; i < mp3files.size(); i++) {
                fos.write(mp3files.get(i));
            }
        }
    }

    @Override
    public void register(BrokerImp a, ArtistName b) {

    }

    @Override
    public void disconnect(BrokerImp a, ArtistName b) {

    }

    @Override
    public void playData(ArtistName a, Value b) {

    }

    public static void register(int port, String name) throws IOException, ClassNotFoundException {
        if(name.equals("@")){
            Socket socket = new Socket(SERVER_IP, port);
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(name);
            Object hash = input.readObject();
            table = (Hashtable<String, Integer>) hash;
            artists = new ArrayList<>(table.keySet());
            socket.close();
        }
    }

    public static void connection(int port, String name) throws IOException, ClassNotFoundException {
            Scanner console = new Scanner(System.in);
            Socket socket = new Socket(SERVER_IP, port);
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            out.writeObject(name);
            Object o = input.readObject();
            ArrayList<String> serverResponse = (ArrayList<String>) o;
            System.out.println("Server says: " + serverResponse);
            String noArtist = "There is no such Artist available";
            if (serverResponse.contains(noArtist)) {
                System.out.println("fail");
            }

            System.out.println("> Enter a song: ");
            name = console.nextLine();
            out.writeObject(name);
            ArrayList<MusicFile> music = new ArrayList<MusicFile>();
            MusicFile song;
            while (true) {
                try {
                    o = input.readObject();
                } catch (IOException e) {
                    break;
                }
                song = (MusicFile) o;
                if (song != null) {
                    music.add(song);
                } else {
                    break;
                }
            }
            if (music.isEmpty()) {
                System.out.println("Not enough info for this song");
            } else {
                File download = new File("src\\Downloaded\\" + name);
                mergeFiles(music, download);
                System.out.println("Downloaded");
            }
            socket.close();
    }
}
