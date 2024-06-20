package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserInterface {

    private HashMap<String, String> optionDict;

    private List<Option> options;

    public UserInterface() {
        options = new ArrayList<Option>();
        options.add(new Option("-f", "--file", 1));
        options.add(new Option("-h", "--help", 0));
        options.add(new Option("-ne", "--named-entity", 1));
        options.add(new Option("-sf", "--stats-format", 1));

        optionDict = new HashMap<String, String>();
    }

    public Config handleInput(String[] args) {

        for (Integer i = 0; i < args.length; i++) {
            for (Option option : options) {
                if (option.getName().equals(args[i]) || option.getLongName().equals(args[i])) {
                    if (option.getnumValues() == 0) {
                        optionDict.put(option.getName(), null);
                    } else {
                        if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                            optionDict.put(option.getName(), args[i + 1]);
                            i++;
                        } else {
                            System.out.println("Invalid inputs");
                            System.exit(1);
                        }
                    }
                }
            }
        }
        Boolean printHelp = optionDict.containsKey("-h");
        Boolean computeNamedEntities = optionDict.containsKey("-ne");
        Boolean specificFile = optionDict.containsKey("-f");

        String filePath = optionDict.get("-f");
        String heuristicKey = optionDict.get("-ne");     //devuelve null si no lo encuentra
        String statsFormat = optionDict.get("-sf");

        return new Config(printHelp, computeNamedEntities, specificFile,heuristicKey, filePath,statsFormat);
    }
}
