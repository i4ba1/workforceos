package com.workforceos.attendance.config;

import com.workforceos.attendance.domain.AttendanceCalculator;
import com.workforceos.attendance.domain.AttendanceRule;
import com.workforceos.attendance.domain.EventPairingPolicy;
import com.workforceos.attendance.domain.OvertimePolicy;
import com.workforceos.attendance.domain.SimpleOvertimePolicy;
import com.workforceos.attendance.domain.StandardEventPairingPolicy;
import com.workforceos.attendance.domain.rule.AbsenceRule;
import com.workforceos.attendance.domain.rule.BreakViolationRule;
import com.workforceos.attendance.domain.rule.EarlyLeaveRule;
import com.workforceos.attendance.domain.rule.LateArrivalRule;
import com.workforceos.attendance.domain.rule.MissingClockInRule;
import com.workforceos.attendance.domain.rule.MissingClockOutRule;
import com.workforceos.attendance.domain.rule.OvertimeRule;
import com.workforceos.attendance.domain.rule.UnscheduledWorkRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Composes the calculation strategies and rule set into beans.
 *
 * <p>Rules are small strategy implementations; adding a rule is a new bean, never a new
 * branch in the calculator.</p>
 */
@Configuration
public class AttendanceConfiguration {

    @Bean
    EventPairingPolicy eventPairingPolicy() {
        return new StandardEventPairingPolicy();
    }

    @Bean
    OvertimePolicy overtimePolicy() {
        return new SimpleOvertimePolicy();
    }

    @Bean
    LateArrivalRule lateArrivalRule() {
        return new LateArrivalRule();
    }

    @Bean
    EarlyLeaveRule earlyLeaveRule() {
        return new EarlyLeaveRule();
    }

    @Bean
    MissingClockInRule missingClockInRule() {
        return new MissingClockInRule();
    }

    @Bean
    MissingClockOutRule missingClockOutRule() {
        return new MissingClockOutRule();
    }

    @Bean
    AbsenceRule absenceRule() {
        return new AbsenceRule();
    }

    @Bean
    OvertimeRule overtimeRule() {
        return new OvertimeRule();
    }

    @Bean
    UnscheduledWorkRule unscheduledWorkRule() {
        return new UnscheduledWorkRule();
    }

    @Bean
    BreakViolationRule breakViolationRule() {
        return new BreakViolationRule();
    }

    @Bean
    AttendanceCalculator attendanceCalculator(EventPairingPolicy pairingPolicy, OvertimePolicy overtimePolicy,
                                              List<AttendanceRule> rules) {
        return new AttendanceCalculator(pairingPolicy, overtimePolicy, rules);
    }
}
