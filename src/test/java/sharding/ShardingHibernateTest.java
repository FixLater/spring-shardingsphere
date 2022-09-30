package sharding;

import com.hyk.sharding.entity.User;
import com.hyk.sharding.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-sharding.xml"})
public class ShardingHibernateTest {

    @Autowired
    private UserService userService;

    @Test
    public void testFindAll(){
        List<User> users = userService.findAll();
        System.out.println(users.size());
        if(!users.isEmpty()){
            for(User u :users){
                System.out.println(u);
            }
        }
    }
}
