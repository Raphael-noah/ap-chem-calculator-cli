# ⚗ AP Chemistry Calculator (Terminal Edition)

纯终端版的 AP 化学计算神器，基于 Java 21 开发。
被 AP 化学折磨后，为了不想手算有效数字（SigFigs）一怒之下手搓而成。

## ✨ 核心功能

- **⚗ 有效数字计算器**：手写词法分析器与递归下降解析，完美遵守 AP 化学有效数字规则。支持科学记数法、嵌套括号及 `log`, `ln`, `root`, `exp` 函数。
- **⚛ 元素周期表**：支持中/英/拉丁文名称、元素符号及原子序数查询，内置标准 ASCII 周期表网格视图。
- **🔬 化学方程式配平**：采用**高斯消元法**解齐次线性方程组，自动求最小正整数解，再复杂的反应也能秒配平。
- **📐 35+ 核心公式库**：涵盖热力学、动力学、电化学、溶液依数性及气体状态方程，支持多级分类、关键词搜索及未知量自动求解。

## 🚀 快速开始

### 方式一：下载免安装包（推荐）
前往右侧的 **[Releases]** 页面，根据你的操作系统下载 `.zip` 压缩包：
- Windows 用户：下载 `APChemCalculator-Windows.zip`，解压后双击 `APChemCalculator.bat`。
- macOS 用户：下载 `APChemCalculator-MacOS.zip`，解压后双击 `APChemCalculator.app`。
*(注：已内置精简版 JRE 运行环境，无需额外配置 Java。)*

### 方式二：从源码编译
需要安装 Java 21 和 Maven：
```bash
git clone git@github.com:Raphael-noah/ap-chem-calculator-terminal.git
cd ap-chem-calculator-terminal
mvn clean package
java -jar target/ap-chem-calculator-2.0-SNAPSHOT.jar
