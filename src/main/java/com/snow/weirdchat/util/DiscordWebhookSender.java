package com.snow.weirdchat.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhookSender {


    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/xxx/xxx";

    public static void sendToDiscord(String pseudo, String message) {
        if (pseudo == null || message == null) return;

        try {
            URL url = new URL(WEBHOOK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Java-DiscordWebhookClient");

            // Couleur au format décimal (ici vert clair)
            int embedColor = 0x1abc9c;

            String payload = String.format(
                    "{\n" +
                            "  \"embeds\": [\n" +
                            "    {\n" +
                            "      \"title\": \"💬 Message WeirdChat\",\n" +
                            "      \"description\": \"**%s** a envoyé :\\n%s\",\n" +
                            "      \"color\": %d,\n" +
                            "      \"footer\": {\n" +
                            "        \"text\": \"WeirdChat - Bizarro Town\"\n" +
                            "      },\n" +
                            "      \"timestamp\": \"%s\"\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}",
                    escapeJson(pseudo),
                    escapeJson(message),
                    embedColor,
                    java.time.Instant.now()
            );

            OutputStream os = connection.getOutputStream();
            os.write(payload.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode != 204 && responseCode != 200) {
                System.err.println("Erreur webhook Discord. Code HTTP : " + responseCode);
                InputStream errorStream = connection.getErrorStream();
                if (errorStream != null) {
                    String errorBody = readStream(errorStream);
                    System.err.println("Corps de la réponse d'erreur : " + errorBody);
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi vers le webhook Discord :");
            e.printStackTrace();
        }
    }


    // Lecture manuelle d'un InputStream (compatible Java 8)
    private static String readStream(InputStream is) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    // Protège les caractères spéciaux pour Discord/JSON
    private static String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
