package smart.management.incoming_customer

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface IncomingCustomerRepository extends PagingAndSortingRepository<IncomingCustomer, String> {

    List<IncomingCustomer> findAllByVisibilityUserGroupIds(String visibilityUserGroupIds)

    Page<IncomingCustomer> findAllByVisibilityUserGroupIdsIn(List<String> visibilityUserGroupIds, Pageable pageable)

    List<IncomingCustomer> findAllByCreateDateBetween(Date min, Date max)

    List<IncomingCustomer> findAllByVisibilityUserGroupIdsAndCreateDateBetween(String visibilityUserGroupIds, Date min, Date max)

    List<IncomingCustomer> findAllByLastModifiedDateBetween(Date min, Date max)

    List<IncomingCustomer> findAllByVisibilityUserGroupIdsAndLastModifiedDateBetween(String visibilityUserGroupIds, Date min, Date max)

}
