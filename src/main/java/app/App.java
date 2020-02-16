package main.java.app;

import main.java.service.SearchServiceClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class App {

    // PROPERTIES
    private static Properties loadPropertiesFromResource(String resourcePath) throws IOException {
        var inputStream = App.class.getResourceAsStream(resourcePath);
        var configProperties = new Properties();
        configProperties.load(inputStream);
        return configProperties;
    }

    // MAIN
    public static void main(String[] args) {
        try {
            var config = loadPropertiesFromResource("/app/config.properties");
            var client = new SearchServiceClient(
                    config.getProperty("SearchServiceName"),
                    config.getProperty("SearchServiceAdminKey"),
                    config.getProperty("SearchServiceQueryKey"),
                    config.getProperty("ApiVersion"),
                    config.getProperty("IndexName")
            );


            // Create Index
            if(client.indexExists()){ client.deleteIndex();}
            client.createIndex("/service/indexSchema.json");
            Thread.sleep(1000L); // wait a second to create the index

            // Upload Documents to Index
            client.uploadDocuments("/service/data.json");
            Thread.sleep(2000L); // wait 2 seconds for data to upload

            // Work
            String rawQuery = "what are the capabilities in India ";
            String luisResponse = SearchServiceClient.applyNLP(rawQuery);
            System.out.println("The LUIS Response is  - \n\n" + luisResponse);

            // Convert LUIS String to JSON Format
            JSONObject jsonObj = new JSONObject(luisResponse);

            JSONArray jArray = jsonObj.getJSONArray("entities");
            List<String> list = new ArrayList<String>();
            List<Double> confidenceScores = new ArrayList<Double>();
            for(int i = 0 ; i < jArray.length() ; i++) {
                confidenceScores.add(i, jArray.getJSONObject(i).getDouble("score"));
                //int m= Collections.max(list1,null);
            }
            double max=0;
            int index = 0;
            for(int k=0;k<jArray.length();k++){
                if(confidenceScores.get(k) > max){
                    max=confidenceScores.get(k);
                    index=k;
                }
            }

            list.add(0,jArray.getJSONObject(index).getString("type"));
            for(int i = 0 ; i < jArray.length() ; i++){
                if(i!=index) {
                    list.add(jArray.getJSONObject(i).getString("entity"));
                }
            }
            System.out.println(list);
            JSONArray a=jsonObj.getJSONArray("intents");
            String intent=a.getJSONObject(0).getString("intent");
            String str = "";
            for (int i = 1; i < list.size(); i++) {
                str=str+list.get(i);

                if (i == list.size()-1){
                    break;
                }
                else{
                    str=str+",";
                }
            }
            System.out.println(str);

            if(intent.equals("Find.inAll")) {
                SearchServiceClient.SearchOptions options2 = client.createSearchOptions();
                options2.select = list.get(0);
                client.searchPlus("*", options2);
            } else {
                SearchServiceClient.SearchOptions options3 = client.createSearchOptions();
                options3.select = list.get(0);
                client.searchPlus(str, options3);
            }

        } catch (Exception e) {
            System.err.println("Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }
}