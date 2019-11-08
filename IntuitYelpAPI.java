package com.company;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IntuitYelpAPI {

    public static void getTopRestaurantsByLocation(String location,int miles, int top){
        try {
            int meters = 1609*miles;
            //setting up the URL with parameters.
            System.out.println("Sending the request to the Yelp API for top "+top+" restaurants near "+location+" in "+miles+" miles.");
            URL url = new URL("https://api.yelp.com/v3/businesses/search?location="+location+"&radius="+meters+"&sort_by=review_count&limit="+top);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            //setting up authorization key.
            conn.setRequestProperty("Authorization", "Bearer "+"1zynhUfpqVLB5mJ81mIHs5SI2v7ZzKmGTDz_HSa6tt8XntpmOtVIPTaxXe46xU1DdkeDozZDpKUyWk6RM3XTq5HSl_wt56sCvFCjKADYSe5eQ0ByK9cnOxHt4VXEXXYx");

            //throwing the error message if the response is not available.
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            //capturing the response and appending it to the response string.
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            StringBuffer response = new StringBuffer();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            //converting the response to the JSONObject.
            JSONObject jsonObj = new JSONObject(response.toString());
            JSONArray list1 = new JSONArray();
            JSONArray arr = jsonObj.getJSONArray("businesses");

            //capturing the specific values(name, rating, reviewcount) from the response
            for (int i = 0; i < arr.length(); i++) {
                JSONObject details = new JSONObject();
                String name = arr.getJSONObject(i).getString("name");
                int rating = arr.getJSONObject(i).getInt("rating");
                int review_count = arr.getJSONObject(i).getInt("review_count");
                String address = arr.getJSONObject(i).getJSONObject("location").getString("address1");
                details.put("Name",name);
                details.put("Rating",rating);
                details.put("Review Count",review_count);
                details.put("Address",address);
                JSONObject object = new JSONObject();
                object.put("restaurant "+(i+1), details);
                list1.put(object);
            }

            //writing the response to the answer.json file.
            System.out.println("Writing the results to answer.json file.");
            try (FileWriter file = new FileWriter("/Users/arishdhingra/IdeaProjects/Intuit/src/com/company/answer.json")) {
                file.write(list1.toString());
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        getTopRestaurantsByLocation("Chicago",10,5);
    }
}

