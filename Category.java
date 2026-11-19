package beerlegit;

public enum Category {
    HUD("HUD"),
    VISUAL("Visual"),
    MISC("Misc");

    public final String label;
    Category(String label) { this.label = label; }
}
