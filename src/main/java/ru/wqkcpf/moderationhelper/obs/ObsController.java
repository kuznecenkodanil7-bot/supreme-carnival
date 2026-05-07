package ru.wqkcpf.moderationhelper.obs;

import ru.wqkcpf.moderationhelper.ModerationHelperClient;
import ru.wqkcpf.moderationhelper.config.ModConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ObsController {
    private final ModConfig config;
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    private volatile WebSocket socket;
    private volatile boolean identified;

    public ObsController(ModConfig config) {
        this.config = config;
    }

    public void startRecording() {
        if (!config.obsEnabled) {
            ModerationHelperClient.chat("OBS-интеграция выключена в конфиге.");
            return;
        }
        if (request("StartRecord")) {
            ModerationHelperClient.RECORDING_TIMER.start();
        }
    }

    public void stopRecording() {
        // Таймер в интерфейсе должен исчезнуть даже если OBS недоступен или выключен в конфиге.
        ModerationHelperClient.RECORDING_TIMER.stop();
        if (!config.obsEnabled) return;
        request("StopRecord");
    }

    private synchronized boolean request(String requestType) {
        try {
            WebSocket ws = ensureConnected();
            if (ws == null) return false;
            String id = "mhg-" + requestType + "-" + System.currentTimeMillis();
            ws.sendText("{\"op\":6,\"d\":{\"requestType\":\"" + requestType + "\",\"requestId\":\"" + id + "\"}}", true);
            return true;
        } catch (Exception e) {
            ModerationHelperClient.LOGGER.warn("OBS request failed: {}", requestType, e);
            ModerationHelperClient.chat("OBS недоступен: " + e.getMessage());
            return false;
        }
    }

    private WebSocket ensureConnected() throws Exception {
        if (socket != null && !socket.isOutputClosed() && identified) return socket;
        identified = false;
        String uri = "ws://" + config.obsHost + ":" + config.obsPort;
        ObsListener listener = new ObsListener();
        socket = httpClient.newWebSocketBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .buildAsync(URI.create(uri), listener)
                .get(4, TimeUnit.SECONDS);
        listener.awaitHelloAndIdentify(socket);
        return socket;
    }

    private final class ObsListener implements WebSocket.Listener {
        private final StringBuilder buffer = new StringBuilder();
        private volatile String hello;

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            buffer.append(data);
            if (last) {
                String text = buffer.toString();
                buffer.setLength(0);
                if (text.contains("\"op\":0")) hello = text;
                if (text.contains("\"op\":2")) identified = true;
            }
            webSocket.request(1);
            return null;
        }

        void awaitHelloAndIdentify(WebSocket ws) throws Exception {
            long until = System.currentTimeMillis() + 2500L;
            while (hello == null && System.currentTimeMillis() < until) Thread.sleep(25L);
            String auth = buildAuth(hello, config.obsPassword);
            String payload = auth == null || auth.isBlank()
                    ? "{\"op\":1,\"d\":{\"rpcVersion\":1}}"
                    : "{\"op\":1,\"d\":{\"rpcVersion\":1,\"authentication\":\"" + json(auth) + "\"}}";
            ws.sendText(payload, true);
            until = System.currentTimeMillis() + 2500L;
            while (!identified && System.currentTimeMillis() < until) Thread.sleep(25L);
            if (!identified) {
                throw new IllegalStateException("OBS websocket не подтвердил авторизацию");
            }
        }
    }

    private static String buildAuth(String hello, String password) throws Exception {
        if (password == null || password.isBlank() || hello == null) return null;
        String salt = extract(hello, "salt");
        String challenge = extract(hello, "challenge");
        if (salt == null || challenge == null) return null;
        String secret = sha256Base64(password + salt);
        return sha256Base64(secret + challenge);
    }

    private static String sha256Base64(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    private static String extract(String json, String key) {
        Matcher matcher = Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*\\\"(.*?)\\\"").matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static String json(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
