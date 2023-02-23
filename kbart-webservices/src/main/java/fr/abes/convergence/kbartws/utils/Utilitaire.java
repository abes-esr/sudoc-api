package fr.abes.convergence.kbartws.utils;

public class Utilitaire {
    public static TYPE_ID getEnumFromString(String type) {
        return switch (type) {
            case "monograph" -> TYPE_ID.ISBN;
            case "serial" -> TYPE_ID.ISSN;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
