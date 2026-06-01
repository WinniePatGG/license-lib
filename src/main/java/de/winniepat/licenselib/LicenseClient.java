package de.winniepat.licenselib;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.winniepat.winnielib.http.Http;
import de.winniepat.winnielib.http.HttpResponse;

import java.net.http.HttpClient;

/**
 * A client for checking plugin licenses against a license server.
 */
public class LicenseClient {

    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private LicenseClient() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Checks the validity of a license key for a given plugin and server ID against the license server API.
     * @param apiUrl Server API url
     * @param plugin Plugin id matching the one on the backend api
     * @param licenseKey License key issued by the api
     * @param serverId Server id matching the one on the backend api
     * @return LicenseResult with the data from the backend
     */
    public static LicenseResult check(
            String apiUrl,
            String plugin,
            String licenseKey,
            String serverId
    ) {

        Http http = new Http();

        JsonObject payload = new JsonObject();
        payload.addProperty("plugin", plugin);
        payload.addProperty("licenseKey", licenseKey);
        payload.addProperty("serverId", serverId);

        HttpResponse response = http.post(apiUrl, GSON.toJson(payload));

        JsonObject json = GSON.fromJson(response.body(), JsonObject.class);

        return new LicenseResult(
                json.get("valid").getAsBoolean(),
                json.get("status").getAsString(),
                json.get("message").getAsString()
        );
    }

    /**
     * Checks if license is valid (without serverId)
     * @param apiUrl Server API url
     * @param plugin Plugin id matching the one on the backend api
     * @param licenseKey License key issued by the api
     * @return LicenseResult with the data from the backend
     */
    public static LicenseResult check(
            String apiUrl,
            String plugin,
            String licenseKey
    ) {

        Http http = new Http();

        JsonObject payload = new JsonObject();
        payload.addProperty("plugin", plugin);
        payload.addProperty("licenseKey", licenseKey);

        HttpResponse response = http.post(apiUrl, GSON.toJson(payload));

        JsonObject json = GSON.fromJson(response.body(), JsonObject.class);

        return new LicenseResult(
                json.get("valid").getAsBoolean(),
                json.get("status").getAsString(),
                json.get("message").getAsString()
        );
    }

    /**
     * Represents the result of a license check.
     * @param isValid boolean if the license is valid or not
     * @param status license status
     * @param message additional message from the backend
     */
    public record LicenseResult(
            boolean isValid,
            String status,
            String message
    ) { }
}
