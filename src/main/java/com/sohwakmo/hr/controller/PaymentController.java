package com.sohwakmo.hr.controller;

import com.sohwakmo.hr.domain.*;
import com.sohwakmo.hr.dto.BusinessCardCreateDto;
import com.sohwakmo.hr.dto.BusinessTripCreateDto;
import com.sohwakmo.hr.dto.LeaveCreateDto;
import com.sohwakmo.hr.dto.VacationCreateDto;
import com.sohwakmo.hr.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final VacationService vacationService;
    private final BusinessTripService businessTripService;
    private final BusinessCardService businessCardService;
    private final LeaveService leaveService;
    private final EmployeeService employeeService;

    // 기안 문서 list
    @GetMapping("/list")
    public void list(Model model, @RequestParam(defaultValue = "vacation")String payment, Principal principal) {

        String employeeNo = principal.getName();

        switch (payment) {
            case "vacation" -> {
                model.addAttribute("list", vacationService.getVacationListSeven(employeeNo));
                model.addAttribute("vacation", "vacation");
            }
            case "trip" -> {
                model.addAttribute("list", businessTripService.getBusinessTripSeven(employeeNo));
                model.addAttribute("trip", "trip");
            }
            case "leave" -> {
                model.addAttribute("list", leaveService.selectByEmployeeNO(employeeNo));
                model.addAttribute("leave", "leave");
            }
            default -> {
                model.addAttribute("list",  businessCardService.getBusinessCardSeven(employeeNo));
                model.addAttribute("card", "card");
            }
        }

    }

    // 기안 문서 create
    @GetMapping("/create")
    public void create() {
    }

    // 결재 대기 list
    @GetMapping("/process")
    public void process(Model model, @RequestParam(defaultValue = "vacation")String payment, Principal principal) {

        String employeeNo = principal.getName();
        PaymentState state = PaymentState.진행중;

        if(payment.equals("vacation")){
            List<Vacation> list = vacationService.selectByEmployeeNoAndState(employeeNo, state);
            model.addAttribute("list", list);
            model.addAttribute("vacation", "vacation");
        } else if(payment.equals("trip")) {
            List<BusinessTrip> list = businessTripService.selectByEmployeeNoAndState(employeeNo, state);
            model.addAttribute("list", list);
            model.addAttribute("trip", "trip");
        } else if(payment.equals("leave")) {
            List<Leave> list = leaveService.selectByEmployeeNoAndState(employeeNo, state);
            model.addAttribute("list", list);
            model.addAttribute("leave", "leave");
        } else {
            List<BusinessCard> list = businessCardService.selectByEmployeeNoAndState(employeeNo, state);
            model.addAttribute("list", list);
            model.addAttribute("card", "card");
        }

    }

    // 결재 완료 list
    @GetMapping("/complete")
    public void complete(Model model, @RequestParam(defaultValue = "vacation")String payment, Principal principal) {

        String employeeNo = principal.getName();
        PaymentState state = PaymentState.승인;
        PaymentState state2 = PaymentState.반려;

        if(payment.equals("vacation")){
            List<Vacation> list = vacationService.selectByEmployeeNoAndStateOrState(employeeNo, state, state2);
            log.info(list.toString());
            model.addAttribute("list", list);
            model.addAttribute("vacation", "vacation");
        } else if(payment.equals("trip")) {
            List<BusinessTrip> list = businessTripService.selectByEmployeeNoAndStateOrState(employeeNo, state, state2);
            log.info(list.toString());
            model.addAttribute("list", list);
            model.addAttribute("trip", "trip");
        } else if(payment.equals("leave")) {
            List<Leave> list = leaveService.selectByEmployeeNoAndStateOrState(employeeNo, state, state2);
            model.addAttribute("list", list);
            model.addAttribute("leave", "leave");
        } else {
            List<BusinessCard> list = businessCardService.selectByEmployeeNoAndStateOrState(employeeNo, state, state2);
            model.addAttribute("list", list);
            model.addAttribute("card", "card");
        }

    }

    // -------------- vacation ----------------
    @GetMapping("/approver")
    public void approver() {
    }

    // 휴가(vacation) list
    @GetMapping("/vacation/create")
    public void createGetVacation(){
    }

    // 휴가(vacation) create
    @PostMapping("/vacation/create")
    public void createPostVacation(VacationCreateDto dto){

        Vacation vacation = Vacation.builder()
                .employeeNo(dto.getEmployeeNo()).title(dto.getTitle()).reason(dto.getReason()).approverNo(dto.getApproverNo())
                .category(dto.getCategory()).effectiveDate(dto.getEffectiveDate()).expirationDate(dto.getExpirationDate())
                .build();

        Vacation vacations = vacationService.create(vacation);
        log.info("vacation={}", vacations);

    }

    // 휴가(vacation) detail
    @GetMapping("/vacation/detail")
    public void detailVacation(Model model, @RequestParam Integer no) {

        Vacation vacation = vacationService.selectByNo(no);
        model.addAttribute("vacation", vacation);
        log.info("vacation={}", vacation);

        Employee employee = employeeService.selectByNo(vacation.getEmployeeNo());
        model.addAttribute("employee", employee);
        log.info("employee={}", employee);

        Employee approver = employeeService.selectByNo(vacation.getApproverNo());
        model.addAttribute("approver", approver);

    }

    // ------------ trip --------------
    // 출장(trip) get create
    @GetMapping("/businessTrip/create")
    public void createGetTrip(){
    }

    // 출장(trip) post create
    @PostMapping("/businessTrip/create")
    public void createPostTrip(BusinessTripCreateDto dto){

        BusinessTrip businessTrip = BusinessTrip.builder()
                .employeeNo(dto.getEmployeeNo()).title(dto.getTitle()).reason(dto.getReason()).approverNo(dto.getApproverNo())
                .category(dto.getCategory()).effectiveDate(dto.getEffectiveDate()).expirationDate(dto.getExpirationDate())
                .place(dto.getPlace()).companionNO(dto.getCompanionNo()).build();


        BusinessTrip businessTrips = businessTripService.create(businessTrip);

    }

    // 출장(trip) detail
    @GetMapping("/businessTrip/detail")
    public void detailTrip(Model model, @RequestParam Integer no) {

        BusinessTrip trip = businessTripService.selectByNo(no);
        model.addAttribute("trip", trip);
        log.info("trip??????????={}", trip);

        Employee employee = employeeService.selectByNo(trip.getEmployeeNo());
        model.addAttribute("employee", employee);
        log.info("employee={}", employee);

        Employee approver = employeeService.selectByNo(trip.getApproverNo());
        model.addAttribute("approver", approver);

        if(employeeService.selectByNo(trip.getCompanionNO()) != null){
            Employee companion = employeeService.selectByNo(trip.getCompanionNO());
            model.addAttribute("companion", companion);
        } else {
            Employee companion = null;
            model.addAttribute("companion", companion);
        }


    }

    // ----------- card -------------
    // 명함(bs card) create list
    @GetMapping("/businessCard/create")
    public  void createGetCard(Model model){

        String category = "명함";
        List<BusinessCard> list = businessCardService.selectByCategory(category);
        model.addAttribute("card", list);

    }

    // 명함(bs card) create
    @PostMapping ("/businessCard/create")
    public void createPostCard(BusinessCardCreateDto dto){

        BusinessCard businessCard = BusinessCard.builder()
                .employeeNo(dto.getEmployeeNo()).title(dto.getTitle()).reason(dto.getReason()).approverNo(dto.getApproverNo())
                .category(dto.getCategory()).build();

        BusinessCard businessCards = businessCardService.create(businessCard);

    }

    // 명함(bs card) detail
    @GetMapping("/businessCard/detail")
    public void detailCard(Model model, @RequestParam Integer no) {

        BusinessCard card = businessCardService.selectByNo(no);
        model.addAttribute("card", card);
        log.info("card={}", card);

        Employee employee = employeeService.selectByNo(card.getEmployeeNo());
        model.addAttribute("employee", employee);
        log.info("employee={}", employee);

        Employee approver = employeeService.selectByNo(card.getApproverNo());
        model.addAttribute("approver", approver);

    }

    // ----------- leave -------------
    // 퇴사(leave) create
    @GetMapping("/leave/create")
    public void createGetLeave(){
    }

    // 퇴사(leave) create
    @PostMapping("/leave/create")
    public void createPostLeave(LeaveCreateDto dto){

        Leave leave = Leave.builder()
                .employeeNo(dto.getEmployeeNo()).approverNo(dto.getApproverNo()).secondApproverNo(dto.getSecondApproverNo())
                .title(dto.getTitle()).reason(dto.getReason()).category(dto.getCategory())
                .effectiveDate(dto.getEffectiveDate())
                .build();
        Leave leaves = leaveService.create(leave);
    }

    // 퇴사(leave) dtail
    @GetMapping("/leave/detail")
    public void detailLeave(@RequestParam Integer no, Model model) {

            Leave leave = leaveService.selectByNo(no);
            model.addAttribute("leave", leave);
            log.info("leave={}", leave);

            Employee employee = employeeService.selectByNo(leave.getEmployeeNo());
            model.addAttribute("employee", employee);
            log.info("employee={}", employee);

            Employee approver = employeeService.selectByNo(leave.getApproverNo());
            model.addAttribute("approver", approver);

            Employee secondApprover = employeeService.selectByNo(leave.getSecondApproverNo());
            model.addAttribute("secondApprover", secondApprover);
    }

    @GetMapping("/request")
    public void request(Model model, @RequestParam(defaultValue = "vacation")String payment, @RequestParam(defaultValue = "") String keyword ,Principal principal){

        log.info("서치1={}", payment);
        log.info("서치={}", keyword);
        String employeeNo = principal.getName();
        PaymentState state = PaymentState.진행중;

        if(payment.equals("vacation")) {

            if (keyword != null) {
                List<Vacation> list = vacationService.search(keyword, state);
                model.addAttribute("list", list);
                model.addAttribute("vacation", "vacation");
            } else  {
                List<Vacation> list = vacationService.selectByApproverNoAndState(employeeNo, state);
                model.addAttribute("list", list);
                model.addAttribute("vacation", "vacation");
            }

        } else if(payment.equals("trip")) {

            if (keyword != null){
                List<BusinessTrip> list = businessTripService.search(keyword, state);
                model.addAttribute("list", list);
                model.addAttribute("trip", "trip");
            } else {
                List<BusinessTrip> list = businessTripService.selectByApproverNoAndState(employeeNo, state);
                model.addAttribute("list", list);
                model.addAttribute("trip", "trip");
            }


        } else if(payment.equals("leave")) {

            if (keyword != null){
                List<Leave> list = leaveService.search(keyword, state);
                model.addAttribute("list", list);
                model.addAttribute("leave", "leave");
            } else {
                List<Leave> list = leaveService.selectByApproverNoOrSecondApproverNoAndState(employeeNo, employeeNo, state);
                model.addAttribute("list", list);
                model.addAttribute("leave", "leave");
            }

        } else if(payment.equals("card")){
            log.info("test");
            if (keyword != null) {
                List<BusinessCard> list = businessCardService.search(keyword, state);
                model.addAttribute("list", list);
                model.addAttribute("card", "card");
            } else {
                List<BusinessCard> list = businessCardService.selectByApproverNoAndState(employeeNo, state);
                log.info("리스트={}",list);
                model.addAttribute("list", list);
                model.addAttribute("card", "card");
            }

        }
    }

    @GetMapping("/response")
    public void response(Model model, @RequestParam(defaultValue = "vacation")String payment, @RequestParam(defaultValue = "") String keyword, Principal principal){

        String employeeNo = principal.getName();
        PaymentState state = PaymentState.승인;
        PaymentState state2 = PaymentState.반려;

        if(payment.equals("vacation")){
            if (keyword != null) {
                List<Vacation> list = vacationService.search2(keyword, state, state2);
                log.info(list.toString());
                model.addAttribute("list", list);
                model.addAttribute("vacation", "vacation");
            } else {
                List<Vacation> list = vacationService.selectByApproverNoAndStateOrState(employeeNo, state, state2);
                log.info(list.toString());
                model.addAttribute("list", list);
                model.addAttribute("vacation", "vacation");
            }

        } else if(payment.equals("trip")) {
            if (keyword != null) {
                List<BusinessTrip> list = businessTripService.search2(keyword, state, state2);
                model.addAttribute("list", list);
                model.addAttribute("trip", "trip");
            } else {
                List<BusinessTrip> list = businessTripService.selectByApproverNoAndStateOrState(employeeNo, state, state2);
                model.addAttribute("list", list);
                model.addAttribute("trip", "trip");
            }

        } else if(payment.equals("leave")) {
            if (keyword != null) {
                List<Leave> list = leaveService.search2(keyword, state, state2);
                model.addAttribute("list", list);
                model.addAttribute("leave", "leave");
            } else {
                List<Leave> list = leaveService.selectByApproverNoOrSecondApproverNoAndStateOrState(employeeNo, employeeNo, state, state2);
                model.addAttribute("list", list);
                model.addAttribute("leave", "leave");
            }

        } else {
            if (keyword != null) {
                List<BusinessCard> list = businessCardService.search2(keyword, state, state2);
                model.addAttribute("list", list);
                model.addAttribute("card", "card");
            } else {
                List<BusinessCard> list = businessCardService.selectByApproverNoAndStateOrState(employeeNo, state, state2);
                model.addAttribute("list", list);
                model.addAttribute("card", "card");
            }

        }
    }


}
