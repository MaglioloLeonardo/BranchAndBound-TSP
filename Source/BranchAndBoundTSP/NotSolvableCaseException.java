package Source.BranchAndBoundTSP;

import java.util.List;
import java.util.Collections;


public class NotSolvableCaseException extends Exception {
    public final List<Integer> UnidirectionalNodesKeys;

    public NotSolvableCaseException(List<Integer> UnidirectionalNodesKeys) {
        this.UnidirectionalNodesKeys = Collections.unmodifiableList(UnidirectionalNodesKeys);
    }
}
