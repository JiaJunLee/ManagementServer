package smart.management.incoming_customer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Service
import smart.management.common.BaseService

@Service
class IncomingCustomerService extends BaseService<IncomingCustomer, String> {

    @Autowired IncomingCustomerRepository incomingCustomerRepository

    @Override
    PagingAndSortingRepository<IncomingCustomer, String> getRepository() {
        return this.incomingCustomerRepository
    }

    List<IncomingCustomer> findAllByVisibilityUserGroupIds(List<String> userGroupIds) {
        List<IncomingCustomer> incomingCustomers = []
        userGroupIds?.each { currentUserGroupId ->
            incomingCustomers.addAll(incomingCustomerRepository.findAllByVisibilityUserGroupIds(currentUserGroupId))
        }
        return incomingCustomers
    }

    Page<IncomingCustomer> getPage(int pageIndex, int pageSize) {
        return incomingCustomerRepository.findAll(new PageRequest(pageIndex, pageSize, new Sort(new Sort.Order(Sort.Direction.DESC, 'lastModifiedDate'))))
    }

    Page<IncomingCustomer> findAllByVisibilityUserGroupIdsOrderByLastModifiedDateDesc(List<String> userGroupIds, int pageIndex, int pageSize) {
        return incomingCustomerRepository.findAllByVisibilityUserGroupIdsIn(userGroupIds, new PageRequest(pageIndex, pageSize, new Sort(new Sort.Order(Sort.Direction.DESC, 'lastModifiedDate'))))
    }

    List<IncomingCustomer> findAllByCreateDateBetween(Date min, Date max) {
        return incomingCustomerRepository.findAllByCreateDateBetween(min, max)
    }

    List<IncomingCustomer> findAllByVisibilityUserGroupIdsAndCreateDateBetween(List<String> userGroupIds, Date min, Date max) {
        List<IncomingCustomer> incomingCustomers = []
        userGroupIds?.each { currentUserGroupId ->
            incomingCustomers.addAll(incomingCustomerRepository.findAllByVisibilityUserGroupIdsAndCreateDateBetween(currentUserGroupId, min, max))
        }
        return incomingCustomers
    }

    List<IncomingCustomer> findAllByLastModifiedDateBetween(Date min, Date max) {
        return incomingCustomerRepository.findAllByLastModifiedDateBetween(min, max)
    }

    List<IncomingCustomer> findAllByVisibilityUserGroupIdsAndLastModifiedDateBetween(List<String> userGroupIds, Date min, Date max) {
        List<IncomingCustomer> incomingCustomers = []
        userGroupIds?.each { currentUserGroupId ->
            incomingCustomers.addAll(incomingCustomerRepository.findAllByVisibilityUserGroupIdsAndLastModifiedDateBetween(currentUserGroupId, min, max))
        }
        return incomingCustomers
    }

}
