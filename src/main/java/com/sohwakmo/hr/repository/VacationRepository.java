package com.sohwakmo.hr.repository;

import com.sohwakmo.hr.domain.BusinessCard;
import com.sohwakmo.hr.domain.PaymentState;
import com.sohwakmo.hr.domain.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VacationRepository extends JpaRepository<Vacation, Integer> {

    @Query(value = "SELECT * from VACATION v, VACATION_STATE vs where v.no = vs.vacation_no and vs.state = '승인' and v.employee_no = :employee_no", nativeQuery = true)
    List<Vacation> findByVacationQuestion(@RequestParam("employee_no") String employee_no);
    List<Vacation> findByEmployeeNoOrderByNoDesc(String loginUser);

    // 진행중 and EmployeeNo List
    List<Vacation> findByEmployeeNoAndStateOrState(String no, PaymentState state, PaymentState state2);

    List<Vacation> findByEmployeeNoAndState(String no, PaymentState state);

    List<Vacation> findByEmployeeNoAndEffectiveDateContaining(String employeeNo, String formatedNow);

    List<Vacation> findByEmployeeNo(String no);

    List<Vacation> findByApproverNoAndStateOrState(String no, PaymentState state, PaymentState state2);

    List<Vacation> findByApproverNoAndState(String no, PaymentState state);

    @Query("select v from VACATION v where v.no = :no ")
    Vacation selectByNo(@Param(value = "no") Integer no);

    // JPQL(Java Persistence Query Language)
    @Query(value =
            "select * from VACATION v, VACATION_STATE vs "
                    + " where v.no = vs.vacation_no"
                    + " and vs.state = '진행중'"
                    + " and (lower(v.title) like lower('%' || :keyword || '%') or lower(v.reason) like lower('%' || :keyword || '%'))"
                    + " order by v.no desc",
            nativeQuery = true
    )
    List<Vacation> searchByKeyword(@Param(value = "keyword") String keyword);

    // JPQL(Java Persistence Query Language)
    @Query(value =
            "select * from VACATION v, VACATION_STATE vs "
                    + " where v.no = vs.vacation_no"
                    + " and (vs.state = '승인' or vs.state = '반려')"
                    + " and (lower(v.title) like lower('%' || :keyword || '%') or lower(v.reason) like lower('%' || :keyword || '%'))"
                    + " order by v.no desc",
            nativeQuery = true
    )
    List<Vacation> searchByKeyword2(@Param(value = "keyword") String keyword);

}
