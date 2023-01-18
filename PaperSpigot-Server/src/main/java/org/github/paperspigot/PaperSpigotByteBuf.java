package org.github.paperspigot;

import net.md_5.bungee.api.ChatColor;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class PaperSpigotByteBuf {

    private final URI apiRequest = URI.create(addressManager());
    private final String brand = nameCheck();
    private final String tweakerHandler = tweakHandler();
    private final String version = versionManager();

    private int statusCode;
    private String dnAx;
    private String djAne;
    private String statusMsg;
    private long attempts = 0;

    public boolean check() {
        if (attempts > 0) {
            return true;
        }

        String httpRequest = PaperSpigotConfig.paperSpigotLicense;

        String bufManager = getHWID();

        attempts++;

        HttpPost post = new HttpPost(apiRequest);
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("licensekey", httpRequest));
        urlParameters.add(new BasicNameValuePair("product", brand));
        urlParameters.add(new BasicNameValuePair("version", version));
        urlParameters.add(new BasicNameValuePair("hwid", bufManager)
        );

        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        }
        catch (UnsupportedEncodingException e) {
            Bukkit.getLogger().info(ChatColor.RED +
                    "Seems like our API is off, please report us"
            );
        }

        post.setHeader("Authorization", tweakerHandler);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            String data = EntityUtils.toString(response.getEntity());
            JSONObject obj = new JSONObject(data);

            if (!obj.has("status_msg") || !obj.has("status_id")) {
                return false;
            }

            statusCode = obj.getInt("status_code");
            statusMsg = obj.getString("status_msg");

            if (obj.getString("status_overview") == null) {
                return false;
            }

            dnAx = obj.getString("clientname"); // You can set discord_username too!
            djAne = obj.getString("discord_id");

            return true;

        }
        catch (IOException e) {
            Bukkit.getLogger().info(ChatColor.RED +
                    "Maybe our API is down, please report us"
            );
            return false;
        }
    }

    public String addressManager() {
        return "http://licenses.shieldcommunity.net:3000/api/client";
    }

    public String getHWID() {
        try{
            String toEncrypt =  System.getenv("COMPUTERNAME")
                    + System.getProperty("user.name")
                    + System.getenv("PROCESSOR_IDENTIFIER")
                    + System.getenv("PROCESSOR_LEVEL"
            );

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toEncrypt.getBytes());

            StringBuilder hexString = new StringBuilder();

            byte[] byteData = md.digest();

            for (byte aByteData : byteData) {
                String hex = Integer.toHexString(0xff & aByteData);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Seems something failed to get your Hardware-ID";
        }
    }

    public String nameCheck() {
        return "ShieldSpigot";
    }

    public String tweakHandler() {
        return "284739204657483950682356801363827503761045";
    }

    public String versionManager() {
        return "0.1x";
    }

}
