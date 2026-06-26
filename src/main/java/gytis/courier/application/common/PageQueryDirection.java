package gytis.courier.application.common;

public enum PageQueryDirection {
    ASC,
    DESC;

    public static PageQueryDirection parseDirection(String dir) {
        if (dir == null || dir.isBlank()) {
            return PageQueryDirection.ASC;
        }

        try {
            return PageQueryDirection.valueOf(dir.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid sort direction " + dir);
        }
    }
}


