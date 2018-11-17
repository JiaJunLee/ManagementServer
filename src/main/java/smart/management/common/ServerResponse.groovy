package smart.management.common

import com.fasterxml.jackson.annotation.JsonFormat

import javax.validation.constraints.NotNull

class ServerResponse {

    static final class ServerResponseCode {
        static final Integer SUCCESSFUL = 200
        static final Integer REJECTED = 400
        static final Integer ERROR = 500
    }

    @NotNull Integer resultCode = ServerResponseCode.SUCCESSFUL
    Object content
    String message
    @NotNull @JsonFormat(pattern = 'yyyy/MM/dd HH:mm:ss') Date handleDate = new Date()

}
