package com.kai.webby.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Component
public class DbSrcTrsUtil {

    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager;

    //开启事务
    public TransactionStatus openTrs() {
        return dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    //提交事务
    public void commitTrs(TransactionStatus trs) {
        dataSourceTransactionManager.commit(trs);
    }

    //回滚事务
    public void rollbackTrs(TransactionStatus trs) {
        dataSourceTransactionManager.rollback(trs);
    }
}
