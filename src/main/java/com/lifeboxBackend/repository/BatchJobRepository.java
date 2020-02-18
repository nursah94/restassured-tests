package com.lifeboxBackend.repository;

import com.lifeboxBackend.entity.Batch_Job;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BatchJobRepository extends CrudRepository<Batch_Job,Long> {

    @Query(value = "SELECT * FROM BATCH_JOB WHERE (ACCOUNT_ID=:account_id OR MSISDN=:msisdn) and OPERATION_TYPE='270'",nativeQuery = true)
    Batch_Job findByMsisdnAndAccount_id(@Param("msisdn") String msisdn,@Param("account_id") int account_id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM BATCH_JOB WHERE ACCOUNT_ID=:account_id OR MSISDN=:msisdn", nativeQuery = true)
    int delete(@Param("msisdn") String msisdn, @Param("account_id") int account_id);

}
