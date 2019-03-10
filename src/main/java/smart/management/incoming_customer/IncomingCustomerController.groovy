package smart.management.incoming_customer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
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

    @RequestMapping('/page')
    @AuthenticationAnnotation
    ServerResponse page(int pageIndex, int pageSize) {
        Page<IncomingCustomer> incomingCustomerPage = incomingCustomerService.getPage(pageIndex, pageSize)
        List<IncomingCustomer> incomingCustomers = incomingCustomerPage.getContent()
        incomingCustomers.each { currentIncomingCustomer ->
            currentIncomingCustomer?.visibilityUserGroupIds?.each { userGroupId ->
                UserGroup userGroup = userGroupService.findById(userGroupId)
                if (userGroup) {
                    currentIncomingCustomer.visibilityUserGroups.add(userGroup)
                }
            }
        }
        return new ServerResponse(content: [
                incomingCustomers: incomingCustomers,
                total: incomingCustomerPage.getTotalElements(),
                currentPageIndex: incomingCustomerPage.getNumber() + 1
        ], message: 'query successful')
    }

    @RequestMapping('/page-employee')
    ServerResponse pageEmployee(String userId, int pageIndex, int pageSize) {
        User user = userService.findById(userId)
        Page<IncomingCustomer> incomingCustomerPage = incomingCustomerService.findAllByVisibilityUserGroupIdsOrderByLastModifiedDateDesc(user.userGroupIds, pageIndex, pageSize)
        List<IncomingCustomer> incomingCustomers = incomingCustomerPage.getContent()
        return new ServerResponse(content: [
                incomingCustomers: incomingCustomers,
                total: incomingCustomerPage.getTotalElements(),
                currentPageIndex: incomingCustomerPage.getNumber() + 1
        ], message: 'query successful')
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
        List<IncomingCustomer> todayCreate = incomingCustomerService.findAllByCreateDateBetween(getYesterday(), getNow())
        List<IncomingCustomer> todayUpdate = incomingCustomerService.findAllByLastModifiedDateBetween(getYesterday(), getNow())
        List<IncomingCustomer> weekCreate = incomingCustomerService.findAllByCreateDateBetween(getLastWeek(), getNow())
        List<IncomingCustomer> weekUpdate = incomingCustomerService.findAllByLastModifiedDateBetween(getLastWeek(), getNow())
        List<IncomingCustomer> monthCreate = incomingCustomerService.findAllByCreateDateBetween(getLastMonth(), getNow())
        List<IncomingCustomer> monthUpdate = incomingCustomerService.findAllByLastModifiedDateBetween(getLastMonth(), getNow())
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
        List<IncomingCustomer> todayCreate = incomingCustomerService.findAllByVisibilityUserGroupIdsAndCreateDateBetween(user.userGroupIds, getYesterday(), getNow())
        List<IncomingCustomer> todayUpdate = incomingCustomerService.findAllByVisibilityUserGroupIdsAndLastModifiedDateBetween(user.userGroupIds, getYesterday(), getNow())
        List<IncomingCustomer> weekCreate = incomingCustomerService.findAllByVisibilityUserGroupIdsAndCreateDateBetween(user.userGroupIds, getLastWeek(), getNow())
        List<IncomingCustomer> weekUpdate = incomingCustomerService.findAllByVisibilityUserGroupIdsAndLastModifiedDateBetween(user.userGroupIds, getLastWeek(), getNow())
        List<IncomingCustomer> monthCreate = incomingCustomerService.findAllByVisibilityUserGroupIdsAndCreateDateBetween(user.userGroupIds, getLastMonth(), getNow())
        List<IncomingCustomer> monthUpdate = incomingCustomerService.findAllByVisibilityUserGroupIdsAndLastModifiedDateBetween(user.userGroupIds, getLastMonth(), getNow())
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

    @RequestMapping('/update')
    @AuthenticationAnnotation
    ServerResponse update(IncomingCustomer incomingCustomer) {
        IncomingCustomer updateIncomingCustomer = incomingCustomerService.findById(incomingCustomer.id)
        updateIncomingCustomer.name = incomingCustomer.name
        updateIncomingCustomer.sex = incomingCustomer.sex
        updateIncomingCustomer.phoneNumber = incomingCustomer.phoneNumber
        updateIncomingCustomer.customerTypeId = incomingCustomer.customerTypeId
        updateIncomingCustomer.income = incomingCustomer.income
        updateIncomingCustomer.cost = incomingCustomer.cost
        updateIncomingCustomer.visibilityUserGroupIds = incomingCustomer.visibilityUserGroupIds
        return new ServerResponse(content: incomingCustomerService.save(updateIncomingCustomer), message: 'update successful')
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

    @RequestMapping('/delete-all')
    @AuthenticationAnnotation
    ServerResponse deleteAll(String[] incomingCustomerIds) {
        return new ServerResponse(content: incomingCustomerService.deleteAll(incomingCustomerIds.toList()), message: 'delete successful')
    }

    @RequestMapping('/visibility')
    @AuthenticationAnnotation
    ServerResponse visibility(String incomingCustomerId, String[] userGroupIds) {
        IncomingCustomer incomingCustomer = incomingCustomerService.findById(incomingCustomerId)
        incomingCustomer.visibilityUserGroupIds = userGroupIds
        incomingCustomerService.save(incomingCustomer)
        return new ServerResponse(content: incomingCustomer, message: 'add visibility user group successful')
    }

    @RequestMapping('/visibility-all')
    @AuthenticationAnnotation
    ServerResponse visibilityAll(String[] incomingCustomerIds, String[] userGroupIds) {
        incomingCustomerIds?.each { incomingCustomerId ->
            IncomingCustomer incomingCustomer = incomingCustomerService.findById(incomingCustomerId)
            incomingCustomer.visibilityUserGroupIds = userGroupIds
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
