package org.crygier.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.GraphQLError;
import groovy.transform.CompileStatic;
import org.crygier.graphql.annotation.GRequestMapping;
import org.crygier.graphql.annotation.GRestController;
import org.crygier.graphql.model.users.Role;
import org.crygier.graphql.model.users.User;
import org.crygier.graphql.repo.UserRepository;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GRestController("ABC")
@RestController
@Validated
class GraphQlController {

    @Autowired
    private GraphQLExecutor graphQLExecutor;

    @Autowired
    private ObjectMapper objectMapper;



    @Autowired
    UserRepository userRepository;
    @CrossOrigin(origins = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.OPTIONS},maxAge=1800L,allowedHeaders ="*")
    @RequestMapping(path = "/graphql")
    ExecutionResult graphQl(@RequestBody Map<String,Object> map) throws IOException {
        Map<String, Object> vsmap = null;

        Object va=map.get("variables");
        if(va==null){
            vsmap=null;
        }else if(va instanceof String){
            vsmap=StringUtils.hasText((String)va)? objectMapper.readValue((String)va, Map.class):null;
        }else{//map
            vsmap=(Map<String, Object>)va;
        }


        GraphQLInputQuery query=null;
        ExecutionResult result=graphQLExecutor.execute(map.get("query").toString(),vsmap);
       // if(result.getErrors()!=null && result.getErrors().size()==0){
            result=new ExecutionResultBos(result.getData(),result.getErrors(),result.getExtensions());
        //}
        return result;
    }

    /**
     * 为了解决一个数据返回规范的问题，前端用graphql-cli/playground的时候，收到后端数据返回时如果发现有errors字段（不管是不是null，是不是为空数组）
     * 都会认为有错误；而ExecutionResultImpl里的toSpecification里是说如果不为null则肯定要返回，为null才不设置该字段），这两者之间有冲突
     * 到底谁对谁错后面需要研究清楚，然后给相应的项目提issue～ TODO
     * 这里先覆盖一个方法把问题先解决。
     */
    static final class ExecutionResultBos extends ExecutionResultImpl{


        public ExecutionResultBos(Object data, List<? extends GraphQLError> errors, Map<Object, Object> extensions) {
            super(data, errors, extensions);

        }

        @Override
        public List<GraphQLError> getErrors() {
            List<GraphQLError> errors=super.getErrors();
            if(errors!=null && errors.size()==0){
                return null;
            }
            return errors;
        }


    }
    public static final class GraphQLInputQuery {
        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getVariables() {
            return variables;
        }

        public void setVariables(String variables) {
            this.variables = variables;
        }

        String query;
        String variables;
    }



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


    @GRequestMapping(path = "/updateuser", method = RequestMethod.POST)
    List<User> createAcceptance(@Length(min = 8, max = 10)  @RequestParam(name="userid",required = true) String userid) {
        List<User> userList=new ArrayList<>();
        userList.add(this.userRepository.findById(userid).orElse(null));
        return userList;
    }


    @GRequestMapping(path = "/createAcceptance", method = RequestMethod.POST)
    User createAcceptance(@RequestParam(name="client",required = true)User client) {
        return this.userRepository.save(client);
    }

    //  @Validate(msg="一定要有姓名和id",value="exist('role{id} &&  id ')")
    @RequestMapping(path = "/abc", method = RequestMethod.POST)
    User create(  Role role, @RequestParam(name="id",required = false)String id, @RequestParam(name="count",required = true)int count) {
        return new User();
    }

}