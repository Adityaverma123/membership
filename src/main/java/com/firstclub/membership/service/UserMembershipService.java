package com.firstclub.membership.service;

import com.firstclub.membership.domain.UserMembershipEntity;
import com.firstclub.membership.exception.ApiException;
import com.firstclub.membership.generated.model.UserMembership;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.service.mapper.MembershipMapper;
import com.firstclub.membership.util.MembershipCalculationUtil;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserMembershipService {
	private final UserMembershipRepository userMembershipRepository;
	private final MembershipService membershipService;
	private final MembershipMapper mapper;

	public UserMembershipService(
		UserMembershipRepository userMembershipRepository,
		MembershipService membershipService,
		MembershipMapper mapper
	) {
		this.userMembershipRepository = userMembershipRepository;
		this.membershipService = membershipService;
		this.mapper = mapper;
	}

	@Transactional
	public UserMembership subscribe(String userId, Long membershipPlanId) {
		var plan = membershipService.getPlanById(membershipPlanId);
		var tier = membershipService.determineTier(userId);

		if (!userMembershipRepository.findCurrentForUpdate(userId, Instant.now()).isEmpty()) {
			throw new ApiException(HttpStatus.CONFLICT, "User already has an active membership");
		}

		Instant startAt = Instant.now();
		UserMembershipEntity membership = new UserMembershipEntity();
		membership.setUserId(userId);
		membership.setMembershipPlan(plan);
		membership.setMembershipTier(tier);
		membership.setStartAt(startAt);
		membership.setEndAt(MembershipCalculationUtil.calculateEndAt(startAt, plan.getDuration()));
		return mapper.toDto(userMembershipRepository.save(membership));
	}

	public UserMembership getCurrentMembership(String userId) {
		return userMembershipRepository.findFirstByUserIdAndCancelledAtIsNullAndEndAtAfterOrderByCreatedAtDesc(
				userId,
				Instant.now()
			)
			.map(mapper::toDto)
			.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Active membership not found"));
	}

	@Transactional
	public UserMembership cancel(String userId) {
		UserMembershipEntity membership = currentMembershipForUpdate(userId);
		membership.setCancelledAt(Instant.now());
		return mapper.toDto(membership);
	}

	@Transactional
	public void updateMembershipTierIfExists(String userId) {
		userMembershipRepository.findCurrentForUpdate(userId, Instant.now()).stream().findFirst().ifPresent(membership -> {
			var newTier = membershipService.determineTier(userId);
			membership.setMembershipTier(newTier);
			userMembershipRepository.save(membership);
		});
	}

	private UserMembershipEntity currentMembershipForUpdate(String userId) {
		return userMembershipRepository.findCurrentForUpdate(userId, Instant.now())
			.stream()
			.findFirst()
			.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Active membership not found"));
	}
}
