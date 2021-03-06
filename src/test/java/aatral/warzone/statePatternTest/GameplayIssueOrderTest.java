package aatral.warzone.statePatternTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import aatral.warzone.gameplay.GameEngine;
import aatral.warzone.gameplay.GamePlayer;
import aatral.warzone.statePattern.GamePlayIssueOrder;

/**
 * GameplayIssueOrderTest This test class tests the state pattern 
 * for game play issue order
 * @author vignesh senthilkumar
 * @since 19.03.2021
 */

public class GameplayIssueOrderTest {
	@Before
	public void constructor()
	{
		try {

			File file = new File("src/main/resources/source/canada/canada-countries.txt");
			//List<>
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				sc.nextLine();

			}

			sc.close();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}


/**
 * validateDeployInputTest method is used to test deploy input values
 */
	@Test
	public void validateDeployInputTest()
	{
		GameEngine obj= new GameEngine();
		GamePlayIssueOrder gp = new  GamePlayIssueOrder(obj);
		String actual = gp.validateDeployInput("deploy 24 4");
		//System.out.println(" 24 4, 23 3");
		String expected = " 24 4";
		assertTrue(actual.equals(expected));
	}
	
/**
 * validateDeployInputTestNegative method is used to test the
 * negative deploy input values
 */
	public void validateDeployInputTestNegative()
	{
		GameEngine obj= new GameEngine();
		GamePlayIssueOrder gp = new GamePlayIssueOrder(obj);
		String actual = gp.validateDeployInput("deploy 24 4");
		//System.out.println(" 24 4, 23 3");
		String expected = " 24 4";
		assertTrue(actual.equals(expected));
	}
	
/**
 * calculateInputArmiesTest method id used to test to calculate input armies
 */
	@Test
	public void calculateInputArmiesTest()
	{
		GameEngine obj= new GameEngine();
		GamePlayIssueOrder gp = new GamePlayIssueOrder(obj);
		int actual = gp.calculateInputArmies("deploy 23");
		int expected=23;
		assertEquals(expected,actual);
	}

	/**
	 * validateInputArmies method is used to test and validate the input armies
	 */
	@Test
	public void validateInputArmies()
	{
		GameEngine obj= new GameEngine();
		GamePlayIssueOrder gp = new GamePlayIssueOrder(obj);
		boolean actual = gp.validateInputArmies(21,24);
		boolean expected = true;
		assertEquals(expected,actual);
	}
	/**
	 * validateInputArmies method is used to test and validate the negative input armies
	 */
	@Test
	public void validateInputArmiesNegative()
	{
		GameEngine obj= new GameEngine();
		GamePlayIssueOrder gp = new GamePlayIssueOrder(obj);
		boolean actual = gp.validateInputArmies(24,21);
		boolean expected = false;
		assertEquals(expected,actual);
	}
	
/**
 * validateCountryValueTest method is used to test the country values matches or not
 */
	@Test
	public void validateCountryValueTest()
	{
		GameEngine obj= new GameEngine();
		GamePlayIssueOrder gp = new GamePlayIssueOrder(obj);
	boolean actual =	gp.validateCountryValue("india");
		assertEquals(false,actual);
	}
	
	/**
	 * validateCountryValueTest method is used to test the negative country values matches or not
	 */
	@Test
	public void validateCountryValueTestNegative()
	{
		GameEngine obj= new GameEngine();
		GamePlayIssueOrder gp = new GamePlayIssueOrder(obj);
	boolean actual =	gp.validateCountryValue("");
		assertEquals(true,actual);
	}
	/*@Test
	public void validateCountryInputTest()
	{
		GameEngine ge=new GameEngine();
		GamePlayIssueOrder gp = new GamePlayIssueOrder(ge);
		GamePlayer gp1 = new GamePlayer();
		String actual=gp.validateCountryInput(" 23 4",gp1);
		String expected="";
	assertNotEquals(actual,expected);
		
	}
*/

}
