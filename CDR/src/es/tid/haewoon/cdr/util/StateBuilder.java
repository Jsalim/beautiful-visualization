package es.tid.haewoon.cdr.util;

import java.util.Map;

public interface StateBuilder<T> {
    public MarkovChainState<T> build (String line, Map<T, MarkovChainState<T>> chain);
}
