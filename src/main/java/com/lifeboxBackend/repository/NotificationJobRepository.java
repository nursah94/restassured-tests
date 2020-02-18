
package com.lifeboxBackend.repository;

import com.lifeboxBackend.entity.NotificationJob;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationJobRepository extends CrudRepository<NotificationJob, Long> {

    @Query("select nj from NotificationJob nj where nj.id=169097")
    List<NotificationJob> findById();

    //Notification_Job tablosundan request_body kolonundan msısdn'e giden otp get eder.
    @Query(value = "select nj from NOTIFICATION_JOB nj where nj.REQUEST_BODY like %:msisdn% )",nativeQuery = true)
    List<NotificationJob> findByIdd(@Param("msisdn") String msisdn);

    @Query(value = "SELECT * FROM NOTIFICATION_JOB WHERE ACCOUNT_ID =:accountId ORDER BY  CREATED_DATE DESC",nativeQuery = true)
    NotificationJob find(@Param("accountId") long account_ıd);

    @Query(value = "SELECT * FROM NOTIFICATION_JOB WHERE ACCOUNT_ID IN(SELECT ID FROM ACCOUNT WHERE USERNAME=:msisdn AND STATUS=1)ORDER BY CREATED_DATE DESC",nativeQuery = true)
    List<NotificationJob> findByMsisdn(@Param("msisdn") String msisdn);

    NotificationJob findFirstByRequestBodyContainingOrderByCreatedDateDesc(@Param("msisdn") String msisdn);

}




