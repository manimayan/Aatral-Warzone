package aatral.warzone.gameplay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.beanio.annotation.Record;

import aatral.warzone.adapterPattern.ConquestMapAdapter;
import aatral.warzone.adapterPattern.ConquestMapReader;
import aatral.warzone.adapterPattern.DominationMapReader;
import aatral.warzone.model.Continent;
import aatral.warzone.model.Countries;
import aatral.warzone.observerPattern.LogEntryBuffer;
import aatral.warzone.observerPattern.LogWriter;
import aatral.warzone.save.GameObjectHandler;
import aatral.warzone.save.GameObjects;
import aatral.warzone.statePattern.MasterMapEditor;
import aatral.warzone.statePattern.Phase;
import aatral.warzone.strategyPattern.AggressiveBehavior;
import aatral.warzone.strategyPattern.BenevolentBehavior;
import aatral.warzone.strategyPattern.CheaterBehavior;
import aatral.warzone.strategyPattern.HumanBehavior;
import aatral.warzone.strategyPattern.RandomBehavior;
import aatral.warzone.utilities.InputProcessor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Record
@NoArgsConstructor
/**
 * <h1>GameEngine</h1> The Class is the kernel of warzone game
 * 
 * @author William Moses
 * @version 1.0
 * @since 24-02-2021
 */
public class GameEngine {

	private Scanner l_input = new Scanner(System.in);
	public static List<String> l_playerList = new ArrayList<>();
	public static HashMap<String, GamePlayer> l_playerObjectList = new HashMap<>();
	public static Map<String, Continent> l_masterMap = new HashMap<>();
	public String l_mapName;
	public GamePlayer l_gamePlayerObject =null;
	public static String l_gameIssueOrder = "";
	public boolean l_gamePlayPopulateFlag = false;
	public boolean l_isFirst = true;
	public static List<Countries> l_neutralCountries = new ArrayList<>();
	public int l_maxTurns = 0;
	public boolean l_singlePlayerMode = false;
	public GameObjectHandler objectHandler = new GameObjectHandler();
	
	LogEntryBuffer log = new LogEntryBuffer();
	LogWriter logWriter = new LogWriter(log);

	private Phase gamePhase;
	private DominationMapReader mapAdapter = new ConquestMapAdapter(new ConquestMapReader());
/**
 * setPhase method is used to set the gamePhase
 * @param p_phase gamePhase
 */
	public void setPhase(Phase p_phase) {
		gamePhase = p_phase;
	}
/**
 * start method is used to initiate the game
 * @throws IOException throws input output exception
 */
	public void start() throws IOException  {
		System.out.println("Welome to Warzone");
		boolean proceed = true;

		setPhase(new MasterMapEditor(this));

		// Main running block of game
		while (proceed) {
			// Choose the type of map to be loaded into game
			System.out.println("\nPlease type the variant of map to load into game");
			System.out
					.println("Available Variants : " + "\"" + "conquest" + "\"" + " or " + "\"" + "domination" + "\"");

			Scanner l_type = new Scanner(System.in);
			String l_typeOfMap = l_type.nextLine().toString();
			if (l_typeOfMap.equalsIgnoreCase("conquest") || l_typeOfMap.equalsIgnoreCase("domination")) {

				// showing the available maps
				System.out.println("\nThe below are the available maps of type " + l_typeOfMap + " \n");
				InputProcessor l_ip = new InputProcessor();
				List<String> l_folder = l_ip.getstartupPhase(l_typeOfMap);
				for (String l_folderName : l_folder) {
					System.out.println(l_folderName);
				}

				// type the map name to load
				System.out.println("\nPlease type in the map name to load the map or type " + "\"" + "newmap" + "\""
						+ " to create a Map");
				Scanner map = new Scanner(System.in);
				String l_warZoneMap = map.nextLine().toString();

				if (l_folder.contains(l_warZoneMap)) {
					mapAdapter.loadMap(l_typeOfMap, l_warZoneMap);
					// type the below commands to run map editor
					System.out.println("\nType the below command to edit the loaded map"
							+ "\n showmap \n savemap filename \n editmap filename \n validatemap \n gameplay \n goback");
					Scanner l_input = new Scanner(System.in);
					String mapEditorCommand = l_input.nextLine().trim();

					if (mapEditorCommand.startsWith("showmap")) {
						gamePhase.showMap(l_typeOfMap, l_warZoneMap);
					} else if (mapEditorCommand.startsWith("savemap")) {
						System.out.println("\nPlease type the variant of map to save");
						System.out.println("Type any of the below Variants : " + "\"" + "conquest" + "\"" + " or "
								+ "\"" + "domination" + "\"");
						Scanner type = new Scanner(System.in);
						String typeOfMap = type.nextLine().toString();
						mapAdapter.saveMap(typeOfMap, mapEditorCommand, log);
						proceed = false;
					} else if (mapEditorCommand.startsWith("editmap")) {
						gamePhase.editMap(l_typeOfMap, mapEditorCommand);
					} else if (mapEditorCommand.startsWith("validatemap")) {
						gamePhase.validateMap(l_typeOfMap, l_warZoneMap);
					} else if (mapEditorCommand.startsWith("gameplay")) {
						gamePhase.next();
						gameUserMenu();
					} else if (mapEditorCommand.startsWith("goback")) {
						continue;
					} else {
						log.info("MapEditor", mapEditorCommand, "Invalid Command");
						System.out.println("Invalid command");
						System.out.println("Editor closed");
					}
				} else if (l_warZoneMap.startsWith("newmap")) {
					System.out.println("Enter the below command to save a map\n Format: \n savemap filename");
					Scanner saveMap = new Scanner(System.in);
					String mapSaveCommand = saveMap.nextLine();
					if (mapSaveCommand.startsWith("savemap")) {
						System.out.println("\nPlease type the variant of map to save");
						System.out.println("Type any of the below Variants : " + "\"" + "conquest" + "\"" + " or "
								+ "\"" + "domination" + "\"");
						Scanner types = new Scanner(System.in);
						String typeMap = types.nextLine().toString();
						mapAdapter.saveMap(typeMap, mapSaveCommand, log);
						proceed = false;
					} else {
						log.info("MapEditor", l_warZoneMap, "Invalid Command");
						System.out.println("Invalid command");
						proceed = false;
						System.out.println("Editor closed");
					}
				} else {
					log.info("MapEditor", l_warZoneMap, "No " + l_warZoneMap + " map exists");
					System.out.println("No such map exists");
				}
			} else {
				log.info("MapEditor", " ", "Invalid Map Type " + l_typeOfMap);
				System.out.println("Please type the appropriate map type");
			}
		}
	}

	/**
	 * gameUserMenu method allows the user to give input options and execute based
	 * on user input
	 * @throws IOException 
	 */
	public void gameUserMenu() throws IOException {
		boolean l_gamePlayFlag = true;
		//load game format
		while(l_gamePlayFlag) {
			System.out.println("\nStartup Format : "
					+ "\n 1-Single Player Mode"
					+ "\n 2-Tournament Mode"
					+ "\n 3-Exit Game");
			String l_playOption = l_input.nextLine();
			switch (l_playOption.split(" ")[0]) {
				case "1":
					l_singlePlayerMode = true;
					@SuppressWarnings("resource")
					Scanner inputScanner = new Scanner(System.in);
					System.out.println("Do you want load the objects from previously saved game (yes/no)?");
					@SuppressWarnings("unused")
					String input = inputScanner.next();

					if ("yes".equalsIgnoreCase(input)) {
						System.out.println(objectHandler.listFilesUsingDirectoryStream());
						System.out.println("Choose a file name to load game");
						String saveGameName = inputScanner.next();
						GameObjects gameElements = objectHandler.loadGame(saveGameName);
						l_playerList = gameElements.getL_playerList();
						l_playerObjectList = gameElements.getL_playerObjectList();
						l_masterMap = gameElements.getL_masterMap();
						l_mapName = gameElements.getL_mapName();
						l_gamePlayerObject = gameElements.getL_gamePlayerObject();
						l_gameIssueOrder = gameElements.getL_gameIssueOrder();
						l_gamePlayPopulateFlag = gameElements.l_gamePlayPopulateFlag;
						l_isFirst = gameElements.l_isFirst;
						l_neutralCountries = gameElements.getL_neutralCountries();
						l_maxTurns = gameElements.getMaxTurns();
						startGame();
					}else {
						l_maxTurns=0;
						l_playerObjectList = new HashMap<String, GamePlayer>();
						l_playerList = new ArrayList<String>();
						l_neutralCountries = new ArrayList<Countries>();
						//Choose the type of map to be loaded into game
						System.out.println("\nPlease type the variant of map to load into game");
						System.out.println("Available Variants : "+ "\""+"conquest"+"\""+" or "+"\""+"domination"+"\"");
					
						Scanner l_type = new Scanner(System.in);
						String l_typeOfMap = l_type.nextLine().toString();
						if(l_typeOfMap.equalsIgnoreCase("conquest") || l_typeOfMap.equalsIgnoreCase("domination")) {
							
						// showing the available maps
						System.out.println("\nThe below are the available maps of type "+l_typeOfMap+" \n");
						InputProcessor l_ip = new InputProcessor();
						List<String> l_folder = l_ip.getstartupPhase(l_typeOfMap);
						for (String l_folderName : l_folder) {
							System.out.println(l_folderName);
						}
						while(l_gamePlayFlag) {
							System.out.println("\nLoadMap Format : "
									+ "\n loadmap fileName");
							l_playOption = l_input.nextLine();
							if (l_playOption.startsWith("loadmap")) {
								String l_warZoneMaps = l_playOption.split(" ")[1];
								if (l_folder.contains(l_warZoneMaps)) {
									log.info("MapEditor", "\"loadmap " + l_warZoneMaps + "\"", l_warZoneMaps + " map loaded");
//									l_gamePlayerObject = new GamePlayer();
									l_masterMap = gamePhase.loadMap(l_typeOfMap, l_warZoneMaps);
									l_gamePlayFlag=false;
								} else {
									log.info("MapEditor", "\"loadmap " + l_warZoneMaps + "\"",
											"No " + l_warZoneMaps + " map exists");
									System.out.println("No such map exists, Please create a new one");
								}
							} else {
								log.info("MapEditor", l_playOption, "Invalid Command");
								System.out.println("Invalid command");
							}
						}
						l_gamePlayFlag=true;
						while(l_gamePlayFlag) {
							System.out.println("\nGameplayer Format : "
									+ "\n gameplayer -add playerName -remove playerName"
									+ "\n showmap"
									+ "\n startgame"
									+ "\n goback");
							l_playOption = l_input.nextLine();
							switch (l_playOption.split(" ")[0]) {
								case "gameplayer":
									boolean l_flag = true;
									String l_playerOption[] = l_playOption.substring(11).split("-");
									ArrayList<String> l_playerObListTempAdd = new ArrayList<>();
									HashMap<String, GamePlayer> l_playerObTempAdd = new HashMap<>();
									List<String> l_playerObListTempRem = new ArrayList<>();
									String l_playerName = "";
									for (String l_option : l_playerOption) {
										if (l_option.isEmpty())
											continue;
										switch (l_option.split(" ")[0]) {
										case "add":
											l_playerName = l_option.substring(3).trim();
											l_playerName = l_playerName.trim();
											if (l_playerName.isEmpty())
												continue;
											gamePhase.addGamePlayer(l_playerName, l_playerObListTempAdd, l_playerList);	
											break;
										case "remove":
											l_playerName = l_option.substring(6).trim();
											l_playerName = l_playerName.trim();
											gamePhase.removeGamePlayer(l_flag, l_playerName, l_playerObListTempRem, l_playerList);
											break;
										default:
											log.info("GamePlay",l_playOption, "invalid option command");	
											System.out.println("Different input has been read...Try again");
											l_flag = false;
											break;
										}
										log.info("GamePlay",l_playOption, "gameplayer command");	
									}
									System.out.println();
									if(l_playerObListTempAdd.size()>0) {
										System.out.println("GamePlayer's Behavior :"
												+ "\n 1-Human"
												+ "\n 2-Aggressive"
												+ "\n 3-Benevolent"
												+ "\n 4-Random"
												+ "\n 5-Cheater");
										for(String playerName : l_playerObListTempAdd) {
											l_playerObTempAdd.put(playerName, new GamePlayer(playerName, new ArrayList<Countries>(), 0));
											while(l_flag) {
												System.out.println("Select anyone behavior for the player "+playerName);
												l_playOption = l_input.nextLine();
												switch (l_playOption) {
													case "1":
														l_playerObTempAdd.get(playerName).setStrategy(new HumanBehavior(l_playerObTempAdd.get(playerName)));
														l_flag = false;
														break;
													case "2":
														l_playerObTempAdd.get(playerName).setStrategy(new AggressiveBehavior(l_playerObTempAdd.get(playerName)));
														l_flag = false;
														break;
													case "3":
														l_playerObTempAdd.get(playerName).setStrategy(new BenevolentBehavior(l_playerObTempAdd.get(playerName)));
														l_flag = false;
														break;
													case "4":
														l_playerObTempAdd.get(playerName).setStrategy(new RandomBehavior(l_playerObTempAdd.get(playerName)));
														l_flag = false;
														break;
													case "5":
														l_playerObTempAdd.get(playerName).setStrategy(new CheaterBehavior(l_playerObTempAdd.get(playerName)));
														l_flag = false;
														break;
													default:
														log.info("GamePlay",l_playOption, "invalid command");	
														System.out.println("You have entered wrong input...Try again");
														break;
												}
											}				
											l_flag = true;
										}			
									}
									while (l_flag) { 
										System.out.println("Give assigncountries to assign it! or 'cancel' to ignore" + "\nFormat : "
												+ "\n assigncountries" + "\n cancel");
										l_playOption = l_input.nextLine();
										switch (l_playOption) {
										case "assigncountries":
											l_isFirst=true;
											l_playerObjectList.putAll(l_playerObTempAdd);
											l_playerList.addAll(l_playerObListTempAdd);
											l_playerList.removeAll(l_playerObListTempRem);
											for(String removeName : l_playerObListTempRem) {
												l_playerObjectList.remove(removeName);
											}
											l_playerObjectList = gamePhase.assignCountries(l_playerObjectList, l_playerList);
											l_gamePlayPopulateFlag = true;
											l_flag = false;
											log.info("GamePlay",l_playOption, "command executed");	
											break;
										case "cancel":
											log.info("GamePlay",l_playOption, "command aborted");	
											System.out.println("Player's modification are aborted!");
											l_flag = false;
											break;
										default:
											log.info("GamePlay",l_playOption, "invalid command");	
											System.out.println("You have entered wrong input...Try again");
											break;
										}
									}
									break;
								case "showmap":
									gamePhase.gamePlayShowMap();
									break;
								case "startgame":
									System.out.println("Game Play has started\n\n");
									log.info("GamePlay",l_playOption, "game started");	
									startGame();
									break;
								case "goback":
									l_gamePlayFlag=false;
									break;
								default:
									log.info("GamePlay",l_playOption, "Input command mismatching");	
									System.out.println("Input format is not matching... Try again/nCheck again/n");
									break;
							}
						}
						l_gamePlayFlag=true;
						} else {
							log.info("MapEditor", " ", "Invalid Map Type " + l_typeOfMap);
							System.out.println("Please type the appropriate map type");
						}
					}
					break;
				case "2":
					l_singlePlayerMode = false;
					boolean l_flag = true;
					tournamentMode();
					break;
				case "3":
					System.exit(0);
					break;
				default:
					break;
			}
		}
	}
	/**
	 * tournamentMode method is used to execute the flow of tournament mode
	 */
	public void tournamentMode() {
		boolean modeFlag = true;
		//Choose the type of map to be loaded into game
		System.out.println("\nPlease type the variant of map to load into game");
		System.out.println("Available Variants : "+ "\""+"conquest"+"\""+" or "+"\""+"domination"+"\"");
	
		Scanner l_type = new Scanner(System.in);
		String l_typeOfMap = l_type.nextLine().toString();
		if(l_typeOfMap.equalsIgnoreCase("conquest") || l_typeOfMap.equalsIgnoreCase("domination")) {
				
			// showing the available maps
			System.out.println("\nThe below are the available maps of type "+l_typeOfMap+" \n");
			InputProcessor l_ip = new InputProcessor();
			List<String> l_folder = l_ip.getstartupPhase(l_typeOfMap);
			for (String l_folderName : l_folder) {
				System.out.println(l_folderName);
			}
			String l_inputLine;
			l_inputLine = validateTournamentInput(l_typeOfMap);
			String MapFileNames[]=l_inputLine.split("-")[1].split(" ")[1].split(",");
			String PlayerStrategies[]=l_inputLine.split("-")[2].split(" ")[1].split(",");
			int numberOfGames = Integer.parseInt(l_inputLine.split("-")[3].split(" ")[1]);
			int maxNoOfTurns = Integer.parseInt(l_inputLine.split("-")[4].split(" ")[1]);
			String tournamentModeResult[][]=new String[MapFileNames.length+1][numberOfGames+1];
			int index=0;
			for(String mapName : MapFileNames) {
				tournamentModeResult[++index][0] = mapName;
			}
			tournamentModeResult[0][0]="WarZone Maps";
			for(int i=0;i<numberOfGames;i++) {
				tournamentModeResult[0][i+1]="Game-"+(i+1);
			}
			l_maxTurns = maxNoOfTurns;
			for(int i=0;i<MapFileNames.length;i++) {
				log.info("MapEditor", "\"loadmap " + MapFileNames[i] + "\"", MapFileNames[i] + " map loaded");
				l_masterMap = gamePhase.loadMap("domination", MapFileNames[i].trim());
				System.out.println("Map name "+MapFileNames[i]);
				for(int j=0;j<numberOfGames;j++) {
					setPlayers(PlayerStrategies);
					l_playerObjectList = gamePhase.assignCountries(l_playerObjectList, l_playerList);
					startGame();
					String winnerName = winnerPlayer();
					tournamentModeResult[i+1][j+1]=winnerName;
				}
			}
			System.out.println("\nTornament Mode - Result\n");
			String printingFormat ="";
			for(int i=0;i<numberOfGames+1;i++) {
				printingFormat+="----------------------";
			}
			System.out.println(printingFormat);
			for(int j=0;j<tournamentModeResult[0].length;j++) {
				System.out.printf("%-20s", tournamentModeResult[0][j]);
			}
			System.out.println("\n"+printingFormat);
			for(int i=1;i<tournamentModeResult.length;i++) {
				for(int j=0;j<tournamentModeResult[i].length;j++) {
					System.out.printf("%-20s", tournamentModeResult[i][j]);
				}System.out.println("\n");
			}
		} else {
			log.info("MapEditor", " ", "Invalid Map Type " + l_typeOfMap);
			System.out.println("Please type the appropriate map type");
		}
	}
	
	/**
	 * setPlayers method is used to set the players for the current instance
	 * @param PlayerStrategies list of array containing the playerNames of behavior
	 */
	public void setPlayers(String[] PlayerStrategies) {
		l_playerObjectList = new HashMap<String, GamePlayer>();
		l_playerList = new ArrayList<String>();
		for(String playerStrategy : PlayerStrategies) {
			l_playerList.add(playerStrategy);
			switch(playerStrategy) {
			case "aggressive":
				l_playerObjectList.put(playerStrategy, new GamePlayer(playerStrategy, new ArrayList<Countries>(), 0));
				l_playerObjectList.get(playerStrategy).setStrategy(new AggressiveBehavior(l_playerObjectList.get(playerStrategy)));
				break;
			case "benevolent":
				l_playerObjectList.put(playerStrategy, new GamePlayer(playerStrategy, new ArrayList<Countries>(), 0));
				l_playerObjectList.get(playerStrategy).setStrategy(new BenevolentBehavior(l_playerObjectList.get(playerStrategy)));
				break;
			case "random":
				l_playerObjectList.put(playerStrategy, new GamePlayer(playerStrategy, new ArrayList<Countries>(), 0));
				l_playerObjectList.get(playerStrategy).setStrategy(new RandomBehavior(l_playerObjectList.get(playerStrategy)));
				break;
			case "cheater":
				l_playerObjectList.put(playerStrategy, new GamePlayer(playerStrategy, new ArrayList<Countries>(), 0));
				l_playerObjectList.get(playerStrategy).setStrategy(new CheaterBehavior(l_playerObjectList.get(playerStrategy)));
				break;
			}
		}
	}
	/**
	 * validateTournamentInput method is to validate the tournament input
	 * @param l_typeOfMap map format
	 * @return perfect input type for tournament mode
	 */
	public String validateTournamentInput(String l_typeOfMap) {
		boolean flag = true;
		String input="";
		List<String> strategies = new ArrayList<>();
		strategies.add("aggressive");strategies.add("benevolent");strategies.add("cheater");strategies.add("random");
		InputProcessor l_ip = new InputProcessor();
		List<String> l_folder = l_ip.getstartupPhase(l_typeOfMap);
		while(flag) {
			flag=false;
			System.out.println("\nTournament Mode Format :"
					+ "\n tournament -M listofmapfiles -P listofplayerstrategies -G numberofgames -D maxnumberofturns");
			input = l_input.nextLine();
			if(input.split("-").length==5) {
				if(input.split("-")[1].split(" ").length!=2 || input.split("-")[2].split(" ").length!=2 || input.split("-")[3].split(" ").length!=2 || input.split("-")[4].split(" ").length!=2) {
					log.info("GamePlay",input, "Input command mismatching");	
					System.out.println("Input format is invalid... Try again!");
					flag=true;
					continue;
				}else {
					String MapFileNames[]=input.split("-")[1].split(" ")[1].split(",");
					String PlayerStrategies[]=input.split("-")[2].split(" ")[1].split(",");
					int numberOfGames = Integer.parseInt(input.split("-")[3].split(" ")[1]);
					int maxNoOfTurns = Integer.parseInt(input.split("-")[4].split(" ")[1]);
					if(!input.startsWith("tournament")) {
						log.info("GamePlay",input, "Input command mismatching");	
						System.out.println("Input format is invalid... Try again!");
						flag=true;
					}
					for(String mapName : MapFileNames) {
						if(!l_folder.contains(mapName)) {
							log.info("GamePlay",input, "Map is not found");	
							System.out.println("Map name is not found... Try again!");
							flag=true;
							break;
						}
					}
					for(String strategy : PlayerStrategies) {
						if(!strategies.contains(strategy)) {
							log.info("GamePlay",input, "Player strategy is invalid");	
							System.out.println("Player strategy is invalid... Try again!");
							flag=true;
							break;
						}
					}
					if(numberOfGames<=0 || numberOfGames>5) {
						log.info("GamePlay",input, "Number of Games is invalid");	
						System.out.println("Number of Games is invalid... Try again!");
						flag=true;
					}
					if(maxNoOfTurns<=0) {
						log.info("GamePlay",input, "Maximum number of turns invalid");	
						System.out.println("Maximum number of turns invalid... Try again!");
						flag=true;
					}
				}
			}else {
				log.info("GamePlay",input, "Input command mismatching");	
				System.out.println("Input format is invalid... Try again!");
				flag=true;
			}
		}
		return input.substring(11);
	}

	/**
	 * startGame method is used to start the game with user's input choice
	 */
	public void startGame() {
		int currentTurn = 0;
		if (l_playerList.isEmpty()) {
			System.out.println("\nNo player has been created to start the game\n");
		} else {
			boolean l_flag = true, l_innerLoopflag = true;
			do {
				for (Entry<String, GamePlayer> l_gameplayObject : l_playerObjectList.entrySet()) {
					l_gamePlayerObject = (GamePlayer) l_gameplayObject.getValue();
					l_gamePlayerObject.hasConqueredInTurn = false;
					if(l_gamePlayerObject.getListOfCountries().size()==0) {
						continue;
					}
					System.out.println("\n\nAssinging reinforcement for the Player " + l_gameplayObject.getKey());
					if (l_isFirst) {
						gamePhase.assignReinforcements(5);
					} else {
						gamePhase.assignReinforcements(calAssignReinforcements(l_gamePlayerObject));
					}
					log.info("StartUp", "For all players", "Reinforcement Assigned");
					showMapPlayer(l_gamePlayerObject);
				}
				if (l_neutralCountries.size() != 0) {
					System.out.println(
							"---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
					System.out.printf("%-14s%-40s%-12s%-20s%-100s\n", "Country ID", "Country Name", "Armies", "Owner",
							"Bordering Countries");
					System.out.println(
							"---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
					for (Countries l_co : l_neutralCountries) {
						System.out.printf("%-14s%-40s%-12s%-20s%-100s\n", "\n" + l_co.getCountryId(),
								l_co.getCountryName(), l_co.getArmies(), "Neutral", countriesUnderPlayerAsString(l_co));
					}
					System.out.println();
				}
				gamePhase.next();
				l_isFirst = false;

				l_gameIssueOrder = "deploy";
				boolean flag = true;
				do {
					flag = false;
					for (Entry<String, GamePlayer> l_gameplayObject : l_playerObjectList.entrySet()) {
						l_gamePlayerObject = (GamePlayer) l_gameplayObject.getValue();
						if(l_gamePlayerObject.getListOfCountries().size()>0) {
							l_gamePlayerObject.setCommit(false);
							gamePhase.issueOrders();
							if (!flag && l_gamePlayerObject.getReinforcementArmies() > 0) {
								flag = true;
							}
						}else {
							l_gamePlayerObject.setReinforcementArmies(0);
							l_gamePlayerObject.setCommit(false);
						}
					}
				} while (flag);

				l_gameIssueOrder = "advance";
				flag = true;
				do {
					flag = false;
					for (Entry<String, GamePlayer> l_gameplayObject : l_playerObjectList.entrySet()) {
						if (!((GamePlayer) l_gameplayObject.getValue()).getCommit()) {
							l_gamePlayerObject = (GamePlayer) l_gameplayObject.getValue();
							if(l_gamePlayerObject.getListOfCountries().size()>0) {
								gamePhase.issueOrders();
							}else {
								l_gamePlayerObject.setCommit(true);
							}
						}
						if (!flag && !((GamePlayer) l_gameplayObject.getValue()).getCommit())
						{
							flag = true;
						}
					}
				} while (flag);

				gamePhase.next();
				gamePhase.executeOrders();
				if(checkIfPlayerHasWon()) {
					l_flag=false;
					gamePhase.next();
					return;
				}
				l_innerLoopflag = true;
				if(l_singlePlayerMode) {
					while (l_innerLoopflag) {
						System.out.println("Do you want save the game (yes/no)?");
						String l_readInput = l_input.nextLine();
						switch (l_readInput) {
						case "no":
							l_innerLoopflag = false;
							gamePhase.next();
							break;
						case "yes":
							GameObjects saveGameObjects = new GameObjects();
							saveGameObjects.setL_playerList(l_playerList);
							saveGameObjects.setL_playerObjectList(l_playerObjectList);
							saveGameObjects.setL_masterMap(l_masterMap);
							saveGameObjects.setL_mapName(l_mapName);
							saveGameObjects.setL_gamePlayerObject(l_gamePlayerObject);
							saveGameObjects.setL_gameIssueOrder(l_gameIssueOrder);
							saveGameObjects.setL_gamePlayPopulateFlag(l_gamePlayPopulateFlag);
							saveGameObjects.setL_isFirst(l_isFirst);
							saveGameObjects.setL_neutralCountries(l_neutralCountries);
							saveGameObjects.setMaxTurns(l_maxTurns);
							System.out.println("Enter save file name");
							String saveGameName = l_input.nextLine();
							objectHandler.saveGame(saveGameObjects, saveGameName);
							l_innerLoopflag = false;
							l_flag = false;
							gamePhase.next();
							break;
						default:
							System.out.println("Input is mismatching...Kindly Try again...");
							break;
						}
					}
				}else {
					l_innerLoopflag = false;
					gamePhase.next();
				}
				currentTurn++;
				if(currentTurn>l_maxTurns && l_maxTurns!=0) {
					return;
				}
			} while (l_flag);
		}
	}
	
	/**
	 * playerHasCountries method is to find whether the player has every countries or not
	 */
	public void playerHasCountries() {
		List<String> removePlayer = new ArrayList<>();
		for(Entry<String, GamePlayer> l_gameplayObject : l_playerObjectList.entrySet()) {
			if(l_gameplayObject.getValue().getListOfCountries().size()==0) {
				removePlayer.add(l_gameplayObject.getKey());
			}
		}
		if(removePlayer.size()>0) {
			for(String playerName : removePlayer) {
				System.out.println("Player "+playerName+" has lost all its countries, so it can't play the further rounds");
				log.info("StartUp", "Player - "+playerName, "Lost all countries. So, removed from gamePlay");
				l_playerObjectList.remove(playerName);
				l_playerList.remove(playerName);
			}
		}
	}
	

	/**
	 * totalCountries method is used to calculate the total countries
	 * 
	 * @return total country value
	 */
	public int totalCountries() {
		int count = 0;
		for (Entry<String, Continent> mapEntry : l_masterMap.entrySet()) {
			List<Countries> set = ((Continent) mapEntry.getValue()).getContinentOwnedCountries();
			count += set.size();
		}
		return count;
	}

	/**
	 * listOfCountries method is used to calculate the list of temporary countries
	 * 
	 * @return temporary country value
	 */
	public List<Countries> listOfCountries() {
		List<Countries> tempCountries = new ArrayList<>();
		for (Entry<String, Continent> mapEntry : l_masterMap.entrySet()) {
			tempCountries.addAll(((Continent) mapEntry.getValue()).getContinentOwnedCountries());
		}
		return tempCountries;
	}

	/**
	 * allCountriesAssigned method is used to assign the all countries
	 * 
	 * @param p_isCountryAssigned country assigned
	 * @return false
	 */
	public boolean allCountriesAssigned(boolean p_isCountryAssigned[]) {
		for (int i = 0; i < p_isCountryAssigned.length; i++) {
			if (!p_isCountryAssigned[i])
				return false;
		}
		return true;
	}

	/**
	 * getCountryID method is used to get the country ID
	 * 
	 * @param p_totalCountryNumber total country number
	 * @param p_isCountryAssigned  country assigned
	 * @return country ID
	 */
	public int getCountryID(int p_totalCountryNumber, boolean[] p_isCountryAssigned) {
		int l_countryID = new Random().nextInt(p_totalCountryNumber);
		while (p_isCountryAssigned[l_countryID]) {
			l_countryID = new Random().nextInt(p_totalCountryNumber);
		}
		return l_countryID;
	}

	/**
	 * checkIfPlayerHasAnyCards method is used to check whether the player has any
	 * card or not
	 * 
	 * @param playerObj player object
	 * @return It returns a value
	 */
	public String checkIfPlayerHasAnyCards(GamePlayer playerObj) {
		String value = "";
		for (Map.Entry playerCard : playerObj.getSpecialCards().entrySet()) {
			if ((int) playerCard.getValue() != 0) {
				switch ((String) playerCard.getKey()) {
				case "bomb":
					value += "\n bomb countryID";
					break;
				case "blockade":
					value += "\n blockade countryID";
					break;
				case "airlift":
					value += "\n airlift sourcecountryID targetcountryID numarmies";
					break;
				case "negotiate":
					value += "\n negotiate playerID";
					break;
				}
			}
		}
		return value;
	}

	/**
	 * getContinentName method is used to get the continent name using continent id
	 * 
	 * @param p_continentMapKeySet Set of continent map key.
	 * @param p_continentID        Continent id.
	 * @return continent name.
	 */
	public String getContinentName(Set<String> p_continentMapKeySet, String p_continentID) {
		String l_continentName = "";
		for (String continentKey : p_continentMapKeySet) {
			if (continentKey.split("_")[0].equalsIgnoreCase(p_continentID)) {
				l_continentName = continentKey.substring(continentKey.indexOf("_") + 1);
				break;
			}
		}
		return l_continentName;
	}

	/**
	 * calAssignReinforcements is used to assign the reinforcements
	 * 
	 * @param p_player game player
	 * @return reinforcement count
	 */
	public int calAssignReinforcements(GamePlayer p_player) {
		List<Countries> l_listOfCountries = p_player.getListOfCountries();
		int l_reinforcementCount = Math.round(l_listOfCountries.size() / 3);
		for (Entry<String, Continent> continent : l_masterMap.entrySet()) {
			List<Countries> l_CountriesUnderContinent = ((Continent) continent.getValue()).getContinentOwnedCountries();
			boolean exists_all = false;
			if (!l_CountriesUnderContinent.isEmpty()) {
				exists_all = true;
				for (Countries c : l_CountriesUnderContinent) {
					if (!l_listOfCountries.contains(c)) {
						exists_all = false;
						break;
					}
				}
			}
			if (exists_all) {
				l_reinforcementCount += Integer.parseInt(((Continent) continent.getValue()).getContinentValue());
			}
		}
		return l_reinforcementCount < 3 ? 3 : l_reinforcementCount;
	}

	/**
	 * countriesUnderPlayerAsString method is used to display the countries assigned
	 * under a player
	 * 
	 * @param p_countryObject has the country object
	 * @return bordering countries
	 */
	public String countriesUnderPlayerAsString(Countries p_countryObject) {
		String borderingCountries = "";
		if (p_countryObject.getCountryOwnedBorders().size() > 0) {
			for (Countries country : listOfCountries()) {
				if (p_countryObject.getCountryOwnedBorders().contains(country.getCountryId())) {
					for (String countryID : p_countryObject.getCountryOwnedBorders()) {
						if (country.getCountryId().equals(countryID)) {
							borderingCountries += ", " + country.getCountryName();
						}
					}
				}
			}
		} else {
			borderingCountries = ", No Bordering Countries exist";
		}
		return borderingCountries.equals("")?"No Bordering Countries exist":borderingCountries.substring(2);
	}
	
	/**
	 * winnerPlayer method returns the playerName who won
	 * 
	 * @return string winner playerName
	 */
	public String winnerPlayer() {
		for (Map.Entry mapPlayerObj : l_playerObjectList.entrySet()) {
			if (!mapPlayerObj.getKey().equals(l_gamePlayerObject.getPlayerName())
					&& ((GamePlayer) mapPlayerObj.getValue()).getListOfCountries().size() != 0) {
				return "draw";
			}
		}
		return l_gamePlayerObject.getPlayerName();
	}

	/**
	 * checkIfPlayerHasWon method checks if player has won the game
	 * 
	 * @return boolean true if won, else false
	 */
	public boolean checkIfPlayerHasWon() {
		for (Map.Entry mapPlayerObj : l_playerObjectList.entrySet()) {
			if (!mapPlayerObj.getKey().equals(l_gamePlayerObject.getPlayerName())
					&& ((GamePlayer) mapPlayerObj.getValue()).getListOfCountries().size() != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * showMapPlayer method is used to show the map corresponds to game player name
	 * 
	 * @param p_gamePlayer object for gameplayer class.
	 */
	public void showMapPlayer(GamePlayer p_gamePlayer) {
		System.out.println(
				"---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.printf("%-14s%-40s%-12s%-20s%-100s\n", "Country ID", "Country Name", "Armies", "Owner",
				"Bordering Countries");
		System.out.println(
				"---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		for (Countries l_co : p_gamePlayer.getListOfCountries()) {
			System.out.printf("%-14s%-40s%-12s%-20s%-100s\n", "\n" + l_co.getCountryId(), l_co.getCountryName(),
					l_co.getArmies(), p_gamePlayer.getPlayerName(), countriesUnderPlayerAsString(l_co));
		}
		System.out.println();
	}

}
