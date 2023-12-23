package top.mcos.listener;

import top.mcos.AesopPlugin;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PlayerLock  {
    public static ConcurrentHashMap<String, ReentrantLock> playLocks = new ConcurrentHashMap<>();
    public static Lock getPlayerLock(String playerId) {
        ReentrantLock lock = playLocks.get(playerId);
        if(lock==null) {
            synchronized (AesopPlugin.sync.intern(playerId)) {
                lock = playLocks.get(playerId);
                if (lock == null) {
                    lock = new ReentrantLock();
                    playLocks.put(playerId, lock);
                }
                return lock;
            }
        }
        return lock;
    }

    public static void removeLock(String playerId) {
        playLocks.remove(playerId);
    }

}
