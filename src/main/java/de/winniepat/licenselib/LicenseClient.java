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
            HttpRequest request = createRequest(apiUrl, plugin, licenseKey, serverId);
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            return fromHttpResponse(response);
        } catch (Exception e) {
            return errorResult(e);
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
        HttpRequest request = createRequest(apiUrl, plugin, licenseKey, serverId);

        return HTTP_CLIENT
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(LicenseClient::fromHttpResponse)
                .exceptionally(LicenseClient::errorResult);
    }

    /**
     * Represents the result of a license check, which can be either a success with license details or an error with a message.
     */
    public sealed interface LicenseResult permits LicenseSuccess, LicenseError {}

    private static HttpRequest createRequest(
            String apiUrl,
            String plugin,
            String licenseKey,
            String serverId
    ) {
        JsonObject payload = new JsonObject();
        payload.addProperty("plugin", plugin);
        payload.addProperty("licenseKey", licenseKey);

        if (serverId != null) {
            payload.addProperty("serverId", serverId);
        }

        return HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(payload)))
                .build();
    }

    private static LicenseResult errorResult(Throwable throwable) {
        return new LicenseError(throwable.getMessage());
    }

    private static String getString(JsonObject json, String key, String def) {
        return json.has(key) && !json.get(key).isJsonNull()
                ? json.get(key).getAsString()
                : def;
    }

    private static LicenseResult fromHttpResponse(HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            return errorResult(new RuntimeException(
                    "HTTP " + response.statusCode() + ": " + response.body()
            ));
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        boolean valid = json.has("valid") && json.get("valid").getAsBoolean();

        return new LicenseSuccess(
                valid,
                getString(json, "status", "unknown"),
                getString(json, "message", ""),
                getString(json, "plugin", null),
                getString(json, "customer", null),
                getString(json, "expiresAt", null),
                getString(json, "checkedAt", null)
        );
    }
}
