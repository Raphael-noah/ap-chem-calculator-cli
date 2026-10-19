package org.martin.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * AP化学有效数字计算引擎。
 * 支持: +, -, *, /, ^, log, root, exp, 科学记数法
 * 自动按AP化学规则保留有效数字。
 */
public class SigFigCalculator {

    private static final int INTERMEDIATE_PRECISION = 50;

    /** 计算结果 */
    public static class Result {
        public final BigDecimal value;
        public final String display;

        public Result(BigDecimal value) {
            this.value = value;
            this.display = value.toPlainString();
        }
    }

    /** 计算表达式（公开API） */
    public static Result calculate(String input) {
        List<String> tokens = tokenize(input.trim());
        BigDecimal result = evaluate(tokens);
        return new Result(result);
    }

    // --- 核心解析与计算 ---

    static BigDecimal evaluate(List<String> tokens) {
        // 1. 处理函数 (log, root, exp)
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equals("log") || token.equals("root") || token.equals("exp") || token.equals("ln")) {
                int bracketCount = 0;
                int start = -1;
                int end = -1;
                for (int j = i + 1; j < tokens.size(); j++) {
                    if (tokens.get(j).equals("(")) {
                        if (start == -1) start = j;
                        bracketCount++;
                    } else if (tokens.get(j).equals(")")) {
                        bracketCount--;
                        if (bracketCount == 0) { end = j; break; }
                    }
                }
                if (start == -1 || end == -1) throw new IllegalArgumentException("括号不匹配: " + token);

                List<String> argsTokens = tokens.subList(start + 1, end);
                List<List<String>> params = splitByComma(argsTokens);

                BigDecimal res;
                if (token.equals("log")) {
                    if (params.size() == 1) {
                        res = funcLog(evaluate(params.get(0)), new BigDecimal(10));
                    } else if (params.size() == 2) {
                        res = funcLog(evaluate(params.get(0)), evaluate(params.get(1)));
                    } else {
                        throw new IllegalArgumentException("log 参数错误");
                    }
                } else if (token.equals("root")) {
                    if (params.size() == 2) {
                        res = funcRoot(evaluate(params.get(0)), evaluate(params.get(1)));
                    } else {
                        throw new IllegalArgumentException("root 需要两个参数: root(底数, 次数)");
                    }
                } else if (token.equals("exp")) {
                    if (params.size() == 1) {
                        res = funcExp(evaluate(params.get(0)));
                    } else {
                        throw new IllegalArgumentException("exp 需要一个参数: exp(x) = e^x");
                    }
                } else if (token.equals("ln")) {
                    if (params.size() == 1) {
                        res = funcLn(evaluate(params.get(0)));
                    } else {
                        throw new IllegalArgumentException("ln 需要一个参数: ln(x)");
                    }
                } else {
                    throw new IllegalArgumentException("未知函数: " + token);
                }

                List<String> newTokens = new ArrayList<>(tokens.subList(0, i));
                newTokens.add(res.toPlainString());
                newTokens.addAll(tokens.subList(end + 1, tokens.size()));
                return evaluate(newTokens);
            }
        }

        // 2. 处理括号
        int level = 0;
        int start = -1;
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals("(")) {
                if (level == 0) start = i;
                level++;
            } else if (tokens.get(i).equals(")")) {
                level--;
                if (level == 0) {
                    BigDecimal innerRes = evaluate(tokens.subList(start + 1, i));
                    List<String> newTokens = new ArrayList<>(tokens.subList(0, start));
                    newTokens.add(innerRes.toPlainString());
                    newTokens.addAll(tokens.subList(i + 1, tokens.size()));
                    return evaluate(newTokens);
                }
            }
        }

        // 3. 加减 (+, -) - 低优先级
        level = 0;
        int splitIdx = -1;
        char op = 0;
        for (int i = 0; i < tokens.size(); i++) {
            String t = tokens.get(i);
            if (t.equals("(")) level++;
            else if (t.equals(")")) level--;
            else if (level == 0 && (t.equals("+") || t.equals("-"))) {
                if (splitIdx == -1) { splitIdx = i; op = t.charAt(0); }
            }
        }
        if (splitIdx != -1) {
            BigDecimal left = evaluate(tokens.subList(0, splitIdx));
            BigDecimal right = evaluate(tokens.subList(splitIdx + 1, tokens.size()));
            return applyOperation(left, right, op);
        }

        // 4. 乘除幂 (*, /, ^)
        level = 0;
        splitIdx = -1;
        op = 0;
        for (int i = 0; i < tokens.size(); i++) {
            String t = tokens.get(i);
            if (t.equals("(")) level++;
            else if (t.equals(")")) level--;
            else if (level == 0 && (t.equals("*") || t.equals("/") || t.equals("^"))) {
                if (splitIdx == -1) { splitIdx = i; op = t.charAt(0); }
            }
        }
        if (splitIdx != -1) {
            BigDecimal left = evaluate(tokens.subList(0, splitIdx));
            BigDecimal right = evaluate(tokens.subList(splitIdx + 1, tokens.size()));
            return applyOperation(left, right, op);
        }

        // 5. 纯数字
        if (tokens.size() == 1) {
            return new BigDecimal(tokens.get(0));
        }
        throw new IllegalArgumentException("无法解析表达式");
    }

    private static List<List<String>> splitByComma(List<String> tokens) {
        List<List<String>> result = new ArrayList<>();
        List<String> current = new ArrayList<>();
        int depth = 0;
        for (String t : tokens) {
            if (t.equals("(")) depth++;
            else if (t.equals(")")) depth--;
            if (t.equals(",") && depth == 0) {
                result.add(current);
                current = new ArrayList<>();
            } else {
                current.add(t);
            }
        }
        result.add(current);
        return result;
    }

    private static BigDecimal applyOperation(BigDecimal a, BigDecimal b, char op) {
        switch (op) {
            case '+': case '-': return addSubtract(a, b, op);
            case '*': case '/': return multiplyDivide(a, b, op);
            case '^': return power(a, b);
            default: throw new IllegalArgumentException("未知运算符");
        }
    }

    private static BigDecimal addSubtract(BigDecimal a, BigDecimal b, char op) {
        BigDecimal raw = (op == '+') ? a.add(b) : a.subtract(b);
        int scale = Math.min(a.scale(), b.scale());
        return raw.setScale(Math.max(0, scale), RoundingMode.HALF_UP);
    }

    private static BigDecimal multiplyDivide(BigDecimal a, BigDecimal b, char op) {
        if (op == '/' && b.compareTo(BigDecimal.ZERO) == 0)
            throw new ArithmeticException("除数不能为零");
        BigDecimal raw = (op == '*') ? a.multiply(b) : a.divide(b, INTERMEDIATE_PRECISION, RoundingMode.HALF_UP);
        int sigFigs = Math.min(countSignificantFigures(a), countSignificantFigures(b));
        return roundToSigFigs(raw, sigFigs);
    }

    private static BigDecimal power(BigDecimal base, BigDecimal exp) {
        double dRes = Math.pow(base.doubleValue(), exp.doubleValue());
        BigDecimal raw = new BigDecimal(dRes);
        int sigFigs = countSignificantFigures(base);
        return roundToSigFigs(raw, sigFigs);
    }

    // 自然对数 ln(x) — 保留double全精度（公式计算用）
    private static BigDecimal funcLn(BigDecimal val) {
        if (val.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("ln 的参数必须大于零");
        double rawDouble = Math.log(val.doubleValue());
        return new BigDecimal(rawDouble);
    }

    // 指数函数 exp(x) = e^x
    private static BigDecimal funcExp(BigDecimal x) {
        double rawDouble = Math.exp(x.doubleValue());
        BigDecimal raw = new BigDecimal(rawDouble);
        // 规则：x的有效数字位数 → 结果的有效数字位数
        int sigFigs = countSignificantFigures(x);
        return roundToSigFigs(raw, Math.max(1, sigFigs));
    }

    private static BigDecimal funcRoot(BigDecimal base, BigDecimal n) {
        if (n.compareTo(BigDecimal.ZERO) == 0)
            throw new IllegalArgumentException("根的次数不能为零");
        BigDecimal inverseN = BigDecimal.ONE.divide(n, INTERMEDIATE_PRECISION, RoundingMode.HALF_UP);
        double dRes = Math.pow(base.doubleValue(), inverseN.doubleValue());
        BigDecimal raw = new BigDecimal(dRes);
        int sigFigs = countSignificantFigures(base);
        return roundToSigFigs(raw, sigFigs);
    }

    private static BigDecimal funcLog(BigDecimal val, BigDecimal base) {
        if (val.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("对数的真数必须大于零");
        if (base.compareTo(BigDecimal.ZERO) <= 0 || base.compareTo(BigDecimal.ONE) == 0)
            throw new IllegalArgumentException("对数的底数必须大于零且不等于1");
        double rawDouble = Math.log(val.doubleValue()) / Math.log(base.doubleValue());
        BigDecimal raw = new BigDecimal(rawDouble);
        int sigFigsVal = countSignificantFigures(val);
        return raw.setScale(sigFigsVal, RoundingMode.HALF_UP);
    }

    // --- 工具方法 ---

    private static boolean isNumericToken(String token) {
        try { new BigDecimal(token); return true; }
        catch (NumberFormatException e) { return false; }
    }

    public static int countSignificantFigures(BigDecimal num) {
        String plain = num.toPlainString();
        if (plain.startsWith("-")) plain = plain.substring(1);
        if (plain.contains(".")) {
            plain = plain.replace(".", "");
            plain = plain.replaceFirst("^0+", "");
            return plain.isEmpty() ? 1 : plain.length();
        } else {
            plain = plain.replaceFirst("^0+", "");
            return plain.isEmpty() ? 1 : plain.length();
        }
    }

    public static BigDecimal roundToSigFigs(BigDecimal num, int n) {
        if (num.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        int magnitude = num.precision() - num.scale();
        int position = n - magnitude;
        return num.setScale(position, RoundingMode.HALF_UP);
    }

    // --- 词法分析 ---

    static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean expectNumber = true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                sb.append(c);
                expectNumber = false;
            } else {
                if (sb.length() > 0) { tokens.add(sb.toString()); sb.setLength(0); }
                if (c == ' ') continue;

                if (Character.isLetter(c)) {
                    if ((c == 'e' || c == 'E') && !tokens.isEmpty()
                        && isNumericToken(tokens.get(tokens.size() - 1))) {
                        String numSoFar = tokens.remove(tokens.size() - 1);
                        StringBuilder sciNum = new StringBuilder(numSoFar);
                        sciNum.append(c);
                        if (i + 1 < input.length()
                            && (input.charAt(i + 1) == '+' || input.charAt(i + 1) == '-')) {
                            sciNum.append(input.charAt(++i));
                        }
                        while (i + 1 < input.length() && Character.isDigit(input.charAt(i + 1))) {
                            sciNum.append(input.charAt(++i));
                        }
                        tokens.add(sciNum.toString());
                        expectNumber = false;
                    } else {
                        sb.append(c);
                        while (i + 1 < input.length() && Character.isLetter(input.charAt(i + 1))) {
                            sb.append(input.charAt(++i));
                        }
                        tokens.add(sb.toString()); sb.setLength(0);
                        expectNumber = false;
                    }
                } else if (c == '-' && expectNumber) {
                    StringBuilder negNum = new StringBuilder("-");
                    while (i + 1 < input.length()
                           && (Character.isDigit(input.charAt(i + 1)) || input.charAt(i + 1) == '.')) {
                        negNum.append(input.charAt(++i));
                    }
                    if (negNum.length() > 1 && i + 1 < input.length()
                        && (input.charAt(i + 1) == 'e' || input.charAt(i + 1) == 'E')) {
                        negNum.append(input.charAt(++i));
                        if (i + 1 < input.length()
                            && (input.charAt(i + 1) == '+' || input.charAt(i + 1) == '-')) {
                            negNum.append(input.charAt(++i));
                        }
                        while (i + 1 < input.length() && Character.isDigit(input.charAt(i + 1))) {
                            negNum.append(input.charAt(++i));
                        }
                    }
                    if (negNum.length() > 1) {
                        tokens.add(negNum.toString());
                        expectNumber = false;
                    } else {
                        tokens.add("-");
                        expectNumber = true;
                    }
                } else {
                    tokens.add(String.valueOf(c));
                    expectNumber = (c == '(' || c == ',' || c == '*' || c == '/' || c == '^' || c == '+' || c == '-');
                }
            }
        }
        if (sb.length() > 0) tokens.add(sb.toString());
        return tokens;
    }
}
