package evolution.service;


import evolution.dao.UserDao;
import evolution.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Admin on 01.03.2017.
 */
@Service
public class UserDetailsServiceImpl
        implements UserDetailsService {

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user;
        try {
            user = userDao.findByLogin(s);
        } catch (NoResultException e) {
            throw new UsernameNotFoundException("user " + s + " not found");
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
        CustomUser customUser = new CustomUser(
                user.getLogin(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                grantedAuthorities,
                user.getId()
        );
        return customUser;
    }

    public class CustomUser extends org.springframework.security.core.userdetails.User {

        public CustomUser(
                String username, String password,
                boolean enabled, boolean accountNonExpired,
                boolean credentialsNonExpired, boolean accountNonLocked,
                Collection<? extends GrantedAuthority> authorities,
                long id) {
            super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
            this.id = id;
        }

        public long getId() {
            return id;
        }

        private final long id;
    }

    @Autowired
    private UserDao userDao;
}