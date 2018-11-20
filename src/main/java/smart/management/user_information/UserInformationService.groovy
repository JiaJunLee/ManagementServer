package smart.management.user_information

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Service
import smart.management.common.BaseService

@Service
class UserInformationService extends BaseService<UserInformation, String> {

    @Autowired UserInformationRepository userInformationRepository

    @Override
    PagingAndSortingRepository<UserInformation, String> getRepository() {
        return this.userInformationRepository
    }

    UserInformation findByUserId(String userId) {
        return userInformationRepository.findByUserId(userId)
    }

}
