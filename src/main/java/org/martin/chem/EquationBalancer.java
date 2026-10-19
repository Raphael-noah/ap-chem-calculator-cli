package org.martin.chem;

import java.util.*;

/**
 * 化学方程式配平引擎
 * 使用高斯消元法求解最小正整数系数
 */
public class EquationBalancer {

    public static class BalanceResult {
        public final boolean success;
        public final String balanced;
        public final String error;
        public final int[] coefficients;
        public final String[] compounds;

        BalanceResult(String balanced, int[] coefficients, String[] compounds) {
            this.success = true;
            this.balanced = balanced;
            this.error = null;
            this.coefficients = coefficients;
            this.compounds = compounds;
        }

        BalanceResult(String error) {
            this.success = false;
            this.balanced = null;
            this.error = error;
            this.coefficients = null;
            this.compounds = null;
        }
    }

    public static BalanceResult balance(String equation) {
        try {
            // Split into reactants and products
            String[] sides = equation.split("->|-->|→|=");
            if (sides.length != 2) return new BalanceResult("请使用 -> 或 = 分隔反应物和产物");

            String[] reactants = splitCompounds(sides[0]);
            String[] products = splitCompounds(sides[1]);

            String[] allCompounds = new String[reactants.length + products.length];
            System.arraycopy(reactants, 0, allCompounds, 0, reactants.length);
            System.arraycopy(products, 0, allCompounds, reactants.length, products.length);

            int n = allCompounds.length; // number of compounds (variables)

            // Parse each compound to get element counts
            Map<String, Integer> elementIndex = new LinkedHashMap<>();
            List<Map<String, Integer>> compoundElements = new ArrayList<>();

            for (String compound : allCompounds) {
                Map<String, Integer> elements = parseFormula(compound.trim());
                compoundElements.add(elements);
                for (String elem : elements.keySet()) {
                    elementIndex.putIfAbsent(elem, elementIndex.size());
                }
            }

            int m = elementIndex.size(); // number of elements (equations)

            // Build matrix: m rows (elements) x n columns (compounds)
            // Reactants are positive, products are negative
            double[][] matrix = new double[m][n];
            for (int j = 0; j < n; j++) {
                Map<String, Integer> elems = compoundElements.get(j);
                for (Map.Entry<String, Integer> entry : elems.entrySet()) {
                    int i = elementIndex.get(entry.getKey());
                    matrix[i][j] = (j < reactants.length) ? entry.getValue() : -entry.getValue();
                }
            }

            // Solve using Gaussian elimination
            int[] solution = solveHomogeneous(matrix, m, n);
            if (solution == null) return new BalanceResult("无法配平此方程式");

            // Build balanced equation string
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < reactants.length; j++) {
                if (j > 0) sb.append(" + ");
                if (solution[j] != 1) sb.append(solution[j]);
                sb.append(reactants[j].trim());
            }
            sb.append(" → ");
            for (int j = 0; j < products.length; j++) {
                int idx = reactants.length + j;
                if (j > 0) sb.append(" + ");
                if (solution[idx] != 1) sb.append(solution[idx]);
                sb.append(products[j].trim());
            }

            return new BalanceResult(sb.toString(), solution, allCompounds);
        } catch (Exception e) {
            return new BalanceResult("解析错误: " + e.getMessage());
        }
    }

    /** Parse a chemical formula like "Ca(OH)2" into {Ca:1, O:2, H:2} */
    static Map<String, Integer> parseFormula(String formula) {
        Deque<Map<String, Integer>> stack = new ArrayDeque<>();
        stack.push(new LinkedHashMap<>());

        int i = 0;
        while (i < formula.length()) {
            char c = formula.charAt(i);
            if (c == '(') {
                stack.push(new LinkedHashMap<>());
                i++;
            } else if (c == ')') {
                i++;
                int num = readNumber(formula, i);
                i += String.valueOf(num).length();
                if (num == 1 && formula.charAt(i - 1) != '1') {
                    // no number after ), multiplier is 1
                }
                Map<String, Integer> inner = stack.pop();
                Map<String, Integer> top = stack.peek();
                for (Map.Entry<String, Integer> entry : inner.entrySet()) {
                    top.merge(entry.getKey(), entry.getValue() * num, Integer::sum);
                }
            } else if (Character.isUpperCase(c)) {
                StringBuilder elem = new StringBuilder();
                elem.append(c);
                i++;
                while (i < formula.length() && Character.isLowerCase(formula.charAt(i))) {
                    elem.append(formula.charAt(i++));
                }
                int num = readNumber(formula, i);
                String numStr = String.valueOf(num);
                if (num == 1 && (i >= formula.length() || !Character.isDigit(formula.charAt(i)))) {
                    // no number, count is 1
                } else {
                    i += numStr.length();
                }
                stack.peek().merge(elem.toString(), num, Integer::sum);
            } else {
                i++;
            }
        }
        return stack.pop();
    }

    private static int readNumber(String s, int pos) {
        int num = 0;
        while (pos < s.length() && Character.isDigit(s.charAt(pos))) {
            num = num * 10 + (s.charAt(pos) - '0');
            pos++;
        }
        return num == 0 ? 1 : num;
    }

    private static String[] splitCompounds(String side) {
        return side.trim().split("\\s*\\+\\s*");
    }

    /** Solve Ax = 0 for smallest positive integer solution using Gaussian elimination */
    private static int[] solveHomogeneous(double[][] matrix, int m, int n) {
        double[][] a = new double[m][n];
        for (int i = 0; i < m; i++) System.arraycopy(matrix[i], 0, a[i], 0, n);

        int pivotCol = 0;
        int[] pivotRow = new int[m];
        Arrays.fill(pivotRow, -1);

        for (int row = 0; row < m && pivotCol < n; row++) {
            // Find pivot
            int maxRow = row;
            for (int i = row + 1; i < m; i++) {
                if (Math.abs(a[i][pivotCol]) > Math.abs(a[maxRow][pivotCol])) maxRow = i;
            }
            if (Math.abs(a[maxRow][pivotCol]) < 1e-10) {
                pivotCol++;
                row--;
                continue;
            }

            // Swap
            double[] tmp = a[row]; a[row] = a[maxRow]; a[maxRow] = tmp;
            pivotRow[row] = pivotCol;

            // Eliminate
            for (int i = 0; i < m; i++) {
                if (i != row && Math.abs(a[i][pivotCol]) > 1e-10) {
                    double factor = a[i][pivotCol] / a[row][pivotCol];
                    for (int j = 0; j < n; j++) a[i][j] -= factor * a[row][j];
                }
            }
            pivotCol++;
        }

        // Back-substitute: set free variable = 1, solve for pivot variables
        int freeVar = -1;
        for (int j = 0; j < n; j++) {
            boolean isPivot = false;
            for (int pr : pivotRow) if (pr >= 0 && Math.abs(a[pr][j]) > 1e-10) { isPivot = true; break; }
            // Check more carefully
            isPivot = false;
            for (int i = 0; i < m; i++) {
                if (pivotRow[i] == j) { isPivot = true; break; }
            }
            if (!isPivot && freeVar == -1) {
                freeVar = j;
            }
        }
        if (freeVar == -1) freeVar = n - 1;

        double[] solution = new double[n];
        solution[freeVar] = 1.0;

        for (int i = m - 1; i >= 0; i--) {
            if (pivotRow[i] < 0) continue;
            int pc = pivotRow[i];
            double sum = 0;
            for (int j = pc + 1; j < n; j++) {
                sum += a[i][j] * solution[j];
            }
            if (Math.abs(a[i][pc]) > 1e-10) {
                solution[pc] = -sum / a[i][pc];
            }
        }

        // Make all positive and convert to smallest integers
        // Find sign: if any negative, negate all
        boolean hasNeg = false;
        for (double v : solution) if (v < -1e-10) { hasNeg = true; break; }
        if (hasNeg) for (int j = 0; j < n; j++) solution[j] = -solution[j];

        // Make all positive
        for (int j = 0; j < n; j++) solution[j] = Math.abs(solution[j]);

        // Find LCM to convert to integers
        double minPositive = Double.MAX_VALUE;
        for (double v : solution) if (v > 1e-10 && v < minPositive) minPositive = v;

        // Try multipliers to find integer solution
        int[] intSolution = new int[n];
        for (int mult = 1; mult <= 1000; mult++) {
            boolean allInt = true;
            for (int j = 0; j < n; j++) {
                double scaled = solution[j] * mult / minPositive;
                int rounded = (int) Math.round(scaled);
                if (Math.abs(scaled - rounded) > 0.05) { allInt = false; break; }
                intSolution[j] = rounded;
            }
            if (allInt) {
                // Check all positive
                boolean allPos = true;
                for (int v : intSolution) if (v <= 0) { allPos = false; break; }
                if (allPos) {
                    // Reduce by GCD
                    int g = intSolution[0];
                    for (int j = 1; j < n; j++) g = gcd(g, intSolution[j]);
                    if (g > 1) for (int j = 0; j < n; j++) intSolution[j] /= g;
                    return intSolution;
                }
            }
        }
        return null;
    }

    private static int gcd(int a, int b) {
        while (b != 0) { int t = b; b = a % b; a = t; }
        return a;
    }
}
