package smart.management.customer

import org.springframework.beans.factory.annotation.Autowired
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
            customers.addAll(customerRepository.findAllByVisibilityUserGroupIds(currentUserGroupId))
        }
        return customers
    }

}
