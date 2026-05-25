package com.firstclub.membership.controller;

import com.firstclub.membership.generated.api.UserMembershipsApi;
import com.firstclub.membership.generated.model.MembershipTier;
import com.firstclub.membership.generated.model.SubscribeRequest;
import com.firstclub.membership.generated.model.UserMembership;
import com.firstclub.membership.service.UserMembershipSubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserMembershipController implements UserMembershipsApi {
    private final UserMembershipSubscriptionService membershipService;

    public UserMembershipController(UserMembershipSubscriptionService membershipService) {
        this.membershipService = membershipService;
    }

    @Override
    public ResponseEntity<UserMembership> subscribeUser(String xUserId, SubscribeRequest subscribeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(membershipService.subscribe(xUserId, subscribeRequest.getMembershipPlanId()));
    }

    @Override
    public ResponseEntity<UserMembership> getUserMembership(String xUserId) {
        return ResponseEntity.ok(membershipService.getCurrentMembership(xUserId));
    }

    @Override
    public ResponseEntity<UserMembership> cancelUserMembership(String xUserId) {
        return ResponseEntity.ok(membershipService.cancel(xUserId));
    }

    @Override
    public ResponseEntity<MembershipTier> evaluateUserTier(String xUserId) {
        return ResponseEntity.ok(membershipService.evaluateTier(xUserId));
    }
}
