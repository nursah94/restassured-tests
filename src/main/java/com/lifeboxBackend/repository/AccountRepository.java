package com.lifeboxBackend.repository;

import com.lifeboxBackend.entity.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    @Query(value = "select * from ACCOUNT where USERNAME=:msisdn and STATUS=1", nativeQuery = true)
    Account findBy(@Param("msisdn") String msisdn);

    @Query(value = "select * from ACCOUNT where USERNAME=:msisdn", nativeQuery = true)
    Account getaccountStatus(@Param("msisdn") String msisdn);

    @Query(value = "select * from ACCOUNT where USERNAME=:msisdn and STATUS=10", nativeQuery = true)
    Account findAccountIdAfterDeletion(@Param("msisdn") String msisdn);

    @Modifying
    @Transactional
    @Query(value = "update ACCOUNT set STATUS=4 where USERNAME=:username and STATUS=1", nativeQuery = true)
    int update(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE ACCOUNT SET TWO_FACTOR_AUTH =:val WHERE USERNAME=:username and STATUS=1", nativeQuery = true)
    int updateTwoFactorAccount(@Param("val") String val, @Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE ACCOUNT SET CREATED_DATE=:createdDate WHERE USERNAME=:username and STATUS=1",nativeQuery = true)
    int updateAccountCreatedDate(@Param("username")String username, @Param("createdDate") Date createdDate);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM ACCOUNT WHERE USERNAME=:username",nativeQuery = true)
    int deleteAccount (@Param("username") String username);
}
