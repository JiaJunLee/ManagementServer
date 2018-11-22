package smart.management.user

import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import smart.management.common.BaseDocument
import smart.management.user_group.UserGroup
import smart.management.user_information.UserInformation

import javax.validation.constraints.NotNull

@Document
class User extends BaseDocument {

    class UserGroupAlreadyJoined extends Exception {}
    class UserGroupNotJoined extends Exception {}

    static final enum UserType {
        ADMINISTRATOR,
        EMPLOYEE
    }

    @NotNull List<String> userGroupIds = []
    @Transient List<UserGroup> userGroups = []

    @NotNull String username
    @NotNull String hsKey
    @NotNull String hsPassword
    UserType type
    UserInformation userInformation

    boolean isAdministrator() {
        return this.type == UserType.ADMINISTRATOR
    }

    boolean isEmployee() {
        return this.type == UserType.EMPLOYEE
    }

    @Override
    String toString() {
        return "username: ${username}, type: ${type}"
    }

    void addUserGroup(String userGroupId) {
        if (userGroupIds.contains(userGroupId)){
            throw new UserGroupAlreadyJoined()
        }
        userGroupIds.add(userGroupId)
    }

    void removeUserGroup(String userGroupId) {
        if (userGroupId.contains(userGroupId)) {
            userGroupIds.remove(userGroupId)
        } else {
            throw new UserGroupNotJoined()
        }
    }

}
