package smart.management.customer_type

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerTypeRepository extends PagingAndSortingRepository<CustomerType, String> {
}
