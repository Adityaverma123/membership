package com.firstclub.membership.controller;

import com.firstclub.membership.generated.api.TiersApi;
import com.firstclub.membership.generated.model.MembershipTier;
import com.firstclub.membership.service.MembershipService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TierController implements TiersApi {
    private final MembershipService membershipService;

    public TierController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @Override
    public ResponseEntity<List<MembershipTier>> getMembershipTiers() {
        return ResponseEntity.ok(membershipService.getTiers());
    }
}
