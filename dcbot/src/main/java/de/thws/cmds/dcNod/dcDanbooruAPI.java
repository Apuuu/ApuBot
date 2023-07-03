package de.thws.cmds.dcNod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class dcDanbooruAPI {

    public static String getDanbooruTags(int Tag){
        try {
 
            String apiUrl = "https://danbooru.donmai.us/posts/"+Tag+".json";
            
            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("GET");
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line, tagString, charString;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                String jsonResponse = response.toString();

                ObjectMapper objectMapper2 = new ObjectMapper();
                JsonNode jsonNode2 = objectMapper2.readTree(jsonResponse.toString());

                if (jsonNode2.has("tag_string_general") && jsonNode2.get("tag_string_general").isTextual()) {
                    tagString  = jsonNode2.get("tag_string_general").asText();
                    charString = jsonNode2.get("tag_string_character").asText();
          
                    String replaceSpace = tagString.replace(' ',',').replace('_',' ');
                    String replaceChar = charString.replace("_"," ");
                    return replaceChar+","+replaceSpace;
                    
                }else{
                    return "No tags";
                }

            } else {
                System.out.println("Error: " + responseCode);
               
            }
            
            connection.disconnect();
            return "Error: Empty";

        }catch (IOException e){
            return "Error: "+e.toString()+"";
        }
    }
}
