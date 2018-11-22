package smart.management.incoming_customer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import smart.management.common.ServerResponse
import smart.management.security.AuthenticationAnnotation
import smart.management.user.User
import smart.management.user.UserService
import smart.management.user_group.UserGroup
import smart.management.user_group.UserGroupService

@RestController
@RequestMapping('/incoming_customer')
class IncomingCustomerController {

    @Autowired UserService userService
    @Autowired IncomingCustomerService incomingCustomerService
    @Autowired UserGroupService userGroupService

    @RequestMapping('/all')
    @AuthenticationAnnotation
    ServerResponse all() {
        List<IncomingCustomer> incomingCustomers = incomingCustomerService.findAll().toList()
        incomingCustomers?.each { currentIncomingCustomer ->
            currentIncomingCustomer?.visibilityUserGroupIds?.each { userGroupId ->
                UserGroup userGroup = userGroupService.findById(userGroupId)
                if (userGroup) {
                    currentIncomingCustomer.visibilityUserGroups.add(userGroup)
                }
            }
        }
        return new ServerResponse(content: incomingCustomers, message: 'query successful')
    }

    @RequestMapping
    ServerResponse index(String userId) {
        User user = userService.findById(userId)
        List<IncomingCustomer> incomingCustomers = incomingCustomerService.findAllByVisibilityUserGroupIds(user.userGroupIds)
        return new ServerResponse(content: incomingCustomers, message: 'query successful!')
    }

    @RequestMapping('/save')
    ServerResponse create(IncomingCustomer incomingCustomer) {
        return new ServerResponse(content: incomingCustomerService.save(incomingCustomer), message: 'save successful')
    }

    @RequestMapping('/delete')
    @AuthenticationAnnotation
    ServerResponse delete(String incomingCustomerId) {
        return new ServerResponse(content: incomingCustomerService.deleteById(incomingCustomerId), message: 'delete successful')
    }

    @RequestMapping('/visibility')
    @AuthenticationAnnotation
    ServerResponse visibility(String incomingCustomerId, String userGroupId) {
        IncomingCustomer incomingCustomer = incomingCustomerService.findById(incomingCustomerId)
        incomingCustomer.addVisibilityUserGroupId(userGroupId)
        incomingCustomerService.save(incomingCustomer)
        return new ServerResponse(content: incomingCustomer, message: 'add visibility user group successful')
    }

    @RequestMapping('/visibilities')
    @AuthenticationAnnotation
    ServerResponse visibilities(String[] incomingCustomerIds, String userGroupId) {
        incomingCustomerIds?.each { incomingCustomerId ->
            IncomingCustomer incomingCustomer = incomingCustomerService.findById(incomingCustomerId)
            incomingCustomer.addVisibilityUserGroupId(userGroupId)
            incomingCustomerService.save(incomingCustomer)
        }
        return new ServerResponse(message: 'add visibilities user group successful')
    }

    @ExceptionHandler(IncomingCustomer.VisibilityUserGroupAlreadyExists.class)
    ServerResponse handleVisibilityUserGroupAlreadyExists() {
        return new ServerResponse(resultCode: ServerResponse.ServerResponseCode.REJECTED, message: 'visibility user group already exists!')
    }

    @RequestMapping('/remove_visibility')
    @AuthenticationAnnotation
    ServerResponse removeVisibility(String incomingCustomerId, String userGroupId) {
        IncomingCustomer incomingCustomer = incomingCustomerService.findById(incomingCustomerId)
        incomingCustomer.removeVisibilityUserGroupId(userGroupId)
        incomingCustomerService.save(incomingCustomer)
        return new ServerResponse(content: incomingCustomer, message: 'remove visibility user group successful')
    }

    @ExceptionHandler(IncomingCustomer.VisibilityUserGroupNotExists.class)
    ServerResponse handleVisibilityUserGroupNotExists() {
        return new ServerResponse(resultCode: ServerResponse.ServerResponseCode.REJECTED, message: 'visibility user group not exists!')
    }

}
