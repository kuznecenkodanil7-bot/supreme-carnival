package ru.wqkcpf.moderationhelper.punishment;

import java.util.ArrayList;
import java.util.List;

public final class RuleRegistry {
    private RuleRegistry() {}

    public static List<RuleReason> reasonsFor(PunishmentType type) {
        return switch (type) {
            case WARN -> List.of(new RuleReason("2.1", "предупреждение", List.of(""), false));
            case MUTE -> muteReasons();
            case BAN -> banReasons();
            case IPBAN -> ipBanReasons();
        };
    }

    private static List<RuleReason> muteReasons() {
        List<RuleReason> r = new ArrayList<>();
        r.add(new RuleReason("2.2", "оскорбления", List.of("1h", "2h", "6h", "12h", "1d", "3d", "7d"), false));
        r.add(new RuleReason("2.3", "оскорбление родных", List.of("1d", "3d", "5d", "15d"), false));
        r.add(new RuleReason("2.4", "сообщения сексуального характера", List.of("2h"), false));
        r.add(new RuleReason("2.5", "неадекватное поведение", List.of("2h"), false));
        r.add(new RuleReason("2.6", "реклама серверов/ресурсов", List.of("1d"), false));
        r.add(new RuleReason("2.7", "пропаганда/агитация ненависти", List.of("9h"), false));
        r.add(new RuleReason("2.8", "ссылки/реклама стримов и видео", List.of("8h", "12h", "1d", "3d"), false));
        r.add(new RuleReason("2.9", "выдача себя за администратора", List.of("12h"), false));
        r.add(new RuleReason("2.10", "угрозы не по игровому процессу", List.of("12h"), false));
        r.add(new RuleReason("2.11", "угроза наказанием без причины", List.of("6h"), false));
        r.add(new RuleReason("2.12", "обсуждение политики", List.of("12h", "1d", "3d", "7d"), false));
        r.add(new RuleReason("2.13", "введение игроков в заблуждение", List.of("2h"), false));
        r.add(new RuleReason("2.14", "попрошайничество", List.of("6h"), false));
        r.add(new RuleReason("2.15", "помехи в голосовом чате", List.of("4h"), false));
        return r;
    }

    private static List<RuleReason> banReasons() {
        List<RuleReason> r = new ArrayList<>();
        r.add(new RuleReason("2.2", "оскорбления", List.of("2d", "1d", "3d", "7d"), false));
        r.add(new RuleReason("2.3", "оскорбление родных", List.of("3d", "7d"), false));
        r.add(new RuleReason("2.6", "реклама серверов/ресурсов", List.of("1d", "3d", "7d", "14d"), false));
        r.add(new RuleReason("2.7", "пропаганда/агитация ненависти", List.of("3d"), false));
        r.add(new RuleReason("3.1", "запрещённый никнейм", List.of("perm"), true));
        r.add(new RuleReason("4.1", "донатер без доказательств", List.of("5d", "15d", "20d", "30d"), false));
        return r;
    }

    private static List<RuleReason> ipBanReasons() {
        List<RuleReason> r = new ArrayList<>();
        r.add(new RuleReason("бот", "бот", List.of("perm"), true));
        r.add(new RuleReason("уход от проверки", "уход от проверки", List.of("30d"), false));
        r.add(new RuleReason("время вышло", "время вышло", List.of("30d"), false));
        r.add(new RuleReason("неадекватное поведение во время проверки", "неадекватное поведение во время проверки", List.of("30d"), false));
        r.add(new RuleReason("признание", "признание", List.of("20d"), false));
        r.add(new RuleReason("3.3", "торговля за реальные деньги", List.of("30d"), false));
        r.add(new RuleReason("3.6", "баги сервера/лаг-машины", List.of("1d", "3d", "7d", "15d"), false));
        r.add(new RuleReason("3.7", "стороннее ПО", List.of("30d", "20d", "15d", "7d"), false));
        r.add(new RuleReason("3.8", "обход бана за читы", List.of("30d"), false));
        r.add(new RuleReason("3.9", "подстрекательство", List.of("3d"), false));
        r.add(new RuleReason("3.10", "тим с читером", List.of("15d"), false));
        return r;
    }
}
