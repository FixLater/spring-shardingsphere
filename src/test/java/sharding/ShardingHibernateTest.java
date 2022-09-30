package sharding;

import com.hyk.sharding.entity.User;
import com.hyk.sharding.service.UserService;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.UUID;

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

    @Test
    public void saveInfo() {
        //通过ID分库，id为1 一个库 id为2 一个库 id为3一个库
        User u = new User();
        u.setId(UUID.randomUUID().toString());
        u.setAge(25);
        u.setSchoolId("1");
        u.setName("张三");
        userService.save(u);
        User u1 = new User();
        u1.setId(UUID.randomUUID().toString());
        u1.setAge(25);
        u1.setSchoolId("2");
        u1.setName("李四");
        userService.save(u1);
        User u2 = new User();
        u2.setId(UUID.randomUUID().toString());
        u2.setAge(25);
        u2.setSchoolId("3");
        u2.setName("王二");
        userService.save(u2);
    }
}
