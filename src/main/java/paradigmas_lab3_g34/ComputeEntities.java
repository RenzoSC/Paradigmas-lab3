package paradigmas_lab3_g34;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import feed.Article;
import feed.FeedParser;
import utils.CategoryMap;
import utils.Config;
import utils.FeedsData;
import utils.HeuristicData;
import utils.JSONDict;
import utils.JSONParser;
import utils.TopicMap;
import utils.UserInterface;

import namedEntities.HeuristicMap;
import namedEntities.Heuristic;

import scala.Tuple2;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;


public class ComputeEntities {

    public static void main(String[] args) {

        List<FeedsData> feedsDataArray = new ArrayList<>();
        try {
            feedsDataArray = JSONParser.parseJsonFeedsData("./src/main/java/data/feeds.json");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        List<HeuristicData> heuristicDataArray = new ArrayList<>();
        try {
            heuristicDataArray = JSONParser.parseJsonHeuristicData("./src/main/java/data/heuristics.json");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        UserInterface ui = new UserInterface();
        Config config = ui.handleInput(args);

        run(config, feedsDataArray, heuristicDataArray);
    }

    private static void run(Config config, List<FeedsData> feedsDataArray, List<HeuristicData> heuristicDataArray) {
        superAssert(config, feedsDataArray, heuristicDataArray);
        configPrintHelp(config, feedsDataArray, heuristicDataArray);
        List<Article> allArticles = new ArrayList<>();
        allArticles = configProcessFeed(config, feedsDataArray);
        configPrintFeed(config, allArticles);
        configComputeNamedEntities(config, allArticles);
    }

    private static void superAssert(Config config, List<FeedsData> feedsDataArray, List<HeuristicData> heuristicDataArray){
        if (feedsDataArray == null || feedsDataArray.size() == 0) {
            System.out.println("No feeds data found");
            System.exit(1);
        }

        if (heuristicDataArray == null || heuristicDataArray.size() == 0) {
            System.out.println("No heuristics found");
            System.exit(1);
        }

        if (!(config.getStatsFormat().equals("cat") || config.getStatsFormat().equals("topic"))) {
            System.out.println("Format not founded!");
            System.out.println("Available formats are: ");
            System.out.println("   - cat: Category-wise stats");
            System.out.println("   - topic: Topic-wise stats");
            System.exit(1);
        }

        if (!HeuristicMap.isHeuristic(config.getHeuristicKey())) {
            System.out.println("Heuristic not founded!");
            if(heuristicDataArray != null){
                System.out.println("Available heuristic names are: ");
                for(HeuristicData heuristicData:heuristicDataArray){
                    System.out.println("   <"+heuristicData.getName()+">: <"+heuristicData.getDescription()+">");
                }
            }
            System.exit(1);
        }
    }

    private static List<Article>  configProcessFeed(Config config, List<FeedsData> feedsDataArray){
        List<Article> allArticles = new ArrayList<>();
        List<Article>currenArticles = new ArrayList<>();
        for (FeedsData feedData : feedsDataArray) {
            try {
                currenArticles = FeedParser.parseXML(FeedParser.fetchFeed(feedData.getUrl()));
                allArticles.addAll(currenArticles);
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        return allArticles;
    }

    private static void configPrintHelp(Config config, List<FeedsData> feedsDataArray, List<HeuristicData> heuristicDataArray){
        if (config.getPrintHelp()) {
            printHelp(feedsDataArray, heuristicDataArray);
            System.exit(1);
        }
    }

    private static void configPrintFeed(Config config, List<Article> allArticles){
        try {
            System.out.println("Printing feed(s) ");
            FileWriter fw=new FileWriter("/data/bigdata.txt");
            for(Article art: allArticles){
                fw.write(art.getTitle() + "\n"); 
                fw.write(art.getDescription()+"\n");
                //art.print(); //Imprimir los Articulos del Big Data en Pantalla
            }
            fw.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    private static void configComputeNamedEntities(Config config, List<Article> allArticles){

        //Iniciar Spark Session
        SparkSession spark = SparkSession
        .builder()
        .appName("JavaComputeEntities")
        .master("local[*]")
        .getOrCreate();
 
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        //Pasos Aproximados Basados en WordCount. 
        // Resilient Distributed Dataset (RDD)
        //1 Leer BigData 
            //JavaRDD<String> lines = spark.read().textFile(args[0]).javaRDD();

        //2 Separarlo en Name Entities (Desechar el Resto) (Usando las Distintas Heuristicas)
            //JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(SPACE.split(s)).iterator());

        //3 Pair cada Name Entity con (NE,1) 
            //JavaPairRDD<String, Integer> ones = words.mapToPair(s -> new Tuple2<>(s, 1));

        //4 Sumar las que sean Iguales 
            //JavaPairRDD<String, Integer> counts = ones.reduceByKey((i1, i2) -> i1 + i2);

        //5 Imprimir los Resultados de la forma que se pidan (Por Cat o por Top)
            /* List<Tuple2<String, Integer>> output = counts.collect();
                for (Tuple2<?,?> tuple : output) {
                System.out.println(tuple._1() + ": " + tuple._2());
                }*/
  
        if (config.getComputeNamedEntities()) {
            String heuristic = config.getHeuristicKey();
            System.out.println("Computing named entities using " + heuristic);

            //EXTRAER PALABRAS (1,2)
            Heuristic heuristicFunc = HeuristicMap.getHeuristic(heuristic);

            JavaRDD<Article> articulosRDD =  jsc.parallelize(allArticles,4);

            JavaRDD<String> titulosRDD = articulosRDD.map(Article::getTitle);

            JavaRDD<String> descsRDD = articulosRDD.map(Article::getDescription);
            JavaRDD<String> textoRDD;
            if(config.getSpecificFile()){
                String filePath = config.getFilePath();
                textoRDD = jsc.textFile(filePath, 4);
            }else{
                textoRDD = titulosRDD.union(descsRDD).repartition(4);
            }

            //Hay que Añadir "import java.io.Serializable; e implements Serializable" a las Heuristicas y Article.
            JavaRDD<List<String>> candidatesListRDD= textoRDD.map(heuristicFunc::extract).cache();
            
            //No se si las otras tambien se pueden hacer con Lambda, quedaria mas bonito. 
            JavaRDD<String> candidatesRDD = candidatesListRDD.flatMap(List::iterator);
            /* 
            // Imprimir todos los candidatos ---- para debuggeo
            List<String> candidatesList = candidatesRDD.collect();
            for (String candidate : candidatesList) {
                System.out.println(candidate);
            }*/

            //CLASIFICAR PALABRAS (3,4)
            JSONDict dict = new JSONDict("./src/main/java/data/dictionary.json");

            final Broadcast<JSONDict> dictBroadcast = jsc.broadcast(dict);

            CategoryMap categoryMap = new CategoryMap();
            TopicMap topicMap = new TopicMap();

            JavaPairRDD<String, Integer> ones = candidatesRDD.mapToPair(s -> new Tuple2<>(s, 1));

            JavaPairRDD<String, Integer> counts = ones.reduceByKey(Integer::sum);

            final Broadcast<CategoryMap> categoryMapBroadcast = jsc.broadcast(categoryMap);
            final Broadcast<TopicMap> topicMapBroadcast = jsc.broadcast(topicMap);

            counts.foreachPartition(iterator ->{
                JSONDict dictLocal = dictBroadcast.value();
                CategoryMap categoryMapLocal = categoryMapBroadcast.value();
                TopicMap topicMapLocal = topicMapBroadcast.value();


                iterator.forEachRemaining(pair->{
                    for(int i = 0; i< dictLocal.getLength(); i++){
                        List<String> keywords = dictLocal.getKeywords(i);
    
                        if(keywords.contains(pair._1())){
                            String cat = dictLocal.getCategory(i);
                            String label = dictLocal.getLabel(i);
                            List<String> topics = dictLocal.getTopic(i);
    
                            for(int j =0; j<pair._2(); j++){
                                categoryMapLocal.addEntity(topics, cat, label);
                                topicMapLocal.addEntity(topics, label, cat);
                            }
                        }
                    }
                });
            });

            categoryMap = categoryMapBroadcast.value();
            topicMap = topicMapBroadcast.value();

            //IMPRIMIR PALABRAS (5)
            //Esto creo que no Cambia.
            String statsFormat = config.getStatsFormat(); //obtenemos el tipo de stats que tenemos que printear
            System.out.println("\nStats: "+statsFormat);
            System.out.println("-".repeat(80));
            if (statsFormat.equals("cat")) {
                categoryMap.print();
            }else{
                topicMap.print();
            }
        }else{
            System.out.println("Printing feed(s) ");
            for(Article art: allArticles){
                art.print();
            }   
        }
        jsc.close();
        jsc.stop();
        spark.stop();
    }

    private static void printHelp(List<FeedsData> feedsDataArray, List<HeuristicData> heuristicDataArray) {
        System.out.println("Usage: \"[OPTIONS]\"");
        System.out.println("Options:");
        System.out.println("  -h, --help: Show this help message and exit");
        System.out.println("  -f, --file: Given a specific path to a file, process that file");
        System.out.println("Available feed keys are: ");
        for (FeedsData feedData : feedsDataArray) {
            System.out.println("                                       " + feedData.getLabel());
        }
        System.out.println("  -ne, --named-entity <heuristicName>: Use the specified heuristic to extract");
        System.out.println("                                       named entities");
        System.out.println("                                       Available heuristic names are: ");

        for(HeuristicData heuristicData:heuristicDataArray){
            System.out.println("                                     <"+heuristicData.getName()+">: <"+heuristicData.getDescription()+">");
        }
    
        System.out.println("  -sf, --stats-format <format>:        Print the stats in the specified format");
        System.out.println("                                       Available formats are: ");
        System.out.println("                                       cat: Category-wise stats");
        System.out.println("                                       topic: Topic-wise stats");
    }

}
