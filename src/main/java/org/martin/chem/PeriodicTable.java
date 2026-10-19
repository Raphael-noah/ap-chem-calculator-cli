package org.martin.chem;

import java.util.*;

/**
 * 元素周期表数据与查询（含拉丁语名称）
 */
public class PeriodicTable {
    private static final List<Element> ELEMENTS = new ArrayList<>();
    private static final Map<String, Element> BY_SYMBOL = new HashMap<>();
    private static final Map<Integer, Element> BY_NUMBER = new HashMap<>();
    private static final Map<String, Element> BY_NAME = new HashMap<>();

    private static final int[][] GRID_POSITIONS = new int[9][18];

    static {
        for (int[] row : GRID_POSITIONS) Arrays.fill(row, -1);

        // Z, sym, English, 中文, Latin, mass, eConfig, category, period, group
        add(1,"H","Hydrogen","氢","Hydrogenium",1.008,"1s1","非金属",1,1);
        add(2,"He","Helium","氦","Helium",4.003,"1s2","稀有气体",1,18);
        add(3,"Li","Lithium","锂","Lithium",6.941,"[He]2s1","碱金属",2,1);
        add(4,"Be","Beryllium","铍","Beryllium",9.012,"[He]2s2","碱土金属",2,2);
        add(5,"B","Boron","硼","Borum",10.81,"[He]2s2 2p1","类金属",2,13);
        add(6,"C","Carbon","碳","Carboneum",12.01,"[He]2s2 2p2","非金属",2,14);
        add(7,"N","Nitrogen","氮","Nitrogenium",14.01,"[He]2s2 2p3","非金属",2,15);
        add(8,"O","Oxygen","氧","Oxygenium",16.00,"[He]2s2 2p4","非金属",2,16);
        add(9,"F","Fluorine","氟","Fluorum",19.00,"[He]2s2 2p5","卤素",2,17);
        add(10,"Ne","Neon","氖","Neon",20.18,"[He]2s2 2p6","稀有气体",2,18);
        add(11,"Na","Sodium","钠","Natrium",22.99,"[Ne]3s1","碱金属",3,1);
        add(12,"Mg","Magnesium","镁","Magnesium",24.31,"[Ne]3s2","碱土金属",3,2);
        add(13,"Al","Aluminum","铝","Aluminium",26.98,"[Ne]3s2 3p1","后过渡金属",3,13);
        add(14,"Si","Silicon","硅","Silicium",28.09,"[Ne]3s2 3p2","类金属",3,14);
        add(15,"P","Phosphorus","磷","Phosphorus",30.97,"[Ne]3s2 3p3","非金属",3,15);
        add(16,"S","Sulfur","硫","Sulphur",32.07,"[Ne]3s2 3p4","非金属",3,16);
        add(17,"Cl","Chlorine","氯","Chlorum",35.45,"[Ne]3s2 3p5","卤素",3,17);
        add(18,"Ar","Argon","氩","Argon",39.95,"[Ne]3s2 3p6","稀有气体",3,18);
        add(19,"K","Potassium","钾","Kalium",39.10,"[Ar]4s1","碱金属",4,1);
        add(20,"Ca","Calcium","钙","Calcium",40.08,"[Ar]4s2","碱土金属",4,2);
        add(21,"Sc","Scandium","钪","Scandium",44.96,"[Ar]3d1 4s2","过渡金属",4,3);
        add(22,"Ti","Titanium","钛","Titanium",47.87,"[Ar]3d2 4s2","过渡金属",4,4);
        add(23,"V","Vanadium","钒","Vanadium",50.94,"[Ar]3d3 4s2","过渡金属",4,5);
        add(24,"Cr","Chromium","铬","Chromium",52.00,"[Ar]3d5 4s1","过渡金属",4,6);
        add(25,"Mn","Manganese","锰","Manganum",54.94,"[Ar]3d5 4s2","过渡金属",4,7);
        add(26,"Fe","Iron","铁","Ferrum",55.85,"[Ar]3d6 4s2","过渡金属",4,8);
        add(27,"Co","Cobalt","钴","Cobaltum",58.93,"[Ar]3d7 4s2","过渡金属",4,9);
        add(28,"Ni","Nickel","镍","Niccolum",58.69,"[Ar]3d8 4s2","过渡金属",4,10);
        add(29,"Cu","Copper","铜","Cuprum",63.55,"[Ar]3d10 4s1","过渡金属",4,11);
        add(30,"Zn","Zinc","锌","Zincum",65.38,"[Ar]3d10 4s2","过渡金属",4,12);
        add(31,"Ga","Gallium","镓","Gallium",69.72,"[Ar]3d10 4s2 4p1","后过渡金属",4,13);
        add(32,"Ge","Germanium","锗","Germanium",72.63,"[Ar]3d10 4s2 4p2","类金属",4,14);
        add(33,"As","Arsenic","砷","Arsenicum",74.92,"[Ar]3d10 4s2 4p3","类金属",4,15);
        add(34,"Se","Selenium","硒","Selenium",78.97,"[Ar]3d10 4s2 4p4","非金属",4,16);
        add(35,"Br","Bromine","溴","Bromium",79.90,"[Ar]3d10 4s2 4p5","卤素",4,17);
        add(36,"Kr","Krypton","氪","Krypton",83.80,"[Ar]3d10 4s2 4p6","稀有气体",4,18);
        add(37,"Rb","Rubidium","铷","Rubidium",85.47,"[Kr]5s1","碱金属",5,1);
        add(38,"Sr","Strontium","锶","Strontium",87.62,"[Kr]5s2","碱土金属",5,2);
        add(39,"Y","Yttrium","钇","Yttrium",88.91,"[Kr]4d1 5s2","过渡金属",5,3);
        add(40,"Zr","Zirconium","锆","Zirconium",91.22,"[Kr]4d2 5s2","过渡金属",5,4);
        add(41,"Nb","Niobium","铌","Niobium",92.91,"[Kr]4d4 5s1","过渡金属",5,5);
        add(42,"Mo","Molybdenum","钼","Molybdaenum",95.95,"[Kr]4d5 5s1","过渡金属",5,6);
        add(43,"Tc","Technetium","锝","Technetium",98.00,"[Kr]4d5 5s2","过渡金属",5,7);
        add(44,"Ru","Ruthenium","钌","Ruthenium",101.1,"[Kr]4d7 5s1","过渡金属",5,8);
        add(45,"Rh","Rhodium","铑","Rhodium",102.9,"[Kr]4d8 5s1","过渡金属",5,9);
        add(46,"Pd","Palladium","钯","Palladium",106.4,"[Kr]4d10","过渡金属",5,10);
        add(47,"Ag","Silver","银","Argentum",107.9,"[Kr]4d10 5s1","过渡金属",5,11);
        add(48,"Cd","Cadmium","镉","Cadmium",112.4,"[Kr]4d10 5s2","过渡金属",5,12);
        add(49,"In","Indium","铟","Indium",114.8,"[Kr]4d10 5s2 5p1","后过渡金属",5,13);
        add(50,"Sn","Tin","锡","Stannum",118.7,"[Kr]4d10 5s2 5p2","后过渡金属",5,14);
        add(51,"Sb","Antimony","锑","Stibium",121.8,"[Kr]4d10 5s2 5p3","类金属",5,15);
        add(52,"Te","Tellurium","碲","Tellurium",127.6,"[Kr]4d10 5s2 5p4","类金属",5,16);
        add(53,"I","Iodine","碘","Iodium",126.9,"[Kr]4d10 5s2 5p5","卤素",5,17);
        add(54,"Xe","Xenon","氙","Xenon",131.3,"[Kr]4d10 5s2 5p6","稀有气体",5,18);
        add(55,"Cs","Cesium","铯","Caesium",132.9,"[Xe]6s1","碱金属",6,1);
        add(56,"Ba","Barium","钡","Barium",137.3,"[Xe]6s2","碱土金属",6,2);
        // Lanthanides (row 8 in grid)
        add(57,"La","Lanthanum","镧","Lanthanum",138.9,"[Xe]5d1 6s2","镧系",8,3);
        add(58,"Ce","Cerium","铈","Cerium",140.1,"[Xe]4f1 5d1 6s2","镧系",8,4);
        add(59,"Pr","Praseodymium","镨","Praseodymium",140.9,"[Xe]4f3 6s2","镧系",8,5);
        add(60,"Nd","Neodymium","钕","Neodymium",144.2,"[Xe]4f4 6s2","镧系",8,6);
        add(61,"Pm","Promethium","钷","Promethium",145.0,"[Xe]4f5 6s2","镧系",8,7);
        add(62,"Sm","Samarium","钐","Samarium",150.4,"[Xe]4f6 6s2","镧系",8,8);
        add(63,"Eu","Europium","铕","Europium",152.0,"[Xe]4f7 6s2","镧系",8,9);
        add(64,"Gd","Gadolinium","钆","Gadolinium",157.3,"[Xe]4f7 5d1 6s2","镧系",8,10);
        add(65,"Tb","Terbium","铽","Terbium",158.9,"[Xe]4f9 6s2","镧系",8,11);
        add(66,"Dy","Dysprosium","镝","Dysprosium",162.5,"[Xe]4f10 6s2","镧系",8,12);
        add(67,"Ho","Holmium","钬","Holmium",164.9,"[Xe]4f11 6s2","镧系",8,13);
        add(68,"Er","Erbium","铒","Erbium",167.3,"[Xe]4f12 6s2","镧系",8,14);
        add(69,"Tm","Thulium","铥","Thulium",168.9,"[Xe]4f13 6s2","镧系",8,15);
        add(70,"Yb","Ytterbium","镱","Ytterbium",173.0,"[Xe]4f14 6s2","镧系",8,16);
        add(71,"Lu","Lutetium","镥","Lutetium",175.0,"[Xe]4f14 5d1 6s2","镧系",8,17);
        add(72,"Hf","Hafnium","铪","Hafnium",178.5,"[Xe]4f14 5d2 6s2","过渡金属",6,4);
        add(73,"Ta","Tantalum","钽","Tantalum",180.9,"[Xe]4f14 5d3 6s2","过渡金属",6,5);
        add(74,"W","Tungsten","钨","Wolfram",183.8,"[Xe]4f14 5d4 6s2","过渡金属",6,6);
        add(75,"Re","Rhenium","铼","Rhenium",186.2,"[Xe]4f14 5d5 6s2","过渡金属",6,7);
        add(76,"Os","Osmium","锇","Osmium",190.2,"[Xe]4f14 5d6 6s2","过渡金属",6,8);
        add(77,"Ir","Iridium","铱","Iridium",192.2,"[Xe]4f14 5d7 6s2","过渡金属",6,9);
        add(78,"Pt","Platinum","铂","Platinum",195.1,"[Xe]4f14 5d9 6s1","过渡金属",6,10);
        add(79,"Au","Gold","金","Aurum",197.0,"[Xe]4f14 5d10 6s1","过渡金属",6,11);
        add(80,"Hg","Mercury","汞","Hydrargyrum",200.6,"[Xe]4f14 5d10 6s2","过渡金属",6,12);
        add(81,"Tl","Thallium","铊","Thallium",204.4,"[Xe]4f14 5d10 6s2 6p1","后过渡金属",6,13);
        add(82,"Pb","Lead","铅","Plumbum",207.2,"[Xe]4f14 5d10 6s2 6p2","后过渡金属",6,14);
        add(83,"Bi","Bismuth","铋","Bisemutum",209.0,"[Xe]4f14 5d10 6s2 6p3","后过渡金属",6,15);
        add(84,"Po","Polonium","钋","Polonium",209.0,"[Xe]4f14 5d10 6s2 6p4","后过渡金属",6,16);
        add(85,"At","Astatine","砹","Astatium",210.0,"[Xe]4f14 5d10 6s2 6p5","卤素",6,17);
        add(86,"Rn","Radon","氡","Radon",222.0,"[Xe]4f14 5d10 6s2 6p6","稀有气体",6,18);
        add(87,"Fr","Francium","钫","Francium",223.0,"[Rn]7s1","碱金属",7,1);
        add(88,"Ra","Radium","镭","Radium",226.0,"[Rn]7s2","碱土金属",7,2);
        // Actinides (row 9 in grid)
        add(89,"Ac","Actinium","锕","Actinium",227.0,"[Rn]6d1 7s2","锕系",9,3);
        add(90,"Th","Thorium","钍","Thorium",232.0,"[Rn]6d2 7s2","锕系",9,4);
        add(91,"Pa","Protactinium","镤","Protactinium",231.0,"[Rn]5f2 6d1 7s2","锕系",9,5);
        add(92,"U","Uranium","铀","Uranium",238.0,"[Rn]5f3 6d1 7s2","锕系",9,6);
        add(93,"Np","Neptunium","镎","Neptunium",237.0,"[Rn]5f4 6d1 7s2","锕系",9,7);
        add(94,"Pu","Plutonium","钚","Plutonium",244.0,"[Rn]5f6 7s2","锕系",9,8);
        add(95,"Am","Americium","镅","Americium",243.0,"[Rn]5f7 7s2","锕系",9,9);
        add(96,"Cm","Curium","锔","Curium",247.0,"[Rn]5f7 6d1 7s2","锕系",9,10);
        add(97,"Bk","Berkelium","锫","Berkelium",247.0,"[Rn]5f9 7s2","锕系",9,11);
        add(98,"Cf","Californium","锎","Californium",251.0,"[Rn]5f10 7s2","锕系",9,12);
        add(99,"Es","Einsteinium","锿","Einsteinium",252.0,"[Rn]5f11 7s2","锕系",9,13);
        add(100,"Fm","Fermium","镄","Fermium",257.0,"[Rn]5f12 7s2","锕系",9,14);
        add(101,"Md","Mendelevium","钔","Mendelevium",258.0,"[Rn]5f13 7s2","锕系",9,15);
        add(102,"No","Nobelium","锘","Nobelium",259.0,"[Rn]5f14 7s2","锕系",9,16);
        add(103,"Lr","Lawrencium","铹","Lawrencium",266.0,"[Rn]5f14 7s2 7p1","锕系",9,17);
        add(104,"Rf","Rutherfordium","𬬻","Rutherfordium",267.0,"[Rn]5f14 6d2 7s2","过渡金属",7,4);
        add(105,"Db","Dubnium","𬭊","Dubnium",268.0,"[Rn]5f14 6d3 7s2","过渡金属",7,5);
        add(106,"Sg","Seaborgium","𬭳","Seaborgium",269.0,"[Rn]5f14 6d4 7s2","过渡金属",7,6);
        add(107,"Bh","Bohrium","𬭛","Bohrium",270.0,"[Rn]5f14 6d5 7s2","过渡金属",7,7);
        add(108,"Hs","Hassium","𬭶","Hassium",277.0,"[Rn]5f14 6d6 7s2","过渡金属",7,8);
        add(109,"Mt","Meitnerium","鿏","Meitnerium",278.0,"[Rn]5f14 6d7 7s2","未知",7,9);
        add(110,"Ds","Darmstadtium","𫟼","Darmstadtium",281.0,"[Rn]5f14 6d8 7s2","未知",7,10);
        add(111,"Rg","Roentgenium","𬬭","Roentgenium",282.0,"[Rn]5f14 6d9 7s2","未知",7,11);
        add(112,"Cn","Copernicium","鿔","Copernicium",285.0,"[Rn]5f14 6d10 7s2","过渡金属",7,12);
        add(113,"Nh","Nihonium","鿭","Nihonium",286.0,"[Rn]5f14 6d10 7s2 7p1","未知",7,13);
        add(114,"Fl","Flerovium","𫓧","Flerovium",289.0,"[Rn]5f14 6d10 7s2 7p2","后过渡金属",7,14);
        add(115,"Mc","Moscovium","镆","Moscovium",290.0,"[Rn]5f14 6d10 7s2 7p3","未知",7,15);
        add(116,"Lv","Livermorium","𫟷","Livermorium",293.0,"[Rn]5f14 6d10 7s2 7p4","未知",7,16);
        add(117,"Ts","Tennessine","鿬","Tennessine",294.0,"[Rn]5f14 6d10 7s2 7p5","未知",7,17);
        add(118,"Og","Oganesson","鿫","Oganesson",294.0,"[Rn]5f14 6d10 7s2 7p6","未知",7,18);
    }

    private static void add(int num, String sym, String nameEn, String nameCn, String nameLatin,
                            double mass, String eConfig, String cat, int period, int group) {
        Element e = new Element(num, sym, nameEn, nameCn, nameLatin, mass, eConfig, cat, period, group);
        ELEMENTS.add(e);
        BY_SYMBOL.put(sym.toLowerCase(), e);
        BY_NUMBER.put(num, e);
        BY_NAME.put(nameEn.toLowerCase(), e);
        BY_NAME.put(nameCn, e);
        BY_NAME.put(nameLatin.toLowerCase(), e);
        GRID_POSITIONS[period - 1][group - 1] = ELEMENTS.size() - 1;
    }

    public static List<Element> getAllElements() { return Collections.unmodifiableList(ELEMENTS); }

    public static Element getBySymbol(String symbol) {
        return BY_SYMBOL.get(symbol.toLowerCase());
    }

    public static Element getByNumber(int number) {
        return BY_NUMBER.get(number);
    }

    public static Element search(String query) {
        String q = query.trim().toLowerCase();
        if (BY_SYMBOL.containsKey(q)) return BY_SYMBOL.get(q);
        if (BY_NAME.containsKey(q)) return BY_NAME.get(q);
        try {
            int num = Integer.parseInt(q);
            return BY_NUMBER.get(num);
        } catch (NumberFormatException ex) {
            for (Element elem : ELEMENTS) {
                if (elem.nameEn.toLowerCase().contains(q)
                    || elem.nameCn.contains(q)
                    || elem.nameLatin.toLowerCase().contains(q)) return elem;
            }
            return null;
        }
    }

    public static Element getAtGrid(int period, int group) {
        if (period < 1 || period > 9 || group < 1 || group > 18) return null;
        int idx = GRID_POSITIONS[period - 1][group - 1];
        return idx >= 0 ? ELEMENTS.get(idx) : null;
    }
}
