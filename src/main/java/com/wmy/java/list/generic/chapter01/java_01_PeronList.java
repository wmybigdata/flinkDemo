package com.wmy.java.list.generic.chapter01;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.list.generic.chapter01
 * @Author: wmy
 * @Date: 2021/9/24
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class java_01_PeronList {

    @Test
    public void testPersonList() {
        PersonList personList = new PersonList();
        // 在写代码的时候可以在泛型这里做一些手脚
        // 第一种情况
        // 这个可以传入Number的子类都是可以的
        Person<String, Integer> person = new Person<String, Integer>("张三",18);
        Person<String, Number> person1 = new Person<String, Number>("张三",18);
        Person<String, Number> person2 = new Person<String, Number>("张三",Integer.valueOf(18));
        personList.addPerson(person2);

        // 第二种情况
        //personList.addPerson1(person); // 这种情况是会报错的 Integer，加不进去，期待的类型是Number
        personList.addPerson1(person1); // 这种情况是可以的

        // 如果需要添加的类型包括子类：? extends Number
        personList.addPerson(person); // 加了之后就可以进行登录 了

        // 看看能不能插入
        person1.setEle(Integer.valueOf(100));
        System.out.println(person1.getEle());


        // 看看能不能成功
        Person<String, Object> person3 = new Person<>("赵六", 18);
        //personList.addPerson(person3); // 这个就会报错
    }

    @Test
    public void superDemo() {
        PersonList personList = new PersonList();
        Person<String, Integer> person = new Person<String, Integer>("张三",18);
        Person<String, Number> person1 = new Person<String, Number>("张三",Integer.valueOf(18));
        personList.setPerson(person);
        personList.setPerson(person1);

        Person<String, Object> person2 = new Person<>("赵六", 18);
        personList.setPerson(person2);
    }

    public static class PersonList {
        // extends传入的参数的类型必须是子类类型
        // super类型传入的类型必须是父类类型
        public void addPerson(Person<String, ? extends Number> person) {
            Number ele = person.getEle(); // 这个一定是一个Number类型的
            // 泛型的擦拭法是不能写的，这样会报错的，擦拭法得到的object和期望得到的数据是不一样的
            System.out.println(person);
        }

        public void addPerson1(Person<String, Number> person) {
            System.out.println(person);
        }

        public void setPerson(Person<String, ? super Integer> person) {
            person.setEle(1);
            System.out.println(person);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Person<T, E> {
        private T prop ;
        private E ele ;

        @Override
        public String toString() {
            return "Person{" +
                    "prop=" + prop +
                    ", ele=" + ele +
                    '}';
        }
    }
}
