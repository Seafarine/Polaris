package org.github.paperspigot;

import net.shieldcommunity.spigot.config.ShieldSpigotConfigImpl;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import lombok.Value;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;

import java.security.NoSuchAlgorithmException;

@RequiredArgsConstructor
@Getter
public class PaperSpigotByteBuf {

    private final URI apiRequest = URI.create(addressManager());
    private final String brand = nameCheck();
    private final String tweakerHandler = tweakHandler();
    private final String version = versionManager();

    private long discord_id;
    private String discord_tag;

    private long attempts = 0;

    public ResultData check() {
        if (this.attempts > 0) {
            return new ResultData(Result.ALREADY_CHECKED);
        }

        if (ShieldSpigotConfigImpl.IMP.YOUR_LICENSE.equals("YOUR-LICENSE")) {
            return new ResultData(Result.DEFAULT_KEY);
        }

        this.attempts++;

        String httpRequest = ShieldSpigotConfigImpl.IMP.YOUR_LICENSE;

        String bufManager;
        try {
            bufManager = getHWID();
        } catch (Exception e) {
            return new ResultData(Result.HWID_FAILED, e);
        }

        HttpPost post = new HttpPost(this.apiRequest);

        try {
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("licensekey", httpRequest));
            urlParameters.add(new BasicNameValuePair("product", this.brand));
            urlParameters.add(new BasicNameValuePair("version", this.version));
            urlParameters.add(new BasicNameValuePair("hwid", bufManager));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (Exception e) {
            return new ResultData(Result.HTTP_ENTITY_CREATION_FAILED, e);
        }

        post.setHeader("Authorization", this.tweakerHandler);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setConnectionRequestTimeout(5000)
                        .setSocketTimeout(5000)
                        .build())
                .build();
             CloseableHttpResponse response = httpClient.execute(post)) {

            JSONObject obj;
            try {
                obj = new JSONObject(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
                return new ResultData(Result.JSON_ERROR, e);
            }

            int code = obj.getInt("status_code");
            if (code != 200) {
                if (code == 401) {
                    if (obj.getString("status_msg").equals("INVALID_LICENSEKEY")) {
                        return new ResultData(Result.INVALID);
                    } else if (obj.getString("status_msg").equals("MAX_IP_CAP")) {
                        return new ResultData(Result.LIMIT);
                    }
                }

                return new ResultData(Result.HTTP_ERROR_CODE);
            }

            this.discord_id = obj.getLong("discord_id");
            this.discord_tag = obj.getString("discord_tag");

            return new ResultData(Result.SUCCESSFULLY);
        } catch (JSONException e) {
            return new ResultData(Result.JSON_ERROR, e);
        } catch (ClientProtocolException e) {
            return new ResultData(Result.HTTP_ERROR_CODE, e);
        } catch (IOException e) {
            return new ResultData(Result.CONNECTION_FAILED, e);
        }
    }

    public String addressManager() {
        return "http://licenses.shieldcommunity.net:3000/api/client";
    }

    public String getHWID() throws NoSuchAlgorithmException {
        String toEncrypt = System.getenv("COMPUTERNAME")
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

    public String nameCheck() {
        return "ShieldSpigot";
    }

    public String tweakHandler() {
        return "284739204657483950682356801363827503761045";
    }

    public String versionManager() {
        return "0.0.1";
    }

    @RequiredArgsConstructor
    @Getter
    public enum Result {
        SUCCESSFULLY(true, "Your license key is valid."),
        ALREADY_CHECKED(true, "Your license key already checked."),
        DEFAULT_KEY(false, "Your license specified on the config is the default one, you must change it or the proxy won't run. To get a license please first verify as buyer on our discord and write /self create to automatically get a license."),
        LIMIT(false, "Your license has reached our fair limit of 3-ips-per-license. Execute /license clear on our discord server to clear stored ips on your license."),
        INVALID(false, "The license does not exists."),
        JSON_ERROR(false, "The server returned an unexpected json response, please report this error to us."),
        HTTP_ERROR_CODE(false, "Error while getting data. Please report this error to us."),
        CONNECTION_FAILED(false, "API is down, please take a look at our discord or warn us if this isn't already known."),
        HWID_FAILED(false, "Failed to calculate hwid."),
        HTTP_ENTITY_CREATION_FAILED(false, "Failed to set http entity.");

        private final boolean valid;
        private final String reason;

    }

    @RequiredArgsConstructor
    @Value
    public static class ResultData {
        Result result;
        Exception exception;

        public ResultData(Result result) {
            this(result, null);
        }
    }
}