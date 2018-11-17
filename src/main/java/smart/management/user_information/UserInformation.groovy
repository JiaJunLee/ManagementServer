package smart.management.user_information

import com.fasterxml.jackson.annotation.JsonFormat
import smart.management.common.BaseDocument
import smart.management.common.Sex

import javax.validation.constraints.NotNull

class UserInformation extends BaseDocument {

    @NotNull String userId

    @NotNull String name = 'System User'
    @NotNull String sex = Sex.UNKNOWN
    String phoneNumber
    @JsonFormat(pattern = 'yyyy/MM/dd HH:mm:ss') Date birth
    String remarks

}
