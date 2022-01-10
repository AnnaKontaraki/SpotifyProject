public class ArtistName {
	String artistName;
	
	public ArtistName(){
		this.artistName = "Unknown";
	}
	
	public ArtistName(String name){
		this.artistName = name;
	}
	
	public void setArtistName(String name){
		this.artistName = name;
		return;
	}
	
	public String getArtistName(){
		return artistName;
	}
}