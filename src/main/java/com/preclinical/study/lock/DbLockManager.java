package com.preclinical.study.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbLockManager {

    private static final Logger log = LoggerFactory.getLogger(DbLockManager.class);
    private final JdbcTemplate jdbcTemplate;

    public DbLockManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean tryLock(String lockName, String podName) {
        try {
            int rows = jdbcTemplate.update("""
                INSERT INTO scheduler_lock(lock_name, locked_at, locked_by)
                VALUES (?, now(), ?)
                ON CONFLICT (lock_name)
                DO NOTHING
            """, lockName, podName);
            return rows > 0;
        } catch (Exception e) {
            log.error("Error acquiring lock {}", lockName, e);
            return false;
        }
    }

    public void releaseLock(String lockName, String podName) {
        try {
            jdbcTemplate.update("DELETE FROM scheduler_lock WHERE lock_name = ? AND locked_by = ?", lockName, podName);
        } catch (Exception e) {
            log.error("Error releasing lock {}", lockName, e);
        }
    }
}
