package pl.sda.treasury.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final UserService userService;
    private final ChildService childService;
    Authentication authentication;

    public boolean isParent(Long childId){
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByLogin(authentication.getName()).getChildren().contains(childService.find(childId));
    }
    public boolean isTreasurer(Long childId){
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByLogin(authentication.getName()).getSchoolClasses().contains(childService.find(childId).getSchoolClass());
    }

    public boolean isThisUser(Long id){
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByLogin(authentication.getName()).getId() == id;
    }


}
