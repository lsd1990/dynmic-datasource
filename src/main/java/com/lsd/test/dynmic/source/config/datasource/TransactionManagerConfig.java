package com.lsd.test.dynmic.source.config.datasource;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(2)
@Aspect
public class TransactionManagerConfig {

    private static final String AOP_POINTCUT_EXPRESSION = "execution(* com.lsd.test.dynmic.source.service.*.*(..))";


    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager transactionManager;


    @Bean
    public TransactionInterceptor txAdvice() {
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();

        /*只读事务，不做更新操作*/
        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
        readOnlyTx.setRollbackRules(
                Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS );
        readOnlyTx.setTimeout(1800);

        /*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        requiredTx.setRollbackRules(
                Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        readOnlyTx.setTimeout(600);

        Map<String, TransactionAttribute> txMap = new HashMap<>();

        txMap.put("list*", requiredTx);
        txMap.put("find*", requiredTx);
        txMap.put("count*", requiredTx);
        txMap.put("sum*", requiredTx);
        txMap.put("read*", requiredTx);
        txMap.put("get*", requiredTx);
        txMap.put("check*", requiredTx);
        txMap.put("*", requiredTx);
        source.setNameMap( txMap );
        TransactionInterceptor txAdvice = new TransactionInterceptor(transactionManager, source);
        return txAdvice;
    }

    @Bean
    public Advisor txAdviceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        return new DefaultPointcutAdvisor(pointcut, txAdvice());
    }

}
