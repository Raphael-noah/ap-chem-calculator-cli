package org.martin.chem;



import org.martin.calc.SigFigCalculator;

import java.util.*;



/**

 * AP化学公式库 — 涵盖热力学、动力学、电化学、溶液、气体等全部核心公式。

 * 所有计算通过 SigFigCalculator 引擎，自动保留有效数字。

 */

public class ChemFormulas {



    // ═══ 物理常数 ═══

    static final String R_L   = "0.08206";     // L·atm/(mol·K)

    static final String R_J   = "8.314";        // J/(mol·K)

    static final String F_CONST = "96485";      // C/mol (Faraday)

    static final String KB    = "1.380649e-23"; // J/K (Boltzmann)



    // ═══ 公式定义 ═══

    public static class Formula {

        public final String name;
        public final String equation;
        public final String[] variables;
        public final String[] descriptions;
        public final String category;
        public String notes = "";
        public String detailedInfo = "";
        final Map<String, String> solveForms = new LinkedHashMap<>();

        Formula(String name, String equation, String[] vars, String[] descs, String cat) {
            this.name = name; this.equation = equation;
            this.variables = vars; this.descriptions = descs;
            this.category = cat;
        }
        Formula form(String unknown, String expr) {
            solveForms.put(unknown, expr); return this;
        }
        Formula note(String n) { this.notes = n; return this; }
        Formula info(String d) { this.detailedInfo = d; return this; }
    }



    public static class SolveResult {

        public final boolean success;

        public final String unknown, value, steps, error;

        SolveResult(String u, String v, String s) { success=true; unknown=u; value=v; steps=s; error=null; }

        SolveResult(String error) { success=false; unknown=null; value=null; steps=null; this.error=error; }

    }



    private static final List<Formula> FORMULAS = new ArrayList<>();



    static {

        // ═══════════════════════════════════════

        //  热力学

        // ═══════════════════════════════════════



        // 1. 吉布斯自由能

        FORMULAS.add(new Formula("吉布斯自由能", "ΔG = ΔH - TΔS",

            new String[]{"dG", "dH", "T", "dS"},

            new String[]{"ΔG (kJ/mol)", "ΔH (kJ/mol)", "温度 T (K)", "ΔS (kJ/(mol·K))"}, "热力学/吉布斯自由能")

            .form("dG", "dH - T * dS")

            .form("dH", "dG + T * dS")

            .form("T", "(dH - dG) / dS")

            .form("dS", "(dH - dG) / T")

            .note("判断反应自发性。ΔG<0自发，ΔG=0平衡，ΔG>0非自发。注意ΔH和ΔS单位统一。")
            .info("""
                    【历史】1876年，美国物理学家 Josiah Willard Gibbs 提出自由能概念，
                    奠定了化学热力学第二定律的核心。
                    【变量详解】
                      ΔG: Gibbs自由能变化(kJ/mol)。决定反应方向的判据。
                      ΔH: 焓变(kJ/mol)。吸热为正，放热为负。
                      T: 热力学温度(K)，必须用开尔文！
                      ΔS: 熵变(J/(mol·K))。注意单位与ΔH统一！
                    【四种情况】
                      ΔH<0, ΔS>0: 任何温度自发 (ΔG<0)
                      ΔH>0, ΔS<0: 任何温度非自发 (ΔG>0)
                      ΔH<0, ΔS<0: 低温自发 (|TΔS|<|ΔH|)
                      ΔH>0, ΔS>0: 高温自发 (TΔS>ΔH)
                    【注意】ΔH和ΔS单位要统一(kJ或J)！"""));



        // 2. 标准吉布斯自由能

        FORMULAS.add(new Formula("标准ΔG与K", "ΔG° = -RT ln K",

            new String[]{"dG", "T", "K"},

            new String[]{"ΔG° (J/mol)", "温度 T (K)", "平衡常数 K"}, "热力学/吉布斯自由能")

            .form("dG", "-1 * " + R_J + " * T * ln(K)")

            .form("T", "dG / (-1 * " + R_J + " * ln(K))")

            .form("K", "exp(dG / (-1 * " + R_J + " * T))")

            .note("由标准吉布斯自由能求平衡常数。K>1产物占优，K<1反应物占优。")
            .info("""
                    【历史】由Gibbs自由能与化学平衡常数K的关联推导。
                    【变量详解】
                      ΔG°: 标准Gibbs自由能变化(J/mol)
                      T: 温度(K)
                      K: 平衡常数(无单位)
                    【适用条件】标准态(P=1atm, c=1mol/L)
                    【要点】
                      ΔG°<0 → K>1: 平衡时产物占优
                      ΔG°>0 → K<1: 平衡时反应物占优
                      ΔG°=0 → K=1"""));



        // 3. 非标准吉布斯自由能

        FORMULAS.add(new Formula("非标准ΔG", "ΔG = ΔG° + RT ln Q",

            new String[]{"dG", "dG0", "T", "Q"},

            new String[]{"ΔG (J/mol)", "ΔG° (J/mol)", "温度 T (K)", "反应商 Q"}, "热力学/吉布斯自由能")

            .form("dG", "dG0 + " + R_J + " * T * ln(Q)")

            .form("dG0", "dG - " + R_J + " * T * ln(Q)")

            .form("T", "(dG - dG0) / (" + R_J + " * ln(Q))")

            .form("Q", "exp((dG - dG0) / (" + R_J + " * T))")

            .note("非标准态下判断反应方向。Q<K正向进行，Q>K逆向进行，Q=K平衡。")
            .info("""
                    【变量】ΔG: 非标准态自由能变化, ΔG°: 标准态自由能变化, Q: 反应商
                    【判据】
                      Q < K → ΔG < 0 → 正向自发
                      Q > K → ΔG > 0 → 逆向自发
                      Q = K → ΔG = 0 → 平衡
                    【Q的表达式】与K相同，但用即时浓度(非平衡浓度)"""));



        // 4. 范特霍夫方程

        FORMULAS.add(new Formula("范特霍夫", "ln(K2/K1) = (-ΔH°/R)(1/T2 - 1/T1)",

            new String[]{"K1", "K2", "dH", "T1", "T2"},

            new String[]{"K1 (T1时)", "K2 (T2时)", "ΔH° (J/mol)", "T1 (K)", "T2 (K)"}, "热力学/吉布斯自由能")

            .form("K2", "K1 * exp(-1 * dH / " + R_J + " * (1/T2 - 1/T1))")

            .form("K1", "K2 / exp(-1 * dH / " + R_J + " * (1/T2 - 1/T1))")

            .form("dH", "ln(K2 / K1) * " + R_J + " * (-1) / (1/T2 - 1/T1)")

            .form("T2", "1 / (1/T1 - ln(K2/K1) * " + R_J + " / dH)")

            .form("T1", "1 / (1/T2 + ln(K2/K1) * " + R_J + " / dH)")

            .note("已知两个温度的K求ΔH°，或预测温度对K的影响。吸热反应升温K增大。")
            .info("""
                    【历史】1884年 van't Hoff 推导。
                    【变量】K1(T1时), K2(T2时), ΔH°: 标准焓变(J/mol)
                    【Le Chatelier原理】
                      吸热反应(ΔH>0): 升温K增大
                      放热反应(ΔH<0): 升温K减小
                    【作图法】lnK 对 1/T 作图，斜率 = -ΔH°/R"""));



        // 5. 基尔霍夫定律

        FORMULAS.add(new Formula("基尔霍夫", "ΔH2 = ΔH1 + ΔCp(T2-T1)",

            new String[]{"dH1", "dH2", "dCp", "T1", "T2"},

            new String[]{"ΔH1 (kJ/mol)", "ΔH2 (kJ/mol)", "ΔCp (kJ/(mol·K))", "T1 (K)", "T2 (K)"}, "热力学/吉布斯自由能")

            .form("dH2", "dH1 + dCp * (T2 - T1)")

            .form("dH1", "dH2 - dCp * (T2 - T1)")

            .form("dCp", "(dH2 - dH1) / (T2 - T1)")

            .form("T1", "T2 - (dH2 - dH1) / dCp")

            .form("T2", "(dH2 - dH1) / dCp + T1"));



        // 6. 玻尔兹曼熵

        FORMULAS.add(new Formula("玻尔兹曼熵", "S = kB ln W",

            new String[]{"S", "W"},

            new String[]{"熵 S (J/K)", "微观态数 W"}, "热力学/吉布斯自由能")

            .form("S", KB + " * ln(W)")

            .form("W", "exp(S / " + KB + ")")

            .note("从微观态数目计算熵。W越大熵越大。kB = 1.381×10⁻²³ J/K。")
            .info("""
                    【历史】1877年 Boltzmann 提出。刻在他的墓碑上。
                    【变量】S: 熵(J/K), kB: Boltzmann常数(1.381×10⁻²³ J/K), W: 微观态数
                    【意义】从微观角度定义熵：系统可能的微观排列方式越多，熵越大
                    【示例】N个粒子分布在2个盒子中: W = 2^N"""));



        // ═══════════════════════════════════════

        //  动力学

        // ═══════════════════════════════════════



        // 7. 阿伦尼乌斯方程

        FORMULAS.add(new Formula("阿伦尼乌斯", "k = A exp(-Ea/RT)",

            new String[]{"k", "A", "Ea", "T"},

            new String[]{"速率常数 k", "指前因子 A", "活化能 Ea (J/mol)", "温度 T (K)"}, "动力学/Arrhenius/方程")

            .form("k", "A * exp(-1 * Ea / (" + R_J + " * T))")

            .form("A", "k / exp(-1 * Ea / (" + R_J + " * T))")

            .form("Ea", "-1 * " + R_J + " * T * ln(k / A)")

            .form("T", "-1 * Ea / (" + R_J + " * ln(k / A))")

            .note("求不同温度下的速率常数。Ea越大反应对温度越敏感。升温k增大。")
            .info("""
                    【历史】1889年 Svante Arrhenius 提出此方程，获1903年诺贝尔化学奖。
                    【变量详解】
                      k: 速率常数(单位取决于反应级数)
                      A: 指前因子(频率因子)，与k同单位
                      Ea: 活化能(J/mol)，反应需克服的能垒
                      T: 温度(K)
                      R: 气体常数 = 8.314 J/(mol·K)
                    【物理意义】
                      exp(-Ea/RT) = 能量超过Ea的分子所占比例
                    【经验规则】温度每升高10°C，k约增大2~4倍
                    【作图法】ln k 对 1/T 作图为直线，斜率 = -Ea/R"""));



        // 8. 阿伦尼乌斯两点式

        FORMULAS.add(new Formula("Arrhenius两点", "ln(k2/k1) = (Ea/R)(1/T1-1/T2)",

            new String[]{"k1", "k2", "Ea", "T1", "T2"},

            new String[]{"k1 (T1时)", "k2 (T2时)", "Ea (J/mol)", "T1 (K)", "T2 (K)"}, "动力学/Arrhenius/两点式")

            .form("k2", "k1 * exp(Ea / " + R_J + " * (1/T1 - 1/T2))")

            .form("k1", "k2 / exp(Ea / " + R_J + " * (1/T1 - 1/T2))")

            .form("Ea", "ln(k2/k1) * " + R_J + " / (1/T1 - 1/T2)")

            .form("T1", "1 / (1/T2 + ln(k2/k1) * " + R_J + " / Ea)")

            .form("T2", "1 / (1/T1 - ln(k2/k1) * " + R_J + " / Ea)")

            .note("由两个温度的速率常数求Ea。比单点法更精确，无需知道A。")
            .info("""
                    【推导】由两点Arrhenius方程相除消去A得到。
                    【变量】k1(T1时), k2(T2时), Ea(活化能), T1, T2
                    【优势】无需知道指前因子A，实验上更实用。
                    【作图法】ln(k2/k1) 对 (1/T1-1/T2) 的斜率 = Ea/R"""));



        // 9. 质量作用定律

        FORMULAS.add(new Formula("质量作用定律", "rate = k[A]^m[B]^n",

            new String[]{"rate", "k", "A", "B", "m", "n"},

            new String[]{"反应速率", "速率常数 k", "[A] (mol/L)", "[B] (mol/L)", "m (A的级数)", "n (B的级数)"}, "动力学/速率定律/质量作用定律")

            .form("rate", "k * A ^ m * B ^ n")

            .form("k", "rate / (A ^ m * B ^ n)")

            .form("A", "root(rate / (k * B ^ n), m)")

            .form("B", "root(rate / (k * A ^ m), n)")

            .note("多组分反应速率方程。m和n由实验确定，不等于化学计量数。")
            .info("""
                    【历史】1864年 Guldberg & Waage 提出质量作用定律。
                    【变量详解】
                      rate: 反应速率(mol/(L·s))
                      k: 速率常数(单位取决于总级数m+n)
                      [A], [B]: 反应物浓度(mol/L)
                      m, n: 对各反应物的反应级数
                    【关键】m和n必须通过实验确定！不等于化学方程式中的计量数。
                    【确定方法】初始速率法(孤立法)、积分法、半衰期法"""));



        // 10. 零级反应积分式

        FORMULAS.add(new Formula("零级反应", "[A] = [A]0 - kt",

            new String[]{"A", "A0", "k", "t"},

            new String[]{"[A] (mol/L)", "[A]0 (mol/L)", "速率常数 k", "时间 t (s)"}, "动力学/零级反应/积分式")

            .form("A", "A0 - k * t")

            .form("A0", "A + k * t")

            .form("k", "(A0 - A) / t")

            .form("t", "(A0 - A) / k"));



        // 11. 零级半衰期

        FORMULAS.add(new Formula("零级半衰期", "t½ = [A]0 / (2k)",

            new String[]{"t12", "A0", "k"},

            new String[]{"半衰期 t½ (s)", "[A]0 (mol/L)", "速率常数 k"}, "动力学/零级反应/半衰期")

            .form("t12", "A0 / (2 * k)")

            .form("A0", "t12 * 2 * k")

            .form("k", "A0 / (2 * t12)")

            .note("零级反应半衰期与初始浓度成正比。")
            .info("【推导】由 [A]=[A]0/2 代入积分式得 t½=[A]0/(2k)\n" +
                  "【特点】初始浓度越大，半衰期越长(与一级反应相反)"));



        // 12. 一级反应积分式

        FORMULAS.add(new Formula("一级反应", "ln([A]0/[A]) = kt",

            new String[]{"A", "A0", "k", "t"},

            new String[]{"[A] (mol/L)", "[A]0 (mol/L)", "速率常数 k (s⁻¹)", "时间 t (s)"}, "动力学/一级反应/积分式")

            .form("A", "A0 * exp(-1 * k * t)")

            .form("A0", "A * exp(k * t)")

            .form("k", "ln(A0 / A) / t")

            .form("t", "ln(A0 / A) / k"));



        // 13. 一级半衰期

        FORMULAS.add(new Formula("一级半衰期", "t½ = ln2 / k",

            new String[]{"t12", "k"},

            new String[]{"半衰期 t½ (s)", "速率常数 k (s⁻¹)"}, "动力学/一级反应/半衰期")

            .form("t12", "ln(2) / k")

            .form("k", "ln(2) / t12"));



        // 14. 二级反应积分式

        FORMULAS.add(new Formula("二级反应", "1/[A] = 1/[A]0 + kt",

            new String[]{"A", "A0", "k", "t"},

            new String[]{"[A] (mol/L)", "[A]0 (mol/L)", "速率常数 k", "时间 t (s)"}, "动力学/二级反应/积分式")

            .form("A", "1 / (1 / A0 + k * t)")

            .form("A0", "1 / (1 / A - k * t)")

            .form("k", "(1 / A - 1 / A0) / t")

            .form("t", "(1 / A - 1 / A0) / k"));



        // 15. 二级半衰期

        FORMULAS.add(new Formula("二级半衰期", "t½ = 1 / (k[A]0)",

            new String[]{"t12", "k", "A0"},

            new String[]{"半衰期 t½ (s)", "速率常数 k", "[A]0 (mol/L)"}, "动力学/二级反应/半衰期")

            .form("t12", "1 / (k * A0)")

            .form("k", "1 / (t12 * A0)")

            .form("A0", "1 / (t12 * k)")

            .note("二级反应半衰期与初始浓度成反比。")
            .info("【推导】由 1/[A] = 1/([A]0/2) = 1/[A]0 + kt½ 得 t½ = 1/(k[A]0)\n" +
                  "【特点】初始浓度越大，半衰期越短(与零级相反)"));



        // ═══════════════════════════════════════

        //  电化学

        // ═══════════════════════════════════════



        // 16. 能斯特方程

        FORMULAS.add(new Formula("能斯特方程", "E = E° - (RT/nF) ln Q",

            new String[]{"E", "E0", "T", "n", "Q"},

            new String[]{"电动势 E (V)", "标准电动势 E° (V)", "温度 T (K)", "电子转移数 n", "反应商 Q"}, "电化学/Nernst方程/能斯特方程")

            .form("E", "E0 - " + R_J + " * T / (n * " + F_CONST + ") * ln(Q)")

            .form("E0", "E + " + R_J + " * T / (n * " + F_CONST + ") * ln(Q)")

            .form("T", "(E0 - E) * n * " + F_CONST + " / (" + R_J + " * ln(Q))")

            .form("n", R_J + " * T * ln(Q) / ((E0 - E) * " + F_CONST + ")")

            .form("Q", "exp((E0 - E) * n * " + F_CONST + " / (" + R_J + " * T))")

            .note("非标准浓度下的电池电势。25°C简化: E = E° - (0.0592/n)logQ。")
            .info("""
                    【历史】1889年 Walther Nernst 提出，获1920年诺贝尔化学奖。
                    【变量详解】
                      E: 非标准态电动势(V)
                      E°: 标准电动势(V)
                      T: 温度(K)
                      n: 转移电子数(无量纲)
                      Q: 反应商(同K的表达式，但用即时浓度)
                      F: Faraday常数 = 96485 C/mol
                    【25°C简化】E = E° - (0.0592/n)·logQ
                    【判据】E>0 正向自发(放电)，E<0 逆向自发，E=0 平衡
                    【与K的关系】平衡时 E=0, Q=K → E° = (RT/nF)lnK"""));



        // 17. 吉布斯与电动势

        FORMULAS.add(new Formula("ΔG与电动势", "ΔG = -nFE",

            new String[]{"dG", "n", "E"},

            new String[]{"ΔG (J/mol)", "电子转移数 n", "电动势 E (V)"}, "电化学/电池电势/ΔG=-nFE")

            .form("dG", "-1 * n * " + F_CONST + " * E")

            .form("n", "dG / (-1 * " + F_CONST + " * E)")

            .form("E", "dG / (-1 * n * " + F_CONST + ")")

            .note("ΔG = -nFE 连接热力学和电化学。E>0则ΔG<0反应自发。F = 96485 C/mol。")
            .info("""
                    【核心关系】连接热力学(ΔG)和电化学(E)的桥梁公式。
                      ΔG: Gibbs自由能变化(J/mol)
                      n: 转移电子数
                      F: Faraday常数 = 96485 C/mol
                      E: 电池电动势(V)
                    【判据】E>0 → ΔG<0 → 正向自发
                    【最大电功】W_max = -ΔG = nFE"""));



        // 18. 标准电动势与K

        FORMULAS.add(new Formula("E°与K", "E° = (RT/nF) ln K",

            new String[]{"E0", "T", "n", "K"},

            new String[]{"标准电动势 E° (V)", "温度 T (K)", "电子转移数 n", "平衡常数 K"}, "电化学/电池电势/E°与K")

            .form("E0", R_J + " * T / (n * " + F_CONST + ") * ln(K)")

            .form("T", "E0 * n * " + F_CONST + " / (" + R_J + " * ln(K))")

            .form("n", R_J + " * T * ln(K) / (E0 * " + F_CONST + ")")

            .form("K", "exp(E0 * n * " + F_CONST + " / (" + R_J + " * T))")

            .note("由标准电动势求平衡常数。25°C时 lnK = nE°/0.0257。")
            .info("""
                    【推导】由 ΔG°=-nFE° 和 ΔG°=-RTlnK 联立得到
                    【25°C简化】lnK = nE°/0.0257 或 logK = nE°/0.0592
                    【意义】E°越大 → K越大 → 反应越完全"""));



        // ═══════════════════════════════════════

        //  溶液与胶体

        // ═══════════════════════════════════════



        // 19. 亨德森-哈塞尔巴尔赫

        FORMULAS.add(new Formula("Henderson-Hasselbalch", "pH = pKa + log([A⁻]/[HA])",

            new String[]{"pH", "pKa", "A", "HA"},

            new String[]{"pH", "pKa", "[A⁻] (mol/L)", "[HA] (mol/L)"}, "溶液/酸碱平衡/Henderson-Hasselbalch")

            .form("pH", "pKa + log(A / HA)")

            .form("pKa", "pH - log(A / HA)")

            .form("A", "HA * 10 ^ (pH - pKa)")

            .form("HA", "A / 10 ^ (pH - pKa)"));



        // 20. 拉乌尔定律

        FORMULAS.add(new Formula("拉乌尔定律", "P = X·P°",

            new String[]{"P", "X", "P0"},

            new String[]{"溶液蒸气压 P", "溶剂摩尔分数 X", "纯溶剂蒸气压 P°"}, "溶液/依数性/拉乌尔定律")

            .form("P", "X * P0")

            .form("X", "P / P0")

            .form("P0", "P / X"));



        // 21. 亨利定律

        FORMULAS.add(new Formula("亨利定律", "C = kH·P",

            new String[]{"C", "kH", "P"},

            new String[]{"溶解度 C (mol/L)", "亨利常数 kH", "气体分压 P (atm)"}, "溶液/气体溶解/亨利定律")

            .form("C", "kH * P")

            .form("kH", "C / P")

            .form("P", "C / kH"));



        // 22. 沸点升高

        FORMULAS.add(new Formula("沸点升高", "ΔTb = i·Kb·m",

            new String[]{"dTb", "i", "Kb", "m"},

            new String[]{"ΔTb (°C)", "范特霍夫因子 i", "Kb (°C/m)", "质量摩尔浓度 m (mol/kg)"}, "溶液/依数性/沸点升高")

            .form("dTb", "i * Kb * m")

            .form("i", "dTb / (Kb * m)")

            .form("Kb", "dTb / (i * m)")

            .form("m", "dTb / (i * Kb)")

            .note("沸点升高。i = 范特霍夫因子(NaCl的i≈2)。水的Kb = 0.512 °C/m。")
            .info("""
                    【变量】ΔTb: 沸点升高(°C), i: van't Hoff因子, Kb: 沸点升高常数, m: 质量摩尔浓度(mol/kg)
                    【常见Kb值】水: 0.512 °C/m, 苯: 2.53 °C/m
                    【i因子】非电解质i=1, NaCl的i≈2, CaCl₂的i≈3
                    【应用】加盐煮面(实际效果很小)"""));



        // 23. 凝固点降低

        FORMULAS.add(new Formula("凝固点降低", "ΔTf = i·Kf·m",

            new String[]{"dTf", "i", "Kf", "m"},

            new String[]{"ΔTf (°C)", "范特霍夫因子 i", "Kf (°C/m)", "质量摩尔浓度 m (mol/kg)"}, "溶液/依数性/凝固点降低")

            .form("dTf", "i * Kf * m")

            .form("i", "dTf / (Kf * m)")

            .form("Kf", "dTf / (i * m)")

            .form("m", "dTf / (i * Kf)")

            .note("凝固点降低。撒盐融冰的原理。水的Kf = 1.86 °C/m。")
            .info("""
                    【变量】ΔTf: 凝固点降低(°C), i: van't Hoff因子, Kf: 凝固点降低常数, m: 质量摩尔浓度
                    【常见Kf值】水: 1.86 °C/m, 苯: 5.12 °C/m
                    【应用】冬天撒盐融冰、汽车防冻液"""));



        // 24. 渗透压

        FORMULAS.add(new Formula("渗透压", "π = iMRT",

            new String[]{"pi", "i", "M", "T"},

            new String[]{"渗透压 π (atm)", "范特霍夫因子 i", "浓度 M (mol/L)", "温度 T (K)"}, "溶液/依数性/渗透压")

            .form("pi", "i * M * " + R_L + " * T")

            .form("i", "pi / (M * " + R_L + " * T)")

            .form("M", "pi / (i * " + R_L + " * T)")

            .form("T", "pi / (i * M * " + R_L + ")")

            .note("渗透压计算。R = 0.08206 L·atm/(mol·K)。反渗透净水的原理。")
            .info("""
                    【历史】1887年 van't Hoff 发现渗透压与理想气体公式形式相同。
                    【变量】π: 渗透压(atm), i: van't Hoff因子, M: 浓度(mol/L), T: 温度(K)
                    【应用】海水淡化(反渗透)、静脉注射液浓度
                    【比较】渗透压是依数性中最灵敏的"""));



        // ═══════════════════════════════════════

        //  气体

        // ═══════════════════════════════════════



        // 25. 理想气体

        FORMULAS.add(new Formula("理想气体", "PV = nRT",

            new String[]{"P", "V", "n", "T"},

            new String[]{"压强 P (atm)", "体积 V (L)", "物质的量 n (mol)", "温度 T (K)"}, "气体/状态方程/理想气体")

            .form("P", "n * " + R_L + " * T / V")

            .form("V", "n * " + R_L + " * T / P")

            .form("n", "P * V / (" + R_L + " * T)")

            .form("T", "P * V / (n * " + R_L + ")")

            .note("理想气体。R = 0.08206 L·atm/(mol·K)。STP下1mol = 22.4L。")
            .info("""
                    【历史】1834年 Clapeyron 综合了Boyle、Charles、Avogadro定律。
                    【变量】P: 压强(atm), V: 体积(L), n: 物质的量(mol), T: 温度(K)
                      R = 0.08206 L·atm/(mol·K) = 8.314 J/(mol·K)
                    【STP】标准温度压强(0°C, 1atm)下，1mol气体 = 22.4L
                    【适用】低压高温。高压低温需用范德华方程修正。"""));



        // 26. 范德华方程

        FORMULAS.add(new Formula("范德华方程", "(P+a(n/V)²)(V-nb)=nRT",

            new String[]{"P", "V", "n", "T", "a", "b"},

            new String[]{"压强 P (atm)", "体积 V (L)", "物质的量 n (mol)", "温度 T (K)",

                         "a (L²atm/mol²)", "b (L/mol)"}, "气体/状态方程/范德华方程")

            .form("P", "n * " + R_L + " * T / (V - n * b) - a * (n / V) ^ 2")

            .form("T", "(P + a * (n / V) ^ 2) * (V - n * b) / (n * " + R_L + ")")

            .note("真实气体修正。a修正分子间引力，b修正分子体积。高压低温偏差大。")
            .info("""
                    【历史】1873年 Johannes van der Waals 提出。
                    【修正项】
                      P + a(n/V)²: 修正分子间引力(内压)
                      V - nb: 修正分子本身占有的体积
                      a: 引力参数(L²·atm/mol²), b: 体积参数(L/mol)
                    【适用】高压、低温下的真实气体
                    【临界点】在临界温度以上，气体不能被液化"""));



        // 27. 联合气体定律

        FORMULAS.add(new Formula("联合气体定律", "P₁V₁/T₁ = P₂V₂/T₂",

            new String[]{"P1", "V1", "T1", "P2", "V2", "T2"},

            new String[]{"P1 (atm)", "V1 (L)", "T1 (K)", "P2 (atm)", "V2 (L)", "T2 (K)"}, "气体/状态方程/联合气体定律")

            .form("P1", "P2 * V2 * T1 / (V1 * T2)")

            .form("V1", "P2 * V2 * T1 / (P1 * T2)")

            .form("T1", "P1 * V1 * T2 / (P2 * V2)")

            .form("P2", "P1 * V1 * T2 / (V2 * T1)")

            .form("V2", "P1 * V1 * T2 / (P2 * T1)")

            .form("T2", "P2 * V2 * T1 / (P1 * V1)")

            .note("气体状态变化(n不变)。特殊形式: Boyle定律、Charles定律等。")
            .info("""
                    【推导】由 PV=nRT 对两个状态相除得到。
                    【特殊形式】
                      等温(T不变): P₁V₁ = P₂V₂ (Boyle定律)
                      等压(P不变): V₁/T₁ = V₂/T₂ (Charles定律)
                      等容(V不变): P₁/T₁ = P₂/T₂ (Gay-Lussac定律)
                    【注意】T必须用开尔文！"""));



        // 28. 克劳修斯-克拉佩龙

        FORMULAS.add(new Formula("克劳修斯-克拉佩龙", "ln(P2/P1) = (ΔHvap/R)(1/T1-1/T2)",

            new String[]{"P1", "P2", "dHvap", "T1", "T2"},

            new String[]{"P1 (T1时蒸气压)", "P2 (T2时蒸气压)", "ΔHvap (J/mol)", "T1 (K)", "T2 (K)"}, "气体/蒸气压/克劳修斯-克拉佩龙")

            .form("P2", "P1 * exp(dHvap / " + R_J + " * (1/T1 - 1/T2))")

            .form("P1", "P2 / exp(dHvap / " + R_J + " * (1/T1 - 1/T2))")

            .form("dHvap", "ln(P2/P1) * " + R_J + " / (1/T1 - 1/T2)")

            .form("T1", "1 / (1/T2 + ln(P2/P1) * " + R_J + " / dHvap)")

            .form("T2", "1 / (1/T1 - ln(P2/P1) * " + R_J + " / dHvap)")

            .note("蒸气压与温度的关系。可求沸点或汽化热。升温蒸气压增大。")
            .info("""
                    【历史】1850年 Clapeyron 推导，后由Clausius简化。
                    【变量】P1(T1时蒸气压), P2(T2时蒸气压), ΔHvap: 汽化热(J/mol)
                    【应用】由两个温度的蒸气压求ΔHvap，或预测某温度的沸点
                    【类似】固-气升华也用此式(ΔHsub代替ΔHvap)"""));



        // ═══════════════════════════════════════

        //  基础公式

        // ═══════════════════════════════════════



        // 29. pH

        FORMULAS.add(new Formula("pH值", "pH = -log[H⁺]",

            new String[]{"pH", "H"},

            new String[]{"pH值", "[H⁺] (mol/L)"}, "基础")

            .form("pH", "-1 * log(H)")

            .form("H", "10 ^ (-1 * pH)")

            .note("pH定义。pH<7酸性，pH=7中性，pH>7碱性(25°C)。")
            .info("""
                    【历史】1909年丹麦化学家 Søren Sørensen 提出pH标度。
                    【变量】pH: 酸碱度(0~14), [H⁺]: 氢离子浓度(mol/L)
                    【相关】pOH = -log[OH⁻], pH + pOH = 14 (25°C)
                      Kw = [H⁺][OH⁻] = 1.0×10⁻¹⁴ (25°C)"""));



        // 30. 摩尔浓度

        FORMULAS.add(new Formula("摩尔浓度", "M = n/V",

            new String[]{"M", "n", "V"},

            new String[]{"浓度 M (mol/L)", "物质的量 n (mol)", "体积 V (L)"}, "基础")

            .form("M", "n / V").form("n", "M * V").form("V", "n / M"));



        // 31. 密度

        FORMULAS.add(new Formula("密度", "ρ = m/V",

            new String[]{"rho", "m", "V"},

            new String[]{"密度 ρ (g/mL)", "质量 m (g)", "体积 V (mL)"}, "基础")

            .form("rho", "m / V").form("m", "rho * V").form("V", "m / rho"));



        // 32. 热量

        FORMULAS.add(new Formula("热量计算", "q = mcΔT",

            new String[]{"q", "m", "c", "dT"},

            new String[]{"热量 q (J)", "质量 m (g)", "比热容 c (J/g·°C)", "ΔT (°C)"}, "基础")

            .form("q", "m * c * dT")

            .form("m", "q / (c * dT)")

            .form("c", "q / (m * dT)")

            .form("dT", "q / (m * c)")

            .note("热量计算。水的比热 c = 4.184 J/(g·°C)。吸热q>0，放热q<0。")
            .info("""
                    【变量】q: 热量(J), m: 质量(g), c: 比热容(J/(g·°C)), ΔT: 温度变化(°C)
                    【常见比热】水: 4.184, 冰: 2.09, 铝: 0.900, 铁: 0.449
                    【符号约定】吸热 q>0 (温度升高), 放热 q<0 (温度降低)
                    【量热计】q(reaction) = -q(water) = -m·c·ΔT"""));



        // 33. 稀释

        FORMULAS.add(new Formula("稀释公式", "M₁V₁ = M₂V₂",

            new String[]{"M1", "V1", "M2", "V2"},

            new String[]{"M1 (mol/L)", "V1 (L)", "M2 (mol/L)", "V2 (L)"}, "基础")

            .form("M1", "M2 * V2 / V1")

            .form("V1", "M2 * V2 / M1")

            .form("M2", "M1 * V1 / V2")

            .form("V2", "M1 * V1 / M2"));



        // 34. 动能

        FORMULAS.add(new Formula("动能", "KE = ½mv²",

            new String[]{"KE", "m", "v"},

            new String[]{"动能 KE (J)", "质量 m (kg)", "速度 v (m/s)"}, "基础")

            .form("KE", "0.5 * m * v ^ 2")

            .form("m", "KE / (0.5 * v ^ 2)")

            .form("v", "root(KE / (0.5 * m), 2)")

            .note("经典动能公式。m用kg，v用m/s，KE用J。")
            .info("""
                    【变量】KE: 动能(J), m: 质量(kg), v: 速度(m/s)
                    【注意单位】m必须用kg！1g = 0.001kg
                    【分子动能】气体分子平均动能 = (3/2)kT"""));



        // 35. 朗缪尔吸附等温式

        FORMULAS.add(new Formula("朗缪尔吸附", "θ = KP / (1+KP)",

            new String[]{"theta", "K", "P"},

            new String[]{"覆盖度 θ", "吸附常数 K", "气体分压 P (atm)"}, "表面化学")

            .form("theta", "K * P / (1 + K * P)")

            .form("K", "theta / (P * (1 - theta))")

            .form("P", "theta / (K * (1 - theta))")

            .note("单分子层吸附。θ∈[0,1]为表面覆盖度。高压时θ→1(饱和)。")
            .info("""
                    【历史】1918年 Irving Langmuir 提出，获1932年诺贝尔化学奖。
                    【变量】θ: 表面覆盖度(0~1), K: 吸附平衡常数, P: 气体分压(atm)
                    【假设】单分子层吸附、均匀表面、无相互作用
                    【极限】低压: θ≈KP (线性), 高压: θ→1 (饱和)
                    【线性化】P/θ = 1/K + P → 斜率=1, 截距=1/K"""));

    }



    // ═══ 公开 API ═══



    public static List<Formula> getAllFormulas() { return Collections.unmodifiableList(FORMULAS); }



    /** 按分类获取公式 */

    public static Map<String, List<Formula>> getByCategory() {

        Map<String, List<Formula>> map = new LinkedHashMap<>();

        for (Formula f : FORMULAS) {

            map.computeIfAbsent(f.category, k -> new ArrayList<>()).add(f);

        }

        return map;

    }



    /** 求解公式 */

    public static SolveResult solve(int formulaIdx, Map<String, String> values) {

        if (formulaIdx < 0 || formulaIdx >= FORMULAS.size())

            return new SolveResult("无效的公式索引");

        Formula f = FORMULAS.get(formulaIdx);



        // 找出未知量

        String unknown = null;

        for (String var : f.variables) {

            String val = values.getOrDefault(var, "").trim();

            if (val.isEmpty()) {

                if (unknown != null) return new SolveResult("请只留一个变量为空作为未知量");

                unknown = var;

            }

        }

        if (unknown == null) return new SolveResult("请留一个变量为空作为未知量");



        // 查找求解表达式

        String expr = f.solveForms.get(unknown);

        if (expr == null) return new SolveResult("无法求解 " + unknown + "（此公式暂不支持该移项）");



        // 代入数值，构建完整表达式

        String fullExpr = expr;

        StringBuilder steps = new StringBuilder(unknown + " = ");

        for (String var : f.variables) {

            if (var.equals(unknown)) continue;

            String val = values.get(var).trim();

            fullExpr = fullExpr.replace(var, "(" + val + ")");

            steps.append(var).append("=").append(val).append(", ");

        }



        try {

            SigFigCalculator.Result result = SigFigCalculator.calculate(fullExpr);

            steps.setLength(steps.length() - 2); // remove trailing ", "

            return new SolveResult(unknown, result.display, steps.toString());

        } catch (Exception e) {

            return new SolveResult("计算错误: " + e.getMessage() + "\n    表达式: " + fullExpr);

        }

    }

}


