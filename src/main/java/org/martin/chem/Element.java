package org.martin.chem;

/**
 * 化学元素数据类
 */
public class Element {
    public final int atomicNumber;
    public final String symbol;
    public final String nameEn;
    public final String nameCn;
    public final String nameLatin;
    public final double atomicMass;
    public final String electronConfig;
    public final String category;
    public final int period;
    public final int group;

    public Element(int atomicNumber, String symbol, String nameEn, String nameCn, String nameLatin,
                   double atomicMass, String electronConfig, String category,
                   int period, int group) {
        this.atomicNumber = atomicNumber;
        this.symbol = symbol;
        this.nameEn = nameEn;
        this.nameCn = nameCn;
        this.nameLatin = nameLatin;
        this.atomicMass = atomicMass;
        this.electronConfig = electronConfig;
        this.category = category;
        this.period = period;
        this.group = group;
    }
}
