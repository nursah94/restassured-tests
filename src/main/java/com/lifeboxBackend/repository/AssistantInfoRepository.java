package com.lifeboxBackend.repository;

import com.lifeboxBackend.entity.Asistant_Info;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AssistantInfoRepository extends CrudRepository<Asistant_Info,Long> {

    //entity ile bağladık crud yazarak eşleştirmek için

    @Query (value="SELECT * FROM ASSISTANT_INFO WHERE ACCOUNT_ID=42270841 AND ID=9635",nativeQuery = true )
    Asistant_Info xx();  //bu select atmak sadece dbde
    //select attığımız zaman tablo dönüyor dbde o yüzden Asisstant_INfo şeklinde yazdık ama update veya delete
    //sorgusu atcaksak int yapmak yeterli çünkü 1 rows update şeklinde dönüyor cevap.

    @Modifying
    @Transactional
    @Query(value="UPDATE ASSISTANT_INFO SET SAVED=0 WHERE ID=9635 AND ACCOUNT_ID='42270841'",nativeQuery = true)
    int updateSavedCard();   //cardı tekrar 0'a çektik. java içinde çağırdık sonra onu dbdeki card değerini 0 yapıyor bu method



 //eğer select sorgusu yazıyorsan query annotationu delete falan yapcaksak modifying.
    //ctrl alt l derleme
}
