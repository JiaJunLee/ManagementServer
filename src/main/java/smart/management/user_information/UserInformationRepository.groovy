package smart.management.user_information

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserInformationRepository extends PagingAndSortingRepository<UserInformation, String> {

}
