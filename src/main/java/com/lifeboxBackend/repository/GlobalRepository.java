package com.lifeboxBackend.repository;

import com.lifeboxBackend.entity.GlobalSetting;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GlobalRepository extends CrudRepository<GlobalSetting,Long> {

    @Query(value = "SELECT * FROM GLOBAL_SETTING WHERE ID=:id",nativeQuery = true)
    GlobalSetting findBy(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE GLOBAL_SETTING SET VALUE=:val WHERE ID=:ıd",nativeQuery = true)
    int update (@Param("val")String val, @Param("ıd") String ıd);
}
