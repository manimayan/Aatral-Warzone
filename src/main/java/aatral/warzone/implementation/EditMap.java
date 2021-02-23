package aatral.warzone.implementation;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;
import org.beanio.builder.DelimitedParserBuilder;
import org.beanio.builder.StreamBuilder;

import aatral.warzone.model.Borders;
import aatral.warzone.model.Continent;
import aatral.warzone.model.Country;
import aatral.warzone.utilities.ContinentMapReader;
import aatral.warzone.utilities.CountryBorderReader;
import aatral.warzone.utilities.CountryMapreader;
import aatral.warzone.utilities.InputProcessor;

/**
 * <h1>EditMap</h1> The Class edits the selected map
 * 
 * @author William Moses
 * * @version 1.0
 * @since 2021-02-23
 */
public class EditMap {

	private ValidateMap validateOb = new ValidateMap();
	InputProcessor inputProcessor = new InputProcessor();

	/**
	 * printBorders method is used to print list of countries and its borders in the
	 * console
	 * @param map 
	 * 
	 * @return
	 */
	public void startEditMap(String p_warZoneMap) {
		boolean l_flag = true;
		while (l_flag) {
			System.out.println("\nEdit Map Format: \neditcontinent -add continentID continentvalue -remove continentID "
					+ "\neditcountry -add countryID continentID -remove countryID "
					+ "\neditneighbor -add countryID neighborcountryID -remove countryID neighborcountryID");
			System.out.println("enter 1 to return");

			Scanner l_ob = new Scanner(System.in);
			String editCommand = l_ob.nextLine().trim();
			if (editCommand.startsWith("editcontinent")) {
				String l_continentString = inputProcessor.getString(editCommand);
				editContinentMap(p_warZoneMap, l_continentString);
			} else if (editCommand.startsWith("editcountry")) {
				String l_countryString = inputProcessor.getString(editCommand);
				editCountryMap(p_warZoneMap, l_countryString);
			} else 	if (editCommand.startsWith("editneighbor")) {
				String l_neighborString = inputProcessor.getString(editCommand);
				editNeighbourMap(p_warZoneMap, l_neighborString);
			} else if (editCommand.startsWith("1")){
				System.out.println("Edit Aborted");
				l_flag=false;
			}
		}
	}

	/**
	 * printBorders method is used to print list of countries and its borders in the
	 * console
	 * @param map 
	 * 
	 * @return
	 */
	public void editContinentMap(String p_warZoneMap, String p_continentCommand) {
		String l_listContinentCommand[] = p_continentCommand.split("-");
		for (String l_editContinentCommand : l_listContinentCommand) {
			if ((l_editContinentCommand).isEmpty()) {
			} else {
				if (l_editContinentCommand.startsWith("add")) {
					List<Continent> l_addContinentList = inputProcessor.getAddContinentInput(l_editContinentCommand);
					addContinent(p_warZoneMap, l_addContinentList);
				} else if (l_editContinentCommand.startsWith("remove")) {
					 List<String> l_deleteContinentList =
					 inputProcessor.getDeleteContinentInput(l_editContinentCommand);
					 removeContinent(p_warZoneMap, l_deleteContinentList);
				} else {
					System.out.println(l_editContinentCommand + " is not a valid command");
				}
			}
		}
	}

	/**
	 * printBorders method is used to print list of countries and its borders in the
	 * console
	 * @param map 
	 * 
	 * @return
	 */
	public void addContinent(String p_warZoneMap, List<Continent> p_addContinentList) {
		List<Continent> vaildContinentList = new ArrayList<>();
		for (Continent l_addContinent : p_addContinentList) {

			if (validateOb.validateContinentID(p_warZoneMap, l_addContinent.getContinentId())
					|| validateOb.validateContinentName(p_warZoneMap, l_addContinent.getContinentName())) {

				System.out.println("The Entered continent " + l_addContinent.getContinentId() + " "
						+ l_addContinent.getContinentName() + " is already present");
			} else {
				vaildContinentList.add(l_addContinent);
			}
		}
		List<Continent> l_sourceContinentList = new ContinentMapReader().readContinentFile(p_warZoneMap);
		l_sourceContinentList.addAll(vaildContinentList);
		writeContinentFile(p_warZoneMap, l_sourceContinentList);
		System.out.println("Successfully Entered...");
		p_addContinentList.clear();
		vaildContinentList.clear();
	}

	/**
	 * printBorders method is used to print list of countries and its borders in the
	 * console
	 * @param map 
	 * 
	 * @return
	 */
	public void removeContinent(String warZoneMap, List<String> deleteContinentList) {

		for (String deleteContinent : deleteContinentList) {
			if (!validateOb.validateContinentID(warZoneMap, deleteContinent)) {
				System.out.println(deleteContinent + " is not available in the map to delete");
			} else {
				List<Continent> continentList = new ContinentMapReader().readContinentFile(warZoneMap);
				for (Iterator<Continent> continentIterator = continentList.iterator(); continentIterator.hasNext();) {
					Continent continentObject = continentIterator.next();
					if (continentObject.getContinentId().equalsIgnoreCase(deleteContinent + "")) {
						List<Country> countryList = new CountryMapreader().readCountryMap(warZoneMap);
						for (Country countryObject : countryList) {
							if (countryObject.getContinentId().equalsIgnoreCase(deleteContinent + "")) {
								removeAllCountry(warZoneMap, Integer.parseInt(countryObject.getCountryId()));
							}
						}
						continentIterator.remove();
					}
				}
				writeContinentFile(warZoneMap, continentList);
				System.out.println("Successfully removed the Continent ID - " + deleteContinent);
			}
		}
	}

	/**
	 * printBorders method is used to print list of countries and its borders in the
	 * console
	 * @param map 
	 * 
	 * @return
	 */
	public void editCountryMap(String warZoneMap, String countryString) {
		String listCountryCommand[] = countryString.split("-");

		for (String editCountryCommand : listCountryCommand) {
			if ((editCountryCommand).isEmpty()) {
			} else {
				if (editCountryCommand.startsWith("add")) {
					List<Country> addCountryList = inputProcessor.getAddCountryInput(editCountryCommand);
					addCountry(warZoneMap, addCountryList);
				} else if (editCountryCommand.startsWith("remove")) {
					List<Country> removeCountryList = inputProcessor.getremoveCountryInput(editCountryCommand);
					removeCountry(warZoneMap, removeCountryList);
				} else {
					System.out.println(editCountryCommand + " is not a valid command");
				}
			}
		}

	}

	/**
	 * printBorders method is used to print list of countries and its borders in the
	 * console
	 * @param map 
	 * 
	 * @return
	 */
	public void addCountry(String warZoneMap, List<Country> addCountryList) {
		List<Country> vaildCountryList = new ArrayList<>();
		for (Country addCountry : addCountryList) {

			if (validateOb.validateCountryID(warZoneMap, addCountry.getCountryId())
					|| !validateOb.validateContinentID(warZoneMap, addCountry.getContinentId())) {

				System.out.println("The Entered country " + addCountry.getCountryId()
				+ " is already present or continentId" + addCountry.getContinentId() + " is not present");
			} else {
				vaildCountryList.add(addCountry);
			}
		}
		List<Country> sourceCountryList = new CountryMapreader().readCountryMap(warZoneMap);
		for (Country country : vaildCountryList) {
			sourceCountryList.add(new Country(country.getCountryId(), "addedCountry", country.getContinentId()));
		}

		writeCountryFile(warZoneMap, sourceCountryList);
		System.out.println("Country File Successfuly updated");
		addCountryList.clear();
		vaildCountryList.clear();
	}

	/**
	 * printBorders method is used to print list of countries and its borders in the
	 * console
	 * @param map 
	 * 
	 * @return
	 */
	public void removeCountry(String warZoneMap, List<Country> removeCountryList) {
		// if (!validateOb.validateCountryID(countryID)) {
		//
		// removeAllCountry(countryID);
		//
		// System.out.println("The Entered country ID is not present, kindly enter an
		// existing ID"
		// + "\nPress\n1. Enter existing ID\n2. Return back");
		// }
	}

	/**
	 * printBorders method is used to print list of countries and its borders in the
	 * console
	 * @param map 
	 * 
	 * @return
	 */
	public void editNeighbourMap(String warZoneMap, String neighborString) {
		String listNeighborCommand[] = neighborString.split("-");

		for (String editNeighborCommand : listNeighborCommand) {
			if ((editNeighborCommand).isEmpty()) {
			} else {
				if (editNeighborCommand.startsWith("add")) {
					inputProcessor.getAddNeighborInput(warZoneMap, editNeighborCommand);
				} else if (editNeighborCommand.startsWith("remove")) {
					inputProcessor.getremoveNeighborInput(warZoneMap, editNeighborCommand);
				} else {
					System.out.println(editNeighborCommand + " is not a valid command");
				}
			}
		}
	}

	/**
	 * printBorders method is used to print list of countries and its borders in the
	 * console
	 * @param map 
	 * 
	 * @return
	 */
	public void addNeighbours(String warZoneMap, String countryId, String neighborCountryID) {

		if (validateOb.validateCountryID(warZoneMap,countryId) && (validateOb.validateCountryID(warZoneMap, neighborCountryID))) {
			List<Borders> bordersList = new CountryBorderReader().mapCountryBorderReader(warZoneMap);
			int count = 0;
			for (Borders borderObject : bordersList) {
				if (borderObject.getCountryId().equalsIgnoreCase(countryId + "")) {
					bordersList.get(bordersList.indexOf(borderObject)).getAdjacentCountries()
					.add(neighborCountryID + "");
					count++;
				} else if (borderObject.getCountryId().equalsIgnoreCase(neighborCountryID + "")) {
					bordersList.get(bordersList.indexOf(borderObject)).getAdjacentCountries().add(countryId + "");
					count++;
				}
				if (count == 2)
					continue;
			}
			writeBordersFile(warZoneMap, bordersList);
			System.out.println("Border File Successfuly updated");

		} else {
			System.out.println("The countryId or Neighboring countryId is invalid");
		}
	}

	/**
	 * printBorders method is used to print list of countries and its borders in the
	 * console
	 * @param map 
	 * 
	 * @return
	 */
	public void removeNeighbours(String warZoneMap) {
		//
		// if (!validateOb.validateCountryID(countryID)) {
		// if (!validateOb.validateContinentID(neighbourCountryID)) {
		// List<Borders> bordersList = new
		// CountryBorderReader().mapCountryBorderReader();
		// int count = 0;
		// for (Borders borderObject : bordersList) {
		// if (borderObject.getCountryId().equalsIgnoreCase(countryID + "")) {
		// bordersList.get(bordersList.indexOf(borderObject)).getAdjacentCountries()
		// .remove(neighbourCountryID + "");
		// count++;
		// } else if (borderObject.getCountryId().equalsIgnoreCase(neighbourCountryID +
		// "")) {
		// bordersList.get(bordersList.indexOf(borderObject)).getAdjacentCountries()
		// .remove(countryID + "");
		// count++;
		// }
		// if (count == 2)
		// break;
		// }
		// System.out.println("Border File Successfuly updated");
		// System.out.println("Your selected option is not valid... Try again");
		// }
		// }
	}

	/**
	 * printBorders method is used to print list of countries and its borders in the
	 * console
	 * @param map 
	 * 
	 * @return
	 */
	public void removeAllCountry(String warZoneMap, int countryID) {
		List<Country> countryList = new CountryMapreader().readCountryMap(warZoneMap);
		List<Borders> bordersList = new CountryBorderReader().mapCountryBorderReader(warZoneMap);

		Iterator<Borders> borderLineObject = bordersList.iterator();

		while (borderLineObject.hasNext()) {
			Borders borObj = borderLineObject.next();
			if (borObj.getCountryId().equalsIgnoreCase(countryID + "")) {
				borderLineObject.remove();
			}

			List<String> adjacentCountries = borObj.getAdjacentCountries();
			Iterator<String> borderInLineObject = adjacentCountries.iterator();
			while (borderInLineObject.hasNext()) {
				String name = borderInLineObject.next();
				if (name.equalsIgnoreCase(countryID + "")) {
					borderInLineObject.remove();
				}
			}
		}

		writeBordersFile(warZoneMap, bordersList);
		System.out.println("Borders File Successfully updated");

		Iterator<Country> countryObject = countryList.iterator();
		{
			while (countryObject.hasNext()) {
				Country couObj = countryObject.next();
				if (couObj.getCountryId().equalsIgnoreCase(countryID + "")) {
					countryObject.remove();
					System.out.println("Country File is Successfully updated");
				}
			}
			writeCountryFile(warZoneMap, countryList);
		}

	}

	/**
	 * printBorders method is used to print list of countries and its borders in the
	 * console
	 * @param map 
	 * 
	 * @return
	 */
	public void writeCountryFile(String warZoneMap, List<Country> updateCountry) {
		String FILE_NAME = "src/main/resources/map/"+warZoneMap+"/"+warZoneMap+"-countries.txt";
		StreamFactory factory = StreamFactory.newInstance();
		StreamBuilder builderCSV = new StreamBuilder("countryWrite").format("delimited")
				.parser(new DelimitedParserBuilder(' ')).addRecord(Country.class);
		factory.define(builderCSV);

		File deleteFile = new File(FILE_NAME);
		deleteFile.delete();

		BeanWriter out = factory.createWriter("countryWrite", new File(FILE_NAME));

		for (Country country : updateCountry) {
			out.write(country);
		}
		out.flush();
		out.close();
	}

	/**
	 * printBorders method is used to print list of countries and its borders in the
	 * console
	 * @param map 
	 * 
	 * @return
	 */
	public void writeContinentFile(String warZoneMap, List<Continent> updateContinent) {
		String FILE_NAME = "src/main/resources/map/"+warZoneMap+"/"+warZoneMap+"-continents.txt";
		StreamFactory factory = StreamFactory.newInstance();
		StreamBuilder builderCSV = new StreamBuilder("continentWrite").format("delimited")
				.parser(new DelimitedParserBuilder(' ')).addRecord(Continent.class);
		factory.define(builderCSV);

		File deleteFile = new File(FILE_NAME);
		deleteFile.delete();

		BeanWriter out = factory.createWriter("continentWrite", new File(FILE_NAME));

		for (Continent continent : updateContinent) {
			out.write(continent);
		}
		out.flush();
		out.close();
	}

	/**
	 * printBorders method is used to print list of countries and its borders in the
	 * console
	 * @param map 
	 * 
	 * @return
	 */
	public void writeBordersFile(String warZoneMap, List<Borders> updateBorder) {
		String FILE_NAME = "src/main/resources/map/"+warZoneMap+"/"+warZoneMap+"-borders1.txt";
		StreamFactory factory = StreamFactory.newInstance();
		StreamBuilder builderCSV = new StreamBuilder("continentWrite").format("delimited")
				.parser(new DelimitedParserBuilder(' ')).addRecord(Borders.class);
		factory.define(builderCSV);

		File deleteFile = new File(FILE_NAME);
		deleteFile.delete();

		BeanWriter out = factory.createWriter("continentWrite", new File(FILE_NAME));

		for (Borders borders : updateBorder) {
			out.write(borders);
		}
		out.flush();
		out.close();
	}

}