package ru.kamuzta.somafactorymanager.enums;

//determines paper sorts
public enum Paper {
    NTC44("NTC44", "Hansol", "NTC ST 44", 44.0f, 49.0f),
    NTC48("NTC48", "Hansol", "NTC ST 48", 48.0f, 52.0f),
    NTC55("NTC55", "Hansol", "NTC ST 55", 55.0f, 59.0f),
    NTC58("NTC58", "Hansol", "NTC STH 58", 58.0f, 71.0f);

    private String paperSKU;
    private String paperManufacturer;
    private String paperSort;
    private float paperweight;
    private float paperthickness;

    Paper(String paperSKU, String paperManufacturer, String paperSort, float paperweight, float paperthickness) {
        this.paperSKU = paperSKU;
        this.paperManufacturer = paperManufacturer;
        this.paperSort = paperSort;
        this.paperweight = paperweight;
        this.paperthickness = paperthickness;
    }

    public String getPaperSKU() {
        return paperSKU;
    }

    public String getPaperManufacturer() {
        return paperManufacturer;
    }

    public String getPaperSort() {
        return paperSort;
    }

    public float getPaperweight() {
        return paperweight;
    }

    public float getPaperthickness() {
        return paperthickness;
    }

    @Override
    public String toString() {
        return getPaperManufacturer() + " " + getPaperSort();
    }
}
