package smart.management.common

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import smart.management.user.User
import smart.management.user_information.UserInformation

import javax.validation.constraints.NotNull

class Remark {

    @NotNull User user
    @NotNull UserInformation userInformation
    String remark
    @NotNull @CreatedDate @JsonFormat(pattern = 'yyyy/MM/dd HH:mm:ss') Date createDate = new Date()

}
