package com.example.baitaptuan1.config;

import org.hibernate.dialect.MySQLDialect;

/**
 * MariaDB 10.4 accepts {@code FOR UPDATE} and rejects MySQL 8's {@code FOR UPDATE OF alias}.
 */
public class MariaDbCompatibleDialect extends MySQLDialect {

    @Override
    protected boolean supportsAliasLocks() {
        return false;
    }
}
