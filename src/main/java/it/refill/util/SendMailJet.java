/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.util;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import static com.mailjet.client.resource.Emailv31.MESSAGES;
import static com.mailjet.client.resource.Emailv31.Message.ATTACHMENTS;
import static com.mailjet.client.resource.Emailv31.Message.BCC;
import static com.mailjet.client.resource.Emailv31.Message.CC;
import static com.mailjet.client.resource.Emailv31.Message.FROM;
import static com.mailjet.client.resource.Emailv31.Message.HTMLPART;
import static com.mailjet.client.resource.Emailv31.Message.SUBJECT;
import static com.mailjet.client.resource.Emailv31.Message.TO;
import static com.mailjet.client.resource.Emailv31.resource;
import com.mailjet.client.resource.Statcounters;
import it.refill.db.Entity;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import static java.nio.file.Files.probeContentType;
import static org.apache.commons.codec.binary.Base64.encodeBase64;
import static org.apache.commons.io.IOUtils.toByteArray;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author rcosco
 */
public class SendMailJet {

    public static boolean sendMail(String name, String[] to, String[] cc, String txt, String subject) {
        return sendMail(name, to, cc, txt, subject, null);
    }

    public static boolean sendMail(String name, String[] to, String txt, String subject) {
        return sendMail(name, to, new String[]{}, txt, subject, null);
    }

    public static boolean sendMail(String name, String[] to, String[] cc, String txt, String subject, File file) {
        try {
            MailjetClient client;
            MailjetRequest request;
            MailjetResponse response;

            String filename = "";
            String content_type = "";
            String b64 = "";

            Entity e = new Entity();
            String mailjet_api = e.getPath("mailjet_api");
            String mailjet_secret = e.getPath("mailjet_secret");
            String mailjet_name = e.getPath("mailjet_name");

            e.close();

            ClientOptions options = ClientOptions.builder()
                    .apiKey(mailjet_api)
                    .apiSecretKey(mailjet_secret)
                    .build();

            client = new MailjetClient(options);
            JSONArray dest = new JSONArray();
            JSONArray ccn = new JSONArray();
            JSONArray ccj = new JSONArray();

            if (to != null) {
                for (String s : to) {
                    dest.put(new JSONObject().put("Email", s)
                            .put("Name", ""));
                }
            }

            if (cc != null) {
                for (String s : cc) {
                    ccj.put(new JSONObject().put("Email", s)
                            .put("Name", ""));
                }
            }

            JSONObject mail = new JSONObject().put(FROM, new JSONObject()
                    .put("Email", mailjet_name)
                    .put("Name", name))
                    .put(TO, dest)
                    .put(CC, ccj)
                    .put(BCC, ccn)
                    .put(SUBJECT, subject)
                    .put(HTMLPART, txt);

            if (file != null) {
                try {
                    filename = file.getName();
                    content_type = probeContentType(file.toPath());
                    try (InputStream i = new FileInputStream(file)) {
                        b64 = new String(encodeBase64(toByteArray(i)));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                mail.put(ATTACHMENTS, new JSONArray()
                        .put(new JSONObject()
                                .put("ContentType", content_type)
                                .put("Filename", filename)
                                .put("Base64Content", b64)));
            }

            request = new MailjetRequest(resource)
                    .property(MESSAGES, new JSONArray()
                            .put(mail));

            response = client.post(request);

            boolean ok = response.getStatus() == 200;

            if (!ok) {
                System.err.println("ERRORE: sendMail - " + response.getStatus() + " -- " + response.getRawResponseContent() + " --- " + response.getData().toList());
            }

            return ok;
        } catch (MailjetException ex) {
            System.err.println("ERRORE: sendMail - " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public static int countMail(String today) {
        int sending = 0;
        try {

            Entity e = new Entity();
            String mailjet_api = e.getPath("mailjet_api");
            String mailjet_secret = e.getPath("mailjet_secret");
            e.close();
            MailjetClient client;
            ClientOptions options = ClientOptions.builder()
                    .apiKey(mailjet_api)
                    .apiSecretKey(mailjet_secret)
                    .build();
            client = new MailjetClient(options);
            MailjetRequest request;
            MailjetResponse response;
            if (today == null) {
                today = new DateTime().withMillisOfDay(0).toString("yyyy-MM-dd'T'HH:mm:ss");
            }
            request = new MailjetRequest(Statcounters.resource).filter(Statcounters.COUNTERSOURCE, "APIKey").filter(Statcounters.COUNTERTIMING, "Message")
                    .filter(Statcounters.COUNTERRESOLUTION, "Day").filter(Statcounters.FROMTS, today).filter(Statcounters.TOTS, today);
            response = client.get(request);
            if (response.getStatus() == 200) {
                try {
                    sending = new JSONArray(response.getData()).getJSONObject(0).getInt("Total");
                } catch (Exception exx) {
                    sending = 0;
                }
            } else {
                sending = -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sending = -1;
        }
        return sending;
    }

}
