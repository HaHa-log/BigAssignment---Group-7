package Client.Controllers.MainPage.ProfilePage;

import Branch.User;

public abstract class BaseController {
    protected User user;

    protected static ProfilePageController navigation;

    public static void setNavigation(ProfilePageController nav) {
        navigation = nav;
    }

    public void setUser(User user) {
        this.user = user;
        initData();
    }

    protected void initData() {}
}
