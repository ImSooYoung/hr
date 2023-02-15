package com.sohwakmo.hr.service;

import com.sohwakmo.hr.domain.BusinessCard;
import com.sohwakmo.hr.domain.Leave;
import com.sohwakmo.hr.domain.PaymentState;
import com.sohwakmo.hr.domain.Vacation;
import com.sohwakmo.hr.repository.BusinessCardRepository;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BusinessCardService {

    private final BusinessCardRepository businessCardRepository;

    // 명함(Bs card) create
    public BusinessCard create(BusinessCard businessCard){
        businessCard.addRole(PaymentState.진행중);

        return businessCardRepository.save(businessCard);
    }

    // TODO: 명함, 사용자 no 가져와서 리스트 출력하기 -> list 출력
    public List<BusinessCard> selectByCategory(String card) {

        return businessCardRepository.selectByCard(card);
    }

    // 명함(Bs card) detail
    public BusinessCard selectByNo(Integer cardNo){
        return businessCardRepository.findById(cardNo).orElse(null);
    }

    public List<BusinessCard> selectByEmployeeNo(String employeeNo){
        return businessCardRepository.selectByEmployeeNo(employeeNo);
    }

    public List<BusinessCard> selectByEmployeeNoAndState(String no, PaymentState state){
        return businessCardRepository.findByEmployeeNoAndState(no, state);
    }

    public List<BusinessCard> selectByEmployeeNoAndStateOrState(String no, PaymentState state, PaymentState state2){
        return businessCardRepository.findByEmployeeNoAndStateOrState(no, state, state2);
    }

    public List<BusinessCard> selectByApproverNoAndState(String no, PaymentState state){
        return businessCardRepository.findByApproverNoAndState(no, state);
    }

    public List<BusinessCard> selectByApproverNoAndStateOrState(String no){
        return businessCardRepository.findByApproverNoAndStateOrState(no);
    }

    @Transactional
    public Integer update(Integer no){

        BusinessCard entity = businessCardRepository.selectByNo(no);
        entity.setState(Collections.singleton(PaymentState.승인));
        entity.add(LocalDateTime.now());

        return no;
    }

    @Transactional
    public Integer delete(Integer no){
        businessCardRepository.deleteById(no);
        return no;
    }

    @Transactional
    public Integer updateReturn(Integer no, String returnReason){

        BusinessCard entity = businessCardRepository.selectByNo(no);
        entity.setState(Collections.singleton(PaymentState.반려));
        entity.add(LocalDateTime.now()); // 반려 시간
        entity.returnReason(returnReason); // 반려 사유

        return no;
    }
    public List<BusinessCard> getBusinessCardSeven(String employeeNo) {
        List<BusinessCard> allList = businessCardRepository.findByEmployeeNoOrderByNoDesc(employeeNo);
        if (allList.size() <=7) {
            return allList;
        }else{
            List<BusinessCard> list = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                list.add(allList.get(i));
            }
            return list;
        }
    }

    public List<BusinessCard> search(String keyword){

        List<BusinessCard> list = new ArrayList<>();
        list = businessCardRepository.searchByKeyword(keyword);

        return list;
    }

    public List<BusinessCard> search2(String keyword){

        List<BusinessCard> list = new ArrayList<>();
        list = businessCardRepository.searchByKeyword2(keyword);

        return list;
    }

}
