package smart.management.user

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import smart.management.user.User.UserType

@Repository
interface UserRepository extends PagingAndSortingRepository<User, String> {

    User findByUsername(String username)

    List<User> findAllByType(UserType type)

}

