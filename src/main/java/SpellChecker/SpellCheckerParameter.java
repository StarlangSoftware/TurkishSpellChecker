package SpellChecker;

public class SpellCheckerParameter {
    private double threshold = 0.0;
    private boolean deMiCheck = true;
    private boolean rootNGram = true;

    public SpellCheckerParameter() {
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public void setDeMiCheck(boolean deMiCheck) {
        this.deMiCheck = deMiCheck;
    }

    public void setRootNGram(boolean rootNGram) {
        this.rootNGram = rootNGram;
    }

    public double getThreshold() {
        return threshold;
    }

    public boolean deMiCheck() {
        return deMiCheck;
    }

    public boolean isRootNGram() {
        return rootNGram;
    }
}
