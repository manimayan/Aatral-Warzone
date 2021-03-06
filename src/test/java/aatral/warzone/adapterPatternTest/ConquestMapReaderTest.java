package aatral.warzone.adapterPatternTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

import org.junit.Test;

import aatral.warzone.adapterPattern.DominationMapReader;
import aatral.warzone.model.Continent;

/**
 * <h1>ConquestMapReaderTest class is used to test the conquest map</h1>
 * @author Vignesh
 * @since 13.04.2021
 *
 */
public class ConquestMapReaderTest {
	public static int expected=0;
	/**
	 * loadMapTestConquest method is used to test the load map test conquest
	 */
	
	/*public void loadMapTestConquest()
	{
//int expected=0;
		try {

			File file = new File("src/main/resources/conquest/africa/africa-continents.txt");
			/// Aatral-Warzone/src/main/resources/source/canada/canada-continents.txt
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				sc.nextLine();
				expected++;
			}

			sc.close();
		} catch (Exception e) {
			e.getStackTrace();
		}
		DominationMapReader dmr=new DominationMapReader();
		Map<String,Continent> actual=dmr.loadMap("conquest","africa");
		int size=actual.size();
		//System.out.println(expected);
		assertEquals(size,expected);
	}*/
	
	
/**
 * loadMapTestConquestNegative method is used to test the value is negative
 */
	@Test
	public void loadMapTestConquestNegative()
	{
int expected1=0;
		
		DominationMapReader dmr=new DominationMapReader();
		Map<String,Continent> actual=dmr.loadMap("conquest","africa");
		int size=actual.size();
		assertFalse(size==expected1);
	}
	
	
	/**
	 * loadMapTestConquestNegative method is used to test the value is negative
	 */	
	@Test
	public void loadMapTestConquestNegativeMap()
	{
int expected2=0;
		
		DominationMapReader dmr=new DominationMapReader();
		Map<String,Continent> actual=dmr.loadMap("conquest","africa");
		int size=actual.size();
		assertFalse(size==expected2);	}
	
}
