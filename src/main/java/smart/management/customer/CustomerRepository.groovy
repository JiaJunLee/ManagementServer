package smart.management.customer

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository extends PagingAndSortingRepository<Customer, String> {

    List<Customer> findAllByVisibilityUserGroupIds(String visibilityUserGroupIds)

}
