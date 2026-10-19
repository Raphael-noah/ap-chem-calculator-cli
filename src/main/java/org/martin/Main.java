package org.martin;

import org.martin.calc.SigFigCalculator;
import org.martin.chem.*;
import org.martin.chem.EquationBalancer.BalanceResult;

import java.util.*;

/**
 * AP化学有效数字计算器 v2.0 — 纯终端版
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println(AsciiArt.BANNER);
        System.out.println();

        while (true) {
            System.out.println(AsciiArt.MENU);
            System.out.print(AsciiArt.PROMPT);
            String choice = sc.nextLine().trim().toLowerCase();

            switch (choice) {
                case "1": case "calc":   calculatorMode(); break;
                case "2": case "pt":     periodicTableMode(); break;
                case "3": case "eq":     equationMode(); break;
                case "4": case "form":   formulaMode(); break;
                case "h": case "help":   showHelp(); break;
                case "q": case "quit": case "exit": case "":
                    System.out.println(AsciiArt.GOODBYE);
                    return;
                default:
                    System.out.println("  ⚠ 未知命令，输入 h 查看帮助");
            }
        }
    }

    // ═══════════════════════════════════════════════
    //  1. 有效数字计算器
    // ═══════════════════════════════════════════════
    static void calculatorMode() {
        System.out.println(AsciiArt.boxTitle("⚗  有效数字计算器"));
        System.out.println(AsciiArt.boxLine("  支持: + - * / ^ log ln root exp 科学记数法"));
        System.out.println(AsciiArt.boxLine("  输入 q 返回主菜单"));
        System.out.println(AsciiArt.BOX_BOTTOM);

        while (true) {
            System.out.print("  calc> ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("q") || input.isEmpty()) return;
            try {
                SigFigCalculator.Result r = SigFigCalculator.calculate(input);
                System.out.println("  ═▸ " + r.display);
            } catch (Exception e) {
                System.out.println("  ✗ " + e.getMessage());
            }
        }
    }

    // ═══════════════════════════════════════════════
    //  2. 元素周期表（含拉丁语名称）
    // ═══════════════════════════════════════════════
    static void periodicTableMode() {
        System.out.println(AsciiArt.boxTitle("⚛  元素周期表查询"));
        System.out.println(AsciiArt.boxLine("  输入符号(Fe)/英文(iron)/中文(铁)/拉丁(ferrum)/序数(26)"));
        System.out.println(AsciiArt.boxLine("  输入 grid 显示周期表网格，输入 q 返回"));
        System.out.println(AsciiArt.BOX_BOTTOM);

        while (true) {
            System.out.print("  pt> ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("q") || input.isEmpty()) return;

            if (input.equalsIgnoreCase("grid")) {
                printPeriodicTableGrid();
                continue;
            }

            Element e = PeriodicTable.search(input);
            if (e == null) {
                System.out.println("  ✗ 未找到: " + input);
            } else {
                printElementDetail(e);
            }
        }
    }

    static void printPeriodicTableGrid() {
        System.out.println();
        System.out.println("  ┌─────────────────────────────────────────────────────────────────┐");
        for (int period = 1; period <= 7; period++) {
            System.out.printf("  │ P%d ", period);
            for (int group = 1; group <= 18; group++) {
                Element e = PeriodicTable.getAtGrid(period, group);
                System.out.printf("%-3s", e != null ? e.symbol : "·");
            }
            System.out.println("│");
        }
        System.out.println("  │                                                                 │");
        System.out.printf("  │ Ln ");
        for (int group = 3; group <= 17; group++) {
            Element e = PeriodicTable.getAtGrid(8, group);
            System.out.printf("%-3s", e != null ? e.symbol : "·");
        }
        System.out.println("     │");
        System.out.printf("  │ Ac ");
        for (int group = 3; group <= 17; group++) {
            Element e = PeriodicTable.getAtGrid(9, group);
            System.out.printf("%-3s", e != null ? e.symbol : "·");
        }
        System.out.println("     │");
        System.out.println("  └─────────────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    static void printElementDetail(Element e) {
        System.out.println("  ┌────────────────────────────────────────┐");
        System.out.printf("  │ #%d  %-4s %-12s %-6s%n",
            e.atomicNumber, e.symbol, e.nameEn, e.nameCn);
        System.out.printf("  │ Latin: %-30s%n", e.nameLatin);
        System.out.println("  │────────────────────────────────────────│");
        System.out.printf("  │ Atomic Mass : %-24.4f%n", e.atomicMass);
        System.out.printf("  │ Electron    : %-24s%n", e.electronConfig);
        System.out.printf("  │ Category    : %-24s%n", e.category);
        System.out.printf("  │ Position    : Period %-2d, Group %-2d%n", e.period, e.group);
        System.out.println("  └────────────────────────────────────────┘");
    }

    // ═══════════════════════════════════════════════
    //  3. 方程式配平
    // ═══════════════════════════════════════════════
    static void equationMode() {
        System.out.println(AsciiArt.boxTitle("🔬 化学方程式配平"));
        System.out.println(AsciiArt.boxLine("  格式: 反应物1 + 反应物2 -> 产物1 + 产物2"));
        System.out.println(AsciiArt.boxLine("  示例: Fe + O2 -> Fe2O3"));
        System.out.println(AsciiArt.boxLine("        C3H8 + O2 -> CO2 + H2O"));
        System.out.println(AsciiArt.boxLine("  输入 q 返回主菜单"));
        System.out.println(AsciiArt.BOX_BOTTOM);

        while (true) {
            System.out.print("  eq> ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("q") || input.isEmpty()) return;

            BalanceResult r = EquationBalancer.balance(input);
            if (r.success) {
                System.out.println("  ✓ " + r.balanced);
                if (r.coefficients != null && r.compounds != null) {
                    System.out.print("    系数: ");
                    for (int i = 0; i < r.compounds.length; i++) {
                        System.out.print(r.compounds[i] + "=" + r.coefficients[i]);
                        if (i < r.compounds.length - 1) System.out.print(", ");
                    }
                    System.out.println();
                }
            } else {
                System.out.println("  ✗ " + r.error);
            }
        }
    }

    // ═══════════════════════════════════════════════
    //  4. 公式快捷计算（多级分类 + 搜索）
    // ═══════════════════════════════════════════════
    static void formulaMode() {
        List<ChemFormulas.Formula> all = ChemFormulas.getAllFormulas();

        while (true) {
            System.out.println(AsciiArt.boxTitle("📐 AP化学公式快捷计算"));
            System.out.println(AsciiArt.boxLine("  输入数字选择分类，s+关键词搜索，q返回"));
            System.out.println(AsciiArt.BOX_BOTTOM);
            System.out.println();

            // 显示分类树（三级层次结构）
            int idx = 1;
            String lastL1 = "", lastL2 = "";
            Map<String, List<ChemFormulas.Formula>> grouped = new LinkedHashMap<>();

            // 先按 L1/L2 分组
            for (ChemFormulas.Formula f : all) {
                String[] parts = f.category.split("/");
                String key = parts[0] + "/" + (parts.length > 1 ? parts[1] : "");
                grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(f);
            }

            String prevL1 = "";
            for (Map.Entry<String, List<ChemFormulas.Formula>> entry : grouped.entrySet()) {
                String[] pathParts = entry.getKey().split("/", 2);
                String l1 = pathParts[0];
                String l2 = pathParts.length > 1 ? pathParts[1] : "";
                List<ChemFormulas.Formula> formulas = entry.getValue();

                // Level 1 header (e.g., "动力学")
                if (!l1.equals(prevL1)) {
                    if (!prevL1.isEmpty()) System.out.println("  │");
                    System.out.println("  " + l1);
                    prevL1 = l1;
                }

                // Level 2 header (e.g., "零级反应")
                if (l2.isEmpty()) {
                    // No L2 - show formulas directly under L1
                    for (int i = 0; i < formulas.size(); i++) {
                        ChemFormulas.Formula f = formulas.get(i);
                        String l1Conn = isLastGroup(grouped, entry.getKey()) ? " " : "│";
                        String l2Conn = (i == formulas.size() - 1) ? "└" : "├";
                        System.out.printf("  %s  %s─ [%2d] %-16s %s%n",
                            l1Conn, l2Conn, idx, f.name, f.equation);
                        idx++;
                    }
                } else {
                    System.out.printf("  │  %s─ %s%n",
                        (isLastGroup(grouped, entry.getKey())) ? "└" : "├", l2);

                    // Level 3: individual formulas
                    for (int i = 0; i < formulas.size(); i++) {
                        ChemFormulas.Formula f = formulas.get(i);
                        String l2Conn = isLastGroup(grouped, entry.getKey()) ? " " : "│";
                        String l3Conn = (i == formulas.size() - 1) ? "└" : "├";
                        System.out.printf("  │  %s  %s─ [%2d] %-16s %s%n",
                            l2Conn, l3Conn, idx, f.name, f.equation);
                        idx++;
                    }
                }
            }
            System.out.println();

            System.out.print("  选择 (1-" + all.size() + "/s搜索/q返回): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("q") || input.isEmpty()) return;

            // 搜索模式
            if (input.toLowerCase().startsWith("s")) {
                String keyword = input.substring(1).trim().toLowerCase();
                if (keyword.isEmpty()) {
                    System.out.print("  输入搜索关键词: ");
                    keyword = sc.nextLine().trim().toLowerCase();
                }
                searchFormulas(all, keyword);
                continue;
            }

            int choice;
            try { choice = Integer.parseInt(input); }
            catch (NumberFormatException e) { System.out.println("  ✗ 请输入数字或 s+关键词"); continue; }

            if (choice < 1 || choice > all.size()) {
                System.out.println("  ✗ 请选择 1-" + all.size());
                continue;
            }

            solveFormula(all.get(choice - 1), choice - 1);
        }
    }

    static <V> boolean isLastGroup(Map<String, V> map, String key) {
        String last = null;
        for (String k : map.keySet()) last = k;
        return key.equals(last);
    }

    static void searchFormulas(List<ChemFormulas.Formula> all, String keyword) {
        System.out.println("\n  ── 搜索结果: \"" + keyword + "\" ──");
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            ChemFormulas.Formula f = all.get(i);
            boolean match = f.name.toLowerCase().contains(keyword)
                || f.equation.toLowerCase().contains(keyword)
                || f.category.toLowerCase().contains(keyword);
            if (!match) {
                for (String desc : f.descriptions) {
                    if (desc.toLowerCase().contains(keyword)) { match = true; break; }
                }
            }
            if (match) {
                System.out.printf("  [%2d] %-16s %-30s %s%n",
                    i + 1, f.category, f.name, f.equation);
                found = true;
            }
        }
        if (!found) System.out.println("  ✗ 未找到匹配的公式");
        System.out.println();

        System.out.print("  输入编号求解 (留空返回): ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()) return;
        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= all.size()) {
                solveFormula(all.get(choice - 1), choice - 1);
            }
        } catch (NumberFormatException e) { /* ignore */ }
    }

    static void solveFormula(ChemFormulas.Formula formula, int idx) {
        // 显示概览页
        System.out.println();
        System.out.println("  ┌────────────────────────────────────────┐");
        System.out.println("  │ " + formula.category + " / " + formula.name);
        System.out.println("  │ " + formula.equation);
        System.out.println("  │────────────────────────────────────────│");
        System.out.println("  │ 符号含义:");
        for (int i = 0; i < formula.variables.length; i++) {
            System.out.printf("  │   %-8s → %s%n", formula.variables[i], formula.descriptions[i]);
        }
        if (!formula.notes.isEmpty()) {
            System.out.println("  │────────────────────────────────────────│");
            System.out.println("  │ 💡 " + formula.notes);
        }
        System.out.println("  │────────────────────────────────────────│");
        System.out.println("  │ [d] 查看详细介绍    [Enter] 开始计算");
        System.out.println("  │ [q] 返回");
        System.out.println("  └────────────────────────────────────────┘");
        System.out.print("  选择: ");
        String choice = sc.nextLine().trim().toLowerCase();

        if (choice.equals("q")) return;

        // 如果选择详细介绍
        if (choice.equals("d") && !formula.detailedInfo.isEmpty()) {
            showDetailedInfo(formula);
            System.out.print("  按 Enter 继续...");
            sc.nextLine();
        }

        // 进入计算模式
        doSolveFormula(formula, idx);
    }

    static void showDetailedInfo(ChemFormulas.Formula formula) {
        System.out.println();
        System.out.println("  ╔════════════════════════════════════════════════════╗");
        System.out.println("  ║  📖 " + formula.name + " — 详细介绍");
        System.out.println("  ╠════════════════════════════════════════════════════╣");
        // 逐行显示详细介绍内容
        for (String line : formula.detailedInfo.split("\n")) {
            System.out.println("  ║  " + line);
        }
        System.out.println("  ╚════════════════════════════════════════════════════╝");
        System.out.println();
    }

    static void doSolveFormula(ChemFormulas.Formula formula, int idx) {
        System.out.println();
        System.out.println("  ┌────────────────────────────────────────┐");
        System.out.println("  │ " + formula.equation);
        System.out.println("  │ 留空一个变量作为未知量:");
        System.out.println("  │");

        Map<String, String> values = new LinkedHashMap<>();
        for (int i = 0; i < formula.variables.length; i++) {
            System.out.printf("  │ %-8s = ", formula.variables[i]);
            String val = sc.nextLine().trim();
            values.put(formula.variables[i], val);
        }

        ChemFormulas.SolveResult r = ChemFormulas.solve(idx, values);
        System.out.println("  └────────────────────────────────────────┘");
        if (r.success) {
            System.out.println("  ✓ " + r.unknown + " = " + r.value);
            System.out.println("    " + r.steps);
        } else {
            System.out.println("  ✗ " + r.error);
        }
        System.out.println();
        System.out.print("  按 Enter 返回...");
        sc.nextLine();
    }

    // ═══════════════════════════════════════════════
    //  帮助
    // ═══════════════════════════════════════════════
    static void showHelp() {
        System.out.println();
        System.out.println("  ┌─────────────────────────────────────────┐");
        System.out.println("  │          AP Chemistry Calculator        │");
        System.out.println("  │                v2.0 Help                │");
        System.out.println("  ├─────────────────────────────────────────┤");
        System.out.println("  │ 命令    功能                            │");
        System.out.println("  │─────────────────────────────────────────│");
        System.out.println("  │ 1/calc  有效数字计算器                  │");
        System.out.println("  │ 2/pt    元素周期表 (含拉丁语名称)       │");
        System.out.println("  │ 3/eq    化学方程式配平                  │");
        System.out.println("  │ 4/form  AP化学公式 (35+公式, 多级分类)  │");
        System.out.println("  │ h/help  显示帮助                        │");
        System.out.println("  │ q/exit  退出程序                        │");
        System.out.println("  ├─────────────────────────────────────────┤");
        System.out.println("  │ 计算器语法:                             │");
        System.out.println("  │  基本: 2.5 * 3.42   乘方: 2.0 ^ 3     │");
        System.out.println("  │  对数: log(100) log(8,2) ln(2.718)     │");
        System.out.println("  │  开方: root(27, 3)  exp(2.5)           │");
        System.out.println("  │  科学: 1.5e-3 * 2.0                    │");
        System.out.println("  ├─────────────────────────────────────────┤");
        System.out.println("  │ 公式搜索: 在公式菜单输入 s+关键词       │");
        System.out.println("  │  例: sNernst  sgibbs  s活化能          │");
        System.out.println("  └─────────────────────────────────────────┘");
        System.out.println();
    }

    // ═══════════════════════════════════════════════
    //  ASCII Art & UI
    // ═══════════════════════════════════════════════
    static class AsciiArt {

        static final String BANNER =
            "\n" +
            "   ╔══════════════════════════════════════════════════════════════════╗\n" +
            "   ║                                                                  ║\n" +
            "   ║       █████╗ ██████╗       ██████╗██╗  ██╗███████╗███╗   ███╗    ║\n" +
            "   ║      ██╔══██╗██╔══██╗     ██╔════╝██║  ██║██╔════╝████╗ ████║    ║\n" +
            "   ║      ███████║██████╔╝     ██║     ███████║█████╗  ██╔████╔██║    ║\n" +
            "   ║      ██╔══██║██╔═══╝      ██║     ██╔══██║██╔══╝  ██║╚██╔╝██║    ║\n" +
            "   ║      ██║  ██║██║          ╚██████╗██║  ██║███████╗██║ ╚═╝ ██║    ║\n" +
            "   ║      ╚═╝  ╚═╝╚═╝           ╚═════╝╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝   ║\n" +
            "   ║                                                                  ║\n" +
            "   ║      ⚗  SigFigs · Periodic Table · Equations · 35+ Formulas  ⚗  ║\n" +
            "   ║                         v2.0                                     ║\n" +
            "   ╚══════════════════════════════════════════════════════════════════╝";

        static final String MENU =
            "\n" +
            "   ┌── 功能菜单 ─────────────────────────┐\n" +
            "   │ [1] ⚗  有效数字计算器                │\n" +
            "   │ [2] ⚛  元素周期表                    │\n" +
            "   │ [3] 🔬 化学方程式配平                │\n" +
            "   │ [4] 📐 公式快捷计算 (35+ 公式)       │\n" +
            "   │ [h] 📖 帮助   [q] 退出               │\n" +
            "   └──────────────────────────────────────┘";

        static final String PROMPT = "  ap-chem> ";

        static final String GOODBYE =
            "\n  ⚗ 再见! Good luck on your AP Chem exam! ⚗\n";

        static final String BOX_BOTTOM = "  └──────────────────────────────────────────┘";

        static String boxTitle(String title) {
            return "\n  ┌── " + title + " " + "─".repeat(Math.max(0, 40 - title.length())) + "┐";
        }

        static String boxLine(String content) {
            return "  │" + content + " ".repeat(Math.max(0, 43 - content.length())) + "│";
        }
    }
}
