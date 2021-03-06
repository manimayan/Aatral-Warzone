package aatral.warzone.gameplay;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import aatral.warzone.model.Continent;
import aatral.warzone.model.Countries;
import aatral.warzone.observerPattern.LogEntryBuffer;
import aatral.warzone.observerPattern.LogWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * <h1>Advance Order</h1> This class is used to execute the advance attack or
 * transfer order
 * 
 * @author William Moses
 * @version 1.0
 * @since 24-02-2021
 */
public class AdvanceOrder extends Order {
	private String countryFromName;
	private String countryToName;
	private String numArmies;
	public GamePlayer gamePlayerObject;
	public HashMap<String, GamePlayer> playerObjectList;

	LogEntryBuffer log = new LogEntryBuffer();
	LogWriter logWriter = new LogWriter(log);
/**
 * 
 * @param countryFromName : country from name
 * @param countryToName : country to name
 * @param numArmies : number of armies
 */
	public AdvanceOrder(String countryFromName, String countryToName, String numArmies) {
		this.countryFromName = countryFromName;
		this.countryToName = countryToName;
		this.numArmies = numArmies;
	}

	/**
	 * execute method is used to execute the game
	 */
	public void execute() 
	{ //need to modify this
		int attackerArmies = getAttackerArmy(this.gamePlayerObject, this.countryFromName);
		int defenderArmies = getDefenderArmy(this.countryToName);
		int attackerArmySum = 0;
		int defenderArmySum = 0;
		if(this.countryToName.equals("")) {
			if(this.countryFromName.equals("advance")) {
				log.info("advanceOrderExecution", gamePlayerObject.getPlayerName(),
						"No country is eligible for attack", "not executed");
				System.out.println(this.gamePlayerObject.getPlayerName() + " No country is eligible for attack");			
			}else {
				log.info("advanceOrderExecution", gamePlayerObject.getPlayerName(),
						"No country is eligible for transfer", "not executed");
				System.out.println(this.gamePlayerObject.getPlayerName() + " No country is eligible for transfer");
			}
		}
		else 
		{
			if(validateFromCountryName(this.gamePlayerObject, this.countryFromName, this.numArmies)) 
			{
				if (isAttack(this.gamePlayerObject, this.countryToName) ) 
				{ // attack
					int attackerCanKill = attackerCalc(this.numArmies);
					int defenderCanKill = defenderCalc(defenderArmies + "");
					attackerArmies = attackerArmies - Integer.parseInt(this.numArmies);
					attackerArmySum = 0;
					defenderArmySum = 0;
					// 1 on 1 Attack
					for(int i=0;i<Integer.parseInt(numArmies);i++) {
						attackerArmySum+=attackerCalc("1");
						defenderArmySum+=defenderCalc("1");
					}
					if (defenderArmies <= attackerCanKill) { // conquer
						defenderArmies = defenderArmies == 0 ? Integer.parseInt(this.numArmies)
								: (Integer.parseInt(this.numArmies) - defenderCanKill);
						boolean flag = true;
						for (Entry<String, GamePlayer> l_mapEntry : this.playerObjectList.entrySet()) {
							for (Countries l_countryObject : ((GamePlayer) l_mapEntry.getValue()).getListOfCountries()) {
								if (l_countryObject.getCountryName().equals(this.countryToName)) {
									this.gamePlayerObject.getListOfCountries().add(l_countryObject);
									((GamePlayer) l_mapEntry.getValue()).getListOfCountries().remove(l_countryObject);
									flag = false;
									break; 
								}
							}
							if (!flag)
								break;
						}
						if (flag) 
						{ // neutral country
							for (Countries l_countryObject : GameEngine.l_neutralCountries) 
							{
								if (l_countryObject.getCountryName().equals(this.countryToName)) 
								{
									this.gamePlayerObject.getListOfCountries().add(l_countryObject);
									GameEngine.l_neutralCountries.remove(l_countryObject);
									break;
								}
							}
						}
						gamePlayerObject.hasConqueredInTurn = true;
					} 
					else 
					{ // attack but dont conquer
						attackerArmies = attackerArmies + Math.max(0, (Integer.parseInt(numArmies) - defenderCanKill));
						defenderArmies = Math.max(0, defenderArmies - attackerCanKill);
					}
				} 
				else 
				{ // move
					attackerArmies = attackerArmies - Integer.parseInt(this.numArmies);
					defenderArmies = defenderArmies + Integer.parseInt(this.numArmies);
				}
				attackerArmySum = attackerArmies;
				defenderArmySum = defenderArmies;
				setAttackerArmy(this.countryFromName, attackerArmies);
				setDefenderArmy(this.countryToName, defenderArmies);
				log.info("advanceOrderExecution", gamePlayerObject.getPlayerName(),
						"advance " + this.countryFromName + " " + this.countryToName + " " + this.numArmies, "executed");
				System.out.println(this.gamePlayerObject.getPlayerName() + " has executed advance order for the country "
						+ this.countryFromName + " to " + this.countryToName + " successfully with the armies "
						+ this.numArmies);
			} else {
				log.info("advanceOrderExecution", gamePlayerObject.getPlayerName(),
						"advance " + this.countryFromName + " " + this.countryToName + " " + this.numArmies, "not executed- as fromCountry is already captured by other player or"
								+ "from countryCountry does not have enough armies as mentioned in the issueOrder");
				System.out.println(this.gamePlayerObject.getPlayerName() + " has not executed advance order for the country "
						+ this.countryFromName + " to " + this.countryToName + " as its already been caputured by the other player in the game or from countryCountry does not have enough armies as mentioned in the issueOrder");
			}
		}
		
	}

	/**
	 * isAttack method is used attack
	 * 
	 * @param l_gamePlayerObject game player object
	 * @param countryToName      country to name
	 * @return false
	 */
	public boolean isAttack(GamePlayer l_gamePlayerObject, String countryToName) {
		for (Countries l_countryObject : l_gamePlayerObject.getListOfCountries()) {
			if (l_countryObject.getCountryName().equals(countryToName)) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * validateFromCountryName will validate if country belongs to the player
	 * @param l_gamePlayerObject has the game player object
	 * @param countryFromName has the country from name
	 * @param numArmies number of armies
	 * @return boolean true if Valid, else false
	 */
	public boolean validateFromCountryName(GamePlayer l_gamePlayerObject, String countryFromName, String numArmies) {
		for (Countries l_countryObject : l_gamePlayerObject.getListOfCountries()) {
			if (l_countryObject.getCountryName().equals(countryFromName) && (l_countryObject.getArmies() >= Integer.parseInt(numArmies) && Integer.parseInt(numArmies)>0)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * setAttackerArmy is used to set the army for attack
	 * 
	 * @param countryFromName country from name
	 * @param armies          number of armies
	 * 
	 */
	public void setAttackerArmy(String countryFromName, int armies) {
		for (Entry<String, Continent> l_mapEntry : GameEngine.l_masterMap.entrySet()) {
			for (Countries l_countryObject : ((Continent) l_mapEntry.getValue()).getContinentOwnedCountries()) {
				if (l_countryObject.getCountryName().equals(countryFromName)) {
					l_countryObject.setArmies(armies);
					return;
				}
			}
		}
	}

	/**
	 * setDefenderArmy method is used to set the defender army
	 * 
	 * @param p_countryToName country name
	 * @param armies          number of armies
	 */
	public void setDefenderArmy(String p_countryToName, int armies) {
		for (Entry<String, Continent> l_mapEntry : GameEngine.l_masterMap.entrySet()) {
			for (Countries l_countryObject : ((Continent) l_mapEntry.getValue()).getContinentOwnedCountries()) {
				if (l_countryObject.getCountryName().equals(p_countryToName)) {
					l_countryObject.setArmies(armies);
					return;
				}
			}
		}
	}

	/**
	 * getAttackerArmy method is used to get attacker army
	 * 
	 * @param l_gamePlayerObject game player object
	 * @param countryFromName    country from name
	 * @return getArmies
	 * 
	 */
	public int getAttackerArmy(GamePlayer l_gamePlayerObject, String countryFromName) {
		for (Countries countryObject : l_gamePlayerObject.getListOfCountries()) {
			if (countryObject.getCountryName().equals(countryFromName)) {
				return countryObject.getArmies();
			}
		}
		return 0;
	}

	/**
	 * getDefenderArmy method is used to get the defender army
	 * 
	 * @param p_countryToName country to name
	 * @return getArmies
	 * 
	 */
	public int getDefenderArmy(String p_countryToName) {
		for (Entry<String, Continent> l_mapEntry : GameEngine.l_masterMap.entrySet()) {
			for (Countries l_countryObject : ((Continent) l_mapEntry.getValue()).getContinentOwnedCountries()) {
				if (l_countryObject.getCountryName().equals(p_countryToName)) {
					return l_countryObject.getArmies();
				}
			}
		}
		return 0;
	}

	/**
	 * attackerCalc method is used to calculate the attacker value
	 * 
	 * @param value attacker value
	 * @return mathround value
	 */
	public int attackerCalc(String value) {
		int armies = Integer.parseInt(value);
		return (int) Math.round(armies * 0.6);
	}

	/**
	 * defenderCal is used to calculate the defender value
	 * 
	 * @param value defender value
	 * @return mathround value
	 */
	public int defenderCalc(String value) {
		int armies = Integer.parseInt(value);
		return (int) Math.round(armies * 0.7);
	}
	


}
