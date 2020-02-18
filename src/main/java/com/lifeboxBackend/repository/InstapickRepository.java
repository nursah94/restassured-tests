package com.lifeboxBackend.repository;

import com.lifeboxBackend.entity.Instapick;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InstapickRepository extends CrudRepository<Instapick, Long> {

    @Modifying
    @Transactional
    @Query(value = "delete from INSTAPICK where ACCOUNT_ID in(select ACCOUNT.ID from ACCOUNT where USERNAME=:username and ACCOUNT.STATUS=1)",nativeQuery = true)
    int delete(@Param("username") String username);
}



