/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.util;

/**
 *
 * @author rcosco
 */
import com.google.gson.Gson;
import it.refill.db.Entity;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Recaptcha V3 - Java Example
 */
public class GoogleRecaptcha {

    public static boolean isValid(String clientRecaptchaResponse) {

        try {
            Entity e = new Entity();
            final String RECAPTCHA_SERVICE_URL = "https://www.google.com/recaptcha/api/siteverify";
            final String SECRET_KEY = e.getPath("googlePrivateKey");
            e.close();
            if (clientRecaptchaResponse == null || "".equals(clientRecaptchaResponse)) {
                return false;
            }
            URL obj = new URL(RECAPTCHA_SERVICE_URL);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            String postParams
                    = "secret=" + SECRET_KEY
                    + "&response=" + clientRecaptchaResponse;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
//            System.out.println("GoogleRecaptcha " + responseCode + " -- " + response.toString());
            CaptchaResponse capRes = new Gson().fromJson(response.toString(), CaptchaResponse.class);
            return (capRes.isSuccess() && capRes.getScore() >= 0.5);
            
//          JSONParser parser = new JSONParser();
//          JsonObject json = new Gson().f parser.parse(response.toString());
//          Boolean success = (Boolean) json.get("success");
//          Double score = (Double) json.get("score");
//          return (success && score >= 0.5);

        } catch (Exception e) {
        }
        return false;

    }
}

class CaptchaResponse {

    public boolean success;
    public double score;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
