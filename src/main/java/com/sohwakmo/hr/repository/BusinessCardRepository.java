package com.sohwakmo.hr.repository;

import com.sohwakmo.hr.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BusinessCardRepository extends JpaRepository<BusinessCard, Integer> {

    @Query("select b from BUSINESSCARD b where b.category = :card ")
    List<BusinessCard> selectByCard(@Param(value = "card") String card);

    @Query("select b from BUSINESSCARD b where b.employeeNo = :no order by b.no desc ")
    List<BusinessCard> selectByEmployeeNo(@Param(value = "no") String no);

    @Query("select b from BUSINESSCARD b where b.no = :no ")
    BusinessCard selectByNo(@Param(value = "no")Integer no);

    List<BusinessCard> findByEmployeeNoAndState(String no, PaymentState state);

    List<BusinessCard> findByEmployeeNoAndStateOrState(String no, PaymentState state, PaymentState state2);

    List<BusinessCard> findByApproverNoAndState(String no, PaymentState state);

    List<BusinessCard> findByApproverNoAndStateOrState(String no, PaymentState state, PaymentState state2);

    List<BusinessCard> findByEmployeeNoOrderByNoDesc(String employeeNo);

    // JPQL(Java Persistence Query Language)
    @Query(
            "select b from BUSINESSCARD b "
                    + " where lower(b.title) like lower('%' || :keyword || '%') "
                    + " or lower(b.reason) like lower('%' || :keyword || '%') "
                    + " and b.state = '승인'"
                    + " or b.state = '반려'"
                    + " order by b.no desc"
    )
    List<BusinessCard> searchByKeyword2(@Param(value = "keyword") String keyword, @Param(value = "state") PaymentState state,  @Param(value = "state") PaymentState state2);

    // JPQL(Java Persistence Query Language)
    @Query(
            "select b from BUSINESSCARD b "
                    + " where lower(b.title) like lower('%' || :keyword || '%') "
                    + " or lower(b.reason) like lower('%' || :keyword || '%') "
                    + " and b.state = '진행중' "
                    + " order by b.no desc"
    )
    List<BusinessCard> searchByKeyword(@Param(value = "keyword") String keyword, @Param(value = "state") PaymentState state);
}
