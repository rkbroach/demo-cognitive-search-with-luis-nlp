package main.java.app;

import main.java.service.SearchServiceClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class App {
    static SearchServiceClient client;

    // PROPERTIES
    private static Properties loadPropertiesFromResource(String resourcePath) throws IOException {
        var inputStream = App.class.getResourceAsStream(resourcePath);
        var configProperties = new Properties();
        configProperties.load(inputStream);
        return configProperties;
    }

    // MAIN
    public static void main(String[] args) {

        System.out.println("Main - Before Try");
        //loadPropertiesFromResource()

    }


    public static List<String> getSearchQuery(String rawQuery) throws IOException, InterruptedException {
        String luisResponse = SearchServiceClient.applyNLP(rawQuery);
        System.out.println("The LUIS Response is  - \n\n" + luisResponse);
        return applyQuery(luisResponse);
    }

    static List<String> applyQuery(String luisResponse) throws IOException, InterruptedException {

        System.out.println("I am in applyQuery method");

        // Create Cognitive
        // try {
        var config = loadPropertiesFromResource("/app/config.properties");
        var client = new SearchServiceClient(
                config.getProperty("SearchServiceName"),
                config.getProperty("SearchServiceAdminKey"),
                config.getProperty("SearchServiceQueryKey"),
                config.getProperty("ApiVersion"),
                config.getProperty("IndexName")
        );
        System.out.println("Apply Query - After Try");

//            // Create Index
//            if(client.indexExists()){
//                client.deleteIndex();
//            }
//            client.createIndex("/service/indexSchema.json");
//            Thread.sleep(1000L); // wait a second to create the index

////            // Upload Documents to Index
//            client.uploadDocuments("/service/data.json");
//            Thread.sleep(2000L); // wait 2 seconds for data to upload

//            // NLP Work
//            String rawQuery = "what are the capabilities in India ";
//            String luisResponse = SearchServiceClient.applyNLP(rawQuery);
////            getSearchQuery("what are the capabilities in India ");
//            System.out.println("The LUIS Response is  - \n\n" + luisResponse);


//        } catch (Exception e) {
//            System.err.println("Exception:" + e.getMessage());
//            e.printStackTrace();
//        }


        // Convert LUIS String to JSON Format
        JSONObject jsonObj = new JSONObject(luisResponse);
        System.out.println("I have created JSON Obj");

        JSONArray jArray = jsonObj.getJSONArray("entities");
        List<String> list = new ArrayList<String>();
        List<Double> confidenceScores = new ArrayList<Double>();
        for (int i = 0; i < jArray.length(); i++) {
            confidenceScores.add(i, jArray.getJSONObject(i).getDouble("score"));
            //int m= Collections.max(list1,null);
        }
        double max = 0;
        int index = 0;
        for (int k = 0; k < jArray.length(); k++) {
            if (confidenceScores.get(k) > max) {
                max = confidenceScores.get(k);
                index = k;
            }
        }
        String f = "";
        list.add(0, jArray.getJSONObject(index).getString("type"));
        f = jArray.getJSONObject(index).getString("entity");
        for (int i = 0; i < jArray.length(); i++) {
            if (i != index) {
                list.add(jArray.getJSONObject(i).getString("entity"));
            }
        }
        String f2 = f.replaceAll("\\s+", "%20");
        System.out.println(list);
        System.out.println(f);
        System.out.println(f2);
        JSONArray a = jsonObj.getJSONArray("intents");
        String intent = a.getJSONObject(0).getString("intent");
        String str = "";
        for (int i = 1; i < list.size(); i++) {
            str = str + list.get(i);

            if (i == list.size() - 1) {
                break;
            } else {
                str = str + ",";
            }
        }
        System.out.println(str);
        String answer = "";
        JSONArray jArray1 = new JSONArray();

        if (intent.equals("Find.inAll")) {
            System.out.println("I am in Find All intent");
            SearchServiceClient.SearchOptions options2 = client.createSearchOptions();
            options2.select = list.get(0);
            answer = client.searchPlus("*", options2);
            JSONObject jsonObj1 = new JSONObject(answer);
            jArray1 = jsonObj1.getJSONArray("value");
        } else if (intent.equals("Find.inAttribute")) { // Find.inAttribute
            SearchServiceClient.SearchOptions options3 = client.createSearchOptions();
            options3.select = list.get(0);
            answer = client.searchPlus(str, options3);
            JSONObject jsonObj1 = new JSONObject(answer);
            jArray1 = jsonObj1.getJSONArray("value");
        } else if (intent.equals("Find.Count")) {

            SearchServiceClient.SearchOptions options3 = client.createSearchOptions();
            options3.select = list.get(0);
            answer = client.searchPlus(f2, options3);
            JSONObject jsonObj1 = new JSONObject(answer);
            jArray1 = jsonObj1.getJSONArray("value");
        } else if (intent.equals("Find.Boolean")) {
            SearchServiceClient.SearchOptions options3 = client.createSearchOptions();
            options3.select = list.get(0);
            answer = client.searchPlus(f2, options3);
            JSONObject jsonObj1 = new JSONObject(answer);
            jArray1 = jsonObj1.getJSONArray("value");
        }

        System.out.println("FINAL ANSWER \n" + answer);

        List<String> ansList = new ArrayList<String>();
        for (int i = 0; i < jArray1.length(); i++) {
            ansList.add(jArray1.getJSONObject(i).getString(list.get(0)));
        }
        if (intent.equals("Find.Count")) {
            int length = ansList.size();
            ansList.clear();
            ansList.add(0, Integer.toString(length));
        } else if (intent.equals("None")) {
            String bool = "Not a suitable query for this database";
//            if(ansList.size()>0){
//                bool="yes";
//            }
//            else {
//                bool="no";
//            }
            ansList.clear();
            ansList.add(0, bool);
        }
        System.out.println("I am in Find Attr intent 4 - \n" + ansList);

        return ansList;

    }


}