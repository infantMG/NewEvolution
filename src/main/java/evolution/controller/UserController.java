package evolution.controller;





import com.fasterxml.jackson.core.JsonProcessingException;
import evolution.dao.FriendsDao;
import evolution.dao.UserDao;
import evolution.model.friend.Friends;
import evolution.model.user.User;
import evolution.service.MyJacksonService;
import evolution.service.SearchService;
import evolution.service.builder.JsonInformationBuilder;
import evolution.service.security.UserDetailsServiceImpl;
import evolution.service.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * Created by Admin on 05.03.2017.
 */
@Controller
@RequestMapping ("/user")
@SessionAttributes(value = {"role", "productList", "user"})
public class UserController {

    @RequestMapping (value = "/id{id}", method = RequestMethod.GET)
    public String home (
            @PathVariable Long id,
            Model model,
            SessionStatus sessionStatus,
            HttpServletRequest request) {
        try {
            sessionStatus.setComplete();
            User authUser = (User) request.getSession().getAttribute("authUser");
            if (authUser.getId().equals(id)) {
                model.addAttribute("user", authUser);
            } else {
                Friends friends = friendsDao.findUserAndFriendStatus(authUser.getId(), id);
                model.addAttribute("user", friends.getUser());
                model.addAttribute("status", friends.getStatus());
            }
            return "user/my-home";
        } catch (Exception e){
            return "redirect:/logout";
        }
    }

    // EDIT
    @ResponseBody @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces={"application/json; charset=UTF-8"})
    public String edit(@RequestBody String json,
                        @PathVariable Long id,
                        @SessionAttribute User user,
                        @AuthenticationPrincipal UserDetailsServiceImpl.CustomUser customUser,
                        HttpServletRequest request) throws IOException {

        User userRequest = (User) jacksonService.jsonToObject(json, User.class);
        // self update
        if (customUser.getUser().getId().equals(id)) {
            userRequest.setId(customUser.getUser().getId());
            userRequest.setLogin(customUser.getUser().getLogin());
            userRequest.setRegistrationDate(customUser.getUser().getRegistrationDate());
            userRequest.setRoleId(customUser.getUser().getRoleId());
        }
        //other update
        else if (request.isUserInRole("ROLE_ADMIN")) {
            userRequest.setId(user.getId());
            userRequest.setRegistrationDate(user.getRegistrationDate());

            // МОЖНО БУДЕТ МЕНЯТЬ И ОТПРАВИТЬ ПИСЬМО О СМЕНЕ ПОЧТЫ
            userRequest.setLogin(user.getLogin());
        }

        if (validator.userValidator(userRequest)) {
            userDao.update(userRequest);
            if (customUser.getUser().getId().equals(id))
                customUser.getUser().updateFields(userRequest);
            return jsonInformationBuilder.buildJson(HttpStatus.OK.toString(), null, true);
        }

        return jsonInformationBuilder.buildJson(HttpStatus.OK.toString(), null, false);
    }


    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public void userPut(@RequestBody String json) throws IOException {
        User user = (User) jacksonService.jsonToObject(json, User.class);
        userDao.update(user);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public void userPost(@RequestBody String json) throws IOException {
        User user = (User) jacksonService.jsonToObject(json, User.class);
        userDao.save(user);
    }

    // DELETE
    @ResponseBody @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetailsServiceImpl.CustomUser  customUser,
                           HttpServletRequest request) {
//        if (request.isUserInRole("ROLE_ADMIN") || customUser.getUser().getId().equals(id)){
//            if (id != 226)
//                userDao.delete(new User(id));
//        }
    }

    // GET FORM PROFILE
    @RequestMapping(value = "/profile/{id}", method = RequestMethod.GET)
    public String profile(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetailsServiceImpl.CustomUser customUser,
                          HttpServletRequest request,
                          Model model){
        if (id.equals(customUser.getUser().getId())) {
            model.addAttribute("user", customUser.getUser());
            return "user/form-my-profile";
        } else if (request.isUserInRole("ROLE_ADMIN")) {
            model.addAttribute("user", userDao.findById(id));
            return "admin/admin-form-profile";
        }

        return "redirect:/welcome";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String adminFormRegistration() {
        return "testing/form-create-user";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String viewSearch(){
        return "user/new-search";
    }


    @ResponseBody @RequestMapping(value = "/search-result", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
    public String resultSearch(@RequestParam String like) throws JsonProcessingException {
        return jacksonService.objectToJson(searchService.searchUser(like));
    }


    @Autowired
    private JsonInformationBuilder jsonInformationBuilder;
    @Autowired
    private UserDao userDao;
    @Autowired
    private MyJacksonService jacksonService;
    @Autowired
    private FriendsDao friendsDao;
    @Autowired
    private Validator validator;
    @Autowired
    private SearchService searchService;
}
