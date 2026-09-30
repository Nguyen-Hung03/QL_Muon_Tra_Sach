package com.example.baitaptuan1.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MariaDbCompatibleDialectTest {

    @Test
    void pessimisticLockOmitsAliasClause() {
        MariaDbCompatibleDialect dialect = new MariaDbCompatibleDialect();
        assertEquals(" for update", dialect.getForUpdateString("b1_0"));
    }
}
