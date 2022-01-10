package MusicFile;

import java.io.Serializable;

public class MusicFile implements Serializable {
    static final long serialVersionUID =-4490313382166500541L;
    String track;
    String artist;
    String albumInfo;
    String genre;
    byte[] musicFileEctract;

    public MusicFile() {
    }

    public MusicFile(String track, String artist, String albumInfo, String genre) {
        this.track = track;
        this.artist = artist;
        this.albumInfo = albumInfo;
        this.genre = genre;
    }
    public MusicFile(String track, String artist, String albumInfo, String genre, byte[] musicFileEctract) {
        this.track = track;
        this.artist = artist;
        this.albumInfo = albumInfo;
        this.genre = genre;
        this.musicFileEctract = musicFileEctract;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumInfo() {
        return albumInfo;
    }

    public void setAlbumInfo(String albumInfo) {
        this.albumInfo = albumInfo;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public byte[] getMusicFileEctract() {
        return musicFileEctract;
    }

    @Override
    public String toString() {
        return "MusicFile.MusicFile{" +
                "track='" + track + '\'' +
                ", artist='" + artist + '\'' +
                ", albumInfo='" + albumInfo + '\'' +
                ", genre='" + genre + "\'}";
    }

    public void setMusicFileEctract(byte[] musicFileEctract) {
        this.musicFileEctract = musicFileEctract;
    }
}
