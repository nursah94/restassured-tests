package com.lifeboxBackend.repository;

import com.lifeboxBackend.entity.DeviceInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository

public interface DeviceInfoRepository extends CrudRepository<DeviceInfo, Long> {

    @Query(value = "SELECT * FROM DEVICE_INFO WHERE ACCOUNT_ID IN(SELECT ID FROM ACCOUNT WHERE USERNAME='12349903' AND STATUS=1)AND UUID='ALPER'", nativeQuery = true)
    DeviceInfo foundDevice();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM DEVICE_INFO WHERE ACCOUNT_ID IN(SELECT ID FROM ACCOUNT WHERE USERNAME='12349903' AND STATUS=1)AND UUID='ALPER'",nativeQuery = true)
    int deleteDevice();
}
