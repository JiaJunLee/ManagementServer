package smart.management.user_group

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Service
import smart.management.common.BaseService

@Service
class UserGroupService extends BaseService<UserGroup, String> {

    @Autowired UserGroupRepository userGroupRepository

    @Override
    PagingAndSortingRepository<UserGroup, String> getRepository() {
        return this.userGroupRepository
    }

}
