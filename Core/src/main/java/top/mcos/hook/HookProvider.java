package top.mcos.hook;

public interface HookProvider<T> {
    HookProvider<T> load() throws Exception;
    boolean isLoaded();
    T getAPI();
    String getAPIName();
}
