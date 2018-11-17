package smart.management.customer

import smart.management.common.BaseDocument
import smart.management.common.Sex

import javax.validation.constraints.NotNull

class Customer extends BaseDocument {

    class VisibilityUserGroupAlreadyExists extends Exception {}
    class VisibilityUserGroupNotExists extends Exception {}

    @NotNull List<String> visibilityUserGroupIds = [:]

    @NotNull String name
    @NotNull String phoneNumber
    @NotNull Sex sex = Sex.UNKNOWN
    String remarks

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
