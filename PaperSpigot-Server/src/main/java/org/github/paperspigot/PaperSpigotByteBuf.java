package org.github.paperspigot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.shieldcommunity.spigot.config.ShieldSpigotConfigImpl;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class PaperSpigotByteBuf {

    private URI host = URI.create("http://licenses.shieldcommunity.net:3000/api/client");
    private String licenseKey = ShieldSpigotConfigImpl.IMP.YOUR_LICENSE;
    private String product = "ShieldSpigot";
    private String version = "0.0.1";
    private String apiKey = "284739204657483950682356801363827503761045";

    private int statusCode;
    private String discordName;
    private String discordID;
    private String statusMsg;


    public boolean check() {
        String hwid = ShieldSpigotConfigImpl.IMP.YOUR_LICENSE;
        HttpPost post = new HttpPost(host);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("licensekey", licenseKey));
        urlParameters.add(new BasicNameValuePair("product", product));
        urlParameters.add(new BasicNameValuePair("version", version));
        urlParameters.add(new BasicNameValuePair("hwid", hwid));

        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        post.setHeader("Authorization", apiKey);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)
        ) {
            String data = EntityUtils.toString(response.getEntity());
            JSONObject obj = new JSONObject(data);
            if(!obj.has("status_msg") || !obj.has("status_id")) {
                return false;
            }

            statusCode = obj.getInt("status_code");
            statusMsg = obj.getString("status_msg");

            if(obj.getString("status_overview") == null){
                return false;
            }

            discordName = obj.getString("clientname"); // You can set discord_username too!
            discordID = obj.getString("discord_id");

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
