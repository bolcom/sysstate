package nl.unionsoft.sysstate.dto;

public class MessageDto {

    public final static String RED = "red";
    public final static String GREEN = "green";
    public final static String YELLOW = "yellow";
    public final static String BLUE = "blue";

    private final String text;
    private final String color;

    public MessageDto (String text, String color) {
        this.text = text;
        this.color = color;
    }

    public MessageDto (String text, String color, Exception e) {
        this(text, color);
    }

    public String getText() {
        return text;
    }

    public String getColor() {
        return color;
    }

}
