package com.sohwakmo.hr.repository;

import com.sohwakmo.hr.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Integer> {

    @Query("select l from LEAVE l where l.employeeNo = :no order by l.no desc ")
    List<Leave> selectByEmployeeNO(@Param(value = "no")String no);

    @Query("select l from LEAVE l where l.no = :no ")
    Leave selectByNo(@Param(value = "no")Integer no);

    @Modifying
    @Query("update LEAVE l SET l.state = :state where l.no = :no")
    Leave updateState(@Param(value = "no")Integer no, @Param(value = "state")PaymentState state);

    List<Leave> findByEmployeeNoAndStateOrState(String no, PaymentState state, PaymentState state2);
    List<Leave> findByEmployeeNoAndState(String no, PaymentState state);
    List<Leave> findByApproverNoOrSecondApproverNoAndStateOrState(String no, String no2, PaymentState state, PaymentState state2);
    List<Leave> findByApproverNoOrSecondApproverNoAndState(String no, String no2, PaymentState state);

    // JPQL(Java Persistence Query Language)
    @Query(
            "select l from LEAVE l "
                    + " where lower(l.title) like lower('%' || :keyword || '%') "
                    + " or lower(l.reason) like lower('%' || :keyword || '%') "
                    + " and l.state = '진행중' "
                    + " order by l.no desc"
    )
    List<Leave> searchByKeyword(@Param(value = "keyword") String keyword, @Param(value = "state") PaymentState state);

    // JPQL(Java Persistence Query Language)
    @Query(
            "select l from LEAVE l "
                    + " where lower(l.title) like lower('%' || :keyword || '%') "
                    + " or lower(l.reason) like lower('%' || :keyword || '%') "
                    + " and l.state = '승인'"
                    + " or l.state = '반려'"
                    + " order by l.no desc"
    )
    List<Leave> searchByKeyword2(@Param(value = "keyword") String keyword, @Param(value = "state") PaymentState state, @Param(value = "state") PaymentState state2);
}
