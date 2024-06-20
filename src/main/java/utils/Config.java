package utils;

public class Config {
    private boolean computeNamedEntities = false;
    private boolean printHelp = false;
    private boolean specificFile;

    private String heuristicKey = null;
    private String statsFormat = "cat";
    private String filePath = null;
    public Config(boolean printHelp, boolean computeNamedEntities, boolean specificFile, String heuristicKey, String filePath,String statsFormat) {
        this.computeNamedEntities = computeNamedEntities;
        this.heuristicKey = heuristicKey;
        this.specificFile = specificFile;
        if(filePath != null){
            this.filePath = filePath; 
        }
        if (statsFormat != null) {
            this.statsFormat = statsFormat;
        }
        this.printHelp = printHelp;
    }

    public boolean getPrintHelp() {
        return printHelp;
    }

    public boolean getComputeNamedEntities() {
        return computeNamedEntities;
    }

    public boolean getSpecificFile(){
        return specificFile;
    }

    public String getFilePath(){
        return filePath;
    }

    public String getHeuristicKey(){
        return heuristicKey;
    }

    public String getStatsFormat(){
        return statsFormat;
    }
}
