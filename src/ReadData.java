import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

import MusicFile.MusicFile;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class ReadData {
    static ArrayList<MusicFile> songs = new ArrayList<MusicFile>();
    static ArrayList<ArtistName> artists = new ArrayList<ArtistName>();

    public void files(String fileLocation){
        try {

            InputStream input = new FileInputStream((fileLocation));
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input,handler, metadata,parseCtx);
            input.close();

            input = new FileInputStream((fileLocation));
            MusicFile musicFile = new MusicFile(metadata.get("title"),
                    metadata.get("xmpDM:artist"), metadata.get("xmpDM:album"), metadata.get("xmpDM:genre"),
                    inputStreamToByteArray(input));
            if(musicFile.getArtist()!=null) songs.add(musicFile);
            input.close();


        } catch (IOException | TikaException | SAXException e) {
            e.printStackTrace();
        }
    }


            public void listFilesForFolder(final File folder){
                for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                    if (fileEntry.isDirectory()) {
                        listFilesForFolder(fileEntry);
                    } else{
                        String sub = fileEntry.getName().substring(0,fileEntry.getName().length()-4);

                        if(!sub.startsWith("._")) {
                            files("C:\\Users\\User\\Desktop\\dataset3\\" + sub + ".mp3");
                            songs.get(songs.size()-1).setTrack(sub);
                        }

                    }
                }
            }
            public static byte[] inputStreamToByteArray(InputStream inStream) throws IOException {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) > 0) {
                    baos.write(buffer, 0, bytesRead);
                }
                return baos.toByteArray();
            }

            public ArrayList<MusicFile> getSongsList() {
                return songs;
            }
            public  ArrayList<ArtistName> getArtistsList() {
                ArrayList<String> temp = new ArrayList<String>();
                for(MusicFile m:songs){
                    String artist = m.getArtist();
                    if(!temp.contains(artist) &&artist!=null) {
                        temp.add(artist);
                        ArtistName a = new ArtistName(artist);
                        artists.add(a);
                    }
                }
                return artists;
            }
}