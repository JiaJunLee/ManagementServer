package smart.management.customer

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository extends PagingAndSortingRepository<Customer, String> {

    List<Customer> findAllByVisibilityUserGroupIdsOrderByLastModifiedDateDesc(String visibilityUserGroupIds)

    List<Customer> findAllByCreateDateBetween(Date min, Date max)

    List<Customer> findAllByVisibilityUserGroupIdsAndCreateDateBetween(String visibilityUserGroupIds, Date min, Date max)

    List<Customer> findAllByLastModifiedDateBetween(Date min, Date max)

    List<Customer> findAllByVisibilityUserGroupIdsAndLastModifiedDateBetween(String visibilityUserGroupIds, Date min, Date max)

    Page<Customer> findAllByVisibilityUserGroupIdsIn(List<String> visibilityUserGroupIds, Pageable pageable)

}
