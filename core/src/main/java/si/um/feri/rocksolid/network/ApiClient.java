package si.um.feri.rocksolid.network;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import si.um.feri.rocksolid.data.ClimbingSpot;
import si.um.feri.rocksolid.utils.Geolocation;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {
    private final HttpClient client;
    private final Gson gson;

    public ApiClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    public String login(String baseUrl, String username, String password) throws IOException {
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        body.addProperty("password", password);

        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/users/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
            .build();

        try {
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2)
                throw new IOException("Login failed");

            return gson.fromJson(resp.body(), JsonObject.class)
                .get("token").getAsString();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        }
    }


    public List<ClimbingSpot> fetchNearbyClimbingSpots(String baseUrl, String token, double latitude, double longitude, double distanceKm) throws IOException {
        JsonObject body = new JsonObject();
        body.addProperty("latitude", latitude);
        body.addProperty("longitude", longitude);
        body.addProperty("distance", distanceKm);

        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/climbingAreas/byProximity"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
            .build();

        try {
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2)
                throw new IOException("Request failed");

            JsonArray arr = gson.fromJson(resp.body(), JsonArray.class);
            List<ClimbingSpot> out = new ArrayList<>();

            for (JsonElement e : arr) {
                JsonObject o = e.getAsJsonObject();
                out.add(new ClimbingSpot(
                    new Geolocation(
                        o.get("latitude").getAsDouble(),
                        o.get("longitude").getAsDouble()
                    ),
                    o.get("name").getAsString()
                ));
            }
            return out;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        }
    }

}
