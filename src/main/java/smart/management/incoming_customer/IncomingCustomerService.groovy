package smart.management.incoming_customer

import org.springframework.beans.factory.annotation.Autowired
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

}
