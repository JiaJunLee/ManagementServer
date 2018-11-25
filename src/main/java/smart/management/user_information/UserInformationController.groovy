package smart.management.user_information

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import smart.management.common.ServerResponse
import smart.management.common.Sex
import smart.management.security.AuthenticationAnnotation

import java.text.SimpleDateFormat

@RestController
@RequestMapping('/user_information')
class UserInformationController {

    @Autowired UserInformationService userInformationService

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat('yyyy-MM-dd')

    @RequestMapping('/save')
    @AuthenticationAnnotation
    ServerResponse save(String userInformationId, String name, String phoneNumber, Sex sex, String birth, String remarks) {
        UserInformation userInformation = null
        if (userInformationId) {
            userInformation = userInformationService.findById(userInformationId)
            userInformation.name = name
            userInformation.phoneNumber = phoneNumber
            userInformation.sex = sex
            if (birth) { userInformation.birth = DATE_FORMAT.parse(birth) }
            userInformation.remarks = remarks
            userInformationService.save(userInformation)
        }
        return new ServerResponse(content: userInformation, message: 'save successful')
    }

}
