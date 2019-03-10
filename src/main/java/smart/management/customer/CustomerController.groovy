package smart.management.customer

import org.apache.shiro.SecurityUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import smart.management.common.Remark
import smart.management.common.ServerResponse
import smart.management.security.AuthenticationAnnotation
import smart.management.security.utils.JWT
import smart.management.user.User
import smart.management.user.UserService
import smart.management.user_group.UserGroup
import smart.management.user_group.UserGroupService
import smart.management.user_information.UserInformation
import smart.management.user_information.UserInformationService

@RestController
@RequestMapping('/customer')
class CustomerController {

    @Autowired UserService userService
    @Autowired UserInformationService userInformationService
    @Autowired CustomerService customerService
    @Autowired UserGroupService userGroupService
    @Autowired JWT jwt

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

    @RequestMapping('/page')
    @AuthenticationAnnotation
    ServerResponse page(int pageIndex, int pageSize) {
        Page<Customer> customerPage = customerService.getPage(pageIndex, pageSize)
        List<Customer> customers = customerPage.getContent()
        customers?.each { currentCustomer ->
            currentCustomer?.visibilityUserGroupIds?.each { userGroupId ->
                UserGroup userGroup = userGroupService.findById(userGroupId)
                if (userGroup) {
                    currentCustomer.visibilityUserGroups.add(userGroup)
                }
            }
        }
        // for history documents update
        User user = userService.findByUsername(jwt.getUsername(SecurityUtils.getSubject().getPrincipal().toString()))
        if (customers.any { it.remarks }) {
            UserInformation userInformation = userInformationService.findByUserId(user?.id)
            customers.findAll { it.remarks }.each {
                it.remarksNew.add(new Remark(remark: it.remarks, user: new User(username: user?.username), userInformation: new UserInformation(name: userInformation?.name), createDate: Calendar.getInstance().getTime()))
                it.remarks = null
            }
            customerService.saveAll(customers)
        }
        sortCustomerRemarksNew(customers)
        return new ServerResponse(content: [
                customers: customers,
                total: customerPage.getTotalElements(),
                currentPageIndex: customerPage.getNumber() + 1
        ], message: 'query successful')
    }

    private static void sortCustomerRemarksNew(List<Customer> customers) {
        customers.each { currentCustomer ->
            sortRemarksNew(currentCustomer.remarksNew)
        }
    }

    private static void sortRemarksNew(List<Remark> remarksNew) {
        remarksNew.sort{ r1, r2 ->
            if (r1.createDate.getTime() > r2.createDate.getTime())
                return -1
            else if (r1.createDate.getTime() == r2.createDate.getTime())
                return 0
            return 1
        }
    }

    @RequestMapping('/append-remark')
    ServerResponse appendRemark(String customerId, String remark) {
        User user = userService.findByUsername(jwt.getUsername(SecurityUtils.getSubject().getPrincipal().toString()))
        UserInformation userInformation = userInformationService.findByUserId(user?.id)
        Customer customer = customerService.findById(customerId)
        if (customer) {
            customer.remarksNew.add(new Remark(remark: remark, user: new User(username: user?.username), userInformation: new UserInformation(name: userInformation?.name), createDate: Calendar.getInstance().getTime()))
            customerService.save(customer)
        }
        sortRemarksNew(customer.remarksNew)
        return new ServerResponse(content: customer, message: 'append remark successful')
    }

    private static Date getYesterday() {
        Calendar calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        return calendar.getTime()
    }

    private static Date getLastWeek() {
        Calendar calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -7)
        return calendar.getTime()
    }

    private static Date getLastMonth() {
        Calendar calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        return calendar.getTime()
    }

    private static Date getNow() {
        Calendar calendar = Calendar.getInstance()
        return calendar.getTime()
    }

    @RequestMapping('/summary')
    @AuthenticationAnnotation
    ServerResponse summary() {
        List<Customer> todayCreate = customerService.findAllByCreateDateBetween(getYesterday(), getNow())
        List<Customer> todayUpdate = customerService.findAllByLastModifiedDateBetween(getYesterday(), getNow())
        List<Customer> weekCreate = customerService.findAllByCreateDateBetween(getLastWeek(), getNow())
        List<Customer> weekUpdate = customerService.findAllByLastModifiedDateBetween(getLastWeek(), getNow())
        List<Customer> monthCreate = customerService.findAllByCreateDateBetween(getLastMonth(), getNow())
        List<Customer> monthUpdate = customerService.findAllByLastModifiedDateBetween(getLastMonth(), getNow())
        return new ServerResponse(content: [
                today: [
                        create: todayCreate.size(),
                        update: todayUpdate.size()
                ],
                week : [
                        create: weekCreate.size(),
                        update: weekUpdate.size()
                ],
                month: [
                        create: monthCreate.size(),
                        update: monthUpdate.size()
                ]
        ], message: 'query successful!')
    }

    @RequestMapping('/summary-employee')
    ServerResponse summaryEmployee(String userId) {
        User user = userService.findById(userId)
        List<Customer> todayCreate = customerService.findAllByVisibilityUserGroupIdsAndCreateDateBetween(user.userGroupIds, getYesterday(), getNow())
        List<Customer> todayUpdate = customerService.findAllByVisibilityUserGroupIdsAndLastModifiedDateBetween(user.userGroupIds, getYesterday(), getNow())
        List<Customer> weekCreate = customerService.findAllByVisibilityUserGroupIdsAndCreateDateBetween(user.userGroupIds, getLastWeek(), getNow())
        List<Customer> weekUpdate = customerService.findAllByVisibilityUserGroupIdsAndLastModifiedDateBetween(user.userGroupIds, getLastWeek(), getNow())
        List<Customer> monthCreate = customerService.findAllByVisibilityUserGroupIdsAndCreateDateBetween(user.userGroupIds, getLastMonth(), getNow())
        List<Customer> monthUpdate = customerService.findAllByVisibilityUserGroupIdsAndLastModifiedDateBetween(user.userGroupIds, getLastMonth(), getNow())
        return new ServerResponse(content: [
                today: [
                        create: todayCreate.size(),
                        update: todayUpdate.size()
                ],
                week : [
                        create: weekCreate.size(),
                        update: weekUpdate.size()
                ],
                month: [
                        create: monthCreate.size(),
                        update: monthUpdate.size()
                ]
        ], message: 'query successful!')
    }

    @RequestMapping('/page-employee')
    ServerResponse pageEmployee(String userId, int pageIndex, int pageSize) {
        User user = userService.findById(userId)
        Page<Customer> customerPage = customerService.findAllByVisibilityUserGroupIdsOrderByLastModifiedDateDesc(user.userGroupIds, pageIndex, pageSize)
        List<Customer> customers = customerPage.getContent()
        sortCustomerRemarksNew(customers)
        return new ServerResponse(content: [
                customers: customers,
                total: customerPage.getTotalElements(),
                currentPageIndex: customerPage.getNumber() + 1
        ], message: 'query successful')
    }

    @RequestMapping('/save')
    @AuthenticationAnnotation
    ServerResponse create(Customer customer) {
        customer.visibilityUserGroups = []
        if (!customer.remarks.isEmpty()) {
            User user = userService.findByUsername(jwt.getUsername(SecurityUtils.getSubject().getPrincipal().toString()))
            UserInformation userInformation = userInformationService.findByUserId(user?.id)
            customer.remarksNew.add(new Remark(remark: customer.remarks, user: new User(username: user?.username), userInformation: new UserInformation(name: userInformation?.name), createDate: Calendar.getInstance().getTime()))
            customer.remarks = null
        }
        return new ServerResponse(content: customerService.save(customer), message: 'save successful')
    }

    @RequestMapping('/update')
    @AuthenticationAnnotation
    ServerResponse update(Customer customer) {
        Customer updateCustomer = customerService.findById(customer.id)
        updateCustomer.name = customer.name
        updateCustomer.sex = customer.sex
        updateCustomer.phoneNumber = customer.phoneNumber
        updateCustomer.customerTypeId = customer.customerTypeId
        updateCustomer.visibilityUserGroupIds = customer.visibilityUserGroupIds
        return new ServerResponse(content: customerService.save(updateCustomer), message: 'update successful')
    }

    @RequestMapping('/delete')
    @AuthenticationAnnotation
    ServerResponse delete(String customerId) {
        return new ServerResponse(content: customerService.deleteById(customerId), message: 'delete successful')
    }

    @RequestMapping('/delete-all')
    @AuthenticationAnnotation
    ServerResponse deleteAll(String[] customerIds) {
        return new ServerResponse(content: customerService.deleteAll(customerIds.toList()), message: 'delete successful')
    }

    @RequestMapping('/visibility')
    @AuthenticationAnnotation
    ServerResponse visibility(String customerId, String[] userGroupIds) {
        Customer customer = customerService.findById(customerId)
        customer.visibilityUserGroupIds = userGroupIds
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

    @RequestMapping('/visibility-all')
    @AuthenticationAnnotation
    ServerResponse visibilityAll(String[] customerIds, String[] userGroupIds) {
        customerIds?.each { customerId ->
            Customer customer = customerService.findById(customerId)
            customer.visibilityUserGroupIds = userGroupIds
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
