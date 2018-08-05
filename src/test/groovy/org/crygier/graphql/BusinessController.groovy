package org.crygier.graphql.mlshop.controller

import groovy.transform.CompileStatic
import org.crygier.graphql.annotation.GRequestMapping
import org.crygier.graphql.annotation.GRestController
import org.crygier.graphql.annotation.SchemaDocumentation
import org.crygier.graphql.mlshop.model.Administ
import org.crygier.graphql.mlshop.model.Advertisement
import org.crygier.graphql.mlshop.model.CarCommunication

import org.crygier.graphql.mlshop.model.CarInfo
import org.crygier.graphql.mlshop.model.CarSource
import org.crygier.graphql.mlshop.model.ConcernCar
import org.crygier.graphql.mlshop.model.ConcernShop
import org.crygier.graphql.mlshop.model.Customer
import org.crygier.graphql.mlshop.model.Feedback
import org.crygier.graphql.mlshop.model.Insurance
import org.crygier.graphql.mlshop.model.InsuranceCommunication
import org.crygier.graphql.mlshop.model.Order
import org.crygier.graphql.mlshop.model.Salesman
import org.crygier.graphql.mlshop.model.Shop
import org.crygier.graphql.mlshop.repo.AdministRepository
import org.crygier.graphql.mlshop.repo.AdvertisementRepository
import org.crygier.graphql.mlshop.repo.CarCommunicationRepository

import org.crygier.graphql.mlshop.repo.CarInfoRepository
import org.crygier.graphql.mlshop.repo.CarSourceRepository
import org.crygier.graphql.mlshop.repo.CustomerRepository
import org.crygier.graphql.mlshop.repo.FeedbackRepository
import org.crygier.graphql.mlshop.repo.InsuranceCommunicationRepository
import org.crygier.graphql.mlshop.repo.InsuranceRepository
import org.crygier.graphql.mlshop.repo.OrderRepository
import org.crygier.graphql.mlshop.repo.SalesmanRepository
import org.crygier.graphql.mlshop.repo.ShopRepository
import org.crygier.graphql.mlshop.service.CarCommunicationService
import org.crygier.graphql.mlshop.service.ConcernCarService
import org.crygier.graphql.mlshop.service.ConcernShopService
import org.crygier.graphql.mlshop.service.InsuranceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SchemaDocumentation("车辆来源相关的增删改操作")
@GRestController("mlshop")
@RestController
@CompileStatic
public class BusinessController {


    @Autowired
    CarSourceRepository carSourceRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    AdministRepository administRepository;

    @Autowired
    SalesmanRepository salesmanRepository;

    @Autowired
    CarCommunicationRepository carCommunicationRepository;

    @Autowired
    InsuranceRepository insuranceRepository;

    @Autowired
    InsuranceCommunicationRepository insuranceCommunicationRepository;

    @Autowired
    CarInfoRepository carInfoRepository;

    @Autowired
    CarCommunicationService carCommunicationServiceImpl;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ConcernShopService concernShopService;

    @Autowired
    ConcernCarService concernCarService;

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    InsuranceService insuranceService;

    @Autowired
    AdvertisementRepository advertisementRepository;

    @SchemaDocumentation("增加车辆来源")
    @GRequestMapping(path = "/addcarsource", method = RequestMethod.POST)
    CarSource addcarsource(@RequestParam(name = "carsource", required = true) CarSource client) {
        return this.carSourceRepository.save(client);
    }

    @SchemaDocumentation("修改车辆来源")
    @GRequestMapping(path = "/updatecarsource", method = RequestMethod.POST)
    CarSource updatecarsource(@RequestParam(name = "carsource", required = true) CarSource client) {
        return this.carSourceRepository.save(client);
    }

    @SchemaDocumentation("禁用车辆来源")
    @GRequestMapping(path = "/disablecarsource", method = RequestMethod.POST)
    CarSource disablecarsource(@RequestParam(name = "carsource", required = true) CarSource client) {
        client.disabled = true;
        return this.carSourceRepository.save(client);
    }

    @SchemaDocumentation("启用车辆来源")
    @GRequestMapping(path = "/enablcarsource", method = RequestMethod.POST)
    CarSource enablcarsource(@RequestParam(name = "carsource", required = true) CarSource client) {
        client.disabled = false;
        return this.carSourceRepository.save(client);
    }

    @SchemaDocumentation("删除车辆来源")
    @GRequestMapping(path = "/removecarsource", method = RequestMethod.POST)
    CarSource removecarsource(@RequestParam(name = "carsource", required = true) CarSource client) {
        client.disabled = false;
        this.carSourceRepository.deleteById(client.getId());
        return client;
    }


    @SchemaDocumentation("增加门店")
    @GRequestMapping(path = "/addshop", method = RequestMethod.POST)
    Shop addshop(@RequestParam(name = "shop", required = true) Shop client) {
        return this.shopRepository.save(client);
    }

    @SchemaDocumentation("修改门店")
    @GRequestMapping(path = "/updateshop", method = RequestMethod.POST)
    Shop updateshop(@RequestParam(name = "shop", required = true) Shop client) {
        return this.shopRepository.save(client);
    }

    @SchemaDocumentation("禁用门店")
    @GRequestMapping(path = "/disableshop", method = RequestMethod.POST)
    Shop disableshop(@RequestParam(name = "shop", required = true) Shop client) {
        client.disabled = true;
        return this.shopRepository.save(client);
    }

    @SchemaDocumentation("启用门店")
    @GRequestMapping(path = "/enablshop", method = RequestMethod.POST)
    Shop enablshop(@RequestParam(name = "shop", required = true) Shop client) {
        client.disabled = false;
        return this.shopRepository.save(client);
    }

    @SchemaDocumentation("删除门店")
    @GRequestMapping(path = "/removeshop", method = RequestMethod.POST)
    Shop removeshop(@RequestParam(name = "shop", required = true) Shop client) {
        client.disabled = false;
        this.shopRepository.deleteById(client.getId());
        return client;
    }


    @SchemaDocumentation("增加管理员")
    @GRequestMapping(path = "/addadminist", method = RequestMethod.POST)
    Administ addadminist(@RequestParam(name = "administ", required = true) Administ client) {
        return this.administRepository.save(client);
    }

    @SchemaDocumentation("修改管理员")
    @GRequestMapping(path = "/updateadminist", method = RequestMethod.POST)
    Administ updateadminist(@RequestParam(name = "administ", required = true) Administ client) {
        return this.administRepository.save(client);
    }

    @SchemaDocumentation("禁用管理员")
    @GRequestMapping(path = "/disableadminist", method = RequestMethod.POST)
    Administ disableadminist(@RequestParam(name = "administ", required = true) Administ client) {
        client.disabled = true;
        return this.administRepository.save(client);
    }

    @SchemaDocumentation("启用管理员")
    @GRequestMapping(path = "/enabladminist", method = RequestMethod.POST)
    Administ enabladminist(@RequestParam(name = "administ", required = true) Administ client) {
        client.disabled = false;
        return this.administRepository.save(client);
    }

    @SchemaDocumentation("删除管理员")
    @GRequestMapping(path = "/removeadminist", method = RequestMethod.POST)
    Administ removeadminist(@RequestParam(name = "administ", required = true) Administ client) {
        client.disabled = false;
        this.administRepository.deleteById(client.getId());
        return client;
    }


    @SchemaDocumentation("增加业务员")
    @GRequestMapping(path = "/addsalesman", method = RequestMethod.POST)
    Salesman addsalesman(@RequestParam(name = "salesman", required = true) Salesman client) {
        return this.salesmanRepository.save(client);
    }

    @SchemaDocumentation("修改业务员")
    @GRequestMapping(path = "/updatesalesman", method = RequestMethod.POST)
    Salesman updatesalesman(@RequestParam(name = "salesman", required = true) Salesman client) {
        return this.salesmanRepository.save(client);
    }

    @SchemaDocumentation("禁用业务员")
    @GRequestMapping(path = "/disablesalesman", method = RequestMethod.POST)
    Salesman disablesalesman(@RequestParam(name = "salesman", required = true) Salesman client) {
        client.disabled = true;
        return this.salesmanRepository.save(client);
    }

    @SchemaDocumentation("启用业务员")
    @GRequestMapping(path = "/enablsalesman", method = RequestMethod.POST)
    Salesman enablsalesman(@RequestParam(name = "salesman", required = true) Salesman client) {
        client.disabled = false;
        return this.salesmanRepository.save(client);
    }

    @SchemaDocumentation("删除业务员")
    @GRequestMapping(path = "/removesalesman", method = RequestMethod.POST)
    Salesman removesalesman(@RequestParam(name = "salesman", required = true) Salesman client) {
        client.disabled = false;
        this.salesmanRepository.deleteById(client.getId());
        return client;
    }


    @SchemaDocumentation("增加用户信息")
    @GRequestMapping(path = "/addcustomer", method = RequestMethod.POST)
    Customer addcustomer(@RequestParam(name = "customer", required = true) Customer client) {
        return this.customerRepository.save(client);
    }

    @SchemaDocumentation("修改用户信息")
    @GRequestMapping(path = "/updatecustomer", method = RequestMethod.POST)
    Customer updatecustomer(@RequestParam(name = "customer", required = true) Customer client) {
        return this.customerRepository.save(client);
    }

    @SchemaDocumentation("禁用用户信息")
    @GRequestMapping(path = "/disablecustomer", method = RequestMethod.POST)
    Customer disablecustomer(@RequestParam(name = "customer", required = true) Customer client) {
        client.disabled = true;
        return this.customerRepository.save(client);
    }

    @SchemaDocumentation("启用用户信息")
    @GRequestMapping(path = "/enablcustomer", method = RequestMethod.POST)
    Customer enablcustomer(@RequestParam(name = "customer", required = true) Customer client) {
        client.disabled = false;
        return this.customerRepository.save(client);
    }

    @SchemaDocumentation("删除用户信息")
    @GRequestMapping(path = "/removecustomer", method = RequestMethod.POST)
    Customer removecustomer(@RequestParam(name = "customer", required = true) Customer client) {
        client.disabled = false;
        this.customerRepository.deleteById(client.getId());
        return client;
    }

    @SchemaDocumentation("增加买车沟通信息")
    @GRequestMapping(path = "/addcarcommunication", method = RequestMethod.POST)
    CarCommunication addcarCommunication(
            @RequestParam(name = "carcommunication", required = true) CarCommunication carCommunication) {
        carCommunicationServiceImpl.updateCustomer(carCommunication.getCustomer());
        return this.carCommunicationRepository.save(carCommunication);
    }

    @SchemaDocumentation("修改买车沟通信息")
    @GRequestMapping(path = "/updatecarcommunication", method = RequestMethod.POST)
    CarCommunication updatecarCommunication(
            @RequestParam(name = "carcommunication", required = true) CarCommunication carCommunication) {
        carCommunicationServiceImpl.updateCustomer(carCommunication.getCustomer());
        return this.carCommunicationRepository.save(carCommunication);
    }

    @SchemaDocumentation("删除买车沟通信息")
    @GRequestMapping(path = "/removecarcommunication", method = RequestMethod.POST)
    CarCommunication removecarCommunication(
            @RequestParam(name = "carcommunication", required = true) CarCommunication carCommunication) {
        this.customerRepository.deleteById(carCommunication.getId());
        return carCommunication;
    }

    @SchemaDocumentation("增加保险信息")
    @GRequestMapping(path = "/addinsurance", method = RequestMethod.POST)
    Insurance addInsurance(
            @RequestParam(name = "insurance", required = true) Insurance insurance) {
        return this.insuranceService.save(insurance);
    }

    @SchemaDocumentation("修改保险信息")
    @GRequestMapping(path = "/updateinsurance", method = RequestMethod.POST)
    Insurance updateInsurance(
            @RequestParam(name = "insurance", required = true) Insurance insurance) {
        return this.insuranceService.save(insurance);
    }

    @SchemaDocumentation("删除保险信息")
    @GRequestMapping(path = "/removeinsurance", method = RequestMethod.POST)
    Insurance removeInsurance(
            @RequestParam(name = "insurance", required = true) Insurance insurance) {
        this.insuranceRepository.deleteById(insurance.getId());
        return insurance;
    }

    @SchemaDocumentation("增加保险回访记录")
    @GRequestMapping(path = "/addinsurancecommunication", method = RequestMethod.POST)
    InsuranceCommunication addInsuranceCommunication(
            @RequestParam(name = "insurancecommunication", required = true) InsuranceCommunication insuranceCommunication) {
        return this.insuranceCommunicationRepository.save(insuranceCommunication);
    }

    @SchemaDocumentation("修改保险回访记录")
    @GRequestMapping(path = "/updateinsurancecommunication", method = RequestMethod.POST)
    InsuranceCommunication updateInsuranceCommunication(
            @RequestParam(name = "insurancecommunication", required = true) InsuranceCommunication insuranceCommunication) {
        return this.insuranceCommunicationRepository.save(insuranceCommunication);
    }

    @SchemaDocumentation("删除保险回访记录")
    @GRequestMapping(path = "/removeinsurancecommunication", method = RequestMethod.POST)
    InsuranceCommunication removeInsuranceCommunication(
            @RequestParam(name = "insurancecommunication", required = true) InsuranceCommunication insuranceCommunication) {
        this.insuranceCommunicationRepository.deleteById(insuranceCommunication.getId());
        return insuranceCommunication;
    }

    @SchemaDocumentation("添加车辆信息")
    @GRequestMapping(path = "/addcarinfo", method = RequestMethod.POST)
    CarInfo addCarInfo(@RequestParam(name = "carinfo", required = true) CarInfo carInfo) {
        return this.carInfoRepository.save(carInfo);
    }

    @SchemaDocumentation("修改车辆信息")
    @GRequestMapping(path = "/updatecarinfo", method = RequestMethod.POST)
    CarInfo updateCarInfo(@RequestParam(name = "carinfo", required = true) CarInfo carInfo) {
        return this.carInfoRepository.save(carInfo);
    }

    @SchemaDocumentation("删除车辆信息")
    @GRequestMapping(path = "/removecarinfo", method = RequestMethod.POST)
    CarInfo removeCarInfo(@RequestParam(name = "carinfo", required = true) CarInfo carInfo) {
        this.carInfoRepository.deleteById(carInfo.getId());
        return carInfo;
    }

    @SchemaDocumentation("添加订单")
    @GRequestMapping(path = "/addorder", method = RequestMethod.POST)
    Order addOrder(@RequestParam(name = "order", required = true) Order order) {
        return this.orderRepository.save(order);
    }

    @SchemaDocumentation("修改订单")
    @GRequestMapping(path = "/updateorder", method = RequestMethod.POST)
    Order updateOrder(@RequestParam(name = "order", required = true) Order order) {
        return this.orderRepository.save(order);
    }

    @SchemaDocumentation("关注车辆")
    @GRequestMapping(path = "/concerncar", method = RequestMethod.POST)
    ConcernCar concernCar(@RequestParam(name = "concerncar", required = true) ConcernCar concernCar) {
        return this.concernCarService.concern(concernCar);
    }

    @SchemaDocumentation("取消关注车辆")
    @GRequestMapping(path = "/cancelcar", method = RequestMethod.POST)
    ConcernCar cancelCar(@RequestParam(name = "concerncar", required = true) ConcernCar concernCar) {
        concernCarService.cancel(concernCar.getId());
        return concernCar;
    }

    @SchemaDocumentation("关注门店")
    @GRequestMapping(path = "/concernshop", method = RequestMethod.POST)
    ConcernShop concernShop(@RequestParam(name = "concernshop", required = true) ConcernShop concernShop) {
        return this.concernShopService.concern(concernShop);
    }

    @SchemaDocumentation("取消关注门店")
    @GRequestMapping(path = "/cancelshop", method = RequestMethod.POST)
    ConcernShop cancelShop(@RequestParam(name = "concernshop", required = true) ConcernShop concernShop) {
        concernShopService.cancel(concernShop.getId());
        return concernShop;
    }

    @SchemaDocumentation("添加反馈")
    @GRequestMapping(path = "/addfeedback", method = RequestMethod.POST)
    Feedback addFeedback(@RequestParam(name = "feedback", required = true) Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @SchemaDocumentation("删除反馈")
    @GRequestMapping(path = "/removefeedback", method = RequestMethod.POST)
    Feedback removeFeedback(@RequestParam(name = "feedback", required = true) Feedback feedback) {
        feedbackRepository.deleteById(feedback.getId());
        return feedback;
    }

    @SchemaDocumentation("/添加广告")
    @GRequestMapping(path = "/addadvertisement",method = RequestMethod.POST)
    Advertisement addAdvertisement(@RequestParam(name = "advertisement",required = true) Advertisement advertisement){
        return advertisementRepository.save(advertisement);
    }

    @SchemaDocumentation("/修改广告")
    @GRequestMapping(path = "/updateadvertisement",method = RequestMethod.POST)
    Advertisement updateAdvertisement(@RequestParam(name = "advertisement",required = true) Advertisement advertisement){
        return advertisementRepository.save(advertisement);
    }

    @SchemaDocumentation("/删除广告")
    @GRequestMapping(path = "/removeadvertisement",method = RequestMethod.POST)
    Advertisement removeAdvertisement(@RequestParam(name = "advertisement",required = true) Advertisement advertisement){
        advertisementRepository.deleteById(advertisement.getId());
        return advertisement;
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


}