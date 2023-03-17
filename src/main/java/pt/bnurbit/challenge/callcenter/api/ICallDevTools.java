package pt.bnurbit.challenge.callcenter.api;

public interface ICallDevTools {

    /**
     * Clear all call records.
     */
    void clear();

    /**
     * Generate a given number of call records.
     */
    void generate(int n);
}