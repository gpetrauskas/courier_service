package gytis.courier.application.common;

public enum PageQueryDirection {
    ASC,
    DESC;

    public static PageQueryDirection parseDirection(String dir) {
        return dir.equalsIgnoreCase("ASC") ?
                ASC : DESC;
    }
}


