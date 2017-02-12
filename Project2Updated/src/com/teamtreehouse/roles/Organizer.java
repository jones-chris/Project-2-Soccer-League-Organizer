package com.teamtreehouse.roles;


import com.teamtreehouse.controller.MainMenu;
import com.teamtreehouse.model.Player;
import com.teamtreehouse.model.Players;
import com.teamtreehouse.model.Team;

import java.util.*;


public class Organizer {

    private Scanner scanner;
    private Map<String, String> choices;


    public Organizer() {
        // instantiates Scanner and HashMap objects assigned to scanner and choices fields.
        scanner = new Scanner(System.in);
        choices = new HashMap<>();
        choices.put("CREATE", "create a new team");
        choices.put("ADD", "add a player to a team");
        choices.put("REMOVE", "remove a player from a team");
        choices.put("HEIGHT", "print a Team Height Report for a team");
        choices.put("BALANCE", "print the League Balance Report");
        choices.put("EXIT", "to exit the program");
    }

    public void run() {
        String userChoice;
        do {
            promptForChoice();
            userChoice = scanner.next();
            if (userChoice.toLowerCase().equals("create")) { // create team
                createNewTeam();
            } else if (userChoice.toLowerCase().equals("add")) { // add player
                addPlayerToTeam();
            } else if (userChoice.toLowerCase().equals("remove")) { // remove player
                removePlayerFromTeam();
            } else if (userChoice.toLowerCase().equals("height")) { // team height report
                printTeamHeightReport();
            } else if (userChoice.toLowerCase().equals("balance")) {
                printLeagueBalanceReport();
            } else {
                System.out.println("That was not a menu choice.  Please try again.");
            }
        } while (! userChoice.toLowerCase().equals("exit"));
    }

    private void printLeagueBalanceReport() {
        System.out.println("Team            Experienced     Not Experienced");
        List<Team> teams = MainMenu.league.teams;
        for (int i=0; i < teams.size(); i++) {

            Set<Player> players = teams.get(i).getPlayers();
            //Map<String, Integer> expMap = players.stream().collect(Collectors.groupingBy(Player::isPreviousExperience));
            Map<String, Integer> expMap = new HashMap<>();

            for (Player player : players) {
                String exp = expGroup(player);
                expMap.put(exp, expMap.getOrDefault(exp, 0) + 1);
            }

            System.out.printf("%-16s%-16s%-15d %n", teams.get(i).getTeamName(), expMap.getOrDefault("Experienced", 0),
                    expMap.getOrDefault("Not Experienced", 0));
        }
    }

    /* the expMap variable in the printLeagueBalanceReport cannot use primitive types, so I have to make the boolean values
      strings in the method below. */
    private String expGroup(Player player) {
        if (player.isPreviousExperience()) {
            return "Experienced";
        } else {
            return "Not Experienced";
        }
    }

    private void printTeamHeightReport() {
        printTeams();

        System.out.println("Choose the index of the team you want to run the report for");
        int teamIndex = scanner.nextInt();

        if (! teamExists(teamIndex)) {
            System.out.println("That team index does not exist.");
            return;
        }

        Set<Player> players = MainMenu.league.teams.get(teamIndex).getPlayers();
        Map<String, List<Player>> heightMap = new HashMap<>();

        for (Player player : players) {
            String height = heightGroup(player);
            // get map's current player list.  If map's get method returns null, then exception is caught.
            List<Player> newList;
            try {
                newList = new ArrayList<>(heightMap.get(height));
            } catch (NullPointerException npe) {
                newList = new ArrayList<>();
            }
            newList.add(player);
            Collections.sort(newList);
            heightMap.put(height, newList );
        }

        // 35-40 group
        System.out.println("35-40");
        System.out.println("Last Name       First Name");
        try {
            for (Player player : heightMap.get("35-40")) {
                System.out.printf("%-16s%-16s %n", player.getLastName(), player.getFirstName());
            }
        } catch (NullPointerException npe) {
            System.out.printf("%-16s%-16s %n", "none", "none");
        }

        // 41-46 group
        System.out.printf("%n41-46%n");
        System.out.println("Last Name       First Name");
        try {
            for (Player player : heightMap.get("41-46")) {
                System.out.printf("%-16s%-16s %n", player.getLastName(), player.getFirstName());
            }
        } catch (NullPointerException npe) {
            System.out.printf("%-16s%-16s %n", "none", "none");
        }

        // 47-50 group
        System.out.printf("%n47-50%n");
        System.out.println("Last Name       First Name");
        try {
            for (Player player : heightMap.get("47-50")) {
                System.out.printf("%-16s%-16s %n", player.getLastName(), player.getFirstName());
            }
        } catch (NullPointerException npe) {
            System.out.printf("%-16s%-16s %n%n", "none", "none");
        }
    }

    private String heightGroup(Player player) {
        int height = player.getHeightInInches();
        if (height >= 35 && height <= 40) {
            return "35-40";
        } else if (height >= 41 && height <= 46) {
            return "41-46";
        } else {
            return "47-50";
        }
    }

    private void promptForChoice() {
        System.out.println("Choose an option below: ");
        for (Map.Entry<String, String> entry : choices.entrySet()) {
            System.out.printf("Choose %s to %s %n", entry.getKey(), entry.getValue());
        }
    }

    private void printAllPlayers() {
        List<Player> players = Arrays.asList(Players.load());
        Collections.sort(players);

        System.out.println("Index      Last Name      First Name       Height In Inches        Prev Exp");
        for (int i=0; i < players.size(); i++) {
            System.out.printf("%-11d%-15s%-17s%-24d%-16s %n", i,
                    players.get(i).getLastName(), players.get(i).getFirstName(),
                    players.get(i).getHeightInInches(), players.get(i).isPreviousExperience());
        }
    }

    private void createNewTeam() {
        System.out.println("What is the name of the team (input as a single word)?");
        String teamName = scanner.next();

        System.out.println("What is the coach's name (ex:  Chris Jones should be input as chris.jones or chris_jones)?");
        String coach = scanner.next();

        /* create new array from Players class because the players field in the MainMenu class won't have an accurate
        count of all players once players are added to teams. */
        Player[] players = Players.load();

        // check that the number of teams is less than the number of available players.
        if (MainMenu.league.teams.size() < players.length) {
            Team newTeam = new Team(teamName, coach);
            MainMenu.league.teams.add(newTeam);
            System.out.println("Team created successfully!");
        } else {
            System.out.printf("The maximum number of teams, %d, already exists", players.length);
        }
    }

    private void printTeams() {
        System.out.println("League teams: ");
        System.out.println("Index       Team Name");
        List<Team> teams = MainMenu.league.teams;
        Collections.sort(teams);
        for (int i=0; i < teams.size(); i++) {
            System.out.printf("%-12d%-15s %n", i, teams.get(i).getTeamName());
        }
    }

    private boolean printTeamPlayers(int teamIndex) {
        try {
            Player[] players = MainMenu.league.teams.get(teamIndex).getPlayers().toArray(new Player[0]);  //TODO:  simpler way to do this?
            System.out.println("Index      Last Name      First Name       Height In Inches        Prev Exp");
            for (int i=0; i < players.length; i++) {
                System.out.printf("%-11d%-15s%-17s%-24d%-16s %n", i, players[i].getLastName(), players[i].getFirstName(), players[i].getHeightInInches(),
                        players[i].isPreviousExperience());
            }
        } catch (IndexOutOfBoundsException ioobe) {
            return false;
        }
        return true;
    }

    private void addPlayerToTeam() {
        if (! teamsExist()) {
            System.out.println("No teams currently exist.  Please create a team before adding players");
            return;
        }

        printTeams();

        System.out.println("Choose the index of the team you want to add a player to");
        int teamIndex = scanner.nextInt();

        Team team;
        try {
            team = MainMenu.league.teams.get(teamIndex);
        } catch(IndexOutOfBoundsException ioobe) {
            System.out.println("That team index does not exist");
            return;
        }

        printAllPlayers();

        System.out.println("Type the index of the player you want to add to the team");
        int playerIndex = scanner.nextInt();

        Player player;
        try {
            player = MainMenu.players.get(playerIndex);
        } catch (IndexOutOfBoundsException ioobe) {
            System.out.println("That player index does not exist.");
            return;
        }


        if (team.getPlayers().size() >= Team.MAX_PLAYERS) {
            System.out.printf("This team is full.  %d players are already assigned to the team. %n", Team.MAX_PLAYERS);
        } else {
            if (team.addPlayer(player)) { // addPlayer() method will return true if player is added successfully
                System.out.println("The player was added successfully!");
            } else {
                System.out.println("The player already exists on the team's roster");
            }
        }
    }

    private void removePlayerFromTeam() {
        if (! teamsExist()) {
            System.out.println("No teams currently exist.  Please create a team before removing players");
            return;
        }

        printTeams();

        System.out.println("Choose the index of the team you want to remove a player from");
        int teamIndex = scanner.nextInt();

        if (! teamExists(teamIndex)) {
            System.out.println("That team index does not exist.");
            return;
        }

        printTeamPlayers(teamIndex);

        System.out.println("Choose the index of a player you want to remove"); // if this line runs, then we know the team exists
        int playerIndex = scanner.nextInt();

        // determine if user index is within array bounds
        Team team;
        Player player;
        try {
            team = MainMenu.league.teams.get(teamIndex);
            Player[] players = team.getPlayers().toArray(new Player[0]);
            player = players[playerIndex];
        } catch (IndexOutOfBoundsException ioobe) {
            System.out.println("That player index does not exist");
            return;
        }

        if (team.removePlayer(player)) {
            System.out.println("Player removed successfully!");
        } else {
            System.out.println("There was a problem removing the player.  Contact system admin for help.");
        }
    }

    private boolean teamExists(int teamIndex) {
        try {
            MainMenu.league.teams.get(teamIndex);
        } catch (IndexOutOfBoundsException ioobe) {
            return false;
        }
        return true;
    }

    private boolean teamsExist() {
        if (MainMenu.league.teams.size() == 0) {
            return false;
        }
        return true;
    }
}
