package smart.management.common

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document

import javax.validation.constraints.NotNull

@Document
abstract class BaseDocument {

    @Id String id
    @NotNull @CreatedDate @JsonFormat(pattern = 'yyyy/MM/dd HH:mm:ss') Date createDate = new Date()
    @NotNull @LastModifiedDate @JsonFormat(pattern = 'yyyy/MM/dd HH:mm:ss') Date lastModifiedDate

}
