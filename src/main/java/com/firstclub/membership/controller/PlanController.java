package com.firstclub.membership.controller;

import com.firstclub.membership.generated.api.PlansApi;
import com.firstclub.membership.generated.model.MembershipPlan;
import com.firstclub.membership.service.MembershipService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlanController implements PlansApi {
    private final MembershipService membershipService;

    public PlanController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @Override
    public ResponseEntity<List<MembershipPlan>> getMembershipPlans() {
        return ResponseEntity.ok(membershipService.getPlans());
    }
}
