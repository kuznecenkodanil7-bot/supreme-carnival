package ru.wqkcpf.moderationhelper.config;

import net.fabricmc.loader.api.FabricLoader;
import ru.wqkcpf.moderationhelper.ModerationHelperClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ModConfig {
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("moderation_helper_gui.json");

    public boolean obsEnabled = true;
    public String obsHost = "localhost";
    public int obsPort = 4455;
    public String obsPassword = "";

    public int recentPlayersLimit = 15;
    public CleanupMode screenshotCleanupMode = CleanupMode.ARCHIVE;
    public int screenshotRetentionDays = 30;
    public String screenshotFolder = "moderation_screenshots";

    public String checkCommandTemplate = "/check {nick}";
    public String tppCommandTemplate = "/tpp {nick}";
    public String tpCommandTemplate = "/tp {nick}";
    public String checkTellTemplate = "/tell {nick} Здравствуйте, проверка на читы. В течении 5 минут жду ваш Anydesk (наилучший вариант, скачать можно в любом браузере)/Discord. Также сообщаю, что в случае признания на наличие чит-клиентов срок бана составит 20 дней, вместо 30.";

    public List<String> quickCustomReasons = new ArrayList<>(List.of("2.2", "2.3", "3.7", "3.8", "бот", "уход от проверки", "признание"));

    public static ModConfig load() {
        ModConfig config = new ModConfig();
        try {
            if (!Files.exists(PATH)) {
                config.save();
                return config;
            }
            String json = Files.readString(PATH, StandardCharsets.UTF_8);
            config.obsEnabled = readBoolean(json, "obsEnabled", config.obsEnabled);
            config.obsHost = readString(json, "obsHost", config.obsHost);
            config.obsPort = readInt(json, "obsPort", config.obsPort);
            config.obsPassword = readString(json, "obsPassword", config.obsPassword);
            config.recentPlayersLimit = readInt(json, "recentPlayersLimit", config.recentPlayersLimit);
            config.screenshotCleanupMode = CleanupMode.fromString(readString(json, "screenshotCleanupMode", config.screenshotCleanupMode.name()));
            config.screenshotRetentionDays = readInt(json, "screenshotRetentionDays", config.screenshotRetentionDays);
            config.screenshotFolder = readString(json, "screenshotFolder", config.screenshotFolder);
            config.checkCommandTemplate = readString(json, "checkCommandTemplate", config.checkCommandTemplate);
            config.tppCommandTemplate = readString(json, "tppCommandTemplate", config.tppCommandTemplate);
            config.tpCommandTemplate = readString(json, "tpCommandTemplate", config.tpCommandTemplate);
            config.checkTellTemplate = readString(json, "checkTellTemplate", config.checkTellTemplate);
            config.quickCustomReasons = readStringList(json, "quickCustomReasons", config.quickCustomReasons);
        } catch (Exception e) {
            ModerationHelperClient.LOGGER.error("Cannot load config, defaults will be used", e);
        }
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, toJson(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            ModerationHelperClient.LOGGER.error("Cannot save config", e);
        }
    }

    private String toJson() {
        return "{\n" +
                "  \"obsEnabled\": " + obsEnabled + ",\n" +
                "  \"obsHost\": \"" + esc(obsHost) + "\",\n" +
                "  \"obsPort\": " + obsPort + ",\n" +
                "  \"obsPassword\": \"" + esc(obsPassword) + "\",\n" +
                "  \"recentPlayersLimit\": " + recentPlayersLimit + ",\n" +
                "  \"screenshotCleanupMode\": \"" + screenshotCleanupMode.name() + "\",\n" +
                "  \"screenshotRetentionDays\": " + screenshotRetentionDays + ",\n" +
                "  \"screenshotFolder\": \"" + esc(screenshotFolder) + "\",\n" +
                "  \"checkCommandTemplate\": \"" + esc(checkCommandTemplate) + "\",\n" +
                "  \"tppCommandTemplate\": \"" + esc(tppCommandTemplate) + "\",\n" +
                "  \"tpCommandTemplate\": \"" + esc(tpCommandTemplate) + "\",\n" +
                "  \"checkTellTemplate\": \"" + esc(checkTellTemplate) + "\",\n" +
                "  \"quickCustomReasons\": [" + toJsonArray(quickCustomReasons) + "]\n" +
                "}\n";
    }

    private static String toJsonArray(List<String> values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("\"").append(esc(values.get(i))).append("\"");
        }
        return sb.toString();
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }

    private static boolean readBoolean(String json, String key, boolean def) {
        Matcher m = Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE).matcher(json);
        return m.find() ? Boolean.parseBoolean(m.group(1)) : def;
    }

    private static int readInt(String json, String key, int def) {
        Matcher m = Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*(-?\\d+)").matcher(json);
        return m.find() ? Integer.parseInt(m.group(1)) : def;
    }

    private static String readString(String json, String key, String def) {
        Matcher m = Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*\\\"(.*?)\\\"", Pattern.DOTALL).matcher(json);
        return m.find() ? unesc(m.group(1)) : def;
    }

    private static List<String> readStringList(String json, String key, List<String> def) {
        Matcher m = Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL).matcher(json);
        if (!m.find()) return def;
        List<String> out = new ArrayList<>();
        Matcher item = Pattern.compile("\\\"(.*?)\\\"").matcher(m.group(1));
        while (item.find()) out.add(unesc(item.group(1)));
        return out.isEmpty() ? def : out;
    }

    private static String unesc(String s) {
        return s.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
