/*
 * Copyright (c) 2026 WinniePatGG
 *
 * Licensed under the MIT License
 */

package de.winniepat.licenselib;

import com.google.gson.*;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * A client for checking plugin licenses against a license server.
 */
public class LicenseClient {

    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

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
    public static LicenseResult check(String apiUrl, String plugin, String licenseKey, String serverId) {
        try {
            JsonObject payload = new JsonObject();
            payload.addProperty("plugin", plugin);
            payload.addProperty("licenseKey", licenseKey);

            if (serverId != null) {
                payload.addProperty("serverId", serverId);
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(payload)))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            JsonObject json = GSON.fromJson(response.body(), JsonObject.class);

            return new LicenseResult(
                    json.get("valid").getAsBoolean(),
                    json.get("status").getAsString(),
                    json.get("message").getAsString()
            );
        } catch (Exception e) {
            return new LicenseResult(
                    false,
                    "error",
                    e.getMessage()
            );
        }
    }

    /**
     * Asynchronously checks the validity of a license key for a given plugin and server ID against the license server API.
     * @param apiUrl Server API url
     * @param plugin Plugin id matching the one on the backend api
     * @param licenseKey License key issued by the api
     * @param serverId Server id matching the one on the backend api
     * @return CompletabaleFuture with the LicenseResult from the backend
     */
    public static CompletableFuture<LicenseResult> checkAsync(String apiUrl, String plugin, String licenseKey, String serverId) {

        JsonObject payload = new JsonObject();
        payload.addProperty("plugin", plugin);
        payload.addProperty("licenseKey", licenseKey);

        if (serverId != null) {
            payload.addProperty("serverId", serverId);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(payload)))
                .build();

        return HTTP_CLIENT
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> parseResponse(response.body()))
                .exceptionally(ex -> new LicenseResult(
                        false,
                        "error",
                        ex.getMessage()
                ));
    }

    /**
     * Parses the JSON response from the license server API into a LicenseResult object.
     * @param body the JSON response body from the license server API
     * @return a LicenseResult object containing the validity, status, and message from the API response
     */
    private static LicenseResult parseResponse(String body) {
        JsonObject json = GSON.fromJson(body, JsonObject.class);

        return new LicenseResult(
                getBoolean(json, "valid", false),
                getString(json, "status", "unknown"),
                getString(json, "message", "")
        );
    }

    /**
     * Represents the result of a license check.
     * @param isValid boolean if the license is valid or not
     * @param status license status
     * @param message additional message from the backend
     */
    public record LicenseResult(boolean isValid, String status, String message) { }

    /**
     * Helper Method for getting Strings from the JsonObject without creating a NullPointerException
     *
     * @param json JsonObject to get the value from
     * @param key key to get the value for
     * @param def fallback value if Null
     * @return the value from the JsonObject or the fallback value if the key is not present or null
     */
    private static String getString(JsonObject json, String key, String def) {
        if (!json.has(key) || json.get(key).isJsonNull()) return def;
        return json.get(key).getAsString();
    }

    /**
     * Helper Method for getting booleans from the JsonObject without creating a NullPointerException
     * @param json JsonObject to get the value from
     * @param key key to get the value for
     * @param def fallback value if Null
     * @return the value from the JsonObject or the fallback value if the key is not present or null
     */
    private static boolean getBoolean(JsonObject json, String key, boolean def) {
        if (!json.has(key) || json.get(key).isJsonNull()) return def;
        return json.get(key).getAsBoolean();
    }
}
