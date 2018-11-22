package smart.management.incoming_customer

import org.springframework.data.annotation.Transient
import smart.management.common.BaseDocument
import smart.management.common.Sex
import smart.management.user_group.UserGroup

import javax.validation.constraints.NotNull

class IncomingCustomer extends BaseDocument {

    class VisibilityUserGroupAlreadyExists extends Exception {}
    class VisibilityUserGroupNotExists extends Exception {}

    @NotNull List<String> visibilityUserGroupIds = []
    @Transient List<UserGroup> visibilityUserGroups = []

    @NotNull String name
    @NotNull String phoneNumber
    @NotNull Sex sex = Sex.UNKNOWN
    @NotNull String income
    @NotNull String cost

    void addVisibilityUserGroupId(String userGroupId) {
        if (visibilityUserGroupIds.contains(userGroupId)) {
            throw new VisibilityUserGroupAlreadyExists()
        }
        this.visibilityUserGroupIds.add(userGroupId)
    }

    void removeVisibilityUserGroupId(String userGroupId) {
        if (!visibilityUserGroupIds.contains(userGroupId)) {
            throw new VisibilityUserGroupNotExists()
        }
        this.visibilityUserGroupIds.remove(userGroupId)
    }

}
