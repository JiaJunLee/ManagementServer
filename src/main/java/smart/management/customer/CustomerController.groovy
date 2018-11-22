package smart.management.customer

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
@RequestMapping('/customer')
class CustomerController {

    @Autowired UserService userService
    @Autowired CustomerService customerService
    @Autowired UserGroupService userGroupService

    @RequestMapping('/all')
    @AuthenticationAnnotation
    ServerResponse all() {
        List<Customer> customers = customerService.findAll().toList()
        customers?.each { currentCustomer ->
            currentCustomer?.visibilityUserGroupIds?.each { userGroupId ->
                UserGroup userGroup = userGroupService.findById(userGroupId)
                if (userGroup) {
                    currentCustomer.visibilityUserGroups.add(userGroup)
                }
            }
        }
        return new ServerResponse(content: customers, message: 'query successful')
    }

    @RequestMapping
    ServerResponse index(String userId) {
        User user = userService.findById(userId)
        List<Customer> customers = customerService.findAllByVisibilityUserGroupIds(user.userGroupIds)
        return new ServerResponse(content: customers, message: 'query successful!')
    }

    @RequestMapping('/save')
    ServerResponse create(Customer customer) {
        return new ServerResponse(content: customerService.save(customer), message: 'save successful')
    }

    @RequestMapping('/delete')
    @AuthenticationAnnotation
    ServerResponse delete(String customerId) {
        return new ServerResponse(content: customerService.deleteById(customerId), message: 'delete successful')
    }

    @RequestMapping('/visibility')
    @AuthenticationAnnotation
    ServerResponse visibility(String customerId, String userGroupId) {
        Customer customer = customerService.findById(customerId)
        customer.addVisibilityUserGroupId(userGroupId)
        customerService.save(customer)
        return new ServerResponse(content: customer, message: 'add visibility user group successful')
    }

    @RequestMapping('/visibilities')
    @AuthenticationAnnotation
    ServerResponse visibilities(String[] customerIds, String userGroupId) {
        customerIds?.each { customerId ->
            Customer customer = customerService.findById(customerId)
            customer.addVisibilityUserGroupId(userGroupId)
            customerService.save(customer)
        }
        return new ServerResponse(message: 'add visibilities user group successful')
    }

    @ExceptionHandler(Customer.VisibilityUserGroupAlreadyExists.class)
    ServerResponse handleVisibilityUserGroupAlreadyExists() {
        return new ServerResponse(resultCode: ServerResponse.ServerResponseCode.REJECTED, message: 'visibility user group already exists!')
    }

    @RequestMapping('/remove_visibility')
    @AuthenticationAnnotation
    ServerResponse removeVisibility(String customerId, String userGroupId) {
        Customer customer = customerService.findById(customerId)
        customer.removeVisibilityUserGroupId(userGroupId)
        customerService.save(customer)
        return new ServerResponse(content: customer, message: 'remove visibility user group successful')
    }

    @ExceptionHandler(Customer.VisibilityUserGroupNotExists.class)
    ServerResponse handleVisibilityUserGroupNotExists() {
        return new ServerResponse(resultCode: ServerResponse.ServerResponseCode.REJECTED, message: 'visibility user group not exists!')
    }

}
