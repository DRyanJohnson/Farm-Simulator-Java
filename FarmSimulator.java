import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Arrays;

/**
 * 
 * Farm Simulator
 * @author Fela Femi Ewetuga, Dakota Johnson, Sophia Kravstov, Kyle Propst
 * 
 * This is a text based simulation game where you buy farm plots,plant, harvest, 
 * keep your crops alive using fences and survivng weather events at random
 * 
 */

public class FarmSimulator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //tracking player/game system
        int gold = 1000;
        int timeLeft = 20;
        int currentDay = 1;
        int currentYear = 1;
        int yearlyEarning = 0;
        //fish system may need to fix complete overhaul!
        int bait = 0;
        
      //cost of time by action of player
        int plantTime = 1;
        int harvestTime = 1;
        int shopTime = 0;
        int fishingTime = 1;
        int fertilizerTime = 1;
        int fencingTime = 1;
        
        //we can use hashmap for crop growth rate 
        //days required to grow
        HashMap<String, Integer> cropGrowthDays = new HashMap<>();
        cropGrowthDays.put("Lettuce", 6);
        cropGrowthDays.put("Radish", 4);
        cropGrowthDays.put("Broccoli", 6);
        cropGrowthDays.put("Potato", 8);
        cropGrowthDays.put("Onions", 6);
        cropGrowthDays.put("Peas", 8);
        cropGrowthDays.put("Tomatoes", 4);
        cropGrowthDays.put("Peppers", 5);
        cropGrowthDays.put("Eggplant", 7);
        cropGrowthDays.put("Cucumbers", 5);
        cropGrowthDays.put("Corn", 6);
        cropGrowthDays.put("Spinach", 4);
        cropGrowthDays.put("Kale", 4);
        cropGrowthDays.put("Carrots", 5);
        
        //seed prices for the shop so every plant has a diffrent cost 
        //the higher the cost of thee seed the more you will get back from harvest
        HashMap<String, Integer> seedPrices = new HashMap<>();
        seedPrices.put("Lettuce", 80);
        seedPrices.put("Radish", 40);
        seedPrices.put("Broccoli", 60);
        seedPrices.put("Potato", 80);
        seedPrices.put("Onions", 80);
        seedPrices.put("Peas", 50);
        seedPrices.put("Tomatoes", 50);
        seedPrices.put("Peppers", 60);
        seedPrices.put("Eggplant", 50);
        seedPrices.put("Cucumbers", 50);
        seedPrices.put("Corn", 60);
        seedPrices.put("Spinach", 40);
        seedPrices.put("Kale", 40);
        seedPrices.put("Carrots", 50);

        //fish types array list for fishing
        ArrayList<Fish>fishList = new ArrayList<>();
            fishList.add(new Fish("Carp", 40, 0.40));
            fishList.add(new Fish("Trout", 60, 0.25));
            fishList.add(new Fish("Bass", 80, 0.10));
            fishList.add(new Fish("Salmon", 100, 0.05));
        
        
        //we need a seasonal map for the seeds
        //keeps track of what season seeds are in
        HashMap<String, java.util.List<String>> seasonalSeeds = new HashMap<>();
        seasonalSeeds.put("Spring", java.util.Arrays.asList("Lettuce","Radish","Broccoli","Onions","Peas"));
        seasonalSeeds.put("Summer", java.util.Arrays.asList("Tomatoes","Peppers","Eggplant","Cucumbers","Corn"));
        seasonalSeeds.put("Fall", java.util.Arrays.asList("Spinach","Kale","Lettuce","Carrots"));
        seasonalSeeds.put("Winter", new java.util.ArrayList<>());

        //Farm plots
        ArrayList<Acre> acres = new ArrayList<>();
        acres.add(new Acre("Empty", 0));

        //we can use a hashmap for inventory
        //leave empty itt will update when you receive items
        HashMap<String, Integer> inventory = new HashMap<>();

        // false so the game keeps looping until condition is true for game over
        boolean gameOver = false;

        //game loop 
        while (!gameOver) {
            String season = getSeason(currentDay);
            List<String> seedList = seasonalSeeds.get(season);

          //this will build our inventory for the shop
            TreeMap<String, Integer> shop = new TreeMap<>();
            shop.put("land plot", 300);
            shop.put("bait", 30);
            shop.put("fence", 100);
            if (!season.equals("Winter")) {
                shop.put("fertilizer", 10);
            }
          //seasonal seeds added to shop inventory
            for (String seed : seedList) {
                shop.put(seed.toLowerCase(), seedPrices.get(seed));
            }

          //shop inventory display
            System.out.println("\nDay" + currentDay + "|Year" + currentYear + "| Season: " + season);
            System.out.println("Gold: " + gold + "|Time Left:" + timeLeft);
            System.out.println("Inventory: " + inventory);
            for (int i = 0; i < acres.size(); i++) {
                Acre acre = acres.get(i);
                System.out.println("  Plot " + (i + 1) + ": " + acre.cropType + " (" + acre.daysGrown + " days)" + (acre.isFenced ? "[Fence]" : "") + (acre.hasFertilizer ? " [Fertilizer]" : ""));
            }
           
          //command prompts
            System.out.print("\nCommand (shop, buy [item], plant [crop], fence[plot], fertilize [plot], sell[fish], harvest, fish, sleep, exit): ");
            String input = scanner.nextLine().trim().toLowerCase();
            //shop command
            if (input.equals("shop")) {
                if (timeLeft >= shopTime) {
                    timeLeft -= shopTime;
                    System.out.println("Shop:");
                    for (String shopItem : shop.keySet()) {
                        System.out.println(" " + capitalize(shopItem) + " - " + shop.get(shopItem) + " gold");
                    }
                } else {
                    System.out.println("Not enough time to shop.");
                }
                    //buy command
            } else if (input.startsWith("buy ")) {
                if (timeLeft >= shopTime) {
                    String buyItem = input.substring(4).trim().toLowerCase();
                    if (shop.containsKey(buyItem)) {
                        int cost = shop.get(buyItem);
                        if (gold >= cost) {
                            gold -= cost;
                            timeLeft -= shopTime;
                            if (buyItem.equals("land plot")) {
                                acres.add(new Acre("Empty", 0));
                                System.out.println("Bought a new plot!");
                            } else {
                                inventory.put(buyItem, inventory.getOrDefault(buyItem, 0) + 1);
                                System.out.println("Bought " + capitalize(buyItem));
                            }
                        } else {
                            System.out.println("Not enough gold.");
                        }
                    } else {
                        System.out.println("Item not in shop.");
                    }
                } else {
                    System.out.println("Not enough time to buy.");
                }
                //fence command
            } else if (input.startsWith("fence ")) {
                try {
                    // 1. Get the plot number from the player (e.g., "fence 2")
                    int plotNumber = Integer.parseInt(input.substring(6).trim());
                    int plotIndex = plotNumber - 1; // Convert to computer counting (0, 1, 2...)

                    // 2. check if this plot actually exists
                    if (plotIndex >= 0 && plotIndex < acres.size()) {
                        Acre selectedAcre = acres.get(plotIndex);
                        int fenceCost = 50; // Price of a fence

                        // 3. Check conditions (Already fenced? Money? Time?)
                        if (selectedAcre.isFenced) {
                            System.out.println("Plot " + plotNumber + " already has a fence.");
                        } else if (gold < fenceCost) {
                            System.out.println("You need " + fenceCost + " gold to build a fence.");
                        } else if (timeLeft < fencingTime) {
                            System.out.println("Not enough time to build a fence.");
                        } else {
                            // 4. Do the work
                            gold -= fenceCost;
                            timeLeft -= fencingTime;
                            selectedAcre.isFenced = true;
                            System.out.println("Success! You built a fence around Plot " + plotNumber);
                        }
                
                    } else {
                        System.out.println("That plot number does not exist.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please type the plot number. Example: 'fence 1'");
                }
            }
            //fertilize command
            else if (input.startsWith("fertilize ")) {
    try {
        int plotNumber = Integer.parseInt(input.substring(10).trim());
        int plotIndex = plotNumber - 1;

        if (plotIndex >= 0 && plotIndex < acres.size()) {
            Acre selectedAcre = acres.get(plotIndex);

            if (selectedAcre.hasFertilizer) {
                System.out.println("Plot " + plotNumber + " already fertilized.");
            } else if (inventory.getOrDefault("fertilizer", 0) <= 0) {
                System.out.println("Fertilizer not present in inventory");
            } else if (timeLeft < fertilizerTime) {
                System.out.println("Not enough time to apply fertilizer");
            } else {
                inventory.put("fertilizer", inventory.get("fertilizer") - 1);
                timeLeft -= fertilizerTime;
                selectedAcre.hasFertilizer = true;
                System.out.println("Fertilizer applied to Plot " + plotNumber);
            }
        } else {
            System.out.println("You do not own this plot number yet");
        }
    } catch (NumberFormatException e) {
        System.out.println("Please enter the plot number");
    }
            //plant command
            } else if (input.startsWith("plant ")) {
                if (timeLeft >= plantTime) {
                    String crop = input.substring(6).trim().toLowerCase();
                    boolean inSeason = seedList.stream().map(String::toLowerCase).anyMatch(c -> c.equals(crop));
                    if (!inSeason) {
                        System.out.println("Can't plant " + crop + " in " + season);
                    } else if (inventory.getOrDefault(crop, 0) > 0) {
                        boolean planted = false;
                        for (Acre acre : acres) {
                            if (acre.cropType.equals("Empty")) {
                                acre.cropType = capitalize(crop);
                                acre.daysGrown = 0;
                                inventory.put(crop, inventory.get(crop) - 1);
                                timeLeft -= plantTime;
                                System.out.println("Planted " + capitalize(crop));
                                planted = true;
                                break;
                            }
                        }
                        if (!planted) {
                            System.out.println("No empty plots.");
                        }
                    } else {
                        System.out.println("No " + crop + " in inventory.");
                    }
                } else {
                    System.out.println("Not enough time to plant.");
                }
                //harvest command
            } else if (input.equals("harvest")) {
                if (timeLeft >= harvestTime) {
                    boolean harvested = false;
                    for (Acre acre : acres) {
                        if (!acre.cropType.equals("Empty")) {
                            String crop = acre.cropType;
                            int grown = acre.daysGrown;
                            int required = cropGrowthDays.getOrDefault(crop, 99);
                            if (grown >= required) {
                                int value = seedPrices.getOrDefault(crop, 5) * 2;
                                gold += value;
                                yearlyEarning += value;
                                System.out.println("Harvested " + crop + " for " + value + " gold.");

                                acre.cropType = "Empty";
                                acre.daysGrown = 0;
                                acre.hasFertilizer= false;

                                harvested = true;
                            }
                        }
                    }
                    if (harvested) {
                        timeLeft -= harvestTime;
                    } else {
                        System.out.println("No crops ready.");
                    }
                } else {
                    System.out.println("Not enough time to harvest.");
                }
                //fishing command
                //fixing in progress...please wait!
           } else if (input.equals("fish")) {
                // 1. Check constraints
                if (timeLeft < fishingTime) {
                    System.out.println("Not enough time to fish.");
                } else if (inventory.getOrDefault("bait", 0) <= 0) {
                    System.out.println("You don't have any bait! Buy some at the shop.");
                } else {
                    // 2. Pay costs
                    inventory.put("bait", inventory.get("bait") -1);
                    timeLeft -= fishingTime;

                    // 3. Pick a random fish from your list
                    // This picks a number between 0 and 3 (Carp, Trout, Bass, Salmon)
                    int randomIndex = (int)(Math.random() * fishList.size());
                    Fish targetFish = fishList.get(randomIndex);

                    System.out.println("You cast your line... A " + targetFish.name + " bites!");

                    // 4. Check if you catch it based on that specific fish's chance
                    if (Math.random() < targetFish.chance) {
                        System.out.println("SUCCESS! You caught a " + targetFish.name + "!");
                        
                        // Add specific fish name to inventory
                        String fishName = targetFish.name.toLowerCase();
                        inventory.put(fishName, inventory.getOrDefault(fishName, 0) + 1);
                    } else {
                        System.out.println("It got away... (It was too strong!)");
                    }
                    System.out.println("Bait left: " + bait);
                }
                // Sell command (allows you to sell fish)
            } else if (input.startsWith("sell ")) {
                String itemToSell = input.substring(5).trim().toLowerCase();
                
               // Check if we have the item in inventory
                if (inventory.getOrDefault(itemToSell, 0) > 0) {
                    
                    //Find the price by looking through our fish list
                    int sellPrice = 0;
                    for (Fish f : fishList) {
                        // We compare the name (ignoring case)
                        if (f.name.toLowerCase().equals(itemToSell)) {
                            sellPrice = f.value;
                            break;
                        }
                    }
                    
                    //If it is a valid fish with a price, sell it
                    if (sellPrice > 0) {
                        inventory.put(itemToSell, inventory.get(itemToSell) - 1);
                        gold += sellPrice;
                        System.out.println("Sold " + capitalize(itemToSell) + " for " + sellPrice + " gold!");
                    } else {
                        System.out.println("The shop doesn't want to buy that (only fish for now).");
                    }
                } else {
                    System.out.println("You don't have any " + itemToSell + ".");
                }
                //sleep command
            } else if (input.equals("sleep")) {
                // --- NIGHT EVENT START ---
                // 30% chance (0.3) that wild animals attack while you sleep
                if (Math.random() < 0.30) {
                    System.out.println("\n*** WARNING: Wild animals acttacked in the night! ***");
                    boolean damageDone = false;
                    
                    for (Acre acre : acres) {
                        // Animals only attack if there is a crop AND NO fence
                        if (!acre.cropType.equals("Empty") && !acre.isFenced) {
                            System.out.println(" - Oh no! Animals ate your " + acre.cropType + "!");
                            acre.cropType = "Empty"; // Crop is destroyed
                            acre.daysGrown = 0;
                            damageDone = true;
                        } 
                        // If there is a fence, the crop is safe
                        else if (!acre.cropType.equals("Empty") && acre.isFenced) {
                             System.out.println(" - Your fence protected your " + acre.cropType + ".");
                        }
                    }
                    
                    if (!damageDone) {
                        System.out.println(" (Luckily, they didn't find any unfenced crops.)");
                    }
                // --- NIGHT EVENT END ---
                } else if (Math.random() < 0.10) { // Weather event with 10% chance of occurring
                    System.out.println("\n*** WARNING: A storm occurred during the night! ***");

                    double intensity = Math.random(); // Determines probability of weather damage destroying an acre
                    boolean damageDone = false;

                    for (Acre acre : acres) {
                        if (!acre.cropType.equals("Empty")) {
                            if (Math.random() < intensity) {
                                acre.cropType = "Empty";
                                acre.daysGrown = 0;
                                damageDone = true;
                                System.out.println(" - Oh no! The storm destroyed your " + acre.cropType + "!");

                            } 
                        }

                    }

                    if (!damageDone) {
                        System.out.println("Luckily, the storm did no damage.");
                    }
                    
                }
                //advance to the next day after sleep
                currentDay++;
                timeLeft = 20;
                System.out.println("You sleep and feel refreshed!");
              //this will grow crops each day automatically
                for (Acre acre : acres) {
                    if (!acre.cropType.equals("Empty")) {
                        if(acre.hasFertilizer){
                            acre.daysGrown += 2;                      
                        }else{
                        acre.daysGrown += 1;
                        }
                    }
                }
              //yearly tax difficulty
                if (currentDay > 112) {
                    int tax = (int)(yearlyEarning * Math.min(5 * currentYear, 30) / 100.0);
                    gold -= tax;
                    System.out.println("Year ended. Paid " + tax + " gold in taxes.");
                    currentYear++;
                    currentDay = 1;
                    yearlyEarning = 0;
                }
                //exit command
            } else if (input.equals("exit")) {
                System.out.println("Thank you for playing.");
                break;

            } else {
                System.out.println("Unknown command.");
            }
                //game over
            if (gold <= 0) {
                System.out.println("Game over; you are broke!");
                gameOver = true;
            }
        }
        scanner.close();
    }

 // This will determine our season based on day
    public static String getSeason(int day) {
        if (day <= 28) return "Spring";
        if (day <= 56) return "Summer";
        if (day <= 84) return "Fall";
        return "Winter";
    }
    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
}

// plots a single farm plot
class Acre {
    String cropType;
    int daysGrown;
    boolean isFenced; // <--- NEW: Tracks if the plot has a fence
    boolean hasFertilizer; 

    Acre(String cropType, int daysGrown) {
        this.cropType = cropType;
        this.daysGrown = daysGrown;
        this.isFenced = false; // Default: No fence when bought
        this.hasFertilizer = false;
    }
}

class Fish{
    String name;
    int value;
    double chance;

    Fish(String name, int value, double chance){
        this.name = name;
        this.value = value;
        this.chance = chance;
    }
}