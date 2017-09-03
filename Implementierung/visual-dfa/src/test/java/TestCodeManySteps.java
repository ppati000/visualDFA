import dfaTests.TestMethod;

public class TestCodeManySteps {
    
    public static TestMethod getCodeManyAnalysisSteps() {
        String signature = "void test_manySteps()";
        // formatter:off
        String method = 
                "void test_allConstantConflict() {"
                + "int x = 0;"
                  // create the assignments x = 1; x = 2; ... x = 100;
                + createAssingmentChain("x", 1, 100)
                + "}";
        // formatter:on
        return new TestMethod(signature, method);
    }
    
    private static String createAssingmentChain(String varName, int startCnt, int endCnt) {
        StringBuilder sb = new StringBuilder(varName).append(" = " + startCnt + ";");
        for (int i = startCnt + 1; i <= endCnt; ++i) {
            sb.append(varName).append(" = " + i + ";");
        }
        
        return sb.toString();
    }

}
