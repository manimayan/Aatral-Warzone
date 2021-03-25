package aatral.warzone.gameplay;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class BlockadeCard extends Order{
	public String countryID;
	public GamePlayer gamePlayerObject;
	public BlockadeCard(String countryID) {
		this.countryID = countryID;
	}
	
	public void execute() {
//		 for country in this.listOfCountries:
//				if country.countryId == this.countryId:
//					country.armies*=3;
	}
}
