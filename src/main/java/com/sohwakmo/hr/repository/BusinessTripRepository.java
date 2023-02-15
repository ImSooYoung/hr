package com.sohwakmo.hr.repository;

import com.sohwakmo.hr.domain.BusinessTrip;
import com.sohwakmo.hr.domain.PaymentState;
import com.sohwakmo.hr.domain.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BusinessTripRepository extends JpaRepository<BusinessTrip, Integer> {
    List<BusinessTrip> findByEmployeeNoOrCompanionNO(String employeeNo, String companionNo);
    List<BusinessTrip> findByEmployeeNoOrderByNoDesc(String employeeNo);
    List<BusinessTrip> findByEmployeeNoAndState(String no, PaymentState state);
    List<BusinessTrip> findByEmployeeNoAndStateOrState(String no, PaymentState state, PaymentState state2);
    List<BusinessTrip> findByApproverNoAndState(String no, PaymentState state);
    List<BusinessTrip> findByApproverNoAndStateOrState(String no, PaymentState state, PaymentState state2);

    @Query("select b from BUSINESSTRIP b where b.no = :no ")
    BusinessTrip selectByNo(@Param(value = "no") Integer no);

    @Query(value = "SELECT * from BUSINESSTRIP b, BUSINESSTRIP_STATE bs where b.no = bs.businesstrip_no and bs.state = '승인' and (b.employee_no = :employee_no or b.companionno = :companionno) ", nativeQuery = true)
    List<BusinessTrip> findByBusinessTripQuestion(@RequestParam("employee_no") String employee_no, @RequestParam("companionno") String companionno);
    List<BusinessTrip> findByEmployeeNo(String employeeNo);
    List<BusinessTrip> findByEmployeeNoAndEffectiveDateContaining(String emplyeeNo, String formatedNow);

    // JPQL(Java Persistence Query Language)
    @Query(
            "select b from BUSINESSTRIP b "
                    + " where lower(b.title) like lower('%' || :keyword || '%') "
                    + " or lower(b.reason) like lower('%' || :keyword || '%') "
                    + " and b.state = '진행중'"
                    + " order by b.no desc"
    )
    List<BusinessTrip> searchByKeyword(@Param(value = "keyword") String keyword, @Param(value = "state") PaymentState state);

    @Query(
            "select b from BUSINESSTRIP b "
                    + " where lower(b.title) like lower('%' || :keyword || '%') "
                    + " or lower(b.reason) like lower('%' || :keyword || '%') "
                    + " and b.state = '승인'"
                    + " or b.state = '반려'"
                    + " order by b.no desc"
    )
    List<BusinessTrip> searchByKeyword2(@Param(value = "keyword") String keyword, @Param(value = "state") PaymentState state, @Param(value = "state") PaymentState state2);

}

