package org.crygier.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ExecutionResult
import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.GRequestMapping
import org.crygier.graphql.annotation.GRestController
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.model.entity.Department
import org.crygier.graphql.model.users.Privi
import org.crygier.graphql.model.users.PriviGroup
import org.crygier.graphql.model.users.Role
import org.crygier.graphql.model.users.User
import org.crygier.graphql.repo.DepartmentRepository
import org.crygier.graphql.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@GRestController("ABC")
@RestController
@CompileStatic
class GraphQlController {

    @Autowired
    private GraphQLExecutor graphQLExecutor;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    DepartmentRepository departmentRepository;


    @Autowired
    UserRepository userRepository;

    @RequestMapping(path = '/graphql', method = RequestMethod.POST)
    ExecutionResult graphQl(@RequestBody final GraphQLInputQuery query) {
        Map<String, Object> variables = query.getVariables() ? objectMapper.readValue(query.getVariables(), Map) : null;

        return graphQLExecutor.execute(query.getQuery(), variables);
    }

    public static final class GraphQLInputQuery {
        String query;
        String variables;
    }

/**
 * Graphql
 * @param User
 * @return
 */

   //@SchemaDocumentation("GraphQlController.create测试下行不行")
  //  @Validate(msg="一定要有姓名和id",value="exist('role{id}')")
  //  @Validate(msg="一定要有姓名和id",value="exist('role{name,tel}')")
  //  @Validate(msg="一定要有姓名和id",value="exist('role{id,name}')")

    //1.赋值
    //2.验证
    //3.准备数据



   /*

    UserService{



        updatePassword(String id, String pwd){
            findByid(id).set(pwd);
        }

        @Assert("exist('client(id,number)')")
        createAcceptance(
        @Item("client")
        Client client){


        }
    }
*/


    @GRequestMapping(path = '/updateuser', method = RequestMethod.POST)
    List<User> createAcceptance(@RequestParam(name="userid",required = true)String userid) {
        List<User> userList=new ArrayList<>();
        userList.add(this.userRepository.findById(userid).orElse(null));
        return userList;
    }



    @GRequestMapping(path = '/createAcceptance', method = RequestMethod.POST)
    User createAcceptance(@RequestParam(name="client",required = true)User client) {
        return this.userRepository.save(client);
    }

    //  @Validate(msg="一定要有姓名和id",value="exist('role{id} &&  id ')")
    @GRequestMapping(path = '/abc', method = RequestMethod.POST)
    Department create(@RequestParam(name="role",required = true)Role role, @RequestParam(name="id",required = false)String id, @RequestParam(name="count",required = true)int count) {
        System.out.println(new User().getId());
        return new Department();//new User();
    }


    @GRequestMapping(path = '/addDepartment', method = RequestMethod.POST)
    Department create(@RequestParam(name="depart",required = true)Department depart) {
        return this.departmentRepository.save(depart);

    }
    @SchemaDocumentation("根据id修改部门信息，并且返回部门信息")
    @GRequestMapping(path = '/retriveDepartment', method = RequestMethod.POST)
    Department create(@RequestParam(name="id",required = true)String id) {
        return this.departmentRepository.findById(id).orElse(null);
    }
}