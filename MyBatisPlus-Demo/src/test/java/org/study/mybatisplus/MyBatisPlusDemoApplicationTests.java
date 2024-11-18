package org.study.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.study.mybatisplus.dao.UserMapper;
import org.study.mybatisplus.entity.UserEntity;
import org.study.mybatisplus.service.UserService;

import java.util.List;

@SpringBootTest
class MyBatisPlusDemoApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Test
    public void print() {
        System.out.println(("----- selectAll method test ------"));
        List<UserEntity> userList = userMapper.selectList(null);
        // TODO 这个 Assert也是MyBatisPlus的
        // Assert.isTrue(5 == userList.size(), "");
        userList.forEach(System.out::println);
    }

    @Test
    public void save() {
        // 假设有一个 User 实体对象
        UserEntity user = new UserEntity();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        boolean result = userService.save(user); // 调用 save 方法
        if (result) {
            System.out.println("User saved successfully.");
        } else {
            System.out.println("Failed to save user.");
        }
        print();
    }

    @Test
    public void queryWrapper() {
        // NOTE
        //  QueryWrapper: 条件构造器，一个「用来声明查询条件的类」
        //  还有 UpdateWrapper、LambdaQueryWrapper等，详见 https://baomidou.com/guides/wrapper/
        List<UserEntity> userList = userMapper.selectList(new QueryWrapper<UserEntity>().eq("name", "John Doe"));
        // NOTE
        //  forEach是Iterable接口声明的方法，List<-Collection<-Iterable
        //  forEach(Consumer<? super T> action): Consumer只包含一个accept抽象方法，是一个函数式接口
        //  可以使用Lambda 表达式或方法引用简化代码（将「实现接口的类对象」简化为「Lambda表达式或方法引用」）
        //  方法引用: System.out::println
        userList.forEach(System.out::println);
    }

    @Test
    public void update() {
        print();
        // 假设有一个 UpdateWrapper 对象，设置更新条件为 name = 'John Doe'，更新字段为 email
        UpdateWrapper<UserEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("name", "John Doe").set("email", "john.doe@gmail.com");
        boolean result = userService.update(updateWrapper); // 调用 update 方法
        if (result) {
            System.out.println("Record updated successfully.");
        } else {
            System.out.println("Failed to update record.");
        }
        print();
    }

}
