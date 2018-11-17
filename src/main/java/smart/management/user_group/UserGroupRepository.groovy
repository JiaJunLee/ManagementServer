package smart.management.user_group

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRepository extends PagingAndSortingRepository<UserGroup, String> {

}
