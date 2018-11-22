package smart.management.incoming_customer

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface IncomingCustomerRepository extends PagingAndSortingRepository<IncomingCustomer, String> {

    List<IncomingCustomer> findAllByVisibilityUserGroupIds(String visibilityUserGroupIds)

}
