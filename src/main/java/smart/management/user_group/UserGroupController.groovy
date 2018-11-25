package smart.management.user_group

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import smart.management.common.ServerResponse
import smart.management.security.AuthenticationAnnotation

@RestController
@RequestMapping('/user_group')
class UserGroupController {

    @Autowired UserGroupService userGroupService

    @RequestMapping('/create')
    @AuthenticationAnnotation
    ServerResponse create(UserGroup userGroup) {
        return new ServerResponse(content: userGroupService.save(userGroup), message: 'create successful')
    }

    @RequestMapping
    @AuthenticationAnnotation
    ServerResponse index() {
        return new ServerResponse(content: userGroupService.findAll(), message: 'query successful')
    }

    @RequestMapping('/delete')
    @AuthenticationAnnotation
    ServerResponse delete(String userGroupId) {
        userGroupService.deleteById(userGroupId)
        return new ServerResponse(message: 'delete successful')
    }

}
