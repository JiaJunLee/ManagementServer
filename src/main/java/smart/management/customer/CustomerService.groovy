package smart.management.customer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Service
import smart.management.common.BaseService

@Service
class CustomerService extends BaseService<Customer, String> {

    @Autowired CustomerRepository customerRepository

    @Override
    PagingAndSortingRepository<Customer, String> getRepository() {
        return this.customerRepository
    }

    List<Customer> findAllByVisibilityUserGroupIds(List<String> userGroupIds) {
        List<Customer> customers = []
        userGroupIds?.each { currentUserGroupId ->
            customers.addAll(customerRepository.findAllByVisibilityUserGroupIdsOrderByLastModifiedDateDesc(currentUserGroupId))
        }
        return customers
    }

    Page<Customer> getPage(int pageIndex, int pageSize) {
        return customerRepository.findAll(new PageRequest(pageIndex, pageSize, new Sort(new Sort.Order(Sort.Direction.DESC, 'lastModifiedDate'))))
    }

    List<Customer> findAllByCreateDateBetween(Date min, Date max) {
        return customerRepository.findAllByCreateDateBetween(min, max)
    }

    List<Customer> findAllByVisibilityUserGroupIdsAndCreateDateBetween(List<String> userGroupIds, Date min, Date max) {
        List<Customer> customers = []
        userGroupIds?.each { currentUserGroupId ->
            customers.addAll(customerRepository.findAllByVisibilityUserGroupIdsAndCreateDateBetween(currentUserGroupId, min, max))
        }
        return customers
    }

    List<Customer> findAllByLastModifiedDateBetween(Date min, Date max) {
        return customerRepository.findAllByLastModifiedDateBetween(min, max)
    }

    List<Customer> findAllByVisibilityUserGroupIdsAndLastModifiedDateBetween(List<String> userGroupIds, Date min, Date max) {
        List<Customer> customers = []
        userGroupIds?.each { currentUserGroupId ->
            customers.addAll(customerRepository.findAllByVisibilityUserGroupIdsAndLastModifiedDateBetween(currentUserGroupId, min, max))
        }
        return customers
    }

    Page<Customer> findAllByVisibilityUserGroupIdsOrderByLastModifiedDateDesc(List<String> userGroupIds, int pageIndex, int pageSize) {
        return customerRepository.findAllByVisibilityUserGroupIdsIn(userGroupIds, new PageRequest(pageIndex, pageSize, new Sort(new Sort.Order(Sort.Direction.DESC, 'lastModifiedDate'))))
    }

}
